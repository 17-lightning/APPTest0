package com.example.test0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    private Button btnio;
    private Button btnqy;
    private Button btnfp;
    private Button btnex;
    private TextView txhello;
    int lv;
    String id;
    String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        Intent intentme = getIntent();
        System.out.println(intentme.getStringExtra("level"));
        lv = intentme.getIntExtra("level",0);
        id = intentme.getStringExtra("id");
        ip = intentme.getStringExtra("ip");

        txhello = findViewById(R.id.txhello);
        txhello.setText("Hello "+id+",您的权限等级是"+lv);

        btnio = findViewById(R.id.btnio);
        btnio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lv>0) {
                    Intent intent = new Intent(Main2Activity.this, ExchangeActivity.class);
                    intent.putExtra("level",lv);
                    intent.putExtra("ip",ip);
                    intent.putExtra("id",id);
                    startActivity(intent);

                }
                else {
                    Toast.makeText(Main2Activity.this,"您的权限不足，不能够查询库存资料",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnqy = findViewById(R.id.btnqy);
        btnqy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this,QueryActivity.class);
                intent.putExtra("level",lv);
                startActivity(intent);
            }
        });

        btnfp = findViewById(R.id.btnfp);
        btnfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this,TaskArrangeActivity.class);
                intent.putExtra("level",lv);
                startActivity(intent);
            }
        });

        btnex = findViewById(R.id.btnex);
        btnex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Main2Activity.this,"其他功能正在开发中",Toast.LENGTH_LONG).show();
            }
        });
    }
}
