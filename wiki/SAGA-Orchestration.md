# SAGA Orchestration

> **Okuma:** ~5 dk · **İlgili modül:** `saga-orchestration/*`

| | |
| --- | --- |
| **Ne işe yarar** | Birden fazla servis ve veritabanına yayılan işlemleri tutarlı tutar |
| **Anahtar** | Telafi edici *(compensating)* adımlar, orkestratör |
| **.NET karşılığı** | MassTransit Saga State Machine, NServiceBus Saga |

## Problem

`event-ticketing-service` modülünde üç tablo tek bir `@Transactional` ile korunuyordu. Peki tablolar üç **ayrı servise** ve üç **ayrı veritabanına** dağılırsa?

Klasik transaction mekanizması burada işe yaramaz. Dağıtık transaction'ın kitabi çözümü 2PC *(Two-Phase Commit)*'tir; ancak kilit süreleri, koordinatör bağımlılığı ve modern veritabanlarındaki sınırlı destek nedeniyle pratikte tercih edilmez. Bu depoda da bilinçli olarak ele alınmamıştır.

SAGA'nın önerisi şudur: **tek büyük transaction yerine, her biri kendi içinde commit eden küçük adımlar; hata durumunda yapılanı geri alan telafi adımları.**

## İki Yaklaşım

| | Orchestration | Choreography |
| --- | --- | --- |
| Koordinasyon | Merkezi orkestratör | Servisler birbirini olaylarla tetikler |
| Akışı izlemek | Kolay — tek yerde | Zor — dağıtık |
| Zayıflık | Tek hata noktası | İzlenebilirlik, Outbox ihtiyacı |
| Altyapı | HTTP çağrıları yeter | Kafka/RabbitMQ gerekir |
| Zorluk | Orta | Yüksek |

Üçüncü bir seçenek **TCC** *(Try-Confirm-Cancel)*'dir: her servis geçici rezervasyon, onay ve iptal olmak üzere üç uç nokta sunar. En sağlam, en pahalı yaklaşımdır.

Bu depo **Orchestration** yaklaşımını uygular.

## Servisler

| Servis | Sorumluluk | Veritabanı | Port |
| --- | --- | --- | --- |
| Event Service | Koltuk rezervasyonu + telafi *(serbest bırakma)* | PostgreSQL | 8081 |
| Wallet Service | Bakiye düşme + telafi *(geri yükleme)* | MySQL | 8082 |
| Booking Audit Service | Her denemeyi kaydeder | H2 | 8083 |
| Orchestrator Service | Akışı yönetir | Yok | 8080 |

Orkestratörün **veritabanı olmaması** dikkat çekicidir: durumu tutmaz, yalnızca akışı yürütür.

## Akış

```java
@ApplicationScoped
public class BookingSagaOrchestrator {

    public BookingResult bookTickets(Long eventId, Long customerId, int seatCount) {
        BigDecimal ticketPrice = BigDecimal.ZERO;

        try {
            ticketPrice = eventServiceClient.reserveSeats(eventId, seatCount);
        } catch (SagaStepException e) {
            auditServiceClient.logAttempt(eventId, customerId, seatCount, "FAILURE", e.getMessage());
        }

        BigDecimal totalPrice = ticketPrice.multiply(BigDecimal.valueOf(seatCount));

        try {
            walletServiceClient.charge(customerId, totalPrice);
        } catch (SagaStepException e) {
            eventServiceClient.releaseSeats(eventId, seatCount);     // ← telafi adımı
            auditServiceClient.logAttempt(eventId, customerId, seatCount, "FAILURE", e.getMessage());
        }

        auditServiceClient.logAttempt(eventId, customerId, seatCount, "SUCCESS", null);
        return new BookingResult(eventId, customerId, seatCount, totalPrice);
    }
}
```

Asıl fikir `releaseSeats` çağrısındadır: bakiye düşürme başarısız olduğunda, **önceki adımın etkisi elle geri alınır**. Rollback yoktur; telafi vardır.

## Servisler Arası Çağrı

```java
@ApplicationScoped
public class EventServiceClient {

    private static final String BASE_URL = System.getenv()
        .getOrDefault("EVENT_SERVICE_URL", "http://localhost:8081/event-service/api");

    @PostConstruct
    void init() { client = ClientBuilder.newClient(); }

    @PreDestroy
    void cleanup() { if (client != null) client.close(); }
}
```

JAX-RS Client API kullanılır. `Client` nesnesi pahalıdır; `@PostConstruct` ile bir kez oluşturulup `@PreDestroy` ile kapatılması bu yüzdendir.

> **Yapılandırma notu:** Varsayılan URL'lerdeki context path'ler *(`event-service`, `wallet-service`, `booking-audit-service`)* ile modüllerin WAR adları *(`saga-event-service`, `saga-wallet-service`, `saga-audit-booking-service`)* farklıdır. Servisleri README'deki komutlarla deploy ediyorsanız `EVENT_SERVICE_URL`, `WALLET_SERVICE_URL` ve `AUDIT_SERVICE_URL` ortam değişkenlerini vermeniz gerekir.

## Bu Örnekteki Bilinçli Sadeleştirmeler

Kodu okurken fark edeceğiniz ve gerçek bir sistemde ele alınması gereken noktalar:

- **`catch` bloğu akışı durdurmuyor.** İlk adım başarısız olsa bile metot devam eder; denemeyi incelerken bu davranışı özellikle gözlemleyin. Gerçek bir orkestratörde her başarısız adım akışı sonlandırmalı ve sonucu çağırana bildirmelidir.
- **Telafi adımının kendisi başarısız olabilir.** `releaseSeats` çağrısı da patlarsa koltuklar rezerve kalır. Üretim sistemlerinde telafi adımları yeniden denenir ve kalıcı olarak kaydedilir.
- **Idempotentlik.** Aynı telafi iki kez çalışırsa bakiye iki kez geri yüklenmemelidir. Bunun için her saga'ya bir kimlik verilir.
- **Saga durumu kalıcı değil.** Orkestratör çökerse yarım kalan akış kaybolur. Gerçek çözümlerde saga durumu bir veritabanında tutulur.

## Test

```bash
mvn -f saga-orchestration/pom.xml clean package

java -Djava.net.preferIPv4Stack=true -jar payara-micro-7.2026.5.jar --port 8081 --deploy wars/saga-event-service.war
java -Djava.net.preferIPv4Stack=true -jar payara-micro-7.2026.5.jar --port 8082 --deploy wars/saga-wallet-service.war
java -Djava.net.preferIPv4Stack=true -jar payara-micro-7.2026.5.jar --port 8083 --deploy wars/saga-audit-booking-service.war
java -Djava.net.preferIPv4Stack=true -jar payara-micro-7.2026.5.jar --port 8080 --deploy wars/saga-orchestrator-service.war
```

SQL betikleri `sql/sagaOrchestration/` altındadır; H2 tarafında tablolar uygulama açılışında oluşturulur.

## İlgili Sayfalar

- [JTA, @Transactional ve Propagation](JTA-Transactional-ve-Propagation)
- [RabbitMQ Exchange, Queue ve Binding](RabbitMQ-Exchange-Queue-ve-Binding)
- [Katmanlı Mimari ve Repository Deseni](Katmanli-Mimari-ve-Repository-Deseni)
