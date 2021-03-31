package com.example.netflix;

import android.content.Intent;
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
import com.example.netflix.util.CategoryTask;
import com.example.netflix.util.ImageDownloaderTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryTask.CategoryLoader {
    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view_da_mainAC);


        List<Categoria> categorias = new ArrayList<>();


        categoryAdapter = new CategoryAdapter(categorias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(categoryAdapter);

        CategoryTask categoryTask = new CategoryTask(this);
        categoryTask.setCategoryLoader(this);
        categoryTask.execute("https://tiagoaguiar.co/api/netflix/home");


    }


    @Override
    public void onResult(List<Categoria> categorias) {
        categoryAdapter.setCategory(categorias);
        categoryAdapter.notifyDataSetChanged();
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


    public interface OnItemClickListener {
        void onClick(int position);
    }

    public static class MovieHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCover;

        public MovieHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.image_view_cover);
            itemView.setOnClickListener(v -> onItemClickListener.onClick(getAdapterPosition()));

        }

    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder> {


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
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.HORIZONTAL, false));

        }

        @Override
        public int getItemCount() {
            return categorias.size();
        }

        void setCategory(List<Categoria> categorias) {
            this.categorias.clear();
            this.categorias.addAll(categorias);


        }
    }

    public class MovieAdapter extends RecyclerView.Adapter<MovieHolder> implements OnItemClickListener {

        private final List<Filme> filmes;


        public MovieAdapter(List<Filme> filmes) {
            this.filmes = filmes;
        }

        @Override
        public void onClick(int position) {
            if (filmes.get(position).getId() <= 3) {
                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                intent.putExtra("id", filmes.get(position).getId());
                startActivity(intent);
            }
        }

        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.movie_item, parent, false);
            return new MovieHolder(view, this);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {

            Filme filme = filmes.get(position);
            new ImageDownloaderTask(holder.imageViewCover).execute(filme.getCoverURL());
        }

        @Override
        public int getItemCount() {
            return filmes.size();
        }


    }


}