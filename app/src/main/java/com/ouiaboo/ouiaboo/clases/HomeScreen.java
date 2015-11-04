package com.ouiaboo.ouiaboo.clases;

import java.io.Serializable;

/**
 * Created by Vareta on 27-07-2015.
 */
public class HomeScreen implements Serializable{

    private String urlCapitulo;
    private String nombre;
    private String informacion;
    private String preview;

    public HomeScreen(String urlCapitulo, String nombre, String informacion, String preview) {
        this.urlCapitulo = urlCapitulo;
        this.nombre = nombre;
        this.informacion = informacion;
        this.preview = preview;
    }


    public void setUrlCapitulo(String urlCapitulo) {
        this.urlCapitulo = urlCapitulo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getUrlCapitulo() {
        return urlCapitulo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getInformacion() {
        return informacion;
    }

    public String getPreview() {
        return preview;
    }
}
