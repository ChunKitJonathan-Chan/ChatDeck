package com.example.chatdeck.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
// Chun Kit Jonathan Chan

public class mImageLoader extends AsyncTask<Void, Void, Bitmap>{

    // region Field members
    private String url;
    private ImageView imageView;
    // end region

    // region Constructor
    public mImageLoader(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }
    // end region

    // region Parse image url to bitmap
    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // end region

    // region Set image for imageView
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(result,140,140,false));
    }
    // end region
}
