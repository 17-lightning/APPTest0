package com.example.test0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    private Button buttontest;

    String ip = null;int lv = 0;
    String id = null;String pw = null;
    Text text = new Text();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text.flag = false;
        //对应ip地址的可编辑文本框相关代码
        etip = findViewById(R.id.et0);
        etip.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("edittext: ", charSequence.toString());
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
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
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
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
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
                Thread t = new Loginconnect(ip,id,pw,text);t.start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(text.flag == false) {Toast.makeText(MainActivity.this,"连接超时，请检查网络状况或ip地址",Toast.LENGTH_LONG).show();btnlg.setText("登录");}
                else if(text.s.equals("loginfail#idWrong")) {text.flag = false;Toast.makeText(MainActivity.this,"找不到该账号",Toast.LENGTH_LONG).show();btnlg.setText("登录");etid.setBackgroundResource(R.drawable.hongsemiaobian);text.s=null;}
                else if(text.s.equals("loginfail#passwordWrong")) {text.flag = false;Toast.makeText(MainActivity.this,"密码错误，请重新输入",Toast.LENGTH_LONG).show();btnlg.setText("登录");etpw.setBackgroundResource(R.drawable.hongsemiaobian);text.s=null;}
                else  {System.out.println(text.s);lv=Integer.valueOf(text.s.split("#")[1]);Toast.makeText(MainActivity.this,"登录成功，您的权限等级是"+lv,Toast.LENGTH_LONG).show();Intent intent = new Intent(MainActivity.this,Main2Activity.class);intent.putExtra("level",lv);intent.putExtra("id",id);intent.putExtra("ip",ip);startActivity(intent);}
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
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);intent.putExtra("level",100);intent.putExtra("id","testuser");intent.putExtra("ip","192.168.1.2");startActivity(intent);
            }
        });


    }
}
//运行此类将会把账号和密码发送给对应ip地址，把返回值装入text中
class Loginconnect extends Thread{
    String id = null;String pw = null;
    Socket socket;String ip = null;
    Text t = null;

    Loginconnect(String ip,String id,String pw,Text t){
        this.ip = ip;this.id = id;this.pw = pw;this.t = t;
    }

    public void run(){
        try{
            socket = new Socket(ip,12000);
            t.flag = true;
            PrintWriter pwr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            pwr.println("loginquery#"+id+"#"+pw);
            pwr.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            t.s = br.readLine();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
