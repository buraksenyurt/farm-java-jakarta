# Veri Kaynağı Tanımlama

> **Okuma:** ~5 dk · **İlgili modül:** `todo-app`, `event-ticketing-service`, `game-catalog-service`

| | |
| --- | --- |
| **Ne işe yarar** | JPA'nın hangi veritabanına, hangi bağlantı havuzuyla gideceğini belirler |
| **Anahtar** | `persistence.xml`, JNDI, `glassfish-resources.xml`, `@DataSourceDefinition` |
| **.NET karşılığı** | `appsettings.json` connection string + `AddDbContext` |

## Zincir

```text
persistence.xml  ──jta-data-source──▶  JNDI adı  ──▶  bağlantı havuzu  ──▶  veritabanı
```

Uygulama kodu hiçbir zaman bir bağlantı dizesi görmez; yalnızca bir **JNDI adı** bilir. Havuzu kimin, nasıl tanımladığı ayrı bir meseledir ve bu depoda iki farklı yöntemle yapılmıştır.

## 1. Adım: persistence.xml

```xml
<persistence-unit name="eventTicketingPU" transaction-type="JTA">
    <jta-data-source>java:app/jdbc/eventticketing</jta-data-source>
    <properties>
        <property name="jakarta.persistence.schema-generation.database.action" value="none"/>
    </properties>
</persistence-unit>
```

| Öğe | Anlamı |
| --- | --- |
| `name` | `@PersistenceContext(unitName = ...)` ile eşleşmesi gereken ad |
| `transaction-type="JTA"` | Transaction'ları container yönetir *(alternatifi `RESOURCE_LOCAL`)* |
| `jta-data-source` | Aranacak JNDI adı |
| `schema-generation` | Bkz. [Schema Generation ve Migration](Schema-Generation-ve-Migration) |

Dosya `src/main/resources/META-INF/` altında bulunmalıdır; WAR içinde `WEB-INF/classes/META-INF/` konumuna düşer.

## 2. Adım — Yöntem A: glassfish-resources.xml

Havuzu sunucuya XML ile tanımlama. `event-ticketing-service` bunu kullanır:

```xml
<resources>
    <jdbc-connection-pool name="EventTicketingPool"
                          res-type="javax.sql.DataSource"
                          datasource-classname="org.postgresql.ds.PGSimpleDataSource">
        <property name="ServerName" value="localhost"/>
        <property name="PortNumber" value="5432"/>
        <property name="DatabaseName" value="eventticketing"/>
        <property name="User" value="johndoe"/>
        <property name="Password" value="somew0rds"/>
    </jdbc-connection-pool>

    <jdbc-resource pool-name="EventTicketingPool"
                   jndi-name="java:app/jdbc/eventticketing"/>
</resources>
```

İki parça vardır: **havuz** *(nereye, hangi kimlikle bağlanılacak)* ve **kaynak** *(havuza hangi JNDI adıyla erişileceği)*. Dosya `src/main/webapp/WEB-INF/` altındadır ve GlassFish soyundan gelen sunuculara *(Payara dahil)* özgüdür.

## 2. Adım — Yöntem B: @DataSourceDefinition

Aynı işi Java kodunda, anotasyonla yapmak. `todo-app` ve `memo-app` bunu kullanır:

```java
@DataSourceDefinition(
        name = "java:app/jdbc/todoDB",
        className = "org.postgresql.ds.PGSimpleDataSource",
        serverName = "localhost",
        portNumber = 5432,
        databaseName = "postgres",
        user = "johndoe",
        password = "somew0rds"
)
@ApplicationScoped
public class DataSourceConfig {
}
```

Sınıfın gövdesi boştur; tek görevi anotasyonu taşımaktır.

## Hangisi Ne Zaman?

| | `glassfish-resources.xml` | `@DataSourceDefinition` |
| --- | --- | --- |
| Taşınabilirlik | Sunucuya özgü | Standart Jakarta EE |
| Değiştirmek | Yeniden derleme gerekmez | Yeniden derleme gerekir |
| Havuz ayarları | Ayrıntılı kontrol | Sınırlı |
| Uygun olduğu yer | Üretim, ortam başına farklı yapılandırma | Örnek, demo, tek ortam |

Her iki yöntemde de parolalar açık metin olarak durur. Bu bir öğrenme deposu için kabul edilebilir; gerçek bir sistemde değerler Vault benzeri bir dış kaynaktan gelmelidir. `Publisher` sınıfındaki `System.getenv()` kullanımı bu yönde atılmış küçük bir adımdır.

## Bu Depodaki Veri Kaynakları

| Modül | Veritabanı | JNDI adı | Yöntem |
| --- | --- | --- | --- |
| `todo-app` | PostgreSQL | `java:app/jdbc/todoDB` | Anotasyon |
| `memo-app` | PostgreSQL | `java:app/jdbc/memoDB` | Anotasyon |
| `game-catalog-service` | MySQL | `java:app/jdbc/gamecatalog` | XML |
| `event-ticketing-service` | PostgreSQL | `java:app/jdbc/eventticketing` | XML |
| `saga-orch-event-service` | PostgreSQL | `java:app/jdbc/eventsaga` | XML |
| `saga-orch-wallet-service` | MySQL | `java:app/jdbc/walletsaga` | XML |
| `saga-orch-booking-audit-service` | H2 | `java:app/jdbc/auditdb` | XML |

Konteynerler `docker-compose.yml` dosyasında tanımlıdır: `java-town-postgres`, `java-town-mysql`, `java-town-mysql-wallet`.

## Tuzaklar

- **JNDI adı uyuşmazlığı.** `persistence.xml` içindeki ad ile kaynak tanımındaki ad birebir aynı olmalıdır; tek karakterlik fark deploy anında "kaynak bulunamadı" hatası üretir.
- **`persistence.xml` dosyasının yanlış klasörde olması.** En sık yapılan hata, dosyayı `webapp/WEB-INF/` altına koymaktır.
- **JDBC sürücüsünü WAR'a koymayı unutmak.** Bkz. [WAR Paketleme ve provided Scope](WAR-Paketleme-ve-provided-Scope).
- **`java:app/` yerine `java:comp/` ya da `jdbc/` öneki kullanmak.** Kapsam önekleri farklı görünürlük anlamına gelir.
- **Veritabanı adını `postgres` bırakmak.** `todo-app` örneğinde olduğu gibi varsayılan veritabanına yazmak, tablolarınızın beklemediğiniz bir yerde oluşmasına yol açar.

## İlgili Sayfalar

- [EntityManager, PersistenceContext ve JPQL](EntityManager-PersistenceContext-ve-JPQL)
- [Schema Generation ve Migration](Schema-Generation-ve-Migration)
- [WAR Paketleme ve provided Scope](WAR-Paketleme-ve-provided-Scope)
