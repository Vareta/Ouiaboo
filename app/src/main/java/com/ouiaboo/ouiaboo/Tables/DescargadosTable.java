package com.ouiaboo.ouiaboo.Tables;



import org.litepal.crud.DataSupport;

/**
 * Created by Vareta on 15-10-2015.
 */
public class DescargadosTable extends DataSupport {
    long idDescarga;
    String nombre;
    String tipo;
    String imagenPreview;
    String dirVideo;
    String urlCapitulo;
    boolean complete;


    public DescargadosTable(long idDescarga, String nombre, String tipo, String imagenPreview, String dirVideo, String urlCapitulo, boolean complete) {
        this.idDescarga = idDescarga;
        this.nombre = nombre;
        this.tipo = tipo;
        this.imagenPreview = imagenPreview;
        this.dirVideo = dirVideo;
        this.urlCapitulo = urlCapitulo;
        this.complete = complete;
    }

    public DescargadosTable() {

    }

    public void setIdDescarga(long idDescarga) {
        this.idDescarga = idDescarga;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public void setImagenPreview(String imagenPreview) {
        this.imagenPreview = imagenPreview;
    }
    public void setDirVideo(String dirVideo) {
        this.dirVideo = dirVideo;
    }
    public void setUrlCapitulo(String urlCapitulo) {
        this.urlCapitulo = urlCapitulo;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    public long getIdDescarga() {
        return idDescarga;
    }
    public String getNombre() {
        return nombre;
    }
    public String getTipo() {
        return tipo;
    }
    public String getImagenPreview() {
        return imagenPreview;
    }
    public String getDirVideo() {
        return dirVideo;
    }
    public String getUrlCapitulo() {
        return urlCapitulo;
    }
    public boolean isComplete() {
        return complete;
    }
}