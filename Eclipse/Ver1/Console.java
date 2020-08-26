package Ver1;

import java.sql.Statement;
import java.util.Scanner;

public class Console implements Runnable{
	
	public Statement statement;
	public Lock lock;
	
	public Console(Statement statement,Lock lock) {
		this.statement = statement;this.lock = lock;
	}
	
	public void run() {
		try {
			Scanner sc = new Scanner(System.in);
			while(true) {
				String x = sc.nextLine();
				while(lock.lock!=true) {
					Thread.sleep(100);
				}
				lock.lock=false;
				try {
					statement.execute(x);
					P.out("��ִ��ָ��"+x);
				}catch(Exception e) {
					P.out("��ִ��ָ�"+x+"ʱ�����쳣������ָ�������Ƿ���ȷ");
					e.printStackTrace();
				}
				lock.lock=true;
			}
		}catch(Exception e) {
			lock.lock=true;
			e.printStackTrace();
		}
	}
}
