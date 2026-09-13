# Maven Multi-Module Yapısı

> **Okuma:** ~4 dk · **İlgili dosya:** kök `pom.xml`

| | |
| --- | --- |
| **Ne işe yarar** | Tüm örnekleri tek komutla derlemek, ortak ayarları tek yerden yönetmek |
| **Anahtar** | Aggregator + parent POM |
| **.NET karşılığı** | `.sln` dosyası + `Directory.Build.props` |

## Yapı

```text
farm-java-jakarta/                <- kök: aggregator + parent pom.xml
├── cdi-concept/
├── games-api/
├── todo-app/
├── memo-app/
├── event-ticketing-service/
├── game-catalog-service/
├── arch-guard-lab/
├── inventory/                    <- grup: RabbitMQ yayınla/dinle
│   ├── inventory-events-service/
│   └── inventory-notification-service/
└── saga-orchestration/           <- grup: dağıtık transaction
    ├── saga-orch-event-service/
    ├── saga-orch-wallet-service/
    ├── saga-orch-booking-audit-service/
    └── saga-orchestrator-service/
```

Kök POM iki rolü birden üstlenir:

- **Aggregator:** `<modules>` listesindeki projeleri sırayla derler.
- **Parent:** Alt modüllere ortak ayarları miras bırakır.

## Ne Miras Alınır, Ne Alınmaz?

Bu ayrım bilinçlidir ve öğretim amacı taşır.

| Parent'ta (ortak) | Modülde (bilerek farklı) |
| --- | --- |
| `maven.compiler.release=21` | `jakarta.jakartaee-api` sürümü (`11.0.0-M1` ↔ `11.0.0`) |
| `UTF-8` kaynak kodlaması | Full API mi, Web Profile mi |
| `maven-compiler-plugin`, `maven-war-plugin` sürümleri | WAR'a gömülen JDBC sürücüleri |
| `groupId` (`com.lectures`) | `finalName`, yani WAR adı |

Öğrenmek istediğimiz noktaları merkezîleştirmiyoruz — fark görünür kalmalı.

## Alt Modül POM'u

```xml
<parent>
    <groupId>com.lectures</groupId>
    <artifactId>farm-java-jakarta</artifactId>
    <version>1.0-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
</parent>

<artifactId>todo-app</artifactId>
<packaging>war</packaging>
```

`<relativePath>` iki seviye derindeki modüllerde `../../pom.xml` olur — `inventory/inventory-events-service` buna örnektir.

Çocuk POM'da `<build><plugins>` bölümü yoktur; WAR paketleme `war` packaging'in varsayılan yaşam döngüsüyle zaten çalışır.

## Komutlar

```bash
# Her şeyi derle
mvn clean package

# Tek modül (kökten)
mvn -pl todo-app clean package

# Tek modül + bağımlı olduğu modüller
mvn -pl arch-guard-lab -am test

# Bir grubu birlikte
mvn -f inventory/pom.xml clean package
mvn -f saga-orchestration/pom.xml clean package
```

| Bayrak | Anlamı |
| --- | --- |
| `-pl` | *project list* — yalnızca bu modül(ler) |
| `-am` | *also make* — seçilen modülün ihtiyaç duyduğu modülleri de derle |
| `-f` | Başka bir POM dosyasını kök kabul et |
| `-B` | Batch mode; CI çıktısını sadeleştirir |

## Tuzaklar

- **`-pl` ile modül adı yerine dizin yolu vermek.** İki seviye derin modüllerde yol tam verilmelidir: `-pl inventory/inventory-events-service`.
- **Parent sürümünü güncellemeyi unutmak.** Kök sürüm değişirse tüm çocuk POM'lardaki `<parent><version>` da değişmelidir; aksi halde Maven parent'ı bulamaz.
- **Kökten `mvn clean package` derken bir modülün patlaması** tüm derlemeyi durdurur. Hangi modülde kaldığını görmek için `-fae` *(fail at end)* kullanabilirsiniz.

## İlgili Sayfalar

- [WAR Paketleme ve provided Scope](WAR-Paketleme-ve-provided-Scope)
- [ArchUnit ile Mimari Testler](ArchUnit-ile-Mimari-Testler)
