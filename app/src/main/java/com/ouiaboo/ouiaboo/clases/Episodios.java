package com.ouiaboo.ouiaboo.clases;

import java.io.Serializable;

/**
 * Created by Vareta on 14-08-2015.
 */
public class Episodios implements Serializable  {
    private String nombreAnime;
    private String urlAnime;
    private String urlEpisodio;
    private String numero;
    private String urlImagen;
    private String informacion;
    private String tipo;
    private String estado;
    private String generos;
    private String fechaInicio;


    public Episodios(String nombreAnime, String urlAnime, String urlEpisodio, String numero, String urlImagen, String informacion,
                     String tipo, String estado, String generos, String fechaInicio){
        this.nombreAnime = nombreAnime;
        this.urlAnime =urlAnime;
        this.urlEpisodio = urlEpisodio;
        this.numero = numero;
        this.urlImagen = urlImagen;
        this.informacion = informacion;
        this.tipo = tipo;
        this.estado = estado;
        this.generos = generos;
        this.fechaInicio = fechaInicio;

    }

    public void setNombreAnime(String nombreAnime) {
        this.nombreAnime = nombreAnime;
    }
    public void setUrlAnime(String urlAnime) {
        this.urlAnime = urlAnime;
    }
    public void setUrlEpisodio(String urlEpisodio) {
        this.urlEpisodio = urlEpisodio;
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
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public void setGeneros(String generos) {
        this.generos = generos;
    }
    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    public String getNombreAnime() {
        return nombreAnime;
    }
    public String getUrlAnime() {
        return urlAnime;
    }
    public String getUrlEpisodio(){
        return urlEpisodio;
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
    public String getTipo() {
        return tipo;
    }
    public String getEstado() {
        return estado;
    }
    public String getGeneros() {
        return generos;
    }
    public String getFechaInicio() {
        return fechaInicio;
    }

}
