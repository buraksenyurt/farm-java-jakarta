package com.lectures.gamecatalog.repository;

import com.lectures.gamecatalog.model.Game;
import com.lectures.gamecatalog.model.Genre;
import com.lectures.gamecatalog.model.Platform;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class JpaGameRepository implements GameRepository {

    @PersistenceContext(unitName = "gameCatalogPU")
    private EntityManager entityManager;

    @Override
    @Transactional
    public Game save(Game game) {
        entityManager.persist(game);
        return game;
    }

    @Override
    @Transactional
    public Optional<Game> update(Long id, Game game) {
        Game existing = entityManager.find(Game.class, id);
        if (existing == null) {
            return Optional.empty();
        }
        game.setId(id);
        return Optional.of(entityManager.merge(game));
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Game existing = entityManager.find(Game.class, id);
        if (existing == null) {
            return false;
        }
        entityManager.remove(existing);
        return true;
    }

    @Override
    public Optional<Game> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Game.class, id));
    }

    /*
        Sorgular ile ilgili bir not düşelim. Migration ile ilgili SQL
    dosyalarında MySQL tablo adı olarak `games` kullanılıyor. Game entity
    sınıfında da Table anotasyonunda `games` adı kullanılmakta. Bu tamamen
    fiziksel tablonun adı.
    
    Ancak aşağıdaki sorgularda dikkat edileceği üzere Game şeklinde
    doğrudan Entity sınıf adı kullanılmakta. Burada JPQL(Jakarta Persistence
    Query Language) notasyonu söz konusudur. Yani Entity seviyesinde yazılan,
    provider-agnostik bir sorgulama dilinden bahsediyoruz. EclipseLink bu
    sorguları SQL'e çevirirken yine tablo adı olarak games'i kullanacaktır.
    
    */
    @Override
    public List<Game> findAll() {
        return entityManager
                .createQuery("SELECT g FROM Game g", Game.class)
                .getResultList();
    }

    @Override
    public List<Game> findByGenre(Genre genre) {
        return entityManager
                .createQuery("SELECT g FROM Game g WHERE g.genre = :genre", Game.class)
                .setParameter("genre", genre)
                .getResultList();
    }

    @Override
    public List<Game> findByPlatform(Platform platform) {
        return entityManager
                .createQuery("SELECT g FROM Game g WHERE g.platform = :platform", Game.class)
                .setParameter("platform", platform)
                .getResultList();
    }

}
