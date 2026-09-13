# Dead Letter Queue ve Ack Stratejisi

> **Okuma:** ~4 dk · **İlgili modül:** `inventory-notification-service`

| | |
| --- | --- |
| **Ne işe yarar** | İşlenemeyen mesajın kaybolmasını ve sonsuz döngüyü önler |
| **Anahtar** | `basicAck`, `basicNack`, `x-dead-letter-exchange`, `basicQos` |
| **.NET karşılığı** | Azure Service Bus dead-letter queue, MassTransit `_error` kuyruğu |

## Soru

Bir mesaj işlenirken hata alırsanız ne olmalı? Üç kötü cevap ve bir iyi cevap vardır.

| Yaklaşım | Sonuç |
| --- | --- |
| Mesajı onayla ve geç | Veri sessizce kaybolur |
| Hiçbir şey yapma | Mesaj onaysız kalır, kuyruk tıkanır |
| Kuyruğa geri koy | **Sonsuz döngü** — tekrar patlar, tekrar girer |
| Ayrı bir kuyruğa yönlendir | Kayıp yok, döngü yok, sonradan incelenebilir |

## Manuel Onaylama

```java
channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);
//                               ^^^^^ autoAck = false
```

`autoAck = true` olsaydı broker, mesajı tüketiciye teslim ettiği anda kuyruktan silerdi. Tüketici işleme sırasında çökerse mesaj yok olurdu.

`false` ile mesaj, siz onaylayana kadar kuyrukta "onaysız teslim edilmiş" durumda bekler. Bağlantı kopar ya da tüketici çökerse broker mesajı yeniden teslim eder.

## İşleme Akışı

```java
private void handleDelivery(Delivery delivery) {
    long deliveryTag = delivery.getEnvelope().getDeliveryTag();
    String json = new String(delivery.getBody(), StandardCharsets.UTF_8);

    try {
        Figure figure = jsonb.fromJson(json, Figure.class);
        notificationSender.notifyStockArrival(figure);

        channel.basicAck(deliveryTag, false);            // başarılı → kuyruktan sil
    } catch (JsonbException | IOException e) {
        logger.error("Mesaj işlenirken hata oluştu, DLQ'ya yönlendiriliyor: ", e);
        channel.basicNack(deliveryTag, false, false);    // başarısız → reddet
    }
}
```

### Bu depodaki en önemli tek satır

```java
channel.basicNack(deliveryTag, false, false);
//                              │      └── requeue = false
//                              └───────── multiple = false
```

`requeue = true` verseydiniz mesaj aynı kuyruğa geri konurdu. Aynı hata tekrar oluşurdu. Mesaj tekrar geri konurdu. Sonsuz döngü — ve bu döngü CPU'yu, logları ve broker'ı meşgul ederken sisteminizde başka hiçbir şey çalışmaz.

`requeue = false` ile mesaj kuyruktan çıkar. Kuyruğun `x-dead-letter-exchange` argümanı tanımlı olduğu için de yok olmaz; DLX'e yönlendirilir.

## DLQ Topolojisi

```java
channel.exchangeDeclare(DLX_NAME, "fanout", true);
channel.queueDeclare(DLQ_NAME, true, false, false, null);
channel.queueBind(DLQ_NAME, DLX_NAME, "");

Map<String, Object> queueArgs = new HashMap<>();
queueArgs.put("x-dead-letter-exchange", DLX_NAME);
channel.queueDeclare(QUEUE_NAME, true, false, false, queueArgs);
```

```text
notification.figure.stock.arrived.q ──nack──▶ figure.exchange.dlx ──▶ ...dlq
```

DLX'in `fanout` olması bilinçlidir: ölü mesajların routing key'i ile uğraşmak istemezsiniz, hepsi tek yere gitsin yeter.

Bir mesaj üç durumda DLQ'ya düşer: `requeue = false` ile reddedilirse, TTL süresi dolarsa, ya da kuyruk uzunluk sınırına ulaşırsa.

## QoS: Kaç Mesaj Aynı Anda?

```java
channel.basicQos(10);
```

Broker'ın aynı anda kaç onaylanmamış mesaj göndereceğini sınırlar. Bu ayar olmasaydı broker, kuyrukta birikmiş tüm mesajları tek seferde pompalar; bellek baskısı oluşur ve birden fazla tüketici varsa yük dengesiz dağılır.

## DLQ Dolduğunda Ne Yapılır?

DLQ bir çöp kutusu değil, bir **inceleme kuyruğudur**. Pratikte:

1. Mesajları inceleyin — veri mi bozuk, kod mu hatalı?
2. Kod hatasıysa düzeltin ve mesajları ana kuyruğa geri taşıyın *(shovel eklentisi ya da elle publish)*.
3. Veri kalıcı olarak bozuksa kayıt altına alıp silin.

DLQ'nun izlenmediği bir sistemde DLQ, yavaş çalışan bir veri kaybı mekanizmasıdır.

## Tuzaklar

- **`autoAck = true` ile başlamak.** Kolaydır, ilk çökmede veri kaybettirir.
- **`requeue = true`.** Sonsuz döngü. En pahalı hata.
- **Yeniden deneme sayacı tutmamak.** Geçici hatalar *(ağ kesintisi)* için sınırlı yeniden deneme mantıklıdır; bunun için mesaj başlıklarında bir sayaç tutulur.
- **`basicAck`'i `finally` bloğuna koymak.** Hata durumunda da onaylanır; mesaj kaybolur.
- **DLQ'yu izlememek.** Kuyruk uzunluğu için alarm kurulmalıdır.
- **`multiple = true` kullanmak.** O tag'e kadarki **tüm** mesajları onaylar; ne yaptığınızı bilmiyorsanız veri kaybettirir.

## İlgili Sayfalar

- [RabbitMQ Exchange, Queue ve Binding](RabbitMQ-Exchange-Queue-ve-Binding)
- [SLF4J ve Payara Loglama](SLF4J-ve-Payara-Loglama)
