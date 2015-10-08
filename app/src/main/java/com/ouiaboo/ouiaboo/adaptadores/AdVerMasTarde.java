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

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.clases.HomeScreenAnimeFLV;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Vareta on 27-07-2015.
 */
public class AdVerMasTarde extends RecyclerView.Adapter<AdVerMasTarde.AdVerMasTardeHolder> {
    public List<HomeScreenAnimeFLV> items;
    public Context context;
    public CustomRecyclerListener customRecyclerListener;

    // HomeScreenAnimeFLV item = (HomeScreenAnimeFLV)getItem(position);

    public AdVerMasTarde(Context context, List<HomeScreenAnimeFLV> items) {
        this.context = context;
        this.items = items;
    }

    public class AdVerMasTardeHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public CardView cardView;
        public TextView nombre;
        public TextView informacion;
        public ImageView preview;


        public AdVerMasTardeHolder(View itemLayoutView) {
            super(itemLayoutView);
            cardView = (CardView)itemLayoutView.findViewById(R.id.card_view);
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
    public AdVerMasTardeHolder onCreateViewHolder(ViewGroup viewGroup, final int viewType) {
        //LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_screen_animeflv, viewGroup, false);
        AdVerMasTarde.AdVerMasTardeHolder vh = new AdVerMasTardeHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(AdVerMasTardeHolder holder, int i) {

        holder.nombre.setText(Html.fromHtml(items.get(i).getNombre()));
        holder.informacion.setText(Html.fromHtml(items.get(i).getInformacion()));
        Picasso.with(context).load(items.get(i).getPreview()).resize(250, 150).into(holder.preview);
        //Log.d("Nombre", items.get(i).getNombre());
        //agrega el preview al imageview via url

        // new Utilities.DownloadImageTask(preview).execute(item.getPreview());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
