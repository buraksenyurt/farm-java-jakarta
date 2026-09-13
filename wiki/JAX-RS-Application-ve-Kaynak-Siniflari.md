# JAX-RS Application ve Kaynak Sınıfları

> **Okuma:** ~4 dk · **İlgili modül:** `games-api`, `todo-app`, tüm servisler

| | |
| --- | --- |
| **Ne işe yarar** | HTTP isteklerini Java metotlarına bağlar |
| **Anahtar** | `@ApplicationPath`, `@Path`, `@GET`/`@POST`/`@PUT`/`@DELETE` |
| **.NET karşılığı** | `[Route]`, `[HttpGet]`, `ControllerBase` |

## Uygulama Yapılandırması

Her modülde tek bir sınıf REST katmanını etkinleştirir:

```java
@ApplicationPath("api")
public class RestApplicationConfig extends Application {
}
```

Gövdesi boştur; varlığı yeterlidir. `web.xml` içinde servlet tanımı yapmaya gerek kalmaz.

`todo-app` bu değeri `api/v1` olarak vererek sürümlemeyi URL'e taşır — kalan modüller yalnızca `api` kullanır.

## Kaynak Sınıfı

```java
@Path("todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoRest {

    @Inject
    TodoService todoService;

    @GET
    @Path("{id}")
    public Todo getTodo(@PathParam("id") Long id) { ... }

    @POST
    @Path("new")
    public Response createTodo(Todo todo) { ... }

    @POST
    @Path("status")
    public Response markAsComplete(@QueryParam("id") Long id) { ... }
}
```

## URL Nasıl Oluşur?

```text
http://localhost:8080/todo-app/api/v1/todo/list
                      └───┬──┘ └─┬──┘ └─┬─┘└─┬─┘
                   WAR adı       │      │    └─ metot @Path
                    @ApplicationPath    └────── sınıf @Path
```

Dört parça: **context path** (WAR adı) + `@ApplicationPath` + sınıf `@Path` + metot `@Path`. Bir uç noktayı bulamıyorsanız hatayı bu dört parçadan birinde arayın.

## Parametre Anotasyonları

| Anotasyon | Nereden okur | Örnek |
| --- | --- | --- |
| `@PathParam` | URL şablonundan | `/todo/{id}` |
| `@QueryParam` | Sorgu dizesinden | `/todo/status?id=2` |
| `@HeaderParam` | HTTP başlığından | `Authorization` |
| `@FormParam` | Form gövdesinden | `application/x-www-form-urlencoded` |
| *(anotasyonsuz)* | İstek gövdesinden | JSON → nesne |

Anotasyonsuz tek parametre gövdedir. `createTodo(Todo todo)` metodunda JSON'dan `Todo` nesnesine dönüşümü JSON-B *(Jakarta JSON Binding)* yapar; ek bir kütüphane ya da yapılandırma gerekmez.

## Dönüş Tipi: Nesne mi, `Response` mu?

```java
public List<Todo> getTodos() { ... }                   // 200 OK + JSON gövde

public Response createTodo(Todo todo) {
    var uri = UriBuilder.fromResource(TodoRest.class)
                        .path(created.getId().toString()).build();
    return Response.created(uri).build();              // 201 Created + Location başlığı
}
```

Durum kodunu, başlıkları ya da boş gövdeyi kontrol etmeniz gerekiyorsa `Response` kullanın; aksi halde doğrudan nesne döndürmek daha okunabilirdir.

`UriBuilder.fromResource` ile `Location` başlığının elle birleştirilmemesi önemlidir: yol değiştiğinde URL kendiliğinden doğru kalır.

## Kapsam Notu

Kaynak sınıfları `@RequestScoped` olarak işaretlenir *(bazı modüllerde anotasyon verilmemiştir; JAX-RS varsayılan olarak istek başına örnek üretir)*. `@ApplicationScoped` bir kaynak sınıfı, istek durumunu paylaşma riski taşır.

## Tuzaklar

- **`@ApplicationPath` taşıyan sınıfı hiç oluşturmamak.** REST katmanı etkinleşmez ve tüm uç noktalar 404 döner.
- **Baştaki eğik çizgi karışıklığı.** `@Path("todo")` ile `@Path("/todo")` aynı sonucu verir; tutarlı olmak okunabilirliği artırır.
- **Aynı yol + aynı HTTP metodu.** İki metot çakışırsa deploy anında belirsizlik hatası alırsınız.
- **`@Consumes` / `@Produces` eksikliği.** İçerik tipi uyuşmazlığında `415 Unsupported Media Type` alırsınız ve neden anlaşılmaz.
- **WAR adının uzunluğu.** `inventory-events-service-1.0-SNAPSHOT` gibi bir context path tüm URL'lerinizde yer alır.

## İlgili Sayfalar

- [JAX-RS ExceptionMapper](JAX-RS-ExceptionMapper)
- [JAX-RS Asenkron Yanıt](JAX-RS-Asenkron-Yanit)
- [Bean Validation](Bean-Validation)
