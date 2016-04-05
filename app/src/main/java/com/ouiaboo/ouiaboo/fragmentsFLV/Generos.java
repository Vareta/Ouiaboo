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

import com.ouiaboo.ouiaboo.AnalyticsApplication;
import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Reyanime;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.adaptadores.AdGeneros;
import com.ouiaboo.ouiaboo.clases.GenerosClass;

import org.jsoup.nodes.Document;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Generos.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Generos extends android.support.v4.app.Fragment implements AdGeneros.CustomRecyclerListener{

    private OnFragmentInteractionListener mListener;
    private RecyclerView list;
    private ProgressBar bar;
    private List<GenerosClass> generos;
    private AdGeneros adaptador;

    public Generos() {
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
        View convertView = inflater.inflate(R.layout.fragment_generos, container, false);
        getActivity().setTitle(R.string.generos_drawer_layout);
        iniciarView(convertView);
        iniciarFragment();

        return convertView;
    }

    private void iniciarView(View convertView) {
        list = (RecyclerView)convertView.findViewById(R.id.generos);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
    }

    private void iniciarFragment() {
        if (getGeneros() == null) {
            new obtenerGeneros().execute(this);
        } else {
            adaptador = new AdGeneros(getActivity(), generos);
            adaptador.setClickListener(this);
            list.setAdapter(adaptador);
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
    public void onDetach() {
        super.onDetach();
        setData(generos);
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsApplication.getInstance().trackScreenView("Generos");
    }

    @Override
    public void customClickListener(View v, int position) {
        mListener.onGenerosInteraction(generos.get(position));
    }

    public interface OnFragmentInteractionListener {
        public void onGenerosInteraction(GenerosClass objGeneros);
    }


    private class obtenerGeneros extends AsyncTask<AdGeneros.CustomRecyclerListener, Void, Void> {

        @Override
        protected Void doInBackground(AdGeneros.CustomRecyclerListener... params) {
            try {
                Utilities util = new Utilities();
                Document codigoFuente;
                if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                    Animeflv animeflv = new Animeflv();
                    codigoFuente = util.connect("http://animeflv.net/animes/");
                    generos = animeflv.generosDisponibles(codigoFuente);
                } else {
                    Reyanime reyanime = new Reyanime();
                    codigoFuente = util.connect("http://reyanime.com/genero/accion");
                    generos = reyanime.generosDisponibles(codigoFuente);
                }
               /* for (int i = 0; i < animesRecientes.size(); i++){

                    Log.d("Url", animesRecientes.get(i).getUrlCapitulo());
                    Log.d("Nombre", animesRecientes.get(i).getNombre());
                    Log.d("Informacion", animesRecientes.get(i).getInformacion());
                    Log.d("Preview", animesRecientes.get(i).getPreview());
                }*/

                adaptador = new AdGeneros(getActivity(), generos);
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
        }
    }

    public void setData(List<GenerosClass> generos) {
        this.generos = generos;
    }

    public List<GenerosClass> getGeneros() {
        return generos;
    }
}
