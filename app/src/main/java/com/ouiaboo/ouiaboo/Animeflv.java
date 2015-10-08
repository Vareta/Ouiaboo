package com.ouiaboo.ouiaboo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.ouiaboo.ouiaboo.clases.Episodios;
import com.ouiaboo.ouiaboo.clases.HomeScreenAnimeFLV;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ouiaboo.ouiaboo.R.string;

/**
 * Created by Vareta on 29-07-2015.
 */
public class Animeflv{
    Resources resources;


    public Animeflv(Resources resources){
        this.resources = resources;
    }
    private String animeflv = "http://animeflv.net/"; //sitio web


    public ArrayList<HomeScreenAnimeFLV> homeScreenAnimeflv(List<String> codigoFuente){

        int max = codigoFuente.size();
        //String que contiene el indicador que en lo que resta del codigo fuente se encuentran los animes recientes
        String condUltimosCap = "<div class=\"ultimos_epis\">";
        //String que contiene el indicador que indica el fin de los capitulos del dia
        String condFinUltimosCap = "<h1>Episodios mas vistos ayer</h1>";
        //String que contiene el indicador del titulo, que otorga el titulo, numero de episodio
        String condTitulo = "<div class=\"not\">";
        //String que contiene el indicador de la imagen del episodios o serie
        String condImagen = "<img class=\"imglstsr lazy\"";
        //String que contiene el indicador de si es una pelicula
        String condPelicula = "<div class=\"tpeli\">";
        //String que contiene el indicador de si es un OVA
        String condOva = "<div class=\"tova\">";
        String apoyo; //string de apoyo
        Boolean ultimo = false; //para verificar si se termina la seccion de codigo de los animes recientes

        String urlCapitulo = null;
        String nombre = null;
        String nombreAux = null;
        String informacion = null;
        String preview = null;

        ArrayList<HomeScreenAnimeFLV> home = new ArrayList<HomeScreenAnimeFLV>();

        for (int i = 0; i < max && !ultimo; i++) {
            if (codigoFuente.get(i).contains(condUltimosCap)){//seccion de ultimos capitulos
               // int j = i + 1;
                for (int j = i + 1; j < max; j++) {

                    if ((urlCapitulo != null) && (nombre != null) && (informacion != null) && (preview != null)){ //para no agregar capitulos repetidos
                        HomeScreenAnimeFLV item = new HomeScreenAnimeFLV(urlCapitulo, nombre, informacion, preview);
                        /*Log.d("URL11  ", urlCapitulo);
                        Log.d("Nombre11  ", nombre);
                        Log.d("Informacion11  ", informacion);
                        Log.d("Preview11  ", preview);*/
                        home.add(item); //agrega el item al arreglo de elementos de la pagina inicial de animeflv
                        urlCapitulo = null;
                        nombre = null;
                        informacion = null;
                        preview = null;
                    }
                    apoyo = codigoFuente.get(j);
                    if (apoyo.contains(condFinUltimosCap)) { //condicion de final de ultimos capitulos
                        ultimo = true; //asi sale de los dos ciclos for
                        break; //rompe todo el ciclo
                    } else {
                        if (apoyo.contains(condTitulo)) { //condicion de titulo

                            urlCapitulo = returnUrlEpisodio(apoyo);
                            nombreAux = returnTitulo(apoyo);
                            String aux = "";
                            String[] div = nombreAux.split(" ");
                            for (int k = 0; k < div.length - 1; k++) { // consigue sólo el nombre
                                aux = aux + div[k] + " ";
                            }
                            nombre = aux; //agrega el nombre de la serie

                            if (apoyo.contains(condPelicula)){//es pelicula
                                informacion = "Película";
                            } else {
                                if (apoyo.contains(condOva)) {//es OVA
                                    informacion = "OVA";
                                } else {//es un episodio
                                    informacion = resources.getString(R.string.numero_episodio_menu_central_ES) + " " + div[div.length - 1];
                                }
                            }
                        } else {
                            if (apoyo.contains(condImagen)){
                                preview = returnPreview(apoyo);
                            } else {
                                continue;
                            }
                        }

                    }



                }
                //Log.d("ANIMEFLV", codigoFuente.get(i));
            }
        }
        return home;

    }

