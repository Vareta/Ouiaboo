package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.app.Activity;
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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.adaptadores.AdEpisodios;
import com.ouiaboo.ouiaboo.adaptadores.AdInfoEpisodios;
import com.ouiaboo.ouiaboo.adaptadores.AdapatadorDrawerExpList;
import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;
import com.ouiaboo.ouiaboo.clases.Episodios;
import com.ouiaboo.ouiaboo.clases.SitiosWeb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EpisodiosFlv.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EpisodiosFlv extends android.support.v4.app.Fragment implements AdEpisodios.CustomRecyclerListener{

    private RecyclerView list;
    private ProgressBar bar;
    private AdEpisodios adaptador;
    private ArrayList<Episodios> epi;
    private ArrayList<Episodios> episodioInfo;
    private OnFragmentInteractionListener mListener;
    private String url;
    private ExpandableListView expListView;
    private ArrayList<DrawerItemsListUno> listPadre;
    private ExpandableListAdapter listAdapter;
    private HashMap<DrawerItemsListUno, List<Episodios>> listChild;

    public EpisodiosFlv() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_episodios, container, false);
        list = (RecyclerView)convertView.findViewById(R.id.episodios);
        bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
        expListView = (ExpandableListView)convertView.findViewById(R.id.info_expandible);
        url = getArguments().getString("query");

        new BackgroundTask().execute(this);


        return convertView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void customRecyclerListener(View v, int position) {
        mListener.onEpisodiosFlvInteraction(epi.get(position).getUrl());
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
        // TODO: Update argument type and name
        public void onEpisodiosFlvInteraction(String url);
    }

    private class BackgroundTask extends AsyncTask<AdEpisodios.CustomRecyclerListener, Void, Void> {


        @Override
        protected Void doInBackground(AdEpisodios.CustomRecyclerListener... params) {
            try {
                Animeflv ani = new Animeflv(getResources());
                epi = ani.getEpisodios(url);
                //lista expandible
                listPadre = new ArrayList<DrawerItemsListUno>();
                listPadre.add(new DrawerItemsListUno("informacion", R.drawable.ic_action_globe)); //bloque informacion
                episodioInfo = new ArrayList<Episodios>();
                episodioInfo.add(new Episodios(epi.get(0).getUrl(), epi.get(0).getNumero(), epi.get(0).getUrlImagen(),
                        epi.get(0).getInformacion(), epi.get(0).getTipo(), epi.get(0).getEstado(), epi.get(0).getGeneros(),
                        epi.get(0).getFechaInicio())); //agrega el primero elemento que contiene la informacion del espisodio
                listChild = new HashMap<DrawerItemsListUno, List<Episodios>>();
                listChild.put(listPadre.get(0), episodioInfo); //lista de elementos que se muestran al expandir
                listAdapter = new AdInfoEpisodios(getActivity(), listPadre, listChild); //crea el adaptador de la lista expandible
               /* if (listAdapter.isEmpty()) {
                    Log.d("NULO", "adaptador expandible nulo");
                }*/
                /*System.out.println("HOLA " + listPadre.get(0).getIconId() + "  " + listPadre.get(0).getNombre());
                System.out.println("222  "+ " "+  epi.get(0).getUrl()+ " "+ epi.get(0).getNumero()+ " "+  epi.get(0).getUrlImagen()+ " "+
                                epi.get(0).getInformacion()+ " "+  epi.get(0).getTipo()+ " "+  epi.get(0).getEstado()+ " "+  epi.get(0).getGeneros()+ " "+
                                        epi.get(0).getFechaInicio());*/
                //lista de episodios
                adaptador = new AdEpisodios(getActivity(), epi);
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
            list.setLayoutManager(new LinearLayoutManager(getActivity()));

            list.setAdapter(adaptador);

            expListView.setAdapter(listAdapter); //setea el adaptador de la lista expandible
            bar.setVisibility(View.GONE);
        }
    }

}
