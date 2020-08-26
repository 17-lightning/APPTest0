package Ver1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 本函数是用来遍历一个数据库里所有的结构的
 * 什么数据库都可以看一遍哦
 */
public class DataBaseReader {
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:D:\\srtp\\database\\hamster.db");
			Statement statement = connection.createStatement();
			ResultSet rSet = statement.executeQuery("select name from sqlite_master where type='table'");
			while(rSet.next()) {
				String str = rSet.getString(1);
				list.add(str);
			}
			//接下来对每一个表做分
			int i=0;
			for(i=0;i<list.size();i++) {
				String str = list.get(i);
				try {
					System.out.println("检测到数据库中表单:"+str+",开始分析其中元素");
					ResultSet rSet2 = statement.executeQuery("pragma table_info("+str+")");
					while(rSet2.next()) {
						P.o(rSet2.getObject("name")+"\t");
					}
					ResultSet rSet3 = statement.executeQuery("select * from "+str);
					P.out();
					while(rSet3.next()) {
						int j = 1;
						try {
							while(true) {
								P.o(rSet3.getObject(j)+"\t");
								j++;
							}
						}catch(Exception e) {
						}
						P.out();
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				P.out();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean check(Statement statement) {
		List<String> list = new ArrayList<String>();
		boolean result = false;
		try {
			Class.forName("org.sqlite.JDBC");
			ResultSet rSet = statement.executeQuery("select name from sqlite_master where type='table'");
			int i=0,j=0;
			while(rSet.next()) {
				String str = rSet.getString(1);
				j++;
				if(!str.equals("shelfwork")) if(!str.equals("shelfcell")) i++;
			}
			if(j-i!=2) return false;//如果不存在shelfwork和shelfcell表单，则数据库必定没有初始化
		}catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
