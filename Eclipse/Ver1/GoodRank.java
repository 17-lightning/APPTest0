package Ver1;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;

public class GoodRank implements Runnable{
	
	public Statement statement;
	public Lock lock;
	public int level;
	
	public GoodRank(Statement statement,Lock lock,int level) {
		this.statement=statement;this.lock=lock;this.level=level;
	}
	
	public void run() {
		try {
			while(true) {
				try {
					while(lock.lock!=true) {
						Thread.sleep(100);
					}
					lock.lock=false;
					HashMap<String,String> map = new HashMap<String,String>();
					ResultSet rs = statement.executeQuery("select name,avetime from goodlist");
					while(rs.next()) {
						map.put(rs.getString(1),rs.getString(2));
					}
					Iterator<String> iter = map.keySet().iterator();
					TaoWa taowa = new TaoWa();
					int all = 1;//用于统计一共有多少个货物
					String name = iter.next();
					taowa.value=name+"-"+map.get(name);
					while(iter.hasNext()) {
						all++;
						name = iter.next();
						TaoWa taowa2 = new TaoWa();
						taowa2.value=name+"-"+map.get(name);
						DealWithTaoWa(taowa,taowa2);
					}
					OutputTaoWa(taowa,statement,1,all);
					lock.lock=true;
					Thread.sleep(600000);
				}catch(Exception e) {
					lock.lock=true;
					P.out("在货物流通性评级的过程中出现错误");
					e.printStackTrace();
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void DealWithTaoWa(TaoWa taowa1,TaoWa taowa2) {
		int time1 = Integer.valueOf(taowa1.value.split("-")[1]);
		int time2 = Integer.valueOf(taowa2.value.split("-")[1]);
		if(time1<=time2) {//如果后来者的时间更长，则将后来者变为第一位，原第一位变为第二位即它的下一位
			String value = taowa1.value;
			TaoWa taowa3 = taowa1.next;
			taowa1.value = taowa2.value;
			taowa1.next = taowa2;
			taowa2.value = value;
			taowa2.next = taowa3;
		}else {//否则比较后来者与序列中后续元素的大小直至找到对应的位置或者队尾
			if(taowa1.next==null) {taowa1.next = taowa2;}
			else {DealWithTaoWa(taowa1.next,taowa2);}
		}
	}

	public void OutputTaoWa(TaoWa taowa,Statement statement,int me,int all) {
		if(taowa==null) {P.out("一轮货物评级完毕");return;}
		try {
			int result = (me*level)/all;
			statement.execute("update goodlist set avetime = '"+taowa.value.split("-")[1]+"-"+taowa.value.split("-")[2]+"-"+result+"' where name = '"+taowa.value.split("-")[0]+"'");
			OutputTaoWa(taowa.next,statement,me+1,all);
		}catch(Exception e) {
			e.printStackTrace();
		}
		P.o(taowa.value+",");
		return ;
	}
}
