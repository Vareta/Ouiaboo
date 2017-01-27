package com.ouiaboo.ouiaboo;

import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;
import com.ouiaboo.ouiaboo.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.VideoView;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class VideoPlayer extends Activity implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "VIDEO PLAYER";
    private String url;
    private VideoView video;
    private ToggleButton play_pause_button;
    private TextView tiempo_actual;
    private TextView tiempo_total;
    private int posicionGuardada = 0;
    private SeekBar barraProgreso;
    Handler mHideHandler = new Handler();
    private String urlEntrada;
    private ProgressBar bar;
    private HomeScreenEpi objEpi;
    private boolean esDeInternet; //para ver si el archivo viene de internet o desde el dispositivo
    private String tActual;
    private String tTotal;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 5000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player);

        bar = (ProgressBar) findViewById(R.id.progressBar);
        //cambia el color de la barra de progreso circular
        ((ProgressBar) findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.accent_light), PorterDuff.Mode.SRC_IN);
        video = (VideoView) findViewById(R.id.videoView);

        getData();

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.videoView);


        //Botones, seekbar y texfield

        play_pause_button = (ToggleButton) findViewById(R.id.playPauseButton);
        tiempo_actual = (TextView) findViewById(R.id.tiempoActual);
        tiempo_total = (TextView) findViewById(R.id.tiempoTotal);
        barraProgreso = (SeekBar) findViewById(R.id.seekBar);
        barraProgreso.setOnSeekBarChangeListener(this);


        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });
        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        play_pause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play_pause_button.isChecked()) {
                    video.start();
                } else {
                    video.pause();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        findViewById(R.id.playPauseButton).setOnTouchListener(mDelayHideTouchListener);
        new GetVideoUrlAndPlay().execute();

        Log.d(TAG, "OnCreate");
    }

   /* @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        posicionGuardada = video.getCurrentPosition();
        savedInstanceState.putInt("posicion", posicionGuardada);
        Log.d(TAG, "guardo "+String.valueOf(posicionGuardada));
        Log.d(TAG, "pause2");
    }*/

    public void getData() {
        objEpi = (HomeScreenEpi) getIntent().getSerializableExtra("episodio");
        urlEntrada = objEpi.getUrlCapitulo();
    }

    public void reproducir(String url) {
        bar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Reproducir");
        if (esDeInternet) {
            Uri aux = Uri.parse(url);
            video.setVideoURI(aux); //setVideoURI llama internamente a prepareAsync(); de mediaplayer
        } else {
            video.setVideoPath(url);
        }
        video.requestFocus();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "Reproducir Listener (Ready)");
                video.start();
                bar.setVisibility(View.GONE);
                updateProgressBar();
            }
        });
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            video.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {


        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            int totalDuration = video.getDuration();
            int currentDuration = video.getCurrentPosition();

            tActual = milliSecondsToTimer(currentDuration);
            tTotal = milliSecondsToTimer(totalDuration);
            // Displaying Total Duration time
            tiempo_total.setText("" + tTotal);
            // Displaying time completed playing
            tiempo_actual.setText("" + tActual);


            barraProgreso.setMax(totalDuration);
            barraProgreso.setProgress(currentDuration);
            barraProgreso.setSecondaryProgress(video.getBufferPercentage());

            if (video.isPlaying()) {
                if (!play_pause_button.isChecked()) { //si se reproduce y no esta marcado, se marca (se muestra pause)
                    play_pause_button.setChecked(true);
                }
            } else {
                if (play_pause_button.isChecked()) { //si esta en pause se desmarca (se muestra play)
                    play_pause_button.setChecked(false);
                }
                if (tTotal.equals(tActual)) {
                    finish();
                    Log.d(TAG, "Finish");
                }
            }
            //Log.d(TAG, "Handler");

            // Running this thread after 100 milliseconds
            mHideHandler.postDelayed(this, 100);
        }
    };

    private void updateProgressBar() {
        mHideHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /*Falta mejorar el como consulta por proveedor ya que se encuentra muy engorroso*/
    private class GetVideoUrlAndPlay extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Utilities util = new Utilities();
            Animeflv animeflv = new Animeflv();
            Reyanime reyanime = new Reyanime();
            try {
                if (objEpi.getInformacion().equals("descargado")) { //video almacenado en el dispositivo
                    esDeInternet = false;
                    url = urlEntrada;
                    if (objEpi.getPreview().contains("animeflv")) {
                        animeflv.añadirHistorialFlv(objEpi.getNombre(), objEpi.getPreview()); //se usa preview ya que en este campo se guarda la url del anime cuando este proviene del dispositivo
                    } else {
                        reyanime.añadirHistorialRey(objEpi.getNombre(), objEpi.getPreview());
                    }
                } else { //video desde internet
                    esDeInternet = true;
                    if (util.queProveedorEs(getBaseContext()) == Utilities.ANIMEFLV) {
                        Log.d("ANIMEFLV", urlEntrada);
                        url = animeflv.urlDisponible(urlEntrada, getBaseContext());
                        Log.d("url", url);
                        animeflv.añadirHistorialFlv(objEpi.getNombre(), objEpi.getUrlCapitulo()); //añade al historial (en la vista de capitulos)
                        animeflv.añadirHistorial(objEpi.getNombre(), objEpi.getInformacion(), objEpi.getPreview(), objEpi.getUrlCapitulo()); //añade al historial (el historial interno, vease fragment Historial)
                    } else {
                        Log.d("REYANIME", urlEntrada);
                        url = reyanime.urlDisponible(urlEntrada, getBaseContext());
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
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            bar.setVisibility(View.GONE);
            reproducir(url);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "OnRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        AnalyticsApplication.getInstance().trackScreenView("Video Player");
        Log.d(TAG, String.valueOf(posicionGuardada));
        if (posicionGuardada == 0) {
            //video.start();
            //reproducir(url);
            //new GetVideoUrlAndPlay().execute();
            Log.d(TAG, "resume de 0");
        } else {
            Log.d(TAG, "resume de" + posicionGuardada);
            bar.setVisibility(View.VISIBLE);
            video.resume();
            video.seekTo(posicionGuardada);
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "Onprepare");
                    mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                        @Override
                        public void onSeekComplete(MediaPlayer mp) {
                            Log.d(TAG, "onSeeker listener ready");
                            mHideHandler.postDelayed(mUpdateTimeTask, 100);
                            video.start();
                            bar.setVisibility(View.GONE);
                            //video.pause();

                        }
                    });

                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        posicionGuardada = video.getCurrentPosition();
        Log.d(TAG, "guardo " + posicionGuardada);
        video.pause();
        mHideHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

}
