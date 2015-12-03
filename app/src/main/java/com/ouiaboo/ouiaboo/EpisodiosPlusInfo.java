package com.ouiaboo.ouiaboo;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ouiaboo.ouiaboo.clases.Episodios;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;
import com.ouiaboo.ouiaboo.fragmentsFLV.AnimeInfo;
import com.ouiaboo.ouiaboo.fragmentsFLV.EpisodiosFlv;
import com.squareup.picasso.Picasso;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class EpisodiosPlusInfo extends AppCompatActivity implements AnimeInfo.OnFragmentInteractionListener, EpisodiosFlv.OnFragmentInteractionListener {
    private ProgressBar bar;
    private AppBarLayout appBar;
    private ArrayList<Episodios> epi;
    private ArrayList<Episodios> epiInfo;
    private ImageView header;
    private FloatingActionButton favorito;
    private Snackbar snackbar;
    private CoordinatorLayout coordLayout;
    private CollapsingToolbarLayout collaToolbar;
    private boolean esFavorito;
    private Context context;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodios_plus_info);

        header = (ImageView) findViewById(R.id.header);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        favorito = (FloatingActionButton) findViewById(R.id.favorito);
        coordLayout = (CoordinatorLayout) findViewById(R.id.coord_layout);
        context = this;


        new GetCapitulosData().execute(getIntent().getStringExtra("url"));
        //new GetCapitulosData().execute("http://animeflv.net/anime/one-piece.html");

        favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Funciones fun = new Funciones();
                if (esFavorito) { //se encuentra en favoritos --> se elimina
                    esFavorito = false;
                    fun.eliminarFavorito(epiInfo.get(0)); //elimina de favoritos
                    favorito.setImageResource(R.drawable.ic_favorite_border_white_24dp); //cambia el icono por el correspondiente (no favorito)
                    snackbar = Snackbar.make(coordLayout, getString(R.string.noti_favoritos_no), Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                } else { //no se encuentra en favoritos --> se añade
                    esFavorito = true;
                    AnalyticsApplication.getInstance().trackEvent("Favorito", "añadir", epiInfo.get(0).getNombreAnime());
                    fun.añadirFavorito(epiInfo.get(0));
                    favorito.setImageResource(R.drawable.ic_favorite_white_24dp);
                    snackbar = Snackbar.make(coordLayout, getString(R.string.noti_favoritos_si), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

    }


    private void setupCollapsingToolbar() {
        CollapsingToolbarLayout cToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        cToolbar.setTitleEnabled(false); //para que el titulo no tenga animacion
    }

    private void setupViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.tab_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupFAB(Episodios epiInfo) {
        Funciones fun = new Funciones();
        if (fun.estaEnFavoritos(epiInfo)) { //si esta en favoritos
            favorito.setImageResource(R.drawable.ic_favorite_white_24dp);
            esFavorito = true;
        } else { //caso contrario
            favorito.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            esFavorito = false;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(sendDataCapitulos(), getString(R.string.episodios_EpisodiosPlusInfo));
        adapter.addFrag(sendDataInformacion(), getString(R.string.informacion_EpisodiosPlusInfo));

        viewPager.setAdapter(adapter);
    }

    private Fragment sendDataCapitulos() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("episodios", epi);

        EpisodiosFlv epiFragment = new EpisodiosFlv();
        epiFragment.setArguments(bundle);

        return epiFragment;
    }

    private Fragment sendDataInformacion() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("informacion", epiInfo);

        AnimeInfo info = new AnimeInfo();
        info.setArguments(bundle);

        return info;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onEpisodiosFlvInteraction(HomeScreenEpi objEpi) {
        Utilities util = new Utilities();
        if (util.esReproductorExterno(this)) {
            AnalyticsApplication.getInstance().trackEvent("Reproductor Externo", "ver", objEpi.getNombre());
            new ReproductorExterno().execute(objEpi);
        } else {
            AnalyticsApplication.getInstance().trackEvent("Reproductor Interno", "ver", objEpi.getNombre());
            new ReproductorInterno().execute(objEpi);
        }
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    private class GetCapitulosData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];

            try {
                Animeflv ani = new Animeflv();
                epi = ani.getEpisodios(url); //contiene los episodios y la info
                epiInfo = new ArrayList<>(); //inicializa
                epiInfo.add(new Episodios(epi.get(0).getNombreAnime(), epi.get(0).getUrlAnime(), epi.get(0).getUrlEpisodio(), epi.get(0).getNumero(), epi.get(0).getUrlImagen(),
                        epi.get(0).getInformacion(), epi.get(0).getTipo(), epi.get(0).getEstado(), epi.get(0).getGeneros(),
                        epi.get(0).getFechaInicio())); //agrega el primero elemento que contiene la informacion del espisodio

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            appBar.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            setupCollapsingToolbar();
            setupViewPager();
            setupToolbar();
            setupFAB(epiInfo.get(0));
            setTitle(epiInfo.get(0).getNombreAnime());
            Picasso.with(getBaseContext()).load(epi.get(0).getUrlImagen()).into(header);
            bar.setVisibility(View.GONE);
            appBar.setVisibility(View.VISIBLE);
        }
    }

    private class ReproductorExterno extends AsyncTask<HomeScreenEpi, Void, Void> {
        String url;

        @Override
        protected Void doInBackground(HomeScreenEpi... params) {
            Utilities util = new Utilities();
            Animeflv anime = new Animeflv();
            HomeScreenEpi objEpi = params[0];
            try {
                url = anime.urlDisponible(objEpi.getUrlCapitulo(), getBaseContext());
                Document codigoFuente = util.connect(objEpi.getUrlCapitulo());
                String preview = anime.getMiniImage(codigoFuente); //preview estilo homeScreen
                anime.añadirHistorialFlv(objEpi.getNombre(), objEpi.getUrlCapitulo()); //añade al historial (en la vista de capitulos)
                anime.añadirHistorial(objEpi.getNombre(), objEpi.getInformacion(), preview, objEpi.getUrlCapitulo()); //añade al historial (el historial interno, vease fragment Historial)

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
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setDataAndType(Uri.parse(url), "video/mp4");
            startActivity(intent);
        }
    }

    /*De esta manera pasa el preview correspondiente al episodio y no al anime en si*/
    private class ReproductorInterno extends AsyncTask<HomeScreenEpi, Void, Void> {
        String url;
        HomeScreenEpi objEpi;

        @Override
        protected Void doInBackground(HomeScreenEpi... params) {
            Utilities util = new Utilities();
            Animeflv anime = new Animeflv();
            objEpi = params[0];
            try {
                Document codigoFuente = util.connect(objEpi.getUrlCapitulo());
                String preview = anime.getMiniImage(codigoFuente); //preview estilo homeScreen
                objEpi.setPreview(preview);

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
            Intent intent = new Intent(context, VideoPlayer.class);
            intent.putExtra("episodio", objEpi);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /*
        Esta función tiene por objetivo solucionar el acto de presentar un titulo (toolbar) incorrecto cuando se ejecuta la siguiente accion:
        buscar serie, ej one piece --> titulo: one piece
        aparecen todas las series asociasdas y seleccionar one piece pelicula --> titulo: one piece pelicula
        buscar otra serie, ej supercampeones --> titulo: supercampeones
        volver atras mediante cualquier opcion --> titulo: one piece.
        lo cual es incorrecto, ya que el titulo debiese ser one piece pelicula
        SOLO FUNCIONA ENTRE FRAGMENTS
     */
   /* private void asignaTitulo() {
        if (titulo == null) {
            titulo = (String)getTitle(); //obtiene el titulo que se le da cuando se le hace click a la serie que es buscada
        }
        // Log.d("TITULO", titulo);
        setTitle(titulo); //setea el titulo de acuerdo a la serie que se seleccionó.
    }*/

}
