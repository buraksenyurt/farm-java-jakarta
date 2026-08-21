# Docker Dünyası

Çalışma ortamımız Ubuntu ve sistemde virtulazation destekli bir işlemci yoksa Docker Desktop kullanamayabiliriz. Bu nedenle terminalden başımızın çağresine nasıl bakacağımızı bilmemiz gerekiyor. Aşağıdaki basit komutlar işimizi görecektir.

Temel işlemlerle başlayalım.

```bash
# Çalışmakta olan konteynerleri listele
docker ps

# Tüm konteynerleri listele
# Özellikle durmuş, hata almış konteynerleri görmek için kullanışlıdır.
docker ps -a

# docker-compose ile konteynerleri ayağa kaldırmak için
docker-compose up -d

# docker-compose ile çalıştırılan servisleri durdurmak ve bağlı olan ağları silmek içinse
docker-compose down

# Çalışan konteynerin içerisine terminal bağlantısı açmamız gerekirse
# Bazı imajlarda bash yerine sh yazmak gerekebilir.
# Çıkmak içinse giriş yapılan terminalden Ctrl+D veya exit komutu kullanılabilir.
docker exec -it <container_name> /bin/bash
```

Başımız sıkışırsa kullanabileceğimiz bazı komutlar:

```bash
# Loglara bakmak istersek
# -f parametresi logların canlı olarak akmasını sağlar. Çıkmak için Ctrl+C kullanılabilir.
docker logs -f <container_name>

# Tüm servis loglarını görmek istersek
docker-compose logs -f

# Konteynerin IP adresi, bağlı olduğu volume bilgileri, ortam değişkenleri gibi detaylı bilgileri görmek istersek
docker inspect <container_name>

# Host makine ile konteyner arsaındaki port eşleşmesini görmek istersek
docker port <container_name>

# Konteynerlerin sistem kaynaklarını ne kadar kullandığını görmek istersek
docker stats

# docker' ın sistemde ne kadar yer kapladığını görmek istersek
docker system df
```

Ara sıra mutfağı temizlemek gerekir. Zira kullanılmayan imajlar, durdurulmuş konteynerler diski fazlasıyla şişirebilir. Bu nedenle aşağıdaki komutları kullanabiliriz.

```bash
# Güvenli temizlik için 
docker system prune

# Ama elimizde nükleer bir güç de var :D
# Sadece o an çalışanlar hariç kullanılmayan tüm imajları, ağları ve veritabanı verileri dahil tüm volume'ları siler
docker system prune -a --volumes

# Belli bir imajı silmek istersek
docker rmi <image_name>

# Tabii ortada çalışan bir konteyner varsa imajı silemezsiniz. O zaman önce konteyneri durdurup silmeniz gerekir. Sonrasında imajı silebiliriz.
docker stop <container_name>
docker rm <container_name>

# Sistemdeki tüm veri birimlerini listelemek istersek
docker volume ls

# Sistemdeki tüm Docker ağlarını listelemek istersek
docker network ls
```
