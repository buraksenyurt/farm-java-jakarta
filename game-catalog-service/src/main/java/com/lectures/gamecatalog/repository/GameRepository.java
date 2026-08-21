package com.lectures.gamecatalog.repository;

import com.lectures.gamecatalog.model.*;
import java.util.List;
import java.util.Optional;

public interface GameRepository {

    Game save(Game game);

    Optional<Game> update(Long id, Game game);

    Optional<Game> findById(Long id);
    
    boolean delete(Long id);

    List<Game> findAll();

    List<Game> findByGenre(Genre genre);

    List<Game> findByPlatform(Platform platform);
}
