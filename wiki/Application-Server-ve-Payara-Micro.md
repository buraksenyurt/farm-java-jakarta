# Application Server ve Payara Micro

> **Okuma:** ~4 dk · **İlgili modül:** tümü

| | |
| --- | --- |
| **Ne işe yarar** | Jakarta EE spesifikasyonlarının çalışma zamanı gerçeklemesini sağlar |
| **Bu depoda** | Payara Micro 7.2026.5, tek bir `.jar` olarak |
| **.NET karşılığı** | Kestrel + ASP.NET Core barındırma altyapısının birleşimi |

## Uygulama Sunucusu Ne Yapar?

Loglama, hata yönetimi *(Error Handling)*, REST endpoints, JSON dönüşümü, CDI, JPA, JMS, EJB gibi kurumsal çözümlerde tekrar eden ihtiyaçların gerçeklemesini sağlar. Geliştirici yalnızca iş mantığına odaklanır ve onu yazar.

Yaygın sunucular:

- [IBM Open Liberty](https://openliberty.io/)
- [Payara Server / GlassFish](https://www.payara.fish/)
- [JBoss WildFly](https://www.wildfly.org/)

Her biri, Jakarta EE spesifikasyonlarının **somut implementasyonudur**. İç mekanizmalarında CDI motoru *(Weld)*, JPA sağlayıcısı *(EclipseLink veya Hibernate)* ve JAX-RS gerçeklemesi *(Jersey veya RESTEasy)* barındırır.

## Neden Payara Micro?

Payara Server'ın hafif sıkletli kardeşi. Tek bir JAR dosyasıdır, kurulum gerektirmez. WAR dosyasını komut satırından kolayca deploy etmek mümkündür. Doğal olarak mikro servis senaryoları için uygundur.

```bash
# Örnek kullanım
java -Djava.net.preferIPv4Stack=true -jar payara-micro-7.2026.5.jar \
  --deploy wars/todo-app.war
```

## Sık Kullanılan Parametreler

| **Parametre** | **Ne yapar?** | **Ne zaman gerekir?** |
| --- | --- | --- |
| `--deploy <war>` | WAR dosyasını yayına alır | Her zaman |
| `--port <n>` | HTTP portunu belirler | Aynı makinede birden fazla servis çalıştırırken |
| `--nocluster` | Hazelcast küme keşfini kapatır | Birden fazla örneğin birbirini bulması istendiğinde gerekir |
| `--logproperties <dosya>` | JUL log ayarlarını dışarıdan verir | Logları dosyaya JSON olarak yazdırmak için |

Birden fazla servisi aynı anda çalıştırma örneği:

```bash
# 8080
java -Djava.net.preferIPv4Stack=true -jar payara-micro-7.2026.5.jar \
  --port 8080 --nocluster --deploy wars/inventory-events-service-1.0-SNAPSHOT.war

# 8081
java -Djava.net.preferIPv4Stack=true -jar payara-micro-7.2026.5.jar \
  --port 8081 --nocluster --deploy wars/inventory-notification-service-1.0.war
```

## IPv4 Zorlaması Neden Gerekli?

```bash
-Djava.net.preferIPv4Stack=true
```

JVM, Linux ortamlarında ısrarla IPv6 kullanmaya çalışabilir. Ortam yalnızca IPv4'e göre yapılandırılmışsa uygulama portu dinleyemez ve `BindException` fırlatır; PostgreSQL gibi servislere bağlanırken de zaman aşımı hataları görülür. Bu parametre JVM'i IPv4'e zorlayarak sorunu kökten keser.

Bu depodaki tüm başlatma komutlarında bu parametrenin bulunmasının nedeni budur.

## URL Nasıl Oluşur?

```text
http://localhost:8080/todo-app/api/v1/todo/list
```

Burada `todo-app` kısmı context path'i ifade eder. `api/v1/todo/list` kısmı ise **JAX-RS** kaynak sınıfındaki `@ApplicationPath` ve `@Path` anotasyonlarıyla belirlenir.

**Context path**, WAR dosyasının adıdır. Bu yüzden `pom.xml` içindeki `<finalName>` değeri doğrudan URL'inizi belirler. `finalName` verilmemişse ad `artifactId-version` biçiminde oluşur, `inventory-notification-service-1.0` gibi.

## Tuzaklar

- **Aynı portta iki uygulama.** `BindException: Address already in use`. `--port` kullanın.
- **`--nocluster` unutulduğunda** aynı makinedeki örnekler birbirini küme üyesi sanabilir; günlüklerde beklenmedik Hazelcast mesajları görürsünüz.
- **Java sürümü uyumu.** Bu depo Java 21 ile derlenir; makinede daha yeni bir JDK kurulu olsa bile `Payara Micro 7.2026.5` ile uyumlu sürüm kullanılmalıdır.
- **Uzun WAR adları.** `inventory-events-service-1.0-SNAPSHOT.war` gibi bir ad, curl komutlarınızda da aynen yer alır. `finalName` vererek kısaltmak okunabilirliği artırır.

## İlgili Sayfalar

- [WAR Paketleme ve provided Scope](WAR-Paketleme-ve-provided-Scope)
- [JAX-RS Application ve Kaynak Sınıfları](JAX-RS-Application-ve-Kaynak-Siniflari)
- [SLF4J ve Payara Loglama](SLF4J-ve-Payara-Loglama)
