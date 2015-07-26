package com.ouiaboo.ouiaboo.clases;

/**
 * Created by Vareta on 23-07-2015.
 */
public class SitiosWeb{

    private String nombre;
    private String idioma;

    public SitiosWeb(String nombre, String idioma){
        this.nombre = nombre;
        this.idioma = idioma;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setIdioma(String idioma){
        this.idioma = idioma;
    }

    public String getNombre(){
        return nombre;
    }

    public String getIdioma(){
        return idioma;
    }
}