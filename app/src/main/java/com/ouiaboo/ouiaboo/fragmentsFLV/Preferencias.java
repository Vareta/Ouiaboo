package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.ouiaboo.ouiaboo.AnalyticsApplication;
import com.ouiaboo.ouiaboo.Funciones;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Preferencias.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Preferencias extends PreferenceFragmentCompat {

    private OnFragmentInteractionListener mListener;
    private Preference preferencias;
    public Preferencias() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //hace que el fragment se conserve
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        getPreferenceManager().setSharedPreferencesName(Utilities.PREFERENCIAS);
        addPreferencesFromResource(R.xml.preferences);
        getActivity().setTitle(R.string.preferencias_drawer_layout);
        tipoActualizacionListener();
        //proveedorListener();
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
        AnalyticsApplication.getInstance().trackScreenView("Preferencias");
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


    private void tipoActualizacionListener() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Utilities.PREFERENCIAS, Context.MODE_PRIVATE);
        String opcActual = sharedPref.getString("tipoUpdate", "enlaces");
        final Preference tipoActuali = getPreferenceManager().findPreference("tipoUpdate");

        if (opcActual.equals("enlaces")) { //enlaces externos
            tipoActuali.setSummary(R.string.tipoActualiEnlaces_Settings);
        } else { //automatico
            tipoActuali.setSummary(R.string.tipoActualiAuto_Settings);
        }

        tipoActuali.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                if (value.equals("enlaces")) { //enlaces externos
                    tipoActuali.setSummary(R.string.tipoActualiEnlaces_Settings);
                } else { //automatico
                    tipoActuali.setSummary(R.string.tipoActualiAuto_Settings);
                }
                return true;
            }
        });
    }

    /*private void proveedorListener() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Utilities.PREFERENCIAS, Context.MODE_PRIVATE);
        final String opcActualProveedor = sharedPref.getString("listaProveedores", "animeflv");
        final Preference tipoProveedor = getPreferenceManager().findPreference("listaProveedores");

        //Muestra la preferencia actual al momento en que se abre el fragmento de preferencias
        if (opcActualProveedor.equals("animeflv")) { //animeflv
            tipoProveedor.setSummary(R.string.proveedorAnimeflv_Settings);
        } else { //reyanime
            tipoProveedor.setSummary(R.string.proveedorReyanime_Settings);
        }
        tipoProveedor.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(Utilities.PREFERENCIAS, Context.MODE_PRIVATE).edit();
                if (value.equals("animeflv")) { //animeflv
                    tipoProveedor.setSummary(R.string.proveedorAnimeflv_Settings);
                    if (!opcActualProveedor.equals("animeflv")) { //quiere decir que el proveedor era otra pagina y no animeflv
                        editor.putBoolean("proveedorModificado", true);
                        AnalyticsApplication.getInstance().trackEvent("Página", "cambió", "animeflv");
                    }
                    editor.putBoolean("animeflv", true);
                    editor.putBoolean("reyanime", false);
                    editor.putString("listaProveedores", "animeflv");

                } else { //reyanime
                    tipoProveedor.setSummary(R.string.proveedorReyanime_Settings);
                    if (!opcActualProveedor.equals("reyanime")) { //quiere decir que el proveedor era otra pagina y no reyanime
                        editor.putBoolean("proveedorModificado", true);
                        AnalyticsApplication.getInstance().trackEvent("Página", "cambió", "reyanime");
                    }
                    editor.putBoolean("animeflv", false);
                    editor.putBoolean("reyanime", true);
                    editor.putString("listaProveedores", "reyanime");
                }
                editor.apply();
                Funciones funciones = new Funciones();
                funciones.actualizarProveedorNavHeader(getActivity(), getContext());

                return true;
            }
        });

    }*/



}
