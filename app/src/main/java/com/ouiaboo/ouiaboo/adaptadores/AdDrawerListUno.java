package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;

import java.util.List;


/**
 * Created by Vareta on 24-07-2015.
 */
public class AdDrawerListUno extends ArrayAdapter {

    public AdDrawerListUno(Context context, List objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawer_list_uno, null);
        }

        ImageView icon = (ImageView)convertView.findViewById(R.id.icono);
        TextView nombre = (TextView)convertView.findViewById(R.id.nombre);

        DrawerItemsListUno item = (DrawerItemsListUno)getItem(position);
        icon.setImageResource(item.getIconId());
        nombre.setText(item.getNombre());

        return convertView;
    }
}
