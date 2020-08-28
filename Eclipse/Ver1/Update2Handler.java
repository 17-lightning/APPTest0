package Ver1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;

public class Update2Handler implements Runnable{
	
	public String str;
	public Statement statement;
	public Socket clientsocket;
	public Lock lock;
	
	public Update2Handler(String str,Socket clientSocket,Statement statement,Lock lock) {
		this.str = str;this.clientsocket = clientSocket;this.statement = statement;this.lock = lock;
	}
	
	public void run() {
		try {
			//1.取得同步锁
			while(lock.lock!=true) {
				Thread.sleep(100);
			}
			lock.lock=false;
			//2.分析用户请求
			//String aim = str.split("#")[1];
			//3.查询仓储信息并输出
			PrintWriter pw = new PrintWriter(clientsocket.getOutputStream());//获取输出流
			StringBuffer sb = new StringBuffer();
			sb.append("update2&");
			HashMap<String,Integer> map = new HashMap<String,Integer>();
			ResultSet rs = statement.executeQuery("select * from shelf");
			while(rs.next()) {
				if(map.containsKey(rs.getString("name"))) {
					if(map.get(rs.getString("name"))<rs.getInt("x")) {
						map.put(rs.getString("name"),rs.getInt("x"));
					}
				}else {
					map.put(rs.getString("name"),rs.getInt("x"));
				}
			}
			Iterator<String> iter = map.keySet().iterator();
			while(iter.hasNext()) {
				String temp = iter.next();
				sb.append(temp+"-"+map.get(temp)+"&");
			}
			sb.append("end");
			pw.println(sb.toString());
			pw.flush();
			P.out("输出了"+sb);
			lock.lock=true;
		}catch(Exception e) {
			lock.lock=true;
			P.out("更新2时出现异常");
			PrintWriter pw;
			try {
				pw = new PrintWriter(clientsocket.getOutputStream());
				pw.println("update2#exception");pw.flush();
			} catch (IOException e1) {
				P.out("异常中异常，不是吧阿sir");
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
}
