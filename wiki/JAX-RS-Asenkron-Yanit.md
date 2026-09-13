# JAX-RS Asenkron Yanıt

> **Okuma:** ~4 dk · **İlgili modül:** `todo-app`

| | |
| --- | --- |
| **Ne işe yarar** | Uzun süren işlerde istek thread'ini serbest bırakır |
| **Anahtar** | `@Suspended`, `AsyncResponse`, `ManagedExecutorService` |
| **.NET karşılığı** | `async Task<IActionResult>` — ama thread yönetimi sizde değil |

## Kod

```java
@Resource
private ManagedExecutorService managedExecutor;

@GET
@Path("async/list")
public void getTodoAsync(@Suspended final AsyncResponse asyncResponse) {
    CompletableFuture
        .supplyAsync(() -> todoService.getTodos(), managedExecutor)
        .thenAccept(result -> asyncResponse.resume(Response.ok(result).build()))
        .exceptionally(ex -> {
            asyncResponse.resume(Response.serverError().entity(ex.getMessage()).build());
            return null;
        });
}
```

Metodun dönüş tipinin `void` olduğuna dikkat edin. Yanıt `return` ile değil, `asyncResponse.resume(...)` ile gönderilir.

## Akış

1. İstek gelir, JAX-RS metodu çağırır.
2. `@Suspended` sayesinde istek **askıya alınır**; HTTP bağlantısı açık kalır ama istek thread'i havuza geri döner.
3. İş, `managedExecutor` havuzundaki başka bir thread'de çalışır.
4. İş bitince `resume()` çağrılır ve yanıt istemciye gider.

Kazanç şudur: 200 eşzamanlı yavaş istek, 200 istek thread'ini işgal etmez.

## Neden `ManagedExecutorService`?

Buradaki asıl öğretici nokta bu. Klasik bir `CompletableFuture.supplyAsync(...)` çağrısı işi JVM'in ortak `ForkJoinPool` havuzuna verir. O havuzdaki thread'ler **container tarafından yönetilmez** ve Jakarta EE bağlamını *(transaction, güvenlik, CDI context'leri)* taşımaz.

Bu depoda bunun somut sonucu, kaynak kodun yorumunda kayıtlıdır: ortak havuz kullanıldığında Weld tarafında proxy üretimiyle ilgili bir hata alınmıştır.

```java
@Resource
private ManagedExecutorService managedExecutor;
```

`@Resource` ile talep edilen bu havuz Payara tarafından yönetilir, bağlamı taşır ve sunucunun kaynak kontrolüne tabidir. Jakarta EE içinde **kendi thread'inizi yaratmamanız** gereken durumun sebebi budur.

| | Ortak `ForkJoinPool` | `ManagedExecutorService` |
| --- | --- | --- |
| Yöneten | JVM | Uygulama sunucusu |
| Jakarta bağlamı | Taşınmaz | Taşınır |
| Havuz boyutu kontrolü | Yok | Sunucu yapılandırması |
| Container görünürlüğü | Yok | Var |

## Ne Zaman Kullanmalı?

**Uygundur:** dış servis çağrıları, uzun süren raporlar, toplu işler, birden fazla kaynağın paralel toplanması.

**Gereksizdir:** hızlı biten veritabanı sorguları. Yukarıdaki `getTodos()` örneği gerçek bir performans kazancı sağlamaz; mekanizmayı göstermek için oradadır. Ek karmaşıklığın bedeli, kazanç yoksa ödenmemelidir.

## Tuzaklar

- **`resume()` çağrısını hiç yapmamak.** İstek sonsuza kadar askıda kalır; istemci zaman aşımına uğrar.
- **`exceptionally` bloğunu atlamak.** İş patlarsa `resume()` çağrılmaz ve yukarıdaki duruma düşersiniz.
- **İki kez `resume()`.** İkincisi `IllegalStateException` fırlatır.
- **Zaman aşımı belirtmemek.** `asyncResponse.setTimeout(...)` ile üst sınır koymak iyi bir alışkanlıktır.
- **Asenkron thread'de transaction beklemek.** `@Transactional` bağlamı thread'e bağlıdır; işi asenkron havuza attığınızda aynı transaction'da olmazsınız.

## İlgili Sayfalar

- [JAX-RS Application ve Kaynak Sınıfları](JAX-RS-Application-ve-Kaynak-Siniflari)
- [JTA, @Transactional ve Propagation](JTA-Transactional-ve-Propagation)
