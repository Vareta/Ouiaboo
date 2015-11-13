package com.ouiaboo.ouiaboo.clases;

import java.io.Serializable;

/**
 * Created by Vareta on 09-11-2015.
 */
public class GenerosClass implements Serializable {
    public String nombre;
    public String urlGenero;

    public GenerosClass(String nombre, String urlGenero) {
        this.nombre = nombre;
        this.urlGenero = urlGenero;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setUrlGenero(String urlGenero) {
        this.urlGenero = urlGenero;
    }
    public String getNombre() {
        return nombre;
    }
    public String getUrlGenero() {
        return urlGenero;
    }

}
