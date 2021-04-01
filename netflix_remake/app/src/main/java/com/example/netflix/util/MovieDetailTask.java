package com.example.netflix.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.netflix.modelo.Filme;
import com.example.netflix.modelo.MovieDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MovieDetailTask extends AsyncTask<String, Void, MovieDetail> {

    private final WeakReference<Context> context;
    private ProgressDialog dialog;
    private MovieDetailLoader movieDetailLoader;


    public MovieDetailTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    public void setMovieDetailLoader(MovieDetailLoader movieDetailLoader) {
        this.movieDetailLoader = movieDetailLoader;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = this.context.get();
        if (context != null)
            dialog = ProgressDialog.show(context, "Carregando", "", true);

    }

    @Override
    protected MovieDetail doInBackground(String... params) {
        String url_p = params[0];
        try {
            URL requestUrl = new URL(url_p);

            HttpsURLConnection urlConnection = (HttpsURLConnection) requestUrl.openConnection();
            urlConnection.setReadTimeout(2000);
            urlConnection.setConnectTimeout(2000);

            int codeResponse = urlConnection.getResponseCode();
            if (codeResponse > 400) {
                throw new IOException("Erro na conexão com servidor");
            }
            InputStream inputStream = urlConnection.getInputStream();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            String jsonAsString = toString(bufferedInputStream);

            MovieDetail movieDetail = getMovieDetail(new JSONObject(jsonAsString));

            bufferedInputStream.close();

            return movieDetail;

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MovieDetail getMovieDetail(JSONObject json) throws JSONException {

        int id = json.getInt("id");
        String title = json.getString("title");
        String desc = json.getString("desc");
        String cast = json.getString("cast");
        String coverUrl = json.getString("cover_url");

        List<Filme> movies = new ArrayList<>();
        JSONArray movieArray = json.getJSONArray("movie");
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movie = movieArray.getJSONObject(i);
            String c = movie.getString("cover_url");
            int idSimilar = movie.getInt("id");

            Filme similar = new Filme();
            similar.setId(idSimilar);
            similar.setCoverURL(c);

            movies.add(similar);
        }

        Filme movie = new Filme();
        movie.setId(id);
        movie.setCoverURL(coverUrl);
        movie.setTitulo(title);
        movie.setDesc(desc);
        movie.setElenco(cast);

        return new MovieDetail(movie, movies);
    }


    @Override
    protected void onPostExecute(MovieDetail movieDetail) {
        super.onPostExecute(movieDetail);
        dialog.dismiss();
        if (movieDetailLoader != null) {
            movieDetailLoader.onResult(movieDetail);
        }
    }

    private String toString(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int byteslidos;
        while ((byteslidos = inputStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, byteslidos);
        }

        return new String(byteArrayOutputStream.toByteArray());


    }

    public interface MovieDetailLoader {
        void onResult(MovieDetail movieDetail);
    }
}