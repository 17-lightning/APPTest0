package com.example.test0;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class OtherActivity extends AppCompatActivity {

    private TextView[] tv;
    private Button btns1;

    //继承模块
    private String id;
    private String ip;
    private int lv;
    //多线程处理器
    private Looper looper;
    private myHandler myHandler;
    private Text t = new Text();
    private boolean EndFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        Intent intentme = getIntent();
        lv = intentme.getIntExtra("level",0);
        id = intentme.getStringExtra("id");
        ip = intentme.getStringExtra("ip");
        //创建looper和handler
        looper = Looper.myLooper();
        final ListView listView = new ListView(OtherActivity.this);//创建列表窗口
        myHandler = new myHandler(looper);
        tv = new TextView[10];
        tv[0] = findViewById(R.id.pos12);
        tv[1] = findViewById(R.id.pos22);
        tv[2] = findViewById(R.id.pos32);
        tv[3] = findViewById(R.id.pos42);
        tv[4] = findViewById(R.id.pos52);
        tv[5] = findViewById(R.id.pos62);
        tv[6] = findViewById(R.id.pos72);
        new Thread(new SumConnect()).start();

        btns1 = findViewById(R.id.btnsell);
        btns1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.s = "salepredict";
                System.out.println(t.s);
                t.flag = true;
            }
        });

    }
    protected void onDestroy(){
        super.onDestroy();
        EndFlag = true;
    }

    class SumConnect extends Thread{

        public void run(){
            try{
                Text t0 = new Text();
                t0.flag=false;
                Thread.sleep(1000);
                System.out.println("启动延时器");
                new Thread(new DelayTimer(t0)).start();//启动延时器，若1s后连接没有建立，认为发生了网络延时，弹出警告框
                Socket socket = new Socket(ip, 57798);//建立Socket连接
                PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));//创建输出流
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));//创建文本输入流
                t0.flag = true;
                System.out.println("网络连接建立成功");
                while(EndFlag!=true){
                    Thread.sleep(500);//每500ms检测一次有无需要发送的信息
                    if(t.flag==true){
                        System.out.println("检测到有需要输出的消息");
                        t.flag = false;//标记发送信号已收到
                        System.out.println("清除发送信号");
                        pw.println(t.s);//发送
                        pw.flush();
                        System.out.println("发送数据："+t.s);
                        String str = br.readLine();//接收一行服务器返回的数据
//                        InputStream in = socket.getInputStream();
//                        byte[] bytes = new byte[1024];
//                        int len = in.read(bytes);
//                        String str = new String(bytes,0,len);
                        System.out.println("接收到："+str);
                        if(str.split("&")[0].equals("Predict")){
                            myHandler.removeMessages(0);
                            Message message = myHandler.obtainMessage(1010,0,0,str);
                            myHandler.sendMessage(message);
                        }else{
                            myHandler.removeMessages(0);
                            Message message = myHandler.obtainMessage(10086,0,0,"异常信息"+str);
                            myHandler.sendMessage(message);
                        }
                    }else{

                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    class DelayTimer extends Thread{
        Text t = null;
        public DelayTimer(Text t){this.t = t;}

        @Override
        public void run() {
            try{
                Thread.sleep(1000);
            }catch(Exception e){
            }
            if(t.flag == false){
                myHandler.removeMessages(0);
                Message message = myHandler.obtainMessage(10003,0,0,"null");
                myHandler.sendMessage(message);
            }
        }
    }

    public class myHandler extends Handler {

        public myHandler(Looper looper){
            super(looper);;
        }

        public void handleMessage(Message message){
            switch (message.what){
                //弹出进出货失败警告框
                case 10003:
                    Toast.makeText(OtherActivity.this,"检查到网络延迟\n网络状况不佳",Toast.LENGTH_LONG).show();
                    break;
                case 10086:
                    Toast.makeText(OtherActivity.this,message.obj.toString(),Toast.LENGTH_LONG).show();
                    break;
                case 1010:
                    //
                    String str = message.obj.toString();
                    System.out.println("str:"+str);
                    int i = 1;
                    while(i<=6){
                        String temp = str.split("&")[i];
                        tv[i].setText(temp);
                        i++;
                    }
                    tv[0].setBackgroundColor(Color.rgb(255,179,167));
                    break;
            }
        }

    }

}