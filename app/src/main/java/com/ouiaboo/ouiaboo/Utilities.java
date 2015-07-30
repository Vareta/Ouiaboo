package com.ouiaboo.ouiaboo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vareta on 27-07-2015.
 */
public class Utilities {

    /*insertar una imagen en un ImageView via url
    * source: http://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
    * */
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public static class DownloadWebPageTask extends AsyncTask<String, Void, List> {

        @Override
        protected List doInBackground(String... urls) {

            List<String> lista = new ArrayList<String>();
            for (String url : urls) {
                HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client
                HttpGet httpget = new HttpGet(url); // Set the action you want to do

                try {
                    HttpResponse response = httpclient.execute(httpget); // Executeit
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent(); // Create an InputStream with the response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"), 8);
                   // StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) { // Read line by line
                       // sb.append(line);
                        //Log.d("STRING", line);
                        lista.add(line);
                        //lista.add(line); // Result is here
                      //  sb = null;
                       // sb.delete(0,0);
                    }



                    is.close(); // Close the stream
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return lista;
        }

        /*@Override
        protected void onPostExecute(List<String> result) {
            textView.setText(result);
        }*/
    }



}
