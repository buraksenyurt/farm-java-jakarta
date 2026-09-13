# C# Geliştiriciler için Kavram Sözlüğü

> **Okuma:** ~5 dk · **Kapsam:** tüm depo

Bu sayfa bir eşleştirme tablosudur. Karşılıklar **kavramsaldır**; birebir aynı şey oldukları anlamına gelmez. Farkların önemli olduğu yerlere not düşülmüştür.

## Platform

| .NET | Jakarta EE | Not |
| --- | --- | --- |
| .NET Runtime | JVM | |
| ASP.NET Core | Jakarta EE Web Profile | |
| Kestrel | Payara Micro | Payara Micro tam bir uygulama sunucusudur, yalnız web sunucusu değil |
| `.csproj` | `pom.xml` | |
| `.sln` | Aggregator `pom.xml` | |
| `Directory.Build.props` | Parent POM | |
| NuGet | Maven Central | |
| `dotnet publish` | `mvn package` → `.war` | |

## Bağımlılık Enjeksiyonu

| .NET | Jakarta EE | Not |
| --- | --- | --- |
| `Microsoft.Extensions.DependencyInjection` | CDI | CDI ayrıca olay ve interceptor da sunar |
| `services.AddScoped` | `@RequestScoped` | |
| `services.AddSingleton` | `@ApplicationScoped` | |
| `services.AddTransient` | `@Dependent` | Tam eşdeğer değil |
| Kurucu enjeksiyonu | `@Inject` | Alan, kurucu ve metot üzerinde çalışır |
| Keyed Services (.NET 8+) | `@Qualifier` | Java'da anahtar bir tiptir, string değil |
| `Program.cs` kayıtları | Merkezi kayıt **yok** | Bileşen kendi kapsamını taşır |
| `IHostedService` | `@Observes @Initialized(ApplicationScoped.class)` | |

## Web ve REST

| .NET | Jakarta EE |
| --- | --- |
| `ControllerBase` | Kaynak sınıfı *(`*Resource`)* |
| `[Route("api/[controller]")]` | `@Path` + `@ApplicationPath` |
| `[HttpGet]`, `[HttpPost]` | `@GET`, `@POST` |
| `[FromRoute]` | `@PathParam` |
| `[FromQuery]` | `@QueryParam` |
| `[FromBody]` | *(anotasyonsuz parametre)* |
| `[FromHeader]` | `@HeaderParam` |
| `IActionResult` | `Response` |
| `CreatedAtAction` | `Response.created(uri)` |
| Exception filter / middleware | `ExceptionMapper<T>` |
| `System.Text.Json` | JSON-B |
| `async Task<IActionResult>` | `@Suspended AsyncResponse` |
| `IHttpClientFactory` | JAX-RS `Client` |

## Veri Erişimi

| .NET | Jakarta EE | Not |
| --- | --- | --- |
| Entity Framework Core | JPA | JPA bir spesifikasyon; EF bir ürün |
| `DbContext` | `EntityManager` | |
| `DbSet<T>` | Tip parametresi ile `find`, `createQuery` | Tip başına özellik yok |
| LINQ | JPQL / Criteria API | JPQL string tabanlı |
| Change tracking | Dirty checking | |
| `SaveChanges()` | Transaction commit | Açık bir `SaveChanges` çağrısı yok |
| `[Table]`, `[Column]`, `[Key]` | `@Table`, `@Column`, `@Id` | |
| EF migration'ları | Flyway / Liquibase | Java'da betikler elle yazılır |
| `EnsureCreated()` | `schema-generation` | |
| `TransactionScope` | `@Transactional` | |
| Connection string | JNDI adı + veri kaynağı tanımı | |

## Kesişen İlgiler

| .NET | Jakarta EE |
| --- | --- |
| Action filter | CDI Interceptor |
| `DispatchProxy`, Castle DynamicProxy | Weld proxy |
| MediatR notification | CDI Event + `@Observes` |
| `ILogger<T>` | SLF4J `Logger` |
| Serilog, NLog | Logback, JUL — SLF4J binding'i ile |
| `DataAnnotations` | Bean Validation |
| `ModelState.IsValid` | `@Valid` |

## Zihinsel Modeldeki Farklar

Tablolardan daha önemli olan üç yapısal fark:

**1. Kayıt merkezi yoktur.** .NET'te "bu servis kayıtlı mı" sorusu `Program.cs` dosyasında cevaplanır. CDI'da cevap sınıfın kendisindedir. Bileşeni taşıdığınızda kapsamı da gelir; ama eksik anotasyonu görebileceğiniz merkezi bir liste de yoktur.

**2. Spesifikasyon ile implementasyon ayrıdır.** EF Core hem arayüz hem implementasyondur. JPA yalnızca arayüzdür; Hibernate ya da EclipseLink implementasyondur. `pom.xml` dosyanızda implementasyonun adı hiç geçmeyebilir — ve bu, sunucu değiştirdiğinizde kodun değişmemesi anlamına gelir.

**3. Anotasyon = davranış.** C#'ta attribute'lar çoğunlukla meta veridir; bir şeyin onları okuması gerekir. Jakarta EE'de anotasyon doğrudan container davranışını tetikler. `@Transactional` yazdığınızda transaction gerçekten başlar; başka hiçbir kayıt gerekmez.

## Yanıltıcı Benzerlikler

- **`@Inject` ≠ kurucu enjeksiyonu zorunluluğu.** Java'da alan enjeksiyonu da mümkündür ve yaygındır; ancak `arch-guard-lab` ADR-0004 bunu test edilebilirlik gerekçesiyle yasaklar.
- **JPQL ≠ SQL.** `SELECT g FROM Game g` sorgusundaki `Game`, tablo adı değil entity adıdır.
- **`merge` ≠ `Update`.** `merge`, verdiğiniz nesneyi değil, yeni bir yönetilen kopyayı döndürür.
- **`@ApplicationScoped` ≠ `static`.** Proxy üzerinden erişilir ve thread güvenliği size aittir.

## İlgili Sayfalar

- [Jakarta EE, JSR ve Reference Implementation](Jakarta-EE-JSR-ve-Reference-Implementation)
- [CDI Kapsamları](CDI-Kapsamlari)
- [EntityManager, PersistenceContext ve JPQL](EntityManager-PersistenceContext-ve-JPQL)
