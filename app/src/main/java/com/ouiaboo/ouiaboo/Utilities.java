package com.ouiaboo.ouiaboo;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.ListAdapter;


import com.ouiaboo.ouiaboo.Tables.DescargadosTable;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;
import com.ouiaboo.ouiaboo.util.CRUD;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
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

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    public static final String FRAGMENT_COMPARTIR = "compartir";
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
            String line;
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
        return lista;

    }

    public String getDataFromUrl (String url) {

        String lista;
        StringBuilder result = new StringBuilder();
        URL urlPagina;
        try {

            urlPagina = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)urlPagina.openConnection();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        lista = result.toString();
        return lista;

    }

    public Document connect(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .followRedirects(true)
                    .ignoreHttpErrors(true) //para ignorar el error 503 para cuando se usa cloudflare
                    .get();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    /**
     * Se Conecta con la pagina web y devuelve el codigo fuente en un Document Jsoup.
     * Esta es ocupada para cuando cloudflare esta activado ya que requiere cookies
     * @param url Url a conectar
     * @param cookies Cookies
     * @return Document jsoup que contiene el codigo fuente
     */
    public Document connect(String url, String cookies) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .followRedirects(true)
                    .ignoreHttpErrors(true) //para ignorar el error 503 para cuando se usa cloudflare
                    .cookies(cookieToHashmap(cookies))
                    .get();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    /**
     * Recibe las cookies via string y las almacena en un hashmap para poder utilizarlas con jsoup
     * @param cookies String que contiene las cookies
     * @return
     */
    public HashMap<String, String> cookieToHashmap(String cookies) {
        final HashMap<String, String> respuesta = new HashMap<>();

        String[] cookieAux = cookies.split(";");
        for (int i = 0; i < cookieAux.length; i++) {
            String[] cookiesAux2 = cookieAux[i].split("=");
            if (cookiesAux2.length == 2) {
                respuesta.put(cookiesAux2[0].trim(), cookiesAux2[1].trim());
            }
        }
        /*for (Map.Entry<String,String> entry : respuesta.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Log.d("KEY", key);
            Log.d("VALUE", value);
        }*/
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

    /**
     * Setea el valor de las cookies (boolean)
     * @param hayCookies Boolean
     * @param context
     */
    public void setCookiesBoolean(boolean hayCookies, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE).edit();
        editor.putBoolean("hayCookies", hayCookies);
        editor.apply();
    }

    /**
     * Consulta en las preferencias si acaso existen cookies
     * @param context
     * @return Boolean con la respuesta
     */
    public boolean existenCookies(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);

        return preferences.getBoolean("hayCookies", true);
    }

    /**
     * Setea las cookies en las preferencias de la aplicacion
     * @param cookies String con las cookies
     */
    public void setCookiesEnSharedPreferences(String cookies, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE).edit();
        editor.putString("cookies", cookies);
        editor.apply();
    }

    /**
     * Obtiene las cookies en las preferencias de la aplicacion
     * @param context
     */
    public String getCookiesEnSharedPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);

        return preferences.getString("cookies", "cookies");
    }



    /*Consulta en las preferencias si acaso el proveedor fue modifcado, en caso de haber sido modificado vuelve el valor
    a falso, ya que este al momento de ser consultado verdadero, debe ser cambiado a falso inmediatamente
         */
    public boolean proveedorModificado(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        if (preferences.getBoolean("proveedorModificado", false)) {
            SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE).edit();
            editor.putBoolean("proveedorModificado", false);
            editor.apply();
            return true;
        } else {
            return false;
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
                urlConn.setConnectTimeout(20000); //<- 3Seconds Timeout
                urlConn.connect();
                int status = urlConn.getResponseCode();
                if (queProveedorEs(context) == Utilities.ANIMEFLV) {
                    Log.d("CODIGO", String.valueOf(status));
                    if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_MOVED_TEMP) { //servidor izanagi animeflv
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    Log.d("CODIGO", String.valueOf(status));
                    if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_PARTIAL || status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_NOT_MODIFIED) { //servidor izanagi animeflv
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


    /**
     * Cliente que utiliza cookies para cuando es requerido por Picasso, es decir, para cuando animeflv tiene activado
     * cloudflare.
     * Utiliza las cookies que se almacenan en el CookieManager
     * @return Cliente OkHttpClient con cookies
     */
    public OkHttpClient cookiesClient (Context context) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        final Request original = chain.request();

                        final Request authorized = original.newBuilder()
                                .addHeader("Cookie", CookieManager.getInstance().getCookie("https://animeflv.net/"))
                                .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                                .build();

                        return chain.proceed(authorized);
                    }
                })
                .cache(new Cache(context.getCacheDir(), 25 * 1024 * 1024))
                .build();

        return client;
    }

    /**
     * Checkea y, de ser necesario, cambia el estatus a completado si la descarga ocurrio con exite, ó
     * la elimina, en caso que la descarga haya fallado
     * @param context Context de la actividad
     * @param id long ID del elemento en la tabla
     * @param idDescarga long ID de la descarga
     */
    public void checkAndUpdateDownloadStatus(Context context, long id, long idDescarga) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor cursor = manager.query(new DownloadManager.Query().setFilterById(idDescarga));

        if (cursor.moveToFirst()) {
            //column for download  status
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            //column for reason code if the download failed or paused
            /*int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
             int reason = cursor.getInt(columnReason);*/

            CRUD crud = new CRUD();
            if (status == DownloadManager.STATUS_FAILED) {
                System.out.println("download failed");
                crud.removeDownload(id); //remueve la descarga de la lista de descargas de la aplicacion
            }
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                System.out.println("download exito");
                crud.updateEstado(id, true); //cambia el estado de la descarga a completado
            }
        }

        cursor.close();
    }

    /**
     * Crea el thumbnail para los videos descardos exitosamente
     * @param video Elemento que contiene los atributos del video
     * @param direccion Direccion en disco en donde se guardara el thumbnail
     */
    public void añadirThumbnail(DescargadosTable video, File direccion) {
        CRUD crud = new CRUD();
        Bitmap preview = ThumbnailUtils.createVideoThumbnail(video.getDirVideo(), MediaStore.Images.Thumbnails.MINI_KIND);
        File nombreImg = new File(direccion, video.getNombre());
        try {
            FileOutputStream fos = new FileOutputStream(nombreImg + ".jpg");
            preview.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            crud.updatePreview(video.getId(), nombreImg.getAbsolutePath() + ".jpg");
            Log.d(video.getNombre() , "thumbnailañadida");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Descarga el capitulo seleccionado via DownloadManager y luego registra el inicio de la descarga
     * en la base de datos
     * @param context Contexto de la actividad
     * @param capitulo Objeto HomeScreenEpi que contiene los datos del capitulo a descargar
     * @param urlVideo Url del capitulo, es decir, url directa del video
     */
    public void descargarCapitulo(Context context, HomeScreenEpi capitulo, String urlVideo) {
        String numeroCapitulo = capitulo.getInformacion();
        String nombreAnime = capitulo.getNombre();
        String urlCapitulo = capitulo.getUrlCapitulo();
        String nombreVideo = nombreAnime + "-" + numeroCapitulo + ".mp4"; //nombre que se le dara al archivo descargado

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlVideo));
        request.setDescription(numeroCapitulo); //descripcion de la notificacion (numero de capitulo)
        request.setTitle(nombreAnime); //titulo de la notificacion (nombre anime)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //setea las notificaciones
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES + "/Ouiaboo", nombreVideo);
        request.setMimeType("video/x-msvideo");

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long id = manager.enqueue(request);

        CRUD crud = new CRUD();
        crud.registraInicioDescarga(id, nombreAnime, numeroCapitulo, nombreVideo, urlCapitulo);
    }


}
