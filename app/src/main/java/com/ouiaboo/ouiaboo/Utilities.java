package com.ouiaboo.ouiaboo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListAdapter;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vareta on 27-07-2015.
 */
public class Utilities {
    public static final String PREFERENCIAS = "preferencias";
    public static final int ANIMEFLV = 0;
    public static final int ANIMEJOY = 1;



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
            URL urlPagina;
            for (String url : urls) {

                try {
                    urlPagina = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection)urlPagina.openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6");
                    connection.setRequestMethod("GET");
                    connection.connect();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "ISO-8859-1"));

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
                    reader.close(); // Close the stream
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return lista;
        }
    }

        /*@Override
        protected void onPostExecute(List<String> result) {
            textView.setText(result);
        }*/


    public List<String> downloadWebPageTaskNoAsync (String url) {

        List<String> lista = new ArrayList<String>();
        URL urlPagina;
        try {
            urlPagina = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)urlPagina.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6");
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "ISO-8859-1"));
            // StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) { // Read line by line
                // sb.append(line);
               // Log.d("STRING", line);
                lista.add(line);
                //lista.add(line); // Result is here
                //  sb = null;
                // sb.delete(0,0);
            }
            reader.close(); // Close the stream
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;

    }

    public Document connect(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .followRedirects(true)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    public HashMap<String, String> cookieToHashmap(String cookies) {
        HashMap<String, String> respuesta = new HashMap<>();

        String[] cookieAux = cookies.split(" ");
        for (int i = 0; i < cookieAux.length; i++) {
            if (cookieAux[i].equals("__test;")) {
                continue;
            } else {
                String[] cookiesAux2 = cookieAux[i].split("=");
                if (cookiesAux2[0].equals("_ga") || cookiesAux2[0].equals("dev")) {
                    continue;
                } else {
                    respuesta.put(cookiesAux2[0], cookiesAux2[1]);
                }
            }

            Log.d("Cokies", cookieAux[i]);
        }
        respuesta.put("dev", "1");

        for (Map.Entry<String,String> entry : respuesta.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Log.d("KEY", key);
            Log.d("VALUE", value);
            // do stuff
        }
        return respuesta;
    }

    /*Consulta en las preferencias acerca de cual pagina de anime se esta utilizando
    como proveedor para luego retornar el resultado
     */
    public int queProveedorEs(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        boolean animeflv = preferences.getBoolean("animeflv", true);
        if (animeflv) {
            return ANIMEFLV;
        } else {
            return ANIMEJOY;
        }

    }
    /*http://stackoverflow.com/questions/25805580/how-to-quickly-check-if-url-server-is-available*/
    public boolean isServerReachable(String url, Context context) {
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL(url);
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                urlConn.connect();
                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK || urlConn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL || urlConn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public boolean esReproductorExterno(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);

        return preferences.getBoolean("reproductorExterno", false);
    }

    /*
    http://stackoverflow.com/questions/1309629/how-to-change-colors-of-a-drawable-in-android
     */
    public Drawable cambiarColorIcono(Drawable drawable, int color) {
        Drawable resultado = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(resultado, color);

        return resultado;
    }

    /*Obtiene el ancho del popup window segun los items contenidos dentro de ella*/
    public int measureContentWidth(ListAdapter adapter, Fragment fragment) {
        int maxWidth = 0;
        int count = adapter.getCount();
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        View itemView = null;
        for (int i = 0; i < count; i++) {
            itemView = adapter.getView(i, itemView, ((ViewGroup)fragment.getView().getParent()));
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            maxWidth = Math.max(maxWidth, itemView.getMeasuredWidth());
        }
        return maxWidth;
    }
}
