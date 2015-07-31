package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.clases.HomeScreenAnimeFLV;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Vareta on 27-07-2015.
 */
public class AdaptadorHomeScreenAnimeFLV extends ArrayAdapter {

    public AdaptadorHomeScreenAnimeFLV (Context context, List objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.home_screen_animeflv, null);
        }

        TextView nombre = (TextView)convertView.findViewById(R.id.nombre_flv);
        TextView informacion = (TextView)convertView.findViewById(R.id.informacion_flv);
        ImageView preview = (ImageView)convertView.findViewById(R.id.preview_flv);

        HomeScreenAnimeFLV item = (HomeScreenAnimeFLV)getItem(position);
        nombre.setText(Html.fromHtml(item.getNombre()));
        informacion.setText(item.getInformacion());

        //agrega el preview al imageview via url

       // new Utilities.DownloadImageTask(preview).execute(item.getPreview());
        Picasso.with(parent.getContext()).load(item.getPreview()).resize(250, 150).into(preview);
        return convertView;
    }

}
