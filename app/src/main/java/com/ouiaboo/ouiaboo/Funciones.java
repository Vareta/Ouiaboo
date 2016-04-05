package com.ouiaboo.ouiaboo;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.Tables.animeflv.FavoritosTable;
import com.ouiaboo.ouiaboo.Tables.animeflv.VerMasTardeTable;
import com.ouiaboo.ouiaboo.Tables.reyanime.FavoritosTableRey;
import com.ouiaboo.ouiaboo.Tables.reyanime.VerMasTardeTableRey;
import com.ouiaboo.ouiaboo.clases.Episodios;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Vareta on 04-10-2015.
 */
public class Funciones {

    /*Funcion para agregar a ver mas tarde desde el Home de la app */
    public boolean esPosibleverMasTardeHome(HomeScreenEpi home, int proveedor) {

        if (proveedor == Utilities.ANIMEFLV) {
            List<VerMasTardeTable> lista = DataSupport.where("nombre=? and tipo=?", home.getNombre(), home.getInformacion()).find(VerMasTardeTable.class);
            if (!lista.isEmpty()) { //si la lista contiene el capitulo que se quiere añadir
                //System.out.println(DataSupport.count(VerMasTardeTable.class));
                return false;
            } else {
                VerMasTardeTable masTarde = new VerMasTardeTable(home.getNombre(), home.getInformacion(), home.getPreview(), home.getUrlCapitulo());
                masTarde.save();
                return true;
            }
        } else { //Reyanime
            List<VerMasTardeTableRey> lista = DataSupport.where("nombre=? and tipo=?", home.getNombre(), home.getInformacion()).find(VerMasTardeTableRey.class);
            if (!lista.isEmpty()) {
                return false;
            } else {
                VerMasTardeTableRey masTarde = new VerMasTardeTableRey(home.getNombre(), home.getInformacion(), home.getPreview(), home.getUrlCapitulo());
                masTarde.save();
                return true;
            }
        }
    }
    /*DEPRECADO HASTA NUEVO AVISO
    public boolean añadeaFavoritos(Episodios home) {

        List<FavoritosTable> lista = DataSupport.where("nombre=?", home.getNombreAnime()).find(FavoritosTable.class);
        if (!lista.isEmpty()) { //si la lista de favoritos contiene el anime que se quiere añadir lo elimina
            DataSupport.deleteAll(FavoritosTable.class, "nombre=?", home.getNombreAnime());
            return false;
        } else { //caso contrario, lo añade
            FavoritosTable favoritos = new FavoritosTable(home.getNombreAnime(), home.getTipo(), home.getUrlImagen(), home.getUrlAnime());
            favoritos.save();
            return true;
        }
    }*/

    public void añadirFavorito(Episodios home, int proveedor) {
        if (proveedor == Utilities.ANIMEFLV) {
            FavoritosTable favoritos = new FavoritosTable(home.getNombreAnime(), home.getTipo(), home.getUrlImagen(), home.getUrlAnime());
            favoritos.save();
        } else { //reyanime
            FavoritosTableRey favoritos = new FavoritosTableRey(home.getNombreAnime(), home.getTipo(), home.getUrlImagen(), home.getUrlAnime());
            favoritos.save();
        }
    }

    public void eliminarFavorito(Episodios home, int proveedor) {
        if (proveedor == Utilities.ANIMEFLV) {
            DataSupport.deleteAll(FavoritosTable.class, "nombre=?", home.getNombreAnime());
        } else {//reyanime
            DataSupport.deleteAll(FavoritosTableRey.class, "nombre=?", home.getNombreAnime());
        }
    }

    public boolean estaEnFavoritos(Episodios home, int proveedor) {
        if (proveedor == Utilities.ANIMEFLV) {
            List<FavoritosTable> lista = DataSupport.where("nombre=?", home.getNombreAnime()).find(FavoritosTable.class);
            if (!lista.isEmpty()) { //si la lista de favoritos contiene el anime que se quiere añadir
                return true;
            } else { //caso contrario
                return false;
            }
        } else { //reyanime
            List<FavoritosTableRey> lista = DataSupport.where("nombre=?", home.getNombreAnime()).find(FavoritosTableRey.class);
            if (!lista.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void actualizarProveedorNavHeader(FragmentActivity fragmentActivity, Context context) {
        NavigationView navigationView = (NavigationView) fragmentActivity.findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        TextView proveedor = (TextView) navHeader.findViewById(R.id.pag_actual);
        Utilities util = new Utilities();
        if (util.queProveedorEs(context) == Utilities.ANIMEFLV) {
            proveedor.setText(R.string.animeflv_drawer_layout);
        } else {//reyanime
            proveedor.setText(R.string.reyanime_drawer_layout);
        }
    }


}
