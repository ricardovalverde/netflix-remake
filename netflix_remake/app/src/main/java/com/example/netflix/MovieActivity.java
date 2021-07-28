package com.example.netflix;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix.model.Movie;
import com.example.netflix.model.MovieDetail;
import com.example.netflix.util.ImageDownloaderTask;
import com.example.netflix.util.MovieDetailTask;

import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity implements MovieDetailTask.MovieDetailLoader {
    private TextView txtDesc, txtElenco, txtTitulo;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ImageView imgCover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setTitle(null);
        }

        txtElenco = findViewById(R.id.textview_elenco);
        txtDesc = findViewById(R.id.textview_descricao);
        txtTitulo = findViewById(R.id.textview_titulofilme);
        recyclerView = findViewById(R.id.recyclerview_similar);
        imgCover = findViewById(R.id.img_movie_details);

        List<Movie> movies = new ArrayList<>();

        movieAdapter = new MovieAdapter(movies);

        recyclerView.setAdapter(movieAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt("id");
            MovieDetailTask movieDetailTask = new MovieDetailTask(this);
            movieDetailTask.setMovieDetailLoader(this);
            movieDetailTask.execute("https://tiagoaguiar.co/api/netflix/" + id);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(MovieDetail movieDetail) {

        txtTitulo.setText(movieDetail.getFilme().getTitle());
        txtDesc.setText(movieDetail.getFilme().getDesc());
        txtElenco.setText(movieDetail.getFilme().getCast());

        ImageDownloaderTask imageDownloaderTask = new ImageDownloaderTask(imgCover);
        imageDownloaderTask.setShadows(true);
        imageDownloaderTask.execute(movieDetail.getFilme().getCoverURL());

        movieAdapter.setFilmes(movieDetail.getFilmes_similar());
        movieAdapter.notifyDataSetChanged();
    }

    private static class MovieHolder extends RecyclerView.ViewHolder {
        final ImageView imageViewCover;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.image_view_cover);
        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {

        private final List<Movie> movies;

        public MovieAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        public void setFilmes(List<Movie> movies) {
            this.movies.clear();
            this.movies.addAll(movies);
        }

        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MovieHolder(getLayoutInflater().inflate(R.layout.movie_item_similar, parent, false));
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

