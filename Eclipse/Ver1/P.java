package Ver1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * lightning自制包含一些有用函数的包
 *   内含方法out、GetDataBaseEnter
 */
public class P {
	
	
	/**
	 * System.out.println的替代语句
	 */
	public static void out(Object obj) {
		System.out.println(obj);
	}
	public static void out() {
		System.out.println();
	}
	
	/**
	 * System.out.print的替代语句
	 */
	public static void o(Object obj) {
		System.out.print(obj);
	}
	
	/**
	 * 获得一个数据库的操作入口，需要输入目标数据库的路径，比如D:\\srtp\\database\\hamster.db
	 */
	public static Statement GetDataBaseEnter(String str) {
		Statement statement = null;
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:"+str);
			statement = connection.createStatement();
			out("已获得"+str+"的操作入口");
		} catch (Exception e) {
			out("在打开数据库的过程中出现错误");
			e.printStackTrace();
		}
		return statement;
	}
	
	/**
	 * 检验数据库中是否存在该数据
	 */
	public static boolean check(Statement statement,String str){
		ResultSet rSet;
		try {
			rSet = statement.executeQuery(str);
			while(rSet.next()) {
				return true;
			}
			return false;
		} catch (SQLException e) {
			P.out("输入指令出现问题");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 以月份-日期 小时
	 */
	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	public static String getTime2() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return sdf.format(new Date());
	}
	
	/**
	 * 读取txt文件中的所有文字
	 */
	public static String readTXT(File file) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));//施工中，未完待续
			StringBuffer sb = new StringBuffer();
			String temp;
			while((temp=br.readLine())!=null) {
				sb.append(temp+"\n");
			}
			return sb.toString();
		}catch(Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	/**
	 * 往目标txt中写入给定字符串
	 */
	public static void writeTXT(File file,String str) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(str.getBytes());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 在目标txt的最后追加内容
	 */
	public static void addtoTXT(String filestr,String str) {
		try {
			File file = openFile(filestr);
			String forestr = readTXT(file);
			writeTXT(file,forestr+str);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 打开目标文件，如果目标文件不存在会自动创建一个
	 */
	public static File openFile(String str) {
		File file = new File("D:\\srtp\\file\\"+str);
		try {
			if(!file.exists()) {
				file.createNewFile();
			}
		}catch(Exception e) {
			P.out("要打开的目标文件不存在，我也不懂哪里出岔子了但总之先创建了这个文件");
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * 获得time1代表的时间到现在的秒数
	 */
	public static int minusTime(String time1) {
		int result = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			result = (int)(sdf.parse(P.getTime()).getTime()-sdf.parse(time1).getTime())/1000;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static boolean WaitLock(Lock lock) {
		try {
			Lock wangwang = new Lock();
			new Thread(new WatchDog(wangwang)).start();
			while(lock.lock!=true) {
				Thread.sleep(100);
				if(wangwang.lock==false) return false;
			}
			lock.lock=false;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static List<String> check(String str){
		List<String> list = new ArrayList<String>();
		try {
			File file = new File("D:\\srtp\\file\\"+str+"-TaskList.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));//施工中，未完待续
			String temp;
			while((temp=br.readLine())!=null) {
				list.add(temp.split(",")[1]+","+temp.split(",")[2]);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static void reload(String str) {
		String s = readTXT(new File("D:\\srtp\\file\\"+str+"-TaskList.txt"));
		openFile(str+"-TaskList-"+getTime()+".txt");
		String ss = "D:\\srtp\\file\\"+str+"-TaskList-"+getTime()+".txt";
		writeTXT(new File(ss),s);
		writeTXT(new File("D:\\srtp\\file\\"+str+"-TaskList.txt"),"");
		new Thread(new GAVer1(ss)).start();
	}
}
