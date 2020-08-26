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
			P.out("�ѿ�ʼ��������57798�˿�");
			while(true) {//��������ʼ��������57798�˿ڣ�ÿ�η����µĿͻ������󣬾ͷ������������Ͳ�������ӦHandler���д���
				Socket clientsocket = serversocket.accept();
				new Thread(new InnerHandler(clientsocket,statement,lock,level,picpath)).start();
				/*BufferedReader br = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
				String temp = null;
				if((temp=br.readLine())==null) {//
					P.out("�����쳣����");
				}else {//
					P.out("���յ���Ϣ��"+temp);
					String tag = temp.split("#")[0];
					switch(tag) {//tag���ǹؼ���
					case "note"://note��˵������ֻ��Ҫ��ʾ������Ա���Ϳ�����$������ĳ�txt��ʽ����$
						P.out(temp.split("#")[1]);
						break;
					case "login"://login����¼������Ҫ�����˺������Ƿ������ƥ�䲢���ض�ӦȨ�޵ȼ�
						new Thread(new LoginHandler(temp,clientsocket,statement,lock)).start();
						break;
					case "register"://register��ע��������Ҫ�����˺��Ƿ��Ѵ��ڣ������򷵻ش��󣬲�������ע�Ტ������ȷ
						new Thread(new RegisterHandler(temp,clientsocket,statement,lock)).start();
						break;
					case "update"://update��������Ϣ�������󣬷��ػ�����Ϣ
						new Thread(new UpdateHandler(temp,clientsocket,statement,lock)).start();
						break;
					case "input"://input������������󣬻����������ڲֿ�ʣ������ʱ���ش��󣬷������
						new Thread(new InputHandler(temp,clientsocket,statement,lock,level,lock2)).start();
						break;
					case "check"://check���������ǰ��ָ��ͻ��˻�ϤĿ������ڸ��ֿ�ķֲ�����Լ����ֿ�Ŀ������
						new Thread(new CheckHandler(temp,clientsocket,statement,lock)).start();
						break;
					case "output"://output������������󣬳����������ڲֿⴢ��ʱ���ش��󣬷���ʹ���Ŵ��㷨���г������
						new Thread(new OutputHandler(temp,clientsocket,statement,lock,level)).start();
						break;
					case "update2"://update2���µĸ��´����࣬���ڸ��²ֿ���Ϣ��
						new Thread(new Update2Handler(temp,clientsocket,statement,lock)).start();
						break;
					case "picture"://picture��ͼƬ������
						new Thread(new PictureHandler(temp,clientsocket,statement,lock,picpath)).start();
						break;
					default: P.out("δ��ʶ��Ķ���"+temp);
					}
					
				}*/
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
