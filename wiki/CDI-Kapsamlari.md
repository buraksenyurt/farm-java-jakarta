# CDI Kapsamları (Scopes)

> **Okuma:** ~4 dk · **İlgili modül:** `cdi-concept`

| | |
| --- | --- |
| **Ne işe yarar** | Bir bean'in kaç kez ve ne kadar süreyle yaşayacağını belirler |
| **Anahtar** | `@RequestScoped`, `@ApplicationScoped`, `@Dependent` |
| **.NET karşılığı** | `AddScoped`, `AddSingleton`, `AddTransient` |

## Kapsam Tablosu

| Anotasyon | Yaşam süresi | .NET karşılığı | Bu depoda örnek |
| --- | --- | --- | --- |
| `@RequestScoped` | Tek bir HTTP isteği | `AddScoped` | `CreditCardProcessor`, tüm `*Resource` sınıfları |
| `@ApplicationScoped` | Uygulamanın tamamı | `AddSingleton` | `CryptoProcessor`, tüm servis ve repository sınıfları |
| `@SessionScoped` | Kullanıcı oturumu | Doğrudan karşılığı yok | Bu depoda kullanılmıyor |
| `@Dependent` | Kendisini enjekte edenle aynı | `AddTransient`'a yakın | Varsayılan kapsam |

## Farkı Gözlemlemek

`cdi-concept` modülündeki iki ödeme bileşeni bilerek farklı kapsamlarla işaretlenmiştir ve kurucularında birer `System.out.println` bulunur:

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

Uç noktaları arka arkaya çağırıp sunucu konsolunu izleyin:

```bash
curl http://localhost:8080/cdi-concept/api/payment/standard   # her çağrıda yeni nesne
curl http://localhost:8080/cdi-concept/api/payment/crypto     # yalnızca ilk çağrıda
```

## Merkezi Kayıt Yok

.NET'te kapsam kayıt sırasında belirtilir:

```csharp
services.AddScoped<IPaymentProcessor, CreditCardProcessor>();
```

CDI'da kapsam bileşenin kendi üzerindedir. Hiçbir merkezi dosyada bu bilgi yer almaz. Bileşeni taşıdığınızda kapsamı da onunla birlikte gider; kayıt dosyasını güncellemeyi unutma diye bir sorun kalmaz.

## Proxy Meselesi

Normal kapsamlı *(normal scoped)* bean'ler — `@ApplicationScoped` ve `@RequestScoped` dahil — doğrudan değil, **proxy** üzerinden enjekte edilir. Bu, uzun ömürlü bir bean'in kısa ömürlü bir bean'e referans tutabilmesini sağlar: proxy her çağrıda o anki doğru örneği bulur.

Bu tasarımın iki pratik sonucu vardır:

1. Bean sınıfının **argümansız bir kurucusu** olmalıdır *(proxy onu üretir)*. Bu yüzden depodaki bazı sınıflarda `protected` argümansız kurucular görürsünüz.
2. Bean sınıfı `final` olamaz, `final` metotları proxy'lenemez.

## Tembellik

Normal kapsamlı bean'ler **tembel** oluşturulur. Proxy enjekte edilir; gerçek nesne ilk metot çağrısına kadar üretilmez. Hiç çağrılmayan bir `@ApplicationScoped` bean hiç oluşmaz ve `@PostConstruct` metodu hiç çalışmaz.

Bu, `inventory-notification-service` içindeki RabbitMQ consumer'ının neden sessizce hiç başlamadığının cevabıdır — ve çözümü için bkz. [Eager Bean Başlatma](Eager-Bean-Baslatma).

## Tuzaklar

- **`@ApplicationScoped` bean içinde paylaşılan durum tutmak.** Tek örnek tüm eşzamanlı isteklerce paylaşılır; thread-safe olmayan alanlar yarış koşulu üretir. `Publisher.publishStockArrival` metodunun `synchronized` olmasının nedeni tam olarak budur.
- **Kapsam anotasyonu hiç vermemek.** Sınıf `@Dependent` olur ve `annotated` keşif modunda bean bile sayılmaz.
- **`@RequestScoped` bir bean'i `@ApplicationScoped` bir bean'in alanında saklamak.** Proxy sayesinde derlenir ve çalışır; ancak isteğin dışında erişilirse `ContextNotActiveException` alırsınız.

## İlgili Sayfalar

- [CDI Qualifier](CDI-Qualifier)
- [Eager Bean Başlatma](Eager-Bean-Baslatma)
- [beans.xml ve Bean Discovery](beans-xml-ve-Bean-Discovery)
