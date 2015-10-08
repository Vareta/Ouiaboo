package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.Funciones;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.VideoPlayer;
import com.ouiaboo.ouiaboo.adaptadores.AdContMenuCentral;
import com.ouiaboo.ouiaboo.adaptadores.AdHomeScreen;
import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;
import com.ouiaboo.ouiaboo.clases.HomeScreenAnimeFLV;

import java.util.ArrayList;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeScreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeScreen extends android.support.v4.app.Fragment implements AdHomeScreen.CustomRecyclerListener{
    private final String animeFLV = "http://animeflv.net/";
    private OnFragmentInteractionListener mListener;
    private AdHomeScreen adaptador;
    private RecyclerView list;
    private ProgressBar bar;
    private ArrayList<HomeScreenAnimeFLV> animesRecientes;
    private Animeflv animes;
    private Utilities util;
    private List<String> codigoFuente;
    private ImageView icono;
    private TextView nombre;
    private static final int VERTICAL_ITEM_SPACE = 48;


    public HomeScreen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView =  inflater.inflate(R.layout.fragment_home_screen, container, false);
        getActivity().setTitle(R.string.inicio_drawer_layout);
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

    @Override
    public void customLongClickListener(View v, int position) {

        final int posAnime = position; //para diferenciar el onclick del listpopup
       // LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       // View popUpView = inflater.inflate(R.layout.context_menu, null);
        List<DrawerItemsListUno> items = new ArrayList<>();
        items.add( new DrawerItemsListUno( "Favorito", R.drawable.ic_action_globe ) );
        items.add( new DrawerItemsListUno( "Favorito2", R.drawable.ic_action_globe ) );
        items.add(new DrawerItemsListUno("Favorito3", R.drawable.ic_action_globe));
        items.add(new DrawerItemsListUno("Favorito4", R.drawable.ic_action_globe));

        AdContMenuCentral adapter = new AdContMenuCentral(getActivity(), items);

        final ListPopupWindow listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setAdapter(adapter);

        listPopupWindow.setAnchorView(v);
        int width = measureContentWidth(adapter);
        listPopupWindow.setWidth(width);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(mContext, getAdapterPosition() + " : " + position, Toast.LENGTH_SHORT).show();

                    /*Intent intent = new Intent(mContext, CarActivity.class);
                    intent.putExtra("car", mList.get( getAdapterPosition() ));
                    mContext.startActivity(intent);*/

                if (position == 3) {
                    Funciones fun = new Funciones();
                    if (!fun.verMasTardeHome(animesRecientes.get(posAnime))) { //no se pudo
                        Toast.makeText(getActivity(), getString(R.string.noti_vermastarde_no), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.noti_vermastarde_si), Toast.LENGTH_SHORT).show();
                    }
                    Log.d("CONTEXT", "hola");
                    listPopupWindow.dismiss();
                }
            }
        });
        listPopupWindow.setModal(true);
        listPopupWindow.setHorizontalOffset(0);
        listPopupWindow.show();
    }

    public int measureContentWidth(ListAdapter adapter) {
        int maxWidth = 0;
        int count = adapter.getCount();
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        View itemView = null;
        for (int i = 0; i < count; i++) {
            itemView = adapter.getView(i, itemView, ((ViewGroup)getView().getParent()));
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            maxWidth = Math.max(maxWidth, itemView.getMeasuredWidth());
        }
        return maxWidth;
    }

    private class BackgroundTask extends AsyncTask<AdHomeScreen.CustomRecyclerListener, Void, Void> {

        @Override
        protected Void doInBackground(AdHomeScreen.CustomRecyclerListener... params) {
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

                adaptador = new AdHomeScreen(getActivity(), animesRecientes);
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
            //list.addItemDecoration(new DividerItemDeco(getActivity(), R.drawable.dvider_recycler_view));
            //list.addItemDecoration(new VerticalItemDeco(VERTICAL_ITEM_SPACE));
            list.setAdapter(adaptador);
            //list.setHasFixedSize(true);
            //getActivity().setProgressBarIndeterminateVisibility(false);
            bar.setVisibility(View.GONE);
            //
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            getActivity().setTitle(R.string.inicio_drawer_layout);
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
