# RabbitMQ Exchange, Queue ve Binding

> **Okuma:** ~5 dk · **İlgili modül:** `inventory-events-service`, `inventory-notification-service`

| | |
| --- | --- |
| **Ne işe yarar** | Servisler arasında kalıcı, asenkron haberleşme kurar |
| **Anahtar** | Exchange, queue, routing key, binding |
| **.NET karşılığı** | MassTransit / RabbitMQ.Client üzerinden aynı kavramlar |

## Temel Model

Üretici *(producer)* mesajı **kuyruğa değil, exchange'e** gönderir. Exchange, routing key'e bakarak mesajı hangi kuyruklara kopyalayacağına karar verir.

```text
Publisher ──▶ figure.exchange ──[figure.stock.arrived]──▶ notification...q ──▶ Consumer
                  (topic)                                    (durable)
```

Bu dolaylılık önemlidir: üretici, kimin dinlediğini bilmez. Yarın üç dinleyici daha eklendiğinde üretici kodunda tek satır değişmez.

## Exchange Türleri

| Tür | Yönlendirme kuralı | Kullanım |
| --- | --- | --- |
| `direct` | Routing key birebir eşleşir | Hedefi belli mesajlar |
| `topic` | Desen eşleşmesi: `figure.#`, `*.stock.*` | Bu depoda kullanılan tür |
| `fanout` | Key'e bakmaz, tüm bağlı kuyruklara kopyalar | Yayın; DLX burada kullanılıyor |
| `headers` | Başlık niteliklerine bakar | Nadiren |

`topic` seçimi geleceğe alan bırakır: bugün yalnızca `figure.stock.arrived` var, yarın `figure.stock.depleted` eklendiğinde `figure.#` deseniyle dinleyen bir kuyruk ikisini de alır.

## Üretici Tarafı

```java
@ApplicationScoped
public class Publisher {

    private static final String EXCHANGE_NAME = "figure.exchange";
    private static final String ROUTING_KEY = "figure.stock.arrived";

    @PostConstruct
    public void init() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv().getOrDefault("RABBITMQ_HOST", "localhost"));
        factory.setPort(5672);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(5000);

        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);   // durable
    }

    public synchronized void publishStockArrival(Object event) {
        String jsonMessage = jsonb.toJson(event);
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null,
                jsonMessage.getBytes(StandardCharsets.UTF_8));
    }

    @PreDestroy
    public void cleanup() { /* channel ve connection kapatılır */ }
}
```

Üç ayrıntı dikkat ister.

**`synchronized` neden var?** Bean `@ApplicationScoped`'tır; tek bir `Channel` nesnesi tüm eşzamanlı isteklerce paylaşılır ve `Channel` thread-safe değildir. Serileştirme, doğru ama ölçeklenmeyen bir çözümdür; yüksek trafikte channel havuzu ya da istek başına kısa ömürlü channel tercih edilmelidir.

**`@PostConstruct` / `@PreDestroy`.** Bağlantı bean ile birlikte doğar ve ölür. Kaynak sızıntısını önleyen şey `@PreDestroy` metodudur.

**Ortam değişkenleri.** `RABBITMQ_HOST` varsayılanı `localhost`'tur; Docker ağı içinden çalışırken `rabbitmq` olmalıdır. Bu bilgilerin gerçek bir sistemde Vault benzeri bir kaynaktan gelmesi beklenir.

## Tüketici Tarafında Topoloji

```java
private void declareTopology() throws IOException {
    channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);

    channel.exchangeDeclare(DLX_NAME, "fanout", true);
    channel.queueDeclare(DLQ_NAME, true, false, false, null);
    channel.queueBind(DLQ_NAME, DLX_NAME, "");

    Map<String, Object> queueArgs = new HashMap<>();
    queueArgs.put("x-dead-letter-exchange", DLX_NAME);

    channel.queueDeclare(QUEUE_NAME, true, false, false, queueArgs);
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
}
```

Exchange'i hem üretici hem tüketici deklare eder. Bu bir tekrar değil, **idempotentlik** kullanımıdır: RabbitMQ, aynı parametrelerle yapılan tekrarlı deklarasyonları hata saymaz. Böylece hangi servisin önce başladığının önemi kalmaz.

Parametrelerin **farklı** olması durumu ise hatadır — aşağıdaki tuzaklara bakın.

## `queueDeclare` Parametreleri

```java
channel.queueDeclare(QUEUE_NAME, durable, exclusive, autoDelete, arguments);
//                                true,    false,     false,      queueArgs
```

| Parametre | Değer | Anlamı |
| --- | --- | --- |
| `durable` | `true` | Broker yeniden başlasa da kuyruk kalır |
| `exclusive` | `false` | Başka bağlantılar da kullanabilir |
| `autoDelete` | `false` | Son tüketici ayrılınca silinmez |
| `arguments` | `x-dead-letter-exchange` | Bkz. [DLQ ve Ack](Dead-Letter-Queue-ve-Ack-Stratejisi) |

## Gözlem

Yönetim paneli `http://localhost:15672` *(guest/guest)*. Konteyner `docker-compose.yml` içinde `java-town-rabbitmq` adıyla tanımlıdır.

Mesajları elle görmek için `figure.exchange` üzerine kendi hata ayıklama kuyruğunuzu bağlayabilirsiniz: `figures.debug.q` adında classic + durable bir kuyruk oluşturup `figure.#` routing key'i ile bind etmek yeterlidir.

## Tuzaklar

- **Farklı parametrelerle yeniden deklarasyon.** Var olan bir kuyruğu farklı `durable` ya da argüman değerleriyle deklare etmek `PRECONDITION_FAILED` hatası verir ve channel kapanır.
- **Mesajı exchange yerine doğrudan kuyruğa göndermek.** Varsayılan exchange üzerinden çalışır ama tüm dolaylılık avantajını kaybedersiniz.
- **Routing key ile binding key uyumsuzluğu.** Mesaj sessizce hiçbir yere gitmez. Kuyrukta `Ready = 0` görüp "işlendi" sanmak buradaki klasik yanılgıdır.
- **Bağlantıyı her mesajda açıp kapatmak.** Pahalıdır; bağlantı uzun ömürlü, channel kısa ömürlü olmalıdır.

## İlgili Sayfalar

- [Dead Letter Queue ve Ack Stratejisi](Dead-Letter-Queue-ve-Ack-Stratejisi)
- [CDI Event ve Observer](CDI-Event-ve-Observer)
- [Eager Bean Başlatma](Eager-Bean-Baslatma)
