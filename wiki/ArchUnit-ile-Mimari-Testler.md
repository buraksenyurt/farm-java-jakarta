# ArchUnit ile Mimari Testler

> **Okuma:** ~5 dk · **İlgili modül:** `arch-guard-lab`

| | |
| --- | --- |
| **Ne işe yarar** | Mimari kuralları çalışabilir testlere dönüştürür |
| **Anahtar** | `@AnalyzeClasses`, `@ArchTest`, `ArchRule` |
| **.NET karşılığı** | NetArchTest, ArchUnitNET |

## Kuralı Kim Zorlar?

| **Alternatif** | **Yakalama zamanı** | **Zayıf yanı** |
| --- | --- | --- |
| Code review | Pull request sırasında | İnsana bağlı, ekip büyüdükçe kaçaklar artar |
| SonarQube | CI sırasında | Mimari kural yazmak zor, geri bildirim geç |
| Checkstyle / PMD | Derleme | Dosya/satır seviyesinde bakar, bağımlılık grafiğini göremez |
| Maven modül ayrımı | Derleme | Güçlü ama ağır, her katman için ayrı modül maliyeti |
| **ArchUnit** | **Test** | Derlenmiş kod gerekir, kural sözdizimi öğrenilmelidir |

ArchUnit'in ayırt edici yanı, **bağımlılık grafiğini *(Dependency Graph)*** görebilmesidir. `Bu paket şu pakete bağımlı olmasın` gibi bir kuralı yalnızca o ifade edebilir.

## En Basit Test

```java
@AnalyzeClasses(
        packages = "com.lectures.archguard",
        importOptions = ImportOption.DoNotIncludeTests.class)
class SmokeArchTest {

    @ArchTest
    static final ArchRule domain_classes_are_exists = classes()
            .that().resideInAPackage("com.lectures.archguard.domain..")
            .should().bePublic()
            .as("Smoke Test: is domain classes are exists in the project?");
}
```

- `@AnalyzeClasses`; hangi paketlerin analiz edileceğini söyler. `DoNotIncludeTests` test sınıflarını dışarıda bırakır
- `@ArchTest`; alanın bir mimari kural olduğunu bildirir. JUnit çalıştırıcısı bunları toplar
- `.as(...)`; kuralın adı. **ADR numarasını buraya yazmak**, başarısız testi doğrudan belgeye bağlar

Kurallar `static final` olarak işaretlenmiş alanlardır *(field)*, metot değil. Sözdizimi akıcı *(fluent)* olduğu için İngilizce'de yazılmış bir cümle gibi okunur.

## Katman Kuralı

```java
@ArchTest
static final ArchRule adr0001_layers = Architectures.layeredArchitecture()
        .consideringOnlyDependenciesInLayers()
        .layer("Api").definedBy("com.lectures.archguard.api..")
        .layer("Application").definedBy("com.lectures.archguard.application..")
        .layer("Domain").definedBy("com.lectures.archguard.domain..")
        .layer("Persistence").definedBy("com.lectures.archguard.persistence..")
        .layer("Config").definedBy("com.lectures.archguard.config..")
        .whereLayer("Api").mayOnlyBeAccessedByLayers("Config")
        .whereLayer("Application").mayOnlyBeAccessedByLayers("Api", "Config")
        .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Config")
        .as("ADR-0001: Layered architecture and dependency direction")
        .because("Dependencies should only flow inward.");
```

`Domain` katmanı için bir `whereLayer` kuralı olmadığına dikkat edin, ona herkes erişebilir. Kısıtlanan, domain'in **kimseye erişememesi**dir ve bu ayrı bir kuralla ifade edilir.

## Dört Kural Ailesi

| **Test sınıfı** | **ADR** | **Örnek kural** |
| --- | --- | --- |
| `LayeredArchitectureArchTest` | 0001 | Katman geçişleri, paket döngüsü olmaması |
| `DomainPurityArchTest` | 0002 | Domain'de `jakarta..`, `org.slf4j..` bağımlılığı yasak; `EntityManager` yalnızca persistence'ta |
| `NamingConventionArchTest` | 0003 | `@Path` sınıfları `*Resource` olmalı; `*Impl` yasak |
| `CodingRulesArchTest` | 0004 | `System.out` yasak, alan enjeksiyonu yasak, genel istisna fırlatma yasak |

## Kendi Kuralınızı Yazmak

