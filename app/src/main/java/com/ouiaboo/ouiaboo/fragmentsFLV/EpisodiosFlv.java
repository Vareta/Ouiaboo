package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.adaptadores.AdEpisodios;
import com.ouiaboo.ouiaboo.adaptadores.AdInfoEpisodios;
import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;
import com.ouiaboo.ouiaboo.clases.Episodios;

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
    private ArrayList<Episodios> epi;
    private OnFragmentInteractionListener mListener;

    public EpisodiosFlv() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_episodios, container, false);
        list = (RecyclerView)convertView.findViewById(R.id.episodios);
        getData();
        setAdaptador();

        return convertView;
    }

    @SuppressWarnings("unchecked")
    private void getData() {
        epi = (ArrayList<Episodios>)getArguments().getSerializable("episodios");
    }

    private void setAdaptador() {
        AdEpisodios adaptador = new AdEpisodios(getContext(), epi);
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
    public void customClickListener(View v, int position) {
        System.out.println("click fragment");
        mListener.onEpisodiosFlvInteraction(epi.get(position).getUrlEpisodio());

    }

    @Override
    public void customLongClickListener(View v, int position) {

    }


    public interface OnFragmentInteractionListener {
        public void onEpisodiosFlvInteraction(String url);
    }




}
