package com.example.test0;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SignalActivity extends AppCompatActivity {

    //元件匹配
    private EditText et;
    private Switch sw;
    private TextView tv;
    private Button btn;
    private Button btnrt;
    //变量
    private HashMap<String,String> map = new HashMap<String, String>();
    private HashMap<String,String> map2 = new HashMap<String, String>();
    private List<String> list = new ArrayList<String>();
    private String id;
    private String ip;
    private int lv;
    private Text t = new Text();
    private boolean flag = false;//switch开关
    private boolean flag2 = true;//仅用于初始的update
    private boolean flag3 = false;
    private boolean endFlag = false;
    //弹出窗口准备（下拉框）
    private PopupWindow pop;//弹出窗口
    private DropdownAdapter adapter;//适配器
    private View layout;//布局文件
    //多线程相应器
    private Looper looper;
    private myHandler myHandler;


    protected void onCreate(Bundle savedInstanceState){
        //通用开头
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal);
        //元件匹配
        et=findViewById(R.id.editText7);
        sw=findViewById(R.id.switch1);
        tv=findViewById(R.id.textView23);
        btn=findViewById(R.id.button18);
        btnrt=findViewById(R.id.button19);
        //初始化

        adapter = new DropdownAdapter(SignalActivity.this, list);//创建一个适配器
        final ListView listView = new ListView(SignalActivity.this);//创建列表窗口
        listView.setAdapter(adapter);//将适配器装入列表窗口中
        layout = findViewById(R.id.view2);

        looper = Looper.myLooper();
        myHandler = new myHandler(looper,listView);

        Intent intentme = getIntent();
        lv = intentme.getIntExtra("level",0);
        id = intentme.getStringExtra("id");
        ip = intentme.getStringExtra("ip");

        new Thread(new SumConnect(t)).start();

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {flag = true;sw.setText("只查看当前记录");}
                else {flag = false;sw.setText("只查看历史记录");}
                if(pop!=null) if(pop.isShowing()) pop.dismiss();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag3){
                    t.flag=true;
                }
            }
        });

        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag2==true) {
                    t.s = "update4#4";
                    t.flag = true;
                    flag2=false;
                }
                else {
                    myHandler.removeMessages(0);
                    Message message = myHandler.obtainMessage(7798,0,0,"null");
                    myHandler.sendMessage(message);
                    /*
                    if (pop == null) {
                        pop = new PopupWindow(listView, layout.getWidth(), 4 * layout.getHeight());
                        pop.showAsDropDown(layout);
                    } else {
                        if (pop.isShowing()) {
                            pop.dismiss();
                        } else {
                            pop.showAsDropDown(layout);
                        }
                    }*/
                }
            }
        });

        et.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                //每次可编辑文本框中的内容变化，就更新list列表中的数据
                /*goal = editable.toString();System.out.println("需要查询的货物是:"+goal);
                ChangeList(list);*/
            }
        });


    }

    class DropdownAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;
        private List<String> list;
        private TextView content;
        private ImageButton close;

        //构造方法，用于获取当前context背景和列表
        public DropdownAdapter(Context context, List<String> list) {
            this.context = context;
            this.list = list;
        }

        //获取长度
        public int getCount() {return list.size(); }//为什么列表中会有多个list_row，就是在这里得知的

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            layoutInflater = LayoutInflater.from(context);//创建一个布局解析器
            convertView = layoutInflater.inflate(R.layout.list_row, null);//将布局转化成View窗口
            convertView.setAlpha(1);//不透明！
            close = (ImageButton)convertView.findViewById(R.id.close_row);//其中的按钮与元件对应关系
            content = (TextView)convertView.findViewById(R.id.text_row);
            final String editContent = list.get(position);//根据位置，从列表中获取对应的元素
            content.setText(list.get(position).toString());//设置文本框的内容为获取到的元素
            //触摸时，将会把下拉列表中触摸的那个元素填入可编辑文本框中，并消灭下拉框(已经被修改，现在仅单击触发）
            content.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    et.setText(editContent);
                    pop.dismiss();
                    if(flag){
                        if(map.containsKey(editContent)){
                            tv.setText(map.get(editContent));
                            t.s="doit#"+editContent;
                            if(map.get(editContent).equals("")||map.get(editContent).equals("已开始处理")){
                                btn.setText("----");
                            }else{
                                flag3=true;
                                btn.setText("提前处理");
                            }
                        }else{
                            btn.setText("----");
                            tv.setText("此记录为空");
                            flag3=false;
                        }
                    }else{
                        btn.setText("----");
                        if(map2.containsKey(editContent)){
                            tv.setText(map2.get(editContent));
                            flag3=true;
                            t.s="doit#"+editContent;
                        }else{
                            tv.setText("此记录为空");
                            flag3=false;
                        }
                    }
                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    et.setText(editContent);
                    pop.dismiss();
                }
            });
            return convertView;
        }
    }

    public class SumConnect implements Runnable{

        public Text t;

        public SumConnect(Text t){
            this.t=t;
        }

        public void run(){
            try {
                Text t0 = new Text();//t0是为了判断网络延时而设置的参照物
                t0.flag = false;//设置t0为false
                Thread.sleep(1000);
                System.out.println("启动延时器");
                new Thread(new DelayTimer(t0)).start();//启动延时器，若1s后连接没有建立，认为发生了网络延时，弹出警告框
                Socket socket = new Socket(ip, 57798);//建立Socket连接
                PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));//创建输出流
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));//创建文本输入流
                DataInputStream dis = new DataInputStream(socket.getInputStream());//创建图片输入流
                t0.flag = true;
                System.out.println("网络连接建立成功");
                while (endFlag == false) {
                    //进行一次网络检查
                    Thread.sleep(500);//每500ms判断一次是否需要网络输出，如果有输出的信号，则发送这行信息并接收服务器端的一行信息
                    if (t.flag == true) {//t.flag = true说明有需要发送的信号
                        System.out.println("检测到有需要输出的消息");
                        t.flag = false;//标记发送信号已收到
                        System.out.println("清除发送信号");
                        pw.println(t.s);//发送
                        pw.flush();
                        System.out.println("发送数据：" + t.s);
                        String str = br.readLine();
                        System.out.println("接收到信息："+str);
                        if(!str.contains("#")) {
                            int i = 1;
                            while (!str.split("&")[i].equals("end")) {
                                String temp = str.split("&")[i];
                                String name = temp.split("\\|")[0];//txt文件的名字
                                if (name.split("-").length >= 4) {//未完成的文件是VOID-4-TaskList.txt,仅有2个-，因此-更多表示这是一个已完成的文件
                                    int j = 1;
                                    StringBuffer sb = new StringBuffer();
                                    while (!temp.split("\\|")[j].equals("end")) {
                                        sb.append(temp.split("\\|")[j] + "\n");
                                        j++;
                                    }
                                    if (j == 1) map2.put(name, "本文件为空");
                                    else map2.put(name, sb.toString());
                                } else {
                                    int j = 1;
                                    StringBuffer sb = new StringBuffer();
                                    while (!temp.split("\\|")[j].equals("end")) {
                                        sb.append(temp.split("\\|")[j] + "\n");
                                        j++;
                                    }
                                    if (j == 1) map.put(name, "本文件为空");
                                    else map.put(name, sb.toString());
                                }
                                i++;
                            }
                            et.setText("");
                            myHandler.removeMessages(0);
                            Message message = myHandler.obtainMessage(7798, 0, 0, "null");
                            myHandler.sendMessage(message);
                        }else{
                            tv.setText("已开始处理");
                            if(map.containsKey(t.s)){
                                map.put(t.s,"已开始处理");
                            }else if(map2.containsKey(t.s)){
                                map2.put(t.s,"已开始处理");
                            }
                        }
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

    //处理器，用于线程交互
    public class myHandler extends Handler {

        public ListView listView;

        public myHandler(Looper looper, ListView listView) {
            super(looper);
            this.listView = listView;
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 10003:
                    Toast.makeText(SignalActivity.this,"检查到网络延迟\n网络状况不佳",Toast.LENGTH_LONG).show();
                    break;
                case 7798:
                    System.out.println("接收到7798的信息");
                    if(flag) {
                        list.clear();
                        Iterator<String> iter = map.keySet().iterator();
                        while(iter.hasNext()){
                            list.add(iter.next());
                        }
                    }else{
                        list.clear();
                        Iterator<String> iter = map2.keySet().iterator();
                        while(iter.hasNext()){
                            String str = iter.next();
                            list.add(str);
                        }
                    }
                    System.out.println("show me the adapter,此时list长度为"+list.size());
                    if (pop == null) {
                        pop = new PopupWindow(listView, layout.getWidth(), 4 * layout.getHeight());
                        pop.showAsDropDown(layout);
                    } else {
                        if (pop.isShowing()) {
                            pop.dismiss();
                        } else {
                            pop.showAsDropDown(layout);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
