package com.example.test0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    private Button btnio;
    private Button btnsc;
    private Button btnfp;
    private Button btnex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnio = findViewById(R.id.btnio);


    }
}
