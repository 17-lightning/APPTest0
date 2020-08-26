package Ver1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginHandler implements Runnable{
	
	String str;
	Socket clientSocket = null;
	Statement statement = null;
	Lock lock;
	
	public LoginHandler(String str,Socket clientSocket,Statement statement,Lock lock) {
		this.str = str;this.clientSocket = clientSocket;this.statement = statement;this.lock=lock;
	}
	
	public void run() {
		try {
			//�ȴ����ݿ��ͬ����
			while(lock.lock!=true) {
				Thread.sleep(100);
			}
			//�ر�ͬ����
			lock.lock = false;
			//����id�Ƿ����
			boolean idtrue = false;
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());//��ȡ�����
			String id = str.split("#")[1];//������������ȡ�˺�
			String password = str.split("#")[2];//������������ȡ����
			ResultSet rSet = statement.executeQuery("select * from namelist where id like '"+id.toString()+"'");
			while(rSet.next()) {
				String passwordx = rSet.getString(2);
				idtrue = true;
				if(password.equals(passwordx)) {//����˺Ŵ���������ƥ�䣬���ص�¼�ɹ�����Ϣ
					pw.println("login#success#"+rSet.getInt(3));pw.flush();System.out.println("login#success#"+rSet.getInt(3));lock.lock=true;return;
				}
				else {//����˺Ŵ��ڵ����벻ƥ�䣬���ص�¼�������Ϣ
					pw.println("login#wrong");System.out.println("login#wrong");pw.flush();lock.lock=true;return;
				}
			}//����˺Ų����ڣ����ص�¼�쳣����Ϣ
			pw.println("login#error");pw.flush();System.out.println("login#error");lock.lock=true;return;
		} catch (Exception e) {
			P.out("��¼ʱ�����쳣");
			PrintWriter pw;
			try {
				pw = new PrintWriter(clientSocket.getOutputStream());
				pw.println("login#exception");pw.flush();
			} catch (IOException e1) {
				P.out("�쳣���쳣�����ǰɰ�sir");
				e1.printStackTrace();
			}
			lock.lock=true;
			// TODO Auto-generated catch block
			e.printStackTrace();
			lock.lock=true;
		}
	}
}
