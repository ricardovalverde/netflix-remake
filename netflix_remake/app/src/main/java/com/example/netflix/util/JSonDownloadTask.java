package com.example.netflix.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.netflix.modelo.Categoria;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class JSonDownloadTask extends AsyncTask<String, Void, List<Categoria>> {
    private final Context context;
    ProgressDialog dialog;

    public JSonDownloadTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    @Override
    protected void onPostExecute(List<Categoria> categorias) {
        super.onPostExecute(categorias);
        dialog.dismiss();
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

}


