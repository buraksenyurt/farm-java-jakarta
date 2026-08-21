package com.lectures.java.games.games.model;

public class Game {

    private int    id;
    private String title;
    private String developer;
    private int    releaseYear;
    private String genre;
    private String platform;

    public Game() {
    }
    
    public Game(int id, String title, String developer,
                int releaseYear, String genre, String platform) {
        this.id          = id;
        this.title       = title;
        this.developer   = developer;
        this.releaseYear = releaseYear;
        this.genre       = genre;
        this.platform    = platform;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDeveloper() {
        return developer;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public String getPlatform() {
        return platform;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}