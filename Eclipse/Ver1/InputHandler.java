package Ver1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class InputHandler implements Runnable{
	
	public String str;
	public Socket clientsocket;
	public Statement statement;
	public Lock lock;
	public int level;
	public Lock lock2;
	
	public InputHandler(String str,Socket clientsocket,Statement statement,Lock lock,int level,Lock lock2) {
		this.str=str;this.clientsocket=clientsocket;this.statement=statement;this.lock=lock;this.level=level;this.lock2=lock2;
	}
	
	public void run() {
		try {
			PrintWriter pw = new PrintWriter(clientsocket.getOutputStream());//��ȡ�����
			while(lock.lock!=true) {
				Thread.sleep(100);
			}
			lock.lock=false;
			/*while(lock2.lock!=true) {
				Thread.sleep(100);
			}
			lock2.lock=false;*/
			String name = str.split("#")[1];//���������Ļ�������
			int number = Integer.valueOf(str.split("#")[2]);//�������
			int check = Integer.valueOf(str.split("#")[3]);//ȷ�ϵ�ǰ�ֿ�ʣ������
			String op = str.split("#")[4];//�����ߵ�����
			boolean feedback = (str.split("#")[5].equals("true"));//Ŀǰ���ֶα�����������
			String aim = str.split("#")[6];//Ŀ��ֿ�
			//ִ���������
			//��һ������������Ƿ���ڣ�У����������Ƿ�ƥ��
			int xlevel = 1;String wide = null;
			boolean flag = false;boolean flag2 = false;
			ResultSet rSet = statement.executeQuery("select * from goodlist where name = '"+name+"'");
			while(rSet.next()) {
				flag = true;
				flag2 = (rSet.getInt(2)==check);
				xlevel = Integer.valueOf(rSet.getString(6).split("-")[2]);//��ͬʱ��ȡ�������ͨ������
				wide = rSet.getString(7);
			}
			if(flag!=true) {P.out("���ʱ���ִ��󣺸û��ﲻ����");pw.println("input#miss");pw.flush();lock.lock=true;return;}
			if(flag2!=true) {P.out("��������У����󣬴˲�������");pw.println("input#wrong");pw.flush();lock.lock=true;return;}
			if(xlevel==0) xlevel=1;//�����ͨ������Ϊ0��ʾ��δ���й�����������͵ȼ�1�����
			//�ڶ���������������Ƿ���ڣ�������Ȩ���Ƿ��㹻
			flag=false;
			rSet = statement.executeQuery("select * from namelist where id = '"+op+"'");
			while(rSet.next()) {
				if(rSet.getInt(3)>1) flag = true;
			}
			if(flag!=true) {P.out("������û�в���Ȩ�ޣ��˲�������");pw.println("input#error");pw.flush();lock.lock=true;return;}
			//������������ֿ������Ƿ����
			HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();//���ｫ���¼�òֿ���ÿ�����ܵ�ʣ����������ʽ��<���ܱ��,�ո�����>
			int i=1;
			rSet=statement.executeQuery("select * from shelfwork where name = '"+aim+"'");//��ȡĿ��ֿ�ĸ�����������Ϣ
			while(rSet.next()) {
				map.put(rSet.getInt(2),rSet.getInt(4));
			}
			int v0=0;
			while(map.containsKey(i)) {
				v0=v0+map.get(i);
				i++;
			}
			if(v0<=number) {P.out("���ڲֿ�ʣ���������㣬�˲�������");pw.println("input#over");pw.flush();lock.lock=true;return;}
			//���Ĳ�����ʼ��ʽ���
			//���Ȱ��ո�����ʣ�������������Ļ���
			i=1;int j=1;int v=0;
			while(map.containsKey(i)) {
				v=v+map.get(i);
				int t=0;
				while(j<=(number*v)/v0) {
					t++;j++;
				}
				statement.execute("update shelfwork set v = "+(map.get(i)-t)+" where name = '"+aim+"' and x = "+i);//��shelfwork����������ȥ
				seniorinput(name,aim,i,level,xlevel,statement,t);//��ʼ��shelfcell���в���
				i++;
			}
			//���¾߱����ڸû�������һ��wide�и�����Ϣ
			int length = wide.split(",").length;
			StringBuffer sb = new StringBuffer();
			for(i=0;i<length;i++) {
				if(wide.split(",")[i].split("-")[0].equals(aim)) {
					sb.append(wide.split(",")[i].split("-")[0]+"-"+(Integer.valueOf(wide.split(",")[i].split("-")[1])+number));
				}else sb.append(wide.split(",")[i]);
			}
			statement.execute("update goodlist set wide = '"+sb+"',number = "+(number+check)+" where name = '"+name+"'");
			pw.println("input#success");pw.flush();lock.lock=true;
		}catch(Exception e) {
			P.out("���ʱ�����쳣");
			PrintWriter pw;
			try {
				pw = new PrintWriter(clientsocket.getOutputStream());
				pw.println("input#exception");pw.flush();
			} catch (IOException e1) {
				P.out("�쳣���쳣�����ǰɰ�sir");
				e1.printStackTrace();
			}
			lock.lock=true;
			e.printStackTrace();
		}
	}
	
	/**
	 * �÷������ã���t������name��Ž�Ŀ��ֿ�i�Ż��ܣ����ܱ���Ϊlevel���ȼ������������ͨ��������xlevel
	 */
	public static void seniorinput(String name,String aim,int x,int level,int xlevel,Statement statement,int t) {
		try {
			List<String> list=P.check(aim+"-"+x);
			Random r = new Random();
			ResultSet rs = statement.executeQuery("select * from shelfcell where name = '"+aim+"' and x = "+x+" and good = 'null'");//��ȡĿ��ֿ�aimĿ�����x�����пո���Ϣ
			int v = 0;int i=0;
			HashMap<Integer,String> map = new HashMap<Integer,String>();//map�д洢�����ȿ��ô���񣬸�ʽ��<���,y-z>
			HashMap<Integer,String> map2 = new HashMap<Integer,String>();//map2�д洢��ȫ�����ô���񣬸�ʽ��<���,y-z>
			while(rs.next()) {
				int l = rs.getInt(5);
				map2.put(i,rs.getInt(3)+"-"+rs.getInt(4));i++;
				if(l>xlevel) {//���ȷ����У��ȼ����ߵĿո���һ��ϵ�������������
					if(r.nextInt(l-xlevel)==0) {map.put(v,rs.getInt(3)+"-"+rs.getInt(4));v++;}
				}else if(l==xlevel) {
					map.put(v,rs.getInt(3)+"-"+rs.getInt(4));v++;
				}
			}
			if(v>t) {//�����������С�����ȷ�����������ֱ�ӽ��з���
				int j=0;
				while(j<t) {
					int rng = r.nextInt(map.size());
					String temp = map.get(rng);
					map.put(rng,map.get(map.size()-1));
					map.remove(map.size()-1);
					String y = temp.split("-")[0];
					String z = temp.split("-")[1];
					if(list.contains(y+","+z)) {
						P.reload(aim+"-"+z);
					}
					statement.execute("update shelfcell set good = '"+name+"',time = '"+P.getTime()+"' where name = '"+aim+"' and x = "+x+" and y = "+y+" and z = "+z);
					//sb.append("in,"+y+","+z);
					P.out("��"+aim+"-"+x+","+y+","+z+"����");
					P.addtoTXT(aim+"-"+x+"-TaskList.txt","in,"+y+","+z);
					j++;
				}
			}
			else {
				int j=0;
				while(j<t) {
					int rng = r.nextInt(map2.size());
					String temp = map2.get(rng);
					map2.remove(rng);
					String y = temp.split("-")[0];
					String z = temp.split("-")[1];
					statement.execute("update shelfcell set good = '"+name+"',time = '"+P.getTime()+"' where name = '"+aim+"' and x = "+x+" and y = "+y+" and z = "+z);
					//sb.append("in,"+y+","+z);
					P.addtoTXT(aim+"-"+x+"-TaskList.txt","in,"+y+","+z);
					j++;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
