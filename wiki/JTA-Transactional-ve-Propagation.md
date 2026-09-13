# JTA, @Transactional ve Propagation

> **Okuma:** ~5 dk · **İlgili modül:** `event-ticketing-service`

| | |
| --- | --- |
| **Ne işe yarar** | Birden fazla yazma işlemini "hepsi ya da hiçbiri" kuralına bağlar |
| **Anahtar** | `@Transactional`, `REQUIRED`, `REQUIRES_NEW` |
| **.NET karşılığı** | `DbContext.SaveChanges()` sınırı, `TransactionScope` |

## Sınırı Kim Çizer?

`event-ticketing-service` modülünün cevabı: **uygulama servisi**.

```java
@ApplicationScoped
public class TicketBookingService {

    @Transactional                       // propagation varsayılan: REQUIRED
    public Booking bookTickets(Long eventId, Long customerId, int seatCount) {
        Event event = eventRepository.findById(eventId);
        eventRepository.reserveSeats(event, seatCount);          // koltuk
        Customer customer = customerRepository.findById(customerId);
        customerRepository.chargeWallet(customer, totalPrice);   // bakiye
        Booking saved = bookingRepository.save(booking);         // rezervasyon
        ...
    }
}
```

Bu metot bir **Unit of Work** sınırıdır. Metot normal biterse container commit eder; unchecked bir istisna yukarı çıkarsa rollback eder. Üç tablo tek bir bütün gibi davranır.

Repository sınıflarında `@Transactional` yoktur ve olmamalıdır. Onlar transaction **sınırı** değil, **katılımcısıdır**.

## Propagation Türleri

| Tür | Aktif transaction varsa | Yoksa |
| --- | --- | --- |
| `REQUIRED` *(varsayılan)* | Katılır | Yenisini başlatır |
| `REQUIRES_NEW` | **Askıya alır**, yeni başlatır | Yenisini başlatır |
| `MANDATORY` | Katılır | İstisna fırlatır |
| `SUPPORTS` | Katılır | Transaction'sız çalışır |
| `NOT_SUPPORTED` | Askıya alır, transaction'sız çalışır | Transaction'sız çalışır |
| `NEVER` | İstisna fırlatır | Transaction'sız çalışır |

Pratikte ilk ikisi işinizi görür. Diğerleri, ne yaptığınızı tam olarak bildiğiniz durumlar içindir.

## REQUIRES_NEW Ne Zaman Gerekir?

Bir gereksinim, ana transaction'ın kaderinden **bağımsız** olmalıysa.

`event-ticketing-service` her rezervasyon denemesini — başarısızlar dahil — kayıt altına alır. Bu kayıt ana transaction'ın içinde olsaydı, rollback onu da geri alır ve elde hiçbir iz kalmazdı.

```java
@ApplicationScoped
public class BookingAttemptLogger {

    @PersistenceContext(unitName = "eventTicketingPU")
    private EntityManager entityManager;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void logAttempt(Long eventId, Long customerId, int seatCount,
                           String status, String failureReason) {
        ...
        entityManager.persist(attempt);
    }
}
```

Container şunu yapar: mevcut transaction askıya alınır → yeni transaction başlar → `persist` commit edilir → askıdaki transaction'a geri dönülür. Ana transaction sonradan rollback olsa bile bu kayıt yerinde kalır.

## Kanıt

Yetersiz bakiyeyle bir rezervasyon deneyin:

```bash
curl -X POST http://localhost:8080/event-ticketing-service/api/bookings \
  -H "Content-Type: application/json" \
  -d '{"eventId":2,"customerId":1,"seatCount":10}'
```

- `events.seats_sold` **değişmez** — oysa `reserveSeats` istisnadan önce çalışmıştı; rollback geri aldı
- `customers.wallet_balance` değişmez
- `bookings` tablosuna satır eklenmez
- `booking_attempts` tablosunda `FAILURE` satırı **vardır**

```bash
curl http://localhost:8080/event-ticketing-service/api/booking-attempts | jq
```

## Rollback Kuralı

`@Transactional` varsayılan olarak yalnızca **unchecked** istisnalarda rollback eder. Bu depodaki tüm iş kuralı istisnalarının `RuntimeException` türevi olmasının nedeni budur:

```text
BookingException extends RuntimeException
```

Checked bir istisnada rollback istiyorsanız açıkça belirtmeniz gerekir:

```java
@Transactional(rollbackOn = SomeCheckedException.class)
@Transactional(dontRollbackOn = ValidationException.class)
```

## Tuzaklar

- **Kendi kendini çağıran metot.** `@Transactional` bir interceptor ile çalışır ve interceptor proxy üzerindedir. Aynı sınıf içinden `this.logAttempt(...)` çağırmak proxy'yi atlar; **hiçbir transaction başlamaz** ve hata da almazsınız. Bu, listedeki en sinsi maddedir.
- **`REQUIRES_NEW` bedeli.** Havuzdan ikinci bir bağlantı ister. Yoğun yükte darboğaz olur; iç içe kullanımlar kilitlenmeye kadar gidebilir.
- **İstisnayı yutmak.** `catch` bloğunda istisnayı yakalayıp yeniden fırlatmazsanız container rollback gerekçesini göremez.
- **`private`, `final` ya da `static` metotlara anotasyon koymak.** Proxy'lenemez, sessizce çalışmaz.
- **Transaction içinde uzun süren dış çağrı.** HTTP çağrısı ya da mesaj gönderimi transaction'ı ve bağlantıyı gereksiz yere açık tutar.
- **Tek veritabanı varsayımı.** Birden fazla servis ve veritabanına geçtiğinizde bu mekanizma yetmez. Bkz. [SAGA Orchestration](SAGA-Orchestration).

## İlgili Sayfalar

- [Entity Yaşam Döngüsü ve Dirty Checking](Entity-Yasam-Dongusu-ve-Dirty-Checking)
- [CDI Interceptor ile AOP](CDI-Interceptor-ile-AOP)
- [SAGA Orchestration](SAGA-Orchestration)
