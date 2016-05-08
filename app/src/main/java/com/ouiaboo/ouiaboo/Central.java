package com.ouiaboo.ouiaboo;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.ouiaboo.ouiaboo.Tables.animeflv.VerMasTardeTable;
import com.ouiaboo.ouiaboo.clases.GenerosClass;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;
import com.ouiaboo.ouiaboo.fragmentsFLV.Compartir;
import com.ouiaboo.ouiaboo.fragmentsFLV.ReporteDeErrores;
import com.ouiaboo.ouiaboo.fragmentsFLV.Busqueda;
import com.ouiaboo.ouiaboo.fragmentsFLV.Descargadas;
import com.ouiaboo.ouiaboo.fragmentsFLV.Faq;
import com.ouiaboo.ouiaboo.fragmentsFLV.Favoritos;
import com.ouiaboo.ouiaboo.fragmentsFLV.Generos;
import com.ouiaboo.ouiaboo.fragmentsFLV.GenerosContenido;
import com.ouiaboo.ouiaboo.fragmentsFLV.Historial;
import com.ouiaboo.ouiaboo.fragmentsFLV.HomeScreen;
import com.ouiaboo.ouiaboo.fragmentsFLV.Preferencias;
import com.ouiaboo.ouiaboo.fragmentsFLV.VerMasTarde;

