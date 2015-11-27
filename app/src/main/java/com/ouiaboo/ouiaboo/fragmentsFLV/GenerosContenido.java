package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ouiaboo.ouiaboo.AnalyticsApplication;
import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.adaptadores.AdGenerosEndless;
import com.ouiaboo.ouiaboo.clases.GenerosClass;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenerosContenido.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class GenerosContenido extends android.support.v4.app.Fragment implements AdGenerosEndless.CustomRecyclerListener {

    private OnFragmentInteractionListener mListener;
    private String url;
    private String urlSiguiente;
    private List<HomeScreenEpi> animeByGenero;
    private List<HomeScreenEpi> animeSiguiente;
    private AdGenerosEndless adaptador;
    private RecyclerView list;
    private ProgressBar bar;
    private boolean tienePaginaSiguiente;
    private Tracker mTracker;


    public GenerosContenido() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView =  inflater.inflate(R.layout.fragment_generos_cont, container, false);
        list = (RecyclerView)convertView.findViewById(R.id.anime_genero);
        bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));



        getData();
        
        new GetAnimeByGenero().execute(this);

        return convertView;
    }

    @SuppressWarnings("unchecked")
    private void getData() {
        GenerosClass generoRecibido = (GenerosClass) getArguments().getSerializable("genero");
        if (generoRecibido != null) {
            url = generoRecibido.getUrlGenero();
            getActivity().setTitle(generoRecibido.getNombre());
        }

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
        AnalyticsApplication.getInstance().trackScreenView("Generos Anime");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void customClickListener(View v, int position) {
        mListener.onGenerosContenidoInteraction(animeByGenero.get(position).getUrlCapitulo());
    }

    public interface OnFragmentInteractionListener {
        public void onGenerosContenidoInteraction(String url);
    }

    private class GetAnimeByGenero extends AsyncTask<AdGenerosEndless.CustomRecyclerListener, Void, Void> {

        @Override
        protected Void doInBackground(AdGenerosEndless.CustomRecyclerListener... params) {
            Animeflv anime = new Animeflv();
            Utilities util = new Utilities();
            try {
                Document codigoFuente = util.connect(url);
                urlSiguiente = anime.siguientePagina(codigoFuente);
                Log.d("URL", urlSiguiente);
                tienePaginaSiguiente = !urlSiguiente.equals(""); //comprueba si tiene pagina siguiente
                animeByGenero = anime.busquedaFLV(codigoFuente); //se ocupa el de busqueda ya que el diseño de la pagina de generos es igual
                adaptador = new AdGenerosEndless(getActivity(), animeByGenero, list);
                adaptador.setClickListener(params[0]);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {

            list.setAdapter(adaptador);
            bar.setVisibility(View.GONE);
            setOnLoadMoreListener();
        }
    }

    private class GetAnimeSiguiente extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Animeflv anime = new Animeflv();
            Utilities util = new Utilities();
            try {

                Document codigoFuente = util.connect(urlSiguiente);
                urlSiguiente = anime.siguientePagina(codigoFuente);//comprueba si tiene pagina siguiente
                tienePaginaSiguiente = !urlSiguiente.equals(""); //si urlSiguiente es igual a "" --> tienePaginaSiguiente = false, de otra manera true
                animeSiguiente = anime.busquedaFLV(codigoFuente); //se ocupa el de busqueda ya que el diseño de la pagina de generos es igual

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
            animeByGenero.remove(animeByGenero.size() - 1); //remueve el item null del progress bar
            adaptador.notifyItemRemoved(animeByGenero.size()); //notifica que removio un item
            int posFinal = animeByGenero.size(); //tamaño antes de añadir items
            animeByGenero.addAll(animeSiguiente); //añade todos los Anime nuevos
            adaptador.notifyItemRangeInserted(posFinal, animeByGenero.size()); //notifica que los añadio todos
            adaptador.setLoaded(); //indica que ya no se esta cargando
        }
    }

    private void setOnLoadMoreListener() {
        adaptador.setOnLoadMoreListener(new AdGenerosEndless.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (tienePaginaSiguiente) {
                    animeByGenero.add(null); //agrega un item null para activar el view de la progressbar
                    adaptador.notifyItemInserted(animeByGenero.size() - 1); //notifica que añadio un elemento
                    new GetAnimeSiguiente().execute(); //recolecta el anime de la pagina siguiente
                }
            }
        });
    }

}
