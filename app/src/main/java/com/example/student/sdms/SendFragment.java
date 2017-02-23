package com.example.student.sdms;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;


public class SendFragment extends Fragment
{
    View myView;
    WebView webView;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.send_fragment,container,false);
        webView =(WebView)myView.findViewById(R.id.send_web_view);
        MobileAds.initialize(this.getContext(),getString(R.string.ads_unit_id));
        AdView adView=(AdView)myView.findViewById(R.id.addView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        String url = "http://sdms.portfolioonline.co.za/mobilemessage";
        if(webView!=null)
        {
            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
        }
        return myView;
    }
}
