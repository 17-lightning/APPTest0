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
			PrintWriter pw = new PrintWriter(clientsocket.getOutputStream());//获取输出流
			while(lock.lock!=true) {
				Thread.sleep(100);
			}
			lock.lock=false;
			/*while(lock2.lock!=true) {
				Thread.sleep(100);
			}
			lock2.lock=false;*/
			String name = str.split("#")[1];//即将操作的货物名称
			int number = Integer.valueOf(str.split("#")[2]);//入库数量
			int check = Integer.valueOf(str.split("#")[3]);//确认当前仓库剩余数量
			String op = str.split("#")[4];//操作者的名称
			boolean feedback = (str.split("#")[5].equals("true"));//目前该字段保留，无意义
			String aim = str.split("#")[6];//目标仓库
			//执行入库手续
			//第一步，检验货物是否存在，校验货物余量是否匹配
			int xlevel = 1;String wide = null;
			boolean flag = false;boolean flag2 = false;
			ResultSet rSet = statement.executeQuery("select * from goodlist where name = '"+name+"'");
			while(rSet.next()) {
				flag = true;
				flag2 = (rSet.getInt(2)==check);
				xlevel = Integer.valueOf(rSet.getString(6).split("-")[2]);//并同时获取货物的流通性评级
				wide = rSet.getString(7);
			}
			if(flag!=true) {P.out("入库时出现错误：该货物不存在");pw.println("input#miss");pw.flush();lock.lock=true;return;}
			if(flag2!=true) {P.out("货物余量校验错误，此操作作废");pw.println("input#wrong");pw.flush();lock.lock=true;return;}
			if(xlevel==0) xlevel=1;//如果流通性评级为0表示还未进行过评级，以最低等级1级替代
			//第二部，检验操作者是否存在，操作者权限是否足够
			flag=false;
			rSet = statement.executeQuery("select * from namelist where id = '"+op+"'");
			while(rSet.next()) {
				if(rSet.getInt(3)>1) flag = true;
			}
			if(flag!=true) {P.out("操作者没有操作权限，此操作作废");pw.println("input#error");pw.flush();lock.lock=true;return;}
			//第三步，检验仓库余量是否充足
			HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();//这里将会记录该仓库中每个货架的剩余容量，格式是<货架编号,空格数量>
			int i=1;
			rSet=statement.executeQuery("select * from shelfwork where name = '"+aim+"'");//获取目标仓库的各货架余量信息
			while(rSet.next()) {
				map.put(rSet.getInt(2),rSet.getInt(4));
			}
			int v0=0;
			while(map.containsKey(i)) {
				v0=v0+map.get(i);
				i++;
			}
			if(v0<=number) {P.out("由于仓库剩余容量不足，此操作作废");pw.println("input#over");pw.flush();lock.lock=true;return;}
			//第四步，开始正式入库
			//首先按照各货架剩余容量分配入库的货物
			i=1;int j=1;int v=0;
			while(map.containsKey(i)) {
				v=v+map.get(i);
				int t=0;
				while(j<=(number*v)/v0) {
					t++;j++;
				}
				statement.execute("update shelfwork set v = "+(map.get(i)-t)+" where name = '"+aim+"' and x = "+i);//在shelfwork处将余量减去
				seniorinput(name,aim,i,level,xlevel,statement,t);//开始对shelfcell进行操作
				i++;
			}
			//完事具备，在该货物的最后一列wide中更新信息
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
			P.out("入库时出现异常");
			PrintWriter pw;
			try {
				pw = new PrintWriter(clientsocket.getOutputStream());
				pw.println("input#exception");pw.flush();
			} catch (IOException e1) {
				P.out("异常中异常，不是吧阿sir");
				e1.printStackTrace();
			}
			lock.lock=true;
			e.printStackTrace();
		}
	}
	
	/**
	 * 该方法作用：把t个货物name存放进目标仓库i号货架，货架被分为level个等级，而货物的流通性评级是xlevel
	 */
	public static void seniorinput(String name,String aim,int x,int level,int xlevel,Statement statement,int t) {
		try {
			List<String> list=P.check(aim+"-"+x);
			Random r = new Random();
			ResultSet rs = statement.executeQuery("select * from shelfcell where name = '"+aim+"' and x = "+x+" and good = 'null'");//获取目标仓库aim目标货架x的所有空格信息
			int v = 0;int i=0;
			HashMap<Integer,String> map = new HashMap<Integer,String>();//map中存储有优先可用储存格，格式是<编号,y-z>
			HashMap<Integer,String> map2 = new HashMap<Integer,String>();//map2中存储有全部可用储存格，格式是<编号,y-z>
			while(rs.next()) {
				int l = rs.getInt(5);
				map2.put(i,rs.getInt(3)+"-"+rs.getInt(4));i++;
				if(l>xlevel) {//优先分配中，等级更高的空格以一定系数计入可用容量
					if(r.nextInt(l-xlevel)==0) {map.put(v,rs.getInt(3)+"-"+rs.getInt(4));v++;}
				}else if(l==xlevel) {
					map.put(v,rs.getInt(3)+"-"+rs.getInt(4));v++;
				}
			}
			if(v>t) {//如果进货数量小于优先分配容量，则直接进行分配
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
					P.out("向"+aim+"-"+x+","+y+","+z+"进货");
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
