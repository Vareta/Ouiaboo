package com.ouiaboo.ouiaboo;

import com.ouiaboo.ouiaboo.Tables.VerMasTardeTable;
import com.ouiaboo.ouiaboo.clases.HomeScreenAnimeFLV;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Vareta on 04-10-2015.
 */
public class Funciones {

    /*Funcion para agregar a ver mas tarde desde el Home de la app */
    public boolean verMasTardeHome(HomeScreenAnimeFLV home) {

        List<VerMasTardeTable> lista = DataSupport.where("nombre=? and tipo=?", home.getNombre(), home.getInformacion()).find(VerMasTardeTable.class);
        if (!lista.isEmpty()){ //si la lista contiene el capitulo que se quiere añadir
            System.out.println(DataSupport.count(VerMasTardeTable.class));
            return false;
        } else {
            VerMasTardeTable masTarde = new VerMasTardeTable(home.getNombre(), home.getInformacion(), home.getPreview(), home.getUrlCapitulo());
            masTarde.save();
            return true;
        }
    }
}
