package com.example.netflix;

import android.graphics.Movie;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix.modelo.Filme;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MovieDetailsActivity extends AppCompatActivity {
    private TextView txtSinopse, txtElenco, txtTitulo;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        txtElenco = findViewById(R.id.textview_elenco);
        txtSinopse = findViewById(R.id.textview_descricao);
        txtTitulo = findViewById(R.id.textview_titulofilme);
        recyclerView = findViewById(R.id.recyclerview_similar);

        List<Filme> filmes = new ArrayList<>();
        for (int i = 0; i < 30 ; i++) {
            Filme filme = new Filme();
            filmes.add(filme);
        }

        recyclerView.setAdapter(new MovieAdapter(filmes));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));




        Toolbar toolbar = findViewById(R.id.toolbar_details_movie);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        }

        txtTitulo.setText("Batman Begins");
        txtSinopse.setText("O jovem Bruce Wayne viaja para o Extremo Oriente, onde recebe treinamento em artes marciais do mestre Henri Ducard, um membro da misteriosa Liga das Sombras. Quando Ducard revela que a verdadeira proposta da Liga é a destruição completa da cidade de Gotham, Wayne retorna à sua cidade com o intuito de livrá-la de criminosos e assassinos. Com a ajuda do mordomo Alfred e do expert Lucius Fox, nasce Batman.");
        txtElenco.setText(getString(R.string.elenco,"Christian Bale, "+
                "Cillian Murphy, "+
                "Gary Oldman, "+
                "Katie Holmes, "+
                "Liam Neeson" ));


    }

    private static class MovieHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_cover);
        }

    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder>{

        List<Filme> listfilmes;

        public MovieAdapter(List<Filme> listfilmes) {
            this.listfilmes = listfilmes;
        }

        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MovieHolder(getLayoutInflater().inflate(R.layout.movie_item_similar,parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        Filme filme = listfilmes.get(position);
        }

        @Override
        public int getItemCount() {
            return listfilmes.size();
        }
    }






}

