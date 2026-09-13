# CDI Interceptor ile AOP

> **Okuma:** ~4 dk · **İlgili modül:** `todo-app`

| | |
| --- | --- |
| **Ne işe yarar** | Loglama, ölçüm, yetkilendirme gibi kesişen ilgileri iş mantığından ayırır |
| **Anahtar** | `@InterceptorBinding`, `@AroundInvoke`, `@Priority` |
| **.NET karşılığı** | ASP.NET Core action filter'ları, `DispatchProxy`, Castle DynamicProxy |

## Üç Parça

Bir interceptor kurmak için üç dosya gerekir.

### 1. Binding anotasyonu — "etiket"

```java
@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
}
```

### 2. Interceptor sınıfı — "asıl iş"

```java
@Interceptor
@LogExecutionTime                                  // hangi etikete bağlanıyor
@Priority(Interceptor.Priority.APPLICATION)        // etkinleştirme
public class ExecutionTimeInterceptor {

    @AroundInvoke
    public Object logTime(InvocationContext context) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            return context.proceed();              // asıl metodu çalıştır
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("EXECUTION DURATION: `"
                + context.getMethod().getName() + "` duration is " + duration + " milliseconds");
        }
    }
}
```

### 3. Kullanım — "etiketi tak"

```java
@Path("list")
@GET
@LogExecutionTime
public List<Todo> getTodos() {
    return todoService.getTodos();
}
```

## `context.proceed()` Neden Kritik?

`proceed()` çağrısı, zincirdeki bir sonraki interceptor'a — ya da zincir bittiyse asıl metoda — kontrolü devreder. Bu satırı unutursanız **asıl metodunuz hiç çalışmaz** ve hiçbir hata da almazsınız. Metot sessizce `null` döner.

`try/finally` kullanımı da bilinçlidir: metot istisna fırlatsa bile süre ölçümü yazılır.

## Etkinleştirme: `@Priority`

CDI 1.1 öncesinde interceptor'lar `beans.xml` içinde tek tek listelenirdi. Bugün `@Priority` bunu gereksiz kılar:

| Sabit | Değer aralığı | Ne için |
| --- | --- | --- |
| `Interceptor.Priority.PLATFORM_BEFORE` | 0–999 | Platform interceptor'ları (transaction gibi) |
| `Interceptor.Priority.LIBRARY_BEFORE` | 1000–1999 | Kütüphaneler |
| `Interceptor.Priority.APPLICATION` | 2000–2999 | Uygulama kodu — bizim yerimiz |
| `Interceptor.Priority.LIBRARY_AFTER` | 3000+ | Son işlemler |

Küçük değer önce çalışır. Aynı metotta birden fazla interceptor varsa sıra bu değerle belirlenir.

## Zaten Kullandığınız Bir Interceptor

`@Transactional` anotasyonu da bir interceptor binding'dir. `TicketBookingService.bookTickets` metodunu çevreleyen, transaction'ı başlatıp commit ya da rollback eden şey, sizinkiyle aynı mekanizmayı kullanan bir platform interceptor'ıdır.

Bu yüzden `@Transactional` ile ilgili tuzaklar burada da geçerlidir — bkz. aşağıdaki ilk madde.

## Tuzaklar

- **Kendi kendini çağıran metot.** Interceptor proxy üzerinden çalışır. Aynı sınıf içinde `this.getTodos()` biçiminde yapılan çağrı proxy'ye uğramaz, interceptor devreye girmez. Bu, `@Transactional` için de aynen geçerlidir ve en sık yapılan hatadır.
- **`@Retention(RUNTIME)` unutmak.** Anotasyon derleme sonrası silinir, hiçbir şey çalışmaz.
- **`proceed()` çağrısını atlamak.** Asıl metot çalışmaz.
- **`private` ya da `final` metotlar** proxy'lenemez, interceptor uygulanmaz.
- **Ağır işleri interceptor'a koymak.** Her çağrıda çalışır; maliyeti kolayca gözden kaçar.

## İlgili Sayfalar

- [JTA, @Transactional ve Propagation](JTA-Transactional-ve-Propagation)
- [CDI Kapsamları](CDI-Kapsamlari)
- [CDI Event ve Observer](CDI-Event-ve-Observer)
