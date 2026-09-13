# Eager Bean Başlatma

> **Okuma:** ~3 dk · **İlgili modül:** `inventory-notification-service`, `game-catalog-service`

| | |
| --- | --- |
| **Ne işe yarar** | Hiçbir yerden enjekte edilmeyen bir bean'i deploy anında ayağa kaldırır |
| **Anahtar** | `@Observes @Initialized(ApplicationScoped.class)` |
| **.NET karşılığı** | `IHostedService` / `StartupTask` |

## Problem

CDI bean'leri tembeldir. `@ApplicationScoped` bir sınıf enjekte edilir ama hiçbir metodu çağrılmazsa nesne hiç oluşturulmaz, dolayısıyla `@PostConstruct` metodu da hiç çalışmaz.

Bu, çoğu zaman istenen davranıştır. Ancak iki durumda tam bir tuzağa dönüşür:

- **Mesaj tüketicisi:** Kimse `Consumer` sınıfını enjekte etmez; onun işi dışarıdan gelen mesajları dinlemektir. Tembel kalırsa kuyruğa hiç bağlanmaz.
- **Başlangıç görevi:** Veritabanı migration'ı, uygulama açılırken bir kez çalışmalıdır.

Her iki durumda da hata almazsınız. Uygulama sorunsuz açılır ve hiçbir şey yapmaz. Teşhis edilmesi en zor hata türü budur.

## Çözüm

```java
@ApplicationScoped
public class Consumer {

    // Container ApplicationScoped context'ini başlattığında (deploy anında)
    // bu observer tetiklenir ve bean eager olarak oluşturulur.
    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object init) {
    }

    @PostConstruct
    public void init() {
        // RabbitMQ bağlantısı, kuyruk deklarasyonu, basicConsume...
    }
}
```

Metodun gövdesi **boştur** ve öyle olması yeterlidir. Amaç iş yapmak değil, CDI'ı bean'i oluşturmaya zorlamaktır. Nesne oluşturulduğu anda `@PostConstruct` da çalışır.

## İkinci Kullanım: Migration

`game-catalog-service` aynı mekanizmayı farklı bir amaçla kullanır — burada metodun gövdesi dolu:

```java
@ApplicationScoped
public class FlywayMigrationRunner {

    @Resource(lookup = "java:app/jdbc/gamecatalog")
    private DataSource dataSource;

    void onStart(@Observes @Initialized(ApplicationScoped.class) Object init) {
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.migrate();
    }
}
```

Uygulama her açıldığında bekleyen migration'lar uygulanır. Bkz. [Flyway ile Versiyonlama](Flyway-ile-Versiyonlama).

## Nasıl Çalışır?

Container, `ApplicationScoped` context'ini başlattığında `@Initialized(ApplicationScoped.class)` qualifier'ı taşıyan bir olay fırlatır. Bu olay Jakarta EE'nin kendi yaşam döngüsü olaylarından biridir; siz fırlatmazsınız, yalnızca dinlersiniz.

Karşılığı `@Destroyed(ApplicationScoped.class)` olup, uygulama kapanırken tetiklenir.

## Teşhis: Bean Çalışıyor mu?

`@PostConstruct` metodunun ilk satırına bir log koyun ve deploy günlüğünde arayın:

```text
RabbitMQ consumer başlatıldı, kuyruk: notification.figure.stock.arrived.q
Flyway migration işlemi başlıyor...
```

Bu satırı göremiyorsanız bean hiç oluşmamıştır.

## Tuzaklar

- **Parametre tipini `Object` dışında bir şey yapmak.** Olayın taşıdığı yük tanımsızdır; `Object` kullanmak standart yaklaşımdır.
- **Qualifier'ı unutmak.** `@Observes Object init` yazarsanız metot, sistemdeki *her* olayı dinler.
- **Ağır işleri buraya koymak.** Bu metot deploy süresini doğrudan uzatır; istisna fırlatırsa deploy başarısız olur.
- **Sıra beklemek.** Birden fazla eager bean varsa başlatılma sırası tanımsızdır; aralarında bağımlılık varsa `@Priority` gerekir.

## İlgili Sayfalar

- [CDI Kapsamları](CDI-Kapsamlari)
- [CDI Event ve Observer](CDI-Event-ve-Observer)
- [Flyway ile Versiyonlama](Flyway-ile-Versiyonlama)
