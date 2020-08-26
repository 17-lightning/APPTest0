package Ver1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PictureHandler implements Runnable {
	
	String str = null;
	Socket clientSocket = null;
	Statement statement = null;
	Lock lock;
	String path;
	
	public PictureHandler(String str,Socket clientSocket,Statement statement,Lock lock,String path) {
		this.clientSocket = clientSocket;
		this.str = str;
		this.statement = statement;
		this.lock = lock;
		this.path=path;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			String name = str.split("#")[1];
			System.out.println("检测到需要输出货物“"+name+"”的图片");
			ResultSet rSet = statement.executeQuery("select picture from goodlist where name = '"+name+"'");
			while(rSet.next()) {
				String picture = rSet.getString(1);
				File file = new File(path+picture+".jpg");
				InputStream fis = new FileInputStream(file);
				byte[] temp = new byte[1024];
				int len = -1;
				while((len = fis.read(temp))!=-1) {
					clientSocket.getOutputStream().write(temp,0,len);
				}
				clientSocket.getOutputStream().flush();
				clientSocket.getOutputStream().close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
//
	}

}
