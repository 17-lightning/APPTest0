package com.example.test0;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class QueryActivity extends AppCompatActivity {

    private EditText query_e1;
    private EditText query_e2;
    private String goodsname,date;
    private Button q_btn;
    private PieChart pieChart;
    private HashMap dataMap;
    private PopupWindow pop;
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
        setContentView(R.layout.activity_query);

        Intent intentme = getIntent();
        lv = intentme.getIntExtra("level",0);
        id = intentme.getStringExtra("id");
        ip = intentme.getStringExtra("ip");
        //创建looper和handler
        looper = Looper.myLooper();
        final ListView listView = new ListView(QueryActivity.this);//创建列表窗口
        myHandler = new myHandler(looper,listView);
        new Thread(new SumConnect()).start();

        query_e1 = findViewById(R.id.qedtx1);
        query_e1.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                //每次可编辑文本框中的内容变化，就更新list列表中的数据
                goodsname = editable.toString();System.out.println("选择的货物是:"+goodsname);
            }
        });
        query_e2 = findViewById(R.id.qedtx2);
        query_e2.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                //每次可编辑文本框中的内容变化，就更新list列表中的数据
                date = editable.toString();System.out.println("选择的日期是:"+date);
            }
        });
        q_btn = findViewById(R.id.button_query);
        q_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goodsname.length()!=0 && date.length()!=0){
                    if(goodsname.equalsIgnoreCase("0")){
                        t.flag2 = true; //0表示所有货物销量统计，用flag2标记
                    }
                    t.s = "Calculate&"+date+"&"+goodsname;
                    t.flag = true;
                    goodsname = ""; date="";
                }
            }
        });

        pieChart=(PieChart)findViewById(R.id.pie_chart);
        //PieChartUtil.getPitChart().setPieChart(pieChart,dataMap,"水质",true);

        //点击事件
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pieEntry=(PieEntry)e;
                pieChart.setCenterText(pieEntry.getLabel());
            }

            @Override
            public void onNothingSelected() {

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
                Socket socket = new Socket(ip,57798);//建立Socket连接
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
                        if(str.split("&")[0].equals("Calculate")){
                            myHandler.removeMessages(0);
                            Message message = myHandler.obtainMessage(1011,0,0,str);
                            myHandler.sendMessage(message);
                        }else{
                            myHandler.removeMessages(0);
                            Message message = myHandler.obtainMessage(10086,0,0,"异常信息"+str);
                            myHandler.sendMessage(message);
                        }
                    }
                    else{

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

        public ListView listView;

        public myHandler(Looper looper, ListView listView){
            super(looper);this.listView = listView;
        }

        public void handleMessage(Message message){
            switch (message.what){
                //弹出进出货失败警告框
                case 10003:
                    Toast.makeText(QueryActivity.this,"检查到网络延迟\n网络状况不佳",Toast.LENGTH_LONG).show();
                    break;
                case 10086:
                    Toast.makeText(QueryActivity.this,message.obj.toString(),Toast.LENGTH_LONG).show();
                    break;
                case 1011:
                    //
                    String str = message.obj.toString();
                    System.out.println("str:"+str);
                    dataMap=new HashMap();
                    if(t.flag2){
                        int i = 1;
                        while(i<=6){
                            String temp = str.split("&")[i];
                            dataMap.put("Goods"+i,temp);
                            i++;
                        }
                        t.flag2 = false;
                    }
                    else{  //仅发送了某种货物的销量
                        String temp = str.split("&")[1];
                        dataMap.put("该货物销量为"+temp,temp);
                    }

                    PieChartUtil.getPitChart().setPieChart(pieChart,dataMap,"销量统计",true);
                    break;
            }
        }

    }

}
