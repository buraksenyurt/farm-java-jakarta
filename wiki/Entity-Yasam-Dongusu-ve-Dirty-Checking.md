# Entity Yaşam Döngüsü ve Dirty Checking

> **Okuma:** ~4 dk · **İlgili modül:** `event-ticketing-service`, `todo-app`

| | |
| --- | --- |
| **Ne işe yarar** | `merge` çağırmadan `UPDATE` üretilmesinin açıklaması |
| **Anahtar** | managed / detached durumları, `@PrePersist` |
| **.NET karşılığı** | EF Core change tracking |

## Dört Durum

| Durum | Anlamı | Nasıl girilir |
| --- | --- | --- |
| **New / Transient** | Yeni `new` edilmiş, veritabanıyla ilgisi yok | `new Booking()` |
| **Managed** | Persistence context izliyor | `persist()` ya da `find()` |
| **Detached** | Bir zamanlar yönetiliyordu, artık değil | Transaction/context kapanınca |
| **Removed** | Silinmek üzere işaretli | `remove()` |

Bu kartın tamamı, **managed** ile **detached** arasındaki farkın pratik sonucundan ibarettir.

## Sihir: Dirty Checking

`TicketBookingService` içindeki akışa dikkatle bakın:

```java
Event event = eventRepository.findById(eventId);     // managed
eventRepository.reserveSeats(event, seatCount);      // yalnızca setter çağrılıyor
```

`reserveSeats` metodunun tamamı şudur:

```java
public void reserveSeats(Event event, int seatCount) {
    if (event.getSeatsSold() + seatCount > event.getTotalSeats()) {
        throw new InsufficientCapacityException(event.getId(), seatCount);
    }
    event.setSeatsSold(event.getSeatsSold() + seatCount);
}
```

Hiçbir yerde `merge`, `update` ya da `save` yok. Yine de veritabanına `UPDATE` gider.

Nedeni: `find` ile getirilen nesne **managed** durumdadır. Persistence context nesnenin ilk halinin bir kopyasını tutar ve commit anında ikisini karşılaştırır. Fark bulursa gerekli SQL'i kendisi üretir. Buna **dirty checking** denir.

Aynı davranışı `JpaCustomerRepository.chargeWallet` metodunda da görürsünüz.

## Neden Bazı Yerlerde `merge` Var?

```java
public Optional<Game> update(Long id, Game game) {
    Game existing = entityManager.find(Game.class, id);
    if (existing == null) return Optional.empty();
    game.setId(id);
    return Optional.of(entityManager.merge(game));
}
```

Burada `game`, HTTP gövdesinden gelen ve JSON'dan üretilmiş bir nesnedir — persistence context onu hiç görmemiştir, yani **detached**'dır. `merge`, bu nesnenin durumunu yönetilen bir kopyaya aktarır.

Kural basitçe şudur:

- Nesne transaction içinde `find` ile geldiyse → setter yeter
- Nesne dışarıdan geldiyse → `merge` gerekir

`merge` metodunun **verdiğiniz nesneyi değil, yeni bir yönetilen kopyayı** döndürdüğüne dikkat edin. Sonraki işlemlerde dönen nesneyi kullanmak gerekir.

## Transaction Sınırının Dışı

Transaction bitince persistence context kapanır ve tüm nesneler detached olur. Detached bir nesne üzerinde yaptığınız değişiklikler **hiçbir yere yazılmaz** ve hata da almazsınız.

Bu, katmanlar arasında entity taşıyan tasarımlarda en sık yaşanan kayıp veri kaynağıdır.

## Yaşam Döngüsü Kancaları

```java
@Entity
public class Todo {

    private LocalDate dateCreated;

    @PrePersist
    private void init() {
        setDateCreated(LocalDate.now());
    }
}
```

Oluşturulma tarihini iş kodunun ayarlaması gerekmez; JPA `persist` öncesinde bu metodu çağırır.

| Kanca | Ne zaman |
| --- | --- |
| `@PrePersist` / `@PostPersist` | `INSERT` öncesi / sonrası |
| `@PreUpdate` / `@PostUpdate` | `UPDATE` öncesi / sonrası |
| `@PreRemove` / `@PostRemove` | `DELETE` öncesi / sonrası |
| `@PostLoad` | Veritabanından okunduktan sonra |

## Tuzaklar

- **Detached nesnede setter çağırıp kaydedildiğini sanmak.** Sessiz veri kaybı.
- **`merge` sonrası eski referansı kullanmaya devam etmek.** Yönetilen olan, dönen nesnedir.
- **Yaşam döngüsü kancasında başka entity'lere dokunmak.** Davranış öngörülemez; kancalar basit kalmalıdır.
- **Dirty checking maliyetini unutmak.** Çok sayıda yönetilen nesne, commit anında karşılaştırma maliyeti demektir. Toplu işlemlerde `clear()` ya da toplu sorgular düşünülmelidir.
- **Enum'larda `EnumType.ORDINAL`.** `Game` sınıfında bilinçli olarak `STRING` kullanılmıştır; ordinal değerler enum'a yeni bir eleman eklendiğinde eski kayıtları sessizce yanlış türe işaret ettirir.

## İlgili Sayfalar

- [EntityManager, PersistenceContext ve JPQL](EntityManager-PersistenceContext-ve-JPQL)
- [JTA, @Transactional ve Propagation](JTA-Transactional-ve-Propagation)
