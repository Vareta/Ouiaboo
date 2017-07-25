package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.AnalyticsApplication;
import com.ouiaboo.ouiaboo.Animeflv;
import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.Reyanime;
import com.ouiaboo.ouiaboo.Utilities;
import com.ouiaboo.ouiaboo.adaptadores.AdGenerosEndless;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;

import org.jsoup.nodes.Document;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Busqueda.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Busqueda extends android.support.v4.app.Fragment implements AdGenerosEndless.CustomRecyclerListener{

    public OnFragmentInteractionListener mListener;
    private RecyclerView list;
    private ProgressBar bar;
    private String searchQuery;
    private String queryTemplateFlv = "http://animeflv.net/browse?q=";
    private String queryTemplateRey = "http://reyanime.com/anime/?title=";
    private List<HomeScreenEpi> animesBuscados;
    private List<HomeScreenEpi> animesBuscadosSiguiente;
    private AdGenerosEndless adaptador;
    private Boolean produceResultados;
    private TextView sinResultados;
    private String urlSiguiente;
    private boolean tienePaginaSiguiente;

    public Busqueda() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //hace que el fragment se conserve
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_busqueda, container, false);
        getActivity().setTitle(getArguments().getString("query"));
        iniciaView(convertView);
        iniciaFragment();

        return convertView;
    }

    private void iniciaView(View convertView) {
        list = (RecyclerView) convertView.findViewById(R.id.busqueda_list_animeflv); //utiliza la misma que home screen
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
        sinResultados = (TextView)convertView.findViewById(R.id.noResultados);
        searchQuery = preparaQuery(getArguments().getString("query")); //prepara la query de busqueda
    }

    private void iniciaFragment() {
        if (getAnimesBuscados() == null && produceResultados == null) { //significa que es la primera vez que inicia el fragment
            new BuscarAnime().execute(this); //ejecuta la busqueda via asynctask
        } else { //el fragment se encontraba guardado en el fragment manager a causa de un cambio en la pantalla (rotacion)
            if (produceResultados) {
                adaptador = new AdGenerosEndless(getActivity(), animesBuscados, list);
                adaptador.setClickListener(this);
                list.setAdapter(adaptador);
                setOnLoadMoreListener();
            } else {
                sinResultados.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsApplication.getInstance().trackScreenView("Busqueda");
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
        setData(animesBuscados, produceResultados);
        mListener = null;
    }

    @Override
    public void customClickListener(View v, int position) {
        mListener.onBusquedaInteraction(animesBuscados.get(position).getUrlCapitulo());
        getActivity().setTitle(animesBuscados.get(position).getNombre());
       // Log.d("HOLA", "listener");
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
        public void onBusquedaInteraction(String url);
    }

    //Funcion que prepara la url de busqueda mediante el query de busqueda
    public String preparaQuery(String query) {
        String aux = "";
        String busqueda;
        String[] div = query.split(" "); //separa por espacios
        for (int i = 0; i < div.length; i++){
            if (i == 0) {
                aux = div[i];
            } else {
                aux = aux + "+" + div[i];
            }
        }

        busqueda = aux;

        return busqueda;
    }


    private class BuscarAnime extends AsyncTask<AdGenerosEndless.CustomRecyclerListener, Void, Void> {

        @Override
        protected Void doInBackground(AdGenerosEndless.CustomRecyclerListener... params) {
            String urlBusqueda;
            Utilities util = new Utilities();
            Document codigoFuente;

            try {
                if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                    Animeflv animeflv = new Animeflv();
                    urlBusqueda = queryTemplateFlv + searchQuery;
                    if (util.existenCookies(getContext())) {
                        codigoFuente = util.connect(urlBusqueda, util.getCookiesEnSharedPreferences(getContext()));
                    } else {
                        codigoFuente = util.connect(urlBusqueda);
                    }
                    animesBuscados = animeflv.busquedaFLV(codigoFuente);

                    if (animesBuscados == null) {
                        produceResultados = false;
                    } else {
                        //  System.out.println("tamaño  " + animesBuscados.size());
                        produceResultados = true;
                        urlSiguiente = animeflv.siguientePagina(codigoFuente);
                        Log.d("URL", urlSiguiente);
                        tienePaginaSiguiente = !urlSiguiente.equals(""); //comprueba si tiene pagina siguiente
                        adaptador = new AdGenerosEndless(getActivity(), animesBuscados, list);
                        adaptador.setClickListener(params[0]);
                    }
                } else { //reyanime
                    Reyanime reyanime = new Reyanime();
                    urlBusqueda = queryTemplateRey + searchQuery;
                    codigoFuente = util.connect(urlBusqueda);
                    animesBuscados = reyanime.busqueda(codigoFuente);

                    if (animesBuscados == null) {
                        produceResultados = false;
                    } else {
                        //  System.out.println("tamaño  " + animesBuscados.size());
                        produceResultados = true;
                        urlSiguiente = reyanime.siguientePagina(codigoFuente);
                        Log.d("URL", urlSiguiente);
                        tienePaginaSiguiente = !urlSiguiente.equals(""); //comprueba si tiene pagina siguiente*/
                        adaptador = new AdGenerosEndless(getActivity(), animesBuscados, list);
                        adaptador.setClickListener(params[0]);
                    }
                }
               // Log.d("HOLA", "pase despues");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            sinResultados.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {

            if (produceResultados) {
                list.setAdapter(adaptador);
                setOnLoadMoreListener();
            } else {
                sinResultados.setVisibility(View.VISIBLE);
            }

            //list.setHasFixedSize(true);
            //getActivity().setProgressBarIndeterminateVisibility(false);
            bar.setVisibility(View.GONE);
        }
    }

    public void setData(List<HomeScreenEpi> animesBuscados, boolean produceResultados) {
        this.animesBuscados = animesBuscados;
        this.produceResultados = produceResultados;
    }

    private List<HomeScreenEpi> getAnimesBuscados() {
        return animesBuscados;
    }

    private class GetAnimeSiguiente extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Utilities util = new Utilities();
            try {
                Document codigoFuente = util.connect(urlSiguiente);
                if (util.queProveedorEs(getContext()) == Utilities.ANIMEFLV) {
                    if (util.existenCookies(getContext())) {
                        codigoFuente = util.connect(urlSiguiente, util.getCookiesEnSharedPreferences(getContext()));
                    }
                    Animeflv anime = new Animeflv();
                    urlSiguiente = anime.siguientePagina(codigoFuente);//comprueba si tiene pagina siguiente
                    tienePaginaSiguiente = !urlSiguiente.equals(""); //si urlSiguiente es igual a "" --> tienePaginaSiguiente = false, de otra manera true
                    animesBuscadosSiguiente = anime.busquedaFLV(codigoFuente); //se ocupa el de busqueda ya que el diseño de la pagina de generos es igual
                } else { //reyanime
                    Reyanime reyanime = new Reyanime();
                    urlSiguiente = reyanime.siguientePagina(codigoFuente);
                    tienePaginaSiguiente = !urlSiguiente.equals("");
                    animesBuscadosSiguiente = reyanime.busqueda(codigoFuente);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void result) {
            animesBuscados.remove(animesBuscados.size() - 1); //remueve el item null del progress bar
            adaptador.notifyItemRemoved(animesBuscados.size()); //notifica que removio un item
            int posFinal = animesBuscados.size(); //tamaño antes de añadir items
            animesBuscados.addAll(animesBuscadosSiguiente); //añade todos los Anime nuevos
            adaptador.notifyItemRangeInserted(posFinal, animesBuscados.size()); //notifica que los añadio todos
            adaptador.setLoaded(); //indica que ya no se esta cargando
        }
    }

    private void setOnLoadMoreListener() {
        adaptador.setOnLoadMoreListener(new AdGenerosEndless.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (tienePaginaSiguiente) {
                    animesBuscados.add(null); //agrega un item null para activar el view de la progressbar
                    adaptador.notifyItemInserted(animesBuscados.size() - 1); //notifica que añadio un elemento
                    new GetAnimeSiguiente().execute(); //recolecta el anime de la pagina siguiente
                }
            }
        });
    }

}
