package com.example.student.sdms;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    FragmentManager fragmentManager = getSupportFragmentManager();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Menu menu;
    public  static int feed=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(getApplicationContext(),getString(R.string.ads_unit_id));
        AdView adView=(AdView)findViewById(R.id.addView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Message(s)");
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()== NetworkInfo.State.CONNECTED
                ||connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() ==NetworkInfo.State.DISCONNECTED)
        {
            Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_LONG);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_message);
        mRecyclerView.setHasFixedSize(true);
        List<ItemObject> myDataset = getAllItemList();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new MessageAdapter(MainActivity.this,myDataset);
        mRecyclerView.setAdapter(mAdapter);

        SqliteController controller = new SqliteController(this);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(feed==0) {
                    getSupportActionBar().setTitle("Feedback");
                    fragmentManager.beginTransaction().replace(R.id.content_main, new feedback()).commit();
                    fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.homefab));
                }
                else
                {
                   startActivity(new Intent(getBaseContext(),MainActivity.class));
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_download) {
            getSupportActionBar().setTitle("Document(s)");
            //menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.not));
            fragmentManager.beginTransaction().replace(R.id.content_main,new downloads()).commit();
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.feedback));

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (id == R.id.nav_about) {
            getSupportActionBar().setTitle("About");
            fragmentManager.beginTransaction().replace(R.id.content_main,new about()).commit();
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.feedback));
        }
        else if (id == R.id.nav_home) {

            getSupportActionBar().setTitle("Message(s)");
            fragmentManager.beginTransaction().replace(R.id.content_main,new exit()).commit();
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.feedback));

        }else if (id == R.id.nav_exit) {
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.feedback));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.logo);
            builder.setMessage(Html.fromHtml("<font color='#627984'>Are you sure you want to exit?</font>"))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        } else if (id == R.id.nav_contacts) {

            getSupportActionBar().setTitle("Contacts");
            fragmentManager.beginTransaction().replace(R.id.content_main,new contacts()).commit();
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.feedback));
        } else if (id == R.id.nav_help) {

            Uri uri = Uri.parse("http://androidbook.blogspot.com/");
            Intent browser = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(browser);
        }
        else if(id==R.id.nav_upload)
        {
         /*   getSupportActionBar().setTitle("Log In");
            fragmentManager.beginTransaction().replace(R.id.content_main,new login()).commit();
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.feedback));*/

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.login);
            AlertDialog alert = builder.create();
            alert.show();


        }
        else if(id==R.id.nav_send)
        {
           /* getSupportActionBar().setTitle("Log In");
            fragmentManager.beginTransaction().replace(R.id.content_main,new SendFragment()).commit();
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.feedback));*/

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.login);
            AlertDialog alert = builder.create();
            alert.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private List<ItemObject> getAllItemList(){


        List<ItemObject> allItems = new ArrayList<>();
        Cursor data = new SqliteController(this).getAllMessage();
        if(data.moveToFirst())
        {
            do
            {
                allItems.add(new ItemObject(data.getString(1),"","",data.getString(2),data.getString(3),""));
            }
            while(data.moveToNext());
        }
        return allItems;
    }
}
