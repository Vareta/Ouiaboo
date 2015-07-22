package com.ouiaboo.ouiaboo;

import android.app.ListActivity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends ListActivity {

    ListView paginasAnime;
    ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] paginasWebNombre = getResources().getStringArray(R.array.paginas_anime);
        String[] paginasWebIdioma = getResources().getStringArray(R.array.idioma_paginas_anime);

        ArrayList<SitiosWeb> webList = new ArrayList<SitiosWeb>();

        for (int i = 0; i < paginasWebNombre.length; i++){
            webList.add(new SitiosWeb(paginasWebNombre[i], paginasWebIdioma[i]));
        }

        setListAdapter(new SitiosWebAdapter(this, R.layout.paginas_anime_lista, webList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //clase de las paginas web para ver anime
    public class SitiosWeb{

        private String nombre;
        private String idioma;

        public SitiosWeb(String nombre, String idioma){
            this.nombre = nombre;
            this.idioma = idioma;
        }

        public void setNombre(String nombre){
            this.nombre = nombre;
        }

        public void setIdioma(String idioma){
            this.idioma = idioma;
        }

        public String getNombre(){
            return nombre;
        }

        public String getIdioma(){
            return idioma;
        }
    }

    public class SitiosWebAdapter extends ArrayAdapter<SitiosWeb>{
        private ArrayList<SitiosWeb> items;
        private SitiosWebViewHolder sitiosWebViewHolder;

        private class SitiosWebViewHolder {
            TextView nombre;
            TextView idioma;
        }

        public SitiosWebAdapter(Context context, int layout, ArrayList<SitiosWeb> items){
            super(context, layout, items);
            this.items = items;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null){
                LayoutInflater vi = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.paginas_anime_lista , null);
                sitiosWebViewHolder = new SitiosWebViewHolder();
                sitiosWebViewHolder.nombre = (TextView)v.findViewById(R.id.pagina);
                sitiosWebViewHolder.idioma = (TextView)v.findViewById(R.id.idioma);
                v.setTag(sitiosWebViewHolder);
            } else {
                sitiosWebViewHolder = (SitiosWebViewHolder)v.getTag();
            }

            SitiosWeb sitiosWeb = items.get(pos);

            if (sitiosWeb != null){
                sitiosWebViewHolder.nombre.setText(sitiosWeb.getNombre());
                sitiosWebViewHolder.idioma.setText(sitiosWeb.getIdioma());
            }

            return v;
        }
    }
}