Hazır sözdizimi yetmediğinde `ArchCondition` türetilir. ADR-0004'teki alan enjeksiyonu yasağı böyle yazılmıştır:

```java
private static final ArchCondition<JavaClass> DO_NOT_USE_INJECT_ON_FIELDS
        = new ArchCondition<>("should not use @Inject on fields") {
    @Override
    public void check(JavaClass javaClass, ConditionEvents events) {
        for (JavaField field : javaClass.getFields()) {
            if (field.isAnnotatedWith("jakarta.inject.Inject")) {
                events.add(SimpleConditionEvent.violated(field, ...));
            }
        }
    }
};
```

## Yapılandırma

`src/test/resources/archunit.properties`:

```properties
resolveMissingDependenciesFromClassPath=false
archRule.failOnEmptyShould=true
junit.displayName.replaceUnderscoresBySpaces=true
```

| **Satır** | **Etkisi** |
| --- | --- |
| `resolveMissingDependenciesFromClassPath=false` | JDK, Jakarta ve üçüncü taraf sınıflarını çözümlemez — analiz belirgin biçimde hızlanır |
| `archRule.failOnEmptyShould=true` | Hiçbir sınıfa uymayan kural başarısız sayılır; yanlış yazılmış kuralı yakalar |
| `junit.displayName.replaceUnderscoresBySpaces=true` | `adr0001_layers` yerine `adr0001 layers` görünür |

## Çalıştırma ve Çıktı Okuma

```bash
mvn -pl arch-guard-lab test
```

Başarısız bir kuralın çıktısı ihlalleri tek tek sıralar. Örneğin;

```text
Architecture Violation [Priority: MEDIUM] - Rule 'ADR-0001: Layered architecture and
dependency direction, because Dependencies should only flow inward.' was violated (4 times):
Constructor <...BookResource.<init>(...JpaBookRepository)> has parameter of type ...
Method <...BookResource.findAll()> calls method <...JpaBookRepository.findAll()> in (BookResource.java:43)
```

Ayrıntılı raporlar projenin `/target/surefire-reports/` klasörü altında yer alır.

## Kuralları Kırarak Öğrenmek

Aşağıdaki dört değişikliği tek tek yapıp testi çalıştırabiliriz.

| Değişiklik | İhlal edilen ADR |
| --- | --- |
| `BookResource` içinde `LoanService` yerine doğrudan `JpaBookRepository` kullanmak | 0001 *(iki kural birden)* |
| `domain.Book` sınıfına `@Entity` ve `@Id` eklemek | 0001 ve 0002 |
| `JpaBookRepository` adını `BookRepositoryImpl` yapmak | 0003 |
| `LoanService` içinde alan enjeksiyonu ve `System.out.println` kullanmak | 0004 |

Bu, kuralların gerçekten çalıştığını görmenin en hızlı yoludur.

## CI Entegrasyonu

```yaml
- name: Architecture tests
  run: mvn -B -pl arch-guard-lab -am test -Dtest='*ArchTest' -Dsurefire.failIfNoSpecifiedTests=false

- name: Upload reports
  if: always()
  uses: actions/upload-artifact@v4
  with:
    name: archunit-surefire-reports
    path: arch-guard-lab/target/surefire-reports/
```

`if: always()` sayesinde rapor, sadece test başarısız olduğunda da yüklenir ki asıl ihtiyaç duyduğunuz an orasıdır.

## Tuzaklar

- **Kaynak kod değil, derlenmiş sınıf gerekir.** ArchUnit bytecode analiz eder; `mvn test` önce derler.
- **Çok katı kuralla başlamak.** Var olan bir kod tabanına yüzlerce ihlalle başlamak kuralların kapatılmasıyla sonuçlanır. Kademeli ilerleyin.
- **Kural adına ADR numarası yazmamak.** Başarısız test, gerekçesini gösteremez.
- **`failOnEmptyShould` ayarını kapatmak.** Yanlış paket adı yazılmış bir kural sessizce geçer.
- **Yalnızca yerelde çalıştırmak.** CI hattına eklenmemiş kural, er ya da geç ihlal edilir.

## İlgili Sayfalar

- [ADR ve MADR Formatı](ADR-ve-MADR-Formati)
- [Katmanlı Mimari ve Repository Deseni](Katmanli-Mimari-ve-Repository-Deseni)
- [Maven Multi-Module Yapısı](Maven-Multi-Module-Yapisi)
