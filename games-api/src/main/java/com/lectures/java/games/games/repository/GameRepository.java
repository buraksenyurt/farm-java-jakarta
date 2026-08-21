package com.lectures.java.games.games.repository;

import com.lectures.java.games.games.model.Game;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class GameRepository {

    private final List<Game> games;

    public GameRepository() {
        games = Collections.unmodifiableList(List.of(
            new Game(1,  "Pac-Man",           "Namco",       1980, "Maze",        "Arcade / Atari 2600"),
            new Game(2,  "Donkey Kong",        "Nintendo",    1981, "Platform",    "Arcade / ColecoVision"),
            new Game(3,  "Space Invaders",     "Taito",       1978, "Shooter",     "Arcade / Atari 2600"),
            new Game(4,  "Galaga",             "Namco",       1981, "Shooter",     "Arcade / NES"),
            new Game(5,  "Frogger",            "Konami",      1981, "Action",      "Arcade / Atari 2600"),
            new Game(6,  "Centipede",          "Atari",       1980, "Shooter",     "Arcade / Atari 2600"),
            new Game(7,  "Asteroids",          "Atari",       1979, "Shooter",     "Arcade / Atari 2600"),
            new Game(8,  "Missile Command",    "Atari",       1980, "Strategy",    "Arcade / Atari 2600"),
            new Game(9,  "Super Mario Bros.",  "Nintendo",    1985, "Platform",    "NES / Arcade"),
            new Game(10, "The Legend of Zelda","Nintendo",    1986, "Action-RPG",  "NES / Famicom Disk")
        ));
    }

    public List<Game> findAll() {
        return games;
    }
}
