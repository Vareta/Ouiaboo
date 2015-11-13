package com.ouiaboo.ouiaboo;

import android.content.res.Resources;
import android.util.Log;


import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Vareta on 04-11-2015.
 */
public class Animejoy {

    public ArrayList<HomeScreenEpi> homeScreenAnimejoy(Document codigoFuente, Resources resources) {
        ArrayList<HomeScreenEpi> home = new ArrayList<>();
        String urlCapitulo, nombre, informacion, preview, aux;
        String animeJoy = "http://www.animejoy.tv/";
        Element objHome = codigoFuente.getElementsByClass("animelist").first();
        Elements episodios = objHome.getElementsByClass("hanime");
        Log.d("TAMAÑO", String.valueOf(episodios.size()));
        Element classImg;
        Element img;
        Element classNombre;
        Element name;
        Element classUrl;
        Element url;

        for (int i = 0; i < episodios.size(); i++) {
            //consigue la url de la imagen
            classImg = episodios.get(i).getElementsByClass("hanimeleft").first();
            img = classImg.select("img").first();
            preview = img.attr("src");
            //consigue el nombre
            classNombre = episodios.get(i).getElementsByClass("hanimeh1").first();
            name = classNombre.select("a").first();
            nombre = name.text();
            //consigue la url del episodio
            classUrl = episodios.get(i).getElementsByClass("hanimeh2").first();
            url = classUrl.select("a").first();
            urlCapitulo = animeJoy + url.attr("href");
            //consigue el numero del capitulo
            aux = url.text(); //obtiene el nombre completo del capitulo, Ej: one piece episodio x
            String[] nomCompleto = aux.split(" "); //se divide para sólo obtener el numero del capitulo
            informacion = resources.getString(R.string.numero_episodio_menu_central_ES) + " " + nomCompleto[nomCompleto.length - 1];
            /*Log.d("PREVIEW", preview);
            Log.d("NOMBRE", nombre);
            Log.d("URLCAPITULO", urlCapitulo);
            Log.d("INFORMACION", informacion);*/
            home.add(new HomeScreenEpi(urlCapitulo, nombre, informacion, preview)); //agrega el nuevo objeto al array
        }

        return home;
    }

    public String urlVideo(Document codigoFuente) {
        String url;
        Element videoPlayer, objVideo;

        videoPlayer = codigoFuente.getElementById("flowplayer"); //obtiene el elemento que contiene el reproductor de video
        objVideo = videoPlayer.select("source").first(); //obtiene el objeto de video del reproductor de video
        url = objVideo.attr("src"); //obtiene la url del video

        return url;
    }

}
