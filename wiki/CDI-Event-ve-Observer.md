# CDI Event ve Observer

> **Okuma:** ~4 dk · **İlgili modül:** `todo-app`

| | |
| --- | --- |
| **Ne işe yarar** | Aynı süreç içindeki bileşenlerin olaylar yardımıyla haberleşmesini sağlar |
| **Anahtar** | `Event<T>`, `fire()`, `@Observes` |
| **.NET karşılığı** | MediatR notification'ları ya da C# `event` — ama kayıt *(service registration)* gerektirmeyen biçimde |

## Problem

Bir todo oluşturulduğunda bildirim gönderilmesi isteniyor. Yarın loglama, öbür gün istatistik güncellemesi de eklenecek. Ancak servis sınıfına her seferinde yeni bir bağımlılık *(dependency)* eklemek istemiyoruz.

## Çözüm

### Olay sınıfı — düz bir POJO *(Plain Old Java Object)*

```java
public class TodoCreatedEvent {
    private final Todo todo;

    public TodoCreatedEvent(Todo todo) { this.todo = todo; }

    public Todo getTodo() { return todo; }
}
```

Hiçbir anotasyon, hiçbir arayüz yok. CDI için bunun bir olay *(event)* olması yeterlidir.

### Fırlatan taraf *(event producer)*

```java
@Inject
private Event<TodoCreatedEvent> todoEvent;

@POST
@Path("new")
public Response createTodo(Todo todo) {
    var created = todoService.createTodo(todo);
    todoEvent.fire(new TodoCreatedEvent(created));   // kimin dinlediğini bilmiyor
    return Response.created(uri).build();
}
```

### Dinleyen taraf *(event observer)*

```java
@ApplicationScoped
public class TodoNotificationObserver {

    public void onTodoCreated(@Observes TodoCreatedEvent event) {
        String title = event.getTodo().getTask();
        System.out.println("[EVENT FIRED] New todo `" + title + "` has been created.");
    }
}
```

Metot adı önemsizdir. Bağlantıyı kuran tek şey parametredeki `@Observes` anotasyonu ve parametrenin **tipi**dir *(TodoCreatedEvent)*

## Nasıl Çalışır?

1. **Weld** motoru, deploy sırasında tüm `@Observes` metotlarını tarar ve tip bazlı bir dinleyici haritası oluşturur.
2. `fire()` çağrıldığında olay tipine uyan tüm observer metotları bulunur.
3. Varsayılan olarak **senkron** çalışırlar. `fire()` metodu, tüm observer'ların işleyişi bitene kadar geri dönmez.

Yeni bir dinleyici eklemek için yapılacak tek şey `@Observes` anotasyonunu kullanan bir metot yazmaktır. Fırlatan sınıfta tek satır değişiklik gerekmez.

## Senkron mu, Asenkron mu?

| **Yöntem** | **Davranış** |
| --- | --- |
| **`event.fire(e)`** | Senkron. Observer'da oluşan istisna çağıran tarafa yansır ve aktif transaction'ı etkileyebilir. |
| **`event.fireAsync(e)`** | Asenkron. `CompletionStage` döner, çağıran tarafı bloklamaz. |
| **`@Observes(during = TransactionPhase.AFTER_SUCCESS)`** | Observer yalnızca transaction başarıyla commit edilirse çalışır. |

Üçüncü satır pratikte çok işe yarar: kaydın veritabanına gerçekten yazıldığından emin olmadan bildirim göndermek istemezsiniz.

## Süreç İçi Olay ile Mesaj Kuyruğu Farkı

Bu depoda iki farklı "event" kavramı ele alınmıştır ve karıştırılmamaları gerekir:

| | **CDI Event** | **RabbitMQ mesajı** |
| --- | --- | --- |
| Sınır *(scope)* | Aynı JVM, aynı süreç | Süreçler ve makineler arası |
| Kalıcılık *(persistence)* | Yok | Kuyrukta saklanır |
| Uygulama çökerse | Olay kaybolur | Mesaj kuyrukta bekler |
| Örnekler | `todo-app` | `inventory/*` |

CDI Event, süreç içi gevşek bağlılık *(Loosely Coupled)* içindir ve dağıtık sistemlerin bir çözümü değildir. Bkz. [RabbitMQ Exchange, Queue ve Binding](RabbitMQ-Exchange-Queue-ve-Binding).

## Tuzaklar

- **Observer'ın senkron olduğunu unutmak.** Uzun süren bir observer işleyişi HTTP yanıtınızı geciktirir. Ağ trafiği yüksek olduğunda performans sorunlarına yol açabilir.
- **Observer'da istisna fırlatmak.** Senkron modda bu istisna `fire()` çağrısına döner ve içinde bulunulan transaction'ı rollback edebilir.
- **Observer sınıfının kapsam anotasyonu taşımaması.** `annotated` keşif modunda taranmaz ve olay sessizce hiçbir yere gitmez — hata da alınmaz. *(Bu durumu garanti altına almak için belki ArchUnit ile ADR testleri yazılabilir)*
- **Sıra garantisi beklemek.** Birden fazla observer varsa çalışma sırası tanımsızdır; gerekirse `@Priority` kullanılmalıdır.

## İlgili Sayfalar

- [CDI Kapsamları](CDI-Kapsamlari)
- [Eager Bean Başlatma](Eager-Bean-Baslatma)
- [RabbitMQ Exchange, Queue ve Binding](RabbitMQ-Exchange-Queue-ve-Binding)
