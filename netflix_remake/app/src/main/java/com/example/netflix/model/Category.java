package com.example.netflix.model;

import java.util.List;

public class Category {

    private String name;
    private List<Movie> films;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Movie> getFilms() {
        return films;
    }

    public void setFilms(List<Movie> films) {
        this.films = films;
    }
}
