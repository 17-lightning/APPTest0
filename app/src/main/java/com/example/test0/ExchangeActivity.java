package com.example.test0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ExchangeActivity extends AppCompatActivity {

    //布局元件
    private Button buttonUpdate;
    private EditText etgoal;
    private ImageButton imgbtnfm;
    private TextView txtitle;
    private TextView txLastUpdate;
    private TextView txTag;
    private TextView txLocation;
    private TextView txNumber;
    private TextView txNote;
    private EditText etinput;
    private EditText etoutput;
    private Button btnin;
    private Button btnout;
    private ImageButton btnpic;
    private Button btnrt;
    private Button buttonupdate;

    //变量
    private String goal;//可输入的货物名称，不保证匹配
    private Text text = new Text();//可视情况删除
    private Text texto = new Text();//弃案，视情况删除
    private String ip;//IP地址
    private String id;//用户账号
    private int lv;//用户权限等级
    private String input;//进货文本框的输入内容
    private String output;//出货文本框的输入内容
    private String exmsg;//进出货的信息
    private String attenntionmsg;//警告信息
    private String namematch = "null";//匹配的货物名称
    private Double numbermatch = 0.0;//匹配的货物数量
    private StringBuffer sb = null;//可拼接字符串，在输出所有日志并退出时用到，目前搁置
    public boolean endFlag = false;
    private String checkString = "null";
    private HashMap<Integer,String> checkmap;
    private int index=0;
    private int index2=0;
    private ListView listView0;

    //数据库器件
    private StorageHelper storageHelper = null;//数据库打开器
    private SQLiteDatabase db = null;//数据库本身
    Cursor cursor = null;//遍历器
    SimpleDateFormat simpleDateFormat = null;//时间结构

    //下拉框器件1，输入下拉框
    private PopupWindow pop;//弹出窗口
    private DropdownAdapter adapter;//适配器
    private View layout;//布局文件
    private List<String> list = new ArrayList<String>();
    //下拉框器件2，确认窗口//作废
    private PopupWindow pop2;
    private ConfirmAdapter adapter2 = null;
    private View viewant;
    //搁置
    public Boolean confirmFLag = false;
    public Boolean confirmCorrectFlag = false;
    //弹出窗口3（警告窗口）
    private PopupWindow pop3;
    private AttentionAdapter adapter3 = null;
    //弹出窗口4（目标仓库选择窗口）
    private PopupWindow pop4;
    private CheckAdapter adapter4 = null;
    //多线程相应器
    private Looper looper;
    private myHandler myHandler;
    //网络处理模块中用于反馈错误的翻译表
    private HashMap<String,String> map;
    //主线程
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //通用开头
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        //创建Looper
        looper = Looper.myLooper();
        //从上一个页面中得知ip、id和lv信息
        Intent intentme = getIntent();
        lv = intentme.getIntExtra("level",0);
        id = intentme.getStringExtra("id");
        ip = intentme.getStringExtra("ip");
        //创建数据库连接
        storageHelper = new StorageHelper(ExchangeActivity.this);
        db = storageHelper.getWritableDatabase();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        //做弹出窗口准备
        adapter = new DropdownAdapter(ExchangeActivity.this, list);//创建一个适配器
        final ListView listView = new ListView(ExchangeActivity.this);//创建列表窗口
        listView.setAdapter(adapter);//将适配器装入列表窗口中
        layout = findViewById(R.id.viewon);
        //2
        adapter2 = new ConfirmAdapter(ExchangeActivity.this,exmsg);
        final ListView listView2 = new ListView(ExchangeActivity.this);//创建列表窗口
        listView2.setAdapter(adapter2);
        viewant = findViewById(R.id.viewant);
        //3
        adapter3 = new AttentionAdapter(ExchangeActivity.this,attenntionmsg);
        final ListView listView3 = new ListView(ExchangeActivity.this);//创建列表窗口
        listView3.setAdapter(adapter3);
        //4
        adapter4 = new CheckAdapter(ExchangeActivity.this,checkString);
        final ListView listView4 = new ListView(ExchangeActivity.this);
        listView4.setAdapter(adapter4);
        listView0=listView4;
        //创建Handler，接下来程序可能需要在这里做改进
        myHandler = new myHandler(looper,listView3);
        //名片中元件匹配
        txtitle = findViewById(R.id.textView3);
        txLastUpdate = findViewById(R.id.textView6);
        txTag = findViewById(R.id.textView2);
        txLocation = findViewById(R.id.textView7);
        txNumber = findViewById(R.id.textView5);
        txNote = findViewById(R.id.textView8);
        //初始化
        goal = "";
        ChangeList(list);
        sb = new StringBuffer();
        new Thread(new SumConnect(text)).start();
        checkmap = new HashMap<Integer, String>();

        //可编辑文本框，这个文本框是用来输入目标名称的
        etgoal = findViewById(R.id.editText);
        etgoal.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                //每次可编辑文本框中的内容变化，就更新list列表中的数据
                goal = editable.toString();System.out.println("需要查询的货物是:"+goal);
                ChangeList(list);

            }
        });

        //更新按键，功能是按下时创建网络连接，更新整个数据库
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                text.s = "update#ALL";//############
                text.flag = true;
            }
        });
        //小更新按键，只更新当前货物信息
        buttonupdate = findViewById(R.id.button);
        buttonupdate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(namematch.equals("null")){
                    attenntionmsg = "请选择需要更新信息的货物";
                    attentionshow(listView3);
                    return;
                }
                text.s = "update#"+namematch;//############
                text.flag = true;
            }
        });

        //展开下拉列表用的按键
        imgbtnfm = findViewById(R.id.fm);
        imgbtnfm.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(pop == null){
                    pop = new PopupWindow(listView,layout.getWidth(),3*layout.getHeight());
                    pop.showAsDropDown(layout);
                    imgbtnfm.setBackgroundResource(R.drawable.shouhui);
                }
                else{
                    if(pop.isShowing()){
                        pop.dismiss();
                        imgbtnfm.setBackgroundResource(R.drawable.zhankai);
                    }
                    else{
                        pop.showAsDropDown(layout);
                        imgbtnfm.setBackgroundResource(R.drawable.shouhui);
                    }
                }
            }
        });

        //获取进货数量的可编辑文本框
        etinput = findViewById(R.id.editText4);
        etinput.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                //每次可编辑文本框中的内容变化，就更新list列表中的数据
                input = editable.toString();System.out.println("进货的数量是:"+input);
            }
        });
        //获取出货数量的可编辑文本框
        etoutput = findViewById(R.id.editText2);
        etoutput.addTextChangedListener(new TextWatcher() {
            @Override//在文本改变之前
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                //每次可编辑文本框中的内容变化，就更新list列表中的数据
                output = editable.toString();System.out.println("出货的数量是:"+output);
            }
        });

        btnpic = findViewById(R.id.imgbtn);
        btnpic.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                text.s = "picture#"+namematch;//############
                text.flag2 = true;
                text.flag = true;
            }
        });

        //进货用的按钮
        btnin = findViewById(R.id.button4);
        btnin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(namematch.equals("null")){
                    attenntionmsg = "请选择要进的货物";
                    attentionshow(listView3);
                }
                if(lv<2){
                    myHandler.removeMessages(0);
                    Message message = myHandler.obtainMessage(10005,0,0,"null");
                    myHandler.sendMessage(message);
                }
                try{
                    Double temp = Double.valueOf(input);
                    exmsg = "input#"+input;
                    //本来是直接进货，现在需要先切换到check页面
                    text.s="check#"+namematch;
                    text.flag=true;
                    /*if(pop2 == null) {
                        pop2 = new PopupWindow(listView2, viewant.getWidth(), viewant.getHeight());
                        pop2.showAsDropDown(viewant);
                    }
                    else{
                        pop2.showAsDropDown(viewant);
                    }*/
                }catch (Exception e){
                    e.printStackTrace();
                    attenntionmsg = "进货数量不是数字，请检查";
                    attentionshow(listView3);
                }
            }
        });

        //出货用的按钮
        btnout = findViewById(R.id.button5);
        btnout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(namematch.equals("null")){
                    attenntionmsg = "没有选择要出的货物";
                    attentionshow(listView3);
                    return;
                }
                if(lv<2){
                    myHandler.removeMessages(0);
                    Message message = myHandler.obtainMessage(10005,0,0,"null");
                    myHandler.sendMessage(message);
                }
                try{
                    Double temp = Double.valueOf(output);
                    if(numbermatch<Double.valueOf(output)){
                        attenntionmsg = "库存不能为负数";
                        attentionshow(listView3);
                        return;
                    }
                    exmsg = "output#"+output;
                    //本来是直接出货，现在需要先切换到check页面
                    text.s="check#"+namematch;
                    text.flag=true;
                    /*
                    if(pop2 == null) {
                        pop2 = new PopupWindow(listView2, viewant.getWidth(), viewant.getHeight());
                        pop2.showAsDropDown(viewant);
                    }
                    else{
                        pop2.showAsDropDown(viewant);
                    }
                    */
                }catch (Exception e){
                    e.printStackTrace();
                    attenntionmsg = "出货数量不是数字，请检查";
                    attentionshow(listView3);
                }
            }
        });

        btnrt = findViewById(R.id.button6);
        btnrt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(ExchangeActivity.this, Main2Activity.class);
                intent.putExtra("level",lv);
                intent.putExtra("ip",ip);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        }//主线程方法结束，以下是其他方法

    protected void onDestroy(){
        super.onDestroy();
        endFlag = true;
    }

    //更新list中的数据，如果发现匹配，则更新名片，并设置namematch和numbermatch
        public void ChangeList(List<String> list){
            list.removeAll(list);
            if(goal == "") {
                //如果输入框内没有文字，则显示今天最近搜索过的三个内容
                cursor = db.rawQuery("select * from store where time like '"+simpleDateFormat.format(new Date()).substring(0,10)+"%'",null);
            }
            //如果输入框内有文字，则显示与文字相似的内容
            else cursor = db.rawQuery("select * from store where name like '%"+goal+"%'",null);
            while(cursor.moveToNext()){
                if(goal.equals(cursor.getString(0))){
                    txtitle.setText(cursor.getString(0));
                    namematch = cursor.getString(0);
                    numbermatch = Double.valueOf(cursor.getString(1));
                    txLastUpdate.setText(cursor.getString(6));
                    txTag.setText(cursor.getString(2));
                    txLocation.setText(cursor.getString(4));
                    txNumber.setText(cursor.getString(1));
                    txNote.setText(cursor.getString(3));
                    System.out.println("货物"+goal+"的数量是"+cursor.getString(1)+",它的标签是"+cursor.getString(2)+",它的相关说明是"+cursor.getString(3)+",它的存储位置是"+cursor.getString(4)+",它的图片位置是"+cursor.getString(5)+",它的最后更新时间是"+cursor.getString(6));
                    if(!cursor.getString(5).equals("null")){
                        btnpic.setImageURI(Uri.fromFile(new File(ExchangeActivity.this.getFilesDir()+"/"+goal+".jpg")));
                    }else{
                        btnpic.setImageURI(null);
                    }
                }
                list.add(cursor.getString(0));
            }
            myHandler.removeMessages(0);
            Message message = myHandler.obtainMessage(1999,0,0,"null");
            myHandler.sendMessage(message);
            //adapter.notifyDataSetChanged();
        }
        //展示警告窗口
        public void attentionshow(ListView listView){
            if(pop3 == null){
                pop3 = new PopupWindow(listView,viewant.getWidth(),viewant.getHeight());
                pop3.showAsDropDown(viewant);
            }
            else{
                pop3.showAsDropDown(viewant);
            }
        }

        //类：下拉框对应的适配器
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
        public int getCount() {
            return list.size();
        }//为什么列表中会有多个list_row，就是在这里得知的

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
                    etgoal.setText(editContent);
                    pop.dismiss();
                    imgbtnfm.setBackgroundResource(R.drawable.arrow_down_float);
                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    etgoal.setText(editContent);
                    pop.dismiss();
                    imgbtnfm.setBackgroundResource(R.drawable.arrow_down_float);
                }
            });
            return convertView;
        }
    }
    //类：确认窗口对应的适配器
    class ConfirmAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;
        private String str;
        private TextView content;
        private Button btny;
        private Button btnn;

        //构造方法，用于获取当前context背景和列表
        public ConfirmAdapter(Context context,String str) {
            this.context = context;this.str = str;
        }

        //获取长度
        public int getCount() {
            return 1;
        }//为什么列表中会有多个list_row，就是在这里得知的

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            layoutInflater = LayoutInflater.from(context);//创建一个布局解析器
            convertView = layoutInflater.inflate(R.layout.confirm, null);//将布局转化成View窗口
            btny = convertView.findViewById(R.id.button8);//其中的按钮与元件对应关系
            btnn = convertView.findViewById(R.id.button7);
            content = (TextView)convertView.findViewById(R.id.textView9);
            content.setText(exmsg);//设置文本框的内容为获取到的元素
            btnn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    pop2.dismiss();
                }
            });
            btny.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    pop2.dismiss();
                    text.s = "exchange#"+namematch+"#"+exmsg+"#"+numbermatch+"#"+id;
                    text.flag = true;
                }
            });
            return convertView;
        }
    }
    //类：警告窗口
    class AttentionAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;
        private String str;
        private TextView content;
        private Button btnrt;

        //构造方法，用于获取当前context背景和列表
        public AttentionAdapter(Context context,String str) {
            this.context = context;this.str = str;
        }

        //获取长度
        public int getCount() {
            return 1;
        }//为什么列表中会有多个list_row，就是在这里得知的

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            layoutInflater = LayoutInflater.from(context);//创建一个布局解析器
            convertView = layoutInflater.inflate(R.layout.attention, null);//将布局转化成View窗口
            btnrt = convertView.findViewById(R.id.btnrt);//其中的按钮与元件对应关系
            content = (TextView)convertView.findViewById(R.id.txerror);
            content.setText(attenntionmsg);//设置文本框的内容为获取到的元素
            //点击删除按键时，从列表中删去这个元素，并更新适配器
            btnrt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    pop3.dismiss();
                }
            });
            return convertView;
        }
    }

    //磊：目标仓库选择窗口
    class CheckAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;
        private String str;
        private TextView tv1;
        private TextView tv2;
        private TextView tv3;
        private Button btnlf;
        private Button btnrt;
        private Button btnyes;
        private Button btnno;

        //构造方法，用于获取当前context背景和列表
        public CheckAdapter(Context context,String str) {
            this.context = context;this.str = str;
        }

        //获取长度
        public int getCount() {
            return 1;
        }//为什么列表中会有多个list_row，就是在这里得知的

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            layoutInflater = LayoutInflater.from(context);//创建一个布局解析器
            convertView = layoutInflater.inflate(R.layout.check, null);//将布局转化成View窗口
            convertView.setAlpha(1);
            //元件对应匹配
            tv1 = convertView.findViewById(R.id.textView17);
            tv2 = convertView.findViewById(R.id.textView18);
            tv3 = convertView.findViewById(R.id.textView16);
            btnrt = convertView.findViewById(R.id.button15);
            btnlf = convertView.findViewById(R.id.button14);
            btnyes = convertView.findViewById(R.id.button13);
            btnno = convertView.findViewById(R.id.button12);
            if(checkmap.containsKey(index)){
                tv1.setText(checkmap.get(index));
            }else{
                tv1.setText("没有更多信息了");
            }
            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    index2=index;
                    tv1.setBackgroundResource(R.drawable.hongsemiaobian);
                    tv2.setBackgroundResource(R.drawable.huisemiaobian);
                    tv3.setBackgroundResource(R.drawable.huisemiaobian);
                }
            });
            if(checkmap.containsKey(index+1)){
                tv2.setText(checkmap.get(index+1));
            }else{
                tv2.setText("没有更多信息了");
            }
            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    index2=index+1;
                    tv2.setBackgroundResource(R.drawable.hongsemiaobian);
                    tv1.setBackgroundResource(R.drawable.huisemiaobian);
                    tv3.setBackgroundResource(R.drawable.huisemiaobian);
                }
            });
            if(checkmap.containsKey(index+2)){
                tv3.setText(checkmap.get(index+2));
            }else{
                tv3.setText("没有更多信息了");
            }
            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    index2=index+2;
                    tv3.setBackgroundResource(R.drawable.hongsemiaobian);
                    tv1.setBackgroundResource(R.drawable.huisemiaobian);
                    tv2.setBackgroundResource(R.drawable.huisemiaobian);
                }
            });
            btnrt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    index=index+3;
                }
            });
            btnlf.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(index-3>0) index=index-3;
                }
            });
            //点击取消按键时，本check框不再显示
            btnno.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    pop4.dismiss();
                }
            });
            btnyes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(checkmap.containsKey(index2)){
                        String checknumber;
                        if(exmsg.split("#")[0].equals("input")) checknumber=input;
                        else checknumber = output;
                        text.s=exmsg.split("#")[0]+"#"+namematch+"#"+checknumber+"#"+numbermatch.toString().split("\\.")[0]+"#"+id+"#false#"+checkmap.get(index2).split("\\|")[0];
                        text.flag=true;
                    }else{
                        myHandler.removeMessages(0);
                        Message message = myHandler.obtainMessage(10086,0,0,"目标仓库不存在");
                        myHandler.sendMessage(message);
                    }
                    pop4.dismiss();
                }
            });
            return convertView;
        }
    }


    //总的网络连接线程，所有网络连接操作在此线程中进行
    class SumConnect extends Thread{

        Socket socket = null;
        Text t = null;

        public SumConnect(Text t){this.t = t;}

        public void run(){
            try{
                map = errorTrans();
                Text t0 = new Text();//t0是为了判断网络延时而设置的参照物
                t0.flag = false;//设置t0为false
                Thread.sleep(1000);
                System.out.println("启动延时器");
                new Thread(new DelayTimer(t0)).start();//启动延时器，若1s后连接没有建立，认为发生了网络延时，弹出警告框
                socket = new Socket(ip,57798);//建立Socket连接
                PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));//创建输出流
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));//创建文本输入流
                DataInputStream dis = new DataInputStream(socket.getInputStream());//创建图片输入流
                t0.flag = true;
                System.out.println("网络连接建立成功");
                while(endFlag == false){
                    //进行一次网络检查
                    Thread.sleep(500);//每500ms判断一次是否需要网络输出，如果有输出的信号，则发送这行信息并接收服务器端的一行信息
                    if(t.flag == true){//t.flag = true说明有需要发送的信号
                        System.out.println("检测到有需要输出的消息");
                        t.flag = false;//标记发送信号已收到
                        System.out.println("清除发送信号");
                        pw.println(t.s);//发送
                        pw.flush();
                        System.out.println("发送数据："+t.s);
                        if(t.flag2 == true){//flag2是用来标记图片输入的，服务器端返回的信息应当是图片而不是语句时，调用以下代码
                            t.flag2 = false;
                            System.out.println("接收到图片输入："+namematch+".jpg");
                            Cursor cursortemp = db.rawQuery("select * from store where name = '"+namematch+"'",null);
                            while(cursortemp.moveToNext()){
                                if(cursortemp.getString(5).equals("null")){//如果原本不存在这张图片，则创建该文件
                                    db.execSQL("update store set picture = '" + namematch + ".jpg' where name = '" + namematch + "'");
                                    File file = new File(ExchangeActivity.this.getFilesDir(),namematch+".jpg");
                                }
                            }
                            OutputStream os = openFileOutput(namematch+".jpg",Context.MODE_PRIVATE);//创建一个图片输出流
                            System.out.println("成功创建图片输出流："+namematch+".jpg");
                            byte[] temp = new byte[1024];//创建byte数组用于读取信息
                            int len = -1;
                            while((len = dis.read(temp))!=-1){
                                os.write(temp,0,len);//将信息写入文件
                            }
                            os.flush();//刷新并关闭文件流
                            os.close();
                            System.out.println("刷新并关闭文件"+namematch+".jpg");
                            myHandler.removeMessages(0);
                            Message message = myHandler.obtainMessage(10004,0,0,"null");
                            myHandler.sendMessage(message);
                            //虽然我也不知道为什么，但是在dis.read方法后，这个连接会被关闭，咱也不知道，咱也搞不懂，所以只能开辟一个新连接了
                            //可能是因为每4字节一读但是图片大小不会正好是4的整数倍，所以连接出现异常关闭，但反正关了这个连接重开一个就是了
                            new Thread(new SumConnect(t)).start();
                            System.out.println("图片交流完毕，关闭本线程并开启新线程");
                            return;
                        }else{//服务器端返回的信息不是图片而是语句时，调用以下代码
                            String str = br.readLine();
                            System.out.println("接收到输入信息："+str);
                            if(str.split("#")[0].equals("update")) new Thread(new PutinStore(str,db)).start();
                            else if(map.containsKey(str)){
                                myHandler.removeMessages(0);
                                Message message = myHandler.obtainMessage(10086,0,0,map.get(str));
                                myHandler.sendMessage(message);
                            }else if(str.split("#")[0].equals("check")){//如果是check型数据，打开check面板
                                //check预处理
                                checkmap.clear();
                                int i=1;//checkmap的数字从1开始
                                while(!str.split("&")[i].equals("end")){
                                    checkmap.put(i,str.split("&")[i]);
                                    i++;
                                }
                                index=1;index2=0;
                                myHandler.removeMessages(0);
                                Message message = myHandler.obtainMessage(12138,0,0,str);
                                myHandler.sendMessage(message);
                            }else if(str.equals("input#success")||str.equals("output#success")){
                                myHandler.removeMessages(0);
                                Message message = myHandler.obtainMessage(10001,0,0,"null");
                                myHandler.sendMessage(message);
                                if(exmsg.split("#")[0].equals("input")){
                                    db.execSQL("update store set number = "+(numbermatch+Double.valueOf(exmsg.split("#")[1]))+" where name = '"+namematch+"'");
                                    sb.append(namematch+"+"+exmsg.split("#")[1]);
                                    myHandler.removeMessages(0);
                                    Message message2 = myHandler.obtainMessage(10004,0,0,"null");
                                    myHandler.sendMessage(message2);
                                }else{
                                    db.execSQL("update store set number = "+(numbermatch-Double.valueOf(exmsg.split("#")[1]))+" where name = '"+namematch+"'");
                                    sb.append(namematch+"-"+exmsg.split("#")[1]);
                                    myHandler.removeMessages(0);
                                    Message message2 = myHandler.obtainMessage(10004,0,0,"null");
                                    myHandler.sendMessage(message2);
                                }
                            }
                            else{
                                myHandler.removeMessages(0);
                                Message message = myHandler.obtainMessage(10086,0,0,"未知消息："+str);
                                myHandler.sendMessage(message);
                            }
                        }
                    }else{
                        //没有需要输出的信息，开始下一次循环
                    }

                }
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("连接中断");
                myHandler.removeMessages(0);
                Message message = myHandler.obtainMessage(10004,0,0,"null");
                myHandler.sendMessage(message);
            }
            System.out.println("终止所有相关线程");
        }
    }
