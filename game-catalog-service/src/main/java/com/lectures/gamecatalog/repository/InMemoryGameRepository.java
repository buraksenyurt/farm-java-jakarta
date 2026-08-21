package com.lectures.gamecatalog.repository;

import com.lectures.gamecatalog.model.Game;
import com.lectures.gamecatalog.model.Genre;
import com.lectures.gamecatalog.model.Platform;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/*
    In-Memory çalışan bir repository var. Application ayağa kalktığında
bir kez oluşturulur. Doğal olarak uygulama sonlandığından bellekteki veriler
de gidecektir. Ancak sonraki versiyondan bunu kalıcı bir veritabanı ile
değiştireceğiz.

    Veriyi bellekte eş zamanlı okuma ve yazmalarda thread-safe olan 
ConcurrentHashMap türünde tutuyoruz. Pek tabii eş zamanlı gelecek HTTP istekleri
söz konusu. Bu nedenle thread-safe ve senkronize edilmiş bir veri yapısını
kullanıyoruz.

Özellikle Id üretimine dikkat edelim. Eş zamanlı isteklerde çakışma olabilir.
Bu nedenle atomik bir sayısal değer kullanıyoruz.

 */
// @ApplicationScoped 
//MySQL sürücüsünü kullanan repository'ye geçtiğimiz için kaldırdık.
public class InMemoryGameRepository implements GameRepository {

    private final Map<Long, Game> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @Override
    public Game save(Game game) {
        long newId = idSequence.incrementAndGet();
        game.setId(newId);
        store.put(newId, game);
        return game;
    }

    @Override
    public Optional<Game> update(Long id, Game game) {
        if (!store.containsKey(id)) {
            return Optional.empty();
        }
        game.setId(id);
        store.put(id, game);
        return Optional.of(game);
    }

    @Override
    public boolean delete(Long id) {
        return store.remove(id) != null;
    }

    @Override
    public Optional<Game> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Game> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public List<Game> findByGenre(Genre genre) {
        return store.values().stream()
                .filter(g -> g.getGenre() == genre)
                .collect(Collectors.toList());
    }

    @Override
    public List<Game> findByPlatform(Platform platform) {
        return store.values().stream()
                .filter(g -> g.getPlatform() == platform)
                .collect(Collectors.toList());
    }

}
