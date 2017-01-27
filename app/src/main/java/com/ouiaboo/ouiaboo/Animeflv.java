package com.ouiaboo.ouiaboo;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.ouiaboo.ouiaboo.Tables.animeflv.HistorialFlvTable;
import com.ouiaboo.ouiaboo.Tables.animeflv.HistorialTable;
import com.ouiaboo.ouiaboo.clases.Episodios;
import com.ouiaboo.ouiaboo.clases.GenerosClass;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

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
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Vareta on 29-07-2015.
 */
public class Animeflv{
    private String animeflv = "http://animeflv.net/"; //sitio web


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


    public List<HomeScreenEpi> busquedaFLV(Document codigoFuente){
        String pelicula = "tipo_2"; //pelicula
        String ova = "tipo_1"; //OVA
        String serie = "tipo_0"; //serie de anime
        List<HomeScreenEpi> search = new ArrayList<HomeScreenEpi>();
        String urlAnime, nombre, informacion, informacionAux, preview;

        Elements objetosEpi;
        objetosEpi = codigoFuente.getElementsByClass("aboxy_lista");
        if (objetosEpi.isEmpty()){ // si no contiene la clase, es decir la busqueda no produjo resultados
            search = null;
            Log.d("Empty", "busqueda animeflv no produce resultados");
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

                HomeScreenEpi item = new HomeScreenEpi(urlAnime, nombre, informacion, preview); //crea un item tipo homescreen y le añade los valores
                search.add(item); //agrega el item a la lista de items
            }
        }

