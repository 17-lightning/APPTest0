package Ver1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class ReadSettingVer1 {
	public static void run(HashMap<String,String> settings) {
		/**
		 * ����ͬ�ļ����µ�settings.txt�ļ�
		 * ��Ҫ����һ��HashMap<String,String>���͵Ĳ���������������������˹�ϣ����
		 */
		File file = new File("settings2.txt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s = null;
			while((s = br.readLine())!=null) {
				String[] temp = s.split(":");
				if(temp.length==2) {
						settings.put(temp[0],temp[1]);
				}
			}
			br.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
