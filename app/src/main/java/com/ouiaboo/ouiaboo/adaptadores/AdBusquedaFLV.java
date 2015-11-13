package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Vareta on 13-08-2015.
 */
public class AdBusquedaFLV extends RecyclerView.Adapter<AdBusquedaFLV.BusquedaHolder>{
    public List<HomeScreenEpi> items;
    public Context context;
    public CustomRecyclerListener customRecyclerListener;

    public AdBusquedaFLV (Context context, List<HomeScreenEpi> items) {
        this.context = context;
        this.items = items;
    }

    public class BusquedaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nombre;
        public TextView informacion;
        public ImageView preview;

        public BusquedaHolder(View itemLayoutView) {
            super(itemLayoutView);
            nombre = (TextView)itemLayoutView.findViewById(R.id.nombre_flv);
            informacion = (TextView)itemLayoutView.findViewById(R.id.informacion_flv);
            preview = (ImageView)itemLayoutView.findViewById(R.id.preview_flv);
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //System.out.println("layou" + getLayoutPosition());
            if (customRecyclerListener != null) {
                customRecyclerListener.customClickListener(v, getLayoutPosition());
            }
        }

    }

    public static interface CustomRecyclerListener {
        public void customClickListener(View v, int position);
    }

    public void setClickListener(CustomRecyclerListener customRecyclerListener){
        this.customRecyclerListener = customRecyclerListener;
    }



    @Override
    public BusquedaHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        //LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.busqueda_items_flv, viewGroup, false);
        AdBusquedaFLV.BusquedaHolder vh = new BusquedaHolder(v);

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
