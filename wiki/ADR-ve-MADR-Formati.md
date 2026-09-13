# ADR ve MADR Formatı

> **Okuma:** ~4 dk · **İlgili dizin:** `docs/adr`

| | |
| --- | --- |
| **Ne işe yarar** | Mimari kararları, gerekçeleriyle birlikte kayıt altına alır |
| **Anahtar** | Context, options considered, decision, consequences |
| **.NET karşılığı** | Aynı uygulama; dile bağlı değildir |

## Neden?

Altı ay sonra kod tabanına bakan biri **ne** yapıldığını görür fakat **Neden** öyle yapıldığını göremez. Kararın alındığı andaki kısıtlar, değerlendirilen alternatifler ve kabul edilen bedeller kaybolur.

Bunun bilinen sonucu şudur: kimse dokunmaya cesaret edemediği için garip bir tasarım yıllarca yaşar ya da tam tersi, gerekçesi unutulmuş bir karar bir gün "temizlik" operasyonu adına bozulur ve nedeni anlaşılmayan hatalar başlar.

ADR *(Architecture Decision Record)*, bu boşluğu doldurur.

## Format

Bu depodaki ADR'lar **MADR** *(Markdown Architecture Decision Records)* biçimindedir. `docs/adr/0000-adr-template.md` dosyasında örnek bir şablon kullanımı vardır.

```markdown
# ADR-0002: The domain layer is framework-free

- **Status:** Accepted
- **Date:** 2026-09-11
- **Deciders:** Burak Selim Şenyurt

## Context
   Kararın alınmasını gerektiren durum, kısıtlar, baskılar

## Options considered
   1. Seçenek A — artıları, eksileri
   2. Seçenek B — artıları, eksileri

## Decision
   Ne seçildi ve neden

## Consequences
   Ne kazandık, hangi bedeli ödedik, ne zorlaştı
```

## Bu Depodaki ADR'lar

| Dosya | Karar |
| --- | --- |
| `0001-layered-architecture-and-dependency-direction.md` | Katmanlı mimari ve bağımlılığın yönü |
| `0002-the-domain-layer-is-framework-free.md` | Domain katmanı framework içermez |
| `0003-naming-and-annotation-conventions.md` | İsimlendirme ve anotasyon kuralları |
| `0004-general-coding-rules.md` | Genel kodlama kuralları |

## İyi Bir ADR Nasıl Yazılır?

ADR-0002 bunun için iyi bir örnektir. Kararın gerekçesi iki seviyede ele alınmıştır;

**Birinci derece etki:** Domain sınıfını doğrudan JPA entity'si yapmak ilk gün kod kazandırır ama modelin tasarımını ORM tarafında devreder *(lazy loading, detached örnekler, üretilmiş kimlik altında `equals`/`hashCode` sözleşmesi, argümansız constructor metot zorunluluğu)*.

**İkinci derece etki ve asıl önemli olan:** Domain sınıfı bir JPA entity'si olduğu anda bir iş kuralını test etmek `EntityManager` bileşeni gerektirir ki o da persistence unit gerektirir ve o da veritabanı gerektirir :D Bir milisaniyede doğrulanabilecek bir kural artık container ister hale gelmiştir.

**Ödenen bedel açıkça yazılmıştır:** `Book <-> BookEntity` eşlemesi için persistence katmanında elle yazılan kod.

> İyi bir ADR sadece alınan kararı savunmaz bedelini de kaydeder.

## Statü Alanı

| Statü | Anlamı |
| --- | --- |
| `Proposed` | Tartışmaya açık |
| `Accepted` | Yürürlükte |
| `Deprecated` | Artık önerilmiyor |
| `Superseded by ADR-XXXX` | Başka bir kararla değiştirildi |

ADR'lar **silinmez**. Bir karar değiştiğinde eski kayıt `Superseded` olarak işaretlenir ve yenisi yazılır. Kararların tarihçesi, kararların kendisi kadar değerlidir.

## Belgeden Teste

Bir ADR yalnızca belge olarak kalırsa zamanla gerçeklikten kopar. İşe yaraması için ArchUnit enstrümanlarına çevrilmesi gerekir. İsimlendirme ve anotasyon kuralları gibi mimari kararlar, ilgili testlerde doğrulanmalıdır.

```java
.as("ADR-0001: Layered architecture and dependency direction")
.because("Dependencies should only flow inward.")
```

Test başarısız olduğunda çıktı doğrudan ilgili ADR belgesini işaret eder. Bkz. [ArchUnit ile Mimari Testler](ArchUnit-ile-Mimari-Testler).

## Tuzaklar

- **Yalnızca kararı yazmak.** Alternatifler ve gerekçe olmadan yazılmış bir ADR sadece bir kural listesinden ibaret kalır.
- **Sonuçlar bölümünü boş bırakmak.** Bedeli yazılmamış karar, eleştiriye kapalıdır.
- **Her şeyi ADR yapmak.** ADR, **mimari** kararlar içindir; değişkene ne ad verileceği için değil.
- **Yazıp güncellememek.** Karar değiştiyse yeni bir ADR gerekir.
- **Üretilen ADR'ları denetlemeden kullanmak.** Bu çalışma alanındaki örnek ADR belgeleri Claude tarafından üretilmiştir. O nedenle gerçek projelerde bu tür belgelerin mimari yetkinliği olan kişilerce yazılması ya da denetlenmesi çok önemlidir!

## İlgili Sayfalar

- [ArchUnit ile Mimari Testler](ArchUnit-ile-Mimari-Testler)
- [Katmanlı Mimari ve Repository Deseni](Katmanli-Mimari-ve-Repository-Deseni)
