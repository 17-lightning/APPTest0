package Ver1;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RobotBase implements Runnable{
	
	Statement statement = null;
	
	public RobotBase(Statement statement) {
		this.statement=statement;
	}
	
	public void run() {
		try {
			List<String> list = new ArrayList<String>();
			//2.������еĻ��ܣ����ɳ���Ӧ�����ˣ�������Ӧ��TaskList�ĵ���
			ResultSet rs = statement.executeQuery("select name,x from shelfwork");
			while(rs.next()) {
				list.add(rs.getString(1)+"-"+rs.getInt(2));
			}
			//3.ÿ10���ӣ�ʱ����ɵ����������еĻ��ܽ���ɨ��
			while(true) {
				Thread.sleep(600000);
				Iterator<String> iter = list.iterator();
				while(iter.hasNext()) {
					GeneticAlgorithmsVer1.run(iter.next());
				}
			}
		}catch(Exception e) {
			
		}
	}
}
