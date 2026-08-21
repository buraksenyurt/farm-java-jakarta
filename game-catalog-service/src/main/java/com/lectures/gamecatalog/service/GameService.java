package com.lectures.gamecatalog.service;

import com.lectures.gamecatalog.model.*;
import com.lectures.gamecatalog.repository.GameRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Year;
import java.util.List;

@ApplicationScoped
public class GameService {

    @Inject
    private GameRepository repository;

    private void validateBusinessRules(Game game) {
        int currentYear = Year.now().getValue();
        if (game.getReleaseYear() > currentYear) {
            // throw new IllegalArgumentException("Gelecek bir yıl için giriş yapamayız");
            throw new GameBusinessValidationException("Gelecek bir yıl için giriş yapamayız!");
        }
    }

    public Game create(Game game) {
        validateBusinessRules(game);
        return repository.save(game);
    }

    public Game update(Long id, Game game) {
        validateBusinessRules(game);
        return repository.update(id, game).orElseThrow(() -> new GameNotFoundException(id));
    }

    public void delete(Long id) {
        if (!repository.delete(id)) {
            throw new GameNotFoundException(id);
        }
    }

    public Game getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new GameNotFoundException(id));
    }

    public List<Game> getAll() {
        return repository.findAll();
    }

    public List<Game> getByGenre(Genre genre) {
        return repository.findByGenre(genre);
    }

    public List<Game> getByPlatform(Platform platform) {
        return repository.findByPlatform(platform);
    }
}
