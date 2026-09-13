# WAR Paketleme ve provided Scope

> **Okuma:** ~4 dk · **İlgili modül:** `todo-app`, `event-ticketing-service`

| | |
| --- | --- |
| **Ne işe yarar** | Hangi bağımlılığın WAR'a gireceğini, hangisinin sunucudan geleceğini belirler |
| **Anahtar** | `<scope>provided</scope>` |
| **.NET karşılığı** | Framework-provided assembly ile NuGet paketinin `publish` çıktısındaki farkı |

## WAR Nedir?

**W**eb **A**pplication **AR**chive. Web uygulamasının paketlenmiş halidir; `mvn clean package` sonrası modülün `target/` klasöründe oluşur. Bir uygulama sunucusuna deploy edilerek çalıştırılır.

## İki Farklı Bağımlılık

`todo-app/pom.xml` iki bağımlılık tanımlar ve aralarındaki fark bilinçlidir:

```xml
<dependency>
    <groupId>jakarta.platform</groupId>
    <artifactId>jakarta.jakartaee-api</artifactId>
    <version>${jakartaee}</version>
    <scope>provided</scope>          <!-- WAR'a GİRMEZ -->
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>        <!-- scope yok → WAR'a GİRER -->
</dependency>
```

### `provided` — derle, ama paketleme

Maven'a "bu arayüzler derleme için gerekli, ancak çıktıya ekleme" demektir. Çünkü `@Entity`, `@Inject`, `@Path` gibi anotasyonların gerçeklemesini çalışma zamanında uygulama sunucusu sağlar: Weld, EclipseLink, Jersey...

Sonuç: WAR dosyası yalnızca sizin iş mantığınızı taşır ve çok küçük kalır.

### Varsayılan kapsam — paketle

JDBC sürücüsü uygulamaya özgüdür. Sunucuda bulunmayabilir. `scope` verilmediğinde varsayılan `compile` geçerlidir ve sürücü `WEB-INF/lib/` altına kopyalanır. Uygulama, sunucuda sürücü kurulu olmasa bile çalışır.

## Doğrulama

Derleme sonrası WAR'ın içine bakabilirsiniz:

```bash
unzip -l todo-app/target/todo-app.war | grep WEB-INF/lib
```

Beklenen çıktı: yalnızca `postgresql-42.7.3.jar` ve onun geçişli bağımlılığı `checker-qual`. Jakarta API'lerinden eser yoktur.

`game-catalog-service` için aynı komut Flyway, Jackson ve MySQL sürücüsünü listeler — çünkü onlar da uygulamaya özgüdür.

## WAR'ın İçi

```text
todo-app.war
├── index.html                       ← src/main/webapp
├── WEB-INF/
│   ├── beans.xml                    ← CDI keşif modu
│   ├── web.xml
│   ├── glassfish-resources.xml      ← (bazı modüllerde) JNDI kaynak tanımı
│   ├── classes/
│   │   ├── com/lectures/...         ← derlenmiş sınıflar
│   │   └── META-INF/persistence.xml ← JPA yapılandırması
│   └── lib/                         ← provided OLMAYAN bağımlılıklar
```

`src/main/resources/META-INF/persistence.xml` dosyasının WAR içinde `WEB-INF/classes/META-INF/` altına düştüğüne dikkat edin. Dosyayı yanlış klasöre koyduğunuzda JPA persistence unit'i bulamaz ve hata mesajı ilk bakışta bu yolu işaret etmez.

## Tuzaklar

- **`provided` unutulursa** Jakarta API'leri WAR'a girer ve sunucunun kendi sınıflarıyla çakışır. Ortaya çıkan `ClassCastException` ya da `LinkageError` mesajları sebebi anlaşılmaz hatalardır.
- **Sürücüyü `provided` yapmak**, sunucuda o sürücü tanımlı değilse `ClassNotFoundException` üretir.
- **`finalName` ile artifactId karışıklığı.** WAR adı context path'i, dolayısıyla tüm URL'lerinizi belirler.

## İlgili Sayfalar

- [Maven Multi-Module Yapısı](Maven-Multi-Module-Yapisi)
- [Veri Kaynağı Tanımlama](Veri-Kaynagi-Tanimlama)
- [Application Server ve Payara Micro](Application-Server-ve-Payara-Micro)
