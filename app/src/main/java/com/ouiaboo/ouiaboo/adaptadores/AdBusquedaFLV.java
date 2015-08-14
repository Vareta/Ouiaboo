package com.ouiaboo.ouiaboo.adaptadores;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.VideoPlayer;
import com.ouiaboo.ouiaboo.clases.HomeScreenAnimeFLV;
import com.ouiaboo.ouiaboo.fragmentsFLV.Busqueda;
import com.ouiaboo.ouiaboo.fragmentsFLV.EpisodiosFlv;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vareta on 13-08-2015.
 */
public class AdBusquedaFLV extends RecyclerView.Adapter<AdBusquedaFLV.BusquedaHolder>{
    public List<HomeScreenAnimeFLV> items;
    public Context context;

    public AdBusquedaFLV (Context context, List<HomeScreenAnimeFLV> items) {
        this.context = context;
        this.items = items;
    }

    public static class BusquedaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nombre;
        public TextView informacion;
        public ImageView preview;
        public CustomRecyclerListener miListener;

        public BusquedaHolder(View itemLayoutView, CustomRecyclerListener listener) {
            super(itemLayoutView);
            miListener = listener;
            nombre = (TextView)itemLayoutView.findViewById(R.id.nombre_flv);
            informacion = (TextView)itemLayoutView.findViewById(R.id.informacion_flv);
            preview = (ImageView)itemLayoutView.findViewById(R.id.preview_flv);
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //System.out.println("layou" + getLayoutPosition());
            miListener.customRecyclerListener(v, getLayoutPosition());
        }

        public static interface CustomRecyclerListener {
            public void customRecyclerListener(View v, int position);
        }
    }



    @Override
    public BusquedaHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        //LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.busqueda_items_flv, viewGroup, false);
        AdBusquedaFLV.BusquedaHolder vh = new BusquedaHolder(v, new AdBusquedaFLV.BusquedaHolder.CustomRecyclerListener() {
            @Override
            public void customRecyclerListener(View v, int position) { //recibe la posicion de mListener

                Bundle bundle = new Bundle();
                bundle.putString("query", items.get(position).getUrlCapitulo());
                EpisodiosFlv capitulo = new EpisodiosFlv();
                capitulo.setArguments(bundle);

                //Inicia el fragmente que contiene los resultados de la busqueda
                FragmentTransaction ft = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.contenedor, capitulo);
                ft.addToBackStack(null); //para que se pueda devolver a un fragment anterior
                ft.commit();
                /* HACER QUE APAREZCA EL OTRO FRAGMENT */
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(BusquedaHolder busquedaHolder, int i) {

        busquedaHolder.nombre.setText(Html.fromHtml(items.get(i).getNombre()));
        busquedaHolder.informacion.setText(Html.fromHtml(items.get(i).getInformacion()));
        Picasso.with(context).load(items.get(i).getPreview()).resize(200, 250).into(busquedaHolder.preview);
        //Log.d("Nombre", items.get(i).getNombre());
        //agrega el preview al imageview via url

        // new Utilities.DownloadImageTask(preview).execute(item.getPreview());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}