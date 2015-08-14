package com.ouiaboo.ouiaboo.clases;

import java.io.Serializable;

/**
 * Created by Vareta on 14-08-2015.
 */
public class Episodios implements Serializable {
    private String url;
    private String numero;
    private String urlImagen;
    private String informacion;

    public Episodios(String url, String numero, String urlImagen, String informacion){
        this.url = url;
        this.numero = numero;
        this.urlImagen = urlImagen;
        this.informacion = informacion;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }
    public void setUrlImage(String urlImage) {
        this.urlImagen = urlImagen;
    }
    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }
    public String getUrl(){
        return url;
    }
    public String getNumero(){
        return numero;
    }
    public String getUrlImagen() {
        return urlImagen;
    }
    public String getInformacion(){
        return informacion;
    }

}
