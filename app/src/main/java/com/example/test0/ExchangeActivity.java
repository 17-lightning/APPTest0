package com.example.test0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private String goal;
    private Text text = new Text();
    private Text texto = new Text();
    private String ip;
    private String id;
    private int lv;
    private String input;
    private String output;
    private String exmsg;
    private String attenntionmsg;
    private String namematch = "null";
    private Double numbermatch = 0.0;

    //数据库器件
    private StorageHelper storageHelper = null;
    private SQLiteDatabase db = null;
    Cursor cursor = null;
    SimpleDateFormat simpleDateFormat = null;

    //下拉框器件
    private PopupWindow pop;
    private DropdownAdapter adapter;//适配器
    private View layout;//布局文件
    private List<String> list = new ArrayList<String>();
    //下拉框器件2（其实是弹出窗口）
    private PopupWindow pop2;
    private ConfirmAdapter adapter2 = null;
    private View viewant;

    public Boolean confirmFLag = false;
    public Boolean confirmCorrectFlag = false;
    //弹出窗口3（警告窗口）
    private PopupWindow pop3;
    private AttentionAdapter adapter3 = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        //从上一个页面中得知ip、id和lv信息
        Intent intentme = getIntent();
        lv = intentme.getIntExtra("level",0);
        id = intentme.getStringExtra("id");
        ip = intentme.getStringExtra("ip");

        //创建数据库连接
        storageHelper = new StorageHelper(ExchangeActivity.this);
        db = storageHelper.getWritableDatabase();
        db.execSQL("create table if not exists store(name String,number double,tag String,note String,location String,picture String,time String)");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");

        //做弹出窗口准备
        adapter = new DropdownAdapter(ExchangeActivity.this, list);//创建一个适配器
        final ListView listView = new ListView(ExchangeActivity.this);//创建列表窗口
        listView.setAdapter(adapter);//将适配器装入列表窗口中
        layout = findViewById(R.id.viewon);
        //2

        //3
        adapter3 = new AttentionAdapter(ExchangeActivity.this,attenntionmsg);
        final ListView listView3 = new ListView(ExchangeActivity.this);//创建列表窗口
        listView3.setAdapter(adapter3);
        //
        adapter2 = new ConfirmAdapter(ExchangeActivity.this,exmsg,listView3);
        final ListView listView2 = new ListView(ExchangeActivity.this);//创建列表窗口
        listView2.setAdapter(adapter2);
        viewant = findViewById(R.id.viewant);


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
                int i = 0;
                System.out.println(list.size());
                adapter.notifyDataSetChanged();
            }
        });
        //更新按键，功能是按下时创建网络连接，更新整个数据库
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                text = new Text();
                Thread t = new Thread(new Updateconnect("ALL",text));t.start();
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(text.flag == true){
                    new Thread(new PutinStore(text.s,db)).start();
                }
                else{
                    Toast.makeText(ExchangeActivity.this,"连接失败，请检查网络情况",Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonupdate = findViewById(R.id.button);
        buttonupdate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                text = new Text();
                if(namematch.equals("null")){
                    attenntionmsg = "请选择需要更新信息的货物";
                    attentionshow(listView3);
                    return;
                }
                Thread t = new Thread(new Updateconnect(namematch,text));t.start();
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(text.flag == true){
                    new Thread(new PutinStore(text.s,db)).start();
                    ChangeList(list);
                }
                else{
                    Toast.makeText(ExchangeActivity.this,"连接失败，请检查网络情况",Toast.LENGTH_SHORT).show();
                }

            }
        });


        //展开下拉列表用的按键
        imgbtnfm = findViewById(R.id.fm);
        imgbtnfm.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(pop == null){
                    pop = new PopupWindow(listView,layout.getWidth(),3*layout.getHeight());
                    pop.showAsDropDown(layout);
                    imgbtnfm.setBackgroundResource(R.drawable.arrow_up_float);
                }
                else{
                    if(pop.isShowing()){
                        pop.dismiss();
                        imgbtnfm.setBackgroundResource(R.drawable.arrow_down_float);
                    }
                    else{
                        pop.showAsDropDown(layout);
                        imgbtnfm.setBackgroundResource(R.drawable.arrow_up_float);
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
                text.flag = false;
                new Thread(new PictureConnect(text)).start();
                try{
                    Thread.sleep(1000);
                    if(text.flag==false) Toast.makeText(ExchangeActivity.this,"连接失败，请检查网络情况",Toast.LENGTH_SHORT).show();
                    ChangeList(list);
                }catch(Exception e){
                    e.printStackTrace();
                }
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
                try{
                    Double temp = Double.valueOf(input);
                    exmsg = "input#"+input;
                    if(pop2 == null) {
                        pop2 = new PopupWindow(listView2, viewant.getWidth(), viewant.getHeight());
                        pop2.showAsDropDown(viewant);
                    }
                    else{
                        pop2.showAsDropDown(viewant);
                    }
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
                try{
                    etinput.setText("");
                    input = "";
                    Double temp = Double.valueOf(output);
                    if(numbermatch<Double.valueOf(output)){
                        attenntionmsg = "库存不能为负数";
                        attentionshow(listView3);
                        return;
                    }
                    exmsg = "output#"+output;
                    if(pop2 == null) {
                        pop2 = new PopupWindow(listView2, viewant.getWidth(), viewant.getHeight());
                        pop2.showAsDropDown(viewant);
                    }
                    else{
                        pop2.showAsDropDown(viewant);
                    }
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




        }
        //更新list中的数据，如果发现匹配，则更新名片
        public void ChangeList(List<String> list){
            list.removeAll(list);
            cursor = db.rawQuery("select * from store where name like '%"+goal+"%'",null);
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
                    }
                }
                list.add(cursor.getString(0));
            }
        }

        public void attentionshow(ListView listView){
            if(pop3 == null){
                pop3 = new PopupWindow(listView,viewant.getWidth(),viewant.getHeight());
                pop3.showAsDropDown(viewant);
            }
            else{
                pop3.showAsDropDown(viewant);
            }
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
            convertView.setAlpha(1);
            close = (ImageButton)convertView.findViewById(R.id.close_row);//其中的按钮与元件对应关系
            content = (TextView)convertView.findViewById(R.id.text_row);
            final String editContent = list.get(position);//根据位置，从列表中获取对应的元素
            content.setText(list.get(position).toString());//设置文本框的内容为获取到的元素
            //触摸时，将会把下拉列表中触摸的那个元素填入可编辑文本框中，并消灭下拉框
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

    class ConfirmAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;
        private String str;
        private TextView content;
        private Button btny;
        private Button btnn;
        public ListView listView;

        //构造方法，用于获取当前context背景和列表
        public ConfirmAdapter(Context context,String str,ListView listView) {
            this.context = context;this.str = str;this.listView = listView;
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
                    text.flag = false;
                    new Thread(new exchangeConnect(exmsg,text,listView)).start();
                    try{
                        Thread.sleep(1000);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if(exmsg.split("#")[0].equals("input")){
                        db.execSQL("update store set number = "+(numbermatch+Double.valueOf(exmsg.split("#")[1]))+" where name = '"+namematch+"'");
                    }else{
                        db.execSQL("update store set number = "+(numbermatch-Double.valueOf(exmsg.split("#")[1]))+" where name = '"+namematch+"'");
                    }
                    pop2.dismiss();
                    ChangeList(list);

                }
            });
            return convertView;
        }
    }

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

    class Updateconnect extends Thread{
        String goal = null;
        Socket socket;
        Text t = null;

        Updateconnect(String goal,Text t){
            this.goal = goal;this.t = t;
        }

        public void run(){
            try{
                socket = new Socket(ip,12000);
                PrintWriter pwr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                pwr.println("updatequery#"+goal);
                pwr.flush();
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                t.s = br.readLine();
                t.flag = true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    class exchangeConnect extends Thread{
        String str = null;
        Socket socket;
        Text t = null;
        ListView listView;
        public exchangeConnect(String str,Text t,ListView listView) {
            this.str = str;this.t = t;this.listView = listView;
        }
        public void run(){
            try{
                socket = new Socket(ip,12000);
                PrintWriter pwr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pwr.println("exchange#"+namematch+"#"+str+"#"+numbermatch);
                pwr.flush();
                t.s = br.readLine();
                System.out.println("从服务器端收到的信息是"+t.s);
                if(t.s.equals("error")) {
                    attenntionmsg = "操作失败\n这可能是网络状况不佳或者没有及时更新货物信息引起的";
                    attentionshow(listView);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class PictureConnect extends Thread{
        Socket socket;
        Text t = null;

        PictureConnect(Text t){
            this.t = t;
        }

        public void run(){
            try{
                System.out.println("开始获取图片");
                if(namematch.equals("null")) return;
                Cursor cursortemp = db.rawQuery("select * from team where name = '"+namematch+"'",null);
                while(cursortemp.moveToNext()){
                    if(cursortemp.getString(5).equals("null")){
                        db.execSQL("update store set picture = '" + namematch + ".jpg' where name = '" + namematch + "'");
                        File file = new File(ExchangeActivity.this.getFilesDir(),namematch+".jpg");
                    }
                }
                db.execSQL("update store set picture = '" + namematch + ".jpg' where name = '" + namematch + "'");
                socket = new Socket(ip,12000);
                PrintWriter pwr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                pwr.println("picturequery#"+namematch);
                pwr.flush();
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                OutputStream os = openFileOutput(namematch+".jpg",Context.MODE_PRIVATE);
                byte[] temp = new byte[1024];
                int len = -1;
                while((len = dis.read(temp))!=-1){
                    os.write(temp,0,len);
                }
                os.flush();
                os.close();
                dis.close();
                t.flag = true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    class BianliStorage extends Thread{
        public void run(){
            cursor = db.rawQuery("select * from store",null);
            while(cursor.moveToNext()){
                System.out.println(cursor.getString(0));
            }
        }

    }

    class PutinStore extends Thread{

        String temp = null;
        SQLiteDatabase db = null;

        PutinStore(String temp,SQLiteDatabase db){
            this.temp = temp;this.db = db;
        }

        public void run(){
            int i = 1;
            String temp = this.temp.split("&")[i];
            while(!temp.equals("updateend")){
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
                    temp = text.s.split("&")[i];

                }else{
                    db.execSQL("insert into store values('"+name+"','"+Double.valueOf(temp.split("#")[1])+"','"+temp.split("#")[2]+"','"+temp.split("#")[3]+"','"+temp.split("#")[4]+"','null','"+simpleDateFormat.format(new Date())+"')");
                    System.out.println("insert into store values('"+name+"','"+Double.valueOf(temp.split("#")[1])+"','"+temp.split("#")[2]+"','"+temp.split("#")[3]+"','"+temp.split("#")[4]+"','null','"+simpleDateFormat.format(new Date())+"')");
                }
            }

        }
    }



}




