package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.clases.Episodios;
import java.util.List;

/**
 * Created by Vareta on 14-08-2015.
 */
public class AdEpisodios extends RecyclerView.Adapter<AdEpisodios.EpisodiosHolder>{
    public List<Episodios> items;
    public Context context;
    public CustomRecyclerListener customRecyclerListener;
    private Animeflv animeflv;

    public AdEpisodios (Context context, List<Episodios> items) {
        animeflv = new Animeflv();
        this.context = context;
        this.items = items;
    }

    public class EpisodiosHolder extends RecyclerView.ViewHolder implements OnClickListener, OnLongClickListener {
        public TextView capitulo;

        public EpisodiosHolder(View itemLayoutView) {
            super(itemLayoutView);
            capitulo = (TextView)itemLayoutView.findViewById(R.id.episodios_flv);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (customRecyclerListener != null) {
                customRecyclerListener.customClickListener(v, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (customRecyclerListener != null) {
                customRecyclerListener.customLongClickListener(v, getLayoutPosition());
            }
            return true;
        }
    }

    public static interface CustomRecyclerListener {
        public void customClickListener(View v, int position);
        public void customLongClickListener(View v, int position);
    }

    public void setClickListener(CustomRecyclerListener customRecyclerListener){
        this.customRecyclerListener = customRecyclerListener;
    }

    @Override
    public EpisodiosHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        //LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.episodios_animeflv, viewGroup, false);
        AdEpisodios.EpisodiosHolder vh = new EpisodiosHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(EpisodiosHolder episodiosHolder, int position) {
        episodiosHolder.capitulo.setText(Html.fromHtml(items.get(position).getNumero()));
        if (animeflv.seEncuentraEnHistorialFlv(items.get(0).getNombreAnime(), items.get(position).getUrlEpisodio())) {
            episodiosHolder.capitulo.setTextColor(ContextCompat.getColor(context, R.color.ColorPrimary));
        } else {
            episodiosHolder.capitulo.setTextColor(ContextCompat.getColor(context, R.color.primary_text_material_dark));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
