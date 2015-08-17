package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.VideoPlayer;
import com.ouiaboo.ouiaboo.clases.HomeScreenAnimeFLV;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vareta on 27-07-2015.
 */
public class AdaptadorHomeScreenAnimeFLV extends RecyclerView.Adapter<AdaptadorHomeScreenAnimeFLV.AdHoScAnFLVHolder> {
    public List<HomeScreenAnimeFLV> items;
    public Context context;
    public CustomRecyclerListener customRecyclerListener;

    // HomeScreenAnimeFLV item = (HomeScreenAnimeFLV)getItem(position);

    public AdaptadorHomeScreenAnimeFLV (Context context, List<HomeScreenAnimeFLV> items) {
        this.context = context;
        this.items = items;
    }

    public class AdHoScAnFLVHolder extends RecyclerView.ViewHolder implements OnClickListener{
        public TextView nombre;
        public TextView informacion;
        public ImageView preview;


        public AdHoScAnFLVHolder(View itemLayoutView) {
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
    public AdHoScAnFLVHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        //LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_screen_animeflv, viewGroup, false);
        AdaptadorHomeScreenAnimeFLV.AdHoScAnFLVHolder vh = new AdHoScAnFLVHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(AdHoScAnFLVHolder adHoScAnFLVHolder, int i) {

        adHoScAnFLVHolder.nombre.setText(Html.fromHtml(items.get(i).getNombre()));
        adHoScAnFLVHolder.informacion.setText(Html.fromHtml(items.get(i).getInformacion()));
        Picasso.with(context).load(items.get(i).getPreview()).resize(250, 150).into(adHoScAnFLVHolder.preview);
        //Log.d("Nombre", items.get(i).getNombre());
        //agrega el preview al imageview via url

        // new Utilities.DownloadImageTask(preview).execute(item.getPreview());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
