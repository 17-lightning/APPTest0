package Ver1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Statement;

public class InnerHandler implements Runnable{
	
	public Socket clientSocket;
	public Statement statement;
	public Lock lock;
	public int level;
	public String picpath;
	
	public InnerHandler(Socket clientSocket,Statement statement,Lock lock,int level,String picpath) {
		this.clientSocket=clientSocket;this.statement=statement;this.lock=lock;this.picpath=picpath;this.level=level;
	}
	
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while(true) {
				String temp = null;
				if((temp=br.readLine())==null) {//
					P.out("�����쳣����:"+temp);
					break;
				}else {//
					P.out("���յ���Ϣ��"+temp);
					String tag = temp.split("#")[0];
					switch(tag) {//tag���ǹؼ���
					case "note"://note��˵������ֻ��Ҫ��ʾ������Ա���Ϳ�����$������ĳ�txt��ʽ����$
						P.out(temp.split("#")[1]);
						break;
					case "login"://login����¼������Ҫ�����˺������Ƿ������ƥ�䲢���ض�ӦȨ�޵ȼ�
						new Thread(new LoginHandler(temp,clientSocket,statement,lock)).start();
						break;
					case "register"://register��ע��������Ҫ�����˺��Ƿ��Ѵ��ڣ������򷵻ش��󣬲�������ע�Ტ������ȷ
						new Thread(new RegisterHandler(temp,clientSocket,statement,lock)).start();
						break;
					case "update"://update��������Ϣ�������󣬷��ػ�����Ϣ
						new Thread(new UpdateHandler(temp,clientSocket,statement,lock)).start();
						break;
					case "input"://input������������󣬻����������ڲֿ�ʣ������ʱ���ش��󣬷������
						new Thread(new InputHandler(temp,clientSocket,statement,lock,level,null)).start();
						break;
					case "check"://check���������ǰ��ָ��ͻ��˻�ϤĿ������ڸ��ֿ�ķֲ�����Լ����ֿ�Ŀ������
						new Thread(new CheckHandler(temp,clientSocket,statement,lock)).start();
						break;
					case "output"://output������������󣬳����������ڲֿⴢ��ʱ���ش��󣬷���ʹ���Ŵ��㷨���г������
						new Thread(new OutputHandler(temp,clientSocket,statement,lock,level)).start();
						break;
					case "update2"://update2���µĸ��´����࣬���ڸ��²ֿ���Ϣ��
						new Thread(new Update2Handler(temp,clientSocket,statement,lock)).start();
						break;
					case "update3":
						new Thread(new Update3Handler(temp,clientSocket,statement,lock)).start();
						break;
					case "picture"://picture��ͼƬ������
						new Thread(new PictureHandler(temp,clientSocket,statement,lock,picpath)).start();
						break;
					default: P.out("δ��ʶ��Ķ���"+temp);
					}
				}
			}
		}catch(Exception e) {
			P.out("�����ж�");
			e.printStackTrace();
		}
		P.out("������null���ж�");
	}
}
