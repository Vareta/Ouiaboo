package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ouiaboo.ouiaboo.AnalyticsApplication;
import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.Funciones;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Tables.DescargadosTable;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.adaptadores.AdContMenuCentral;
import com.ouiaboo.ouiaboo.adaptadores.AdEpisodios;
import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;
import com.ouiaboo.ouiaboo.clases.Episodios;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EpisodiosFlv.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EpisodiosFlv extends android.support.v4.app.Fragment implements AdEpisodios.CustomRecyclerListener{

    private RecyclerView list;
    private ArrayList<Episodios> episodios;
    private OnFragmentInteractionListener mListener;
    private Snackbar snackbar;
    private CoordinatorLayout coordLayout;
    private int posicionAnime;
    private int posAnimeOnClick = -1; //valor auxiliar para actualizar el adaptador en OnAttach
    private AdEpisodios adaptador;
    private Tracker mTracker;

    public EpisodiosFlv() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_episodios, container, false);
        list = (RecyclerView)convertView.findViewById(R.id.episodios);
        coordLayout = (CoordinatorLayout)getActivity().findViewById(R.id.coord_layout);


        getData();
        setAdaptador();
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        return convertView;
    }

    @SuppressWarnings("unchecked")
    private void getData() {
        episodios = (ArrayList<Episodios>)getArguments().getSerializable("episodios");
    }

    private void setAdaptador() {
        adaptador = new AdEpisodios(getContext(), episodios);
        adaptador.setClickListener(this);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(adaptador);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (posAnimeOnClick != -1) {
            adaptador.notifyItemChanged(posAnimeOnClick);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            AnalyticsApplication.getInstance().trackScreenView("Episodios");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(onComplete);
        mListener = null;
    }

    @Override
    public void customClickListener(View v, int position) {
        posAnimeOnClick = position; //para luego al volver de ver el video, le diga al adaptador que dicho item cambió en OnAttach
        HomeScreenEpi objEpi = new HomeScreenEpi(episodios.get(position).getUrlEpisodio(), episodios.get(0).getNombreAnime(),
                                                episodios.get(position).getNumero(), episodios.get(0).getUrlImagen());
        mListener.onEpisodiosFlvInteraction(objEpi);

    }

    @Override
    public void customLongClickListener(View v, int position) {
        final int posAnime = position; //para diferenciar el onclick del listpopup
        Utilities util = new Utilities();
        List<DrawerItemsListUno> items = new ArrayList<>();
        items.add(new DrawerItemsListUno(getActivity().getString(R.string.descargar_PopupWindow), R.drawable.ic_file_download_white_24dp));
        items.add(new DrawerItemsListUno(getActivity().getString(R.string.masTarde_PopupWindow), R.drawable.ic_watch_later_white_24dp));

        AdContMenuCentral adapter = new AdContMenuCentral(getActivity(), items);

        final ListPopupWindow listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setAdapter(adapter);

        listPopupWindow.setAnchorView(v.findViewById(R.id.episodios_flv));
        int width = util.measureContentWidth(adapter, this);
        listPopupWindow.setWidth(width);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Funciones fun = new Funciones();
                if (position == 0) {

                    posicionAnime = posAnime; //almacena la posicion para asi utilizarla en otros contextos
                    List<DescargadosTable> lista = DataSupport.where("urlCapitulo=?", episodios.get(posAnime).getUrlEpisodio()).find(DescargadosTable.class);
                    if (!lista.isEmpty()) { //ya tiene el capitulo
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
                        AnalyticsApplication.getInstance().trackEvent("Anime", "descargar", episodios.get(0).getNombreAnime());
                        new DownloadAnime().execute();
                    }

                    listPopupWindow.dismiss();
                }

                if (position == 1) {
                    //getNombreAnime y getUrlImagen son en posicion 0 ya que en las demas se encuentran como null
                    HomeScreenEpi episodio = new HomeScreenEpi(episodios.get(posAnime).getUrlEpisodio(), episodios.get(0).getNombreAnime(),
                            episodios.get(posAnime).getNumero(), episodios.get(0).getUrlImagen());
                    if (!fun.esPosibleverMasTardeHome(episodio)) { //no se pudo
                        snackbar = Snackbar.make(coordLayout, getString(R.string.noti_vermastarde_no), Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView)sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();

                    } else {
                        AnalyticsApplication.getInstance().trackEvent("Anime", "ver mas tarde", episodios.get(0).getNombreAnime());
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


    public interface OnFragmentInteractionListener {
        public void onEpisodiosFlvInteraction(HomeScreenEpi objEpi);
    }

    public int measureContentWidth(ListAdapter adapter) {
        int maxWidth = 0;
        int count = adapter.getCount();
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        View itemView = null;
        for (int i = 0; i < count; i++) {
            itemView = adapter.getView(i, itemView, ((ViewGroup)getView().getParent()));
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            maxWidth = Math.max(maxWidth, itemView.getMeasuredWidth());
        }
        return maxWidth;
    }

    /*Obtiene la url de un anime mediante el url del capitulo de manera asincrona*/
    public class DownloadAnime extends AsyncTask<Void, Void, Void> {
        String nombreVideo = episodios.get(0).getNombreAnime() + "-" + episodios.get(posicionAnime).getNumero() + ".mp4";
        @Override
        protected Void doInBackground(Void... params) {
            Animeflv animeflv = new Animeflv();
            String url = animeflv.urlDisponible(episodios.get(posicionAnime).getUrlEpisodio(), getActivity()); //consigue la url del video a descargar
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(episodios.get(posicionAnime).getNumero()); //descripcion de la notificacion
            request.setTitle(episodios.get(0).getNombreAnime()); //titulo de la notificacion
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //setea las notificaciones
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES + "/Ouiaboo", nombreVideo);

            request.setMimeType("video/x-msvideo");

            DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            long id = manager.enqueue(request);

            //almacena los capitulos guardados en la tabla, sin importar si estos estan completamente descargados
            DescargadosTable descargas = new DescargadosTable(id, episodios.get(0).getNombreAnime(),
                    episodios.get(posicionAnime).getNumero(),
                    null,//preview, null por defecto
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Ouiaboo/" + nombreVideo,
                    episodios.get(posicionAnime).getUrlEpisodio(),
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
}