    public String returnUrlEpisodio(String linea){
        String url = "";

        String[] div = linea.split("\"");
        url = animeflv + div[div.length - 4];
       // Log.d("LINEA:  ", titulo);
        return url;
    }

    public String returnTitulo(String linea){
        String titulo = "";

        String[] div = linea.split("\"");
        titulo = div[div.length - 2];
        //Log.d("TITULO ", titulo);
        return titulo;
    }

    public String returnPreview(String linea){
        String preview = "";

        String[] div = linea.split("\"");
        preview = div[div.length - 4];
        //Log.d("TITULO ", titulo);
        return preview;
    }

    public String urlVideo(String paginaEpisodio){
        String url = "";
        String auxUrl = "";
        List<String> paginaWeb = null;


        Utilities.DownloadWebPageTask task = new Utilities.DownloadWebPageTask();
        task.execute(new String[]{paginaEpisodio});
        try {
            paginaWeb = task.get();
            int max = paginaWeb.size();

            for (int i = 0; i < max; i++) {
                if (paginaWeb.get(i).contains("var videos")) {
                    Matcher localMatcher = Pattern.compile("hyperion.php\\?key=(.*?)&provider").matcher(paginaWeb.get(i));
                    //Log.d("URL  ", paginaWeb.get(i));
                    while (localMatcher.find()) {
                        auxUrl = localMatcher.group(1);
                        //System.out.println(aux);
                    }

                }
            }
            String[] aux = auxUrl.split("25"); //se quita el 25 de la url
            for (int m = 0; m < aux.length; m++){
                if (m == 0){
                    auxUrl = aux[m];
                } else {
                    auxUrl = auxUrl + aux[m];
                }
            }
            //Log.d("WEB", auxUrl);
            url = "http://animeflv.net/video/hyperion.php?key=" + auxUrl;
           // url = "http://animejoy.tv/video/sore-ga-seiyuu/005.mp4"; //para trabajar mientras animeflv esta caido
           // Log.d("WEB", url);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return url;
    }

  /*  private void crawl(String url) throws IOException {

        Response response = Jsoup.connect(url).followRedirects(false).execute();

        System.out.println("hola   " + response.statusCode() + " : " + url);

        if (response.hasHeader("location")) {
            String redirectUrl = response.header("location");
            crawl(redirectUrl);
        }

    }

    public void repeat(){
        String url2 = "http://www.animeid.moe/";
        try {
            crawl(url2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


    public ArrayList<HomeScreenAnimeFLV> busquedaFLV(String url){
        String pelicula = "tipo_2"; //pelicula
        String ova = "tipo_1"; //OVA
        String serie = "tipo_0"; //serie de anime
        Document doc = null;
        ArrayList<HomeScreenAnimeFLV> search = new ArrayList<HomeScreenAnimeFLV>();
        String urlAnime;
        String nombre;
        String informacion;
        String informacionAux;
        String preview;
        Map<String, String> cookyes = new HashMap<String, String>();


       // repeat();
        try {

            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .followRedirects(true)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Elements objetosEpi;
        if (doc != null) {
            objetosEpi = doc.getElementsByClass("aboxy_lista");
            if (objetosEpi.isEmpty()){ // si no contiene la clase, es decir la busqueda no produjo resultados
                search = null;
                Log.d("Empty", "busqueda no produce resultados");
            } else {
                Element dirAnime; //primer elemento del objeto aboxy_lista(i) que contiene el nombre, link e imagen del anime
                Element dirAnimeTipo; //elemento que contiene la informacion acerca si es pelicula, ova o serie
                for (int i = 0; i < objetosEpi.size(); i++) {
                    dirAnime = objetosEpi.get(i).select("a").first();
                    urlAnime = "http://animeflv.net" + dirAnime.attr("href");
                    nombre = dirAnime.attr("title");
                    preview = dirAnime.select("img").attr("data-original"); //accede a img, ya que <a> contiene un <img> en su interior. Ej: <a <img /> </a>
                    dirAnimeTipo = objetosEpi.get(i).select("span").first();
                    informacionAux = dirAnimeTipo.attr("class");
                    if (informacionAux.equals(pelicula)) { //es pelicula?
                        informacion = "Pelicula";
                    } else {
                        if (informacionAux.equals(ova)) { //es OVA?
                            informacion = "OVA";
                        } else { // es serie
                            informacion = "Serie de anime";
                        }
                    }
                   // System.out.println("url  : " + urlAnime);
                   // System.out.println("Nombre  : " + nombre);
                   // System.out.println("Imagen  : " + preview);
                   // System.out.println("Tipo  : " + informacion);

                    HomeScreenAnimeFLV item = new HomeScreenAnimeFLV(urlAnime, nombre, informacion, preview); //crea un item tipo homescreen y le añade los valores
                    search.add(item); //agrega el item a la lista de items
                }
            }
        } else {
            search = null;
            Log.d("ERROR", "doc.getElementsByClass(\"aboxy_lista\"); es nulo");
        }

        return search;
    }

    public ArrayList<Episodios> getEpisodios(String url) {
        String urlEp;
        String numero;
        String urlImagen = null;
        String informacion = null;
        String tipo = "";
        String estado = "";
        String generos = "";
        String fechaInicio = "";
        ArrayList<Episodios> capitulos = new ArrayList<Episodios>();
        Document doc = null;

        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements objEpisodios;
        if (doc != null) {
            objEpisodios = doc.getElementsByClass("anime_episodios"); //clase que contiene los capitulos
            if (objEpisodios.isEmpty()) {
                Log.d("Error", "Episodios inexistentes (null)");
            } else {


                /********      SLECCION DE EPISODIOS  **************/
                Elements epiIndividuales = objEpisodios.select("ul");
                Elements episodiosFocus = epiIndividuales.get(0).select("a");
                for (int i = 0; i < episodiosFocus.size(); i++) {
                    Episodios epi;
                    if (i == 0) {
                        Element sinopsis = doc.getElementsByClass("sinopsis").first();
                        informacion = sinopsis.text();
                        Element img = doc.getElementsByClass("portada").first();
                        urlImagen = img.attr("src");
                        Element info = doc.getElementsByClass("ainfo").first();
                        Elements infoDetallada = info.select("li");
                        // se hace de esta manera, debido a que existen casos en que ciertos argumentos (que son 4) no aparecen por lo que se genera un error
                        for (int j = 0; j < infoDetallada.size(); j++) {
                           // System.out.println("Hola   " + infoDetallada.get(j).select("b").first().toString());
                            if (infoDetallada.get(j).select("b").first().toString().equals("<b>Tipo:</b>")) {
                                tipo = infoDetallada.get(j).text();
                            }
                            if (infoDetallada.get(j).select("b").first().toString().equals("<b>Estado:</b>")) {
                                estado = infoDetallada.get(j).text();
                            }
                            if (infoDetallada.get(j).select("b").first().toString().equals("<b>Generos:</b>")) {
                                generos = infoDetallada.get(j).text();
                            }
                            if (infoDetallada.get(j).select("b").first().toString().equals("<b>Fecha de Inicio:</b>")) {
                                fechaInicio = infoDetallada.get(j).text();
                            }
                        }

                        urlEp = "http://animeflv.net" + episodiosFocus.get(i).attr("href");
                        numero = numeroEpisodio(episodiosFocus.get(i).text());
                        epi = new Episodios(urlEp, numero, urlImagen, informacion, tipo, estado, generos, fechaInicio);

                    } else {
                        urlEp = "http://animeflv.net" + episodiosFocus.get(i).attr("href");
                        numero = numeroEpisodio(episodiosFocus.get(i).text());
                        epi = new Episodios(urlEp, numero, null, null, null, null, null, null);
                    }
                    /*System.out.println("url  " + urlEp);
                    System.out.println("numero  " + numero);
                    System.out.println("portada  " + urlImagen);
                    System.out.println("info   " + informacion);
                    System.out.println("tipo    " + tipo);
                    System.out.println("Estado   " + estado);
                    System.out.println("generos  " + generos);
                    System.out.println("fechaInicio   " + fechaInicio);*/
                    capitulos.add(epi);
                }

            }
        }

        return capitulos;
    }

    //entrega el valor: Episodio número "x"
    private String numeroEpisodio(String episodio) {
        String epi;

        String[] aux = episodio.split(":");
        String[] aux2 = aux[0].split(" ");
        epi = resources.getString(R.string.numero_episodio_menu_central_ES) + " " + aux2[aux2.length - 1];

        return epi;
    }


}

