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
			if(str.split("#")[1].equals("ALL")) {//客户端是否想要获取全部货物的信息
				pw.print("update#start#&");
				System.out.print("输出："+"update#start#&");
				ResultSet rSet = statement.executeQuery("select * from goodlist");
				while(rSet.next()) {
					pw.print(rSet.getString(1)+"#"+rSet.getInt(2)+"#"+rSet.getString(3)+"#"+rSet.getString(4)+"#"+rSet.getString(7)+"&");//输出货物的名称、数量、标签、说明
					System.out.print(rSet.getString(1)+"#"+rSet.getInt(2)+"#"+rSet.getString(3)+"#"+rSet.getString(4)+"#"+rSet.getString(7)+"&");
				}
				pw.println("end");
				pw.flush();
			}
			else {//如果客户端请求的是具体的货物名，则只需要返回该货物的信息
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
			P.out("更新时出现异常");
			PrintWriter pw;
			try {
				pw = new PrintWriter(clientSocket.getOutputStream());
				pw.println("update#exception");pw.flush();
			} catch (IOException e1) {
				P.out("异常中异常，不是吧阿sir");
				e1.printStackTrace();
			}
			lock.lock=true;
			lock.lock = true;
		}
		
	}
}
