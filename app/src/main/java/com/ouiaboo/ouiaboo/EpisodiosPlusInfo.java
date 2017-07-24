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

import com.ouiaboo.ouiaboo.clases.Episodios;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;
import com.ouiaboo.ouiaboo.fragmentsFLV.AnimeInfo;
import com.ouiaboo.ouiaboo.fragmentsFLV.EpisodiosFlv;
import com.squareup.picasso.Picasso;

import org.jsoup.nodes.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EpisodiosPlusInfo extends AppCompatActivity implements AnimeInfo.OnFragmentInteractionListener, EpisodiosFlv.OnFragmentInteractionListener {
    private ProgressBar bar;
    private AppBarLayout appBar;
    private List<Episodios> epi;
    private List<Episodios> epiInfo;
    private ImageView header;
    private FloatingActionButton favorito;
    private Snackbar snackbar;
    private CoordinatorLayout coordLayout;
    private CollapsingToolbarLayout collaToolbar;
    private boolean esFavorito;
    private Context context;
    ViewPager viewPager;
    private final String EPISODIOS = "episodios";
    private final String EPISODISOPLUSINFO = "episodiosplusinfo";


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

        if (savedInstanceState != null) {
            getSavedData(savedInstanceState);
        } else {
            new GetCapitulosData().execute(getIntent().getStringExtra("url"));
            //new GetCapitulosData().execute("http://animeflv.net/anime/one-piece.html");
        }
        favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Funciones fun = new Funciones();
                Utilities util = new Utilities();
                if (esFavorito) { //se encuentra en favoritos --> se elimina
                    esFavorito = false;
                    if (util.queProveedorEs(context) == Utilities.ANIMEFLV) {
                        fun.eliminarFavorito(epiInfo.get(0), Utilities.ANIMEFLV); //elimina de favoritos
                    } else { //reyanime
                        fun.eliminarFavorito(epiInfo.get(0), Utilities.REYANIME);
                    }
                    favorito.setImageResource(R.drawable.ic_favorite_border_white_24dp); //cambia el icono por el correspondiente (no favorito)
                    snackbar = Snackbar.make(coordLayout, getString(R.string.noti_favoritos_no), Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                } else { //no se encuentra en favoritos --> se añade
                    esFavorito = true;
                    AnalyticsApplication.getInstance().trackEvent("Favorito", "añadir", epiInfo.get(0).getNombreAnime());
                    if (util.queProveedorEs(context) == Utilities.ANIMEFLV) {
                        fun.añadirFavorito(epiInfo.get(0), Utilities.ANIMEFLV);
                    } else { //reyanime
                        fun.añadirFavorito(epiInfo.get(0), Utilities.REYANIME);
                    }
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
        viewPager = (ViewPager) findViewById(R.id.tab_viewpager);
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
        Utilities util = new Utilities();
        //Verifica si se encuentra en favoritos y asigna el valor a esfavorito (primera vez dentro de la funcion)
        if (util.queProveedorEs(context) == Utilities.ANIMEFLV) {
            esFavorito = fun.estaEnFavoritos(epiInfo, Utilities.ANIMEFLV);
        } else {
            esFavorito = fun.estaEnFavoritos(epiInfo, Utilities.REYANIME);
        }
        //setea el FAB de favoritos segun sea el caso
        if (esFavorito) { //si esta en favoritos
            favorito.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else { //caso contrario
            favorito.setImageResource(R.drawable.ic_favorite_border_white_24dp);
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
        bundle.putSerializable("episodios", (Serializable) epi);

        EpisodiosFlv epiFragment = new EpisodiosFlv();
        epiFragment.setArguments(bundle);

        return epiFragment;
    }

    private Fragment sendDataInformacion() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("informacion", (Serializable) epiInfo);

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
            Intent intent = new Intent(context, VideoPlayer.class);
            intent.putExtra("episodio", objEpi);
            startActivity(intent);
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
            Utilities util = new Utilities();
            try {
                if (util.queProveedorEs(context) == Utilities.ANIMEFLV) { //animeflv
                    Animeflv animeflv = new Animeflv();
                    epi = animeflv.getEpisodios(url, context); //contiene los episodios y la info
                    epiInfo = new ArrayList<>(); //inicializa
                    epiInfo.add(new Episodios(epi.get(0).getNombreAnime(), epi.get(0).getUrlAnime(), epi.get(0).getUrlEpisodio(), epi.get(0).getNumero(), epi.get(0).getUrlImagen(),
                            epi.get(0).getInformacion(), epi.get(0).getTipo(), epi.get(0).getEstado(), epi.get(0).getGeneros(),
                            epi.get(0).getFechaInicio())); //agrega el primer elemento, ya que este contiene la informacion del espisodio
                } else {
                    Reyanime reyanime = new Reyanime();
                    Document codigoFuente = util.connect(url);
                    epi = reyanime.getEpisodios(codigoFuente, url);
                    epiInfo = new ArrayList<>(); //inicializa
                    epiInfo.add(new Episodios(epi.get(0).getNombreAnime(), epi.get(0).getUrlAnime(), epi.get(0).getUrlEpisodio(), epi.get(0).getNumero(), epi.get(0).getUrlImagen(),
                            epi.get(0).getInformacion(), epi.get(0).getTipo(), epi.get(0).getEstado(), epi.get(0).getGeneros(),
                            epi.get(0).getFechaInicio())); //agrega el primer elemento, ya que este contiene la informacion del espisodio
                }
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
            HomeScreenEpi objEpi = params[0];
            Document codigoFuente;
            try {
                if (util.queProveedorEs(getBaseContext()) == Utilities.ANIMEFLV) {
                    Animeflv animeflv = new Animeflv();
                    url = animeflv.urlDisponible(objEpi.getUrlCapitulo(), getBaseContext());
                   // codigoFuente = util.connect(objEpi.getUrlCapitulo()); Animeflv cambion, por lo que ya no guarda este tipo de imagen en series antiguas
                   // String preview = animeflv.getMiniImage(codigoFuente); //preview estilo homeScreen
                    animeflv.añadirHistorialFlv(objEpi.getNombre(), objEpi.getUrlCapitulo()); //añade al historial (en la vista de capitulos)
                    animeflv.añadirHistorial(objEpi.getNombre(), objEpi.getInformacion(), objEpi.getPreview(), objEpi.getUrlCapitulo()); //añade al historial (el historial interno, vease fragment Historial)
                } else {
                    Reyanime reyanime = new Reyanime();
                    url = reyanime.urlDisponible(objEpi.getUrlCapitulo(), getBaseContext());
                    //PARA REYANIME SE PASA LA MISMA IMAGEN DEL ANIME COMO IMAGEN DE EPISODIO
                    reyanime.añadirHistorialRey(objEpi.getNombre(), objEpi.getUrlCapitulo());
                    reyanime.añadirHistorial(objEpi.getNombre(), objEpi.getInformacion(), objEpi.getPreview(), objEpi.getUrlCapitulo());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void result) {
            bar.setVisibility(View.GONE);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setDataAndType(Uri.parse(url), "video/mp4");
            startActivity(intent);
        }
    }

    /*De esta manera pasa el preview correspondiente al episodio y no al anime en si*/
    private class ReproductorInterno extends AsyncTask<HomeScreenEpi, Void, Void> {
        HomeScreenEpi objEpi;

        @Override
        protected Void doInBackground(HomeScreenEpi... params) {
            Utilities util = new Utilities();
            objEpi = params[0];
            try {
                if (util.queProveedorEs(context) == Utilities.ANIMEFLV) {
                    Animeflv animeflv = new Animeflv();
                    Document codigoFuente = util.connect(objEpi.getUrlCapitulo());
                    String preview = animeflv.getMiniImage(codigoFuente); //preview estilo homeScreen
                    objEpi.setPreview(preview);
                }
                /* PARA REYANIME SE PASA LA MISMA IMAGEN DEL ANIME COMO IMAGEN DE EPISODIO
                else { //reyanime
                }
                */
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
        if (viewPager != null) {
            viewPager.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(EPISODIOS, (Serializable) epi);
        savedInstanceState.putSerializable(EPISODISOPLUSINFO, (Serializable) epiInfo);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void getSavedData(Bundle savedInstanceState) {
        epi = (List<Episodios>) savedInstanceState.getSerializable(EPISODIOS);
        epiInfo = (List<Episodios>) savedInstanceState.getSerializable(EPISODISOPLUSINFO);
        setupCollapsingToolbar();
        setupViewPager();
        setupToolbar();
        setupFAB(epiInfo.get(0));
        setTitle(epiInfo.get(0).getNombreAnime());
        Picasso.with(getBaseContext()).load(epi.get(0).getUrlImagen()).into(header);
    }

}
