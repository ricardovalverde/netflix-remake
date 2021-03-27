package com.example.netflix.modelo;

import java.util.List;

public class MovieDetail {
    private final Filme filme;
    private final List<Filme> filmes_similar;

    public MovieDetail(Filme filme, List<Filme> filmes_similar) {
        this.filme = filme;
        this.filmes_similar = filmes_similar;
    }

    public Filme getFilme() {
        return filme;
    }

    public List<Filme> getFilmes_similar() {
        return filmes_similar;
    }
}
