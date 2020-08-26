package Ver1;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * 使用这个对象来读取配置文件,需要配置文件setting.txt在同一文件夹下</p>
 * 使用方法:SettingReading setting = new SettingReading();</p>
 * 包含参数shelflevel,xmovetime,ymovetime,oldtimequality,capaforsenior,spreadcon,picturepath,databasepath
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
			P.out("开始读取配置文件");
			String str = br.readLine();
			while(!str.equals("end")) {
				P.out("读取到语句:"+str);
				if(str.split("->")[0].equals("shelflevel")) shelflevel = Integer.valueOf(str.split("->")[1]);
				else if(str.split("->")[0].equals("xmovetime")) xmovetime = Integer.valueOf(str.split("->")[1]);
				else if(str.split("->")[0].equals("ymovetime")) ymovetime = Integer.valueOf(str.split("->")[1]);
				else if(str.split("->")[0].equals("oldtimequality")) oldtimequality = Float.valueOf(str.split("->")[1]);
				else if(str.split("->")[0].equals("capaforsenior")) capaforsenior = Float.valueOf(str.split("->")[1]);
				else if(str.split("->")[0].equals("spreadcon")) spreadcon = Float.valueOf(str.split("->")[1]);
				else if(str.split("->")[0].equals("picturepath")) picturepath = str.split("->")[1];
				else if(str.split("->")[0].equals("databasepath")) databasepath = str.split("->")[1];
				else P.out("检测到未知语句段:"+str);
				str = br.readLine();
			}
			P.out("配置文件读取完毕");
			br.close();
		} catch (Exception e) {
			System.out.println("配置文件读取异常，请检查配置文件是否在当前文件夹内或配置文件是否完好");
			e.printStackTrace();
		}
	}
}
