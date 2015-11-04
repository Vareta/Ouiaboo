package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.adaptadores.AdBusquedaFLV;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Busqueda.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Busqueda extends android.support.v4.app.Fragment implements AdBusquedaFLV.CustomRecyclerListener{

    public OnFragmentInteractionListener mListener;
    private RecyclerView list;
    private ProgressBar bar;
    private String searchQuery;
    private String queryTemplate = "http://animeflv.net/animes/?buscar=";
    private ArrayList<com.ouiaboo.ouiaboo.clases.HomeScreen> animesBuscados;
    private AdBusquedaFLV adaptador;
    private boolean produceResultados;
    private TextView sinResultados;

    public Busqueda() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_busqueda, container, false);
        getActivity().setTitle(getArguments().getString("query"));

        list = (RecyclerView) convertView.findViewById(R.id.busqueda_list_animeflv); //utiliza la misma que home screen
        bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
        sinResultados = (TextView)convertView.findViewById(R.id.noResultados);
        //sinResultados.setText(R.string.noResultado_busquedaFLV_ES);
        //Log.d("query", getArguments().getString("query"));
        searchQuery = preparaQuery(getArguments().getString("query")); //prepara la query de busqueda
        //Log.d("queryLista", searchQuery);

        new BackgroundTask().execute(this); //ejecuta la busqueda via asynctask


        return convertView;
    }

    // TODO: Rename method, update argument and hook method into UI event
   /* public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(url);
        }
    }*/

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void customClickListener(View v, int position) {
        mListener.onBusquedaInteraction(animesBuscados.get(position).getUrlCapitulo());
        getActivity().setTitle(animesBuscados.get(position).getNombre());
       // Log.d("HOLA", "listener");
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
        public void onBusquedaInteraction(String url);
    }

    //Funcion que prepara la url de busqueda mediante el query de busqueda
    public String preparaQuery(String query) {
        String aux = "";
        String busqueda;
        String[] div = query.split(" "); //separa por espacios
        for (int i = 0; i < div.length; i++){
            if (i == 0) {
                aux = div[i];
            } else {
                aux = aux + "+" + div[i];
            }
        }

        busqueda = queryTemplate + aux;

        return busqueda;
    }


    private class BackgroundTask extends AsyncTask<AdBusquedaFLV.CustomRecyclerListener, Void, Void> {

        @Override
        protected Void doInBackground(AdBusquedaFLV.CustomRecyclerListener... params) {
            Animeflv anime = new Animeflv(getResources());
            try {
                animesBuscados = anime.busquedaFLV(searchQuery, getActivity());
                if (animesBuscados.isEmpty()) {
                    produceResultados = false;
                } else {
                    //  System.out.println("tamaño  " + animesBuscados.size());
                    produceResultados = true;
                    adaptador = new AdBusquedaFLV(getActivity(), animesBuscados);
                    adaptador.setClickListener(params[0]);
                }
               // Log.d("HOLA", "pase despues");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            sinResultados.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {

            if (produceResultados) {
                list.setLayoutManager(new LinearLayoutManager(getActivity()));
                list.setAdapter(adaptador);
            } else {
                sinResultados.setVisibility(View.VISIBLE);
            }

            //list.setHasFixedSize(true);
            //getActivity().setProgressBarIndeterminateVisibility(false);
            bar.setVisibility(View.GONE);
        }
    }

}