//延迟计时器，用于判断网络连接超时错误
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


//更新数据库用方法
    class PutinStore extends Thread{

        String temp = null;
        SQLiteDatabase db = null;

        PutinStore(String temp,SQLiteDatabase db){
            this.temp = temp;this.db = db;
        }

        public void run(){
            int i = 1;
            String temp = this.temp.split("&")[i];
            db.execSQL("create table if not exists store(name String,number double,tag String,note String,location String,picture String,time String)");
            while(!temp.equals("end")){
                String name = temp.split("#")[0];
                boolean matchFlag = false;
                Cursor cursorTemp = db.rawQuery("select * from store where name = '"+name+"'",null);
                while(cursorTemp.moveToNext()) {
                    matchFlag = true;
                }
                if(matchFlag == true) {
                    System.out.println(temp);
                    Double number = Double.valueOf(temp.split("#")[1]);
                    String tag = temp.split("#")[2];
                    String note = temp.split("#")[3];
                    String location = temp.split("#")[4];
                    db.execSQL("update store set number = " + number + " where name = '" + name + "'");
                    db.execSQL("update store set tag = '" + tag + "' where name = '" + name + "'");
                    db.execSQL("update store set note = '" + note + "' where name = '" + name + "'");
                    db.execSQL("update store set location = '" + location + "' where name = '" + name + "'");
                    db.execSQL("update store set time = '" + simpleDateFormat.format(new Date()) + "' where name = '" + name + "'");
                    i++;
                    System.out.println("insert into store values('"+name+"','"+Double.valueOf(temp.split("#")[1])+"','"+temp.split("#")[2]+"','"+temp.split("#")[3]+"','"+temp.split("#")[4]+"','null','"+simpleDateFormat.format(new Date())+"')");
                    temp = this.temp.split("&")[i];

                }else{
                    db.execSQL("insert into store values('"+name+"','"+Double.valueOf(temp.split("#")[1])+"','"+temp.split("#")[2]+"','"+temp.split("#")[3]+"','"+temp.split("#")[4]+"','null','"+simpleDateFormat.format(new Date())+"')");
                    System.out.println("insert into store values('"+name+"','"+Double.valueOf(temp.split("#")[1])+"','"+temp.split("#")[2]+"','"+temp.split("#")[3]+"','"+temp.split("#")[4]+"','null','"+simpleDateFormat.format(new Date())+"')");
                }
            }
            ChangeList(list);
        }
    }
