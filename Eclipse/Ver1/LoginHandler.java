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
			//等待数据库的同步锁
			while(lock.lock!=true) {
				Thread.sleep(100);
			}
			//关闭同步锁
			lock.lock = false;
			//检验id是否存在
			boolean idtrue = false;
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());//获取输出流
			String id = str.split("#")[1];//从请求行中提取账号
			String password = str.split("#")[2];//从请求行中提取密码
			ResultSet rSet = statement.executeQuery("select * from namelist where id like '"+id.toString()+"'");
			while(rSet.next()) {
				String passwordx = rSet.getString(2);
				idtrue = true;
				if(password.equals(passwordx)) {//如果账号存在且密码匹配，返回登录成功的信息
					pw.println("login#success#"+rSet.getInt(3));pw.flush();System.out.println("login#success#"+rSet.getInt(3));lock.lock=true;return;
				}
				else {//如果账号存在但密码不匹配，返回登录错误的信息
					pw.println("login#wrong");System.out.println("login#wrong");pw.flush();lock.lock=true;return;
				}
			}//如果账号不存在，返回登录异常的信息
			pw.println("login#error");pw.flush();System.out.println("login#error");lock.lock=true;return;
		} catch (Exception e) {
			P.out("登录时出现异常");
			PrintWriter pw;
			try {
				pw = new PrintWriter(clientSocket.getOutputStream());
				pw.println("login#exception");pw.flush();
			} catch (IOException e1) {
				P.out("异常中异常，不是吧阿sir");
				e1.printStackTrace();
			}
			lock.lock=true;
			// TODO Auto-generated catch block
			e.printStackTrace();
			lock.lock=true;
		}
	}
}
