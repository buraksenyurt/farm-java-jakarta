package com.lectures.gamecatalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/*
    Bazı doğrulama(Validation)' lar olduğunu görebiliriz.
    @NotBlank, @Min, @Positive gibi annotation'lar ile sağlanan bu kontroller
    yapısal doğrulamalardır. Bean Validation olarak ifade edebiliriz ve
resource katmanın otomatik olarak çalışırlar.

    Ancak örneğin Publisher değerinin gelecek bir yılda olmaması gibi kurallar
daha çok iş kuralları gibi düşünülebilir. Bunları service katmanında yazarız
zira domain bilgisi gerektirir.

    Entity sınıfımız Persistance sürücüsünü mySql'e çevirdikten sonra
bazı anotasyonlarla zenginleştirdik. Entity, Table, Column, Enumerated gibi.

    Enum türlerinde kasıtlı olarak ORDINAL yerine EnumType.STRING kullandık.
0,1,2 gibi sıralı numaraları kullandığımızda yeni bir enum değer eklenmesi veritabanı
tarafında eski kayıtların sessizce yanlış türe işaret etmesine neden olabilir.
STRING seçimi biraz yer kaplasa da bu sorunun yaşanmamasını da garanti eder.
 */
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Oyun başlığı boş olamaz")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Oyun türü seçilmelidir")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Genre genre;

    @NotNull(message = "Oyun platformu seçilmelidir")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Platform platform;

    @Min(value = 1970, message = "Piyasaya sürüldüğü yıl bilgisi geçerli olmalı.")
    @Column(nullable = false, name = "release_year")
    private int releaseYear;

    @NotBlank(message = "Yayıncı bilgisi boş olamaz")
    @Column(nullable = false)
    private String publisher;

    @Min(value = 1, message = "Topluluk puanı 1 den büyük, 10'dan küçük olmalıdır")
    @Max(value = 10, message = "Topluluk puanı 1 den büyük, 10'dan küçük olmalıdır")
    @Column(nullable = false)
    private short score;

    @Positive(message = "Fiyat bilgisi pozitif olmalıdır")
    @Column(nullable = false)
    private double price;

    @PositiveOrZero(message = "Stok değeri negatif olamaz")
    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(length = 1000)
    private String summary;

    public Game() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public short getScore() {
        return score;
    }

    public void setScore(short score) {
        this.score = score;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
