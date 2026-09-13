# beans.xml ve Bean Discovery

> **Okuma:** ~3 dk · **İlgili dosya:** her modülde `src/main/webapp/WEB-INF/beans.xml`

| | |
| --- | --- |
| **Ne işe yarar** | CDI motorunun hangi sınıfları bean olarak tarayacağını belirler |
| **Anahtar** | `bean-discovery-mode="annotated"` |
| **.NET karşılığı** | `Program.cs` içindeki servis kayıtları — ama tersine çevrilmiş biçimde |

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

İçi boştur ve öyle olması gerekir. Dosyanın varlığı ve tek bir özniteliği, tüm bağımlılık yönetiminin nasıl kurulacağını belirler.

## Perde Arkası: Weld

Payara, WildFly, JBoss EAP, GlassFish gibi sunucular içlerinde bir CDI motoru barındırır. Literatürde **Weld** olarak geçer. Bu motor deploy anında arşivi tarar, bean adaylarını bulur, bağımlılık grafiğini kurar ve doğrular.

`bean-discovery-mode` değerini okuyan işte bu motordur.

## Üç Mod

| Mod | Ne taranır | Sonuç |
| --- | --- | --- |
| `all` | Arşivdeki tüm sınıflar | Gereksiz tarama, yavaş başlangıç |
| `annotated` | Yalnızca bean tanımlayıcı anotasyon taşıyanlar | Bu depoda kullanılan mod |
| `none` | Hiçbiri | CDI fiilen kapalı |

`annotated` modunda bir sınıfın bean olabilmesi için üzerinde bir **bean defining annotation** bulunmalıdır: `@ApplicationScoped`, `@RequestScoped`, `@SessionScoped`, `@Dependent`, `@Interceptor` ve benzerleri.

## .NET Tarafından Bakınca

.NET'te kayıt merkezîdir; her servisi `Program.cs` içinde tek tek bildirirsiniz:

```csharp
services.AddScoped<ITodoService, TodoService>();
```

CDI'da kayıt **dağıtıktır**. Her sınıf kendi kapsamını kendi üzerinde taşır ve motor onu bulur. Merkezi bir liste yoktur.

Bunun bedeli şudur: .NET'te "bu servis kayıtlı mı" sorusunun cevabı tek bir dosyadadır. CDI'da cevap sınıfın kendisindedir — ve anotasyonu unuttuysanız hiçbir yerde eksik bir satır göremezsiniz.

## Tuzaklar

- **Anotasyonu unutulan sınıf** hiç taranmaz, aday listesine girmez ve `@Inject` noktasında `UnsatisfiedResolutionException` alırsınız. Hata mesajı "sınıfını bulamadım" demez, "tatmin edilemeyen bağımlılık" der.
- **`beans.xml` dosyasının hiç bulunmaması.** CDI 4.x'te `annotated` mod zaten varsayılandır; ancak dosyayı açıkça koymak niyeti görünür kılar.
- **Yanlış klasör.** Dosya WAR içinde `WEB-INF/` altında olmalıdır; `META-INF/` altına konması JAR arşivleri içindir.
- **Şema sürümü uyumsuzluğu.** `beans_4_0.xsd` Jakarta EE 10+ içindir; eski `beans_1_1.xsd` ile karıştırılmamalıdır.

## İlgili Sayfalar

- [CDI Kapsamları](CDI-Kapsamlari)
- [Eager Bean Başlatma](Eager-Bean-Baslatma)
- [CDI Interceptor ile AOP](CDI-Interceptor-ile-AOP)
