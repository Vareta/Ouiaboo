package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.app.DownloadManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.Animejoy;
import com.ouiaboo.ouiaboo.EpisodiosPlusInfo;
import com.ouiaboo.ouiaboo.Funciones;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Tables.DescargadosTable;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.VideoPlayer;
import com.ouiaboo.ouiaboo.adaptadores.AdContMenuCentral;
import com.ouiaboo.ouiaboo.adaptadores.AdHomeScreen;
import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import org.jsoup.nodes.Document;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeScreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeScreen extends android.support.v4.app.Fragment implements AdHomeScreen.CustomRecyclerListener{
    private String animeFLV = "http://animeflv.net/";
    private String animeJoy = "http://www.animejoy.tv/";
    private OnFragmentInteractionListener mListener;
    private AdHomeScreen adaptador;
    private RecyclerView list;
    private ProgressBar bar;
    private ArrayList<HomeScreenEpi> animesRecientes;
    private Animeflv flvAnimes;
    private Animejoy joyAnimes;
    private Utilities util;
    private CoordinatorLayout coordLayout;
    private Snackbar snackbar;
    private int posicionAnime;
    private WebView webView;
    public static final String PREFERENCIAS = "preferencias";
    private AdHomeScreen.CustomRecyclerListener listener;

    public HomeScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView =  inflater.inflate(R.layout.fragment_home_screen, container, false);
        coordLayout = (CoordinatorLayout)convertView.findViewById(R.id.coord_layout);
        getActivity().setTitle(R.string.inicio_drawer_layout);
        list = (RecyclerView)convertView.findViewById(R.id.home_screen_list_animeflv); //lista fragment
        bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
        webView = (WebView)getActivity().findViewById(R.id.web_view);
        new BackgroundTask().execute(this);
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        return convertView;
    }

    /*en caso que la pagina contenga cloudflare*/
    public void setWebView() {
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
    }

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
        items.add( new DrawerItemsListUno( "Favorito", R.drawable.ic_action_globe ) );
        items.add( new DrawerItemsListUno( "Descargar", R.drawable.ic_action_globe ) );
        items.add(new DrawerItemsListUno("Ir a anime", R.drawable.ic_action_globe));
        items.add(new DrawerItemsListUno("Ver mas tarde", R.drawable.ic_action_globe));

        AdContMenuCentral adapter = new AdContMenuCentral(getActivity(), items);

        final ListPopupWindow listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setAdapter(adapter);

        listPopupWindow.setAnchorView(v.findViewById(R.id.nombre_flv));
        int width = measureContentWidth(adapter);
        listPopupWindow.setWidth(width);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Funciones fun = new Funciones();
                //Toast.makeText(mContext, getAdapterPosition() + " : " + position, Toast.LENGTH_SHORT).show();

                    /*Intent intent = new Intent(mContext, CarActivity.class);
                    intent.putExtra("car", mList.get( getAdapterPosition() ));
                    mContext.startActivity(intent);*/
                if (position == 0) {
                    String path2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Ouiaboo";
                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Ouiaboo/Kowabon -Episodio número 3.mp4";
                    File file = new File(path2);

                    if (file.list() == null) {
                        Log.d("Null", "PATH null");
                    } else {
                        for (int i = 0; i < file.list().length; i++) {
                            Log.d("List", file.list()[i]);
                        }
                    }
                    Bitmap preview = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);

                    if (preview == null) {
                        Log.d("Null", "preview null");
                        Log.d("PATH", path);
                    }
                    Log.d("PATH", Environment.DIRECTORY_MOVIES);


                }

                if (position == 1) {

                    posicionAnime = posAnime; //almacena la posicion para asi utilizarla en otros contextos
                    List<DescargadosTable> lista = DataSupport.where("urlCapitulo=?", animesRecientes.get(posAnime).getUrlCapitulo()).find(DescargadosTable.class);
                    if (!lista.isEmpty()) { //ya tiene el capitulo
                        Log.d("TAMAÑO", String.valueOf(lista.size()));
                        if (!lista.get(0).isComplete()){ //cuando la descarga se esta efectuando en estos momentos
                            snackbar = Snackbar.make(coordLayout, getString(R.string.noti_descargado_actualmente), Snackbar.LENGTH_LONG);
                        } else {
                            snackbar = Snackbar.make(coordLayout, getString(R.string.noti_descargado_existe), Snackbar.LENGTH_LONG);
                        }
                        View sbView = snackbar.getView();
                        TextView textView = (TextView)sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
                    } else { //no tiene el capitulo, por lo tanto lo descarga
                        new DownloadAnime().execute();
                    }

                    listPopupWindow.dismiss();
                }


                if (position == 2) {

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

                if (position == 3) {

                    if (!fun.esPosibleverMasTardeHome(animesRecientes.get(posAnime))) { //no se pudo
                        snackbar = Snackbar.make(coordLayout, getString(R.string.noti_vermastarde_no), Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView)sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();

                    } else {
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

    public int measureContentWidth(ListAdapter adapter) {
        int maxWidth = 0;
        int count = adapter.getCount();
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        View itemView = null;
        for (int i = 0; i < count; i++) {
            itemView = adapter.getView(i, itemView, ((ViewGroup)getView().getParent()));
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            maxWidth = Math.max(maxWidth, itemView.getMeasuredWidth());
        }
        return maxWidth;
    }


    private class BackgroundTask extends AsyncTask<AdHomeScreen.CustomRecyclerListener, Void, Void> {

        @Override
        protected Void doInBackground(AdHomeScreen.CustomRecyclerListener... params) {
            try {
                util = new Utilities();
                Document codigoFuente;
                Log.d("PROVEEDOR", String.valueOf(util.queProveedorEs(getContext())));
                if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                    flvAnimes = new Animeflv();
                    codigoFuente = util.connect(animeFLV);
                    animesRecientes = flvAnimes.homeScreenAnimeFlv(codigoFuente, getResources());
                } else {
                    joyAnimes = new Animejoy();
                    codigoFuente = util.connect(animeJoy);
                    animesRecientes = joyAnimes.homeScreenAnimejoy(codigoFuente, getResources());
                }
               /* for (int i = 0; i < animesRecientes.size(); i++){

                    Log.d("Url", animesRecientes.get(i).getUrlCapitulo());
                    Log.d("Nombre", animesRecientes.get(i).getNombre());
                    Log.d("Informacion", animesRecientes.get(i).getInformacion());
                    Log.d("Preview", animesRecientes.get(i).getPreview());
                }*/

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
            list.setLayoutManager(new LinearLayoutManager(getActivity()));
            list.setAdapter(adaptador);
            bar.setVisibility(View.GONE);
            //
        }


    }



    /*Obtiene la url de un anime mediante el url del capitulo de manera asincrona*/
    public class EpiUrlToAnimeUlr extends AsyncTask<String, Void, Void> {
        private String url;
        @Override
        protected Void doInBackground(String... params) {
            url = flvAnimes.urlCapituloToUrlAnime(params[0]);
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
            Animeflv animeflv = new Animeflv();
            String url = animeflv.urlDisponible(animesRecientes.get(posicionAnime).getUrlCapitulo(), getActivity()); //consigue la url del video a descargar
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(animesRecientes.get(posicionAnime).getInformacion()); //descripcion de la notificacion
            request.setTitle(animesRecientes.get(posicionAnime).getNombre()); //titulo de la notificacion
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //setea las notificaciones
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES + "/Ouiaboo", nombreVideo);

            request.setMimeType("video/x-msvideo");

            DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
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
        }

        @Override
        protected void onPostExecute(Void result) {
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
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(onComplete);
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
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
            intent.setDataAndType(Uri.parse(url), "video/mp4");
            startActivity(intent);
        }
    }

}
