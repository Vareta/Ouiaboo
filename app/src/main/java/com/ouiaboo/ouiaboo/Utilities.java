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
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListAdapter;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
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
    public static final int REYANIME = 1;
    public static final String URL_APP_UPDATE = "https://aterav.wordpress.com/perro-zapato-camion-corbata/";
    public static final String URL_APP_DESCARGA = "https://ouiaboo.wordpress.com/descarga/";
    public static final String URL_REPORTE_ERRORES = "https://ouiaboo.wordpress.com/reporte-de-errores/";
    public static final String FRAGMENT_HOMESCREEN = "homescreen";
    public static final String FRAGMENT_VERMASTARDE = "vermastarde";
    public static final String FRAGMENT_FAVORITOS = "favoritos";
    public static final String FRAGMENT_DESCARGADAS = "descargadas";
    public static final String FRAGMENT_HISTORIAL = "historial";
    public static final String FRAGMENT_GENEROS = "generos";
    public static final String FRAGMENT_PREFERENCIAS = "preferencias";
    public static final String FRAGMENT_FAQ = "faq";
    public static final String FRAGMENT_BUSQUEDA = "busqueda";
    public static final String FRAGMENT_GENEROSCONTENIDO = "generoscontenido";



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
            return REYANIME;
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
                urlConn.setConnectTimeout(10000); //<- 3Seconds Timeout
                urlConn.connect();
                //no considera el http 200, ya que si bien el url funciona, no existe el archivo de video por lo cual no se puede reproducir
                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL || urlConn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                    return true;
                } else {
                    if (url.contains("subidas") && urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) { //si es AFLV como servidor
                        return true;
                    } else {
                        return false;
                    }

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

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            maxWidth = (int) (maxWidth * 1.5);
        }

        return maxWidth;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /*mover archivo*/
    public void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public void copyFile(File src, File dst) throws IOException
    {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try
        {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally
        {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    public void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + "/" + inputFile);
            out = new FileOutputStream(outputPath + "/" + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    /* Obtiene la version del codigo y el enlace de descarga desde el codigo fuente*/
    public List<String> obtenerEnlaceActualizacion(Document codigoFuente) {
        List<String> resultado = new ArrayList<>();

        Element objEnlace = codigoFuente.getElementsByClass("entry-content").first();
        String contenido = objEnlace.select("p").first().text();
        String[] aux = contenido.split(" ");
        String version = aux[0], enlace = aux[1];
        resultado.add(version);
        resultado.add(enlace);

        return resultado;
    }

}
