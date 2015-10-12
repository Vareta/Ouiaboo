package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Tables.FavoritosTable;
import com.ouiaboo.ouiaboo.Tables.VerMasTardeTable;
import com.ouiaboo.ouiaboo.adaptadores.AdBusquedaFLV;
import com.ouiaboo.ouiaboo.adaptadores.AdVerMasTarde;
import com.ouiaboo.ouiaboo.clases.HomeScreenAnimeFLV;

import org.litepal.crud.DataSupport;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Favoritos.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Favoritos extends android.support.v4.app.Fragment implements AdBusquedaFLV.CustomRecyclerListener{

    private OnFragmentInteractionListener mListener;
    private RecyclerView lista;
    private ProgressBar bar;
    private ArrayList<HomeScreenAnimeFLV> animeFavoritos;
    private TextView noFavoritos;
    private boolean existenFavoritos;
    private AdBusquedaFLV adaptador;
    private CoordinatorLayout coordinatorLayout;


    public Favoritos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_favoritos, container, false);
        coordinatorLayout = (CoordinatorLayout)convertView.findViewById(R.id.coordinator_layout);
        getActivity().setTitle(R.string.favoritos_drawer_layout);

        lista = (RecyclerView)convertView.findViewById(R.id.favoritos_recyclerview);
        bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
        noFavoritos = (TextView)convertView.findViewById(R.id.noFavoritos);
        animeFavoritos = new ArrayList<HomeScreenAnimeFLV>();
        new BackgroundTask().execute(this);

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
        mListener.onFavoritoInteraction(animeFavoritos.get(position).getUrlCapitulo());  //En este caso urlCapitulo, contiene la url del anime
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
        public void onFavoritoInteraction(String url);
    }

    private class BackgroundTask extends AsyncTask<AdBusquedaFLV.CustomRecyclerListener, Void, Void> {

        @Override
        protected Void doInBackground(AdBusquedaFLV.CustomRecyclerListener... params) {
            try {
                List<FavoritosTable> datos = DataSupport.findAll(FavoritosTable.class);
                if (datos.isEmpty()) {
                    existenFavoritos = false;
                } else {
                    existenFavoritos = true;
                    for (int i = 0; i < datos.size(); i++) {
                        animeFavoritos.add(new HomeScreenAnimeFLV(datos.get(i).getUrlAnime(), datos.get(i).getNombre(), datos.get(i).getTipo(), datos.get(i).getUrlImagen()));
                    }

                    adaptador = new AdBusquedaFLV(getActivity(), animeFavoritos);
                    adaptador.setClickListener(params[0]);
                }

            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            noFavoritos.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);
            // Log.d("HOLA", "PREEXECUTE 333");
        }

        @Override
        protected void onPostExecute(Void result) {

            if (!existenFavoritos) {
                noFavoritos.setVisibility(View.VISIBLE);
            } else {
                lista.setLayoutManager(new LinearLayoutManager(getActivity()));
                lista.setAdapter(adaptador);

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallBack);
                itemTouchHelper.attachToRecyclerView(lista); //añade la lista a la escucha


            }
            bar.setVisibility(View.GONE);
            //
        }
    }

    /*opcion deslizante para eliminar un item*/
    ItemTouchHelper.SimpleCallback simpleItemTouchCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition(); //obtiene la posicion
            final HomeScreenAnimeFLV aux = animeFavoritos.get(position); //guarda el elemento al cual se le hizo swipe
            animeFavoritos.remove(position); //remueve de la lista de capitulos en memoria
            adaptador.notifyItemRemoved(position); //notifica al adaptador que un item fue removido
            Snackbar snackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.animeBorrado_verMasTarde), Snackbar.LENGTH_LONG) //muestra la snackbar para informar al usuario
                    .setAction(getResources().getString(R.string.deshacer_verMasTarde), new View.OnClickListener() { //setea la accion de restaurar
                        @Override
                        public void onClick(View v) { //en caso de restaurar
                            animeFavoritos.add(position, aux); //añade el elemnto que se sacó y lo coloca en la misma posicion de antes
                            adaptador.notifyItemInserted(position); //notifica al adaptador que se añadio un item
                        }
                    }).setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) { //para cuando la snackbar desapara, se toman todos los casos en donde esto puede ocurrir
                            super.onDismissed(snackbar, event);
                            if (event != DISMISS_EVENT_ACTION) { //excluye la accion ya que choca con onclick, haciendo que al clickear undo se ejecutara esta accion
                                DataSupport.deleteAll(FavoritosTable.class, "nombre =? and tipo =?", aux.getNombre(), aux.getInformacion());
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

                if (dX > 0) { // swiping right
                    paint.setColor(ContextCompat.getColor(getContext(), R.color.black_overlay));
                    bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.ic_launcher);
                    float height = (itemView.getHeight() / 2) - (bitmap.getHeight() / 2);

                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), paint);
                    c.drawBitmap(bitmap, 96f, (float) itemView.getTop() + height, null);

                } else { // swiping left
                    paint.setColor(ContextCompat.getColor(getContext(), R.color.ColorPrimary));

                    bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.ic_launcher);
                    float height = (itemView.getHeight() / 2) - (bitmap.getHeight() / 2);
                    float bitmapWidth = bitmap.getWidth();

                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                    c.drawBitmap(bitmap, ((float) itemView.getRight() - bitmapWidth) - 96f, (float) itemView.getTop() + height, null);

                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);




            }
        }
    };

}
