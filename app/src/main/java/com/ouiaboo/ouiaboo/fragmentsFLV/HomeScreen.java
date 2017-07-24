package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.app.DownloadManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.ouiaboo.ouiaboo.AnalyticsApplication;
import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.EpisodiosPlusInfo;
import com.ouiaboo.ouiaboo.Funciones;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Reyanime;
import com.ouiaboo.ouiaboo.Tables.DescargadosTable;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.adaptadores.AdContMenuCentral;
import com.ouiaboo.ouiaboo.adaptadores.AdHomeScreen;
import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import org.jsoup.nodes.Document;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeScreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeScreen extends android.support.v4.app.Fragment implements AdHomeScreen.CustomRecyclerListener {
    private String animeflvUrl = "https://animeflv.net/";
    private String reyanimeUrl = "http://reyanime.com/";
    private OnFragmentInteractionListener mListener;
    private AdHomeScreen adaptador;
    private RecyclerView list;
    private ProgressBar bar;
    private List<HomeScreenEpi> animesRecientes;
    private Animeflv animeflv;
    private Reyanime reyanime;
    private Utilities util;
    private CoordinatorLayout coordLayout;
    private Snackbar snackbar;
    private int posicionAnime;
    private AdHomeScreen.CustomRecyclerListener listener;
    private SwipeRefreshLayout swipeRefresh;
    List<BroadcastReceiver> receivers = new ArrayList<>(); //variable que contiene el receiver de descarga
    private ProgressBar downloadBar;
    private WebView webView;

    public HomeScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //hace que el fragment se conserve
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_home_screen, container, false);
        getActivity().setTitle(R.string.inicio_drawer_layout);
        iniciaView(convertView);
        listener = this;
        iniciaFragment();

        //registra el servicio
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        //agrega el servicio a la lista
        receivers.add(onComplete);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SwipeRefresh().execute(listener);
            }
        });

        return convertView;
    }

    private void iniciaView(View convertView) {
        coordLayout = (CoordinatorLayout) convertView.findViewById(R.id.coord_layout);
        webView = (WebView) getActivity().findViewById(R.id.webView);
        list = (RecyclerView) convertView.findViewById(R.id.home_screen_list_animeflv); //lista fragment
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        bar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        downloadBar = (ProgressBar) getActivity().findViewById(R.id.updateAppProgressBar);
        ((ProgressBar) getActivity().findViewById(R.id.updateAppProgressBar)).getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.rojo), PorterDuff.Mode.SRC_IN);
        //Cambia el color de la progressbar para versiones anteriores
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            bar.setIndeterminate(true);
            ((ProgressBar) getActivity().findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.accent_light), PorterDuff.Mode.SRC_IN);
        }
        swipeRefresh = (SwipeRefreshLayout) convertView.findViewById(R.id.homeScreen_swipe_refresh);
    }

    private void iniciaFragment() {
        if (getAnimesRecientes() == null) {
            new GetAnimeHomeScreen().execute(this);
        } else {
            adaptador = new AdHomeScreen(getActivity(), animesRecientes);
            adaptador.setClickListener(this);
            list.setAdapter(adaptador);
        }
    }

    /*en caso que la pagina contenga cloudflare*/
   /* public void setWebView() {
        //CookieManager.getInstance().setAcceptCookie(true);
        String agent = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
        webView.getSettings().setUserAgentString(agent);
        webView.loadUrl(animeFLV);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                String cookies = CookieManager.getInstance().getCookie(url); //carga las cookies

                SharedPreferences sharedPref = getActivity().getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("cookies", cookies); //guarda las cookies en preferencias
                editor.apply();
                Log.d("Null", "Cargue");
            }
        });
    }*/

    @Override
    public void customClickListener(View v, int position) {
        //flvAnimes.añadirHistorialFlv(animesRecientes.get(position).getNombre(), animesRecientes.get(position).getUrlCapitulo());
        HomeScreenEpi objEpi = animesRecientes.get(position);
        mListener.onHomeScreenInteraction(objEpi);
    }

    @Override
    public void customLongClickListener(View v, int position) {

        final int posAnime = position; //para diferenciar el onclick del listpopup
        // LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // View popUpView = inflater.inflate(R.layout.context_menu, null);
        List<DrawerItemsListUno> items = new ArrayList<>();
        items.add(new DrawerItemsListUno(getString(R.string.descargar_PopupWindow), R.drawable.ic_file_download_white_24dp));
        items.add(new DrawerItemsListUno(getString(R.string.irAnime_PopupWindow), R.drawable.ic_forward_white_24dp));
        items.add(new DrawerItemsListUno(getString(R.string.masTarde_PopupWindow), R.drawable.ic_watch_later_white_24dp));

        AdContMenuCentral adapter = new AdContMenuCentral(getActivity(), items);

        final ListPopupWindow listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setAdapter(adapter);

        listPopupWindow.setAnchorView(v.findViewById(R.id.nombre_flv));
        int width = util.measureContentWidth(adapter, this);
        listPopupWindow.setWidth(width);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Funciones fun = new Funciones();
                Utilities util = new Utilities();

                if (position == 0) {//descarga

                    posicionAnime = posAnime; //almacena la posicion para asi utilizarla en otros contextos
                    List<DescargadosTable> lista = DataSupport.where("urlCapitulo=?", animesRecientes.get(posAnime).getUrlCapitulo()).find(DescargadosTable.class);
                    if (!lista.isEmpty()) { //ya tiene el capitulo
                        if (!lista.get(0).isComplete()) { //cuando la descarga se esta efectuando en estos momentos
                            snackbar = Snackbar.make(coordLayout, getString(R.string.noti_descargado_actualmente), Snackbar.LENGTH_LONG);
                        } else {
                            snackbar = Snackbar.make(coordLayout, getString(R.string.noti_descargado_existe), Snackbar.LENGTH_LONG);
                        }
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
                    } else { //no tiene el capitulo, por lo tanto lo descarga
                        AnalyticsApplication.getInstance().trackEvent("Anime", "descargar", animesRecientes.get(posicionAnime).getNombre());
                        new DownloadAnime().execute();
                    }

                    listPopupWindow.dismiss();
                }


                if (position == 1) {//ir a anime

                    new EpiUrlToAnimeUlr().execute(animesRecientes.get(posAnime).getUrlCapitulo());

                   /* if (!fun.esPosibleFavoritosHome(animesRecientes.get(posAnime))) { //no se pudo
                        snackbar = Snackbar.make(coordLayout, getString(R.string.noti_favoritos_no), Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView)sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
                    } else {
                        snackbar = Snackbar.make(coordLayout, getString(R.string.noti_favoritos_si), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }*/
                    listPopupWindow.dismiss();
                }

                if (position == 2) {//ver mas tarde
                    boolean esPosibleMasTarde;

                    if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                        esPosibleMasTarde = fun.esPosibleverMasTardeHome(animesRecientes.get(posAnime), Utilities.ANIMEFLV);
                    } else {//reyanime
                        esPosibleMasTarde = fun.esPosibleverMasTardeHome(animesRecientes.get(posAnime), Utilities.REYANIME);
                    }

                    if (!esPosibleMasTarde) { //no se pudo
                        snackbar = Snackbar.make(coordLayout, getString(R.string.noti_vermastarde_no), Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
                    } else {
                        AnalyticsApplication.getInstance().trackEvent("Anime", "ver mas tarde", animesRecientes.get(posicionAnime).getNombre());
                        snackbar = Snackbar.make(coordLayout, getString(R.string.noti_vermastarde_si), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    listPopupWindow.dismiss();
                }
            }
        });
        listPopupWindow.setModal(true);
        listPopupWindow.setHorizontalOffset(0);
        listPopupWindow.show();
    }


    private class GetAnimeHomeScreen extends AsyncTask<AdHomeScreen.CustomRecyclerListener, Void, Void> {
        boolean sonNecesariasCookies = false;
        @Override
        protected Void doInBackground(AdHomeScreen.CustomRecyclerListener... params) {
            try {
                util = new Utilities();
                Document codigoFuente;
                Log.d("PROVEEDOR", String.valueOf(util.queProveedorEs(getContext())));
                if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                    animeflv = new Animeflv();
                    codigoFuente = util.connect(animeflvUrl);
                    if (animeflv.estaCloudflareActivado(codigoFuente) && !util.existenCookies(getContext())) {
                        sonNecesariasCookies = true;
                    }
                    if (util.existenCookies(getContext())) {
                        codigoFuente = util.connect(animeflvUrl, util.getCookiesEnSharedPreferences(getContext()));
                    }
                    animesRecientes = animeflv.homeScreenAnimeFlv(codigoFuente, getResources());

                } else {
                    reyanime = new Reyanime();
                    codigoFuente = util.connect(reyanimeUrl);
                    animesRecientes = reyanime.homeScreen(codigoFuente, getResources());
                }
               /* for (int i = 0; i < animesRecientes.size(); i++){

                    Log.d("Url", animesRecientes.get(i).getUrlCapitulo());
                    Log.d("Nombre", animesRecientes.get(i).getNombre());
                    Log.d("Informacion", animesRecientes.get(i).getInformacion());
                    Log.d("Preview", animesRecientes.get(i).getPreview());
                }*/
                listener = params[0];
                adaptador = new AdHomeScreen(getActivity(), animesRecientes);
                adaptador.setClickListener(params[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
            // Log.d("HOLA", "PREEXECUTE 333");
        }

        @Override
        protected void onPostExecute(Void result) {
            if (sonNecesariasCookies) {
                getCookies();
            }
            list.setAdapter(adaptador);
            bar.setVisibility(View.GONE);
            //
        }

    }

    /**
     * Obtiene las cookies de animeflv para cuando este tiene activado el cloudflare
     */
    public void getCookies() {
        Log.d("cookies", "1");
        CookieManager.getInstance().setAcceptCookie(true);
        String agent = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setUserAgentString(agent);
        webView.setWebViewClient(new WebViewClient() {
            boolean respondioACloudflare = true;
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Loading started for URL.
                webView.clearCache(true);
                webView.clearHistory();
                Log.d("url", url);
                /*
                ********* Por algun motivo ya no muestra la url direccionada, asi que se deshabilita por el momento*******/
                /*if (url.contains("cdn-cgi")) { //es la url que envia la respuesta al script de cloudflare
                    Log.d("cloud", "1");
                    respondioACloudflare = true;
                }*/
                if (url.contains(animeflvUrl) && respondioACloudflare) {
                    Log.d("cloud", "2");
                    System.out.println("cookies   " + CookieManager.getInstance().getCookie(url)); //carga las cookies
                    util.setCookiesEnSharedPreferences(CookieManager.getInstance().getCookie(url), getContext());
                    util.setCookiesBoolean(true, getContext());
                    webView.destroy();
                    new GetAnimeHomeScreen().execute(listener);
                }

            }


        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(animeflvUrl);

    }

    private class SwipeRefresh extends AsyncTask<AdHomeScreen.CustomRecyclerListener, Void, Void> {
        boolean sonNecesariasCookies = false;
        @Override
        protected Void doInBackground(AdHomeScreen.CustomRecyclerListener... params) {
            try {
                util = new Utilities();
                Document codigoFuente;
                if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                    animeflv = new Animeflv();
                    codigoFuente = util.connect(animeflvUrl);
                    if (animeflv.estaCloudflareActivado(codigoFuente) && !util.existenCookies(getContext())) {
                        sonNecesariasCookies = true;
                    }
                    if (util.existenCookies(getContext())) {
                        codigoFuente = util.connect(animeflvUrl, util.getCookiesEnSharedPreferences(getContext()));
                    }
                    animesRecientes = animeflv.homeScreenAnimeFlv(codigoFuente, getResources());
                } else {
                    reyanime = new Reyanime();
                    codigoFuente = util.connect(reyanimeUrl);
                    animesRecientes = reyanime.homeScreen(codigoFuente, getResources());
                }
                adaptador = new AdHomeScreen(getActivity(), animesRecientes);
                adaptador.setClickListener(params[0]);

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
            if (sonNecesariasCookies) {
                getCookies();
            }
            list.setAdapter(adaptador);
            swipeRefresh.setRefreshing(false);
        }


    }

    /*Obtiene la url de un anime mediante el url del capitulo de manera asincrona*/
    public class EpiUrlToAnimeUlr extends AsyncTask<String, Void, Void> {
        private String url;

        @Override
        protected Void doInBackground(String... params) {
            Utilities util = new Utilities();
            Document codigoFuente = util.connect(params[0]);
            if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                if (util.existenCookies(getContext())) {
                    codigoFuente = util.connect(params[0], util.getCookiesEnSharedPreferences(getContext()));
                }
                url = animeflv.urlCapituloToUrlAnime(codigoFuente);
            } else {
                url = reyanime.urlCapituloToUrlAnime(codigoFuente);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            list.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            list.setVisibility(View.VISIBLE);
            bar.setVisibility(View.GONE);
            Intent intent = new Intent(getActivity(), EpisodiosPlusInfo.class);
            intent.putExtra("url", url);
            startActivity(intent);
        }
    }

    /*Obtiene la url de un anime mediante el url del capitulo de manera asincrona*/
    public class DownloadAnime extends AsyncTask<Void, Void, Void> {
        String nombreVideo = animesRecientes.get(posicionAnime).getNombre() + "-" + animesRecientes.get(posicionAnime).getInformacion() + ".mp4";

        @Override
        protected Void doInBackground(Void... params) {

            Utilities util = new Utilities();
            String url;
            if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                Animeflv animeflv = new Animeflv();
                url = animeflv.urlDisponible(animesRecientes.get(posicionAnime).getUrlCapitulo(), getContext()); //consigue la url del video a descargar
            } else {//reyanime
                Reyanime reyanime = new Reyanime();
                url = reyanime.urlDisponible(animesRecientes.get(posicionAnime).getUrlCapitulo(), getContext());
            }
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(animesRecientes.get(posicionAnime).getInformacion()); //descripcion de la notificacion
            request.setTitle(animesRecientes.get(posicionAnime).getNombre()); //titulo de la notificacion
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //setea las notificaciones
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES + "/Ouiaboo", nombreVideo);
            request.setMimeType("video/x-msvideo");

            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            long id = manager.enqueue(request);

            //almacena los capitulos guardados en la tabla, sin importar si estos estan completamente descargados
            DescargadosTable descargas = new DescargadosTable(id, animesRecientes.get(posicionAnime).getNombre(),
                    animesRecientes.get(posicionAnime).getInformacion(),
                    null,//preview, null por defecto
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Ouiaboo/" + nombreVideo,
                    animesRecientes.get(posicionAnime).getUrlCapitulo(),
                    false); //estado de la descarga, falso por defecto
            descargas.save();
            return null;
        }

        @Override
        protected void onPreExecute() {
            downloadBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            downloadBar.setVisibility(View.GONE);
            Log.d("TERMINE", "termine");
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            getActivity().setTitle(R.string.inicio_drawer_layout);
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Si el usuario cambio de proveedor, se actualiza el fragment
        Utilities util = new Utilities();
        if (util.proveedorModificado(getContext())) {
            snackbar = Snackbar.make(coordLayout, getString(R.string.actualizandoProveedor_Settings), Snackbar.LENGTH_SHORT);
            snackbar.show();
            new SwipeRefresh().execute(this);
        }
        AnalyticsApplication.getInstance().trackScreenView("Home Screen");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("HOMESCREEN", "DETACH");
        setData(animesRecientes);
        /*
        Aqui se verifica que el receiver este registrado antes de sacarlo del registro. Ya que existen
        casos en donde la pantalla rota y el fragment ejecuta sólo onDetach, por lo cual jamas
        registra el receiver. lo cual genera un error
         */
        if (receivers.contains(onComplete)) {
            receivers.remove(onComplete);
            getActivity().unregisterReceiver(onComplete);
        }
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onHomeScreenInteraction(HomeScreenEpi objEpi);
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            List<DescargadosTable> list = DataSupport.where("complete=?", String.valueOf(0)).find(DescargadosTable.class); //obtiene todas las descargas no completadas

            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    DownloadManager dw = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Cursor c = dw.query(new DownloadManager.Query().setFilterById(list.get(i).getIdDescarga()));
                    if (!c.moveToFirst()) {
                        Log.e("vacio", "Empty row");
                        return;
                    }
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        DescargadosTable descargadosTable = new DescargadosTable();
                        descargadosTable.setComplete(true);
                        descargadosTable.updateAll("urlCapitulo=?", list.get(i).getUrlCapitulo());
                        break;
                    }
                }
            }
        }
    };

    /* deprecado
    private class GetVideoUrlAndPlay extends AsyncTask<Integer, Void, Void> {
        String url;

        @Override
        protected Void doInBackground(Integer... params) {
            Utilities util = new Utilities();
            int posicion = params[0];
            try {
                Document codigoFuente = util.connect(animesRecientes.get(posicion).getUrlCapitulo());
                if (util.queProveedorEs(getActivity()) == Utilities.ANIMEFLV) {
                    Animeflv anime = new Animeflv();
                    url = anime.urlVideo(animesRecientes.get(posicion).getUrlCapitulo());
                } else {
                    Animejoy joyAnime = new Animejoy();
                    url = joyAnime.urlVideo(codigoFuente);
                }

                // Log.d("HOLA", "pase despues");
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
            intent.setDataAndType(Uri.parse(url), "video/x-msvideo");
            startActivity(intent);
        }
    }*/

    public void setData(List<HomeScreenEpi> animesRecientes) {
        this.animesRecientes = animesRecientes;
    }

    public List<HomeScreenEpi> getAnimesRecientes() {
        return animesRecientes;
    }


}