import org.jsoup.nodes.Document;
import org.litepal.LitePalApplication;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Central extends AppCompatActivity implements HomeScreen.OnFragmentInteractionListener, Busqueda.OnFragmentInteractionListener,
        VerMasTarde.OnFragmentInteractionListener, ReporteDeErrores.OnFragmentInteractionListener,
        Favoritos.OnFragmentInteractionListener, Descargadas.OnFragmentInteractionListener,
        Historial.OnFragmentInteractionListener, Generos.OnFragmentInteractionListener,
        Preferencias.OnFragmentInteractionListener, Faq.OnFragmentInteractionListener,
        GenerosContenido.OnFragmentInteractionListener, Compartir.OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ProgressBar updateAppBar;
    private boolean deboActualizar = true;
    private static final String DEBO_ACTUALIZAR = "deboActualizar";
    private ProgressBar bar;
    private RelativeLayout contenedor;
    private int homeScreenFragmentId;


    //  private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);
        LitePalApplication.initialize(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); //el drawerLayout
        contenedor = (RelativeLayout) findViewById(R.id.contenedor);
        updateAppBar = (ProgressBar) findViewById(R.id.updateAppProgressBar);
        bar = (ProgressBar) findViewById(R.id.progressBarTop);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            ((ProgressBar) findViewById(R.id.updateAppProgressBar)).getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.rojo), PorterDuff.Mode.SRC_IN);
            ((ProgressBar) findViewById(R.id.progressBarTop)).getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.accent_light), PorterDuff.Mode.SRC_IN);
        }
        updateAppBar.setIndeterminate(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        TextView proveedor = (TextView) navHeader.findViewById(R.id.pag_actual);
        Utilities util = new Utilities();
        if (util.queProveedorEs(getBaseContext()) == Utilities.ANIMEFLV) {
            proveedor.setText(R.string.animeflv_drawer_layout);
            AnalyticsApplication.getInstance().trackEvent("Página","proveedor", "animeflv");
        } else {//reyanime
            proveedor.setText(R.string.reyanime_drawer_layout);
            AnalyticsApplication.getInstance().trackEvent("Página", "proveedor", "reyanime");
        }

        setUpToolbar(); //setea la toolbar
        setUpNavDrawer(); //setea el navigation drawer
        SQLiteDatabase db = Connector.getDatabase(); //crea las tablas, si estas no existen

        //listener del navigationview
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(false);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                switch (menuItem.getItemId()) {

                    case R.id.nav_inicio: //inicia fragmento inicio Inicio
                        ft.replace(R.id.contenedor, new HomeScreen(), Utilities.FRAGMENT_HOMESCREEN);
                        //no se agrega ft.addToBackStack ya que de lo contrario nunca saldria de homeScreen porque siempre agregaria un fragment al stack lo que contrasta con la condicion de salida de la funcion onBackpressed implementada
                        break;

                    case R.id.nav_mas_tarde:
                        ft.replace(R.id.contenedor, new VerMasTarde(), Utilities.FRAGMENT_VERMASTARDE);
                        ft.addToBackStack(Utilities.FRAGMENT_VERMASTARDE);
                        break;

                    case R.id.nav_favoritos:
                        ft.replace(R.id.contenedor, new Favoritos(), Utilities.FRAGMENT_FAVORITOS);
                        ft.addToBackStack(Utilities.FRAGMENT_FAVORITOS);
                        break;

                    case R.id.nav_descargadas:
                        ft.replace(R.id.contenedor, new Descargadas(), Utilities.FRAGMENT_DESCARGADAS);
                        ft.addToBackStack(Utilities.FRAGMENT_DESCARGADAS);
                        break;

                    case R.id.nav_historial:
                        ft.replace(R.id.contenedor, new Historial(), Utilities.FRAGMENT_HISTORIAL);
                        ft.addToBackStack(Utilities.FRAGMENT_HISTORIAL);
                        break;

                    case R.id.nav_generos:
                        ft.replace(R.id.contenedor, new Generos(), Utilities.FRAGMENT_GENEROS);
                        ft.addToBackStack(Utilities.FRAGMENT_GENEROS);
                        break;

                    case R.id.nav_preferencias:
                        ft.replace(R.id.contenedor, new Preferencias(), Utilities.FRAGMENT_PREFERENCIAS);
                        ft.addToBackStack(Utilities.FRAGMENT_PREFERENCIAS);
                        break;

                    case R.id.nav_compartir:
                        ft.replace(R.id.contenedor, new Compartir(), Utilities.FRAGMENT_COMPARTIR);
                        ft.addToBackStack(Utilities.FRAGMENT_COMPARTIR);
                        break;

                    case R.id.nav_faq:
                        ft.replace(R.id.contenedor, new Faq(), Utilities.FRAGMENT_FAQ);
                        ft.addToBackStack(Utilities.FRAGMENT_FAQ);
                        break;

                    case R.id.nav_aviso_legal:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(Utilities.URL_REPORTE_ERRORES));
                        startActivity(intent);
                        break;

                    default:
                        return true;
                }
                ft.commit();
                drawerLayout.closeDrawers();
                return true;
            }
        });

        if (savedInstanceState != null) { //si existe informacion guardada
            deboActualizar = savedInstanceState.getBoolean(DEBO_ACTUALIZAR);
        }

        if (deboActualizar) { //pregunta si debe actualizar
            new CheckForUpdate().execute();
        }


        if (!existeAlgunFragmentGuardado()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.contenedor, new HomeScreen(), Utilities.FRAGMENT_HOMESCREEN);
            ft.addToBackStack(Utilities.FRAGMENT_HOMESCREEN);
            ft.commit();
        }

        //new ActualizarAplicacion().execute();

    }

    /*
    Verifica si existe algun fragment guardado.
    PS: Revisar por si existe alguna manera de escribir esta funcion de otra manera (codigo engorroso)
     */
    private boolean existeAlgunFragmentGuardado() {
        FragmentManager fm = getSupportFragmentManager();
        Busqueda busquedaFragm = (Busqueda) fm.findFragmentByTag(Utilities.FRAGMENT_BUSQUEDA);
        if (busquedaFragm == null) {
            Descargadas descargadasFragm = (Descargadas) fm.findFragmentByTag(Utilities.FRAGMENT_DESCARGADAS);
            if (descargadasFragm == null) {
                Favoritos favoritosFragm = (Favoritos) fm.findFragmentByTag(Utilities.FRAGMENT_FAVORITOS);
                if (favoritosFragm == null) {
                    Generos generosFragm = (Generos) fm.findFragmentByTag(Utilities.FRAGMENT_GENEROS);
                    if (generosFragm == null) {
                        GenerosContenido generosContenidoFragm = (GenerosContenido) fm.findFragmentByTag(Utilities.FRAGMENT_GENEROSCONTENIDO);
                        if (generosContenidoFragm == null) {
                            Historial historialFragm = (Historial) fm.findFragmentByTag(Utilities.FRAGMENT_HISTORIAL);
                            if (historialFragm == null) {
                                HomeScreen homeScreenFragm = (HomeScreen) fm.findFragmentByTag(Utilities.FRAGMENT_HOMESCREEN);
                                if (homeScreenFragm == null) {
                                    Preferencias preferenciasFragm = (Preferencias) fm.findFragmentByTag(Utilities.FRAGMENT_PREFERENCIAS);
                                    if (preferenciasFragm == null) {
                                        VerMasTarde verMasTardeFragm = (VerMasTarde) fm.findFragmentByTag(Utilities.FRAGMENT_VERMASTARDE);
                                        if (verMasTardeFragm == null) {
                                            Compartir compartirFragm = (Compartir) fm.findFragmentByTag(Utilities.FRAGMENT_COMPARTIR);
                                            if (compartirFragm == null) {
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private void setUpNavDrawer() {
        if (toolbar != null) {
            assert getSupportActionBar() != null; //agregado ya que getSupportAction puede producir Method invocation may produce java.lang.NullPointerException
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
            setTitle(R.string.inicio_drawer_layout);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        contenedor.setVisibility(View.VISIBLE);
    }


    /*
        AHora esto se realiza mediante la libreria (borrar proximamente)

    private void poblarMenuLateral(){

        String[] drawerTitulos = getResources().getStringArray(R.array.drawer_list_uno); //arreglo de strings de la lista

        //poblando la lista expandible
        String[] paginasWebNombre = getResources().getStringArray(R.array.paginas_anime);
        String[] paginasWebIdioma = getResources().getStringArray(R.array.idioma_paginas_anime);

        ArrayList webList = new ArrayList<SitiosWeb>();
        for (int i = 0; i < paginasWebNombre.length; i++){
            webList.add(new SitiosWeb(paginasWebNombre[i], paginasWebIdioma[i]));
        }

        listDataHeader = new ArrayList<DrawerItemsListUno>();
        listDataChild = new HashMap<DrawerItemsListUno, List<SitiosWeb>>();

        String[] titulosExpList = getResources().getStringArray(R.array.drawer_list_cero);
        listDataHeader.add(new DrawerItemsListUno(titulosExpList[0], R.drawable.ic_action_globe));
        listDataChild.put(listDataHeader.get(0), webList);

        //poblando la lista 1 del drawer layout (menu de al lado)
        items = new ArrayList<DrawerItemsListUno>();
        items.add(new DrawerItemsListUno(drawerTitulos[0], R.drawable.ic_action_home)); //home
        items.add(new DrawerItemsListUno(drawerTitulos[1], R.drawable.ic_action_heart)); //ver mas tarde
        items.add(new DrawerItemsListUno(drawerTitulos[2], R.drawable.ic_action_heart)); //favoritos
        items.add(new DrawerItemsListUno(drawerTitulos[3], R.drawable.ic_action_help)); //descargadas
        items.add(new DrawerItemsListUno(drawerTitulos[4], R.drawable.ic_action_info)); //historial
        items.add(new DrawerItemsListUno(drawerTitulos[5], R.drawable.ic_action_help)); //FAQ
        items.add(new DrawerItemsListUno(drawerTitulos[6], R.drawable.ic_action_info)); //aviso legal
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_central, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final MenuItem searchItem = menu.findItem(R.id.buscar);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true); //submit button
        searchView.setQueryRefinementEnabled(true); //query refinement for search sugestion

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // Do something
                //Log.d("TextChange", newText);
                //Log.d("TextChange", "cambie");
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Utilities util = new Utilities();
                boolean realizaBusqueda = false;
                query = query.trim(); //elimina los espacion al comienzo y al final
                if (util.queProveedorEs(getBaseContext()) == Utilities.ANIMEFLV) {
                    if (query.length() < 4) { //se le quitan los espacios en blanco
                        // Log.d("MENOS DE 4", query);
                        Toast.makeText(Central.this, getString(R.string.ins_caracteres_menu_central_ES), Toast.LENGTH_SHORT).show();
                        AnalyticsApplication.getInstance().trackEvent("Warning", "Buscar", query);
                    } else {
                        realizaBusqueda = true;
                    }
                } else { //reyanime
                    realizaBusqueda = true;

                }

                if (realizaBusqueda) {
                    Bundle bundle = new Bundle();
                    bundle.putString("query", query);
                    Busqueda search = new Busqueda();
                    search.setArguments(bundle);
                    Log.d("QUERY", query);
                    AnalyticsApplication.getInstance().trackEvent("Anime", "Buscar", query);

                    //Inicia el fragmente que contiene los resultados de la busqueda
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.contenedor, search, Utilities.FRAGMENT_BUSQUEDA);
                    ft.addToBackStack(Utilities.FRAGMENT_BUSQUEDA); //para que se pueda devolver a un fragment anterior
                    ft.commit();
                    searchView.onActionViewCollapsed(); //cierra el teclado
                }

                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.drawable.ic_menu_white_24dp) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    @Override
    public void onBusquedaInteraction(String url) {
        Intent intent = new Intent(getBaseContext(), EpisodiosPlusInfo.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }


    @Override
    public void onVerMasTardeInteraction(HomeScreenEpi objEpi) {
        reproducir(objEpi);
    }

    @Override
    public void onFavoritoInteraction(String url) {
        Intent intent = new Intent(getBaseContext(), EpisodiosPlusInfo.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public void onGenerosInteraction(GenerosClass objGeneros) {
        //Envia la query al fragment Generos
        Bundle bundle = new Bundle();
        bundle.putSerializable("genero", objGeneros);
        GenerosContenido generosContenido = new GenerosContenido();
        generosContenido.setArguments(bundle);

        //Inicia el fragmente que contiene la url
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contenedor, generosContenido, Utilities.FRAGMENT_GENEROSCONTENIDO);
        ft.addToBackStack(Utilities.FRAGMENT_GENEROSCONTENIDO); //para que se pueda devolver a un fragment anterior
        ft.commit();
    }

    @Override
    public void onGenerosContenidoInteraction(String url) {
        Intent intent = new Intent(getBaseContext(), EpisodiosPlusInfo.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public void onHomeScreenInteraction(HomeScreenEpi objEpi) {
        reproducir(objEpi);
    }

    @Override
    public void onDescargadasInteraction(HomeScreenEpi objEpi) {
        reproducir(objEpi);
    }

    @Override
    public void onHistorialInteraction(HomeScreenEpi objEpi) {
        reproducir(objEpi);
    }

    /*Si los datos son dirigidos hacia el VideoPlayer, esta funcion es la encargada de manejar los datos
        correspondientes y verificar si se usa el reproductor nativo o el externo
     */
    private void reproducir(HomeScreenEpi objEpi) {
        Utilities util = new Utilities();

        if (util.esReproductorExterno(this)) {
            AnalyticsApplication.getInstance().trackEvent("Reproductor Externo", "ver", objEpi.getNombre());
            new ReproductorExterno().execute(objEpi);
        } else {
            AnalyticsApplication.getInstance().trackEvent("Reproductor Interno", "ver", objEpi.getNombre());
            Intent intent = new Intent(getBaseContext(), VideoPlayer.class);
            intent.putExtra("episodio", objEpi);
            startActivity(intent);
        }
    }

    private class ReproductorExterno extends AsyncTask<HomeScreenEpi, Void, Void> {
        String url;

        @Override
        protected Void doInBackground(HomeScreenEpi... params) {
            Animeflv anime = new Animeflv();
            Reyanime reyanime = new Reyanime();
            Utilities util = new Utilities();
            HomeScreenEpi objEpi = params[0];
            try {
                if (objEpi.getInformacion().equals("descargado")) { //video almacenado en el dispositivo
                    //se agrega file:// ya que en ocasiones ocurria un error en el cual exponia que no habia actividad para manejar el Intent
                    url = "file://" + objEpi.getUrlCapitulo(); //aqui la direccion es el path del video en el dispositivo
                    if (objEpi.getPreview().contains("animeflv")) {
                        anime.añadirHistorialFlv(objEpi.getNombre(), objEpi.getPreview()); //se usa preview ya que en este campo se guarda la url del anime cuando este proviene del dispositivo
                    } else { //reyanime
                        reyanime.añadirHistorialRey(objEpi.getNombre(), objEpi.getPreview());
                    }

                } else {
                    if (util.queProveedorEs(getBaseContext()) == Utilities.ANIMEFLV) {
                        url = anime.urlDisponible(objEpi.getUrlCapitulo(), getBaseContext());
                        anime.añadirHistorialFlv(objEpi.getNombre(), objEpi.getUrlCapitulo()); //añade al historial (en la vista de capitulos)
                        anime.añadirHistorial(objEpi.getNombre(), objEpi.getInformacion(), objEpi.getPreview(), objEpi.getUrlCapitulo()); //añade al historial (el historial interno, vease fragment Historial)
                    } else { //reyanime
                        url = reyanime.urlDisponible(objEpi.getUrlCapitulo(), getBaseContext());
                        reyanime.añadirHistorialRey(objEpi.getNombre(), objEpi.getUrlCapitulo());
                        reyanime.añadirHistorial(objEpi.getNombre(), objEpi.getInformacion(), objEpi.getPreview(), objEpi.getUrlCapitulo());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            contenedor.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            bar.setVisibility(View.GONE);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setDataAndType(Uri.parse(url), "video/*");
            startActivity(intent);
        }
    }

    private class ActualizarAplicacion extends AsyncTask<String, Integer, Void> {
        File outputFile;

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                int lenghtOfFile = c.getContentLength();

                String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                File file = new File(PATH);
                file.mkdirs();
                outputFile = new File(file, "Ouiaboo.apk");
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int cont, total = 0;
                while ((cont = is.read(buffer)) != -1) {
                    total = total + cont;
                    publishProgress(((total * 100) / lenghtOfFile));
                    fos.write(buffer, 0, cont);
                }
                fos.close();
                is.close();
            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            updateAppBar.setProgress(progress[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            updateAppBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            updateAppBar.setVisibility(View.GONE);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(outputFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void actualizarApp(final String urlUpdate) {
        //check for update
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.mensajeDialog_Central)
                .setTitle(R.string.tituloDialog_Central)
                .setCancelable(false);

        builder.setPositiveButton(R.string.aceptarDialog_Central, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPref = getSharedPreferences(Utilities.PREFERENCIAS, Context.MODE_PRIVATE);
                String opcion = sharedPref.getString("tipoUpdate", "enlaces");

                if (opcion.equals("enlaces")) { //si esta marcada la opcion de enlaces, envia hacia la pagina
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(Utilities.URL_APP_DESCARGA));
                    startActivity(intent);
                } else { //Si es automatico, descarga automaticamente la aplicacion
                    new ActualizarAplicacion().execute(urlUpdate);
                }
            }
        });

        builder.setNegativeButton(R.string.cancelarDialog_Central, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deboActualizar = false;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /* Accede a la pagina donde se mantiene la version del codigo y el enlace de descarga */
    private class CheckForUpdate extends AsyncTask<Void, Void, Void> {
        List<String> versionAndUrl = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... params) {
            Utilities utilities = new Utilities();
            Document codigoFuente = utilities.connect(Utilities.URL_APP_UPDATE); //obtiene el codigo fuente de la pagina
            if (codigoFuente != null) { // si existe
                //obtiene una lista, en donde el primer item es la version del codigo mientras que el segundo es el enlace de descarga de la apk
                versionAndUrl = utilities.obtenerEnlaceActualizacion(codigoFuente);
            } else {// si no existe, entonces declara que no debe actualizar durante el tiempo que la aplicacion se este ejecutando y no se destruya
                deboActualizar = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!versionAndUrl.isEmpty()) {
                int versionCode = BuildConfig.VERSION_CODE;
                if (versionCode < Integer.parseInt(versionAndUrl.get(0))) {
                    actualizarApp(versionAndUrl.get(1));
                }
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(DEBO_ACTUALIZAR, deboActualizar);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


   /*
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        deboActualizar = savedInstanceState.getBoolean(DEBO_ACTUALIZAR);
        Log.d("ACTUALIZAR2", String.valueOf(deboActualizar));
    }*/

    /*
        Esta función maneja el flujo de la aplicacion, la cual funciona de la siguiente manera:
        Dado cualquier fragment en la actividad central, al presionar back se deberá rederigir hacia el fragmento homeScreen. La unica excepción para
        este flujo es cuando el usuario se dirige a generos y selecciona un genero. En ese caso el usuario, al presionar back, será rederigido nuevamente a la seccion
        de generos. Esto sólo funciona cuando el fragment generos y generos contenido fueron los ultimos accedidos
     */
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int contador = fm.getBackStackEntryCount();
        if (contador > 1) {
            if (contador > 2) {
                if ((fm.getBackStackEntryAt(contador - 2).getName().equals(Utilities.FRAGMENT_GENEROS)) && (fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName().equals(Utilities.FRAGMENT_GENEROSCONTENIDO))) { //si
                    fm.popBackStack(Utilities.FRAGMENT_GENEROS, 0);
                } else {
                    fm.popBackStackImmediate(Utilities.FRAGMENT_HOMESCREEN, 0);
                }
            } else {
                fm.popBackStackImmediate(Utilities.FRAGMENT_HOMESCREEN, 0);
            }
        } else {
            supportFinishAfterTransition(); //metodo que se llama dentro de super.onBackPressed(); cuando no hay fragments
        }

    }
}
