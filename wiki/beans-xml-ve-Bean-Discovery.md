# beans.xml ve Bean Discovery

> **Okuma:** ~3 dk · **İlgili dosya:** her modülde `src/main/webapp/WEB-INF/beans.xml`

| | |
| --- | --- |
| **Ne işe yarar** | CDI motorunun hangi sınıfları bean olarak tarayacağını belirler |
| **Anahtar** | `bean-discovery-mode="annotated"` |
| **.NET karşılığı** | `Program.cs` içindeki servis kayıtları — ama tersine çevrilmiş biçimde *(DI Register işlemlerini hatırlayalım)* |

## Dosya

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
                           https://jakarta.ee/xml/ns/jakartaee/beans_4_0.xsd"
       bean-discovery-mode="annotated">
</beans>
```

Buradaki en önemli nitelik `bean-discovery-mode` niteliğidir. Nelerin taranacağını ve hangi sınıfların CDI tarafından yönetileceğini belirler.

## Perde Arkası: Weld

Payara, WildFly, JBoss EAP, GlassFish gibi sunucular içlerinde bir **CDI** motoru barındırır. Literatürde **Weld** olarak da geçer. Bu motor paket dağıtımı *(deployment)* anında arşivi tarar, **bean** adaylarını bulur, bağımlılık grafiğini *(dependency graph)* kurar ve doğrular.

`bean-discovery-mode` değerini okuyan işte bu motordur.

## Üç Mod

| **Mod** | **Ne taranır** | **Sonuç** |
| --- | --- | --- |
| `all` | Arşivdeki tüm sınıflar | Gereksiz tarama, yavaş başlangıç |
| `annotated` | Yalnızca bean tanımlayıcı anotasyonu taşıyanlar | Bu depodaki örneklerde bu modu kullanıyoruz |
| `none` | Hiçbiri | CDI fiilen kapalı *(Neden tercih ederiz sorusuna cevap bulunmalı?)* |

`annotated` modunda bir sınıfın bean olabilmesi için üzerinde bir **bean defining annotation** bulunmalıdır. Örneğin: `@ApplicationScoped`, `@RequestScoped`, `@SessionScoped`, `@Dependent`, `@Interceptor` ve benzerleri.

## .NET Tarafından Bakınca

.NET'te DI Register işlemleri bu şekilde yapılır. Her servisi `Program.cs` içinde tek tek bildiririz ya da `IServiceCollection` arayüzünü genişletip *(extension method)* merkezi bir kayıt noktası oluşturabiliriz.

```csharp
services.AddScoped<ITodoService, TodoService>();
```

CDI'da kayıt sistemi dağıtık yapıdadır. Her sınıf kendi kapsamını kendi üzerinde taşır ve motor onu bulur. Merkezi bir liste yoktur. Bunun Java dünyası açısından bakılınca şöyle bir dezavantajı olabilir; .NET'te "bu servis kayıtlı mı?" sorusunun cevabı tek bir dosyadadır. CDI'da ise cevap sınıfın kendisindedir ve anotasyonu unuttuysanız hiçbir yerde eksik bir satır göremezsiniz.

## Tuzaklar

- **Anotasyonu unutulan sınıf** hiç taranmaz, aday listesine girmez ve `@Inject` ile bileşen bildirimi yapılan yerlerde `UnsatisfiedResolutionException` alırsınız. Hata mesajı "sınıfını bulamadım" demez, "tatmin edilemeyen bağımlılık" der. *(Bunu gerçeken deneyip ispat etmeye çalışmalıyız!)*
- **`beans.xml` dosyasının hiç bulunmaması.** CDI 4.x'te `annotated` mod zaten varsayılandır ancak dosyayı açıkça koymak niyeti görünür kılar. *(Bunda ne sakınca olabilir?)*
- **Yanlış klasör.** Dosya WAR içinde `WEB-INF/` altında olmalıdır. `META-INF/` altına konması JAR arşivleri içindir.
- **Şema sürüm uyumsuzluğu.** `beans_4_0.xsd` Jakarta EE 10+ içindir ve eski `beans_1_1.xsd` ile karıştırılmamalıdır.

## İlgili Sayfalar

- [CDI Kapsamları](CDI-Kapsamlari)
- [Eager Bean Başlatma](Eager-Bean-Baslatma)
- [CDI Interceptor ile AOP](CDI-Interceptor-ile-AOP)
