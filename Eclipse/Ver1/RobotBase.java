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
			//2.获得所有的货架，并派出相应机器人（创建相应的TaskList文档）
			ResultSet rs = statement.executeQuery("select name,x from shelfwork");
			while(rs.next()) {
				list.add(rs.getString(1)+"-"+rs.getInt(2));
			}
			//3.每10分钟（时间间距可调），对所有的货架进行扫荡
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
