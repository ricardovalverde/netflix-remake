package com.example.netflix.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.example.netflix.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;


public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewWeakReference;
    private boolean shadows;


    public ImageDownloaderTask(ImageView imageView) {
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    public void setShadows(boolean shadows) {
        this.shadows = shadows;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String url_s = params[0];
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(url_s);
            urlConnection = (HttpURLConnection) url.openConnection();
            int coderesponse = urlConnection.getResponseCode();
            if (coderesponse != 200) return null;

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                return BitmapFactory.decodeStream(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return null;
    }


    protected void onPostExecute(Bitmap bitmap) {

        if (isCancelled()) {
            bitmap = null;
        }

        ImageView imageView = imageViewWeakReference.get();

        if (imageView != null && bitmap != null) {
            if (shadows) {
                LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(imageView.getContext(), R.drawable.shadow);

                if (layerDrawable != null) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);

                    layerDrawable.setDrawableByLayerId(R.id.cover_drawble, bitmapDrawable);
                    imageView.setImageDrawable(layerDrawable);
                }
            } else {
                if (((bitmap.getWidth()) < (imageView.getWidth())) || ((bitmap.getHeight()) < (imageView.getHeight()))) {
                    Matrix matrix = new Matrix();
                    matrix.postScale((float) imageView.getWidth() / (float) bitmap.getWidth(), (float) imageView.getHeight() / (float) bitmap.getHeight());
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                }
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
