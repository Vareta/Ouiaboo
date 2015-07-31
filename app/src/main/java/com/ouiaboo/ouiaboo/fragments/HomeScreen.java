package com.ouiaboo.ouiaboo.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.adaptadores.AdaptadorHomeScreenAnimeFLV;
import com.ouiaboo.ouiaboo.clases.HomeScreenAnimeFLV;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeScreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeScreen extends Fragment {
    private final String animeFLV = "http://animeflv.net/";
    private OnFragmentInteractionListener mListener;
    ArrayAdapter adaptador;

    public HomeScreen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView =  inflater.inflate(R.layout.fragment_home_screen, container, false);
        ListView list = (ListView)convertView.findViewById(R.id.home_screen_list_animeflv); //lista fragment

        ArrayList<HomeScreenAnimeFLV> animesRecientes;
        Animeflv animes = new Animeflv();
        Utilities.DownloadWebPageTask task = new Utilities.DownloadWebPageTask();
        task.execute(new String[]{animeFLV});
        List<String> codigoFuente = null;
        try {
            codigoFuente = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        animesRecientes = animes.homeScreenAnimeflv(codigoFuente);
       /* for (int i = 0; i < animesRecientes.size(); i++){

            Log.d("Url", animesRecientes.get(i).getUrlCapitulo());
            Log.d("Nombre", animesRecientes.get(i).getNombre());
            Log.d("Informacion", animesRecientes.get(i).getInformacion());
            Log.d("Preview", animesRecientes.get(i).getPreview());
        }*/
        adaptador = new AdaptadorHomeScreenAnimeFLV(getActivity(), animesRecientes);
        list.setAdapter(adaptador);
        return convertView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
