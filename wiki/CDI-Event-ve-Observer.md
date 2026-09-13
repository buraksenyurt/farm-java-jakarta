# CDI Event ve Observer

> **Okuma:** ~4 dk · **İlgili modül:** `todo-app`

| | |
| --- | --- |
| **Ne işe yarar** | Aynı süreç içinde bileşenleri birbirinden habersiz haberleştirir |
| **Anahtar** | `Event<T>`, `fire()`, `@Observes` |
| **.NET karşılığı** | MediatR notification'ları ya da C# `event` — ama kayıt gerektirmeyen biçimde |

## Problem

Bir todo oluşturulduğunda bildirim gönderilmesi isteniyor. Yarın loglama, öbür gün istatistik güncellemesi de eklenecek. Servis sınıfına her seferinde yeni bir bağımlılık eklemek istemiyoruz.

## Çözüm

### Olay sınıfı — düz bir POJO

```java
public class TodoCreatedEvent {
    private final Todo todo;

    public TodoCreatedEvent(Todo todo) { this.todo = todo; }

    public Todo getTodo() { return todo; }
}
```

Hiçbir anotasyon, hiçbir arayüz yok. CDI için bunun bir olay olması yeterlidir.

### Fırlatan taraf

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

### Dinleyen taraf

```java
@ApplicationScoped
public class TodoNotificationObserver {

    public void onTodoCreated(@Observes TodoCreatedEvent event) {
        String title = event.getTodo().getTask();
        System.out.println("[EVENT FIRED] New todo `" + title + "` has been created.");
    }
}
```

Metot adı önemsizdir. Bağlantıyı kuran tek şey parametredeki `@Observes` anotasyonu ve parametrenin **tipi**dir.

## Nasıl Çalışır?

1. Weld, deploy sırasında tüm `@Observes` metotlarını tarar ve tip bazlı bir dinleyici haritası oluşturur.
2. `fire()` çağrıldığında olay tipine uyan tüm observer metotları bulunur.
3. Varsayılan olarak **senkron** çalışırlar: `fire()` metodu, tüm observer'lar bitene kadar geri dönmez.

Yeni bir dinleyici eklemek için tek yapılacak `@Observes` taşıyan bir metot yazmaktır. Fırlatan sınıfta tek satır değişmez.

## Senkron mu, Asenkron mu?

| Yöntem | Davranış |
| --- | --- |
| `event.fire(e)` | Senkron. Observer'da oluşan istisna çağıran tarafa yansır ve aktif transaction'ı etkileyebilir. |
| `event.fireAsync(e)` | Asenkron. `CompletionStage` döner, çağıranı bloklamaz. |
| `@Observes(during = TransactionPhase.AFTER_SUCCESS)` | Observer yalnızca transaction başarıyla commit edilirse çalışır. |

Üçüncü satır pratikte çok işe yarar: kaydın veritabanına gerçekten yazıldığından emin olmadan bildirim göndermek istemezsiniz.

## Süreç İçi Olay ile Mesaj Kuyruğu Farkı

Bu depoda iki farklı "event" kavramı yan yana duruyor ve karıştırılmamaları gerekir:

| | CDI Event | RabbitMQ mesajı |
| --- | --- | --- |
| Sınır | Aynı JVM, aynı süreç | Süreçler ve makineler arası |
| Kalıcılık | Yok | Kuyrukta saklanır |
| Uygulama çökerse | Olay kaybolur | Mesaj kuyrukta bekler |
| Örnek | `todo-app` | `inventory/*` |

CDI Event, süreç içi gevşek bağlılık içindir; dağıtık sistemlerin çözümü değildir. Bkz. [RabbitMQ Exchange, Queue ve Binding](RabbitMQ-Exchange-Queue-ve-Binding).

## Tuzaklar

- **Observer'ın senkron olduğunu unutmak.** Uzun süren bir observer, HTTP yanıtınızı geciktirir.
- **Observer'da istisna fırlatmak.** Senkron modda bu istisna `fire()` çağrısına döner ve içinde bulunulan transaction'ı rollback edebilir.
- **Observer sınıfının kapsam anotasyonu taşımaması.** `annotated` keşif modunda taranmaz ve olay sessizce hiçbir yere gitmez — hata da alınmaz.
- **Sıra garantisi beklemek.** Birden fazla observer varsa çalışma sırası tanımsızdır; gerekirse `@Priority` kullanılmalıdır.

## İlgili Sayfalar

- [CDI Kapsamları](CDI-Kapsamlari)
- [Eager Bean Başlatma](Eager-Bean-Baslatma)
- [RabbitMQ Exchange, Queue ve Binding](RabbitMQ-Exchange-Queue-ve-Binding)
