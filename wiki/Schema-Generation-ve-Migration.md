# Schema Generation ve Migration

> **Okuma:** ~3 dk · **İlgili dosya:** her modülde `persistence.xml`

| | |
| --- | --- |
| **Ne işe yarar** | Veritabanı şemasının kim tarafından oluşturulacağını belirler |
| **Anahtar** | `jakarta.persistence.schema-generation.database.action` |
| **.NET karşılığı** | `EnsureCreated()` ile `Migrate()` arasındaki fark |

## Ayar

```xml
<property name="jakarta.persistence.schema-generation.database.action" value="none"/>
```

| Değer | Davranış | Nerede kullanılır |
| --- | --- | --- |
| `none` | Hiçbir şey yapma | Üretim; bu depodaki çoğu modül |
| `create` | Yoksa oluştur | Hızlı başlangıç |
| `drop-and-create` | Her açılışta sil ve yeniden oluştur | Yalnızca geliştirme |
| `drop` | Sil | Test temizliği |

## `drop-and-create` Tuzağı

Uygulama her başladığında tablolar silinip yeniden oluşturulur. Şemayı hızla sıfırlamak için kullanışlıdır — ve **tüm veriyi yok eder**. Geliştirme sırasında sunucuyu yeniden başlattığınız her sefer, dün girdiğiniz test verisi gider.

Üretim ortamında bu ayar bir felakettir.

## Alternatif: Migration

Şemayı, sürümlenmiş ve elle yazılmış SQL betikleriyle yönetmek. Bu depoda `game-catalog-service` modülü Flyway ile bu yaklaşımı gösterir; `event-ticketing-service` ise şemayı doğrudan `sql/eventTicketing.sql` betiğiyle kurar.

## Neden Java Dünyasında "Code First" Yok?

Entity Framework'ten gelen bir geliştirici, entity sınıflarından migration üreten bir araç arar ve bulamaz. Bu bir eksiklik değil, **bilinçli bir gelenektir**.

Otomatik üretilen migration'lar semantik olarak belirsiz değişikliklerde hata yapar. Klasik örnek yeniden adlandırmadır: bir sütunun adını değiştirdiğinizde araç bunu "eski sütunu sil, yeni sütun ekle" olarak yorumlayabilir. Sonuç sessiz veri kaybıdır.

Java ekosisteminde migration betiklerinin **elle yazılmış ve gözden geçirilmiş** olması tercih edilir. EF tarafında da aynı risk vardır; fark, Java dünyasının bu riske karşı baştan temkinli davranmasıdır.

## Ara Yol

Geliştirmenin erken safhasında `drop-and-create` ile hızlı ilerleyip, şema oturduktan sonra migration'a geçmek yaygın bir yaklaşımdır. `memo-app` modülü bu nedenle `create` ya da `drop-and-create` ile çalışabilecek biçimde bırakılmıştır.

Geçiş anını kaçırmamak gerekir: paylaşılan bir ortama ilk deploy, son sınırdır.

## Tuzaklar

- **Ayarı `drop-and-create` bırakıp ortamı paylaşıma açmak.** Bir başkasının verisini silersiniz.
- **`none` ayarıyla tabloları oluşturmayı unutmak.** Uygulama açılır, ilk sorguda "tablo yok" hatası alırsınız.
- **Hem schema-generation hem Flyway kullanmak.** İkisi aynı şemaya farklı yollardan müdahale eder; Flyway kullanıyorsanız bu ayar `none` olmalıdır.
- **Üretimde test için "bir kereliğine" değiştirmek.** Bir kerelik değişikliklerin geri alınması çoğunlukla unutulur.

## İlgili Sayfalar

- [Flyway ile Versiyonlama](Flyway-ile-Versiyonlama)
- [Veri Kaynağı Tanımlama](Veri-Kaynagi-Tanimlama)
