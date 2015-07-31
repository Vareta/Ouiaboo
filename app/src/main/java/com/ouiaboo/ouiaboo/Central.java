package com.ouiaboo.ouiaboo;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.database.DataSetObserver;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ouiaboo.ouiaboo.adaptadores.AdapatadorDrawerExpList;
import com.ouiaboo.ouiaboo.adaptadores.AdaptadorDrawerListUno;
import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;
import com.ouiaboo.ouiaboo.clases.SitiosWeb;
import com.ouiaboo.ouiaboo.fragments.HomeScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Central extends Activity implements HomeScreen.OnFragmentInteractionListener{
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    LinearLayout linearLayout;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<DrawerItemsListUno> listDataHeader; //objetos padre lista expandible
    HashMap<DrawerItemsListUno, List<SitiosWeb>> listDataChild; //objetos hijos de la lista expandible

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);

        HomeScreen homeScreen = (HomeScreen)getFragmentManager().findFragmentById(R.id.fragment_home_animeflv);

        String[] drawerTitulos = getResources().getStringArray(R.array.drawer_list_uno); //arreglo de strings de la lista
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout); //el drawerLayout
        linearLayout = (LinearLayout)findViewById(R.id.left_drawer);
        drawerList = (ListView)findViewById(R.id.drawer_list); //el listView
        expListView = (ExpandableListView)findViewById(R.id.drawer_expandable_list); //lista expandible

        //poblando la lista expandible
        String[] paginasWebNombre = getResources().getStringArray(R.array.paginas_anime);
        String[] paginasWebIdioma = getResources().getStringArray(R.array.idioma_paginas_anime);

        ArrayList webList = new ArrayList<SitiosWeb>();
        for (int i = 0; i < paginasWebNombre.length; i++){
            webList.add(new SitiosWeb(paginasWebNombre[i], paginasWebIdioma[i]));
        }

        listDataHeader = new ArrayList<DrawerItemsListUno>();
        listDataChild = new HashMap<DrawerItemsListUno, List<SitiosWeb>>();

        String[] titulosExpList = getResources().getStringArray(R.array.drawer_list_cero);
        listDataHeader.add(new DrawerItemsListUno(titulosExpList[0], R.drawable.ic_action_globe));
        listDataChild.put(listDataHeader.get(0), webList);

        //poblando la lista 1 del drawer layout (menu de al lado)
        ArrayList<DrawerItemsListUno> items = new ArrayList<DrawerItemsListUno>();
        items.add(new DrawerItemsListUno(drawerTitulos[0], R.drawable.ic_action_home));
        items.add(new DrawerItemsListUno(drawerTitulos[1], R.drawable.ic_action_heart));
        items.add(new DrawerItemsListUno(drawerTitulos[2], R.drawable.ic_action_help));
        items.add(new DrawerItemsListUno(drawerTitulos[3], R.drawable.ic_action_info));

        //relaciona el adaptador y el listener para la lista del drawer
        drawerList.setAdapter(new AdaptadorDrawerListUno(this, items));
        //se crea una instancia del adaptador, para luego setearlo
        listAdapter = new AdapatadorDrawerExpList(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        drawerLayout.closeDrawer(linearLayout);

        Utilities.DownloadWebPageTask task = new Utilities.DownloadWebPageTask();
        task.execute(new String[]{"http://animeflv.net/"});
        List<String> codigoFuente = null;
        try {
            codigoFuente = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (codigoFuente != null) {
            //Log.d("NULL", "no es nulo");
            Animeflv util = new Animeflv();
            util.homeScreenAnimeflv(codigoFuente);
        } else {
           // Log.d("NULL", "nulo");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_central, menu);
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

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}
