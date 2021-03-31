package com.example.netflix.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.netflix.modelo.Categoria;
import com.example.netflix.modelo.Filme;

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

public class CategoryTask extends AsyncTask<String, Void, List<Categoria>> {
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
    protected List<Categoria> doInBackground(String... params) {
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
            BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
            String jsonAsString = toString(bufferedInputStream);
            List<Categoria> categorias = getCategorias(new JSONObject(jsonAsString));
            bufferedInputStream.close();
            return categorias;


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
    protected void onPostExecute(List<Categoria> categorias) {
        super.onPostExecute(categorias);
        dialog.dismiss();
        if (categoryLoader != null) {
            categoryLoader.onResult(categorias);
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

    private List<Categoria> getCategorias(JSONObject jsonObject) throws JSONException {

        List<Categoria> categorias = new ArrayList<>();

        JSONArray categoriaArray = jsonObject.getJSONArray("category");

        for (int i = 0; i < categoriaArray.length(); i++) {

            JSONObject jsonObj = categoriaArray.getJSONObject(i);

            String titulo = jsonObj.getString("title");

            List<Filme> filmeList = new ArrayList<>();
            JSONArray movieArray = jsonObj.getJSONArray("movie");

            Categoria categoria = new Categoria();
            categoria.setNome(titulo);
            categoria.setFilmes(filmeList);
            categorias.add(categoria);


            for (int j = 0; j < movieArray.length(); j++) {
                JSONObject filme = movieArray.getJSONObject(j);
                String cover_url = filme.getString("cover_url");
                int id = filme.getInt("id");
                Filme filme1 = new Filme();
                filme1.setId(id);
                filme1.setCoverURL(cover_url);
                filmeList.add(filme1);

            }

        }
        return categorias;
    }

    public interface CategoryLoader {
        void onResult(List<Categoria> categorias);
    }

}


