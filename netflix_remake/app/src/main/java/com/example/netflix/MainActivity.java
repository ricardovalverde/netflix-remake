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

import com.example.netflix.model.Category;
import com.example.netflix.model.Movie;
import com.example.netflix.util.CategoryTask;
import com.example.netflix.util.ImageDownloaderTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryTask.CategoryLoader {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        recyclerView = findViewById(R.id.recycler_view_da_mainAC);

        List<Category> categories = new ArrayList<>();

        categoryAdapter = new CategoryAdapter(categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(categoryAdapter);

        CategoryTask categoryTask = new CategoryTask(this);
        categoryTask.setCategoryLoader(this);
        categoryTask.execute("https://tiagoaguiar.co/api/netflix/home");
    }


    @Override
    public void onResult(List<Category> categories) {
        categoryAdapter.setCategory(categories);
        categoryAdapter.notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onClick(int position);
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

    public static class MovieHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCover;

        public MovieHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.image_view_cover);
            itemView.setOnClickListener(v -> onItemClickListener.onClick(getAdapterPosition()));
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder> {

        private final List<Category> categories;

        public CategoryAdapter(List<Category> categories) {
            this.categories = categories;
        }

        @NonNull
        @Override
        public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CategoryHolder(getLayoutInflater().inflate(R.layout.category_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
            Category category = categories.get(position);

            holder.textView.setText(category.getName());
            holder.recyclerView.setAdapter(new MovieAdapter(category.getFilms()));
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.HORIZONTAL, false));
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        void setCategory(List<Category> categories) {
            this.categories.clear();
            this.categories.addAll(categories);
        }
    }

    public class MovieAdapter extends RecyclerView.Adapter<MovieHolder> implements OnItemClickListener {

        private final List<Movie> movies;

        public MovieAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        @Override
        public void onClick(int position) {
            if (movies.get(position).getId() <= 3) {
                Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                intent.putExtra("id", movies.get(position).getId());
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

            Movie movie = movies.get(position);
            new ImageDownloaderTask(holder.imageViewCover).execute(movie.getCoverURL());
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }
    }
}