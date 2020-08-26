package Ver1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Statement;

//17upup
public class hamster {
	public static void main(String[] args) {
		
		Statement statement = null;
		
		//首先读取配置文件，然后可以setting对象中获取到需要的参数
		SettingReading setting = new SettingReading();
		
		//与数据库建立连接
		statement = P.GetDataBaseEnter(setting.databasepath);
		
		//检验数据库是否需要初始化
		boolean flag = DataBaseReader.check(statement);
		//如果存在，不需要进行处理，如果工作表单不存在，需要对数据库进行工作状态的初始化
		if(flag==true) P.out("目标数据库无需进行初始化操作");
		else {P.out("需要对目标数据库进行初始化处理");
		//对数据库进行初始化处理
		HamsterInit.run(statement, setting.xmovetime, setting.ymovetime,setting.shelflevel);
		}
		
		//与仓库穿梭车建立连接线程
		new Thread(new RobotBase(statement)).start();
		
		//至此数据库初始化完毕，程序可以开始工作
		//使用支线程来处理客户端发来的网络请求，使用主线程用键盘执行一些指令，为避免指令冲突，在数据库中加入同步锁
		Lock lock = new Lock();//lock是数据库的同步锁
		Lock lock2 = new Lock();//lock2是任务清单的同步锁#####目前未使用
		try {
			ServerSocket socket = new ServerSocket(57798);
			new Thread(new Handler(statement,socket,lock,setting.shelflevel,lock2,setting.picturepath)).start();
		} catch (IOException e) {
			P.out("服务器开启失败，请检查当前网络或者57798端口使用情况");
			e.printStackTrace();
		}
		
		//开启第三个线程，用于与控制台对接
		new Thread(new Console(statement,lock)).start();
		
		//开启第四个线程，每一段时间评估一次货物的流通等级
		new Thread(new GoodRank(statement,lock,setting.shelflevel)).start();
	}
}
