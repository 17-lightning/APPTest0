package Ver1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Statement;

//17upup
public class hamster {
	public static void main(String[] args) {
		
		Statement statement = null;
		
		//���ȶ�ȡ�����ļ���Ȼ�����setting�����л�ȡ����Ҫ�Ĳ���
		SettingReading setting = new SettingReading();
		
		//�����ݿ⽨������
		statement = P.GetDataBaseEnter(setting.databasepath);
		
		//�������ݿ��Ƿ���Ҫ��ʼ��
		boolean flag = DataBaseReader.check(statement);
		//������ڣ�����Ҫ���д�����������������ڣ���Ҫ�����ݿ���й���״̬�ĳ�ʼ��
		if(flag==true) P.out("Ŀ�����ݿ�������г�ʼ������");
		else {P.out("��Ҫ��Ŀ�����ݿ���г�ʼ������");
		//�����ݿ���г�ʼ������
		HamsterInit.run(statement, setting.xmovetime, setting.ymovetime,setting.shelflevel);
		}
		
		//��ֿ⴩�󳵽��������߳�
		new Thread(new RobotBase(statement)).start();
		
		//�������ݿ��ʼ����ϣ�������Կ�ʼ����
		//ʹ��֧�߳�������ͻ��˷�������������ʹ�����߳��ü���ִ��һЩָ�Ϊ����ָ���ͻ�������ݿ��м���ͬ����
		Lock lock = new Lock();//lock�����ݿ��ͬ����
		Lock lock2 = new Lock();//lock2�������嵥��ͬ����#####Ŀǰδʹ��
		try {
			ServerSocket socket = new ServerSocket(57798);
			new Thread(new Handler(statement,socket,lock,setting.shelflevel,lock2,setting.picturepath)).start();
		} catch (IOException e) {
			P.out("����������ʧ�ܣ����鵱ǰ�������57798�˿�ʹ�����");
			e.printStackTrace();
		}
		
		//�����������̣߳����������̨�Խ�
		new Thread(new Console(statement,lock)).start();
		
		//�������ĸ��̣߳�ÿһ��ʱ������һ�λ������ͨ�ȼ�
		new Thread(new GoodRank(statement,lock,setting.shelflevel)).start();
	}
}
