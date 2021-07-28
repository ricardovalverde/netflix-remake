package com.example.netflix.model;

import java.util.List;

public class MovieDetail {

    private final Movie movie;
    private final List<Movie> filmes_similar;

    public MovieDetail(Movie movie, List<Movie> filmes_similar) {
        this.movie = movie;
        this.filmes_similar = filmes_similar;
    }

    public Movie getFilme() {
        return movie;
    }

    public List<Movie> getFilmes_similar() {
        return filmes_similar;
    }
}
