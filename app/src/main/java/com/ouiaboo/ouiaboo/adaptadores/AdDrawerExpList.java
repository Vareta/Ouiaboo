package com.ouiaboo.ouiaboo.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;
import com.ouiaboo.ouiaboo.clases.SitiosWeb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vareta on 26-07-2015.
 */
public class AdDrawerExpList extends BaseExpandableListAdapter {

    private Context _context;
    private HashMap<DrawerItemsListUno, List<SitiosWeb>> paginasWeb;
    private List<DrawerItemsListUno> tituloPadre;


    public AdDrawerExpList(Context context, List<DrawerItemsListUno> objects, HashMap<DrawerItemsListUno, List<SitiosWeb>> pagWebDesplegable) {
        this._context = context;
        this.tituloPadre = objects;
        this.paginasWeb = pagWebDesplegable;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return  this.paginasWeb.get(this.tituloPadre.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawer_expandable_parent_list, null);
        }

        TextView titulo = (TextView)convertView.findViewById(R.id.pagina);
        TextView subtitulo = (TextView)convertView.findViewById(R.id.idioma);

        SitiosWeb item = (SitiosWeb)getChild(groupPosition, childPosition);

        titulo.setText(item.getNombre());
        subtitulo.setText(item.getIdioma());

        return  convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.paginasWeb.get(this.tituloPadre.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.tituloPadre.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.tituloPadre.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawer_list_uno, null);
        }


        ImageView icon = (ImageView)convertView.findViewById(R.id.icono);
        TextView nombre = (TextView)convertView.findViewById(R.id.nombre);

        DrawerItemsListUno item = (DrawerItemsListUno)getGroup(groupPosition);
        icon.setImageResource(item.getIconId());
        nombre.setText(item.getNombre());

        return  convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
