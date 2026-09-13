# JAX-RS ExceptionMapper

> **Okuma:** ~4 dk · **İlgili modül:** `event-ticketing-service`, `game-catalog-service`

| | |
| --- | --- |
| **Ne işe yarar** | İş mantığındaki istisnaları HTTP durum kodlarına çevirir |
| **Anahtar** | `@Provider`, `ExceptionMapper<T>` |
| **.NET karşılığı** | Exception filter / middleware, `ProblemDetails` |

## Problem

`TicketBookingService` bakiye yetersizse `InsufficientBalanceException` fırlatır. Kaynak sınıfının bunu yakalayıp `409 Conflict` üretmesi gerekir. Ama bu kodu her uç noktada tekrarlamak istemeyiz:

```java
try {
    ...
} catch (InsufficientBalanceException e) {
    return Response.status(409)...      // her metotta aynı blok
}
```

## Çözüm: İstisna Hiyerarşisi + Eşleyiciler

`event-ticketing-service` istisnaları anlamlarına göre gruplar:

```text
BookingException (abstract, RuntimeException)
├── BusinessConflictException (abstract)
│   ├── InsufficientBalanceException
│   └── InsufficientCapacityException
└── ResourceNotFoundException (abstract)
    ├── CustomerNotFoundException
    └── EventNotFoundException
```

Her **ara sınıf** için tek bir eşleyici yazılır:

```java
@Provider
public class BusinessConflictExceptionMapper implements ExceptionMapper<BusinessConflictException> {

    @Override
    public Response toResponse(BusinessConflictException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity(Map.of("error", e.getMessage()))
                .build();
    }
}
```

`ResourceNotFoundExceptionMapper` aynı biçimde `404` döner.

Yeni bir iş kuralı istisnası eklendiğinde doğru ara sınıftan türetmek yeterlidir; HTTP katmanında tek satır değişmez. Kuralın adı da domain dilinde kalır.

## Son Savunma Hattı

```java
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable e) {
        logger.error("Beklenmeyen bir hata yakalandı.", e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Beklenmeyen bir hata oluştu"))
                .build();
    }
}
```

İki iş birden yapar: yığın izini **sunucu tarafında loglar**, istemciye **hiçbir iç detay sızdırmadan** genel bir mesaj döner. Bu ayrım güvenlik açısından önemlidir — istisna metni tablo adı, SQL parçası ya da dosya yolu içerebilir.

## Eşleyici Seçimi: En Yakın Tip Kazanır

JAX-RS, fırlatılan istisna için **en özel** eşleyiciyi seçer. `InsufficientBalanceException` fırlatıldığında:

1. Tam eşleşen bir eşleyici var mı? → Hayır
2. Üst sınıfı `BusinessConflictException` için var mı? → **Evet, bu kullanılır**
3. `Throwable` eşleyicisi yedekte bekler.

Bu yüzden `GenericExceptionMapper` diğerlerini gölgelemez.

## Doğrulama Hataları

`game-catalog-service` bir eşleyici daha ekler: `ConstraintViolationExceptionMapper`. Bean Validation ihlallerini toplayıp `400 Bad Request` ile birlikte okunabilir bir liste döner. Ayrıntı için bkz. [Bean Validation](Bean-Validation).

## Tuzaklar

- **`@Provider` unutmak.** Sınıf taranmaz, eşleyici hiç devreye girmez ve istemci ham bir `500` görür.
- **Kontrol edilen *(checked)* istisna kullanmak.** `@Transactional` varsayılan olarak yalnızca unchecked istisnalarda rollback eder; iş kuralı istisnalarının `RuntimeException` türevi olması bu yüzdendir.
- **Eşleyici içinde yeni istisna fırlatmak.** Zincir kopar, sonuç öngörülemez olur.
- **Yığın izini istemciye göndermek.** Hata ayıklamayı kolaylaştırır, güvenliği bozar.
- **`WebApplicationException` ile karıştırmak.** O, durum kodunu doğrudan taşıyan bir istisnadır; eşleyici mekanizmasına gerek bırakmaz ama domain dilini HTTP'ye bağlar.

## İlgili Sayfalar

- [JAX-RS Application ve Kaynak Sınıfları](JAX-RS-Application-ve-Kaynak-Siniflari)
- [Bean Validation](Bean-Validation)
- [JTA, @Transactional ve Propagation](JTA-Transactional-ve-Propagation)