        return search;
    }




    public List<Episodios> getEpisodios(String url) {
        String nombreAnime;
        String urlAnime;
        String urlEp;
        String numero;
        String urlImagen = null;
        String informacion = null;
        String tipo = "";
        String estado = "";
        String generos = "";
        String fechaInicio = "";
        List<Episodios> capitulos = new ArrayList<Episodios>();
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
                        nombreAnime = doc.select("h1").first().text();
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
                                tipo = adaptaInfoTipoEstadoGenerosFecha(infoDetallada.get(j).text());
                            }
                            if (infoDetallada.get(j).select("b").first().toString().equals("<b>Estado:</b>")) {
                                estado = adaptaInfoTipoEstadoGenerosFecha(infoDetallada.get(j).text());
                            }
                            if (infoDetallada.get(j).select("b").first().toString().equals("<b>Generos:</b>")) {
                                generos = adaptaInfoTipoEstadoGenerosFecha(infoDetallada.get(j).text());
                            }
                            if (infoDetallada.get(j).select("b").first().toString().equals("<b>Fecha de Inicio:</b>")) {
                                fechaInicio = adaptaInfoTipoEstadoGenerosFecha(infoDetallada.get(j).text());
                            }
                        }
                        urlAnime = url;
                        urlEp = "http://animeflv.net" + episodiosFocus.get(i).attr("href");
                        numero = episodiosFocus.get(i).text();
                        epi = new Episodios(nombreAnime, urlAnime, urlEp, numero, urlImagen, informacion, tipo, estado, generos, fechaInicio);

                    } else {
                        urlEp = "http://animeflv.net" + episodiosFocus.get(i).attr("href");
                        numero = episodiosFocus.get(i).text();
                        epi = new Episodios(null, null, urlEp, numero, null, null, null, null, null, null);
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
    /****************Deprecado*************************/
    private String numeroEpisodio(String episodio, Resources resources) {
        String epi;

        String[] aux = episodio.split(":");
        String[] aux2 = aux[0].split(" ");
        epi = resources.getString(R.string.numero_episodio_menu_central_ES) + " " + aux2[aux2.length - 1];

        return epi;
    }

    private String adaptaInfoTipoEstadoGenerosFecha(String info) {
        String data;

        String[] aux = info.split(": ");
        data = aux[aux.length - 1];

        return data;
    }


    public String urlCapituloToUrlAnime(Document codigoFuente) {
        String urlAnime = null;

        Elements objEpisodios;
        if (codigoFuente != null) {
            objEpisodios = codigoFuente.getElementsByClass("episodio_head");
            if (objEpisodios.isEmpty()) {
                Log.d("Error", "No se puedo encontrar la url del anime");
            } else {
                Element titulo = objEpisodios.select("a").first();
                urlAnime = "http://animeflv.net" + titulo.attr("href");
               // Log.d("URL", urlAnime);
            }
        }
        return urlAnime;
    }

    public ArrayList<HomeScreenEpi> homeScreenAnimeFlv(Document codigoFuente, Resources resources) {
        ArrayList<HomeScreenEpi> home = new ArrayList<>();
        String urlCapitulo, nombre, informacion, preview, aux;

        Element objHome = codigoFuente.getElementsByClass("ultimos_epis").first(); //contiene los episodios recientes
        Elements objEpisodios = objHome.select("a"); //contiene los episodios recientes pero como una lista
        Elements objEpiTipo = objHome.getElementsByClass("not"); //contiene los episodios mas el tipo (ova, pelicula, anime)
        Element img;
        Element auxElement;
        for (int i = 0; i < objEpisodios.size(); i++) {
            //Obtiene url del capitulo
            aux = objEpisodios.get(i).attr("href");
            urlCapitulo = "http://animeflv.net" + aux;
            //Obtiene nombre Anime
            nombre = objEpisodios.get(i).attr("title");
            //Obtiene informacion capitulo, Ej; capitulo numero x, Ova, pelicula
            String[] div = nombre.split(" ");
            auxElement = objEpiTipo.get(i).getElementsByClass("tova").first(); //es ova? (probable)
           // Log.d("AUX", String.valueOf(auxElement.size()));
            if (auxElement != null) {
                informacion = resources.getString(R.string.ova_menu_central_ES);
            } else {
                auxElement = objEpiTipo.get(i).getElementsByClass("tpeli").first(); //es pelicula? (poco probable)
                if (auxElement != null) {
                    informacion = resources.getString(R.string.pelicula_menu_central_ES);
                } else { //entonces es anime (mas probable)
                    String nombreAux = "";
                    for (int j = 0; j < div.length - 1; j++) { // consigue sólo el nombre del anime
                        if (j < div.length - 2) {
                            nombreAux = nombreAux + div[j] + " ";
                        } else {
                            nombreAux = nombreAux + div[j]; //ultima palabra y asi no queda con un espacio al final
                        }
                    }
                    nombre = nombreAux; //agrega el nombre de la serie
                    informacion = resources.getString(R.string.numero_episodio_menu_central_ES) + " " + div[div.length - 1]; //info, Ej: Capitulo numero x
                }
            }
            //Obtiene url de la imagen del episodio
            img = objEpisodios.get(i).select("img").first();
            preview = img.attr("src");
            home.add(new HomeScreenEpi(urlCapitulo, nombre, informacion, preview)); //agrega el nuevo objeto al array

           /* Log.d("URLCAPITULO", urlCapitulo);
            Log.d("NOMBRE", nombre);
            Log.d("CAPITULO", informacion);
            Log.d("PREVIEW", preview);*/
        }

        return home;
    }

    public String urlIzanagiServer(List<String> paginaWeb){
        String url = "", auxUrl = "";

        int max = paginaWeb.size();

        for (int i = 0; i < max; i++) {
            if (paginaWeb.get(i).contains("var videos")) {
                Matcher localMatcher = Pattern.compile("embed_izanagi.php\\?key=(.*?)\"").matcher(paginaWeb.get(i));
                //Log.d("URL  ", paginaWeb.get(i));
                while (localMatcher.find()) {
                    auxUrl = localMatcher.group(1);
                    //System.out.println(aux);
                    try {
                        Log.d("auxurl", auxUrl);
                        auxUrl = "https://s2.animeflv.com/izanagi.php?id=" + URLEncoder.encode(auxUrl, "UTF-8"); //luego de la ultima actualizacion de animeflv, parte de la url esta encodeada (la key basicamente)
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //Ahora se procede a obtener la direccion del video (debido a que animeflv actualizo la forma en que se obtiene la url de izanagi
        Utilities util = new Utilities();
        List<String> urlResponse = util.downloadWebPageTaskNoAsync(auxUrl);
        for (int j = 0; j < urlResponse.size(); j++) {
            if (urlResponse.get(j).contains("file")) {
                String[] dataAux = urlResponse.get(j).split("\""); //elimina los " de la linea que contiene: {file:"url"}
                url = dataAux[dataAux.length - 2].replace("\\", "" ); //en el penultimo lugar se encuentra la url del video
            }
        }

        return url;
    }

    public String urlYottaServer(List<String> paginaWeb){
        String url = "", auxUrl = "";
        int max = paginaWeb.size();

        for (int i = 0; i < max; i++) {
            if (paginaWeb.get(i).contains("var videos")) {
                Matcher localMatcher = Pattern.compile("embed_yotta.php\\?key=(.*?)\"").matcher(paginaWeb.get(i));
                while (localMatcher.find()) {
                    auxUrl = localMatcher.group(1);
                    //System.out.println(aux);
                    auxUrl = "https://s1.animeflv.com/gdrive.php?id=" + auxUrl;
                }
            }
        }

        //Ahora se procede a obtener la direccion del video (debido a que animeflv actualizo la forma en que se obtiene la url de izanagi
        Utilities util = new Utilities();
        List<String> urlResponse = util.downloadWebPageTaskNoAsync(auxUrl);
        for (int j = 0; j < urlResponse.size(); j++) {
            if (urlResponse.get(j).contains("file")) {
                Matcher localMatcher = Pattern.compile("\"file\":\"(.*?)\"").matcher(urlResponse.get(j)); //obtiene la url del video en hd
                while (localMatcher.find()) {
                    String dataAux = localMatcher.group(1);
                    url = dataAux.replace("\\", ""); // Elimina los caracteres \
                }
            }
        }
        Log.d("YOTAURL", url);
        return url;
    }

    /*Servidor eliminado por animeflv*/
    public String urlFLVServer(List<String> paginaWeb){
        String auxUrl = "", urlFinal = "";

        int max = paginaWeb.size();

        for (int i = 0; i < max; i++) {
            if (paginaWeb.get(i).contains("var videos")) {
                Matcher localMatcher = Pattern.compile("embed.php\\?aid=(.*?)\\\\").matcher(paginaWeb.get(i));
                //Log.d("URL  ", paginaWeb.get(i));
                while (localMatcher.find()) {
                    auxUrl = localMatcher.group(1);
                    //System.out.println(aux);
                    String[] url = auxUrl.split("&num=");

                    urlFinal = "http://subidas.com/files/" + url[0] + "/" + url[1] + ".mp4";
                }
            }
        }


        return urlFinal;
    }

    /*Servidor eliminado por animeflv*/
    public String urlHyperionServer(List<String> paginaWeb){
        String auxUrl = "", url = "";

        int max = paginaWeb.size();

        for (int i = 0; i < max; i++) {
            if (paginaWeb.get(i).contains("var videos")) {
                Matcher localMatcher = Pattern.compile("hyperion.php\\?key=(.*?)&provider").matcher(paginaWeb.get(i));
                //Log.d("URL  ", paginaWeb.get(i));
                while (localMatcher.find()) {
                    auxUrl = localMatcher.group(1);
                    //System.out.println(aux);
                    try {
                        url = URLDecoder.decode("http://animeflv.net/video/hyperion.php?key=" + auxUrl, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        return url;
    }

    /* se elimino de animeflv */
    public String urlKamiServer(List<String> paginaWeb){
        String url = "", auxUrl = "";

        int max = paginaWeb.size();

        for (int i = 0; i < max; i++) {
            if (paginaWeb.get(i).contains("var videos")) {
                Matcher localMatcher = Pattern.compile("kami.php\\?key=(.*?)\"").matcher(paginaWeb.get(i));
                //Log.d("URL  ", paginaWeb.get(i));
                while (localMatcher.find()) {
                    auxUrl = localMatcher.group(1);
                    //System.out.println(aux);
                    try {
                        auxUrl = URLDecoder.decode("https://animeflv.net/video/kami.php?key=" + auxUrl, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //Ahora se procede a obtener la direccion del video (debido a que animeflv actualizo la forma en que se obtiene la url de izanagi
        Utilities util = new Utilities();
        List<String> urlResponse = util.downloadWebPageTaskNoAsync(auxUrl);
        for (int j = 0; j < urlResponse.size(); j++) {
            if (urlResponse.get(j).contains("file")) {
                String[] dataAux = urlResponse.get(j).split("\""); //elimina los espacios de la linea que contiene: sources: [{file: "http://2.bp.blogspot.com/au5Dbu69zEyYwuN5H_CctNXFfWrsfFcp79WWwSC1BzWL=m18", label: "360", type: "video/mp4"}],
                url = dataAux[1]; //en el lugar 1 se encuentra la url del video
            }
        }
        Log.d("URLKAMI", url);
        return url;
    }

    /*Entrega la url que este disponible
    @urlEpisodio --> es la url del capitulo en cuestion
     */
    public String urlDisponible(String urlEpisodio, Context context) {
        String url = "";
        Utilities util = new Utilities();
        List<String> codFuente = util.downloadWebPageTaskNoAsync(urlEpisodio); //obtiene el codigo fuente en forma de una lista de string
        String urlAux;
        boolean  izanagiDisponible = false;

        /*urlAux = urlIzanagiServer(codFuente);
        if (!urlAux.equals("")) {
            if (util.isServerReachable(urlAux, context)) {
                url = urlAux;
                Log.d("izanagi", url);
            } else {
                izanagiDisponible = false;
            }
        } else {
            izanagiDisponible = false;
        }*/
        if (!izanagiDisponible) {
            urlAux = urlYottaServer(codFuente);
            if (!urlAux.equals("")) { //revisa si existe la url
                if (util.isServerReachable(urlAux, context)) { //revisa si la url es accesible
                    url = urlAux;
                    Log.d("yotta", url);
                }
            }
        }

        Log.d("URL", url);
        return url;
    }

    public List<GenerosClass> generosDisponibles(Document codigoFuente) {
        List<GenerosClass> resultado = new ArrayList<>();
        String nombre, url;
        Element objGeneros;
        Elements generos;

        objGeneros = codigoFuente.getElementsByClass("generos_box").first();
        generos = objGeneros.select("a");

        for (int i = 0; i < generos.size(); i++) {
            nombre = generos.get(i).text();
            url = "http://animeflv.net" + generos.get(i).attr("href");

            resultado.add(new GenerosClass(nombre, url));
        }

        return resultado;
    }

    /*verifica si la pagina contiene mas elementos que mostrar en una segunda pagina.
    * En caso de existir devuelve la url de dicha pagina
    */
    public String siguientePagina(Document codigoFuente) {
        String urlPagina = "";

        Element numPaginas = codigoFuente.getElementsByClass("pagin").first();
        Element pagSiguiente = numPaginas.select("a").last();
        if (pagSiguiente != null) {
            if (pagSiguiente.text().equals("»")) { //&raquo; es el simbolo para »
                urlPagina = "http://animeflv.net" + pagSiguiente.attr("href");
                //Log.d("URL", urlPagina);
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

        Elements imgRecipiente = codigoFuente.select("meta");
        Element imgAtributo;
        for (int i = 0; i < imgRecipiente.size(); i++) {
            imgAtributo = imgRecipiente.get(i).getElementsByAttributeValueContaining("property", "og:image").first();
            if (imgAtributo != null) {
                imagen = imgAtributo.attr("content");
            }
        }

        return imagen;
    }

}

