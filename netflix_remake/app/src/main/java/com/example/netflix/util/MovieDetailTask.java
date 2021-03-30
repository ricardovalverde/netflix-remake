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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MovieDetailTask extends AsyncTask<String, Void, MovieDetail> {

    private final WeakReference<Context> context;
    private ProgressDialog dialog;
    private MovieDetailLoader MovieDetailLoader;


    public MovieDetailTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    public void setMovieDetailLoader(MovieDetailLoader movieDetailLoader) {
        this.MovieDetailLoader = movieDetailLoader;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = this.context.get();
        dialog = ProgressDialog.show(context, "Carregando", "", true);

    }

    @Override
    protected MovieDetail doInBackground(String... params) {
        String url_p = params[0];
        try {
            URL url = new URL(url_p);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MovieDetail getMovieDetail(JSONObject jsonObject) throws JSONException {

        int id = jsonObject.getInt("id");
        String titulo = jsonObject.getString("title");
        String elenco = jsonObject.getString("cast");
        String desc = jsonObject.getString("desc");
        String cover = jsonObject.getString("cover_url");

        List<Filme> filmes = new ArrayList<>();
        JSONArray filmes_similar = jsonObject.getJSONArray("movie");
        for (int i = 0; i < filmes_similar.length(); i++) {

            JSONObject movie = filmes_similar.getJSONObject(i);
            String cover_url = movie.getString("cover_url");
            int idSimilar = movie.getInt("id");

            Filme similiar = new Filme();
            similiar.setId(idSimilar);
            similiar.setCoverURL(cover_url);
            filmes.add(similiar);
        }
        Filme filme = new Filme();
        filme.setTitulo(titulo);
        filme.setCoverURL(cover);
        filme.setDesc(desc);
        filme.setElenco(elenco);
        return new MovieDetail(filme, filmes);

    }


    @Override
    protected void onPostExecute(MovieDetail movieDetail) {
        super.onPostExecute(movieDetail);
        dialog.dismiss();
        if (MovieDetailLoader != null) MovieDetailLoader.onResult(movieDetail);
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