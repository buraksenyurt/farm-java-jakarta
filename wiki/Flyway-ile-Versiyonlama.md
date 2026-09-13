# Flyway ile Versiyonlama

> **Okuma:** ~4 dk · **İlgili modül:** `game-catalog-service`

| | |
| --- | --- |
| **Ne işe yarar** | Veritabanı şemasını sürümlenmiş SQL betikleriyle yönetir |
| **Anahtar** | `V1__ad.sql` konvansiyonu, `flyway_schema_history` |
| **.NET karşılığı** | EF Core migration'ları — ama betikler elle yazılır |

## Kurulum

Migration dosyaları `src/main/resources/db/migration` altında bulunur. Flyway bu dizini varsayılan olarak tarar.

```text
game-catalog-service/src/main/resources/db/migration/
├── V1__create_games_table.sql
└── V2__add_summary_column.sql
```

## Dosya Adı Konvansiyonu

```text
V 1 __ create_games_table .sql
│ │ │       │
│ │ │       └── açıklama (alt çizgiler boşluğa dönüşür)
│ │ └────────── iki alt çizgi, zorunlu ayraç
│ └──────────── sürüm numarası
└────────────── V = versioned migration
```

`V` dışında `R` *(repeatable — her değiştiğinde yeniden çalışır, view'ler için)* ve `U` *(undo, ticari sürüm)* önekleri de vardır.

## Çalıştırma

Migration'ları uygulama açılışında tetikleyen küçük bir bileşen yazılmıştır:

```java
@ApplicationScoped
public class FlywayMigrationRunner {

    @Resource(lookup = "java:app/jdbc/gamecatalog")
    private DataSource dataSource;

    void onStart(@Observes @Initialized(ApplicationScoped.class) Object init) {
        logger.info("Flyway migration işlemi başlıyor...");

        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.migrate();

        logger.info("Flyway migration işlemi tamamlandı...");
    }
}
```

`@Observes @Initialized(ApplicationScoped.class)` deseninin neden gerekli olduğu için bkz. [Eager Bean Başlatma](Eager-Bean-Baslatma).

Veri kaynağı `@Resource(lookup = ...)` ile JNDI üzerinden alınır — yani Flyway, uygulamanın kullandığı havuzun aynısını kullanır.

## Nasıl Çalışır?

1. Flyway, veritabanında `flyway_schema_history` adlı bir tablo tutar.
2. Açılışta bu tabloyu okur ve hangi sürümlerin uygulandığını görür.
3. Eksik olanları **sürüm sırasına göre** uygular.
4. Her uygulanan betiğin bir sağlama toplamını *(checksum)* saklar.

Geçmişi görmek için:

```bash
docker exec -it java-town-mysql mysql -u gamecatalog_user -p gamecatalog \
  -e "SELECT version, description, success FROM flyway_schema_history; DESCRIBE games;"
```

## Altın Kural

**Uygulanmış bir migration dosyası asla değiştirilmez.**

Flyway her betiğin checksum'ını saklar. Dosyayı sonradan düzenlerseniz checksum tutmaz ve uygulama `ValidationException` ile açılmayı reddeder. Bu bir aksilik değil, koruma mekanizmasıdır: sizin makinenizde uygulanmış olan betik ile üretimdekinin farklı olması, izi sürülmesi en zor hata sınıfıdır.

Bir şeyi düzeltmek gerekiyorsa yeni bir sürüm dosyası eklenir.

## Entity ile Betiği Birlikte Düşünmek

`V2__add_summary_column.sql` ile `games` tablosuna `summary` sütunu eklendiğinde, `Game` entity sınıfına da karşılık gelen alanın eklenmesi gerekir. Flyway şemayı yönetir, entity'yi değil — ikisini eşlemek geliştiricinin sorumluluğudur.

Tablo adı ile entity adının farklı olabileceğini de unutmayın: fiziksel tablo `games`, JPQL'de kullanılan ad `Game`'dir.

## Tuzaklar

- **Tek alt çizgi kullanmak.** `V1_create.sql` tanınmaz; **iki** alt çizgi gerekir.
- **Sürüm numarası çakışması.** İki geliştirici aynı anda `V3__` yazarsa birleştirme sırasında çakışma çıkar. Takımlarda zaman damgalı sürüm numaraları *(`V20260913120000__`)* tercih edilir.
- **Hem Flyway hem `schema-generation` kullanmak.** `persistence.xml` içindeki değer `none` olmalıdır.
- **Migration içine veri düzeltmesi yazmak.** Şema değişikliği ile veri düzeltmesini ayrı dosyalarda tutmak geri dönüşü kolaylaştırır.
- **Başarısız migration sonrası elle müdahale.** `flyway_schema_history` tablosunda başarısız kayıt kalır; `flyway repair` bunun içindir.

## İlgili Sayfalar

- [Schema Generation ve Migration](Schema-Generation-ve-Migration)
- [Eager Bean Başlatma](Eager-Bean-Baslatma)
- [Veri Kaynağı Tanımlama](Veri-Kaynagi-Tanimlama)
