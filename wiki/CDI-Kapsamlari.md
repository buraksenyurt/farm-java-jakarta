# CDI Kapsamları (Scopes)

> **Okuma:** ~4 dk · **İlgili modül:** `cdi-concept`

| | |
| --- | --- |
| **Ne işe yarar** | Bir bean *(tekrar kullanılabilir yazılım bileşeni)* enstrümanının ne kadar süreyle yaşayacağını belirler |
| **Anahtar** | `@RequestScoped`, `@ApplicationScoped`, `@Dependent` |
| **.NET karşılığı** | `AddScoped`, `AddSingleton`, `AddTransient` |

## Kapsam Tablosu

| **Anotasyon** | **Yaşam süresi** | **.NET karşılığı** | **Bu çalışma alanındaki örnek** |
| --- | --- | --- | --- |
| `@RequestScoped` | Tek bir HTTP isteği | `AddScoped` | `CreditCardProcessor`, tüm `*Resource` sınıfları |
| `@ApplicationScoped` | Uygulamanın tamamı | `AddSingleton` | `CryptoProcessor`, tüm servis ve repository sınıfları |
| `@SessionScoped` | Kullanıcı oturumu | Doğrudan karşılığı yok *(Kontrol edilmeli?)* | Bu depoda kullanılmıyor |
| `@Dependent` | Kendisini enjekte edenin yaşam süresi ile aynı | `AddTransient`'a yakın | Varsayılan kapsam |

## Farkı Gözlemlemek

`cdi-concept` modülündeki iki ödeme bileşeni bilerek farklı kapsamlarla işaretlenmiştir ve kurucularında *(constructors)* birer `System.out.println` bulunur. İşleyişi kolayca izlemek için.

```java
@RequestScoped
public class CreditCardProcessor implements PaymentProcessor {
    public CreditCardProcessor() {
        System.out.println("CreditCardProcessor nesnesi oluşturuldu.");
    }
}

@ApplicationScoped
@Crypto
public class CryptoProcessor implements PaymentProcessor {
    public CryptoProcessor() {
        System.out.println("CryptoProcessor nesnesi üretildi");
    }
}
```

Uç noktaları *(endpoints)* arka arkaya çağırıp sunucu konsolunu izleyin:

```bash
curl http://localhost:8080/cdi-concept/api/payment/standard   # her çağrıda yeni nesne
curl http://localhost:8080/cdi-concept/api/payment/crypto     # yalnızca ilk çağrıda
```

## Merkezi Kayıt Yok

.NET'te kapsam kayıt sırasında belirtilir. *(DI servisini yapılandırırken)*

```csharp
services.AddScoped<IPaymentProcessor, CreditCardProcessor>();
```

CDI'da kapsam bileşenin kendi üzerindedir. Hiçbir merkezi dosyada bu bilgi yer almaz. Bileşeni taşıdığınızda kapsamı da onunla birlikte gider ve `kayıt dosyasını güncellemeyi unutma` diye bir sorun kalmaz.

## Proxy Meselesi

Normal kapsamlı *(normal scoped)* bean'ler — `@ApplicationScoped` ve `@RequestScoped` dahil — doğrudan değil, **proxy** üzerinden enjekte edilir. Bu, uzun ömürlü bir bean'in kısa ömürlü bir bean referansını taşıyabilmesini sağlar: proxy her çağrıda o anki doğru örneği bulur.

Bu tasarımın iki pratik sonucu vardır:

1. Bean sınıfının **argümansız bir kurucusu *(no-argument constructor)*** olmalıdır *(proxy onu üretir)*. Bu yüzden depodaki bazı sınıflarda `protected` erişim belirleyicisi ile imzalanmış argümansız kurucular görürsünüz.
2. Bean sınıfı `final` olamaz çünkü `final` metotlar proxy tarafından override edilemez.

## Tembellik *(Laziness)*

Normal kapsamlı bean'ler **tembel *(lazy)*** oluşturulur. Gerçek nesne ilk metot çağrısına kadar üretilmez. Hiç çağrılmayan bir `@ApplicationScoped` bean hiç oluşmaz ve dolayısıyla içeridiği `@PostConstruct` metodu da asla çalışmaz.

Bu, `inventory-notification-service` içindeki RabbitMQ consumer'ının neden sessizce hiç başlamadığının cevabıdır — ve çözümü için bkz. [Eager Bean Başlatma](Eager-Bean-Baslatma).

## Tuzaklar

- **`@ApplicationScoped` bean içinde paylaşılan durum tutmak *(Shared State)***. Tek örnek tüm eşzamanlı isteklerce paylaşılır, thread-safe olmayan alanlar için yarış koşulu *(race condition)* durumları oluşur. `Publisher.publishStockArrival` metodunun `synchronized` olmasının nedeni tam olarak budur.
- **Kapsam anotasyonunu hiç vermemek.** Sınıf `@Dependent` hale gelir ve `annotated` keşif modunda bean olarak sayılmaz.
- **`@RequestScoped` bir bean'i `@ApplicationScoped` bir bean içerisinde alan olarak saklamak.** Proxy sayesinde derlenir ve çalışır; ancak isteğin dışında erişilirse `ContextNotActiveException` alırsınız.

## İlgili Sayfalar

- [CDI Qualifier](CDI-Qualifier)
- [Eager Bean Başlatma](Eager-Bean-Baslatma)
- [beans.xml ve Bean Discovery](beans-xml-ve-Bean-Discovery)
