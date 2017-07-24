package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import java.util.List;

import okhttp3.OkHttpClient;


/**
 * Created by Vareta on 27-07-2015.
 */
public class AdHomeScreen extends RecyclerView.Adapter<AdHomeScreen.AdHoScAnFLVHolder> {
    public List<HomeScreenEpi> items;
    public Context context;
    public CustomRecyclerListener customRecyclerListener;
    private Utilities util;

    // HomeScreenEpi item = (HomeScreenEpi)getItem(position);

    public AdHomeScreen(Context context, List<HomeScreenEpi> items) {
        this.context = context;
        this.items = items;

    }

    public class AdHoScAnFLVHolder extends RecyclerView.ViewHolder implements OnClickListener, OnLongClickListener{
        public CardView cardView;
        public TextView nombre;
        public TextView informacion;
        public ImageView preview;


        public AdHoScAnFLVHolder(View itemLayoutView) {
            super(itemLayoutView);
            cardView = (CardView)itemLayoutView.findViewById(R.id.card_view);
            nombre = (TextView)itemLayoutView.findViewById(R.id.nombre_flv);
            informacion = (TextView)itemLayoutView.findViewById(R.id.informacion_flv);
            preview = (ImageView)itemLayoutView.findViewById(R.id.preview_flv);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
            util = new Utilities();
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
    public AdHoScAnFLVHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        //LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_screen_animeflv, viewGroup, false);
        AdHomeScreen.AdHoScAnFLVHolder vh = new AdHoScAnFLVHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(AdHoScAnFLVHolder adHoScAnFLVHolder, int i) {

        adHoScAnFLVHolder.nombre.setText(Html.fromHtml(items.get(i).getNombre()));
        adHoScAnFLVHolder.informacion.setText(Html.fromHtml(items.get(i).getInformacion()));
        if (util.existenCookies(context)) {
            GlideUrl glideUrl = new GlideUrl(items.get(i).getPreview(), new LazyHeaders.Builder()
                    .addHeader("Cookie", CookieManager.getInstance().getCookie("https://animeflv.net/"))
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .build()
            );
            Glide.with(context).load(glideUrl).apply(RequestOptions.overrideOf(250, 150)).apply(RequestOptions.centerCropTransform()).into(adHoScAnFLVHolder.preview);
        } else {
            Glide.with(context).load(items.get(i).getPreview()).apply(RequestOptions.overrideOf(250, 150)).apply(RequestOptions.centerCropTransform()).into(adHoScAnFLVHolder.preview);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
