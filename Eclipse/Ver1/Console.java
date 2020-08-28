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
					P.out("已执行指令"+x);
				}catch(Exception e) {
					P.out("在执行指令："+x+"时发生异常，请检查指令输入是否正确");
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
