# Katmanlı Mimari ve Repository Deseni

> **Okuma:** ~5 dk · **İlgili modül:** `game-catalog-service`, `arch-guard-lab`

| | |
| --- | --- |
| **Ne işe yarar** | Sorumlulukları ayırır, bağımlılığın yönünü sabitler |
| **Anahtar** | Port/Adapter, Dependency Inversion |
| **.NET karşılığı** | Clean Architecture katmanları, `IRepository` + EF implementasyonu |

## Katmanlar

```text
        api  ──────▶  application  ──────▶  domain
                                              ▲
                                              │
                                        persistence
```

| Katman | Sorumluluk | `game-catalog-service` | `arch-guard-lab` |
| --- | --- | --- | --- |
| `api` / `resource` | HTTP, serileştirme | `GameResource` | `BookResource` |
| `application` / `service` | İş kuralları, orkestrasyon | `GameService` | `LoanService` |
| `domain` / `model` | Çekirdek model, arayüzler | `Game`, `GameRepository` | `Book`, `Isbn`, `BookRepository` |
| `persistence` / `repository` | Veri erişimi | `JpaGameRepository` | `JpaBookRepository` |

## İki Kural

### 1. Oklar içeri bakar

`domain` hiç kimseyi tanımaz. `api`, `persistence` katmanını doğrudan çağıramaz — aradaki `application` katmanını atlayamaz.

### 2. Persistence oku yukarı bakar

Bu, ilk bakışta şemayı bozuyor gibi görünür. `JpaBookRepository`, `domain` içindeki `BookRepository` arayüzünü implemente eder — yani dışarıdaki katman içerideki soyutlamaya bağımlıdır.

Buna **Dependency Inversion Principle** denir. Arayüz *(port)* domain'e aittir; implementasyon *(adapter)* dışarıdadır. Sonuç: domain, veritabanının varlığından bile habersiz kalır.

## Repository Deseni Uygulamada

`game-catalog-service` bu ayrımın değerini iki implementasyonla gösterir:

```java
public interface GameRepository {
    Game save(Game game);
    Optional<Game> findById(Long id);
    List<Game> findByGenre(Genre genre);
    ...
}
```

| Implementasyon | Ne yapar |
| --- | --- |
| `InMemoryGameRepository` | Bellekteki koleksiyon — ilk aşama |
| `JpaGameRepository` | MySQL üzerinde JPA — ikinci aşama |

Bellek içi implementasyondan JPA'ya geçişte `GameService` sınıfında **tek satır** değişmedi. Bağımlılık arayüzedir; hangi implementasyonun enjekte edileceğini CDI çözer. Birden fazla aday olduğunda seçim için bkz. [CDI Qualifier](CDI-Qualifier).

## İsimlendirme: Neden `Impl` Yok?

`arch-guard-lab` içindeki ADR-0003 kuralı `*Impl` son ekini yasaklar ve bir ArchUnit testiyle bunu zorunlu kılar:

```java
noClasses().should().haveSimpleNameEndingWith("Impl")
```

Gerekçe: `BookRepositoryImpl` adı hiçbir bilgi taşımaz. `JpaBookRepository` ise implementasyonun **nasıl** çalıştığını söyler — ve yarın bir `InMemoryBookRepository` eklendiğinde iki ad yan yana anlamlı durur.

## İki Model mi, Tek Model mi?

`arch-guard-lab` bilinçli olarak iki ayrı sınıf tutar:

- `domain.Book` — düz Java, `new` ile üretilir, hiçbir framework anotasyonu yok
- `persistence.BookEntity` — JPA anotasyonlarıyla donatılmış

Maliyeti, persistence katmanında açıkça yazılan eşleme kodudur. Kazancı ADR-0002'de şöyle kayıtlıdır: domain sınıfı bir JPA entity'si olduğu anda, bir iş kuralını test etmek `EntityManager` gerektirir, o da persistence unit gerektirir, o da veritabanı gerektirir. Bir milisaniyede doğrulanabilecek kural için container ayağa kaldırmak gerekir.

`game-catalog-service` ise tek model kullanır — daha az kod, daha hızlı başlangıç. İkisi arasındaki seçim proje ölçeğine bağlıdır ve bu depoda her iki yaklaşım da bilerek bulunur.

## Tuzaklar

- **Anemik domain.** Tüm mantık servise taşınır, domain sınıfları yalnızca getter/setter kalır. `Book.borrow(today)` metodunun domain içinde olması bilinçli bir tercihtir.
- **Repository'den entity yerine DTO döndürmek.** Katman sorumluluğunu bulandırır; dönüşüm `api` katmanının işidir *(`BookView.from(...)`)*.
- **`api` katmanından repository'ye kısayol.** "Sadece bu seferlik" diye başlar; ArchUnit testi bunu derlemede yakalar.
- **Katman sayısını artırmak.** Her katman bir maliyettir; üç katman çoğu iş için yeterlidir.

## İlgili Sayfalar

- [ADR ve MADR Formatı](ADR-ve-MADR-Formati)
- [ArchUnit ile Mimari Testler](ArchUnit-ile-Mimari-Testler)
- [CDI Qualifier](CDI-Qualifier)
