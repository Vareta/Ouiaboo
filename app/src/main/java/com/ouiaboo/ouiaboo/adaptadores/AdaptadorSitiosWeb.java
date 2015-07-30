package com.ouiaboo.ouiaboo.adaptadores;

/**
 * Created by Vareta on 23-07-2015.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.clases.SitiosWeb;

import java.util.List;

public class AdaptadorSitiosWeb extends ArrayAdapter<SitiosWeb> {

    public AdaptadorSitiosWeb(Context context, List<SitiosWeb> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con image_list_view.xml
            listItemView = inflater.inflate(R.layout.paginas_anime_lista, parent, false);
        }

        //Obteniendo instancias de los elementos
        TextView titulo = (TextView) listItemView.findViewById(R.id.pagina);
        TextView subtitulo = (TextView) listItemView.findViewById(R.id.idioma);

        //Obteniendo instancia de la Tarea en la posicion actual
        SitiosWeb item = getItem(position);

        titulo.setText(item.getNombre());
        subtitulo.setText(item.getIdioma());

        //Devolver al ListView la fila creada
        return listItemView;

    }
}