package Ver1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * ��ʼ���������ݿ⣬ɾȥ���мӹ���Ϣ�ʹ洢��Ϣ��shelfwork��shelfcell�������ó�ʼ��goodlist,namelist��shelf��</p>
 * ���޲��Խ׶�ʹ��
 */
public class DataBaseInitialization {
	public static void main(String[] args) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:D:\\srtp\\database\\hamster.db");
			Statement statement = connection.createStatement();
			statement.execute("drop table if exists goodlist");
			statement.execute("create table if not exists goodlist(name String,number int,tag String,note String,picture String,avetime String,wide String)");
			statement.execute("insert into goodlist values('corn',0,'fifo,food','made of yellow and green pixels','corn','0-0-0','null')");
			statement.execute("insert into goodlist values('egg',0,'fifo,food','first in first out or egg in chicken out','egg','0-0-0','null')");
			statement.execute("insert into goodlist values('potato',0,'fifo,food','anyone want to eat?','potato','0-0-0','null')");
			statement.execute("insert into goodlist values('shell',0,'bottom','water whatever under it','shell','0-0-0','null')");
			statement.execute("insert into goodlist values('wood',0,'','common good no tag','wood','0-0-0','null')");
			statement.execute("drop table if exists namelist");
			statement.execute("create table if not exists namelist(id String,password String,level int)");
			statement.execute("insert into namelist values('3170101649','lightning',24)");
			statement.execute("insert into namelist values('testuser','testonly',99)");
			statement.execute("drop table if exists shelf");
			statement.execute("create table if not exists shelf(name String,x int,y int,z int)");
			statement.execute("insert into shelf values('VOID',1,5,5)");
			statement.execute("insert into shelf values('VOID',2,7,4)");
			statement.execute("insert into shelf values('VOID',3,9,3)");
			statement.execute("insert into shelf values('VOID',4,10,5)");
			statement.execute("insert into shelf values('VOID',5,8,6)");
			statement.execute("drop table if exists shelfwork");
			statement.execute("drop table if exists shelfcell");
		} catch (Exception e) {
			P.out("�ڴ����ݿ�Ĺ����г��ִ���");
			e.printStackTrace();
		}
	}
}
