package Ver1;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

public class GAVer1 implements Runnable{
	public String str;
	public GAVer1(String str) {
		this.str = str;
	}
	public void run() {
		P.out("正在对"+str+"的任务清单进行简化处理");
		HashMap<String,String> settings = new HashMap<String,String>();//存储配置信息
		Task[] taskList = new Task[100];//任务清单，任务清单大小小于100（在此调节任务清单长度）
		int i;//任务清单长度
		int[][] group0 = new int[200][100];//第一个参数表示种群大小小于200，第二个参数表示任务清单小于100（在此调节种群大小和任务清单长度）
		int[] timer0 = null;int[] weight0 = null;
		int taskListLength = 0;
		int groupScale = 0;
		int iteration = 0;int iter = 0;
		//特别参数
		Random r = new Random(1);
		//程序主体
		System.out.println("在进行遗传算法前，先解析配置文件，需要获取的信息是：任务清单、种群大小、迭代次数、交叉概率、变异概率");
		ReadSettingVer1.run(settings);//调用此方法来解析同文件夹下的settings.txt文件
		taskList = TaskReadVer2.run(taskList,str);//调用此方法来解析任务清单，得到Task[]数组
		taskListLength = HowManyAreYou.run(taskList);//调用你算老几方法，得到任务清单中的任务数量
		if(taskListLength==0) {P.out("货架"+str+"上没有需要执行的任务");return;}
		groupScale = Integer.parseInt(settings.get("GroupScale"));
		iteration = Integer.parseInt(settings.get("iteration"));
		RandomGroupMakerVer1.run(group0,groupScale,taskListLength);//调用此方法来产生初始种群group0
		while(iter<iteration) {
			System.out.println("第"+iter+"次迭代");
			group0 = NaturalSelectionVer1.run(group0,taskList,groupScale,taskListLength,r);
			iter++;
		}
		timer0=JudgeVer2.run(group0, taskList,groupScale,taskListLength);//调用此方法来进行个体评判，得到每个序列的耗时序列time0[]
		StringBuffer sb = new StringBuffer();
		for(i=0;i<taskListLength-1;i++) {
			sb.append(taskList[i].toString()+"\n");
		}
		sb.append(taskList[i].toString());
		P.writeTXT(new File(str+"-TaskList.txt"),sb.toString());
	}
}
