package com.ouiaboo.ouiaboo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ouiaboo.ouiaboo.Tables.animeflv.HistorialFlvTable;
import com.ouiaboo.ouiaboo.Tables.animeflv.HistorialTable;
import com.ouiaboo.ouiaboo.clases.Episodios;
import com.ouiaboo.ouiaboo.clases.GenerosClass;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;
import com.ouiaboo.ouiaboo.fragmentsFLV.HomeScreen;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ouiaboo.ouiaboo.MainActivity.PREFERENCIAS;


/**
 * Created by Vareta on 29-07-2015.
 */
public class Animeflv{
    private String animeflv = "http://animeflv.net"; //sitio web
    private String TAG = "Animeflv";


    public ArrayList<HomeScreenEpi> homeScreenAnimeflv(List<String> codigoFuente, Resources resources){

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

        ArrayList<HomeScreenEpi> home = new ArrayList<HomeScreenEpi>();

        for (int i = 0; i < max && !ultimo; i++) {
            if (codigoFuente.get(i).contains(condUltimosCap)){//seccion de ultimos capitulos
               // int j = i + 1;
                for (int j = i + 1; j < max; j++) {

                    if ((urlCapitulo != null) && (nombre != null) && (informacion != null) && (preview != null)){ //para no agregar capitulos repetidos
                        HomeScreenEpi item = new HomeScreenEpi(urlCapitulo, nombre, informacion, preview);
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

    public String urlVideoNoAsync(String paginaEpisodio){
        String url = "";
        String auxUrl = "";
        List<String> paginaWeb;

        Utilities util = new Utilities();

        paginaWeb = util.downloadWebPageTaskNoAsync(paginaEpisodio);

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


        return url;
    }


    /**
     * Encuentra los animes resultantes de una busqueda realizada y los devuelve mediante una lista de elementos
     * @param codigoFuente Codigo fuente de la busqueda realizada
     * @return Lista que contiene todos los animes encontrados
     */
    public List<HomeScreenEpi> busquedaFLV(Document codigoFuente) {
        List<HomeScreenEpi> search = new ArrayList<>();
        String urlAnime, nombre, informacion, preview;
        HomeScreenEpi item;
        Elements animesEncontrados = codigoFuente.getElementsByClass("ListAnimes AX Rows A03 C03 D03").select("li");
        if (animesEncontrados.size() == 0) {
            search = null;
            Log.d("Empty", "busqueda animeflv no produce resultados");
        } else {
            for (Element cadaAnime : animesEncontrados) {
                urlAnime = animeflv + cadaAnime.getElementsByClass("Anime alt B").select("a").first().attr("href");
                preview = animeflv + cadaAnime.getElementsByClass("Image").select("img").attr("src");
                informacion = cadaAnime.getElementsByClass("Image").select("span").text();
                nombre = cadaAnime.getElementsByClass("Title").first().text();
                /*Log.d("urlAnime", urlAnime);
                Log.d("nombre", nombre);
                Log.d("informacion", informacion);
                Log.d("preview", preview);*/
                item = new HomeScreenEpi(urlAnime, nombre, informacion, preview); //crea un item tipo homescreen y le añade los valores
                search.add(item);
            }
        }
        return  search;
    }

    /**
     * Obtiene toda la informacion que corresponde al anime y a los capitulos que este contiene.
     * Se utiliza para recopilar los datos que iran en la vista de informacion del anime
     * @param url String con la url de la pagina principal del anime
     * @return Lista que contiene todos los elementos recopilados
     */
    public List<Episodios> getEpisodios(String url, Context context) {
        List<Episodios> capitulos = new ArrayList<>();
        String nombreAnime, urlAnime, urlEp, numero, urlImagen, informacion, tipo, estado, generos, fechaInicio;
        Document document;
        Utilities util = new Utilities();


        if (util.existenCookies(context)) {
            document = util.connect(url, util.getCookiesEnSharedPreferences(context));
        } else {
            document = util.connect(url);
        }

        if (document != null) {
            urlImagen = animeflv + document.getElementsByClass("Image").select("figure").first().select("img").attr("src");
            Elements elementosGenero = document.getElementsByClass("Categories").select("a"); //contiene todos los generos del anime, los cuales estan contenidos en divisiones 'a'
            generos = adaptaGeneros(elementosGenero);
            estado = document.getElementsByClass("AnimStat On fa-check").text(); //estado = en emision
            if (estado.length() == 0) { //estado = finalizado
                estado = document.getElementsByClass("AnimStat Off fa-hourglass-end").text();
            }
            estado = adaptaEstado(estado);
            nombreAnime = document.getElementsByClass("Container").select("h1").first().text();;//contiene el nombre del anime y el tipo (pelicula, anime u ova)
            tipo = identificarTipo(document);
            urlAnime = url;
            fechaInicio = " "; //desde el cambio de la nueva pagina, este atributo ya no existe. No se decide aun que hacer con el
            informacion = document.getElementsByClass("Sect Descrtn").first().select("p").first().text();
            Elements listaEpisodios = document.getElementsByClass("ListEpisodes").select("li");
            Element aux;
            Episodios episodio;
            for (int i = 0; i < listaEpisodios.size(); i++) {
                aux = listaEpisodios.get(i).select("a").first();
                urlEp = animeflv + aux.attr("href");
                numero = aux.text();
                if (i == 0) {
                    episodio = new Episodios(nombreAnime, urlAnime, urlEp, numero, urlImagen, informacion, tipo, estado, generos, fechaInicio);
                } else {
                    episodio = new Episodios(null, null, urlEp, numero, null, null, null, null, null, null);
                }
                capitulos.add(episodio);
               /* Log.d("urlImagen", urlImagen);
                Log.d("generos", generos);
                Log.d("estado", estado);
                Log.d("nombreAnime", nombreAnime);
                Log.d("urlAnime", urlAnime);
                Log.d("tipo", tipo);
                Log.d("informacion", informacion);
                Log.d("urlEp", urlEp);
                Log.d("numero", numero);*/
            }

        } else {
            Log.d(TAG, "Error al obtener la página del anime");
        }

        return capitulos;
    }

    /**
     * Identifica el tipo de anime, ya sea anime, ova o pelicula. Esto para el caso
     * de la pagina principal del anime, ya que es la unica forma de hacerlo (hasta el momento)
     * @param codigoFuente Codigo fuente de la página de anime
     * @return EL tipo de anime
     */
    private String identificarTipo(Document codigoFuente) {
        String tipo;
        tipo = codigoFuente.getElementsByClass("Type tv").text();
        if (tipo.length() == 0) {
            tipo = codigoFuente.getElementsByClass("Type ova").text();
            if (tipo.length() == 0) {
                tipo = codigoFuente.getElementsByClass("Type movie").text();
            }
        }
        return tipo;
    }

    /**
     * Recibe el string de estado y lo adapta para que solo diga lo requerido, es decir, elimina
     * la palabra anime que se ubica al principio
     * @param estado String que contiene el estado
     * @return String modificado con el estado en su formato final
     */
    private String adaptaEstado(String estado) {
        String estadoFinal = "";

        String[] aux = estado.split(" ");
        for (int i = 1; i < aux.length; i++) { //parte desde 1 ya que en la primera posicion se encuentra la variable que se quiere eliminar
            if (i == 1) {
                estadoFinal = aux[i];
            } else {
                estadoFinal = estadoFinal + " " + aux[i];
            }
        }

        return estadoFinal;
    }

    /**
     * Recibe los elementos de genero y los convierte a un unico string
     * @param elementosGenero Contiene los generos encontrados
     * @return String que contiene los generos
     */
    private String adaptaGeneros(Elements elementosGenero) {
        String generos = "";
        int i = 0;
        for (Element cadaGenero : elementosGenero) { //recorre los generos para entregar un string unificado
            if (i == 0) {
                generos = cadaGenero.text();
            } else {
                generos = generos + " - " + cadaGenero.text();
            }
            i++;
        }

        return generos;
    }

    /**
     * Desde la pagina del capitulo de un anime, consigue la url a la pagina principal del anime
     * @param codigoFuente Codigo fuente de la pagina del capitulo del anime
     * @return String con el url de la página principal del anime
     */
    public String urlCapituloToUrlAnime(Document codigoFuente) {
        String urlAnime = null;

        Elements objEpisodios;
        if (codigoFuente != null) {
            objEpisodios = codigoFuente.getElementsByClass("CapiList");
            if (objEpisodios.isEmpty()) {
                Log.d("Error", "No se pudo encontrar la url del anime");
            } else {
                urlAnime = animeflv + objEpisodios.select("li").first().select("a").attr("href");
               // Log.d("URL", urlAnime);
            }
        }
        return urlAnime;
    }

    /**
     * Escanea la pagina de inicio de animeflv y retorna todos los datos que corresponden a los animes diarios
     * @param codigoFuente Codigo fuente de la página de inicio
     * @param resources Recursos de la aplicación (para utilizar "getstring()")
     * @return
     */
    public ArrayList<HomeScreenEpi> homeScreenAnimeFlv(Document codigoFuente, Resources resources) {
        ArrayList<HomeScreenEpi> home = new ArrayList<>();
        String urlCapitulo, nombre, informacion, preview;
        Element objHome = codigoFuente.getElementsByClass("ListEpisodios AX Rows A06 C04 D03").first(); //contiene los episodios recientes
        Elements objEpisodios = objHome.select("li"); //contiene los episodios recientes pero como una lista
        Element episodioAux;
        for (Element episodio : objEpisodios) {
            episodioAux = episodio.select("a").first();
            urlCapitulo = animeflv + episodioAux.attr("href");
            nombre = episodioAux.getElementsByClass("Title").text();
            informacion = episodioAux.getElementsByClass("Capi").text();
            preview = animeflv + episodioAux.getElementsByClass("Image").select("img").first().attr("src");

            home.add(new HomeScreenEpi(urlCapitulo, nombre, informacion, preview)); //agrega el nuevo objeto al array
           /* Log.d("URLCAPITULO", urlCapitulo);
            Log.d("NOMBRE", nombre);
            Log.d("CAPITULO", informacion);
            Log.d("PREVIEW", preview);*/
        }

        return home;
    }


    /**
     * SERVIDOR DESCONTINUADO POR ANIMEFLV
     * Consigue la url del servidor izanagi
     * @param codigoFuente Codigo fuente que contiene las urls de los videos
     * @return Url del servidor izanagi o string en blanco si no lo encuentra
     */
    private String urlIzanagiServer(String codigoFuente){
        String url = "", auxUrl = "";

        Matcher localMatcher = Pattern.compile("server=izanagi&v=(.*?)\"").matcher(codigoFuente);
        while (localMatcher.find()) {
            auxUrl = localMatcher.group(1);
        }
        //Ahora se procede a obtener la direccion del video (debido a que animeflv actualizo la forma en que se obtiene la url de izanagi
        if (!auxUrl.equals("")) {
            auxUrl = "https://s3.animeflv.com/check.php?server=izanagi&v=" + auxUrl;
            Utilities util = new Utilities();
            Document respuesta = util.connect(auxUrl);
            Matcher localMatcher2 = Pattern.compile("\"file\":\"(.*?)\"").matcher(respuesta.toString());
            while (localMatcher2.find()) {
                url = localMatcher2.group(1);
            }
            url = url.replace("\\", "");
        }
        return url;
    }

    /**
     * SERVIDOR DESCONTINUADO POR ANIMEFLV
     * Consigue la url del servidor yotta
     * @param codigoFuente Codigo fuente que contiene las urls de los videos
     * @return Url del servidor yotta o string en blanco si no lo encuentra
     */
    private String urlYottaServer(String codigoFuente){
        String url = "", auxUrl = "";

        Matcher localMatcher = Pattern.compile("server=gdrive&v=(.*?)\"").matcher(codigoFuente);
        while (localMatcher.find()) {
            auxUrl = localMatcher.group(1);
        }

        //Ahora se procede a obtener la direccion del video (debido a que animeflv actualizo la forma en que se obtiene la url de yotta
        if (!auxUrl.equals("")) {//si existe una url donde buscar
            auxUrl = "https://s3.animeflv.com/check.php?server=gdrive&v=" + auxUrl;
            Utilities util = new Utilities();
            Document respuesta = util.connect(auxUrl);
            Matcher localMatcher2 = Pattern.compile("\"default\":\"true\",\"label\":480,\"type\":\"video\\/mp4\",\"file\":\"(.*?)\"").matcher(respuesta.toString());
            while (localMatcher2.find()) {
                url = localMatcher2.group(1);
            }
            if (url.equals("")) {
                Matcher localMatcher3 = Pattern.compile("\"default\":\"false\",\"label\":360,\"type\":\"video\\/mp4\",\"file\":\"(.*?)\"").matcher(respuesta.toString());
                while (localMatcher3.find()) {
                    url = localMatcher3.group(1);
                }
            }
            url = url.replace("&amp;", "&");
        }

        return url;
    }

    /**
     * Consigue la url del servidor YourUpload
     * @param codigoFuente Codigo fuente que contiene las urls de los videos
     * @return Url del servidor YourUpload o string en blanco si no lo encuentra
     */
    private String urlYourUploadServer(String codigoFuente){
        String url = "", auxUrl = "";

        Matcher localMatcher = Pattern.compile("yourupload&v=(.*?)\"").matcher(codigoFuente);
        while (localMatcher.find()) {
            auxUrl = localMatcher.group(1);
        }
        //Ahora se procede a obtener la direccion del video
        if (!auxUrl.equals("")) {//si existe una url donde buscar
            auxUrl = "https://s3.animeflv.com/check.php?server=yourupload&v=" + auxUrl;
            Utilities util = new Utilities();
            Document respuesta = util.connect(auxUrl);
            Matcher localMatcher2 = Pattern.compile("file\":\"(.*?)\"").matcher(respuesta.toString());
            while (localMatcher2.find()) {
                url = localMatcher2.group(1);
            }
            url = url.replace("\\", "");
        }
        return url;
    }

    /***
     * Consigue la url del servidor Hyperion
     * @param codigoFuente Codigo fuente que contiene las urls de los videos
     * @return Url del servidor Hyperion o string en blanco si no lo encuentra
     */
    public String urlHyperionServer(String codigoFuente){
        String url = "", auxUrl = "";

        Matcher localMatcher = Pattern.compile("hyperion&v=(.*?)\"").matcher(codigoFuente);
        while (localMatcher.find()) {
            auxUrl = localMatcher.group(1);
        }
        //Ahora se procede a obtener la direccion del video
        if (!auxUrl.equals("")) {//si existe una url donde buscar
            auxUrl = "https://s3.animeflv.com/check.php?server=hyperion&v=" + auxUrl;
            Utilities util = new Utilities();
            Document respuesta = util.connect(auxUrl);
            Matcher localMatcher2 = Pattern.compile("file\":\"(.*?)\"").matcher(respuesta.toString());
            while (localMatcher2.find()) {
                url = localMatcher2.group(1);
            }
            url = url.replace("\\", "");
        }
        return url;
    }

    /***
     * Consigue la url del servidor Clup
     * @param codigoFuente Codigo fuente que contiene las urls de los videos
     * @return Url del servidor Clup o string en blanco si no lo encuentra
     */
    public String urlClupServer(String codigoFuente){
        String url = "", auxUrl = "";

        Matcher localMatcher = Pattern.compile("direct\\.php\\?v=(.*?)\"").matcher(codigoFuente);
        while (localMatcher.find()) {
            auxUrl = localMatcher.group(1);
        }
        //Ahora se procede a obtener la direccion del video
        if (!auxUrl.equals("")) {//si existe una url donde buscar
            url = auxUrl;
        }
        return url;
    }

    /**
     * SERVIDOR DESCONTINUADO POR ANIMEFLV
     * Consigue la url del servidor Minhateca
     * @param codigoFuente Codigo fuente que contiene las urls de los videos
     * @return Url del servidor Minhateca o string en blanco si no lo encuentra
     */
    private String urlMinhatecaServer(String codigoFuente){
        String url = "", auxUrl = "";

        Matcher localMatcher = Pattern.compile("minhateca&v=(.*?)\"").matcher(codigoFuente);
        while (localMatcher.find()) {
            auxUrl = localMatcher.group(1);
        }
        //Ahora se procede a obtener la direccion del video
        if (!auxUrl.equals("")) {
            auxUrl = "https://s3.animeflv.com/check.php?server=minhateca&v=" + auxUrl;
            Utilities util = new Utilities();
            Document respuesta = util.connect(auxUrl);
            if (respuesta.toString().contains("Por favor intenta de nuevo en unos segundos")) { //se tienen que esperar 3 segundo antes de volver a consultar
                try {
                    Thread.sleep(3500);
                    respuesta = util.connect(auxUrl);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Matcher localMatcher2 = Pattern.compile("file\":\"(.*?)\"").matcher(respuesta.toString());
            while (localMatcher2.find()) {
                url = localMatcher2.group(1);
            }
            url = url.replace("\\", "");
            url = url.replace("&amp;", "&");
        }
        return url;
    }

    /**
     * Verifica dentro de los servidores soportados, cual de ellos esta disponible y entrega la url
     * del video. El orden de prioridad de los servidores es el siguiente :
     * hyperion > clup > yourupload
     * @param urlEpisodio La url del capitulo en cuestion
     * @param context
     * @return Url del video
     */
    public String urlDisponible(String urlEpisodio, Context context) {
        String url = "", codigoUrlVideos;
        Utilities util = new Utilities();
        Document codigoFuente;
        if (util.existenCookies(context)) {
            codigoFuente = util.connect(urlEpisodio, util.getCookiesEnSharedPreferences(context));
        } else {
            codigoFuente = util.connect(urlEpisodio);
        }
        codigoUrlVideos = codigoFuente.select("script").toString();
        String urlAux;
        boolean  hyperionDisponible = true, clupDisponible = false;

        urlAux = urlHyperionServer(codigoUrlVideos);
        if (!urlAux.equals("")) {
            if (util.isServerReachable(urlAux, context)) {
                url = urlAux;
                Log.d("hyperion", url);
            } else {
                hyperionDisponible = false;
            }
        } else {
            hyperionDisponible = false;
        }

        if (!hyperionDisponible) {
            urlAux = urlClupServer(codigoUrlVideos);
            if (!urlAux.equals("")) { //revisa si existe la url
                if (util.isServerReachable(urlAux, context)) { //revisa si la url es accesible
                    url = urlAux;
                    clupDisponible = true;
                    Log.d("clup", url);
                }
            }
        }

        if (!hyperionDisponible && !clupDisponible) {
            urlAux = urlYourUploadServer(codigoUrlVideos);
            if (!urlAux.equals("")) { //revisa si existe la url
                if (util.isServerReachable(urlAux, context)) { //revisa si la url es accesible
                    url = urlAux;
                    Log.d("yourupload", url);
                }
            }
        }

        return url;
    }

    /**
     * Verifica cuales son los generos disponibles. Obteniendo el nombre y la url a estos
     * @param codigoFuente Codigo fuente de la pagina en donde estan contenidos los generos
     * @return Lista con los generos encontrados
     */
    public List<GenerosClass> generosDisponibles(Document codigoFuente) {
        List<GenerosClass> resultado = new ArrayList<>();
        String nombre, url;
        Elements generos;

        generos = codigoFuente.getElementsByClass("filters").first().getElementById("genre_select").select("option");

        for (Element cadaGenero : generos) {
            nombre = cadaGenero.text();
            url = "http://animeflv.net/browse?genre[]=" + cadaGenero.attr("value") + "&order=title";
            resultado.add(new GenerosClass(nombre, url));
        }

        return resultado;
    }


    /**
     * Verifica si la pagina contiene mas elementos que mostrar en una segunda pagina.
     * En caso de existir devuelve la url de dicha pagina
     * @param codigoFuente Codigo fuente de la página actual
     * @return String con la url de la siguiente pagina o string en blanco si no existen mas paginas
     */
    public String siguientePagina(Document codigoFuente) {
        String urlPagina = "";
        Elements numPaginas = codigoFuente.getElementsByClass("pagination").select("li"); //paginas. Pueden ser 1 o mas

        if (numPaginas.size() > 1) { //para cuando son mas de 1 pagina
            Element paginaSiguiente = numPaginas.last();
            if (paginaSiguiente.getElementsByClass("disabled").first() == null) { //si no es la ultima pagina
                urlPagina = animeflv + paginaSiguiente.select("a").first().attr("href");
                try {
                    urlPagina = URLDecoder.decode(urlPagina, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return urlPagina;
    }

    public void añadirHistorialFlv(String nombre, String urlEpisodio) {
        List<HistorialFlvTable> lista = DataSupport.where("nombre=? and urlEpisodio=?", nombre, urlEpisodio).find(HistorialFlvTable.class);
        if (lista.isEmpty()){ //si la lista no contiene el capitulo que se quiere añadir
            HistorialFlvTable historialFlv = new HistorialFlvTable(nombre, urlEpisodio);
            historialFlv.save();
        }
    }

    public boolean seEncuentraEnHistorialFlv(String nombre, String urlEpisodio) {
        List<HistorialFlvTable> lista = DataSupport.where("nombre=? and urlEpisodio=?", nombre, urlEpisodio).find(HistorialFlvTable.class);
        if (lista.isEmpty()){ //si la lista no contiene el capitulo que se quiere añadir
            return false;
        } else {
            return true;
        }
    }

    public void añadirHistorial(String nombre, String tipo, String urlImagen, String urlCapitulo) {
        List<HistorialTable> lista = DataSupport.where("nombre=? and urlCapitulo=?", nombre, urlCapitulo).find(HistorialTable.class);
        if (lista.isEmpty()){ //si la lista no contiene el capitulo que se quiere añadir
            int tamaño = DataSupport.count(HistorialTable.class);
            if (tamaño < 20) {
                HistorialTable historial = new HistorialTable(nombre, tipo, urlImagen, urlCapitulo);
                historial.save();
            }
            if (tamaño == 20) { //El historial tendra un maximo de 20 items
                //ELimina el primero
                HistorialTable primero = DataSupport.findFirst(HistorialTable.class);
                DataSupport.deleteAll(HistorialTable.class, "nombre=? and urlCapitulo=?", primero.getNombre(), primero.getUrlCapitulo());
                //Agrega el siguiente
                HistorialTable historial = new HistorialTable(nombre, tipo, urlImagen, urlCapitulo);
                historial.save();
            }
        } else {
            if (lista.size() == 1) { //si ya se encuentra en la lista
                //Se elimina de la posicion anterior
                DataSupport.deleteAll(HistorialTable.class, "nombre=? and urlCapitulo=?", lista.get(0).getNombre(), lista.get(0).getUrlCapitulo());
                //se agrega nuevamente
                HistorialTable historial = new HistorialTable(nombre, tipo, urlImagen, urlCapitulo);
                historial.save();
            }
        }
    }


    /*obtiene la foto de preview del capitulo del anime, cuando este proviene desde la vista
    de los episodios (EpisodiosFlv)
     */
    public String getMiniImage(Document codigoFuente) {
        String imagen = "";

        Elements imgRecipiente = codigoFuente.select("head");
        Element imgAtributo;
        for (int i = 0; i < imgRecipiente.size(); i++) {
            imgAtributo = imgRecipiente.get(i).getElementsByAttributeValueContaining("property", "og:image").first();
            if (imgAtributo != null) {
                imagen = imgAtributo.attr("content");
            }
        }

        return imagen;
    }

    /**
     * Verifica si CloudFlare se encuentra activado en animeflv
     * @param codigoFuente Codigo fuente de la pagina
     * @return Respuesta
     */
    public boolean estaCloudflareActivado(Document codigoFuente) {
        String cloudflare = "";
        cloudflare = codigoFuente.getElementsByClass("attribution").select("a").text();
        return cloudflare.contains("DDoS protection by Cloudflare");
    }




}

