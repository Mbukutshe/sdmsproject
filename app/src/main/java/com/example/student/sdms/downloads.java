package com.example.student.sdms;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Student on 9/5/2016.
 */
public class downloads extends Fragment{

    View myView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ImageView imageView;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.downloads,container,false);

        mRecyclerView = (RecyclerView)myView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        List<ItemObject> myDataset = getAllItemList();


        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        MobileAds.initialize(this.getContext(),getString(R.string.ads_unit_id));
        AdView adView=(AdView)myView.findViewById(R.id.addView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        mAdapter = new MyAdapter(this.getContext(),myDataset);
        mRecyclerView.setAdapter(mAdapter);
        return myView;
    }
    private List<ItemObject> getAllItemList(){

        List<ItemObject> allItems = new ArrayList<>();
        Cursor data = new SqliteController(this.getContext()).getAllMessages();
        if(data.moveToFirst())
        {
            do
            {
                allItems.add(new ItemObject(data.getString(1),data.getString(2),data.getString(3),data.getString(4),data.getString(5),data.getString(6)));
            }
            while(data.moveToNext());
        }
        return allItems;
    }
}
