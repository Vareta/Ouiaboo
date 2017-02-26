package com.ouiaboo.ouiaboo;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

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
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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


    /**
     * Encuentra los animes resultantes de una busqueda realizada y los devuelve mediante una lista de elementos
     * @param codigoFuente Codigo fuente de la busqueda realizada
     * @return Lista que contiene todos los animes encontrados
     */
    public List<HomeScreenEpi> busquedaFLV(Document codigoFuente) {
        List<HomeScreenEpi> search = new ArrayList<>();
        String urlAnime, nombre, informacion, preview;
        HomeScreenEpi item;
        Elements animesEncontrados = codigoFuente.getElementsByClass("ListAnimes AX Rows A03 C02 D02").select("li");
        if (animesEncontrados.size() == 0) {
            search = null;
            Log.d("Empty", "busqueda animeflv no produce resultados");
        } else {
            for (Element cadaAnime : animesEncontrados) {
                urlAnime = animeflv + cadaAnime.getElementsByClass("Image").select("a").attr("href");
                preview = animeflv + cadaAnime.getElementsByClass("Image").select("img").attr("src");
                informacion = cadaAnime.getElementsByClass("Image").select("span").text();
                nombre = cadaAnime.getElementsByClass("Title").text();
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
    public List<Episodios> getEpisodios(String url) {
        List<Episodios> capitulos = new ArrayList<>();
        String nombreAnime, urlAnime, urlEp, numero, urlImagen, informacion, tipo, estado, generos, fechaInicio;
        Document document;
        Utilities util = new Utilities();

        document = util.connect(url);

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
            }
            /*Log.d("urlImagen", urlImagen);
            Log.d("generos", generos);
            Log.d("estado", estado);
            Log.d("nombreAnime", nombreAnime);
            Log.d("urlAnime", urlAnime);
            Log.d("tipo", tipo);
            Log.d("informacion", informacion);
            Log.d("urlEp", urlEp);
            Log.d("numero", numero);*/
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
        Log.d(TAG, String.valueOf(tipo.length()));
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
            /*Log.d("URLCAPITULO", urlCapitulo);
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
        boolean  yottaDisponible = true;

        urlAux = urlYottaServer(codFuente);
        if (!urlAux.equals("")) {
            if (util.isServerReachable(urlAux, context)) {
                url = urlAux;
                Log.d("yotta", url);
            } else {
                yottaDisponible = false;
            }
        } else {
            yottaDisponible = false;
        }
        if (!yottaDisponible) {
            urlAux = urlIzanagiServer(codFuente);
            if (!urlAux.equals("")) { //revisa si existe la url
                if (util.isServerReachable(urlAux, context)) { //revisa si la url es accesible
                    url = urlAux;
                    Log.d("izanagi", url);
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

