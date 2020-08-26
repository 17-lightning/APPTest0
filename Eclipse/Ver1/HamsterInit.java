package Ver1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用于初始化数据库工作状态的方法，将获取shelf中的货架信息参数并生成货架信息表单shelfwork和货位信息表单shelfcell
 */
public class HamsterInit {
	public static void run(Statement statement,int xmove,int ymove,int level) {
		//首先获悉货架的信息参数
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		List<String> list = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		try {
			//如果不存在目标表单shelfwork则创建一个，shelfcell亦然
			statement.execute("create table if not exists shelfwork(name String,x int,c int,v int)");
			statement.execute("create table if not exists shelfcell(name String,x int,y int,z int,l int,good String,time String)");
			ResultSet rSet = statement.executeQuery("select * from shelfwork");
			while(rSet.next()) {
				map.put(rSet.getString(1)+"-"+rSet.getInt(2),0);//把shelfwork表单中各货架的数据全部读取进map中
			}
			rSet = statement.executeQuery("select * from shelf");
			P.out("开始读取shelf表单的数据");
			while(rSet.next()) {
				if(map.get(rSet.getString(1)+"-"+rSet.getInt(2))==null) {//shelf表单里的这个货架在shelfwork里搜索不到，说明需要进行初始化
					//但似乎数据库不能同时被多方查询，所以关于初始化的处理我们秋后算账，先把名单加入到list中
					list.add(rSet.getString(1)+"-"+rSet.getInt(2));
					if(!list2.contains(rSet.getString(1))) list2.add(rSet.getString(1));//同时，list2用于存储所有的仓库名
					P.out("检测到未初始化的目标货架："+rSet.getString(1)+"-"+rSet.getInt(2));
				}
			}
			//这里我们假设shelfwork和shelfcell是共进退的，将他们同时处理
			int i=0;
			for(i=0;i<list.size();i++) {
				String name = list.get(i).split("-")[0];//提取出需要处理的目标仓库name目标货架x
				int x = Integer.valueOf(list.get(i).split("-")[1]);
				P.openFile(name+"-"+x+"-TaskList.txt");
				rSet = statement.executeQuery("select * from shelf where name = '"+name+"' and x = "+x);
				while(rSet.next()) {
					int y = rSet.getInt(3);//提取出目标货架的y和z值
					int z = rSet.getInt(4);
					P.out("目标货架"+name+"-"+x+"的y值是"+y+",z值是"+z);
					int m=0;int n=0;
					TaoWa taowa = new TaoWa();
					taowa.value = "1-1-"+xmove;
					for(m=1;m<=y;m++) {
						for(n=1;n<=z;n++) {
							if(m==1&&n==1) ;
							else {
								TaoWa taowa2 = new TaoWa();
								int time = m*xmove+(n-1)*ymove;
								taowa2.value = m+"-"+n+"-"+time;
								DealWithTaoWa(taowa,taowa2);
							}
						}
					}//对所有的单元格都进行套娃处理并排列之后，即得到单元格耗时从大到小的排列
					int no = OutputTaoWa(taowa);
					P.out("本货架一共有"+no+"个储物格，各个储物格耗时从大到小排列如上");
					statement.execute("insert into shelfwork values('"+name+"',"+x+","+no+","+no+")");
					//接下来处理shelfcell
					AddinTaoWa(taowa,no,level,1,statement,name,x);
				}
			}
			//在处理完了shelfwork和shelfcell之后，还要记得对goodlist的最后一个属性wide分布进行初始化操作
			StringBuffer sb = new StringBuffer();
			String aim = list2.get(0);
			sb.append(aim+"-0");
			for(i=1;i<list2.size();i++) {
				aim = list2.get(i);
				sb.append(","+aim+"-0");
			}
			statement.execute("update goodlist set wide = '"+sb.toString()+"'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void DealWithTaoWa(TaoWa taowa1,TaoWa taowa2) {
		int time1 = Integer.valueOf(taowa1.value.split("-")[2]);
		int time2 = Integer.valueOf(taowa2.value.split("-")[2]);
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
	
	public static int OutputTaoWa(TaoWa taowa) {
		if(taowa==null) {P.out();return 0;}
		P.o(taowa.value+",");
		return OutputTaoWa(taowa.next)+1;
	}
	
	public static void AddinTaoWa(TaoWa taowa,int all,int level,int me,Statement statement,String name,int x) {
		int i=1;
		while(me>(all*i)/level) {
			i++;
		}
		int y=Integer.valueOf(taowa.value.split("-")[0]);
		int z=Integer.valueOf(taowa.value.split("-")[1]);
		try {
			statement.execute("insert into shelfcell values('"+name+"',"+x+","+y+","+z+","+i+",'null','null')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(taowa.next==null) return;
		AddinTaoWa(taowa.next,all,level,me+1,statement,name,x);
	}
}
