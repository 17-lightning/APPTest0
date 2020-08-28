package Ver1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class OutputHandler implements Runnable{
	
	public String str;
	public Socket clientsocket;
	public Statement statement;
	public Lock lock;
	public int ilevel;
	
	public OutputHandler(String str,Socket clientsocket,Statement statement,Lock lock,int level) {
		this.str = str;this.clientsocket = clientsocket;this.statement = statement;this.lock = lock;this.ilevel=level;
	}
	
	public void run() {
		try {
			//1.��ȡͬ����
			PrintWriter pw = new PrintWriter(clientsocket.getOutputStream());//��ȡ�����
			while(lock.lock!=true) {
				Thread.sleep(100);
			}
			lock.lock=false;
			//2.��ȡ�������е�������Ϣ������Ŀ�����Ŀ��ֿ�Ŀ������������λ������Ա
			String name = str.split("#")[1];//���������Ļ�������
			int number = Integer.valueOf(str.split("#")[2]);//�������
			int check = Integer.valueOf(str.split("#")[3]);//ȷ�ϵ�ǰ�ֿ�ʣ������
			String op = str.split("#")[4];//�����ߵ�����
			boolean feedback = (str.split("#")[5].equals("true"));//Ŀǰ���ֶα�����������
			String aim = str.split("#")[6];//Ŀ��ֿ�
			//3.ִ���������
			//3-1.��һ������������Ƿ���ڣ�У����������Ƿ�ƥ��
			String wide = null;
			int oldtime = 0;
			int oldqutt = 0;
			int level = 0;
			boolean flag = false;boolean flag2 = false;
			ResultSet rSet = statement.executeQuery("select * from goodlist where name = '"+name+"'");
			while(rSet.next()) {
				flag = true;
				flag2 = (rSet.getInt(2)==check);
				wide = rSet.getString(7);//��û����ڸ����ֿ���ķֲ�
				oldtime = Integer.valueOf(rSet.getString(6).split("-")[0]);
				oldqutt = Integer.valueOf(rSet.getString(6).split("-")[1]);
				level = Integer.valueOf(rSet.getString(6).split("-")[2]);
			}
			if(flag!=true) {P.out("���ʱ���ִ��󣺸û��ﲻ����");pw.println("output#miss");pw.flush();lock.lock=true;return;}
			if(flag2!=true) {P.out("��������У����󣬴˲�������");pw.println("output#wrong");pw.flush();lock.lock=true;return;}
			//3-2.����������Ƿ���ڣ�������Ȩ���Ƿ��㹻
			flag=false;
			rSet = statement.executeQuery("select * from namelist where id = '"+op+"'");
			while(rSet.next()) {
				if(rSet.getInt(3)>1) flag = true;
			}
			if(flag!=true) {P.out("������û�в���Ȩ�ޣ��˲�������");pw.println("output#error");pw.flush();lock.lock=true;return;}
			//3-3.������������ֿ������Ƿ���㣬ͬʱ��ǰ�滮�õ�ʱ��Ҫ������goodlist��wide�е���䣬��ִ����֮�����
			StringBuffer sb = new StringBuffer();
			HashMap<String,Integer> map = new HashMap<String,Integer>();//���ｫ���¼ÿ���ֿ��еĻ��ﴢ��
			int i=0;int length = wide.split(",").length;
			int space=0;
			for(i=0;i<length;i++) {
				String namex = wide.split(",")[i].split("-")[0];
				int left = Integer.valueOf(wide.split(",")[i].split("-")[1]);
				map.put(name,left);
				if(namex.equals(aim)) {space=left;sb.append(((i==0)?"":",")+namex+"-"+(left-number));}
				else {sb.append(((i==0)?"":",")+name+"-"+left);}
			}
			if(space<=number) {P.out("���ڲֿ�ʣ�ഢ�����㣬�˲�������");pw.println("output#over");pw.flush();lock.lock=true;return;}
			//3-4.��ʼ��ʽ����
			//���Ȱ��ո�����ʣ�������������Ļ���
			rSet = statement.executeQuery("select * from shelfcell where name = '"+aim+"' and good = '"+name+"'");
			HashMap<Integer,HashMap<Integer,List<String>>> maps = new HashMap<Integer,HashMap<Integer,List<String>>>();
			//maps�Ľṹ�ǣ�key1Ϊ���ܱ���x��key2Ϊ��ͨ�ȼ�l��List����"y-z-&2020-08-19 13��06��16&"�ķ�ʽ�洢��Ŀ��λ��//####################
			//3-4-1.��ȡ������ÿ��������ÿ�����ȼ��ķֲ�
			while(rSet.next()) {
				P.out(aim+"�ֿ����"+name+"����ֲ���"+rSet.getInt(3)+"-"+rSet.getInt(4));
				if(maps.containsKey(rSet.getInt(2))) {
					HashMap<Integer,List<String>> lyg = maps.get(rSet.getInt(2));
					if(lyg.containsKey(rSet.getInt(5))) {
						List<String> J82 = lyg.get(rSet.getInt(5));
						J82.add(rSet.getInt(3)+"-"+rSet.getInt(4)+"-&"+rSet.getString(7));
					}else {
						List<String> J82 = new ArrayList<String>();
						J82.add(rSet.getInt(3)+"-"+rSet.getInt(4)+"-&"+rSet.getString(7));
						lyg.put(rSet.getInt(5), J82);
					}
				}else {
					HashMap<Integer,List<String>> lyg = new HashMap<Integer,List<String>>();
					List<String> J82 = new ArrayList<String>();
					J82.add(rSet.getInt(3)+"-"+rSet.getInt(4)+"-&"+rSet.getString(7));
					lyg.put(rSet.getInt(5), J82);
					maps.put(rSet.getInt(2),lyg);
				}
			}
			//3-4-2.���ݻ����ڸ������ϵķֲ��������ÿ������Ӧ�ó�����ٻ����ֱ�ӵ��ó��ⷽ��
			i=1;int v=0;
			int c=0;int time=0;
			while(i<=ilevel) {//���i�Ż��ܴ��ڣ���1�ſ�ʼ��
				if(maps.containsKey(i)) {
					int t = getInnerSize(maps.get(i));//��ȡi�Ż����ϵĻ�������
					int t2 = (number*(v+t)/space)-(number*v/space);//��ȡӦ�ô�i�Ż��ܳ���Ļ�������
					v=v+t;
					P.out("t="+t+",number="+number+",space="+space+",v="+v);
					P.out("��"+i+"�Ż����ϻ�ȡ"+t2+"������");
					String temp = senioroutput(t2,name,aim,i,maps.get(i),ilevel,statement);//���ó��ⷽ��
					c=c+Integer.valueOf(temp.split("-")[0]);
					time=time+Integer.valueOf(temp.split("-")[1]);
				}
				i++;
			}
			//3-4-3.�����Ѿ�ȫ����⣬shelfcell������ϣ���������shelfwork,goodlist���и�����Ϣ
			//�������ﻹҪ�ȼ�����������avetime
			int newqutt = oldqutt+c;
			P.out("c="+c);
			P.out("time="+time);
			int newtime = ((oldqutt*oldtime*99)+(c*time*100))/((c+oldqutt)*100);
			String newavetime = newtime+"-"+newqutt+"-"+level;
			statement.execute("update goodlist set wide = '"+sb.toString()+"',avetime = '"+newavetime+"' where name = '"+name+"'");
			
			pw.println("output#success");pw.flush();lock.lock=true;
		}catch(Exception e) {
			P.out("����ʱ�����쳣");
			PrintWriter pw;
			try {
				pw = new PrintWriter(clientsocket.getOutputStream());
				pw.println("output#exception");pw.flush();
			} catch (IOException e1) {
				P.out("�쳣���쳣�����ǰɰ�sir");
				e1.printStackTrace();
			}
			lock.lock=true;
			e.printStackTrace();
		}
	}
	
	/**
	 * �÷������ã���t������name��Ž�Ŀ��ֿ�aim��i�Ż��ܣ������ϵĻ���ֲ����洢��maps<Integer,List<String>>�У�keyֵΪ
	 */
	public static String senioroutput(int t,String name,String aim,int x,HashMap<Integer,List<String>> maps,int level,Statement statement) {
		String str = null;
		try {
			int i=level;boolean flag = false;int j=0;int time=0;
			while(j<t) {
				while(!maps.containsKey(i)) {//�ҵ���ͨ�ȼ���ߵĲ�Ϊ�յĸ���
					i--;
				}
				List<String> list = maps.get(i);
				Iterator<String> iter = list.iterator();
				while(iter.hasNext()&&(j<t)) {
					String temp = iter.next();
					int y = Integer.valueOf(temp.split("-")[0]);
					int z = Integer.valueOf(temp.split("-")[1]);
					P.out("temp:"+temp);
					time = time+P.minusTime(temp.split("&")[1]);
					List<String> list2 = P.check(aim+"-"+x);
					if(list2.contains(y+"-"+z)) {
						P.reload(aim+"-"+x);
					}
					P.out("��"+aim+"-"+x+","+y+","+z+"����");
					statement.execute("update shelfcell set good = 'null',time = 'null' where name = '"+aim+"' and x = "+x+" and y = "+y+" and z = "+z);
					P.addtoTXT(aim+"-"+x+"-TaskList.txt","out,"+y+","+z);
					j++;
				}
			}
			str=t+"-"+time;
			int v=0;
			ResultSet rs = statement.executeQuery("select v from shelfwork where name = '"+aim+"' and x = "+x);
			while(rs.next()) {
				v=rs.getInt(1);
			}
			statement.execute("update shelfwork set v = "+(v+t)+" where name = '"+aim+"' and x = "+x);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * ��ȡHashMap<,List>�����ݸ�ʽ�Ĵ�С
	 */
	public static int getInnerSize(HashMap<Integer,List<String>> map) {
		int i=1;int v=0;
		Iterator<Integer> iter = map.keySet().iterator();
		while(iter.hasNext()) {
			int x = iter.next();
			v=v+map.get(x).size();
		}
		/*
		while(map.containsKey(i)) {
			v=v+map.get(i).size();
			i++;
		}*/
		return v;
	}
	
	/**
	 * ���嵽һ�����ӵĻ��ﴦ������￪ʼ
	 * ��Ҫ����������:
	 * ����shelfcell��aim-x-y-z���ӵ�good��Ϣ������¼��time��Ϣ
	 * 
	 */
	public static void junioroutput(String name,String aim,int x,int y,int z) {
		
	}
}

