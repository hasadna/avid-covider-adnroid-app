package com.map.uri.corona;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Button setupbtn=findViewById(R.id.setupBtn);
        setupbtn.setOnClickListener(this);

        WebView wv= findViewById(R.id.wv);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("https://avid-covider.phonaris.com/");
    }

    @Override
    public void onClick(View v) {
        SetupDialog su = new SetupDialog(this);
        su.show();
    }
}
