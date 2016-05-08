package com.ouiaboo.ouiaboo.Tables.animeflv;

import org.litepal.crud.DataSupport;

/**
 * Created by Vareta on 17-11-2015.
 */
public class HistorialFlvTable extends DataSupport {
    private String nombre;
    private String urlEpisodio;

    public HistorialFlvTable(String nombre, String urlEpisodio) {
        this.nombre = nombre;
        this.urlEpisodio = urlEpisodio;
    }

    public HistorialFlvTable() {

    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setUrlEpisodio(String urlEpisodio) {
        this.urlEpisodio = urlEpisodio;
    }
    public String getNombre() {
        return nombre;
    }
    public String getUrlEpisodio() {
        return urlEpisodio;
    }

}
