package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;

import java.util.List;

/**
 * Created by Vareta on 30-09-2015.
 * obtenido de https://github.com/viniciusthiengo/tc-material-design/blob/master/app/src/main/java/br/com/thiengo/tcmaterialdesign/adapters/ContextMenuAdapter.java
 */
public class AdContMenuCentral extends BaseAdapter{
    private Context context;
    private List<DrawerItemsListUno> listaMenu;
    private LayoutInflater inflater;
    private int extraPadding;

    public AdContMenuCentral (Context context, List<DrawerItemsListUno> listaMenu) {
        this.context = context;
        this.listaMenu = listaMenu;
        inflater = LayoutInflater.from(context);

        float scale = context.getResources().getDisplayMetrics().density;
        extraPadding = (int)(8 * scale + 0.5f);
    }



    @Override
    public int getCount() {
        return listaMenu.size();
    }

    @Override
    public Object getItem(int position) {
        return listaMenu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.context_menu, parent, false);
            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.icono = (ImageView)convertView.findViewById(R.id.imagen);
            holder.nombre = (TextView)convertView.findViewById(R.id.nombre);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.icono.setImageResource(listaMenu.get(position).getIconId());
        holder.nombre.setText(listaMenu.get(position).getNombre());

        //Extra padding



        return convertView;
    }

    public static class ViewHolder {
        ImageView icono;
        TextView nombre;
    }
}
