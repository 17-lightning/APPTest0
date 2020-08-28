package Ver1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ���ڳ�ʼ�����ݿ⹤��״̬�ķ���������ȡshelf�еĻ�����Ϣ���������ɻ�����Ϣ��shelfwork�ͻ�λ��Ϣ��shelfcell
 */
public class HamsterInit {
	public static void run(Statement statement,int xmove,int ymove,int level) {
		//���Ȼ�Ϥ���ܵ���Ϣ����
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		List<String> list = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		try {
			//���������Ŀ���shelfwork�򴴽�һ����shelfcell��Ȼ
			statement.execute("create table if not exists shelfwork(name String,x int,c int,v int)");
			statement.execute("create table if not exists shelfcell(name String,x int,y int,z int,l int,good String,time String)");
			ResultSet rSet = statement.executeQuery("select * from shelfwork");
			while(rSet.next()) {
				map.put(rSet.getString(1)+"-"+rSet.getInt(2),0);//��shelfwork���и����ܵ�����ȫ����ȡ��map��
			}
			rSet = statement.executeQuery("select * from shelf");
			P.out("��ʼ��ȡshelf��������");
			while(rSet.next()) {
				if(map.get(rSet.getString(1)+"-"+rSet.getInt(2))==null) {//shelf��������������shelfwork������������˵����Ҫ���г�ʼ��
					//���ƺ����ݿⲻ��ͬʱ���෽��ѯ�����Թ��ڳ�ʼ���Ĵ�������������ˣ��Ȱ��������뵽list��
					list.add(rSet.getString(1)+"-"+rSet.getInt(2));
					if(!list2.contains(rSet.getString(1))) list2.add(rSet.getString(1));//ͬʱ��list2���ڴ洢���еĲֿ���
					P.out("��⵽δ��ʼ����Ŀ����ܣ�"+rSet.getString(1)+"-"+rSet.getInt(2));
				}
			}
			//�������Ǽ���shelfwork��shelfcell�ǹ����˵ģ�������ͬʱ����
			int i=0;
			for(i=0;i<list.size();i++) {
				String name = list.get(i).split("-")[0];//��ȡ����Ҫ�����Ŀ��ֿ�nameĿ�����x
				int x = Integer.valueOf(list.get(i).split("-")[1]);
				P.openFile(name+"-"+x+"-TaskList.txt");
				rSet = statement.executeQuery("select * from shelf where name = '"+name+"' and x = "+x);
				while(rSet.next()) {
					int y = rSet.getInt(3);//��ȡ��Ŀ����ܵ�y��zֵ
					int z = rSet.getInt(4);
					P.out("Ŀ�����"+name+"-"+x+"��yֵ��"+y+",zֵ��"+z);
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
					}//�����еĵ�Ԫ�񶼽������޴�������֮�󣬼��õ���Ԫ���ʱ�Ӵ�С������
					int no = OutputTaoWa(taowa);
					P.out("������һ����"+no+"������񣬸���������ʱ�Ӵ�С��������");
					statement.execute("insert into shelfwork values('"+name+"',"+x+","+no+","+no+")");
					//����������shelfcell
					AddinTaoWa(taowa,no,level,1,statement,name,x);
				}
			}
			//�ڴ�������shelfwork��shelfcell֮�󣬻�Ҫ�ǵö�goodlist�����һ������wide�ֲ����г�ʼ������
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
		if(time1<=time2) {//��������ߵ�ʱ��������򽫺����߱�Ϊ��һλ��ԭ��һλ��Ϊ�ڶ�λ��������һλ
			String value = taowa1.value;
			TaoWa taowa3 = taowa1.next;
			taowa1.value = taowa2.value;
			taowa1.next = taowa2;
			taowa2.value = value;
			taowa2.next = taowa3;
		}else {//����ȽϺ������������к���Ԫ�صĴ�Сֱ���ҵ���Ӧ��λ�û��߶�β
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