//处理器，用于线程交互
    public class myHandler extends Handler{

        public ListView listView;

        public myHandler(Looper looper,ListView listView){
            super(looper);this.listView = listView;
        }

        public void handleMessage(Message message){
            switch (message.what){
                //弹出进出货失败警告框
                case 10000:
                    System.out.println("检测到进出货失败");
                    attenntionmsg = "操作失败\n请及时更新货物信息";
                    attentionshow(listView);
                    break;
                //弹出进出货成功警告框
                case 10001:
                    System.out.println("检测到进出货成功");
                    attenntionmsg = "操作成功\n";
                    attentionshow(listView);
                    break;
                case 10002:
                    attenntionmsg = "操作失败\n网络连接出现异常";
                    attentionshow(listView);
                    break;
                case 10003:
                    attenntionmsg = "网络连接超时\n请尝试退出页面重进";
                    attentionshow(listView);
                    break;
                case 10004:
                    ChangeList(list);
                    break;
                case 10005:
                    attenntionmsg = "您的权限不够\n不能对库存进行操作";
                    attentionshow(listView);
                    break;
                case 10086:
                    attenntionmsg = message.obj.toString();
                    attentionshow(listView);
                    break;
                case 12138:
                    if(pop4 == null){
                        pop4 = new PopupWindow(listView0,viewant.getWidth(),2*viewant.getHeight());
                        pop4.showAsDropDown(viewant);
                    }
                    else{
                        pop4.showAsDropDown(viewant);
                    }
                    break;
                case 1999:
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    attenntionmsg = "未知错误";
                    attentionshow(listView);
                    break;
            }
        }

    }

    public static HashMap<String,String> errorTrans(){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("input#miss","要进货的目标货物不存在，货物信息可能已被删除");
        map.put("input#wrong","货物余量校验错误\n请及时更新当前货物信息");
        map.put("input#error","您的操作权限不够\n请联系管理员\n电话13780094528");
        map.put("input#over","当前仓库剩余容量不足\n已取消入库操作");
        map.put("output#miss","要出货的目标货物不存在，货物信息可能已被删除");
        map.put("output#wrong","货物余量校验错误\n请及时更新当前货物信息");
        map.put("output#error","宁的操作权限不足\n请联系管理员\n电话18888920671");
        map.put("output#over","目标仓库中该货物的余量不足\n已取消出库操作");
        return map;
    }

}




