package Ver1;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

public class GeneticAlgorithmsVer1 {
	/**
	 * �Ŵ��㷨�����ļ�
	 */
	public static void main(String[] args) {
		
		//�õ��ķ���
		//�����ļ���ȡ��ReadSettingVer1�������嵥��ȡ��TaskReadVer2��������в�����RandomGroupMakerVer1
		
		//ע�⣺�����嵥�ĳ���Ӧ������10����Ҫ���󳤶���Ҫ�޸�taskList�Ķ��������group�ĵڶ��������
		//ע�⣺��Ⱥ��СӦ������100����Ҫ���󳤶���Ҫ�޸�group�ĵ�һ���������Timer2Weight�е�result�������
		//������Ⱥ
		HashMap<String,String> settings = new HashMap<String,String>();//�洢������Ϣ
		Task[] taskList = new Task[100];//�����嵥�������嵥��СС��100���ڴ˵��������嵥���ȣ�
		int i;//�����嵥����
		int[][] group0 = new int[200][100];//��һ��������ʾ��Ⱥ��СС��200���ڶ���������ʾ�����嵥С��100���ڴ˵�����Ⱥ��С�������嵥���ȣ�
		int[] timer0 = null;int[] weight0 = null;
		int taskListLength = 0;
		int groupScale = 0;
		int iteration = 0;int iter = 0;
		//�ر����
		Random r = new Random(1);
		//��������
		System.out.println("�ڽ����Ŵ��㷨ǰ���Ƚ��������ļ�����Ҫ��ȡ����Ϣ�ǣ������嵥����Ⱥ��С������������������ʡ��������");
		ReadSettingVer1.run(settings);//���ô˷���������ͬ�ļ����µ�settings.txt�ļ�
		taskList = TaskReadVer2.run(taskList,settings.get("TaskList"));//���ô˷��������������嵥���õ�Task[]����
		taskListLength = HowManyAreYou.run(taskList);//���������ϼ��������õ������嵥�е���������
		groupScale = Integer.parseInt(settings.get("GroupScale"));
		iteration = Integer.parseInt(settings.get("iteration"));
		RandomGroupMakerVer1.run(group0,groupScale,taskListLength);//���ô˷�����������ʼ��Ⱥgroup0
		while(iter<iteration) {
			System.out.println("��"+iter+"�ε���");
			group0 = NaturalSelectionVer1.run(group0,taskList,groupScale,taskListLength,r);
			iter++;
		}
		timer0=JudgeVer2.run(group0, taskList,groupScale,taskListLength);//���ô˷��������и������У��õ�ÿ�����еĺ�ʱ����time0[]
	}
	
	public static void run(String str) {
		P.out("���ڶ�"+str+"�������嵥���м򻯴���");
		HashMap<String,String> settings = new HashMap<String,String>();//�洢������Ϣ
		Task[] taskList = new Task[100];//�����嵥�������嵥��СС��100���ڴ˵��������嵥���ȣ�
		int i;//�����嵥����
		int[][] group0 = new int[200][100];//��һ��������ʾ��Ⱥ��СС��200���ڶ���������ʾ�����嵥С��100���ڴ˵�����Ⱥ��С�������嵥���ȣ�
		int[] timer0 = null;int[] weight0 = null;
		int taskListLength = 0;
		int groupScale = 0;
		int iteration = 0;int iter = 0;
		//�ر����
		Random r = new Random(1);
		//��������
		System.out.println("�ڽ����Ŵ��㷨ǰ���Ƚ��������ļ�����Ҫ��ȡ����Ϣ�ǣ������嵥����Ⱥ��С������������������ʡ��������");
		ReadSettingVer1.run(settings);//���ô˷���������ͬ�ļ����µ�settings.txt�ļ�
		taskList = TaskReadVer2.run(taskList,"D:\\srtp\\file\\"+str+"-TaskList.txt");//���ô˷��������������嵥���õ�Task[]����
		taskListLength = HowManyAreYou.run(taskList);//���������ϼ��������õ������嵥�е���������
		if(taskListLength==0) {P.out("����"+str+"��û����Ҫִ�е�����");return;}
		groupScale = Integer.parseInt(settings.get("GroupScale"));
		iteration = Integer.parseInt(settings.get("iteration"));
		RandomGroupMakerVer1.run(group0,groupScale,taskListLength);//���ô˷�����������ʼ��Ⱥgroup0
		while(iter<iteration) {
			System.out.println("��"+iter+"�ε���");
			group0 = NaturalSelectionVer1.run(group0,taskList,groupScale,taskListLength,r);
			iter++;
		}
		timer0=JudgeVer2.run(group0, taskList,groupScale,taskListLength);//���ô˷��������и������У��õ�ÿ�����еĺ�ʱ����time0[]
		StringBuffer sb = new StringBuffer();
		for(i=0;i<taskListLength-1;i++) {
			sb.append(taskList[i].toString()+"\n");
		}
		sb.append(taskList[i].toString());
		P.writeTXT(new File(str+"-TaskList.txt"),"");
		File file = P.openFile(str+"-TaskList-"+P.getTime()+".txt");
		P.writeTXT(file,sb.toString());
	}
}
