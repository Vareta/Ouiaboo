package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.adaptadores.AdEpisodios;
import com.ouiaboo.ouiaboo.clases.Episodios;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EpisodiosFlv.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EpisodiosFlv extends android.support.v4.app.Fragment {

    private RecyclerView list;
    private ProgressBar bar;
    private AdEpisodios adaptador;
    private ArrayList<Episodios> epi;
    private OnFragmentInteractionListener mListener;
    private String url;

    public EpisodiosFlv() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_episodios, container, false);
        list = (RecyclerView)convertView.findViewById(R.id.episodios);
        bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
        url = getArguments().getString("query");

        new BackgroundTask().execute();

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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            try {
                Animeflv ani = new Animeflv(getResources());
                epi = ani.getEpisodios(url);
                adaptador = new AdEpisodios(getActivity(), epi);
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
            bar.setVisibility(View.GONE);
        }
    }

}
