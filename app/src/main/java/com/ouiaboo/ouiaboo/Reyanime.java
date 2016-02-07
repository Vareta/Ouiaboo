package com.ouiaboo.ouiaboo;

import android.content.res.Resources;
import android.util.Log;

import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

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
            informacion = objetosEpi.get(i).select("sombra").text(); //numero capitulo

             System.out.println("url  : " + urlAnime);
             System.out.println("Nombre  : " + nombre);
             System.out.println("Imagen  : " + preview);
             System.out.println("Tipo  : " + informacion);

            HomeScreenEpi item = new HomeScreenEpi(urlAnime, nombre, informacion, preview); //crea un item tipo homescreen y le añade los valores
            home.add(item); //agrega el item a la lista de items

        }

        return home;
    }
}
