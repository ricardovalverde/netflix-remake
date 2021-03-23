package com.example.netflix;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix.modelo.Categoria;
import com.example.netflix.modelo.Filme;
import com.example.netflix.util.JSonDownloadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view_da_mainAC);
        CategoryAdapter categoryAdapter;


        int contador = 0;
        List<Categoria> categoriaArrayList = new ArrayList<>();
        List<Filme> filmes = new ArrayList<>();

        while(contador < 10){


            Categoria categoria = new Categoria();
            categoria.setNome("CATEGORIA " + contador);



            Filme filme = new Filme();
            //filme.setCoverURL(R.drawable.movie);
            filmes.add(filme);


            categoria.setFilmes(filmes);
            categoriaArrayList.add(categoria);

            contador++;
        }


        categoryAdapter = new CategoryAdapter(categoriaArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(categoryAdapter);
        new JSonDownloadTask(this).execute("https://tiagoaguiar.co/api/netflix/home");



    }

    private static class CategoryHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RecyclerView recyclerView;
        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview_category_item);
            recyclerView = itemView.findViewById(R.id.recyclerview_category_item);

        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder>{


       private final List<Categoria> categorias;


        public CategoryAdapter(List<Categoria> categorias) {
            this.categorias = categorias;

        }

        @NonNull
        @Override
        public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CategoryHolder(getLayoutInflater().inflate(R.layout.category_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
            Categoria categoria = categorias.get(position);

            holder.textView.setText(categoria.getNome());
            holder.recyclerView.setAdapter(new MovieAdapter(categoria.getFilmes()));
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(),RecyclerView.HORIZONTAL,false));

        }

        @Override
        public int getItemCount() {
            return categorias.size();
        }
    }





    public static class MovieHolder extends RecyclerView.ViewHolder{
        ImageView imageViewCover;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            //imageViewCover = itemView.findViewById(R.id.image_view_cover);
        }
    }
    public class MovieAdapter extends RecyclerView.Adapter<MovieHolder>{

        private final List<Filme> list_filmes;


        public MovieAdapter(List<Filme> list_filmes) {
            this.list_filmes = list_filmes;
        }

        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MovieHolder(getLayoutInflater().inflate(R.layout.movie_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        Filme filme = list_filmes.get(position);
        //holder.imageViewCover.setImageResource(filme.getCoverURL());
        }

        @Override
        public int getItemCount() {
            return list_filmes.size();
        }
    }


}