package com.example.test0;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Button btnlg;
    private Button btnrg;
    private EditText etip;
    private EditText etid;
    private EditText etpw;
    private TextView txip;
    private TextView txid;
    private TextView txpw;

    private boolean idRed = false;
    private boolean pwRed = false;

    private Button buttontest;

    String ip = null;int lv = 0;
    String id = null;String pw = null;
    Text text = new Text();

    Looper looper = null;
    ConnectHandler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        looper = Looper.myLooper();
        handler = new ConnectHandler(looper);

        txip = findViewById(R.id.tx2);
        txid = findViewById(R.id.tx3);
        txpw = findViewById(R.id.tx4);

        //对应ip地址的可编辑文本框相关代码
        etip = findViewById(R.id.et0);
        etip.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                ip = editable.toString();
                System.out.println("输入的ip地址是"+ip);
            }
        });

        //对应账号输入的可编辑文本框相关代码
        etid = findViewById(R.id.et1);
        etid.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(idRed){
                    txid.setTextColor(Color.rgb(128,128,128));
                    txid.setText("账号：");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                id = editable.toString();System.out.println("输入的账号是"+id);
            }
        });

        //对应密码输入的可编辑文本框相关代码
        etpw = findViewById(R.id.et2);
        etpw.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(pwRed){
                    txpw.setTextColor(Color.rgb(128,128,128));
                    txpw.setText("账号：");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                pw = editable.toString();System.out.println("输入的密码是"+pw);
            }
        });

        //对应登陆按钮按下，登录按钮变为登录中，创建网络进程，等待1s，然后根据网络进程的结果决定是进入下一个界面还是报告错误
        btnlg = findViewById(R.id.btnlg);
        btnlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnlg.setText("登录中");etid.setBackgroundResource(R.drawable.huisemiaobian);etpw.setBackgroundResource(R.drawable.huisemiaobian);
                Thread t = new Loginconnect(ip,id,pw,text,handler);t.start();
                }
        });

        btnrg = findViewById(R.id.btnrg);
        btnrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        buttontest = findViewById(R.id.button9);
        buttontest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);intent.putExtra("level",100);intent.putExtra("id","testuser");intent.putExtra("ip",ip);startActivity(intent);
            }
        });



    }



    class IdWrong implements Runnable{
        public void run(){
            etid.setBackgroundResource(R.drawable.hongsemiaobian);
            txid.setTextColor(Color.rgb(255,0,0));
            txid.setText("该账号不存在");
            idRed = true;
        }
    }

    class PwWrong implements Runnable{
        public void run(){
            etpw.setBackgroundResource(R.drawable.hongsemiaobian);
            txpw.setTextColor(Color.rgb(255,0,0));
            txpw.setText("密码错误");
            pwRed = true;
        }
    }

    class NetDelayed implements Runnable{
        public void run(){
            Toast.makeText(MainActivity.this,"检查到网络延迟\n可能是网络状况不佳或ip地址输入错误",Toast.LENGTH_LONG).show();
        }
    }

    class LoginSuccess implements Runnable{
        String id;
        String lv;
        public LoginSuccess(String id,String lv){
            this.id = id;this.lv = lv;
        }
        public void run(){
            Intent intent = new Intent(MainActivity.this,Main2Activity.class);
            intent.putExtra("level",Integer.valueOf(lv));
            intent.putExtra("id",id);
            intent.putExtra("ip",ip);
            startActivity(intent);
        }
    }

    class LoginOver implements Runnable{
        public void run(){
            btnlg.setText("登录");
        }
    }


    class ConnectHandler extends Handler{

        public ConnectHandler(Looper looper){
            super(looper);
        }

        public void handleMessage(Message message){
            switch (message.what){
                case 10000:
                {
                    System.out.println("接收到10000号指令");
                }
            }
        }
    }

    //运行此类将会把账号和密码发送给对应ip地址，把返回值装入text中
    class Loginconnect extends Thread{
        String id = null;String pw = null;
        Socket socket;String ip = null;
        Text t = null;

        ConnectHandler handler = null;

        Loginconnect(String ip, String id, String pw, Text t, ConnectHandler handler){
            this.ip = ip;this.id = id;this.pw = pw;this.t = t;this.handler = handler;
        }

        public void run(){
            try{
                t.flag = false;
                new Thread(new DelayTimer(t,handler)).start();
                socket = new Socket(ip,57798);
                t.flag = true;
                if(id.equals("")) {handler.post(new IdWrong());return;}
                if(pw.equals("")) {handler.post(new PwWrong());return;}
                PrintWriter pwr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                pwr.println("login#"+id+"#"+pw);
                pwr.flush();
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String str = br.readLine();
                if(str.split("#")[1].equals("success")) handler.post(new LoginSuccess(id,str.split("#")[2]));
                else if(str.equals("login#error")) handler.post(new IdWrong());
                else if(str.equals("login#wrong")) handler.post(new PwWrong());
                handler.post(new LoginOver());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    class DelayTimer extends Thread{
        Text t = null;ConnectHandler handler = null;
        public DelayTimer(Text t,ConnectHandler handler){
            this.t = t;this.handler = handler;
        }
        public void run(){
            try{
                Thread.sleep(1000);
                if(t.flag == false){
                    handler.post(new NetDelayed());
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}


