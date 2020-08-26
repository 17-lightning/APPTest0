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
			//对同步锁进行处理
			while(lock.lock!=true) {
				Thread.sleep(100);
			}
			lock.lock = false;
			String name = str.split("#")[1];//name是需要处理的货物名称
			ResultSet rs = statement.executeQuery("select * from goodlist where name = '"+name+"'");
			while(rs.next()) {
				temp = rs.getString(7);//这里就存储有货物在各个仓库里的分布
			}
			HashMap<String,Integer> map = new HashMap<String,Integer>();//这里存储有各个仓库的剩余容量
			rs = statement.executeQuery("select name,v from shelfwork");
			while(rs.next()) {
				String aim = rs.getString(1);
				if(map.containsKey(aim)) {//如果目标仓库已经在map中存在，说明此条记录是本仓库的其它货架
					int oldvalue = map.get(aim);
					map.replace(aim,oldvalue,oldvalue+rs.getInt(2));//将这个货架的容量也加入此仓库容量中
				}else {
					map.put(aim,rs.getInt(2));
				}
			}
			//现在已经获得了各仓库的余量以及货物在各仓库中的分布，接下来获取货物在每个仓库里的分布
			StringBuffer sb = new StringBuffer();
			sb.append("check#start#"+name+"#&");
			int i=0;int j=temp.split(",").length;
			while(i<j) {
				sb.append(temp.split(",")[i].split("-")[0]+"|"+temp.split(",")[i].split("-")[1]+"|"+map.get(temp.split(",")[i].split("-")[0])+"&");
				i++;
			}
			sb.append("end");
			PrintWriter pw = new PrintWriter(clientsocket.getOutputStream());//获取输出流
			pw.println(sb.toString());
			pw.flush();
			lock.lock=true;
		}catch(Exception e) {
			P.out("在check处理中出现异常");
			PrintWriter pw;
			try {
				pw = new PrintWriter(clientsocket.getOutputStream());
				pw.println("check#exception");pw.flush();
			} catch (IOException e1) {
				P.out("异常中异常，不是吧阿sir");
				e1.printStackTrace();
			}
			lock.lock = true;
			e.printStackTrace();
		}
		lock.lock = true;
	}

}
