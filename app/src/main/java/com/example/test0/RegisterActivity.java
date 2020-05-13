package com.example.test0;

import androidx.appcompat.app.AppCompatActivity;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RegisterActivity extends AppCompatActivity {

    private Button btnrg;
    private Button btnsc;
    private Button btnfp;
    private Button btnex;
    private EditText etip;
    private EditText etid;
    private EditText etpw;
    private EditText etpw2;
    private TextView txatt;

    String ip = "";
    String id = "";
    String password = "";
    String password2 = "";
    Looper looper = null;
    HandlerMe handler = null;
    Boolean attshow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intentme = getIntent();

        looper = Looper.myLooper();
        handler = new HandlerMe(looper);

        txatt = findViewById(R.id.textView11);

        btnrg = findViewById(R.id.button10);
        btnrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("注册按钮被点击");
                if(attshow == true){
                    txatt.setText(" ");
                    txatt.setTextColor(Color.rgb(127,127,127));
                    attshow = false;
                }
                try{
                    int i = 0;
                    while(i<4){
                        System.out.println(ip.split("\\.")[0]);
                        Double temp = Double.valueOf(ip.split("\\.")[0]);
                        if(temp>255||temp<0) {
                            txatt.setText("请输入正确的ip地址");
                            txatt.setTextColor(Color.rgb(255,0,0));
                            attshow = true;
                            return;
                        }i++;
                    }
                }catch(Exception e){
                    txatt.setText("请输入正确的ip地址");
                    txatt.setTextColor(Color.rgb(255,0,0));
                    attshow = true;
                    return;
                }
                System.out.println("检查ip地址结束");
                if(id.equals("")){
                    txatt.setText("账号不能为空");
                    attshow = true;
                    return;
                }else if(password.equals("")){
                    txatt.setText("密码不能为空");
                    attshow = true;
                    return;
                }else if(!password.equals(password2)){
                    txatt.setText("两次输入的密码不一致");
                    attshow = true;
                    return;
                }
                new Thread(new RegisterConnect()).start();
                btnrg.setText("正在注册中");
            }
        });

        etip = findViewById(R.id.editText6);
        etip.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                ip = editable.toString();
            }
        });

        etid = findViewById(R.id.editText1);
        etid.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                id = editable.toString();
            }
        });

        etpw = findViewById(R.id.editText5);
        etpw.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                password = editable.toString();
            }
        });

        etpw2 = findViewById(R.id.editText2);
        etpw2.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                password2 = editable.toString();
            }
        });

    }

    public class RegisterConnect extends Thread{
        public void run(){
            try{
                Text t = new Text();
                new Thread(new DelayTimer(t)).start();
                Socket socket = new Socket(ip,12000);
                t.flag = true;
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.println("register#"+id+"#"+password);
                pw.flush();
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String str = br.readLine();
                if(str.equals("register#success")){
                    handler.removeMessages(0);
                    Message message = handler.obtainMessage(99,0,0,"null");
                    handler.sendMessage(message);
                }else if(str.equals("register#idexist")){
                    handler.removeMessages(0);
                    Message message = handler.obtainMessage(100,0,0,"该账号已存在");
                    handler.sendMessage(message);
                }
            }catch (Exception e){
                e.printStackTrace();
                handler.removeMessages(0);
                Message message = handler.obtainMessage(101,0,0,"网络连接出现异常\n请检查IP地址和网络状况");
                handler.sendMessage(message);
            }
        }
    }

    public class DelayTimer extends Thread{
        Text t = null;
        public DelayTimer(Text t){
            this.t = t;
        }
        public void run(){
            try{
                Thread.sleep(1000);
                if(t.flag == false){
                    handler.removeMessages(0);
                    Message message = handler.obtainMessage(100,0,0,"检测到网络延时\n请检查网络状况或IP地址");
                    handler.sendMessage(message);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public class HandlerMe extends Handler{
        public HandlerMe(Looper looper){
            super(looper);
        }
        public void handleMessage(Message message){
            switch (message.what){
                case 99:
                    Intent intent = new Intent(RegisterActivity.this,Main2Activity.class);
                    intent.putExtra("level",1);
                    intent.putExtra("id",id);
                    intent.putExtra("ip",ip);
                    startActivity(intent);
                    break;
                case 100:
                    txatt.setText(message.obj.toString());
                    txatt.setTextColor(Color.rgb(255,0,0));
                    attshow = true;
                    Toast.makeText(RegisterActivity.this,message.obj.toString(),Toast.LENGTH_LONG).show();
                    btnrg.setText("注册账号");
                    break;
                case 101:
                    txatt.setText(message.obj.toString());
                    txatt.setTextColor(Color.rgb(255,0,0));
                    attshow = true;
                    btnrg.setText("注册账号");
            }
        }
    }
}
