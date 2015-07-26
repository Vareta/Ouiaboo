package com.ouiaboo.ouiaboo.clases;

/**
 * Created by Vareta on 24-07-2015.
 */
public class DrawerItemsListUno {
    private String nombre;
    private int iconId;

    public DrawerItemsListUno(String nombre, int iconId) {
        this.nombre = nombre;
        this.iconId = iconId;
    }

    public void setNombre() {
        this.nombre = nombre;
    }

    public void setIconId() {
        this.iconId = iconId;
    }

    public String getNombre() {
        return nombre;
    }

    public int getIconId() {
        return iconId;
    }
}
