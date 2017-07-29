package com.ouiaboo.ouiaboo.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ouiaboo.ouiaboo.Tables.DescargadosTable;
import com.ouiaboo.ouiaboo.util.CRUD;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vareta on 25-07-2017.
 */

public class DownloadService extends Service {
    private static final String TAG = "classes.URLService: ";
    private String urlDescarga;
    private String numeroCapitulo;
    private String nombreAnime;
    private String urlCapitulo;
    private Handler threadHandler = new Handler();
    private long id;
    Cursor cursor;
    DownloadManager manager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.d(TAG, "inicio service");
            urlDescarga = intent.getStringExtra("urlDescarga");
            numeroCapitulo = intent.getStringExtra("numeroCapitulo");
            nombreAnime = intent.getStringExtra("nombreAnime");
            urlCapitulo = intent.getStringExtra("urlCapitulo");
            iniciarDescarga();
        }

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    /**
     * Inicia la descaga del capitulo de anime mediante el DownloadManager
     */
    private void iniciarDescarga() {
        String nombreVideo = nombreAnime + "-" + numeroCapitulo + ".mp4"; //nombre que se le dara al archivo descargado
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlDescarga));
        request.setDescription(numeroCapitulo); //descripcion de la notificacion (numero de capitulo)
        request.setTitle(nombreAnime); //titulo de la notificacion (nombre anime)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //setea las notificaciones
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES + "/Ouiaboo", nombreVideo);
        request.setMimeType("video/x-msvideo");

        manager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        id = manager.enqueue(request);
        UpdateSeekBarThread update = new UpdateSeekBarThread();
        threadHandler.postDelayed(update, 1000);

        //almacena los capitulos guardados en la tabla, sin importar si estos estan completamente descargados
        CRUD crud = new CRUD();
        crud.registraInicioDescarga(id, nombreAnime, numeroCapitulo, nombreVideo, urlCapitulo);
    }



    // Thread to Update position for SeekBar.
    private class UpdateSeekBarThread implements Runnable {
        DownloadManager.Query videoDownloadQuery = new DownloadManager.Query();

        public void run()  {
            //set the query filter to our previously Enqueued download
            videoDownloadQuery.setFilterById(id);
            //Query the download manager about downloads that have been requested.
            cursor = manager.query(videoDownloadQuery);
            if(cursor.moveToFirst()){
                downloadStatus(cursor, id);
            }
            cursor.close();
            threadHandler.postDelayed(this, 1000);
        }
    }

    private void downloadStatus(Cursor cursor, long DownloadId){

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);

        String statusText = "";
        String reasonText = "";

        switch(status){
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                Log.d("Status", statusText);
                switch(reason){
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        Log.d("reasonText", reasonText);
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        Log.d("reasonText", reasonText);
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        Log.d("reasonText", reasonText);
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        Log.d("reasonText", reasonText);
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        Log.d("reasonText", reasonText);
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        Log.d("reasonText", reasonText);
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        Log.d("reasonText", reasonText);
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        Log.d("reasonText", reasonText);
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        Log.d("reasonText", reasonText);
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                Log.d("Status", statusText);
                switch(reason){
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        Log.d("reasonText", reasonText);
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        Log.d("reasonText", reasonText);
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        Log.d("reasonText", reasonText);
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        Log.d("reasonText", reasonText);
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                Log.d("Status", statusText);
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                Log.d("Status", statusText);
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                Log.d("Status", statusText);
                DescargadosTable descargadosTable = new DescargadosTable();
                descargadosTable.setComplete(true);
                descargadosTable.updateAll("idDescarga=?", String.valueOf(id));
                stopSelf();
                break;
        }

    }



    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Log.d("destroy", "ondestroy");
    }


}
