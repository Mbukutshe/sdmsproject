package com.example.student.sdms;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Student on 10/9/2016.
 */

public class downloadfile extends Activity
{
    private static final String DL_ID = "downloadId";
    private SharedPreferences prefs;
    private DownloadManager dm;
    private ImageView imageView;
    public  static String dwnload_file_path = "http://wiseman.cloudaccess.host/Mbukutshe.pdf";
    public  static String filename="Mbukutshe.pdf";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = new ImageView(this);
        setContentView(imageView);
        Bundle extras = getIntent().getExtras();
        dwnload_file_path = extras.getString("link");
        filename = extras.getString("filename");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        dm = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!prefs.contains(DL_ID)) {
            Uri resource = Uri.parse("http://wiseman.cloudaccess.host/Mbukutshe.pdf");
            DownloadManager.Request request = new DownloadManager.Request(resource);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setAllowedOverRoaming(false);
            request.setTitle("SDMS");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Mbukutshe.pdf");
            request.setDescription("downloading ..... " + "Mbukutshe.pdf");
            request.setVisibleInDownloadsUi(true);
            long id = dm.enqueue(request);
            prefs.edit().putLong(DL_ID, id).commit();
        } else {
            queryDownloadStatus();
        }
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            queryDownloadStatus();
        }
    };
    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(prefs.getLong(DL_ID, 0));
        Cursor c = dm.query(query);
        if(c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            Log.d("DM Sample","Status Check: "+status);
            switch(status) {
                case DownloadManager.STATUS_PAUSED:
                case DownloadManager.STATUS_PENDING:
                case DownloadManager.STATUS_RUNNING:
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    try {
                        ParcelFileDescriptor file = dm.openDownloadedFile(prefs.getLong(DL_ID, 0));
                        FileInputStream fis = new ParcelFileDescriptor.AutoCloseInputStream(file);
                        //imageView.setImageBitmap(BitmapFactory.decodeStream(fis));
                        Toast.makeText(getBaseContext(),"download complete",Toast.LENGTH_LONG);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(getBaseContext(),"Failed",Toast.LENGTH_LONG);
                    dm.remove(prefs.getLong(DL_ID, 0));
                    prefs.edit().clear().commit();
                    break;
            }
        }
    }
}