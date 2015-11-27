package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ouiaboo.ouiaboo.AnalyticsApplication;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.clases.Episodios;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AnimeInfo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AnimeInfo extends  android.support.v4.app.Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<Episodios> informacion;
    private Tracker mTracker;

    public AnimeInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_anime_info, container, false);


        adquiereInfo();
        asignaInfo(convertView);
        return convertView;
    }
    @SuppressWarnings("unchecked")
    private void adquiereInfo() {
        informacion = (ArrayList<Episodios>)getArguments().getSerializable("informacion");
    }

    private void asignaInfo(View view) {
        TextView tipoData = (TextView)view.findViewById(R.id.tipo_data);
        TextView estadoData = (TextView)view.findViewById(R.id.estado_data);
        TextView generosData = (TextView)view.findViewById(R.id.generos_data);
        TextView fechaData = (TextView)view.findViewById(R.id.fecha_inicio_data);
        TextView sinopsisData = (TextView)view.findViewById(R.id.sinopsis_data);

        tipoData.setText(informacion.get(0).getTipo());
        estadoData.setText(informacion.get(0).getEstado());
        generosData.setText(informacion.get(0).getGeneros());
        fechaData.setText(informacion.get(0).getFechaInicio());
        sinopsisData.setText(informacion.get(0).getInformacion());
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            AnalyticsApplication.getInstance().trackScreenView("Anime info");
        }
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

}
