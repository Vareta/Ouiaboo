package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Vareta on 27-07-2015.
 */
public class AdDescargadas extends RecyclerView.Adapter<AdDescargadas.AdDescargadasHolder> {
    public List<HomeScreenEpi> items;
    public Context context;
    public CustomRecyclerListener customRecyclerListener;

    // HomeScreenEpi item = (HomeScreenEpi)getItem(position);

    public AdDescargadas(Context context, List<HomeScreenEpi> items) {
        this.context = context;
        this.items = items;
    }

    public class AdDescargadasHolder extends RecyclerView.ViewHolder implements OnClickListener, OnLongClickListener{
        public CardView cardView;
        public TextView nombre;
        public TextView informacion;
        public ImageView preview;


        public AdDescargadasHolder(View itemLayoutView) {
            super(itemLayoutView);
            cardView = (CardView)itemLayoutView.findViewById(R.id.card_view);
            nombre = (TextView)itemLayoutView.findViewById(R.id.nombre_flv);
            informacion = (TextView)itemLayoutView.findViewById(R.id.informacion_flv);
            preview = (ImageView)itemLayoutView.findViewById(R.id.preview_flv);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //System.out.println("layou" + getLayoutPosition());
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
    public AdDescargadasHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        //LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_screen_animeflv, viewGroup, false);
        AdDescargadas.AdDescargadasHolder vh = new AdDescargadasHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(AdDescargadasHolder adHoScAnFLVHolder, int i) {

        adHoScAnFLVHolder.nombre.setText(Html.fromHtml(items.get(i).getNombre()));
        adHoScAnFLVHolder.informacion.setText(Html.fromHtml(items.get(i).getInformacion()));
        Glide.with(context).load(new File(items.get(i).getPreview())).apply(RequestOptions.overrideOf(250, 150)).apply(RequestOptions.centerCropTransform()).into(adHoScAnFLVHolder.preview);//para imagenes guardadas dentro de la sd
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
