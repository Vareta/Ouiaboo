package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.ouiaboo.ouiaboo.Tables.DescargadosTable;
import com.ouiaboo.ouiaboo.adaptadores.AdDescargadas;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Descargadas.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Descargadas extends android.support.v4.app.Fragment implements AdDescargadas.CustomRecyclerListener {

    private OnFragmentInteractionListener mListener;
    private ProgressBar bar;
    private ArrayList<HomeScreenEpi> animeDescargado;
    private TextView sinDescargados;
    private RecyclerView list;
    private boolean existenDescargados;
    private AdDescargadas adaptador;
    private CoordinatorLayout coordinatorLayout;


    public Descargadas() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_descargadas, container, false);
        getActivity().setTitle(R.string.descargadas_drawer_layout);

        list = (RecyclerView) convertView.findViewById(R.id.descargados_recyclerview);
        sinDescargados = (TextView) convertView.findViewById(R.id.noDescargados);
        bar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        coordinatorLayout = (CoordinatorLayout)convertView.findViewById(R.id.coord_layout);

        new listarDescargas().execute(this);
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

    }

    @Override
    public void customLongClickListener(View v, int position) {

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

    private class listarDescargas extends AsyncTask<AdDescargadas.CustomRecyclerListener, Void, Void> {

        @Override
        protected Void doInBackground(AdDescargadas.CustomRecyclerListener... params) {
            try {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Ouiaboo"; //direccion de donde se encuentran los capitulos
                File carpetaDescargados = new File(path);
                List<DescargadosTable> completas = DataSupport.where("complete=?", String.valueOf(1)).find(DescargadosTable.class);
                if (!completas.isEmpty()) {
                    existenDescargados = true;
                    animeDescargado = new ArrayList<>();
                    // setComplete(archivos); //busca si hay archivos y los marca como completos
                    File thumbnailCarpeta = getActivity().getApplicationContext().getDir("imgThumbnail", Context.MODE_PRIVATE); //direccion de donde se guardaran thumbnails
                    for (int i = 0; i < completas.size(); i++) {
                        if (completas.get(i).isComplete() && completas.get(i).getImagenPreview() == null) {//comprueba si existen capitulos y que no tengan preview
                            Bitmap preview = ThumbnailUtils.createVideoThumbnail(completas.get(i).getDirVideo(), MediaStore.Images.Thumbnails.MINI_KIND);
                            File nombreImg = new File(thumbnailCarpeta, completas.get(i).getNombre());
                            FileOutputStream fos;
                            fos = new FileOutputStream(nombreImg + ".jpg");
                            preview.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();

                            DescargadosTable descargadosTable = new DescargadosTable();
                            descargadosTable.setImagenPreview(nombreImg.getAbsolutePath() + ".jpg");
                            descargadosTable.updateAll("urlCapitulo=?", completas.get(i).getUrlCapitulo());
                        }
                    }

                    List<DescargadosTable> enDisco = DataSupport.where("complete=?", String.valueOf(1)).find(DescargadosTable.class);
                    HomeScreenEpi objeto;
                    for (int j = 0; j < enDisco.size(); j++) {
                        objeto = new HomeScreenEpi(enDisco.get(j).getUrlCapitulo(), enDisco.get(j).getNombre(), enDisco.get(j).getTipo(), enDisco.get(j).getImagenPreview());
                        animeDescargado.add(objeto);
                    }

                    adaptador = new AdDescargadas(getActivity(), animeDescargado);
                    adaptador.setClickListener(params[0]);

                } else {
                    existenDescargados = false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
            sinDescargados.setVisibility(View.GONE);
            // Log.d("HOLA", "PREEXECUTE 333");
        }

        @Override
        protected void onPostExecute(Void result) {
            if (existenDescargados) {
                list.setLayoutManager(new LinearLayoutManager(getActivity()));
                list.setAdapter(adaptador);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallBack);
                itemTouchHelper.attachToRecyclerView(list); //añade la lista a la escucha
            } else {
                sinDescargados.setVisibility(View.VISIBLE);
            }
            bar.setVisibility(View.GONE);
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
            final HomeScreenEpi aux = animeDescargado.get(position); //guarda el elemento al cual se le hizo swipe
            animeDescargado.remove(position); //remueve de la lista de capitulos en memoria
            adaptador.notifyItemRemoved(position); //notifica al adaptador que un item fue removido
            Snackbar snackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.animeBorrado_verMasTarde), Snackbar.LENGTH_LONG) //muestra la snackbar para informar al usuario
                    .setAction(getResources().getString(R.string.deshacer_verMasTarde), new View.OnClickListener() { //setea la accion de restaurar
                        @Override
                        public void onClick(View v) { //en caso de restaurar
                            animeDescargado.add(position, aux); //añade el elemnto que se sacó y lo coloca en la misma posicion de antes
                            adaptador.notifyItemInserted(position); //notifica al adaptador que se añadio un item
                        }
                    }).setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) { //para cuando la snackbar desaparece, se toman todos los casos en donde esto puede ocurrir
                            super.onDismissed(snackbar, event);
                            if (event != DISMISS_EVENT_ACTION) { //excluye la accion ya que choca con onclick, haciendo que al clickear undo se ejecutara esta accion
                                List<DescargadosTable> aBorrar = DataSupport.where("imagenPreview=?", aux.getPreview()).find(DescargadosTable.class); //busca el video a borrar en la bd
                                if (!aBorrar.isEmpty()) {
                                    deleteVideo(aBorrar.get(0).getDirVideo()); //borra el video
                                    File img = new File(aBorrar.get(0).getImagenPreview());
                                    img.delete();
                                    DataSupport.deleteAll(DescargadosTable.class, "imagenPreview=?", aux.getPreview());
                                } else {
                                    Log.d("Descargadas", "elemento a borrar de las descargas no encontrado");
                                }
                                //DataSupport.deleteAll(FavoritosTable.class, "nombre =? and tipo =?", aux.getNombre(), aux.getInformacion());
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


    /*from http://stackoverflow.com/questions/10716642/android-deleting-an-image/20780472#20780472 */
    public void deleteVideo(String path) {

        // Set up the projection (we only need the ID)
        String[] projection = { MediaStore.Video.Media._ID };

        // Match on the file path
        String selection = MediaStore.Video.Media.DATA + " = ?";
        String[] selectionArgs = new String[] { path };

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
        } else {
            // File not found in media store DB
        }
        c.close();
    }

}
