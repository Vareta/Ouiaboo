package com.ouiaboo.ouiaboo;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.ouiaboo.ouiaboo.Tables.reyanime.HistorialReyTable;
import com.ouiaboo.ouiaboo.Tables.reyanime.HistorialTableRey;
import com.ouiaboo.ouiaboo.clases.Episodios;
import com.ouiaboo.ouiaboo.clases.GenerosClass;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vareta on 07-02-2016.
 */
public class Reyanime {
    private final String TAG = "Reyanime";

    public List<HomeScreenEpi> homeScreen(Document codigoFuente, Resources resources) {
        List<HomeScreenEpi> home = new ArrayList<>();
        String urlAnime, nombre, informacion, informacionAux, preview;
        Elements objetosHome, objetosEpi;
        objetosHome = codigoFuente.getElementsByClass("emision");//contiene los episodios diarios
        objetosEpi = objetosHome.select("a"); //contiene los episodios diarios como una lista

        Element nombreEpi; //elemento que contiene el nombre del episodio
        Element dirAnimeTipo; //elemento que contiene la informacion acerca si es pelicula, ova o serie

        URI urlAux; //almacena el correcto valor de la url de imagen
        for (int i = 0; i < objetosEpi.size(); i++) {
            nombreEpi = objetosEpi.get(i).getElementsByClass("sobre").first();
            nombre = nombreEpi.select("name").text(); //nombre anime
            urlAnime = "http://reyanime.com" + objetosEpi.get(i).attr("href");
            preview = urlImagenPreview(objetosEpi.get(i).select("img").attr("src")); //url imagen
            informacionAux = objetosEpi.get(i).select("sombra").text(); //numero capitulo
            String[] aux = informacionAux.split(" ");
            informacion = resources.getString(R.string.numero_episodio_menu_central_ES) + " " + aux[aux.length - 1];

            /* System.out.println("url  : " + urlAnime);
             System.out.println("Nombre  : " + nombre);
             System.out.println("Imagen  : " + preview);
             System.out.println("Tipo  : " + informacion);*/

            HomeScreenEpi item = new HomeScreenEpi(urlAnime, nombre, informacion, preview); //crea un item tipo homescreen y le añade los valores
            home.add(item); //agrega el item a la lista de items

        }

        return home;
    }

    public String urlDisponible(String urlEpisodio, Context context) {
        String url = "";
        Utilities util = new Utilities();
        String urlDeServidores = urlsReproductor(urlEpisodio); //obtiene las urls de los servidores disponibles para el episodio
        String urlAux;
        boolean zeroDisponible = true, ichiDisponible = false, amzDisponible = false; //nombre de los servidores

        urlAux = urlZeroServer(urlDeServidores);
        Log.d(TAG, "inicio zero server");
        if (!urlAux.equals("")) {
            if (util.isServerReachable(urlAux, context)) {
                url = urlAux;
                Log.d("urlDisponible", "zero server");
            } else {
                zeroDisponible = false;
            }
        } else {
            zeroDisponible = false;
        }

        if (!zeroDisponible) {
            Log.d(TAG, "inicio ichi server");
            urlAux = urlIchiServer(urlDeServidores);
            if (!urlAux.equals("")) {
                System.out.println(urlAux);
                if (util.isServerReachable(urlAux, context)) {
                    url = urlAux;
                    ichiDisponible = true;
                    Log.d("urlDisponible", "ichi server");
                }
            }
        }


        if (!zeroDisponible && !ichiDisponible) { //amz server
            Log.d(TAG, "inicio amz server");
            urlAux = urlAmzServer(urlDeServidores);
            if (!urlAux.equals("")) {
                if (util.isServerReachable(urlAux, context)) {
                    url = urlAux;
                    amzDisponible = true;
                    Log.d("urlDisponible", "amz server");
                }
            }
        }

        if (!zeroDisponible && !ichiDisponible && !amzDisponible) { //amzcl server
            Log.d(TAG, "inicio amzcl server");
            urlAux = urlAmzclServer(urlDeServidores);
            if (!urlAux.equals("")) {
                if (util.isServerReachable(urlAux, context)) {
                    url = urlAux;
                    Log.d("urlDisponible", "amzcl server");
                }
            }
        }
        Log.d("url final", url);
        return url;
    }

