# EntityManager, PersistenceContext ve JPQL

> **Okuma:** ~5 dk · **İlgili modül:** `todo-app`, `game-catalog-service`, `event-ticketing-service`

| | |
| --- | --- |
| **Ne işe yarar** | Nesnelerle veritabanı arasındaki tüm iletişimi yürütür |
| **Anahtar** | `@PersistenceContext`, `EntityManager`, JPQL |
| **.NET karşılığı** | `DbContext` + `DbSet` |

## EntityManager Nedir?

JPA'nın çalışan kalbi. Nesneleri kalıcı hale getirir, sorgular, günceller ve siler. `DbContext` ile doğrudan karşılaştırılabilir — ancak `DbSet<T>` gibi tip başına bir özellik yoktur; tüm işlemler tek nesne üzerinden, tip parametresiyle yapılır.

```java
@ApplicationScoped
public class JpaGameRepository implements GameRepository {

    @PersistenceContext(unitName = "gameCatalogPU")
    private EntityManager entityManager;
}
```

`@PersistenceContext`, `@Inject` değildir. CDI değil, **container** enjekte eder ve enjekte edilen şey thread'e duyarlı bir proxy'dir. `unitName`, `persistence.xml` içindeki persistence unit adına karşılık gelir; tek bir unit varsa yazılmayabilir — `todo-app` böyle yapar.

## Temel İşlemler

| Metot | Ne yapar | EF karşılığı |
| --- | --- | --- |
| `persist(e)` | Yeni nesneyi yönetime alır | `Add` |
| `find(Type.class, id)` | Birincil anahtarla getirir | `Find` |
| `merge(e)` | Ayrık *(detached)* nesneyi yönetime geri alır | `Update` / `Attach` |
| `remove(e)` | Siler | `Remove` |
| `createQuery(...)` | JPQL sorgusu oluşturur | LINQ |

```java
public Todo createTodo(Todo todo) {
    entityManager.persist(todo);
    return todo;
}

public Todo findTodoById(Long id) {
    return entityManager.find(Todo.class, id);
}
```

## JPQL — Tablo Değil, Entity Sorgulamak

```java
entityManager
    .createQuery("SELECT g FROM Game g WHERE g.genre = :genre", Game.class)
    .setParameter("genre", genre)
    .getResultList();
```

Buradaki `Game`, veritabanındaki tablonun adı **değildir**. Fiziksel tablo adı `games`'tir ve `@Table(name = "games")` ile bildirilmiştir. JPQL, entity sınıfı ve alanları üzerinden yazılan, sağlayıcıdan bağımsız bir sorgulama dilidir; SQL'e çeviriyi JPA sağlayıcısı yapar.

Aynı JPQL sorgusu PostgreSQL, MySQL ve H2 üzerinde çalışır. Bu depoda bunun üçü birden kullanılıyor.

| | JPQL | SQL |
| --- | --- | --- |
| Ne sorgular | Entity ve alan adları | Tablo ve sütun adları |
| Taşınabilirlik | Veritabanından bağımsız | Lehçeye bağlı |
| Doğrulama | Deploy/çalışma zamanı | Çalışma zamanı |

## İsimli Parametreler

```java
.setParameter("genre", genre)      // doğru
```

Sorgu metnini string birleştirmeyle kurmak SQL injection kapısı açar. İsimli parametreler hem güvenli hem de sorgu planı önbelleği açısından verimlidir.

## Kalıtılan Bir Tuzak: `getSingleResult()`

Sonuç yoksa `NoResultException`, birden fazlaysa `NonUniqueResultException` fırlatır — `null` dönmez. Bu depodaki repository'ler bu yüzden `find` ve `getResultList` tercih eder, `Optional` ile sarmalar:

```java
public Optional<Game> findById(Long id) {
    return Optional.ofNullable(entityManager.find(Game.class, id));
}
```

## Tuzaklar

- **`@PersistenceContext` yerine `@Inject` kullanmak.** `EntityManager` bir CDI bean'i değildir; enjeksiyon başarısız olur.
- **`unitName` yanlış yazmak.** Deploy anında persistence unit bulunamaz hatası alırsınız.
- **Transaction dışında yazma işlemi.** `persist` çağrısı bir transaction bağlamı gerektirir; yoksa `TransactionRequiredException` gelir. Bkz. [JTA, @Transactional ve Propagation](JTA-Transactional-ve-Propagation).
- **`EntityManager`'ı alanda saklayıp paylaşmak.** Enjekte edilen proxy zaten thread'e duyarlıdır; elle örnek üretip saklamak yarış koşulu üretir.
- **N+1 sorgu problemi.** Koleksiyon ilişkileri tembel yüklenir; döngü içinde erişim her elemanda ayrı sorgu üretir. `JOIN FETCH` çözümdür.

## İlgili Sayfalar

- [Entity Yaşam Döngüsü ve Dirty Checking](Entity-Yasam-Dongusu-ve-Dirty-Checking)
- [Veri Kaynağı Tanımlama](Veri-Kaynagi-Tanimlama)
- [JTA, @Transactional ve Propagation](JTA-Transactional-ve-Propagation)
