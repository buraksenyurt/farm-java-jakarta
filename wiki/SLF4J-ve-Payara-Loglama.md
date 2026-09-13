# SLF4J ve Payara Loglama

> **Okuma:** ~3 dk · **İlgili modül:** `inventory/*`, `event-ticketing-service`, `game-catalog-service`

| | |
| --- | --- |
| **Ne işe yarar** | Uygulama kodunu loglama kütüphanesinden bağımsız kılar |
| **Anahtar** | Facade + binding, `slf4j-api`, `slf4j-jdk14` |
| **.NET karşılığı** | `ILogger<T>` soyutlaması ile Serilog/NLog sağlayıcıları |

## Facade Nedir?

[SLF4J](https://github.com/qos-ch/slf4j) — *Simple Logging Facade for Java* — farklı loglama framework'lerini tek bir arayüz arkasına alır. Uygulama kodu yalnızca arayüzü bilir:

```java
private static final Logger logger = LoggerFactory.getLogger(Publisher.class);

logger.info("Event gönderildi [{}]: {}", ROUTING_KEY, jsonMessage);
logger.error("RabbitMQ bağlantısı kurulurken hata oluştu: ", e);
```

`org.slf4j.Logger` ve `org.slf4j.LoggerFactory` dışında hiçbir import yoktur. Arkadaki motor değişse kod değişmez.

## İki Bağımlılık, İki Rol

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>       <!-- arayüz -->
    <version>2.0.18</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>     <!-- binding: JUL'a köprü -->
    <version>2.0.18</version>
</dependency>
```

İkincisi, SLF4J çağrılarını `java.util.logging` *(JUL)* üzerine yönlendirir. Payara kendi loglama altyapısını JUL üzerine kurduğu için, bu seçimle uygulama logları sunucunun kendi log akışına karışır: aynı dosya, aynı biçim, aynı yapılandırma.

Alternatif binding'ler `slf4j-simple`, `logback-classic`, `slf4j-log4j12` olurdu. Bağımlılığı değiştirmek yeterlidir; kod aynı kalır. Facade'in tüm amacı budur.

## Parametreli Loglama

```java
logger.info("Event alındı: {}", figure.getName());              // doğru
logger.info("Event alındı: " + figure.getName());               // yanlış
```

İlkinde string birleştirme, log seviyesi gerçekten etkinse yapılır. İkincisinde `DEBUG` kapalı olsa bile birleştirme maliyeti ödenir. Sıcak yollarda fark ölçülebilir hale gelir.

İstisna loglarken `e` nesnesini **son parametre** olarak verin; SLF4J yığın izini otomatik basar:

```java
logger.error("Mesaj işlenirken hata oluştu, DLQ'ya yönlendiriliyor: ", e);
```

## `System.out.println` Meselesi

Bu depoda bilinçli bir tutarsızlık vardır: `cdi-concept` ve `todo-app` gibi erken örneklerde `System.out.println` kullanılır — kavramı en az gürültüyle göstermek için. Daha ileri modüller SLF4J'ye geçer.

`arch-guard-lab` bu tercihi bir kurala dönüştürür ve ADR-0004 altında test eder:

```java
GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
    .as("ADR-0004: Do not use System.out / System.err ")
    .because("Logging via SLF4J is used.");
```

Gerekçe: `System.out` çıktısı seviyelendirilemez, biçimlendirilemez, yönlendirilemez ve merkezi bir toplayıcıya aktarılamaz.

## Log Seviyeleri

| Seviye | Ne zaman |
| --- | --- |
| `ERROR` | İşlem başarısız, müdahale gerekebilir |
| `WARN` | Beklenmedik ama işlem sürdü |
| `INFO` | İş akışının önemli adımları |
| `DEBUG` | Geliştirme sırasında ayrıntı |
| `TRACE` | Çok ayrıntılı izleme |

## Tuzaklar

- **Birden fazla binding.** Classpath'te iki binding varsa SLF4J uyarı basar ve birini rastgele seçer.
- **Binding'i hiç eklememek.** `slf4j-api` tek başına hiçbir yere yazmaz; "no-operation" uyarısı görürsünüz.
- **Logger'ı `static final` yapmamak.** Her örnek için yeni logger üretmek gereksizdir.
- **Hassas veri loglamak.** Parola, token, kişisel veri log dosyasına düşerse orada kalır.
- **Loglama uğruna istisna yutmak.** `logger.error` çağırıp istisnayı yeniden fırlatmamak, transaction'ın rollback olmamasına yol açabilir.

## İlgili Sayfalar

- [Fluent Bit ve OpenObserve](Fluent-Bit-ve-OpenObserve)
- [ArchUnit ile Mimari Testler](ArchUnit-ile-Mimari-Testler)
- [Application Server ve Payara Micro](Application-Server-ve-Payara-Micro)