    /**
     * Obtiene todas las urls de los servidores disponibles para un episodio.
     *
     * @param urlEpisodio URL del episodio
     * @return Un string que contiene las urls de los servidores disponibles para un episodios. En caso de error devuelve
     * un string en blanco
     */
    private String urlsReproductor(String urlEpisodio) {
        String urls = "";
        Utilities util = new Utilities();
        Document cod = util.connect(urlEpisodio);
        Element reproductor = cod.getElementsByClass("conten-box").first(); //obtienen el elemento que contiene el reproductor
        if (reproductor == null) {
            Log.d(TAG, "No se puede conseguir el elemento que contiene la informacion del reproductor");
        } else {
            Elements scripts = reproductor.select("script");
            if (scripts == null) {
                Log.d(TAG, "No se puede conseguir el script que contiene las url de video del episodio");
            } else {
                for (Element s : scripts) {
                    if (s.toString().contains("tabsArray")) {
                        urls = s.toString();
                        break;
                    }
                }
            }
        }

        return urls;
    }

    private String urlZeroServer(String codFuente) {
        String auxUrl = "", url = "";
        boolean seEcuentraUrl = false;

        if (codFuente.contains("az?v")) {
            Matcher localMatcher = Pattern.compile("repro-rc\\/az\\?v=(.*?)\"").matcher(codFuente);
            while (localMatcher.find()) {
                auxUrl = localMatcher.group(1);
                auxUrl = "http://ozhe.larata.in/repro-rc/az?v=" + auxUrl; //url completa
                seEcuentraUrl = true;
            }
        }

        if (seEcuentraUrl) {
            Utilities util = new Utilities();
            List<String> urlResponse = util.downloadWebPageTaskNoAsync(auxUrl);
            for (int j = 0; j < urlResponse.size(); j++) {
                if (urlResponse.get(j).contains("<script type='text/javascript'>")) { //linea donde se encuentra la url del video
                    Matcher localMatcher = Pattern.compile("file: \"(.*?)\"").matcher(urlResponse.get(j)); //obtiene la url del video completa
                    while (localMatcher.find()) {
                        auxUrl = localMatcher.group(1);
                        try {
                            url = URLDecoder.decode(auxUrl, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return url;
    }

    private String urlIchiServer(String codFuente) {
        String auxUrl = "", url = "";
        boolean seEncuentraUrl = false;

        if (codFuente.contains("send?v")) {
            Matcher localMatcher = Pattern.compile("send\\?v=(.*?)\"").matcher(codFuente);
            while (localMatcher.find()) {
                auxUrl = localMatcher.group(1);
                auxUrl = "http://my.mp4link.com/embed/sendvid/code=" + auxUrl;
                seEncuentraUrl = true;
            }
        }

        //ahora obtiene
        if (seEncuentraUrl) {
            Utilities util = new Utilities();
            List<String> urlResponse = util.downloadWebPageTaskNoAsync(auxUrl);
            for (int j = 0; j < urlResponse.size(); j++) {
                if (urlResponse.get(j).contains("<script type='text/javascript'> var jwPlayer")) { //linea donde se encuentra la url del video
                    Matcher localMatcher = Pattern.compile("file: \"(.*?)\"").matcher(urlResponse.get(j)); //obtiene la url del video completa
                    while (localMatcher.find()) {
                        auxUrl = localMatcher.group(1);
                        try {
                            url = URLDecoder.decode(auxUrl, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return url;
    }

    /**
     * Obtiene la url del video del episodios para el servidor amz
     * @param codFuente String que contiene las urls disponibles
     * @return la url del video
     */
    private String urlAmzServer(String codFuente) {
        String auxUrl = "", url = "";

        if (codFuente.contains("amz?v")) {
            Matcher localMatcher = Pattern.compile("amz\\?v=(.*?)\"").matcher(codFuente);
            while (localMatcher.find()) {
                auxUrl = localMatcher.group(1);
                url = "https://www.amazon.com/gp/drive/share?s=" + auxUrl;
            }
        }

        return url;
    }

    /**
     * Obtiene la url del video del episodios para el servidor amzcl
     * @param codFuente String que contiene las urls disponibles
     * @return la url del video
     */
    private String urlAmzclServer(String codFuente) {
        String auxUrl = "", url = "";
        boolean seEncuentraUrl = false;

        if (codFuente.contains("amzcl?v")) {
            Matcher localMatcher = Pattern.compile("amzcl\\?v=(.*?)\"").matcher(codFuente);
            while (localMatcher.find()) {
                auxUrl = localMatcher.group(1);
                auxUrl = "http://ozhe.larata.in/repro-rc/amzcl?v=" + auxUrl;
                seEncuentraUrl = true;
            }
        }

        //ahora obtiene la url del video
        if (seEncuentraUrl) {
            Utilities util = new Utilities();
            List<String> urlResponse = util.downloadWebPageTaskNoAsync(auxUrl);
            for (int j = 0; j < urlResponse.size(); j++) {
                if (urlResponse.get(j).contains("<script type='text/javascript'>")) { //linea donde se encuentra la url del video
                    Matcher localMatcher = Pattern.compile("file: \"(.*?)\"").matcher(urlResponse.get(j)); //obtiene la url del video completa
                    while (localMatcher.find()) {
                        auxUrl = localMatcher.group(1);
                        try {
                            url = URLDecoder.decode(auxUrl, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return url;
    }

    /** DEPRECADO*/
    private String urlAmzzServer(String codFuente) {
        String auxUrl = "", url = "";

        if (codFuente.contains("amzz?v")) {
            Matcher localMatcher = Pattern.compile("amzz\\?v=(.*?)\"").matcher(codFuente);
            while (localMatcher.find()) {
                auxUrl = localMatcher.group(1);
                try {
                    url = URLDecoder.decode("http://larata.in/amz/filerey/" + auxUrl + ".mp4", "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return url;
    }

    /** DEPRECADO*/
    private String urlPicasaServer(String codFuente) {
        String auxUrl = "", url = "";
        boolean seEncuentraUrl = false;


        if (codFuente.contains("picasa?v")) {
            Matcher localMatcher = Pattern.compile("picasa\\?v=(.*?)\"").matcher(codFuente);
            while (localMatcher.find()) {
                auxUrl = localMatcher.group(1);
                try {
                    url = URLDecoder.decode("http://my.mp4link.com/embed/picasa/code=" + auxUrl, "UTF-8");
                    seEncuentraUrl = true;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        //ahora obtiene
        if (seEncuentraUrl) {
            Utilities util = new Utilities();
            List<String> urlResponse = util.downloadWebPageTaskNoAsync(url);
            for (int j = 0; j < urlResponse.size(); j++) {
                //Log.d("response", urlResponse.get(j));
                if (urlResponse.get(j).contains("},{")) { //linea donde se encuentra la url del video
                    Matcher localMatcher = Pattern.compile("file: \"(.*?)\"").matcher(urlResponse.get(j)); //obtiene la url del video completa
                    while (localMatcher.find()) {
                        auxUrl = localMatcher.group(1);
                        try {
                            url = URLDecoder.decode(auxUrl, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return url;
    }

    public void añadirHistorialRey(String nombre, String urlEpisodio) {
        List<HistorialReyTable> lista = DataSupport.where("nombre=? and urlEpisodio=?", nombre, urlEpisodio).find(HistorialReyTable.class);
        if (lista.isEmpty()) {//si la lista no contiene el capitulo que se quiere añadir
            HistorialReyTable historialRey = new HistorialReyTable(nombre, urlEpisodio);
            historialRey.save();
        }
    }

    public void añadirHistorial(String nombre, String tipo, String urlImagen, String urlCapitulo) {
        List<HistorialTableRey> lista = DataSupport.where("nombre=? and urlCapitulo=?", nombre, urlCapitulo).find(HistorialTableRey.class);
        if (lista.isEmpty()) { //si la lista no contiene el capitulo que se quiere añadir
            int tamaño = DataSupport.count(HistorialTableRey.class);
            if (tamaño < 20) {
                HistorialTableRey historial = new HistorialTableRey(nombre, tipo, urlImagen, urlCapitulo);
                historial.save();
            }
            if (tamaño == 20) { //El historial tendra un maximo de 20 items
                //ELimina el primero
                HistorialTableRey primero = DataSupport.findFirst(HistorialTableRey.class);
                DataSupport.deleteAll(HistorialTableRey.class, "nombre=? and urlCapitulo=?", primero.getNombre(), primero.getUrlCapitulo());
                //Agrega el siguiente
                HistorialTableRey historial = new HistorialTableRey(nombre, tipo, urlImagen, urlCapitulo);
                historial.save();
            }
        } else {
            if (lista.size() == 1) { //si ya se encuentra en la lista
                //Se elimina de la posicion anterior
                DataSupport.deleteAll(HistorialTableRey.class, "nombre=? and urlCapitulo=?", lista.get(0).getNombre(), lista.get(0).getUrlCapitulo());
                //se agrega nuevamente
                HistorialTableRey historial = new HistorialTableRey(nombre, tipo, urlImagen, urlCapitulo);
                historial.save();
            }
        }
    }

    public List<HomeScreenEpi> busqueda(Document codigoFuente) {
        List<HomeScreenEpi> search = new ArrayList<>();
        String urlAnime, nombre, informacion, informacionAux, preview;
        Elements objetosBusqueda, objetosEpi;
        objetosBusqueda = codigoFuente.getElementsByClass("resultado");//contiene los episodios resultantes de la busqueda
        objetosEpi = objetosBusqueda.select("a"); //contiene los episodios diarios como una lista
        if (objetosEpi.isEmpty()) {
            search = null;
            Log.d("Empty", "busqueda reyanime no produce resultados");
        } else {
            Element dirAnime; //primer elemento del objeto objetosEpi que contiene el nombre, link e imagen del anime
            Element dirAnimeTipo; //elemento que contiene la informacion acerca si es pelicula, ova o serie
            for (int i = 0; i < objetosEpi.size(); i++) {
                preview = objetosEpi.get(i).select("img").attr("src");
                urlAnime = "http://reyanime.com" + objetosEpi.get(i).attr("href");
                nombre = objetosEpi.get(i).attr("title");
                informacionAux = objetosEpi.get(i).select("h3").select("i").text(); //si es pelicula, ova o serie
                informacion = informacionAux.replaceAll("[()]", ""); // quita los parentesis
                /*System.out.println("url  : " + urlAnime);
                System.out.println("Nombre  : " + nombre);
                System.out.println("Imagen  : " + preview);
                System.out.println("Tipo  : " + informacion);*/

                HomeScreenEpi item = new HomeScreenEpi(urlAnime, nombre, informacion, preview); //crea un item tipo homescreen y le añade los valores
                search.add(item); //agrega el item a la lista de items
            }
        }

        return search;
    }

    /*verifica si la pagina contiene mas elementos que mostrar en una segunda pagina.
    * En caso de existir devuelve la url de dicha pagina
    */
    public String siguientePagina(Document codigoFuente) {
        String urlPagina = "";

        Element numPaginas = codigoFuente.getElementsByClass("paginacion").select("div").first();
        if (numPaginas != null) {
            Element pagSiguiente = numPaginas.select("a").last();
            if (pagSiguiente != null) {
                Element siguiente = pagSiguiente.getElementsByClass("next").first();
                if (siguiente != null) {
                    urlPagina = "http://reyanime.com" + pagSiguiente.attr("href");
                }
            }
        }
        return urlPagina;
    }

    /*verifica si la pagina contiene mas elementos que mostrar en una segunda pagina para la seccion de generos
    * En caso de existir devuelve la url de dicha pagina
    */
    public String siguientePaginaGenero(Document codigoFuente, String url) {
        String urlPagina = "";

        Element numPaginas = codigoFuente.getElementsByClass("pagination").select("div").first();
        if (numPaginas != null) {
            Element pagSiguiente = numPaginas.select("a").last();
            if (pagSiguiente != null) {
                Element siguiente = pagSiguiente.getElementsByClass("next").first();
                if (siguiente != null) {
                    urlPagina = url + pagSiguiente.attr("href");
                }
            }
        }
        return urlPagina;
    }

    public List<Episodios> getEpisodios(Document codigoFuente, String urlAnime) {
        List<Episodios> episodios = new ArrayList<>();
        String informacion = "", fechaInicio = "", generos = "", nombreAnime = "", urlImagen = "", estado = "",
                urlEp = "", numero = "", tipo = "";

        Element allObj = codigoFuente.getElementsByClass("cuerpo-dentro").first(); //contiene sinopsis + episodios
        Element estadoAndUrlImgObj = allObj.getElementsByClass("izq-gris").first(); //sólo contiene url de la imagen y el estado de emision dle anime
        urlImagen = estadoAndUrlImgObj.select("img").attr("src");

        Element estadoObj = estadoAndUrlImgObj.getElementsByClass("box-emision").first();
        if (estadoObj != null) { //para cuando la serie se encuentra en transmision
            estado = estadoObj.text();
        } else { //para cuando la serie se encuentra finalizada
            estado = estadoAndUrlImgObj.getElementsByClass("box-finalizada").first().text();
        }

        Element infoObj = allObj.getElementsByClass("conten-box").first(); //objeto que contiene la informacion restante del anime
        nombreAnime = infoObj.select("h1").first().ownText();
        tipo = infoObj.select("h1").first().select("b").first().text();
        tipo = tipo.replaceAll("[()]", ""); //elimina los parentesis
        Element sinopsisObj = infoObj.getElementsByClass("sinopsis").first();
        informacion = sinopsisObj.ownText(); //ownText devulve todo lo que no esta dentro de los elementos hijos
        fechaInicio = sinopsisObj.select("span").text();
        generos = sinopsisObj.select("b").text();

        Elements episodiosObj = infoObj.getElementsByAttributeValue("id", "box-cap").first().select("a");

        for (int i = 0; i < episodiosObj.size(); i++) {
            urlEp = "http://reyanime.com" + episodiosObj.get(i).attr("href");
            numero = episodiosObj.get(i).attr("title");
            if (i != 0) {
                episodios.add(new Episodios(null, null, urlEp, numero, null, null, null, null, null, null));
            } else {
                episodios.add(new Episodios(nombreAnime, urlAnime, urlEp, numero, urlImagen, informacion, tipo, estado, generos, fechaInicio));
            }
        }
       /* System.out.println("nombre   " + nombreAnime);
        System.out.println("info   " + informacion);
        System.out.println("fechaInicio   " + fechaInicio);
        System.out.println("generos   " + generos);
        System.out.println("urlimagen   " + urlImagen);
        System.out.println("emision   " + estado);
        System.out.println("urlep   " + urlEp);
        System.out.println("numero   " + numero);
        System.out.println("urlanime   " + urlAnime);
        System.out.println("tipo   " + tipo);*/

        return episodios;
    }

    public String urlCapituloToUrlAnime(Document codigoFuente) {
        String urlAnime = null;

        Elements objEpisodios;
        if (codigoFuente != null) {
            objEpisodios = codigoFuente.getElementsByClass("conten-capitulo");
            if (objEpisodios.isEmpty()) {
                Log.d("Error", "No se puedo encontrar la url del anime");
            } else {
                String titulo = objEpisodios.select("a").first().attr("href");
                urlAnime = "http://reyanime.com" + titulo;
                // Log.d("URL", urlAnime);
            }
        }
        return urlAnime;
    }

    public List<GenerosClass> generosDisponibles(Document codigoFuente) {
        List<GenerosClass> resultado = new ArrayList<>();
        String nombre, url;

        Elements generos = codigoFuente.getElementsByClass("lista-hoja-genero-2").first().select("a");
        resultado.add(new GenerosClass("acción", "http://reyanime.com/genero/accion")); //se agrega ya que este se encuentra por default
        for (int i = 0; i < generos.size(); i++) {
            nombre = generos.get(i).text();
            url = "http://reyanime.com" + generos.get(i).attr("href");

            resultado.add(new GenerosClass(nombre, url));
        }

        return resultado;
    }

    /*Entrega el anime contenido dentro de las secciondes de genero de reyanime*/
    public List<HomeScreenEpi> animePorGenero(Document codigoFuente) {
        List<HomeScreenEpi> anime = new ArrayList<>();
        String urlAnime, nombre, informacion, preview;

        Elements animeObj = codigoFuente.getElementsByClass("paginacion-alta").first().select("a");

        for (int i = 0; i < animeObj.size(); i++) {
            urlAnime = "http://reyanime.com" + animeObj.get(i).attr("href");
            nombre = animeObj.get(i).select("span").text();
            informacion = ""; //reaynime no proporciona el tipo de anime en esta seccion
            preview = animeObj.get(i).select("img").first().attr("src");

            HomeScreenEpi item = new HomeScreenEpi(urlAnime, nombre, informacion, preview);
            anime.add(item);
        }

        return anime;
    }

    public boolean seEncuentraEnHistorialRey(String nombre, String urlEpisodio) {
        List<HistorialReyTable> lista = DataSupport.where("nombre=? and urlEpisodio=?", nombre, urlEpisodio).find(HistorialReyTable.class);
        if (lista.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Recibe parte de la url y la transforma a una url correcta, Ej:
     * recibe anime/Minami Kamakura Koukou Joshi Jitensha-bu.jpg
     * retorna http://reyanime.com/anime/Minami%20Kamakura%20Koukou%20Joshi%20Jitensha-bu.jpg
     * @param urlParcial String que conforma parte de la url
     * @return String que representa la url real de la imangen
     */
    private String urlImagenPreview(String urlParcial) {
        String url = "";
        URI urlAux;
        try {
            urlAux = new URI("http", "reyanime.com", urlParcial, null);
            url = urlAux.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return url;
    }
}
