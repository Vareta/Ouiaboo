package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;


import java.util.List;

/**
 * Created by Vareta on 13-08-2015.
 */
public class AdBusquedaFLV extends RecyclerView.Adapter<AdBusquedaFLV.BusquedaHolder>{
    public List<HomeScreenEpi> items;
    public Context context;
    public CustomRecyclerListener customRecyclerListener;
    public Utilities util;

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
            util = new Utilities();
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
        if (util.existenCookies(context)) {
            GlideUrl glideUrl = new GlideUrl(items.get(i).getPreview(), new LazyHeaders.Builder()
                    .addHeader("Cookie", CookieManager.getInstance().getCookie("https://animeflv.net/"))
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .build()
            );
            Glide.with(context).load(glideUrl).apply(RequestOptions.overrideOf(200, 250)).apply(RequestOptions.centerCropTransform()).into(busquedaHolder.preview);
        } else {
            Glide.with(context).load(items.get(i).getPreview()).apply(RequestOptions.overrideOf(200, 250)).apply(RequestOptions.centerCropTransform()).into(busquedaHolder.preview);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
