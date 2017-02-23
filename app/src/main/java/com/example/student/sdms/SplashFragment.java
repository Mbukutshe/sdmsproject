package com.example.student.sdms;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * Created by Student on 10/11/2016.
 */
public class SplashFragment extends FragmentActivity{
    View myView;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.downloads,container,false);
        return myView;
    }
}
