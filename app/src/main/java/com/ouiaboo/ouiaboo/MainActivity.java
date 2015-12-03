package com.ouiaboo.ouiaboo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ouiaboo.ouiaboo.adaptadores.AdSitiosWeb;
import com.ouiaboo.ouiaboo.clases.SitiosWeb;

import java.util.ArrayList;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener{

    private static final String TAG = "";
    public static final String PREFERENCIAS = "preferencias";
    private ListView paginasAnime;
    private ArrayAdapter adaptador;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE); //se cargan las preferencias
        if (!sharedPreferences.contains("animeflv")){//si no tiene el string, quiere decir que es primera vez que se crean las preferencias
            Log.d("PREF", "primera");
            SharedPreferences.Editor editor = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE).edit();
            editor.putBoolean("animeflv", true);
            //editor.putBoolean("animejoy", false);
            editor.apply();
            PreferenceManager.setDefaultValues(this, PREFERENCIAS, MODE_PRIVATE, R.xml.preferences, false);
        } else {
            Intent intent = new Intent(getBaseContext(), Central.class);
            startActivity(intent); //pasa a la nueva actividad
            finish(); //cierra la actividad actual, para no poder volver con el boton back
        }

        setContentView(R.layout.activity_main);
        // Obtain the shared Tracker instance.

        String[] paginasWebNombre = getResources().getStringArray(R.array.paginas_anime);
        String[] paginasWebIdioma = getResources().getStringArray(R.array.idioma_paginas_anime);

        ArrayList webList = new ArrayList<SitiosWeb>();

        for (int i = 0; i < paginasWebNombre.length; i++){
            webList.add(new SitiosWeb(paginasWebNombre[i], paginasWebIdioma[i]));
        }

        //Instancia del ListView (es por ello que va el id del listview)
        paginasAnime = (ListView)findViewById(R.id.listView);

        //Inicializar el adaptador con la fuente de datos
        adaptador = new AdSitiosWeb(this, webList);

        //Relacionando la lista con el adaptador
        paginasAnime.setAdapter(adaptador);

        paginasAnime.setOnItemClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        AnalyticsApplication.getInstance().trackScreenView("Main Activity");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        /*
        position 0 = AnimeFLV
        position 1 = KissAnime
        */
        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE).edit();

            if (position == 0){ //AnimeFLV
                editor.putBoolean("animeflv", true);
                //editor.putBoolean("animejoy", false);
            }/*else{
                if (position == 1){ //KissAnime
                    editor.putBoolean("animeflv", false);
                    editor.putBoolean("animejoy", true);
                }
            }*/
        editor.apply();

        SharedPreferences prefs = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE);

       // Log.d(TAG,  "AnimeFLV  "+prefs.getBoolean("animeflv", false) + "  Animejoy" + prefs.getBoolean("animejoy", false));

        Intent intent = new Intent(getBaseContext(), Central.class);
        startActivity(intent);
        finish();
    }
}
