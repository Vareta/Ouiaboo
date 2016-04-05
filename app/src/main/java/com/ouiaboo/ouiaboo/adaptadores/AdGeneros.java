package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.clases.GenerosClass;

import com.ouiaboo.ouiaboo.R;
import java.util.List;

/**
 * Created by Vareta on 09-11-2015.
 */
public class AdGeneros extends RecyclerView.Adapter<AdGeneros.GenerosHolder>{
    public List<GenerosClass> items;
    public Context context;
    public CustomRecyclerListener customRecyclerListener;

    public AdGeneros (Context context, List<GenerosClass> items) {
        this.context = context;
        this.items = items;
    }

    public class GenerosHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public TextView genero;

        public GenerosHolder(View itemView) {
            super(itemView);
            genero = (TextView)itemView.findViewById(R.id.generos_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
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
    public GenerosHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.generos_elements, parent, false);
        AdGeneros.GenerosHolder vh = new GenerosHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(GenerosHolder holder, int position) {
        holder.genero.setText(Html.fromHtml(items.get(position).getNombre()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
