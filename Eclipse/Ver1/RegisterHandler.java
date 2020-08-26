package Ver1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class RegisterHandler implements Runnable{
	
	String str = null;
	Socket clientSocket = null;
	Statement statement = null;
	Lock lock = null;
	
	public RegisterHandler(String str,Socket clientSocket,Statement statement,Lock lock) {
		this.str = str;this.clientSocket = clientSocket;this.statement = statement;this.lock = lock;
	}
	
	public void run() {
		try {
			while(lock.lock=true) {
				Thread.sleep(100);
			}
			lock.lock=false;
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());
			Class.forName("org.sqlite.JDBC");
			boolean flag = false;
			String id = str.split("#")[1];
			String password = str.split("#")[2];
			ResultSet rSet = statement.executeQuery("select * from namelist where id = '"+id.toString()+"'");
			while(rSet.next()) {
				flag = true;
			}
			if(flag == true) {
				System.out.println("说明：由于此账号已经存在，注册账号失败");
				pw.println("register#fail");
				pw.flush();
				System.out.println("输出：register#fail");
			}else {
				statement.execute("insert into namelist values('"+id+"','"+password+"',1)");
				System.out.println("说明：注册账号："+id+"，密码是"+password);
				pw.println("register#success");
				pw.flush();
				System.out.println("输出：register#success");
			}
			lock.lock=true;
		}catch(Exception e) {
			P.out("注册时出现异常");
			PrintWriter pw;
			try {
				pw = new PrintWriter(clientSocket.getOutputStream());
				pw.println("register#exception");pw.flush();
			} catch (IOException e1) {
				P.out("异常中异常，不是吧阿sir");
				e1.printStackTrace();
			}
			lock.lock=true;
			e.printStackTrace();
			lock.lock=true;
		}
	}
}
