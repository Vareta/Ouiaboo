package com.ouiaboo.ouiaboo.util;

import android.os.Environment;

import com.ouiaboo.ouiaboo.Tables.DescargadosTable;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Vareta on 25-07-2017.
 */

public class CRUD {

    /******************************************************************************************************
     * ****************************************DESCARGADOSTABLE********************************************
     ******************************************************************************************************/


    /**
     * Registra el inicio de la descarga del capitulo de anime
     * @param idDescarga long con el ID que se le otorga a la descarga del video
     * @param nombreAnime String que contiene el nombre del anime
     * @param numeroCapitulo String que contiene el numero del capitulo
     * @param nombreVideoDescargado String que contiene el nombre que llevara el capitulo en el directorio
     * @param urlCapitulo String que contiene la url del capitulo
     */
    public void registraInicioDescarga(long idDescarga, String nombreAnime, String numeroCapitulo, String nombreVideoDescargado, String urlCapitulo) {
        DescargadosTable descargas = new DescargadosTable(idDescarga, nombreAnime, numeroCapitulo,
                null, //preview, null por defecto (ya que aun no se tiene el preview)
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Ouiaboo/" + nombreVideoDescargado,
                urlCapitulo,
                false); //estado de la descarga, falso (ya que recien se inicia)
        descargas.save();
    }

    public void actualizaEstadosDescarga() {
        List<DescargadosTable> todasLasDescargas = descargas();
        if (!todasLasDescargas.isEmpty()) {

        }
    }

    /**
     * Remueve un elemento de la tabla de descargas
     * @param id long con el ID del elemento en la tabla
     */
    public void removeDownload(long id) {
        DataSupport.delete(DescargadosTable.class, id);
    }

    /**
     * Actualiza el estado de una descarga (si esta completa o aun no)
     * @param id long ID del elemento en la tabla
     * @param estado boolean Estado de la descarga actual (true = completo, false = incompleta)
     */
    public void updateEstado(long id, boolean estado) {
        DescargadosTable descargados = new DescargadosTable();
        descargados.setComplete(estado); // raise the price
        descargados.update(id);
    }

    /**
     * Retorna todas las descargas registradas
     * @return Lista con las descargas
     */
    public List<DescargadosTable> descargas() {
        return DataSupport.findAll(DescargadosTable.class);
    }

    /**
     * Actualiza el preview de un video descargado
     * @param id long ID del elemento en la tabla
     * @param direccionPreview Dirección en el disco donde se encuentra almacenado el preview
     */
    public void updatePreview(long id, String direccionPreview) {
        DescargadosTable descargados = new DescargadosTable();
        descargados.setImagenPreview(direccionPreview);
        descargados.update(id);
    }

    /**
     * Obtiene todas las descargas exitosas
     * @return Lista con las descargas encontradas
     */
    public List<DescargadosTable> obtenerDescargasExitosas() {
        return DataSupport.where("complete=?", String.valueOf(1)).find(DescargadosTable.class);
    }

}
