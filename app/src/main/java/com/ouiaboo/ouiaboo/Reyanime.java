package com.ouiaboo.ouiaboo;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.ouiaboo.ouiaboo.Tables.reyanime.HistorialReyTable;
import com.ouiaboo.ouiaboo.Tables.reyanime.HistorialTableRey;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vareta on 07-02-2016.
 */
public class Reyanime {

    public List<HomeScreenEpi> homeScreen(Document codigoFuente, Resources resources){
        List<HomeScreenEpi> home = new ArrayList<>();
        String urlAnime, nombre, informacion, informacionAux, preview;
        Elements objetosHome, objetosEpi;
        objetosHome = codigoFuente.getElementsByClass("emision");//contiene los episodios diarios
        objetosEpi = objetosHome.select("a"); //contiene los episodios diarios como una lista

        Element nombreEpi; //elemento que contiene el nombre del episodio
        Element dirAnimeTipo; //elemento que contiene la informacion acerca si es pelicula, ova o serie
        for (int i = 0; i < objetosEpi.size(); i++) {
            nombreEpi = objetosEpi.get(i).getElementsByClass("sobre").first();
            nombre = nombreEpi.select("name").text(); //nombre anime
            urlAnime = "http://reyanime.com" + objetosEpi.get(i).attr("href");
            preview = objetosEpi.get(i).select("img").attr("src"); //url imagen
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
        List<String> codFuente = util.downloadWebPageTaskNoAsync(urlEpisodio); //obtiene el codigo fuente en forma de una lista de string
        String urlAux;
        boolean zeroDisponible = true, ichiDisponible = false; //nombre de los servidores

        urlAux = urlZeroServer(codFuente);
        if (!urlAux.equals("")) {
            if (util.isServerReachable(urlAux, context)) {
                url = urlAux;
            } else {
                zeroDisponible = false;
            }
        } else {
            zeroDisponible = false;
        }

        if (!zeroDisponible) {
            urlAux = urlIchiServer(codFuente);
            if (!urlAux.equals("")) {
                if (util.isServerReachable(urlAux, context)) {
                    url = urlAux;
                    ichiDisponible = true;
                }
            }
        }

        return url;
    }

    private String urlZeroServer(List<String> codFuente) {
        String auxUrl = "", url = "";
        int max = codFuente.size();

        for (int i = 0; i < max; i++) {
            if (codFuente.get(i).contains("tabsArray['12']")) {
                Matcher localMatcher = Pattern.compile("repro-rc\\/az\\?v=(.*?)\"").matcher(codFuente.get(i));
                while (localMatcher.find()) {
                    auxUrl = localMatcher.group(1);
                    try {
                        url = URLDecoder.decode("http://larata.in/amz/filerey/" + auxUrl + ".mp4", "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                break; //para no seguir buscando hasta el final del codigo fuente
            }
        }

        return url;
    }

    private String urlIchiServer(List<String> codFuente) {
        String auxUrl = "", url = "";
        int max = codFuente.size();

        for (int i = 0; i < max; i++) {
            if (codFuente.get(i).contains("tabsArray['1']")) {
                Matcher localMatcher = Pattern.compile("send\\?v=(.*?)\"").matcher(codFuente.get(i));
                while (localMatcher.find()) {
                    auxUrl = localMatcher.group(1);
                    try {
                        url = URLDecoder.decode("http://4.sendvid.com/" + auxUrl + ".mp4", "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                break; //para no seguir buscando hasta el final del codigo fuente
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
        if (lista.isEmpty()){ //si la lista no contiene el capitulo que se quiere añadir
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

    public List<HomeScreenEpi> busqueda(Document codigoFuente){
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
                informacion = informacionAux.replaceAll("[()]", "");
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
        Element pagSiguiente = numPaginas.select("a").last();
        Element siguiente = pagSiguiente.getElementsByClass("next").first();
        if (siguiente != null) {
            urlPagina = "http://reyanime.com" + pagSiguiente.attr("href");
        }

        return urlPagina;
    }
}
