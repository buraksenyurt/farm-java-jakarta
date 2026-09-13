# Bean Validation

> **Okuma:** ~4 dk · **İlgili modül:** `game-catalog-service`, `todo-app`, `memo-app`

| | |
| --- | --- |
| **Ne işe yarar** | Yapısal doğrulamayı anotasyonlarla bildirir |
| **Anahtar** | `@NotBlank`, `@Min`, `@Valid`, `ConstraintViolationException` |
| **.NET karşılığı** | `System.ComponentModel.DataAnnotations` + `ModelState` |

## İki Farklı Doğrulama

Bu ayrım, `game-catalog-service` modülünün en öğretici tarafıdır.

| | **Yapısal doğrulama** | **İş kuralı doğrulaması** |
| --- | --- | --- |
| Soru | "Bu veri biçimsel olarak geçerli mi?" | "Bu veri bizim iş dünyamızda anlamlı mı?" |
| Nerede | Entity/DTO üzerinde anotasyon | Servis katmanında kod |
| Örnek | Başlık boş olamaz, puan 1-10 arası | Yayın yılı gelecekte olamaz |
| Ne bilir | Yalnızca alanın kendisi | Domain bağlamı, tarih, diğer kayıtlar |

Yayın yılının gelecekte olamayacağı kuralı neden anotasyon değil? Çünkü "gelecek" bir çalışma zamanı bilgisidir ve domain bilgisi gerektirir. Bu yüzden `GameService` içinde yaşar.

```java
private void validateBusinessRules(Game game) {
    int currentYear = Year.now().getValue();
    if (game.getReleaseYear() > currentYear) {
        throw new GameBusinessValidationException("Gelecek bir yıl için giriş yapamayız!");
    }
}
```

## Yapısal Doğrulama

```java
@Entity
@Table(name = "games")
public class Game {

    @NotBlank(message = "Oyun başlığı boş olamaz")
    @Column(nullable = false)
    private String title;

    @Min(value = 1970, message = "Piyasaya sürüldüğü yıl bilgisi geçerli olmalı.")
    @Column(nullable = false, name = "release_year")
    private int releaseYear;

    @Min(value = 1, message = "Topluluk puanı 1 den büyük, 10'dan küçük olmalıdır")
    @Max(value = 10, message = "Topluluk puanı 1 den büyük, 10'dan küçük olmalıdır")
    private short score;
}
```

`@Column(nullable = false)` ile `@NotBlank` birlikte kullanılmıştır ve bu tekrar değildir: ilki **veritabanı** kısıtıdır, ikincisi **uygulama** kısıtı. Birincisi ihlal edilirse anlaşılmaz bir SQL hatası alırsınız; ikincisi ihlal edilirse istemciye anlamlı bir mesaj gider.

## Tetikleme: `@Valid`

```java
@POST
public Response create(@Valid Game game, @Context UriInfo uriInfo) { ... }
```

`@Valid` olmadan anotasyonlar REST katmanında **çalışmaz**. Bu, en sık gözden kaçan ayrıntıdır: anotasyonlar yerinde durur, kod derlenir, hiçbir doğrulama yapılmaz.

## Hatanın İstemciye Dönüşü

Doğrulama başarısız olursa çalışma zamanına `ConstraintViolationException` istisnası fırlar. Varsayılan yanıt, istemci için hiçbir şey ifade etmeyen bir `500` mesajı olurdu. Eşleyici bunu okunabilir hale getirir:

```java
@Provider
public class ConstraintViolationExceptionMapper
        implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        List<String> errors = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("errors", errors))
                .build();
    }
}
```

Sonuç:

```json
{ "errors": ["Oyun başlığı boş olamaz", "Topluluk puanı 1 den büyük, 10'dan küçük olmalıdır"] }
```

Tek tek değil, **tüm** ihlaller birlikte döner. Formu dolduran kullanıcı hataları tek seferde görür.

## Sık Kullanılan Kısıtlar

| **Anotasyon** | **Ne kontrol eder** | **Not** |
| --- | --- | --- |
| `@NotNull` | `null` değil | Boş string geçerlidir |
| `@NotEmpty` | `null` değil ve boş değil | String, koleksiyon, dizi |
| `@NotBlank` | `null` değil ve sadece boşluk değil | Yalnızca String |
| `@Size(min, max)` | Uzunluk | String ve koleksiyon |
| `@Min` / `@Max` | Sayısal sınır | |
| `@Positive` | Sıfırdan büyük | `@PositiveOrZero` de vardır |
| `@Pattern` | Düzenli ifade | `memo-app` kullanır |
| `@FutureOrPresent` | Tarih bugün ya da sonrası | `todo-app` kullanır |

## Tuzaklar

- **`@Valid` unutmak.** Sessiz başarısızlık; hiçbir doğrulama yapılmaz.
- **`@NotNull` ile `@NotBlank` karıştırmak.** `""` değeri `@NotNull` kontrolünü geçer.
- **Entity üzerinde doğrulama yaparken JPA katmanını unutmak.** Anotasyonlar `persist`/`update` sırasında da çalışır; REST katmanında yakalanmayan bir ihlal commit anında patlar ve bu kez `400` değil `500` alırsınız.
- **İş kurallarını anotasyona sıkıştırmaya çalışmak.** Bağlam gerektiren her kural servis katmanına aittir.
- **`message` vermemek.** Varsayılan mesajlar İngilizce ve genel olur.

## İlgili Sayfalar

- [JAX-RS ExceptionMapper](JAX-RS-ExceptionMapper)
- [JAX-RS Application ve Kaynak Sınıfları](JAX-RS-Application-ve-Kaynak-Siniflari)
- [Katmanlı Mimari ve Repository Deseni](Katmanli-Mimari-ve-Repository-Deseni)
