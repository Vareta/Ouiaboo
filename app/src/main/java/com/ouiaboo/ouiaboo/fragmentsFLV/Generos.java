package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.Animejoy;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.adaptadores.AdEpisodios;
import com.ouiaboo.ouiaboo.adaptadores.AdGeneros;
import com.ouiaboo.ouiaboo.adaptadores.AdHomeScreen;
import com.ouiaboo.ouiaboo.clases.Episodios;
import com.ouiaboo.ouiaboo.clases.GenerosClass;
import com.squareup.picasso.Picasso;

import org.jsoup.nodes.Document;

import java.io.File;
import java.util.ArrayList;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_generos, container, false);
        getActivity().setTitle(R.string.generos_drawer_layout);
        list = (RecyclerView)convertView.findViewById(R.id.generos);
        bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);

        new obtenerGeneros().execute(this);
        return convertView;
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
        mListener = null;
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
                Log.d("PROVEEDOR", String.valueOf(util.queProveedorEs(getContext())));
                if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                    Animeflv flvAnime = new Animeflv();
                    codigoFuente = util.connect("http://animeflv.net/animes/");
                    generos = flvAnime.generosDisponibles(codigoFuente);
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

}
