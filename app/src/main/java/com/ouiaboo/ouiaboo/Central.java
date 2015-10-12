package com.ouiaboo.ouiaboo;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;
import com.ouiaboo.ouiaboo.clases.SitiosWeb;
import com.ouiaboo.ouiaboo.fragmentsFLV.AvisoLegal;
import com.ouiaboo.ouiaboo.fragmentsFLV.Busqueda;
import com.ouiaboo.ouiaboo.fragmentsFLV.Descargadas;
import com.ouiaboo.ouiaboo.fragmentsFLV.Faq;
import com.ouiaboo.ouiaboo.fragmentsFLV.Favoritos;
import com.ouiaboo.ouiaboo.fragmentsFLV.Generos;
import com.ouiaboo.ouiaboo.fragmentsFLV.Historial;
import com.ouiaboo.ouiaboo.fragmentsFLV.HomeScreen;
import com.ouiaboo.ouiaboo.fragmentsFLV.EpisodiosFlv;
import com.ouiaboo.ouiaboo.fragmentsFLV.PaginasAnime;
import com.ouiaboo.ouiaboo.fragmentsFLV.VerMasTarde;

import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Central extends AppCompatActivity implements HomeScreen.OnFragmentInteractionListener, Busqueda.OnFragmentInteractionListener,
                                                        VerMasTarde.OnFragmentInteractionListener, AvisoLegal.OnFragmentInteractionListener,
                                                        Favoritos.OnFragmentInteractionListener, Descargadas.OnFragmentInteractionListener,
                                                        Historial.OnFragmentInteractionListener, Generos.OnFragmentInteractionListener,
                                                        PaginasAnime.OnFragmentInteractionListener, Faq.OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;
    private String drawerTitle;
    private ListView drawerList;
    private NavigationView navigationView;
    LinearLayout linearLayout;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<DrawerItemsListUno> listDataHeader; //objetos padre lista expandible
    HashMap<DrawerItemsListUno, List<SitiosWeb>> listDataChild; //objetos hijos de la lista expandible
    ArrayList<DrawerItemsListUno> items;
    private Toolbar toolbar;
  //  private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);


        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout); //el drawerLayout
        navigationView = (NavigationView)findViewById(R.id.nav_view);

        setUpToolbar(); //setea la toolbar
        setUpNavDrawer(); //setea el navigation drawer
        SQLiteDatabase db = Connector.getDatabase(); //crea las tablas, si estas no existen


        //listener del navigationview
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                switch (menuItem.getItemId()) {

                    case R.id.nav_inicio: //inicia fragmento inicio Inicio
                        ft.replace(R.id.contenedor, new HomeScreen());
                        break;

                    case R.id.nav_mas_tarde:
                        ft.replace(R.id.contenedor, new VerMasTarde());
                        break;

                    case R.id.nav_favoritos:
                       /* Intent intent = new Intent(getBaseContext(), EpisodiosPlusInfo.class);
                        startActivity(intent);*/
                        ft.replace(R.id.contenedor, new Favoritos());
                        break;

                    case R.id.nav_descargadas:
                        ft.replace(R.id.contenedor, new Descargadas());
                        break;

                    case R.id.nav_historial:
                        ft.replace(R.id.contenedor, new Historial());
                        break;

                    case R.id.nav_generos:
                        ft.replace(R.id.contenedor, new Generos());
                        break;

                    case R.id.nav_paginas:
                        ft.replace(R.id.contenedor, new PaginasAnime());
                        break;

                    case R.id.nav_faq:
                        ft.replace(R.id.contenedor, new Faq());
                        break;

                    case R.id.nav_aviso_legal:
                        ft.replace(R.id.contenedor, new AvisoLegal());
                        break;

                    default:
                        return true;
                }

                ft.addToBackStack(null); //para que se pueda devolver a un fragment anterior
                ft.commit();
                drawerLayout.closeDrawers();
                return true;
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contenedor, new HomeScreen());
        ft.commit();

    }

    private void setUpToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private void setUpNavDrawer() {
        if (toolbar != null) {
            assert getSupportActionBar() != null; //agregado ya que getSupportAction puede producir Method invocation may produce java.lang.NullPointerException
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_menu_white);
            setTitle(R.string.inicio_drawer_layout);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }



    /*
        AHora esto se realiza mediante la libreria (borrar proximamente)

    private void poblarMenuLateral(){

        String[] drawerTitulos = getResources().getStringArray(R.array.drawer_list_uno); //arreglo de strings de la lista

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
        items = new ArrayList<DrawerItemsListUno>();
        items.add(new DrawerItemsListUno(drawerTitulos[0], R.drawable.ic_action_home)); //home
        items.add(new DrawerItemsListUno(drawerTitulos[1], R.drawable.ic_action_heart)); //ver mas tarde
        items.add(new DrawerItemsListUno(drawerTitulos[2], R.drawable.ic_action_heart)); //favoritos
        items.add(new DrawerItemsListUno(drawerTitulos[3], R.drawable.ic_action_help)); //descargadas
        items.add(new DrawerItemsListUno(drawerTitulos[4], R.drawable.ic_action_info)); //historial
        items.add(new DrawerItemsListUno(drawerTitulos[5], R.drawable.ic_action_help)); //FAQ
        items.add(new DrawerItemsListUno(drawerTitulos[6], R.drawable.ic_action_info)); //aviso legal
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_central, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView)menu.findItem(R.id.buscar).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
       // searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true); //submit button
        searchView.setQueryRefinementEnabled(true); //query refinement for search sugestion

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // Do something
                //Log.d("TextChange", newText);
                //Log.d("TextChange", "cambie");
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something
                //Log.d("TextChange", query);
                //Log.d("TextSubmit", "busqué");
                if (query.length() < 4) {
                    //Log.d("TextSubmit", "menos o igual de 3");
                    Toast.makeText(Central.this, getString(R.string.ins_caracteres_menu_central_ES), Toast.LENGTH_SHORT).show();
                } else {
                    //Envia la query al fragment Busqueda
                    Bundle bundle = new Bundle();
                    bundle.putString("query", query);
                    Busqueda search = new Busqueda();
                    search.setArguments(bundle);

                    //Inicia el fragmente que contiene los resultados de la busqueda
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.contenedor, search);
                    ft.addToBackStack(null); //para que se pueda devolver a un fragment anterior
                    ft.commit();
                    searchView.onActionViewCollapsed(); //cierra el teclado
                }

                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.drawable.ic_menu_white) {
            Log.d("Toolbar", "hola");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    @Override
    public void onBusquedaInteraction(String url) {
        Intent intent = new Intent(getBaseContext(), EpisodiosPlusInfo.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }


    @Override
    public void onVerMasTardeInteraction(String url) {
        Intent intent = new Intent(this, VideoPlayer.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public void onFavoritoInteraction(String url) {
        Intent intent = new Intent(getBaseContext(), EpisodiosPlusInfo.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }


    //para probar jsoup
    private class BackgroundTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            try {
                Animeflv ani = new Animeflv(getResources());
                ani.getEpisodios("http://animeflv.net/anime/nanatsu-no-taizai.html");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

}
