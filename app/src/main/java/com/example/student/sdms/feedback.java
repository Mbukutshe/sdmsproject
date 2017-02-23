package com.example.student.sdms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Student on 9/5/2016.
 */
public class feedback extends Fragment{

    View myView;
    Button button;
    FloatingActionButton fab;
    EditText name,email,phone,message;
    String nam,mail,cell,text;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.feedback,container,false);
        button = (Button)myView.findViewById(R.id.submit);
        fab = (FloatingActionButton)myView.findViewById(R.id.fab);
        name = (EditText)myView.findViewById(R.id.name);
        email = (EditText)myView.findViewById(R.id.email);
        phone = (EditText)myView.findViewById(R.id.phone);
        message = (EditText)myView.findViewById(R.id.message);

       // fab.hide();
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                nam = name.getText().toString();
                mail = email.getText().toString();
                cell = phone.getText().toString();
                text = message.getText().toString();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"wisemanmbukutshe@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback from SDMS");
                i.putExtra(Intent.EXTRA_TEXT   , "Name: \t"+nam+"\nE-mail: \t"+mail+"\nPhone No: \t"+cell+"\n\n"+text);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(myView.getContext(),"There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return myView;
    }
}
