package com.example.netflix.modelo;

import java.util.List;

public class Categoria {
    private String nome;
    private List<Filme> filmes;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Filme> getFilmes() {
        return filmes;
    }

    public void setFilmes(List<Filme> filmes) {
        this.filmes = filmes;
    }
}
