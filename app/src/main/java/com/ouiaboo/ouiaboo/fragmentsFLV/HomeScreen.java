package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.VideoPlayer;
import com.ouiaboo.ouiaboo.adaptadores.AdaptadorHomeScreenAnimeFLV;
import com.ouiaboo.ouiaboo.clases.HomeScreenAnimeFLV;

import java.util.ArrayList;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeScreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeScreen extends android.support.v4.app.Fragment implements AdaptadorHomeScreenAnimeFLV.CustomRecyclerListener{
    FragmentActivity mActivity;
    private final String animeFLV = "http://animeflv.net/";
    private OnFragmentInteractionListener mListener;
    private AdaptadorHomeScreenAnimeFLV adaptador;
    private RecyclerView list;
    private ProgressBar bar;
    private ArrayList<HomeScreenAnimeFLV> animesRecientes;
    private Animeflv animes;
    private Utilities util;
    private List<String> codigoFuente;


    public HomeScreen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView =  inflater.inflate(R.layout.fragment_home_screen, container, false);

        list = (RecyclerView)convertView.findViewById(R.id.home_screen_list_animeflv); //lista fragment
        bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);

        new BackgroundTask().execute(this);

        return convertView;
    }

    @Override
    public void customClickListener(View v, int position) {
        Intent intent = new Intent(getActivity(), VideoPlayer.class);
        intent.putExtra("url", animesRecientes.get(position).getUrlCapitulo());
        startActivity(intent);
    }

    private class BackgroundTask extends AsyncTask<AdaptadorHomeScreenAnimeFLV.CustomRecyclerListener, Void, Void> {

        @Override
        protected Void doInBackground(AdaptadorHomeScreenAnimeFLV.CustomRecyclerListener... params) {
            try {
                animes = new Animeflv(getResources());
                util = new Utilities();
                codigoFuente = util.downloadWebPageTaskNoAsync(animeFLV);
                animesRecientes = animes.homeScreenAnimeflv(codigoFuente);
               /* for (int i = 0; i < animesRecientes.size(); i++){

                    Log.d("Url", animesRecientes.get(i).getUrlCapitulo());
                    Log.d("Nombre", animesRecientes.get(i).getNombre());
                    Log.d("Informacion", animesRecientes.get(i).getInformacion());
                    Log.d("Preview", animesRecientes.get(i).getPreview());
                }*/

                adaptador = new AdaptadorHomeScreenAnimeFLV(getActivity(), animesRecientes);
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
            //Log.d("HOLA", "POSTEXECUTE33333");
            //getActivity().setProgressBarIndeterminateVisibility(false);
            list.setLayoutManager(new LinearLayoutManager(getActivity()));
            list.setAdapter(adaptador);
            //list.setHasFixedSize(true);
            //getActivity().setProgressBarIndeterminateVisibility(false);
            bar.setVisibility(View.GONE);
            //
        }
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
