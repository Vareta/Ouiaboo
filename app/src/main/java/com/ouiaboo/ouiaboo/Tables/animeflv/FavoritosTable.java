package com.ouiaboo.ouiaboo.Tables.animeflv;

import org.litepal.crud.DataSupport;

/**
 * Created by Vareta on 03-10-2015.
 */
public class FavoritosTable extends DataSupport {
    private String nombre;
    private String tipo;
    private String urlImagen;
    private String urlAnime;

    public FavoritosTable(String nombre, String tipo, String urlImagen, String urlAnime) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.urlImagen = urlImagen;
        this.urlAnime = urlAnime;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }
    public void setUrlAnime(String urlAnime) {
        this.urlAnime = urlAnime;
    }
    public String getNombre() {
        return nombre;
    }
    public String getTipo() {
        return tipo;
    }
    public String getUrlImagen() {
        return urlImagen;
    }
    public String getUrlAnime() {
        return urlAnime;
    }
}
