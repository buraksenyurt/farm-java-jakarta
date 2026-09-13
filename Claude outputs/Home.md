# farm-java-jakarta Wiki

Bu wiki, [farm-java-jakarta](https://github.com/buraksenyurt/farm-java-jakarta) deposundaki örneklerin arkasındaki kavramları **kavram kartı** biçiminde toplar. Her sayfa tek bir konuyu ele alır, 3-6 dakikada okunur ve depodaki gerçek koda referans verir.

Kartlar bir ders anlatımı değil, **başvuru kaynağıdır**. Bir konuya takıldığınızda ilgili kartı açın, gerekirse bağlantılı kartlara sıçrayın.

> C# geçmişinden gelenler için: neredeyse her kartta bir **.NET karşılığı** bölümü var. Yeni bir dünyaya değil, tanıdık kavramların farklı isimlerine bakıyorsunuz.

---

## Temeller

| Kart | Ne öğrenirsiniz |
| --- | --- |
| [Jakarta EE, JSR ve Reference Implementation](Jakarta-EE-JSR-ve-Reference-Implementation) | Spesifikasyon ile implementasyonun neden ayrı olduğu |
| [Application Server ve Payara Micro](Application-Server-ve-Payara-Micro) | Uygulama sunucusu ne yapar, Payara Micro neden seçildi |
| [WAR Paketleme ve provided Scope](WAR-Paketleme-ve-provided-Scope) | WAR dosyasına neyin girip neyin girmediği |
| [Maven Multi-Module Yapısı](Maven-Multi-Module-Yapisi) | Parent POM, aggregator ve `-pl` / `-am` kullanımı |

## CDI — Uygulamanın Sinir Sistemi

| Kart | Ne öğrenirsiniz |
| --- | --- |
| [beans.xml ve Bean Discovery](beans-xml-ve-Bean-Discovery) | Weld motorunun hangi sınıfları taradığı |
| [CDI Kapsamları](CDI-Kapsamlari) | `@RequestScoped`, `@ApplicationScoped`, `@Dependent` farkı |
| [CDI Qualifier](CDI-Qualifier) | Aynı arayüzden iki implementasyon arasında seçim |
| [CDI Event ve Observer](CDI-Event-ve-Observer) | Süreç içi gevşek bağlı haberleşme |
| [CDI Interceptor ile AOP](CDI-Interceptor-ile-AOP) | Kesişen ilgilerin metot çevresine taşınması |
| [Eager Bean Başlatma](Eager-Bean-Baslatma) | Tembel bean'in deploy anında uyandırılması |

## JAX-RS — Dışa Açılan Kapı

| Kart | Ne öğrenirsiniz |
| --- | --- |
| [JAX-RS Application ve Kaynak Sınıfları](JAX-RS-Application-ve-Kaynak-Siniflari) | URL'in nasıl oluştuğu, `@ApplicationPath` |
| [JAX-RS ExceptionMapper](JAX-RS-ExceptionMapper) | İstisnaların HTTP yanıtına çevrilmesi |
| [JAX-RS Asenkron Yanıt](JAX-RS-Asenkron-Yanit) | `@Suspended`, `AsyncResponse`, `ManagedExecutorService` |
| [Bean Validation](Bean-Validation) | `@NotNull`, `@Size` ve doğrulama hatalarının yönetimi |

## JPA — Uygulamanın Hafızası

| Kart | Ne öğrenirsiniz |
| --- | --- |
| [EntityManager, PersistenceContext ve JPQL](EntityManager-PersistenceContext-ve-JPQL) | Sorgulama ve kalıcılık işlemlerinin merkezi |
| [Entity Yaşam Döngüsü ve Dirty Checking](Entity-Yasam-Dongusu-ve-Dirty-Checking) | `merge` çağırmadan `UPDATE` nasıl oluşur |
| [Veri Kaynağı Tanımlama](Veri-Kaynagi-Tanimlama) | `persistence.xml`, `glassfish-resources.xml`, `@DataSourceDefinition` |
| [JTA, @Transactional ve Propagation](JTA-Transactional-ve-Propagation) | `REQUIRED` ve `REQUIRES_NEW` arasındaki fark |
| [Schema Generation ve Migration](Schema-Generation-ve-Migration) | `drop-and-create` neden production'da olmaz |
| [Flyway ile Versiyonlama](Flyway-ile-Versiyonlama) | Elle yazılmış SQL migration'ların yönetimi |

## Mimari

| Kart | Ne öğrenirsiniz |
| --- | --- |
| [Katmanlı Mimari ve Repository Deseni](Katmanli-Mimari-ve-Repository-Deseni) | Bağımlılığın yönü, port/adapter ayrımı |
| [SAGA Orchestration](SAGA-Orchestration) | Dağıtık transaction ve telafi adımları |
| [ADR ve MADR Formatı](ADR-ve-MADR-Formati) | Mimari kararların kayıt altına alınması |
| [ArchUnit ile Mimari Testler](ArchUnit-ile-Mimari-Testler) | Kuralların derleme/test aşamasında zorlanması |

## Mesajlaşma ve Gözlemlenebilirlik

| Kart | Ne öğrenirsiniz |
| --- | --- |
| [RabbitMQ Exchange, Queue ve Binding](RabbitMQ-Exchange-Queue-ve-Binding) | Topic exchange topolojisi |
| [Dead Letter Queue ve Ack Stratejisi](Dead-Letter-Queue-ve-Ack-Stratejisi) | İşlenemeyen mesajın akıbeti |
| [SLF4J ve Payara Loglama](SLF4J-ve-Payara-Loglama) | Facade/implementation ayrımı, JUL köprüsü |
| [Fluent Bit ve OpenObserve](Fluent-Bit-ve-OpenObserve) | Log dosyasından merkezi gözlemlenebilirliğe |

## Çeviri Tablosu

| Kart | Ne öğrenirsiniz |
| --- | --- |
| [C# Geliştiriciler için Kavram Sözlüğü](CSharp-Gelistiriciler-icin-Kavram-Sozlugu) | .NET ↔ Jakarta EE terim eşleştirmesi |

---

## Depodaki Modüller

| Modül | Konu | WAR adı |
| --- | --- | --- |
| `games-api` | En basit JAX-RS örneği, in-memory koleksiyon | `games-world.war` |
| `cdi-concept` | Qualifier ve kapsamlar | `cdi-concept.war` |
| `todo-app` | CDI + JPA + Event + Interceptor + async | `todo-app.war` |
| `memo-app` | Vanilla JS arayüzlü tam uygulama | `memo-app-1.0.war` |
| `game-catalog-service` | Katmanlı mimari, Flyway, MySQL | `game-catalog-service.war` |
| `inventory/*` | RabbitMQ producer/consumer, DLQ | iki ayrı WAR |
| `event-ticketing-service` | Tek veritabanında transaction yönetimi | `event-ticketing-service.war` |
| `saga-orchestration/*` | Dağıtık transaction, SAGA | dört ayrı WAR |
| `arch-guard-lab` | ADR + ArchUnit | JAR (deploy edilmez) |
