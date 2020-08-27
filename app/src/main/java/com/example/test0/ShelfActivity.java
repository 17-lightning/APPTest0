package com.example.test0;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ShelfActivity extends AppCompatActivity {

    //元件匹配
    private TextView[] tv;
    private TableRow[] tr;
    private Button btnget;
    private ImageButton btn1u;
    private ImageButton btn1d;
    private ImageButton btn2u;
    private ImageButton btn2d;
    private TextView tv1;
    private TextView tv2;
    private Button btnrt;
    private TextView tvnew;
    private TextView tvnew2;
    private TextView tvnew3;
    private ImageView iv;
    private TextView shame;
    private TextView tvold;
    //继承模块
    private String id;
    private String ip;
    private int lv;
    //多线程处理器
    private Looper looper;
    private myHandler myHandler;
    //数据库器件
    private StorageHelper storageHelper = null;//数据库打开器
    private SQLiteDatabase db = null;//数据库本身
    Cursor cursor = null;//遍历器
    //网络通讯模块
    private String aim = "null";
    private int aimx = 0;
    private int aimi = 0;
    private boolean flag = false;
    private Text t = new Text();
    //变量
    private boolean EndFlag = false;
    private HashMap<Integer,String> map1 = new HashMap<Integer,String>();
    private HashMap<Integer,String> map2 = new HashMap<Integer,String>();
    private int red = 0;
    private int oldred = 0;
    private List<Integer> list;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);

        //从上一个页面中得知ip、id和lv信息
        Intent intentme = getIntent();
        lv = intentme.getIntExtra("level",0);
        id = intentme.getStringExtra("id");
        ip = intentme.getStringExtra("ip");
        //创建looper和handler
        looper = Looper.myLooper();
        final ListView listView = new ListView(ShelfActivity.this);//创建列表窗口
        myHandler = new myHandler(looper,listView);
        //创建数据库连接
        storageHelper = new StorageHelper(ShelfActivity.this);
        db = storageHelper.getWritableDatabase();
        //元件匹配
        tv = new TextView[100];
        tr = new TableRow[10];
        try {
            btnrt = findViewById(R.id.button16);
            tvold = findViewById(R.id.textView22);
            shame = findViewById(R.id.textView21);
            tvnew = findViewById(R.id.textView15);
            tvnew2 = findViewById(R.id.textView20);
            iv = findViewById(R.id.imageView);
            btnget = findViewById(R.id.button17);
            btn1u = findViewById(R.id.imageButton3);
            btn1d = findViewById(R.id.imageButton4);
            btn2u = findViewById(R.id.imageButton);
            btn2d = findViewById(R.id.imageButton2);
            tv1 = findViewById(R.id.textView19);
            tv2 = findViewById(R.id.textView12);
            btnrt = findViewById(R.id.button16);
            tv[10] = findViewById(R.id.textView111);
            tv[11] = findViewById(R.id.textView112);
            tv[12] = findViewById(R.id.textView113);
            tv[13] = findViewById(R.id.textView114);
            tv[14] = findViewById(R.id.textView115);
            tv[15] = findViewById(R.id.textView116);
            tv[16] = findViewById(R.id.textView117);
            tv[17] = findViewById(R.id.textView118);
            tv[18] = findViewById(R.id.textView119);
            tv[19] = findViewById(R.id.textView110);
            tv[20] = findViewById(R.id.textView121);
            tv[21] = findViewById(R.id.textView122);
            tv[22] = findViewById(R.id.textView123);
            tv[23] = findViewById(R.id.textView124);
            tv[24] = findViewById(R.id.textView125);
            tv[25] = findViewById(R.id.textView126);
            tv[26] = findViewById(R.id.textView127);
            tv[27] = findViewById(R.id.textView128);
            tv[28] = findViewById(R.id.textView129);
            tv[29] = findViewById(R.id.textView120);
            tv[30] = findViewById(R.id.textView131);
            tv[31] = findViewById(R.id.textView132);
            tv[32] = findViewById(R.id.textView133);
            tv[33] = findViewById(R.id.textView134);
            tv[34] = findViewById(R.id.textView135);
            tv[35] = findViewById(R.id.textView136);
            tv[36] = findViewById(R.id.textView137);
            tv[37] = findViewById(R.id.textView138);
            tv[38] = findViewById(R.id.textView139);
            tv[39] = findViewById(R.id.textView130);
            tv[40] = findViewById(R.id.textView141);
            tv[49] = findViewById(R.id.textView140);
            tv[41] = findViewById(R.id.textView142);
            tv[42] = findViewById(R.id.textView143);
            tv[43] = findViewById(R.id.textView144);
            tv[44] = findViewById(R.id.textView145);
            tv[45] = findViewById(R.id.textView146);
            tv[46] = findViewById(R.id.textView147);
            tv[47] = findViewById(R.id.textView148);
            tv[48] = findViewById(R.id.textView149);
            tv[50] = findViewById(R.id.textView151);
            tv[51] = findViewById(R.id.textView152);
            tv[52] = findViewById(R.id.textView153);
            tv[53] = findViewById(R.id.textView154);
            tv[54] = findViewById(R.id.textView155);
            tv[55] = findViewById(R.id.textView156);
            tv[56] = findViewById(R.id.textView157);
            tv[57] = findViewById(R.id.textView158);
            tv[58] = findViewById(R.id.textView159);
            tv[59] = findViewById(R.id.textView150);
            tv[60] = findViewById(R.id.textView161);
            tv[61] = findViewById(R.id.textView162);
            tv[62] = findViewById(R.id.textView163);
            tv[63] = findViewById(R.id.textView164);
            tv[64] = findViewById(R.id.textView165);
            tv[65] = findViewById(R.id.textView166);
            tv[66] = findViewById(R.id.textView167);
            tv[67] = findViewById(R.id.textView168);
            tv[68] = findViewById(R.id.textView169);
            tv[69] = findViewById(R.id.textView160);
            tv[70] = findViewById(R.id.textView171);
            tv[71] = findViewById(R.id.textView172);
            tv[72] = findViewById(R.id.textView173);
            tv[73] = findViewById(R.id.textView174);
            tv[74] = findViewById(R.id.textView175);
            tv[75] = findViewById(R.id.textView176);
            tv[76] = findViewById(R.id.textView177);
            tv[77] = findViewById(R.id.textView178);
            tv[78] = findViewById(R.id.textView179);
            tv[79] = findViewById(R.id.textView170);
            tv[80] = findViewById(R.id.textView181);
            tv[81] = findViewById(R.id.textView182);
            tv[82] = findViewById(R.id.textView183);
            tv[83] = findViewById(R.id.textView184);
            tv[84] = findViewById(R.id.textView185);
            tv[85] = findViewById(R.id.textView186);
            tv[86] = findViewById(R.id.textView187);
            tv[87] = findViewById(R.id.textView188);
            tv[88] = findViewById(R.id.textView189);
            tv[89] = findViewById(R.id.textView180);
            tr[1] = findViewById(R.id.row1);
            tr[2] = findViewById(R.id.row2);
            tr[3] = findViewById(R.id.row3);
            tr[4] = findViewById(R.id.row4);
            tr[5] = findViewById(R.id.row5);
            tr[6] = findViewById(R.id.row6);
            tr[7] = findViewById(R.id.row7);
            tr[8] = findViewById(R.id.row8);
        }catch(Exception e){
            //80个tv以及其它东西的匹配都在这里，并不会出什么错误，只是太长了想把他收起来而已
            e.printStackTrace();
        }
        //初始化
        new Thread(new SumConnect()).start();
        list = new ArrayList<>();

        //按键：获取
        btnget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==false){
                    t.s="update2#amsyes";
                    t.flag=true;
                }else{
                    if(aim.equals("null")||aimx==0){
                        Toast.makeText(ShelfActivity.this,"错误",Toast.LENGTH_LONG).show();
                    }else{
                        t.s="update3#"+aim.split("-")[0]+"#"+aimx;
                        t.flag=true;
                        shame.setBackgroundResource(R.drawable.nobai);
                        shame.setText("");
                    }
                }
            }
        });

        int i = 10;
        while(i<89){
            tv[i].setBackgroundResource(R.drawable.huisemiaobian);
            tv[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = 10;
                    while(i<=89){
                        if(!list.contains(i)) {tv[i].setBackgroundResource(0);i++;}
                        else{if(!map2.get(i).equals("null,null")) {tv[i].setBackgroundResource(R.drawable.huisemiaobianx);i++;}
                             else {tv[i].setBackgroundResource(R.drawable.huisemiaobian);i++;}
                        }
                    }
                    view.setBackgroundResource(R.drawable.hongsemiaobian);
                    i=11;red=10;
                    while(i<=89) {
                        if (tv[i].equals(view)) {
                            red = i;
                            break;
                        }
                        i++;
                    }
                    System.out.println("当前红色选中目标是"+red);
                    //############施工中############已经知道了自己是几号，接下来只需要从map2中找出red对应的name和time显示在对应位置上
                    if(map2.get(red).split(",")[0].equals("null")){
                        tvnew.setText("本格为空");
                        tvnew2.setText("");
                        tvold.setText("");
                        iv.setImageURI(Uri.EMPTY);
                    }else{
                        view.setBackgroundResource(R.drawable.hongsemiaobianx);
                        tvnew.setText("");
                        tvold.setText("当前存放货物："+map2.get(red).split(",")[0]);
                        tvnew2.setText("存入时间："+map2.get(red).split(",")[1]);
                        cursor = db.rawQuery("select * from store where name = '"+map2.get(red).split(",")[0]+"'",null);
                        while(cursor.moveToNext()) {
                            if (!cursor.getString(5).equals("null")) {
                                iv.setImageURI(Uri.fromFile(new File(ShelfActivity.this.getFilesDir() + "/" + map2.get(red).split(",")[0] + ".jpg")));
                            } else {
                                iv.setImageURI(null);
                            }
                        }
                    }


                }
            });
            i++;
        }

        btn1u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aimi!=0){
                    if(map1.containsKey(aimi+1)){
                        aimi=aimi+1;
                        aim = map1.get(aimi);
                        tv1.setText(aim);
                        aimx=1;
                        tv2.setText(aimx+"号货架");
                    }
                }
            }
        });

        btn1d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aimi!=0){
                    if(map1.containsKey(aimi-1)){
                        aimi=aimi-1;
                        aim = map1.get(aimi);
                        tv1.setText(aim);
                        aimx=1;
                        tv2.setText(aimx+"号货架");
                    }
                }
            }
        });

        btn2u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aimx!=0){
                    int max = Integer.valueOf(map1.get(aimi).split("-")[1]);
                    if(aimx+1<=max){
                        aimx=aimx+1;
                        tv2.setText(aimx+"号货架");
                    }
                }
            }
        });

        btn2d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aimx!=0){
                    if(aimx-1>0){
                        aimx=aimx-1;
                        tv2.setText(aimx+"号货架");
                    }
                }
            }
        });

        btnrt = findViewById(R.id.button16);
        btnrt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(ShelfActivity.this, Main2Activity.class);
                intent.putExtra("level",lv);
                intent.putExtra("ip",ip);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });



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
                DataInputStream dis = new DataInputStream(socket.getInputStream());//创建图片输入流
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
                        System.out.println("接收到信息："+str);
                        if(str.split("&")[0].equals("update2")){
                            int i = 1;
                            while(!str.split("&")[i].equals("end")) {
                                map1.put(i, str.split("&")[i]);
                                i++;
                            }
                            tv1.setText(map1.get(1).split("-")[0]);
                            tv2.setText("1号货架");
                            aim = map1.get(1);
                            aimx=1;
                            aimi=1;
                            flag=true;
                        }else if(str.split("&")[0].equals("update3")){
                            myHandler.removeMessages(0);
                            Message message = myHandler.obtainMessage(1246,0,0,str);
                            myHandler.sendMessage(message);
                            /*
                            int i = 10;
                            while(i<=89){
                                tv[i].setText("");
                                tv[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT,0));
                                i++;
                            }
                            i=1;
                            while(i<=8){
                                tr[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT,0));
                            }
                            i=1;
                            map2.clear();
                            while(!str.split("&")[i].equals("end")){
                                String temp = str.split("&")[i];
                                i++;
                                int y = Integer.valueOf(temp.split("\\|")[0]);
                                int z = Integer.valueOf(temp.split("\\|")[1]);
                                String name = temp.split("\\|")[2];
                                String time = temp.split("\\|")[3];
                                int index = 10*z+y-1;
                                tr[z].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT,1));
                                tv[index].setText(y+","+z);
                                tv[index].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT,1));
                                map2.put(index,name+","+time);
                            }*/
                        }else{
                            myHandler.removeMessages(0);
                            Message message = myHandler.obtainMessage(10086,0,0,"异常信息"+str);
                            myHandler.sendMessage(message);
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

    public class myHandler extends Handler {

        public ListView listView;

        public myHandler(Looper looper,ListView listView){
            super(looper);this.listView = listView;
        }

        public void handleMessage(Message message){
            switch (message.what){
                //弹出进出货失败警告框
                case 10003:
                    Toast.makeText(ShelfActivity.this,"检查到网络延迟\n网络状况不佳",Toast.LENGTH_LONG).show();
                    break;
                case 10086:
                    Toast.makeText(ShelfActivity.this,message.obj.toString(),Toast.LENGTH_LONG).show();
                    break;
                case 1246:
                    //
                    list.clear();
                    String str = message.obj.toString();
                    System.out.println("str:"+str);
                    int j = 1;
                    int i = 10;
                    while(i<=89){
                        tv[i].setText("");
                        tv[i].setBackgroundResource(0);
                        tv[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT,0));
                        i++;
                    }
                    i=1;
                    while(i<=8){
                        tr[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT,0));
                        i++;
                    }
                    i=1;
                    map2.clear();
                    while(!str.split("&")[i].equals("end")) {
                        String temp = str.split("&")[i];
                        i++;
                        int y = Integer.valueOf(temp.split("\\|")[0]);
                        int z = Integer.valueOf(temp.split("\\|")[1]);
                        String name = temp.split("\\|")[2];
                        String time = temp.split("\\|")[3];
                        int index = 10 * z + y - 1;
                        tr[z].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
                        tv[index].setText(y + "," + z);
                        if(name.equals("null")) tv[index].setBackgroundResource(R.drawable.huisemiaobian);
                        else tv[index].setBackgroundResource(R.drawable.huisemiaobianx);
                        tv[index].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
                        map2.put(index, name + "," + time);
                        list.add(index);
                        tvold.setText("");
                        tvnew.setText("本格为空");
                        tvnew2.setText("");
                        iv.setImageURI(null);
                    }
                    //
                    break;
            }
        }

    }

    }
