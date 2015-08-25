package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;
import com.ouiaboo.ouiaboo.clases.Episodios;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Vareta on 18-08-2015.
 */
public class AdInfoEpisodios extends BaseExpandableListAdapter{
    private Context context;
    private HashMap<DrawerItemsListUno, List<Episodios>> informacion;
    private List<DrawerItemsListUno> infoPadre;

    public AdInfoEpisodios(Context context, List<DrawerItemsListUno> infoPadre, HashMap<DrawerItemsListUno, List<Episodios>> informacion) {
        this.context = context;
        this.infoPadre = infoPadre;
        this.informacion = informacion;
    }

    @Override
    public int getGroupCount() {
        return this.infoPadre.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.informacion.get(this.infoPadre.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.infoPadre.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.informacion.get(this.infoPadre.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawer_list_uno, null);
        }

        ImageView icon = (ImageView)convertView.findViewById(R.id.icono);
        TextView nombre = (TextView)convertView.findViewById(R.id.nombre);

        DrawerItemsListUno item = (DrawerItemsListUno)getGroup(groupPosition);
        icon.setImageResource(item.getIconId());
        nombre.setText(item.getNombre());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater  = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.episodio_info, null);
        }

        ImageView portada = (ImageView)convertView.findViewById(R.id.img_portada);
        TextView tipo = (TextView)convertView.findViewById(R.id.tipo);
        TextView estado = (TextView)convertView.findViewById(R.id.estado);
        TextView fechaInicio = (TextView)convertView.findViewById(R.id.fecha_inicio);
        TextView sinopsis = (TextView)convertView.findViewById(R.id.sinopsis);

        Episodios episodios = (Episodios)getChild(groupPosition, childPosition);

        Picasso.with(context).load(episodios.getUrlImagen()).resize(250, 400).into(portada);
        tipo.setText(episodios.getTipo());
        estado.setText(episodios.getEstado());
        fechaInicio.setText(episodios.getFechaInicio());
        sinopsis.setText(episodios.getInformacion());


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
