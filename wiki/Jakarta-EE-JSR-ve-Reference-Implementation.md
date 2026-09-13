# Jakarta EE, JSR ve Reference Implementation

> **Okuma:** ~4 dk · **Ön bilgi:** yok · **İlgili modül:** tümü

| | |
| --- | --- |
| **Ne işe yarar** | Kurumsal ihtiyaçların *(kalıcılık, güvenlik, transaction, web servis)* soyutlamalarını standartlaştırır |
| **Anahtar kavram** | Spesifikasyon ile implementasyonun kesin çizgilerle ayrılması |
| **.NET karşılığı** | Doğrudan bir karşılığı yok; en yakını `.NET Standard` fikri ile arayüz/implementasyon ayrımı |

## Bağlam

Kurumsal projeler birbirine benzeyen ihtiyaçlar taşır: veri saklama, güvenlik, web servisleri, transaction yönetimi, gevşek bağlılık. Bu ihtiyaçlar değişmediği için soyutlamalarının standartlaştırılması mümkündür. Java EE — bugünkü adıyla Jakarta EE — tam olarak bunu yapar.

Java ekosisteminin en ayırt edici özelliği burada ortaya çıkar: **kuralı koyan taraf ile işi yapan taraf aynı kurum değildir.**

## Üç Kavram

### JSR (Java Specification Request)

Java platformuna eklenecek bir özelliği tanımlayan belge. Her JSR belirli bir teknoloji için spesifikasyon sunar; asıl kodu içermez.

- CDI 1.0 → [JSR-299](https://jcp.org/en/jsr/detail?id=299)
- JPA 2.0 → [JSR-317](https://jcp.org/en/jsr/detail?id=317)
- Java EE 8'in kendisi → [JSR-366](https://jcp.org/en/jsr/detail?id=366)

### Reference Implementation (RI)

Soyut spesifikasyonun çalışan örnek gerçeklemesi. JAX-RS için Jersey, Java EE 8 için GlassFish böyledir. "Spesifikasyon gerçekten uygulanabilir mi" sorusunun kanıtıdır.

### Jakarta EE

Oracle'ın Java EE'yi Eclipse Foundation'a devretmesiyle ortaya çıkan isim. Tüm özellikler ve API'ler korunmuş, ancak paket adları değişmiştir:

```java
javax.persistence.Entity      // Java EE
jakarta.persistence.Entity    // Jakarta EE
```

Bu tek satırlık fark, eski örneklerle çalışırken karşılaşacağınız en sık derleme hatasının kaynağıdır.

## Üç Ana Bileşen

| Bileşen | Rol | Depodaki kart |
| --- | --- | --- |
| **CDI** | Uygulamanın sinir sistemi — yaşam döngüsü ve bağımlılıklar | [CDI Kapsamları](CDI-Kapsamlari) |
| **JPA** | Uygulamanın hafızası — nesne/tablo eşlemesi | [EntityManager ve JPQL](EntityManager-PersistenceContext-ve-JPQL) |
| **JAX-RS** | Uygulamanın kapısı — RESTful servisler | [Application ve Kaynaklar](JAX-RS-Application-ve-Kaynak-Siniflari) |

Bu üçü doğru bir şekilde bir araya geldiğinde uygulama sunucusundan bağımsız, taşınabilir bir mimari elde edilir.

## Neden Önemli?

Depodaki hiçbir modül `pom.xml` dosyasında Hibernate, EclipseLink, Weld ya da Jersey adını geçirmez. Yalnızca `jakarta.jakartaee-api` bağımlılığı vardır ve o da `provided` kapsamındadır. Motorları çalışma zamanında uygulama sunucusu sağlar. Sonuç: WAR dosyanız yalnızca sizin iş mantığınızı içerir ve sunucu değiştirdiğinizde kod değişmez.

## Tuzaklar

- **`javax.*` ile `jakarta.*` karıştırmak.** İnternetteki eski örneklerin çoğu `javax.*` kullanır; birebir kopyalarsanız derlenmez.
- **Spesifikasyon sürümü ile implementasyon sürümünü karıştırmak.** "Jakarta EE 11" bir spesifikasyon sürümüdür; "Payara Micro 7.2026.5" onu gerçekleyen ürünün sürümüdür.
- **RI'ı "en iyi implementasyon" sanmak.** RI, referans niteliğindedir; üretimde başka bir implementasyon daha uygun olabilir.

## İlgili Sayfalar

- [Application Server ve Payara Micro](Application-Server-ve-Payara-Micro)
- [WAR Paketleme ve provided Scope](WAR-Paketleme-ve-provided-Scope)
- [C# Geliştiriciler için Kavram Sözlüğü](CSharp-Gelistiriciler-icin-Kavram-Sozlugu)
