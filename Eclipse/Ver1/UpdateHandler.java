package Ver1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class UpdateHandler implements Runnable{
	
	public String str;
	public Socket clientSocket;
	public Statement statement;
	public Lock lock;
	
	public UpdateHandler(String str,Socket clientSocket,Statement statement,Lock lock) {
		this.clientSocket = clientSocket;
		this.str = str;
		this.statement=statement;
		this.lock=lock;
	}
	
	public void run() {
		try {
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());
			while(lock.lock!=true) {
				Thread.sleep(100);
			}
			lock.lock=false;
			if(str.split("#")[1].equals("ALL")) {//�ͻ����Ƿ���Ҫ��ȡȫ���������Ϣ
				pw.print("update#start#&");
				System.out.print("�����"+"update#start#&");
				ResultSet rSet = statement.executeQuery("select * from goodlist");
				while(rSet.next()) {
					pw.print(rSet.getString(1)+"#"+rSet.getInt(2)+"#"+rSet.getString(3)+"#"+rSet.getString(4)+"#"+rSet.getString(7)+"&");//�����������ơ���������ǩ��˵��
					System.out.print(rSet.getString(1)+"#"+rSet.getInt(2)+"#"+rSet.getString(3)+"#"+rSet.getString(4)+"#"+rSet.getString(7)+"&");
				}
				pw.println("end");
				pw.flush();
			}
			else {//����ͻ���������Ǿ���Ļ���������ֻ��Ҫ���ظû������Ϣ
				pw.print("update#start#&");
				ResultSet rSet = statement.executeQuery("select * from goodlist where name like '"+str.split("#")[1]+"'");
				while(rSet.next()) {
					pw.print(rSet.getString(1)+"#"+rSet.getInt(2)+"#"+rSet.getString(3)+"#"+rSet.getString(4)+"#"+rSet.getString(7)+"&");
					System.out.print(rSet.getString(1)+"#"+rSet.getInt(2)+"#"+rSet.getString(3)+"#"+rSet.getString(4)+"#"+rSet.getString(7)+"&");
				}
				pw.println("end");
				System.out.println("end");
				pw.flush();
			}
			lock.lock = true;
		}catch(Exception e) {
			e.printStackTrace();
			P.out("����ʱ�����쳣");
			PrintWriter pw;
			try {
				pw = new PrintWriter(clientSocket.getOutputStream());
				pw.println("update#exception");pw.flush();
			} catch (IOException e1) {
				P.out("�쳣���쳣�����ǰɰ�sir");
				e1.printStackTrace();
			}
			lock.lock=true;
			lock.lock = true;
		}
		
	}
}
