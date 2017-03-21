package com.example.student.sdms;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Student on 9/5/2016.
 */
public class about extends Fragment{

    View myView;
    private WebView webview;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.about,container,false);
        webview = (WebView)myView.findViewById(R.id.website);
        webview.loadUrl("http://www.plattdriveprimary.co.za");
        webview.requestFocus();
        return myView;
    }
}
