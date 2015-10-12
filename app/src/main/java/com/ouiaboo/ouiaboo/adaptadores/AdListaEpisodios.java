package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.clases.Episodios;

import java.util.List;

/**
 * Created by Vareta on 09-10-2015.
 */
public class AdListaEpisodios extends ArrayAdapter {

    public AdListaEpisodios(Context context, List objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lista_episodios, null);
        }

        TextView nombre = (TextView)convertView.findViewById(R.id.nombre);

        Episodios episodios = (Episodios)getItem(position);
        nombre.setText(episodios.getNumero());

        return convertView;
    }
}
