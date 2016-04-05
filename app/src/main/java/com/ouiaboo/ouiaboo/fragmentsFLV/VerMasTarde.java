package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;
import com.ouiaboo.ouiaboo.AnalyticsApplication;
import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.EpisodiosPlusInfo;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Reyanime;
import com.ouiaboo.ouiaboo.Tables.animeflv.VerMasTardeTable;
import com.ouiaboo.ouiaboo.Tables.reyanime.VerMasTardeTableRey;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.adaptadores.AdContMenuCentral;
import com.ouiaboo.ouiaboo.adaptadores.AdVerMasTarde;
import com.ouiaboo.ouiaboo.clases.DrawerItemsListUno;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import org.jsoup.nodes.Document;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VerMasTarde.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class VerMasTarde extends android.support.v4.app.Fragment implements AdVerMasTarde.CustomRecyclerListener{
    private RecyclerView lista;
    private ProgressBar bar;
    private List<HomeScreenEpi> masTardeAnime;
    private AdVerMasTarde adaptador;
    private Boolean existeAnimeMastarde = null;
    private CoordinatorLayout coordinatorLayout;
    private TextView sinResultados;
    private OnFragmentInteractionListener mListener;

    public VerMasTarde() {
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
        View convertView = inflater.inflate(R.layout.fragment_ver_mas_tarde, container, false);
        getActivity().setTitle(R.string.mas_tarde_drawer_layout);
        iniciaView(convertView);
        iniciaFragment();

        return convertView;
    }

    private void iniciaView(View convertView) {
        coordinatorLayout = (CoordinatorLayout)convertView.findViewById(R.id.coordinator_layout);
        lista = (RecyclerView)convertView.findViewById(R.id.ver_mas_tarde_recyclerview);
        lista.setLayoutManager(new LinearLayoutManager(getActivity()));
        bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
        sinResultados = (TextView)convertView.findViewById(R.id.noResultados);
    }

    public void iniciaFragment() {
        if (getMasTardeAnime() == null && existeAnimeMastarde == null) { //primera vez que inicia el fragment
            new ListarAnimeMasTarde().execute(this);
        } else { //inicia desde un punto guardado
            if (!existeAnimeMastarde) {
                sinResultados.setVisibility(View.VISIBLE);
            } else {
                adaptador = new AdVerMasTarde(getActivity(), masTardeAnime);
                adaptador.setClickListener(this);
                lista.setAdapter(adaptador);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallBack);
                itemTouchHelper.attachToRecyclerView(lista); //añade la lista a la escucha
            }
        }
    }

    /*opcion deslizante para eliminar un item*/
    ItemTouchHelper.SimpleCallback simpleItemTouchCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition(); //obtiene la posicion
            final HomeScreenEpi aux = masTardeAnime.get(position); //guarda el elemento al cual se le hizo swipe
            masTardeAnime.remove(position); //remueve de la lista de capitulos en memoria
            adaptador.notifyItemRemoved(position); //notifica al adaptador que un item fue removido
            Snackbar snackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.animeBorrado_verMasTarde), Snackbar.LENGTH_LONG) //muestra la snackbar para informar al usuario
                    .setAction(getResources().getString(R.string.deshacer_verMasTarde), new View.OnClickListener() { //setea la accion de restaurar
                        @Override
                        public void onClick(View v) { //en caso de restaurar
                            masTardeAnime.add(position, aux); //añade el elemnto que se sacó y lo coloca en la misma posicion de antes
                            adaptador.notifyItemInserted(position); //notifica al adaptador que se añadio un item
                        }
                    }).setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) { //para cuando la snackbar desapara, se toman todos los casos en donde esto puede ocurrir
                            super.onDismissed(snackbar, event);
                            if (event != DISMISS_EVENT_ACTION && event != DISMISS_EVENT_MANUAL) { //excluye la accion ya que choca con onclick, haciendo que al clickear undo se ejecutara esta accion
                                Utilities util = new Utilities();
                                if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                                    DataSupport.deleteAll(VerMasTardeTable.class, "nombre=? and tipo=?", aux.getNombre(), aux.getInformacion());
                                } else { //reyanime
                                    DataSupport.deleteAll(VerMasTardeTableRey.class, "nombre=? and tipo=?", aux.getNombre(), aux.getInformacion());
                                }
                            }
                        }
                    });

            snackbar.show();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;

                Paint paint = new Paint();
                Bitmap bitmap;

                if (dX < 0) {  // swiping left
                    paint.setColor(ContextCompat.getColor(getContext(), R.color.ColorPrimaryDark));

                    bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_delete_white_36dp);
                    float height = (itemView.getHeight() / 2) - (bitmap.getHeight() / 2);
                    float bitmapWidth = bitmap.getWidth();

                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                    c.drawBitmap(bitmap, ((float) itemView.getRight() - bitmapWidth) - 96f, (float) itemView.getTop() + height, null);

                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        AnalyticsApplication.getInstance().trackScreenView("Ver mas tarde");
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
        setData(masTardeAnime, existeAnimeMastarde);
        mListener = null;
    }

    @Override
    public void customClickListener(View v, int position) {
        HomeScreenEpi objEpi = masTardeAnime.get(position);
        mListener.onVerMasTardeInteraction(objEpi);
    }

    @Override
    public void customLongClickListener(View v, int position) {
        final int posAnime = position; //para diferenciar el onclick del listpopup
        Utilities util = new Utilities();

        List<DrawerItemsListUno> items = new ArrayList<>();
        items.add(new DrawerItemsListUno(getString(R.string.irAnime_PopupWindow), R.drawable.ic_forward_white_24dp));

        AdContMenuCentral adapter = new AdContMenuCentral(getActivity(), items);

        final ListPopupWindow listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setAdapter(adapter);

        listPopupWindow.setAnchorView(v.findViewById(R.id.nombre_flv));
        int width = util.measureContentWidth(adapter, this);
        listPopupWindow.setWidth(width);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    new EpiUrlToAnimeUrl().execute(masTardeAnime.get(posAnime).getUrlCapitulo());
                    listPopupWindow.dismiss();
                }
            }
        });
        listPopupWindow.setModal(true);
        listPopupWindow.setHorizontalOffset(0);
        listPopupWindow.show();
    }



    public interface OnFragmentInteractionListener {
        public void onVerMasTardeInteraction(HomeScreenEpi objEpi);
    }

    private class ListarAnimeMasTarde extends AsyncTask<AdVerMasTarde.CustomRecyclerListener, Void, Void> {

        @Override
        protected Void doInBackground(AdVerMasTarde.CustomRecyclerListener... params) {
            Utilities util = new Utilities();
            try {

                masTardeAnime = new ArrayList<>();
                if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                    List<VerMasTardeTable> datos = DataSupport.findAll(VerMasTardeTable.class);
                    if (datos.isEmpty()) {
                        existeAnimeMastarde = false;
                    } else {
                        existeAnimeMastarde = true;
                        for (int i = 0; i < datos.size(); i++) {
                            masTardeAnime.add(new HomeScreenEpi(datos.get(i).getUrlCapitulo(), datos.get(i).getNombre(), datos.get(i).getTipo(), datos.get(i).getUrlImagen()));
                        }

                        adaptador = new AdVerMasTarde(getActivity(), masTardeAnime);
                        adaptador.setClickListener(params[0]);
                    }
                } else { //reyanime
                    List<VerMasTardeTableRey> datos = DataSupport.findAll(VerMasTardeTableRey.class);
                    if (datos.isEmpty()) {
                        existeAnimeMastarde = false;
                    } else {
                        existeAnimeMastarde = true;
                        for (int i = 0; i < datos.size(); i++) {
                            masTardeAnime.add(new HomeScreenEpi(datos.get(i).getUrlCapitulo(), datos.get(i).getNombre(), datos.get(i).getTipo(), datos.get(i).getUrlImagen()));
                        }

                        adaptador = new AdVerMasTarde(getActivity(), masTardeAnime);
                        adaptador.setClickListener(params[0]);
                    }
                }

            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            sinResultados.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);
            // Log.d("HOLA", "PREEXECUTE 333");
        }

        @Override
        protected void onPostExecute(Void result) {

            if (!existeAnimeMastarde) {
                sinResultados.setVisibility(View.VISIBLE);
                lista.setVisibility(View.GONE);
            } else {
                lista.setAdapter(adaptador);

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallBack);
                itemTouchHelper.attachToRecyclerView(lista); //añade la lista a la escucha


            }
            bar.setVisibility(View.GONE);
            //
        }
    }

    /*Obtiene la url de un anime mediante el url del capitulo de manera asincrona*/
    private class EpiUrlToAnimeUrl extends AsyncTask<String, Void, Void> {
        private String url;

        @Override
        protected Void doInBackground(String... params) {
            Utilities util = new Utilities();

            Document codigoFuente = util.connect(params[0]);
            if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                Animeflv animeflv = new Animeflv();
                url = animeflv.urlCapituloToUrlAnime(codigoFuente);
            } else {
                Reyanime reyanime = new Reyanime();
                url = reyanime.urlCapituloToUrlAnime(codigoFuente);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            lista.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            lista.setVisibility(View.VISIBLE);
            bar.setVisibility(View.GONE);
            Intent intent = new Intent(getActivity(), EpisodiosPlusInfo.class);
            intent.putExtra("url", url);
            startActivity(intent);
        }
    }

    public void setData(List<HomeScreenEpi> masTardeAnime, boolean existeAnimeMastarde) {
        this.masTardeAnime = masTardeAnime;
        this.existeAnimeMastarde = existeAnimeMastarde;
    }

    public List<HomeScreenEpi> getMasTardeAnime() {
        return masTardeAnime;
    }

}
