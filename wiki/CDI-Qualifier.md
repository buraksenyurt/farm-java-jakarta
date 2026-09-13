# CDI Qualifier

> **Okuma:** ~4 dk · **İlgili modül:** `cdi-concept`

| | |
| --- | --- |
| **Konu** | Bağımlılık çözümlemede belirsizliğin giderilmesi |
| **Jakarta API** | CDI 4.x — `jakarta.inject.Qualifier`, `jakarta.inject.Inject` |
| **.NET karşılığı** | Keyed Services (.NET 8+), öncesinde Factory deseni |
| **Ön bilgi** | `@Inject`, `@RequestScoped`, `@ApplicationScoped` |
| **Kaynak kod** | `cdi-concept/src/main/java/com/lectures/cdi/payment` |

---

## Problem

Elimizde tek bir soyutlama var:

```java
public interface PaymentProcessor {
    String pay(double amount);
}
```

Ve bu soyutlamayı gerçekleyen **iki** bileşen: `CreditCardProcessor` ve `CryptoProcessor`. Servis sınıfında basitçe şunu yazarsak:

```java
@Inject
private PaymentProcessor paymentProcessor;
```

CDI'ın çözmesi gereken bir belirsizlik *(ambiguous dependency)* doğar: iki adaydan hangisi enjekte edilecek? Container bu durumda deploy anında `AmbiguousResolutionException` fırlatır. Yani hata çalışma zamanında ilk istekte değil, uygulama ayağa kalkarken önümüze gelir; bu iyi bir şeydir.

## Çözüm: Kendi Qualifier'ımızı Yazmak

Qualifier, bir anotasyonun "DI için ayırt edici etiket" haline gelmesidir.

```java
@Qualifier                                   // CDI'a bunun bir ayırt edici olduğunu söyler
@Retention(RetentionPolicy.RUNTIME)          // Anotasyon çalışma zamanında okunabilir olmalı
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface Crypto {
}
```

Etiketi implementasyona takarız:

```java
@ApplicationScoped
@Crypto
public class CryptoProcessor implements PaymentProcessor { ... }
```

Ve enjeksiyon noktasında talep ederiz:

```java
@RequestScoped
public class InvoiceAcceptService {

    @Inject
    private PaymentProcessor paymentProcessor;        // @Default → CreditCardProcessor

    @Inject
    @Crypto
    private PaymentProcessor cryptoProcessor;         // @Crypto → CryptoProcessor
}
```

## Nasıl Çalışır?

1. Hiçbir qualifier belirtilmeyen her bean örtük olarak `@Default` qualifier'ı taşır. `CreditCardProcessor` bu yüzden çıplak `@Inject` ile gelir.
2. Bir bean'e açık bir qualifier (`@Crypto`) eklendiğinde, o bean **artık `@Default` değildir**. Bu, iki aday arasındaki belirsizliği ortadan kaldıran asıl mekanizmadır.
3. Çözümleme *(resolution)* tip + qualifier kümesi üzerinden yapılır ve deploy sırasında doğrulanır.

## .NET Tarafından Bakınca

```csharp
// .NET 8+
services.AddKeyedScoped<IPaymentProcessor, CryptoProcessor>("crypto");
...
public InvoiceAcceptService([FromKeyedServices("crypto")] IPaymentProcessor p) { }
```

Aradaki fark yüzeysel değil: .NET'te anahtar bir **string**'tir ve merkezi bir kayıt noktasında (`Program.cs`) tanımlanır. Java'da anahtar bir **tip**'tir, derleyici tarafından denetlenir ve kayıt merkezi yoktur — bileşen kendi kapsamını ve etiketini kendi üzerinde taşır. Yazım hatası yapma ihtimaliniz olan bir string yerine, yanlış yazarsanız derlenmeyen bir anotasyon.

## Kapsam Farkı

İki bileşen bilerek farklı kapsamlarla işaretlendi: `CreditCardProcessor` `@RequestScoped`, `CryptoProcessor` ise `@ApplicationScoped`. Ayrıntı için bkz. [CDI Kapsamları](CDI-Kapsamlari).

## Denemek İçin

```bash
mvn -pl cdi-concept clean package
java -Djava.net.preferIPv4Stack=true -jar payara-micro-7.2026.5.jar --deploy wars/cdi-concept.war

curl http://localhost:8080/cdi-concept/api/payment/standard
# 1000.0 TL ödeme kredi kartı ile yapıldı

curl http://localhost:8080/cdi-concept/api/payment/crypto
# 15.0 değerinde Kripto para ile ödeme yapıldı
```

## Tuzaklar

- **`@Retention(RUNTIME)` unutulursa** anotasyon derleme sonrası silinir; CDI onu göremez ve yine belirsizlik hatası alırsınız. Sessiz bir hata değildir ama nedeni ilk bakışta anlaşılmaz.
- **İki implementasyona da qualifier vermek** belirsizliği çözmez, sadece taşır: artık çıplak `@Inject` hiçbir adayı bulamaz ve `UnsatisfiedResolutionException` alırsınız.
- **`beans.xml` içindeki `bean-discovery-mode="annotated"`** ile çalışıyorsanız, implementasyonlarınızda bir kapsam anotasyonu bulunmak zorundadır. Çıplak bir POJO taranmaz ve aday listesine hiç girmez.
- Qualifier'lar üyeli *(member)* de olabilir (`@Payment(type = CRYPTO)`). Bu, her varyasyon için ayrı anotasyon yazmaktan kurtarır; `@Nonbinding` ile hangi üyenin çözümlemeye dahil olmayacağını da belirleyebilirsiniz.

## İlgili Sayfalar

- [CDI Kapsamları](CDI-Kapsamlari)
- [CDI Event ve Observer](CDI-Event-ve-Observer)
- [CDI Interceptor ile AOP](CDI-Interceptor-ile-AOP)
- [beans.xml ve Bean Discovery](beans-xml-ve-Bean-Discovery)
