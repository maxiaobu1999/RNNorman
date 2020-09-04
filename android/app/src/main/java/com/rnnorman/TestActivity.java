package com.rnnorman;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        String json = bundle.getString("data");
        Toast.makeText(this,json,Toast.LENGTH_LONG).show();
    }
}
