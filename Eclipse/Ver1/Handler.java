package Ver1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Statement;

public class Handler implements Runnable{
	
	Statement statement = null;
	ServerSocket serversocket = null;
	Lock lock = null;
	int level = 0;
	Lock lock2 = null;
	String picpath;
	
	public Handler(Statement statement,ServerSocket serversocket,Lock lock,int level,Lock lock2,String picpath) {
		this.statement=statement;this.serversocket=serversocket;this.lock=lock;this.level=level;this.lock2 = lock2;this.picpath=picpath;
	}

	public void run() {
		try {
			P.out("已开始监听网络57798端口");
			while(true) {//服务器开始持续监听57798端口，每次发现新的客户端请求，就分析该请求类型并交给相应Handler进行处理
				Socket clientsocket = serversocket.accept();
				new Thread(new InnerHandler(clientsocket,statement,lock,level,picpath)).start();
				/*BufferedReader br = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
				String temp = null;
				if((temp=br.readLine())==null) {//
					P.out("出现异常连接");
				}else {//
					P.out("接收到信息："+temp);
					String tag = temp.split("#")[0];
					switch(tag) {//tag就是关键词
					case "note"://note，说明请求，只需要显示给操作员看就可以了$把这里改成txt格式更好$
						P.out(temp.split("#")[1]);
						break;
					case "login"://login，登录请求，需要分析账号密码是否存在且匹配并返回对应权限等级
						new Thread(new LoginHandler(temp,clientsocket,statement,lock)).start();
						break;
					case "register"://register，注册请求，需要分析账号是否已存在，存在则返回错误，不存在则注册并返回正确
						new Thread(new RegisterHandler(temp,clientsocket,statement,lock)).start();
						break;
					case "update"://update，货物信息更新请求，返回货物信息
						new Thread(new UpdateHandler(temp,clientsocket,statement,lock)).start();
						break;
					case "input"://input，货物入库请求，货物数量大于仓库剩余容量时返回错误，否则入库
						new Thread(new InputHandler(temp,clientsocket,statement,lock,level,lock2)).start();
						break;
					case "check"://check，入库出库的前置指令，客户端获悉目标货物在各仓库的分布情况以及各仓库的空余情况
						new Thread(new CheckHandler(temp,clientsocket,statement,lock)).start();
						break;
					case "output"://output，货物出库请求，出库数量大于仓库储量时返回错误，否则使用遗传算法进行出库操作
						new Thread(new OutputHandler(temp,clientsocket,statement,lock,level)).start();
						break;
					case "update2"://update2，新的更新处理类，用于更新仓库信息！
						new Thread(new Update2Handler(temp,clientsocket,statement,lock)).start();
						break;
					case "picture"://picture，图片处理类
						new Thread(new PictureHandler(temp,clientsocket,statement,lock,picpath)).start();
						break;
					default: P.out("未能识别的对象："+temp);
					}
					
				}*/
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
