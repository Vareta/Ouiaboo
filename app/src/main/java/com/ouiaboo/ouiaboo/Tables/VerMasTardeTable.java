package com.ouiaboo.ouiaboo.Tables;

import org.litepal.crud.DataSupport;

/**
 * Created by Vareta on 03-10-2015.
 */
public class VerMasTardeTable extends DataSupport {
    private String nombre;
    private String tipo;
    private String urlImagen;
    private String urlCapitulo;

    public VerMasTardeTable(String nombre, String tipo, String urlImagen, String urlCapitulo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.urlImagen = urlImagen;
        this.urlCapitulo = urlCapitulo;
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
    public void setUrlCapitulo(String urlAnime) {
        this.urlCapitulo = urlAnime;
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
    public String getUrlCapitulo() {
        return urlCapitulo;
    }
}