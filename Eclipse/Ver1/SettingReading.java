package Ver1;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * ʹ�������������ȡ�����ļ�,��Ҫ�����ļ�setting.txt��ͬһ�ļ�����</p>
 * ʹ�÷���:SettingReading setting = new SettingReading();</p>
 * ��������shelflevel,xmovetime,ymovetime,oldtimequality,capaforsenior,spreadcon,picturepath,databasepath
 */
public class SettingReading {
	public int shelflevel;
	public int xmovetime;
	public int ymovetime;
	public float oldtimequality;
	public float capaforsenior;
	public float spreadcon;
	public String picturepath;
	public String databasepath;
	
	public SettingReading() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("setting.txt"));
			P.out("��ʼ��ȡ�����ļ�");
			String str = br.readLine();
			while(!str.equals("end")) {
				P.out("��ȡ�����:"+str);
				if(str.split("->")[0].equals("shelflevel")) shelflevel = Integer.valueOf(str.split("->")[1]);
				else if(str.split("->")[0].equals("xmovetime")) xmovetime = Integer.valueOf(str.split("->")[1]);
				else if(str.split("->")[0].equals("ymovetime")) ymovetime = Integer.valueOf(str.split("->")[1]);
				else if(str.split("->")[0].equals("oldtimequality")) oldtimequality = Float.valueOf(str.split("->")[1]);
				else if(str.split("->")[0].equals("capaforsenior")) capaforsenior = Float.valueOf(str.split("->")[1]);
				else if(str.split("->")[0].equals("spreadcon")) spreadcon = Float.valueOf(str.split("->")[1]);
				else if(str.split("->")[0].equals("picturepath")) picturepath = str.split("->")[1];
				else if(str.split("->")[0].equals("databasepath")) databasepath = str.split("->")[1];
				else P.out("��⵽δ֪����:"+str);
				str = br.readLine();
			}
			P.out("�����ļ���ȡ���");
			br.close();
		} catch (Exception e) {
			System.out.println("�����ļ���ȡ�쳣�����������ļ��Ƿ��ڵ�ǰ�ļ����ڻ������ļ��Ƿ����");
			e.printStackTrace();
		}
	}
}
