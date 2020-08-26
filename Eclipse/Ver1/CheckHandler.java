package Ver1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CheckHandler implements Runnable{
	
	public Statement statement;
	public String str;
	public Socket clientsocket;
	public Lock lock;
	
	public CheckHandler(String str,Socket clientsocket,Statement statement,Lock lock) {
		this.str = str;
		this.clientsocket = clientsocket;
		this.statement = statement;
		this.lock = lock;
	}
	
	public void run() {
		try {
			String temp="";
			//��ͬ�������д���
			while(lock.lock!=true) {
				Thread.sleep(100);
			}
			lock.lock = false;
			String name = str.split("#")[1];//name����Ҫ����Ļ�������
			ResultSet rs = statement.executeQuery("select * from goodlist where name = '"+name+"'");
			while(rs.next()) {
				temp = rs.getString(7);//����ʹ洢�л����ڸ����ֿ���ķֲ�
			}
			HashMap<String,Integer> map = new HashMap<String,Integer>();//����洢�и����ֿ��ʣ������
			rs = statement.executeQuery("select name,v from shelfwork");
			while(rs.next()) {
				String aim = rs.getString(1);
				if(map.containsKey(aim)) {//���Ŀ��ֿ��Ѿ���map�д��ڣ�˵��������¼�Ǳ��ֿ����������
					int oldvalue = map.get(aim);
					map.replace(aim,oldvalue,oldvalue+rs.getInt(2));//��������ܵ�����Ҳ����˲ֿ�������
				}else {
					map.put(aim,rs.getInt(2));
				}
			}
			//�����Ѿ�����˸��ֿ�������Լ������ڸ��ֿ��еķֲ�����������ȡ������ÿ���ֿ���ķֲ�
			StringBuffer sb = new StringBuffer();
			sb.append("check#start#"+name+"#&");
			int i=0;int j=temp.split(",").length;
			while(i<j) {
				sb.append(temp.split(",")[i].split("-")[0]+"|"+temp.split(",")[i].split("-")[1]+"|"+map.get(temp.split(",")[i].split("-")[0])+"&");
				i++;
			}
			sb.append("end");
			PrintWriter pw = new PrintWriter(clientsocket.getOutputStream());//��ȡ�����
			pw.println(sb.toString());
			pw.flush();
			lock.lock=true;
		}catch(Exception e) {
			P.out("��check�����г����쳣");
			PrintWriter pw;
			try {
				pw = new PrintWriter(clientsocket.getOutputStream());
				pw.println("check#exception");pw.flush();
			} catch (IOException e1) {
				P.out("�쳣���쳣�����ǰɰ�sir");
				e1.printStackTrace();
			}
			lock.lock = true;
			e.printStackTrace();
		}
		lock.lock = true;
	}

}
