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
 * lightning���ư���һЩ���ú����İ�
 *   �ں�����out��GetDataBaseEnter
 */
public class P {
	
	
	/**
	 * System.out.println��������
	 */
	public static void out(Object obj) {
		System.out.println(obj);
	}
	public static void out() {
		System.out.println();
	}
	
	/**
	 * System.out.print��������
	 */
	public static void o(Object obj) {
		System.out.print(obj);
	}
	
	/**
	 * ���һ�����ݿ�Ĳ�����ڣ���Ҫ����Ŀ�����ݿ��·��������D:\\srtp\\database\\hamster.db
	 */
	public static Statement GetDataBaseEnter(String str) {
		Statement statement = null;
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:"+str);
			statement = connection.createStatement();
			out("�ѻ��"+str+"�Ĳ������");
		} catch (Exception e) {
			out("�ڴ����ݿ�Ĺ����г��ִ���");
			e.printStackTrace();
		}
		return statement;
	}
	
	/**
	 * �������ݿ����Ƿ���ڸ�����
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
			P.out("����ָ���������");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * ���·�-���� Сʱ
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
	 * ��ȡtxt�ļ��е���������
	 */
	public static String readTXT(File file) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));//ʩ���У�δ�����
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
	 * ��Ŀ��txt��д������ַ���
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
	 * ��Ŀ��txt�����׷������
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
	 * ��Ŀ���ļ������Ŀ���ļ������ڻ��Զ�����һ��
	 */
	public static File openFile(String str) {
		File file = new File("D:\\srtp\\file\\"+str);
		try {
			if(!file.exists()) {
				file.createNewFile();
			}
		}catch(Exception e) {
			P.out("Ҫ�򿪵�Ŀ���ļ������ڣ���Ҳ��������������˵���֮�ȴ���������ļ�");
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * ���time1�����ʱ�䵽���ڵ�����
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
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));//ʩ���У�δ�����
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
