# Fluent Bit ve OpenObserve

> **Okuma:** ~5 dk · **İlgili modül:** `inventory/*`

| | |
| --- | --- |
| **Ne işe yarar** | Dağınık log dosyalarını merkezi, sorgulanabilir bir yere taşır |
| **Anahtar** | Log forwarder, JSON formatter, stream |
| **.NET karşılığı** | Serilog sink'leri + Seq / Elastic |

## Problem

Payara Micro logları yalnızca konsola ve dosyaya yazar. İki servisi iki ayrı terminalde çalıştırdığınızda, aralarındaki bir olay akışını takip etmek için iki pencere arasında gidip gelmeniz gerekir. Servis sayısı arttıkça bu yöntem tamamen çöker.

Merkezi gözlemlenebilirlik *(observability)* bu sorunun cevabıdır.

## Zincir

```text
Uygulama ──SLF4J──▶ JUL ──▶ server.log (JSON) ──Fluent Bit──▶ OpenObserve
```

Üç halkanın üçü de ayrı ayrı yapılandırılmalıdır; biri eksikse log akışı sessizce durur.

## 1. Halka: Payara'yı JSON Yazmaya İkna Etmek

Payara Micro klasörüne bir `logging.properties` dosyası eklenir:

```properties
handlers=java.util.logging.FileHandler,java.util.logging.ConsoleHandler

java.util.logging.FileHandler.pattern=/home/buraks/payara-micro/logs/server.log
java.util.logging.FileHandler.formatter=fish.payara.enterprise.server.logging.JSONLogFormatter
java.util.logging.FileHandler.limit=10000000
java.util.logging.FileHandler.count=1
java.util.logging.FileHandler.append=true
java.util.logging.FileHandler.level=INFO

java.util.logging.ConsoleHandler.formatter=com.sun.enterprise.server.logging.ODLLogFormatter
java.util.logging.ConsoleHandler.level=FINE

.level=INFO
```

İki handler vardır: `ConsoleHandler` insan okuması için, `FileHandler` makine okuması için. Kritik satır `JSONLogFormatter` seçimidir — Fluent Bit'in ayrıştırabilmesi buna bağlıdır.

Dosya, sunucu başlatılırken açıkça belirtilmelidir:

```bash
java -Djava.net.preferIPv4Stack=true -jar payara-micro-7.2026.5.jar \
  --deploy wars/inventory-events-service-1.0-SNAPSHOT.war \
  --logproperties /home/buraks/payara-micro/logging.properties
```

## 2. Halka: Fluent Bit

```conf
[SERVICE]
    Flush         1
    Log_Level     info
    Parsers_File  parsers.conf

[INPUT]
    Name              tail
    Path              /var/log/payara/*.log
    Path_Key          source_file
    Tag               payara.*
    Parser            payara_json
    Refresh_Interval  5

[OUTPUT]
    Name          http
    Match         payara.*
    Host          openobserve
    Port          5080
    URI           /api/default/payara_logs/_json
    Format        json
    http_User     admin@example.com
    http_Passwd   ComplexPassword123!
    tls           off
```

| Bölüm | Rol |
| --- | --- |
| `[SERVICE]` | Genel ayarlar; `Flush` gönderim sıklığı |
| `[INPUT]` | `tail` eklentisi, dosya sonuna eklenen satırları izler |
| `[OUTPUT]` | `http` eklentisi ile OpenObserve'a gönderim |

`Path` değerinin `*.log` olması bilinçlidir: her servis kendi log dosyasına yazar, Fluent Bit hepsini toplar. `Path_Key` sayesinde her kaydın hangi dosyadan geldiği de saklanır.

`parsers.conf` minimaldir:

```conf
[PARSER]
    Name    payara_json
    Format  json
```

Konteyner tanımı `docker-compose.yml` içindedir ve host'taki log klasörünü salt okunur olarak bağlar:

```yml
fluent-bit:
  image: fluent/fluent-bit:latest
  container_name: java-town-fluent-bit
  volumes:
    - /home/buraks/payara-micro/logs:/var/log/payara:ro
    - ./fluent-bit.conf:/fluent-bit/etc/fluent-bit.conf:ro
    - ./parsers.conf:/fluent-bit/etc/parsers.conf:ro
```

## 3. Halka: Servis Başına Ayrı Log Dosyası

İki servisi aynı anda çalıştırırken her birine kendi properties dosyasını vermek, kaynakların ayrışmasını sağlar:

```bash
# Event service
java ... --port 8080 --nocluster \
  --deploy wars/inventory-events-service-1.0-SNAPSHOT.war \
  --logproperties /home/buraks/payara-micro/logging-events.properties

# Notification service
java ... --port 8081 --nocluster \
  --deploy wars/inventory-notification-service-1.0.war \
  --logproperties /home/buraks/payara-micro/logging-notification.properties
```

İki dosya yalnızca `FileHandler.pattern` satırında farklıdır.

Yapılandırma değiştiğinde Fluent Bit konteynerini yeniden başlatmayı unutmayın:

```bash
docker restart java-town-fluent-bit
```

## Sonuç

OpenObserve arayüzünde `payara_logs` adında bir stream oluşur; arayüze `http://localhost:5080` adresinden erişilir. Artık iki servisin logları tek bir yerde, zaman sıralı ve sorgulanabilir durumdadır.

## Tuzaklar

- **`--logproperties` vermeyi unutmak.** Payara varsayılan biçimde yazar, Fluent Bit JSON bekler, hiçbir kayıt ulaşmaz.
- **Volume yolunu yanlış vermek.** Konteyner boş bir klasör izler ve sessizce hiçbir şey yapmaz.
- **Yapılandırma sonrası konteyneri yeniden başlatmamak.** Fluent Bit eski ayarlarla çalışmaya devam eder.
- **Tek log dosyasına iki servis yazdırmak.** Kayıtlar karışır, kaynak ayrımı kaybolur.
- **Bu kurulumu evrensel sanmak.** Yollar ve ayarlar bu makineye özgüdür; tam Payara Server kurulumunda farklılık gösterir. Öğrenilmesi gereken şey yollar değil, üç halkalı zincirin mantığıdır.

## İlgili Sayfalar

- [SLF4J ve Payara Loglama](SLF4J-ve-Payara-Loglama)
- [Application Server ve Payara Micro](Application-Server-ve-Payara-Micro)
