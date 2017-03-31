package com.example.student.sdms;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.DataOutputStream;

import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.Inflater;

import in.gauriinfotech.commons.Commons;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    FragmentManager fragmentManager = getSupportFragmentManager();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Menu menu;
    public  static Menu menus;
    public  static int feed=0;
    private Uri filepath;
    private TextView choose;
    int serverResponseCode = 0;
    String urgent;
    ProgressDialog dialogprogress = null;
    TextView messageText,networkState;
    AppCompatButton uploadButton;
    String upLoadServerUri = "http://dms.plattdriveprimary.co.za/simpleupload.php";
    public static String uploadFilePath="";
    public static String uploadFileName="";
    private final String UPLOAD_URL="http://sdms.portfolioonline.co.za/upload.php";
    RequestQueue requestQueue;
    private static boolean active=true;
    public static boolean messageActive=true;
    public static Context context;
    static int size=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menus =menu;
        context = getApplicationContext();




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_message);
        mRecyclerView.setHasFixedSize(true);
        List<ItemObject> myDataset = getAllItemList();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MessageAdapter(MainActivity.this,myDataset);
        mRecyclerView.setAdapter(mAdapter);

        int size = mAdapter.getItemCount();
        if(size==4) {
            MobileAds.initialize(getApplicationContext(), getString(R.string.ads_unit_id));
            AdView adView = (AdView) findViewById(R.id.addView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        SqliteController controller = new SqliteController(this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!(getSupportActionBar().getTitle().toString().equalsIgnoreCase("feedback"))) {
                    getSupportActionBar().setTitle("Feedback");
                    fragmentManager.beginTransaction().replace(R.id.content_main, new feedback()).commit();
                    fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.homefab));

                    MenuItem documentOption = menu.findItem(R.id.action_download);
                    documentOption.setVisible(true);

                    MenuItem messageOption = menu.findItem(R.id.action_messages);
                    messageOption.setVisible(true);
                }
                else
                {
                    getSupportActionBar().setTitle("Messages");
                    fragmentManager.beginTransaction().replace(R.id.content_main,new exit()).commit();
                    fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.feedback));

                    MenuItem documentOption = menu.findItem(R.id.action_download);
                    documentOption.setVisible(true);

                    MenuItem messageOption = menu.findItem(R.id.action_messages);
                    messageOption.setVisible(false);
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
        this.menus=menu;
        MenuItem documentOption = menu.findItem(R.id.action_download);
        documentOption.setVisible(true);
        MenuItem messageOption = menu.findItem(R.id.action_messages);
        messageOption.setVisible(false);
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
            getSupportActionBar().setTitle("Documents");
            //menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.not));
            fragmentManager.beginTransaction().replace(R.id.content_main,new downloads()).commit();
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.feedback));

            MenuItem documentOption = menu.findItem(R.id.action_download);
            documentOption.setVisible(false);

            MenuItem messageOption = menu.findItem(R.id.action_messages);
            messageOption.setVisible(true);

            messageActive =false;

        }
        else
            if(id==R.id.action_messages)
            {
                getSupportActionBar().setTitle("Messages");
                //menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.not));
                fragmentManager.beginTransaction().replace(R.id.content_main,new exit()).commit();
                fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.feedback));

                MenuItem documentOption = menu.findItem(R.id.action_download);
                documentOption.setVisible(true);

                MenuItem messageOption = menu.findItem(R.id.action_messages);
                messageOption.setVisible(false);

                messageActive =true;
            }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        active = false;
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        active=true;
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (id == R.id.nav_about) {
            Uri uri = Uri.parse("http://www.plattdriveprimary.co.za/");
            Intent browser = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(browser);
        }
        else if (id == R.id.nav_home) {

            getSupportActionBar().setTitle("Messages");
            fragmentManager.beginTransaction().replace(R.id.content_main,new exit()).commit();
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),R.drawable.feedback));

            MenuItem documentOption = menu.findItem(R.id.action_download);
            documentOption.setVisible(true);

            MenuItem messageOption = menu.findItem(R.id.action_messages);
            messageOption.setVisible(false);

            messageActive =true;

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

            Uri uri = Uri.parse("http://dms.plattdriveprimary.co.za/");
            Intent browser = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(browser);

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
            final Animation textAnim = new AlphaAnimation(0.0f,1.0f);
           final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.login);
           final AppCompatButton login = (AppCompatButton)dialog.findViewById(R.id.btn_login);
           final ValueAnimator anim = new ValueAnimator();
            anim.setIntValues(Color.parseColor("#FFFFFF"),Color.parseColor("#00FF00"));
            anim.setEvaluator(new ArgbEvaluator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    login.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText username = (EditText)dialog.findViewById(R.id.input_username);
                    EditText password = (EditText)dialog.findViewById(R.id.input_password);
                    String user=username.getText().toString();
                    String pass=password.getText().toString();
                    if(user.equalsIgnoreCase("wiseman") && pass.equalsIgnoreCase("Khanyisa18"))
                    {
                        anim.setDuration(100);
                        anim.start();
                        dialog.setContentView(R.layout.upload_fragment);
                        choose = (TextView)dialog.findViewById(R.id.title_choose);
                        AppCompatButton btnChoose = (AppCompatButton)dialog.findViewById(R.id.btn_choose);
                        messageText = (TextView)dialog.findViewById(R.id.network_state);
                        networkState = (TextView)dialog.findViewById(R.id.network_state);
                        final EditText sender = (EditText)dialog.findViewById(R.id.sender_doc);
                        final EditText title = (EditText)dialog.findViewById(R.id.title_doc);
                        final EditText desc = (EditText)dialog.findViewById(R.id.desc_doc);
                        btnChoose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setType("*/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(Intent.createChooser(intent,"Select document"),1);

                            }
                        });
                        ImageButton back = (ImageButton)dialog.findViewById(R.id.left_arrow);
                        back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        AppCompatButton upload =(AppCompatButton)dialog.findViewById(R.id.btn_upload);
                        upload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                                if (isConnected)
                                {
                                    uploadMultipart();
                                    dialogprogress = ProgressDialog.show(dialog.getContext(), "", "Uploading file...", true);

                                    new Thread(new Runnable() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    messageText.setText("uploading started.....");
                                                }
                                            });

                                            uploadFile(uploadFilePath,sender.getText().toString(),title.getText().toString(),desc.getText().toString());

                                        }
                                    }).start();
                                }
                                else
                                {
                                    ImageView network = (ImageView)dialog.findViewById(R.id.offline);
                                    network.setBackgroundResource(R.drawable.offline);
                                    networkState.setText("No internet connection!");
                                    textAnim.setDuration(50);
                                    textAnim.setStartOffset(20);
                                    textAnim.setRepeatMode(Animation.REVERSE);
                                    textAnim.setRepeatCount(3);
                                    network.startAnimation(textAnim);
                                    networkState.startAnimation(textAnim);
                                }

                            }
                        });
                    }
                    else
                    {
                        TextView textView = (TextView)dialog.findViewById(R.id.error);
                        textView.setText("Incorrect Username or Password. Please try again or later!");
                        textAnim.setDuration(50);
                        textAnim.setStartOffset(20);
                        textAnim.setRepeatMode(Animation.REVERSE);
                        textAnim.setRepeatCount(3);
                        textView.startAnimation(textAnim);
                    }
                }
            });
            dialog.show();
        }
        else if(id==R.id.nav_send)
        {
           final Animation textAnim = new AlphaAnimation(0.0f,1.0f);
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.login);
            dialog.setCanceledOnTouchOutside(false);

            final AppCompatButton login = (AppCompatButton)dialog.findViewById(R.id.btn_login);
            final ValueAnimator anim = new ValueAnimator();
            anim.setIntValues(Color.parseColor("#FFFFFF"),Color.parseColor("#00FF00"));
            anim.setEvaluator(new ArgbEvaluator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    login.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    EditText username = (EditText)dialog.findViewById(R.id.input_username);
                    EditText password = (EditText)dialog.findViewById(R.id.input_password);
                    String user=username.getText().toString();
                    String pass=password.getText().toString();

                    if(user.equals("Wiseman") && pass.equals("Khanyisa18"))
                    {
                        anim.setDuration(50);
                        anim.start();
                        dialog.setContentView(R.layout.send_fragment);
                        ImageButton back = (ImageButton)dialog.findViewById(R.id.left_arrow_message);
                        back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        Spinner spinner = (Spinner)dialog.findViewById(R.id.spinner);
                        ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(dialog.getContext(),R.array.urgent,android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                urgent = adapterView.getItemAtPosition(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                urgent = adapterView.getItemAtPosition(0).toString();
                            }
                        });
                        final TextView offline = (TextView)dialog.findViewById(R.id.network_state_message);
                        final EditText messageMessage =(EditText)dialog.findViewById(R.id.input_message);
                        final EditText fromMessage =(EditText)dialog.findViewById(R.id.from_message);
                        final  EditText subjectMessage =(EditText)dialog.findViewById(R.id.subject_message);
                        AppCompatButton send = (AppCompatButton)dialog.findViewById(R.id.btn_send);

                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                                if(isConnected)
                                {

                                    final String insertUrl = "http://dms.plattdriveprimary.co.za/sendmessage.php";
                                    final String from,subject,message;
                                    from = fromMessage.getText().toString();
                                    subject = subjectMessage.getText().toString();
                                    message = messageMessage.getText().toString();
                                    dialogprogress = ProgressDialog.show(dialog.getContext(), "", "Sending message...", true);

                                    new Thread(new Runnable() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {

                                                }
                                            });


                                        }
                                    }).start();
                                    StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response) {
                                            dialogprogress.dismiss();
                                            offline.setText("Message has been sent!");

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            offline.setText("error while sending a message!");
                                            textAnim.setDuration(50);
                                            textAnim.setStartOffset(20);
                                            textAnim.setRepeatMode(Animation.REVERSE);
                                            textAnim.setRepeatCount(3);
                                            offline.startAnimation(textAnim);
                                        }

                                    }) {
                                        @Override
                                        protected Map<String,String> getParams(){
                                            Map<String,String> parameters = new HashMap<String,String>();
                                            parameters.put("from", from);
                                            parameters.put("subject", subject);
                                            parameters.put("message", message);
                                            parameters.put("urgent", urgent);
                                            return parameters;
                                        }
                                    };
                                    request.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    requestQueue = Volley.newRequestQueue(getApplicationContext( ));
                                    requestQueue.add(request);

                                }
                                else
                                {
                                    ImageView network = (ImageView)dialog.findViewById(R.id.offline_message);
                                    network.setBackgroundResource(R.drawable.offline);
                                    offline.setText("No internet Connection!");
                                    textAnim.setDuration(50);
                                    textAnim.setStartOffset(20);
                                    textAnim.setRepeatMode(Animation.REVERSE);
                                    textAnim.setRepeatCount(3);
                                    network.startAnimation(textAnim);
                                    offline.startAnimation(textAnim);
                                }
                            }
                        });

                    }
                    else
                    {
                        TextView textView = (TextView)dialog.findViewById(R.id.error);
                        textView.setText("Incorrect Username or Password. Please try again or later!");
                        textAnim.setDuration(50);
                        textAnim.setStartOffset(20);
                        textAnim.setRepeatMode(Animation.REVERSE);
                        textAnim.setRepeatCount(3);
                        textView.startAnimation(textAnim);
                    }
                }
            });
            dialog.show();
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
                allItems.add(new ItemObject(data.getInt(0),data.getString(1),data.getString(4),"",data.getString(2),data.getString(3),"",data.getString(5)));
            }
            while(data.moveToNext());
        }
        return allItems;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            filepath = data.getData();
            choose.setText(""+filepath);
        }
    }

    public static String getRealPath(Context context,Uri contentUri)
    {
       String[] proj = {MediaStore.Images.Media.DATA};
        String result =null;
        CursorLoader cursorLoader = new CursorLoader(context,contentUri,proj,null,null,null);
        Cursor cursor = cursorLoader.loadInBackground();
        if(cursor!=null)
        {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }
    public void uploadMultipart()
    {

        String name = choose.getText().toString().trim();
        String path = getRealPath(getApplicationContext(),filepath);
        uploadFilePath=path;
        if(path == null)
        {

        }
        else
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
            if(isConnected)
            {
                try
                {
                    String uploadId = UUID.randomUUID().toString();
                    new MultipartUploadRequest(getApplicationContext(),UPLOAD_URL)
                            .addFileToUpload(path,"pdf")
                            .addParameter("name",name)
                            .setMethod("POST")
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();

                }
                catch (Exception ex)
                {

                }
            }
            else
            {

            }

        }
    }
    public int uploadFile(String sourceFileUri,String sender,String title,String desc) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
       /* File sourceFile = new File(Environment.getDataDirectory().getAbsolutePath(),sourceFileUri);

        if (!sourceFile.isFile()) {

            dialogprogress.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath);

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("Source File not exist :"+uploadFilePath );
                }
            });

            return 0;

        }
        else
        {*/
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(new File(sourceFileUri));
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; sender=\"sender\"; title=\"title\"; desc=\"desc\"; name=\"uploaded_file\";filename=\""
                                + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.";

                            messageText.setText(msg);
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialogprogress.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(getApplicationContext(), "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialogprogress.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(getApplicationContext(), "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Exception : " ,"Upload file to server Exception"+ e.getMessage(), e);
            }
            dialogprogress.dismiss();
            return serverResponseCode;

        } // End else block
    //}
    public static boolean isAppActive()
    {
        return active;
    }
    public static void newMessage()
    {
        MenuItem messages = menus.getItem(R.id.action_messages);
        messages.setIcon(R.drawable.newmessages);
        LayoutInflater inflater = LayoutInflater.from(context);
        ImageView iv = (ImageView)inflater.inflate(R.layout.blinkmessage,null);
        Animation anim = AnimationUtils.loadAnimation(context,R.anim.blinkanim);
        iv.startAnimation(anim);
        messages.setActionView(iv);
    }

}
