package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.VideoPlayer;
import com.ouiaboo.ouiaboo.clases.Episodios;
import com.ouiaboo.ouiaboo.clases.HomeScreenAnimeFLV;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vareta on 14-08-2015.
 */
public class AdEpisodios extends RecyclerView.Adapter<AdEpisodios.EpisodiosHolder>{
    public List<Episodios> items;
    public Context context;

    public AdEpisodios (Context context, List<Episodios> items) {
        this.context = context;
        this.items = items;
    }

    public static class EpisodiosHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView capitulo;

        public CustomRecyclerListener mListener;

        public EpisodiosHolder(View itemLayoutView, CustomRecyclerListener listener) {
            super(itemLayoutView);
            mListener = listener;
            capitulo = (TextView)itemLayoutView.findViewById(R.id.espisodios_flv);
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //System.out.println("layou" + getLayoutPosition());
            mListener.customRecyclerListener(v, getLayoutPosition());
        }

        public static interface CustomRecyclerListener {
            public void customRecyclerListener(View v, int position);
        }
    }



    @Override
    public EpisodiosHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        //LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.episodios_animeflv, viewGroup, false);
        AdEpisodios.EpisodiosHolder vh = new EpisodiosHolder(v, new AdEpisodios.EpisodiosHolder.CustomRecyclerListener() {
            @Override
            public void customRecyclerListener(View v, int position) { //recibe la posicion de mListener

                Intent intent = new Intent(context, VideoPlayer.class);
                intent.putExtra("url", items.get(position).getUrl());
                context.startActivity(intent);
                /* HACER QUE APAREZCA EL OTRO FRAGMENT */
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(EpisodiosHolder episodiosHolder, int i) {

        episodiosHolder.capitulo.setText(Html.fromHtml(items.get(i).getNumero()));

        // new Utilities.DownloadImageTask(preview).execute(item.getPreview());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
