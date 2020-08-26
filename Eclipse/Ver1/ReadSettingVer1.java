package Ver1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class ReadSettingVer1 {
	public static void run(HashMap<String,String> settings) {
		/**
		 * 解析同文件夹下的settings.txt文件
		 * 需要传入一个HashMap<String,String>类型的参数，解析结果将会添加入此哈希表中
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
