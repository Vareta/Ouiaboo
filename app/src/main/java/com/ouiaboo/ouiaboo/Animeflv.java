package com.ouiaboo.ouiaboo;

import android.util.Log;

import com.ouiaboo.ouiaboo.clases.HomeScreenAnimeFLV;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vareta on 29-07-2015.
 */
public class Animeflv {

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
                                    informacion = "Episodio número " + div[div.length - 1];
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

}
