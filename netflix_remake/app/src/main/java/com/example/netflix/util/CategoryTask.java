package com.example.netflix.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.netflix.model.Category;
import com.example.netflix.model.Movie;

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

public class CategoryTask extends AsyncTask<String, Void, List<Category>> {
    private final WeakReference<Context> context;
    private ProgressDialog dialog;
    private CategoryLoader categoryLoader;

    public CategoryTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    public void setCategoryLoader(CategoryLoader categoryLoader) {
        this.categoryLoader = categoryLoader;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = this.context.get();
        dialog = ProgressDialog.show(context, "Carregando", "", true);
    }

    @Override
    protected List<Category> doInBackground(String... params) {

        String url = params[0];

        try {

            URL requestUrl = new URL(url);
            HttpsURLConnection urlConnection = (HttpsURLConnection) requestUrl.openConnection();

            urlConnection.setReadTimeout(2000);
            urlConnection.setConnectTimeout(2000);

            int responseCode = urlConnection.getResponseCode();
            if (responseCode > 400) {
                throw new IOException("Erro na conexão com o servidor");
            }

            InputStream inputStream = urlConnection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            String jsonAsString = toString(bufferedInputStream);
            List<Category> categories = getCategorias(new JSONObject(jsonAsString));

            bufferedInputStream.close();

            return categories;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Category> categories) {
        super.onPostExecute(categories);

        dialog.dismiss();

        if (categoryLoader != null) {
            categoryLoader.onResult(categories);
        }
    }

    private String toString(InputStream inputStream) throws IOException {

        byte[] bytes = new byte[1024];

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int bytes_lidos;

        while ((bytes_lidos = inputStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, bytes_lidos);
        }

        return new String(byteArrayOutputStream.toByteArray());
    }

    private List<Category> getCategorias(JSONObject jsonObject) throws JSONException {

        List<Category> categories = new ArrayList<>();

        JSONArray categoriaArray = jsonObject.getJSONArray("category");

        for (int i = 0; i < categoriaArray.length(); i++) {

            JSONObject jsonObj = categoriaArray.getJSONObject(i);

            String titulo = jsonObj.getString("title");

            List<Movie> movieList = new ArrayList<>();
            JSONArray movieArray = jsonObj.getJSONArray("movie");

            Category category = new Category();
            category.setName(titulo);
            category.setFilms(movieList);
            categories.add(category);


            for (int j = 0; j < movieArray.length(); j++) {
                JSONObject filme = movieArray.getJSONObject(j);
                String cover_url = filme.getString("cover_url");
                int id = filme.getInt("id");

                Movie movie1 = new Movie();
                movie1.setId(id);
                movie1.setCoverURL(cover_url);
                movieList.add(movie1);
            }
        }
        return categories;
    }

    public interface CategoryLoader {
        void onResult(List<Category> categories);
    }
}


