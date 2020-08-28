package Ver1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class OutputHandler implements Runnable{
	
	public String str;
	public Socket clientsocket;
	public Statement statement;
	public Lock lock;
	public int ilevel;
	
	public OutputHandler(String str,Socket clientsocket,Statement statement,Lock lock,int level) {
		this.str = str;this.clientsocket = clientsocket;this.statement = statement;this.lock = lock;this.ilevel=level;
	}
	
	public void run() {
		try {
			//1.获取同步锁
			PrintWriter pw = new PrintWriter(clientsocket.getOutputStream());//获取输出流
			while(lock.lock!=true) {
				Thread.sleep(100);
			}
			lock.lock=false;
			//2.获取请求行中的所有信息，包括目标货物目标仓库目标数量、检验位、操作员
			String name = str.split("#")[1];//即将操作的货物名称
			int number = Integer.valueOf(str.split("#")[2]);//入库数量
			int check = Integer.valueOf(str.split("#")[3]);//确认当前仓库剩余数量
			String op = str.split("#")[4];//操作者的名称
			boolean feedback = (str.split("#")[5].equals("true"));//目前该字段保留，无意义
			String aim = str.split("#")[6];//目标仓库
			//3.执行入库手续
			//3-1.第一步，检验货物是否存在，校验货物余量是否匹配
			String wide = null;
			int oldtime = 0;
			int oldqutt = 0;
			int level = 0;
			boolean flag = false;boolean flag2 = false;
			ResultSet rSet = statement.executeQuery("select * from goodlist where name = '"+name+"'");
			while(rSet.next()) {
				flag = true;
				flag2 = (rSet.getInt(2)==check);
				wide = rSet.getString(7);//获得货物在各个仓库里的分布
				oldtime = Integer.valueOf(rSet.getString(6).split("-")[0]);
				oldqutt = Integer.valueOf(rSet.getString(6).split("-")[1]);
				level = Integer.valueOf(rSet.getString(6).split("-")[2]);
			}
			if(flag!=true) {P.out("入库时出现错误：该货物不存在");pw.println("output#miss");pw.flush();lock.lock=true;return;}
			if(flag2!=true) {P.out("货物余量校验错误，此操作作废");pw.println("output#wrong");pw.flush();lock.lock=true;return;}
			//3-2.检验操作者是否存在，操作者权限是否足够
			flag=false;
			rSet = statement.executeQuery("select * from namelist where id = '"+op+"'");
			while(rSet.next()) {
				if(rSet.getInt(3)>1) flag = true;
			}
			if(flag!=true) {P.out("操作者没有操作权限，此操作作废");pw.println("output#error");pw.flush();lock.lock=true;return;}
			//3-3.第三步，检验仓库余量是否充足，同时提前规划好到时候要更新入goodlist的wide行的语句，但执行在之后进行
			StringBuffer sb = new StringBuffer();
			HashMap<String,Integer> map = new HashMap<String,Integer>();//这里将会记录每个仓库中的货物储量
			int i=0;int length = wide.split(",").length;
			int space=0;
			for(i=0;i<length;i++) {
				String namex = wide.split(",")[i].split("-")[0];
				int left = Integer.valueOf(wide.split(",")[i].split("-")[1]);
				map.put(name,left);
				if(namex.equals(aim)) {space=left;sb.append(((i==0)?"":",")+namex+"-"+(left-number));}
				else {sb.append(((i==0)?"":",")+name+"-"+left);}
			}
			if(space<=number) {P.out("由于仓库剩余储量不足，此操作作废");pw.println("output#over");pw.flush();lock.lock=true;return;}
			//3-4.开始正式出库
			//首先按照各货架剩余容量分配出库的货物
			rSet = statement.executeQuery("select * from shelfcell where name = '"+aim+"' and good = '"+name+"'");
			HashMap<Integer,HashMap<Integer,List<String>>> maps = new HashMap<Integer,HashMap<Integer,List<String>>>();
			//maps的结构是，key1为货架编码x，key2为流通等级l，List中以"y-z-&2020-08-19 13：06：16&"的方式存储着目标位置//####################
			//3-4-1.获取货物在每个货架上每个优先级的分布
			while(rSet.next()) {
				P.out(aim+"仓库里的"+name+"货物分布在"+rSet.getInt(3)+"-"+rSet.getInt(4));
				if(maps.containsKey(rSet.getInt(2))) {
					HashMap<Integer,List<String>> lyg = maps.get(rSet.getInt(2));
					if(lyg.containsKey(rSet.getInt(5))) {
						List<String> J82 = lyg.get(rSet.getInt(5));
						J82.add(rSet.getInt(3)+"-"+rSet.getInt(4)+"-&"+rSet.getString(7));
					}else {
						List<String> J82 = new ArrayList<String>();
						J82.add(rSet.getInt(3)+"-"+rSet.getInt(4)+"-&"+rSet.getString(7));
						lyg.put(rSet.getInt(5), J82);
					}
				}else {
					HashMap<Integer,List<String>> lyg = new HashMap<Integer,List<String>>();
					List<String> J82 = new ArrayList<String>();
					J82.add(rSet.getInt(3)+"-"+rSet.getInt(4)+"-&"+rSet.getString(7));
					lyg.put(rSet.getInt(5), J82);
					maps.put(rSet.getInt(2),lyg);
				}
			}
			//3-4-2.根据货物在各货架上的分布，计算出每个货架应该出库多少货物，并直接调用出库方法
			i=1;int v=0;
			int c=0;int time=0;
			while(i<=ilevel) {//如果i号货架存在（从1号开始）
				if(maps.containsKey(i)) {
					int t = getInnerSize(maps.get(i));//获取i号货架上的货物数量
					int t2 = (number*(v+t)/space)-(number*v/space);//获取应该从i号货架出库的货物数量
					v=v+t;
					P.out("t="+t+",number="+number+",space="+space+",v="+v);
					P.out("从"+i+"号货架上获取"+t2+"个货物");
					String temp = senioroutput(t2,name,aim,i,maps.get(i),ilevel,statement);//调用出库方法
					c=c+Integer.valueOf(temp.split("-")[0]);
					time=time+Integer.valueOf(temp.split("-")[1]);
				}
				i++;
			}
			//3-4-3.货物已经全部入库，shelfcell处理完毕，接下来对shelfwork,goodlist进行更新信息
			//但是这里还要先计算出货物的新avetime
			int newqutt = oldqutt+c;
			P.out("c="+c);
			P.out("time="+time);
			int newtime = ((oldqutt*oldtime*99)+(c*time*100))/((c+oldqutt)*100);
			String newavetime = newtime+"-"+newqutt+"-"+level;
			statement.execute("update goodlist set wide = '"+sb.toString()+"',avetime = '"+newavetime+"' where name = '"+name+"'");
			
			pw.println("output#success");pw.flush();lock.lock=true;
		}catch(Exception e) {
			P.out("出库时出现异常");
			PrintWriter pw;
			try {
				pw = new PrintWriter(clientsocket.getOutputStream());
				pw.println("output#exception");pw.flush();
			} catch (IOException e1) {
				P.out("异常中异常，不是吧阿sir");
				e1.printStackTrace();
			}
			lock.lock=true;
			e.printStackTrace();
		}
	}
	
	/**
	 * 该方法作用：把t个货物name存放进目标仓库aim的i号货架，货架上的货物分布被存储在maps<Integer,List<String>>中，key值为
	 */
	public static String senioroutput(int t,String name,String aim,int x,HashMap<Integer,List<String>> maps,int level,Statement statement) {
		String str = null;
		try {
			int i=level;boolean flag = false;int j=0;int time=0;
			while(j<t) {
				while(!maps.containsKey(i)) {//找到流通等级最高的不为空的格子
					i--;
				}
				List<String> list = maps.get(i);
				Iterator<String> iter = list.iterator();
				while(iter.hasNext()&&(j<t)) {
					String temp = iter.next();
					int y = Integer.valueOf(temp.split("-")[0]);
					int z = Integer.valueOf(temp.split("-")[1]);
					P.out("temp:"+temp);
					time = time+P.minusTime(temp.split("&")[1]);
					List<String> list2 = P.check(aim+"-"+x);
					if(list2.contains(y+"-"+z)) {
						P.reload(aim+"-"+x);
					}
					P.out("从"+aim+"-"+x+","+y+","+z+"进货");
					statement.execute("update shelfcell set good = 'null',time = 'null' where name = '"+aim+"' and x = "+x+" and y = "+y+" and z = "+z);
					P.addtoTXT(aim+"-"+x+"-TaskList.txt","out,"+y+","+z);
					j++;
				}
			}
			str=t+"-"+time;
			int v=0;
			ResultSet rs = statement.executeQuery("select v from shelfwork where name = '"+aim+"' and x = "+x);
			while(rs.next()) {
				v=rs.getInt(1);
			}
			statement.execute("update shelfwork set v = "+(v+t)+" where name = '"+aim+"' and x = "+x);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 获取HashMap<,List>类数据格式的大小
	 */
	public static int getInnerSize(HashMap<Integer,List<String>> map) {
		int i=1;int v=0;
		Iterator<Integer> iter = map.keySet().iterator();
		while(iter.hasNext()) {
			int x = iter.next();
			v=v+map.get(x).size();
		}
		/*
		while(map.containsKey(i)) {
			v=v+map.get(i).size();
			i++;
		}*/
		return v;
	}
	
	/**
	 * 具体到一个格子的货物处理从这里开始
	 * 需要做的事情有:
	 * 更新shelfcell的aim-x-y-z格子的good信息，并记录其time信息
	 * 
	 */
	public static void junioroutput(String name,String aim,int x,int y,int z) {
		
	}
}

