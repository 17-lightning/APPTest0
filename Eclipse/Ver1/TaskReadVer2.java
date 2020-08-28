package Ver1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TaskReadVer2 {
	public static Task[] run(Task[] taskList,String addr) {
		System.out.println("��ʼ���������嵥");
		//��������ָ��
		int index = 0;
		//��ȡĿ���ļ�
		File file = new File(addr);
		try {
			//�ļ���ȡ��������������brָ�򻺳���
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s = null;
			int flag_error = 0;
			while((s = br.readLine())!=null&&flag_error==0) {
				Task task = new Task();
				taskList[index] = task;
				int type = -1;
				if(s.startsWith("in")) {type=0;taskList[index].typ=true;}
				else if(s.startsWith("out")) {type=1;taskList[index].typ=false;}
				else {flag_error = 1;System.out.println("ERROR!:"+s);};
				String[] Tasktag = s.split(",");
				int row = Integer.valueOf(Tasktag[1]);
				taskList[index].row = row;
				int col = Integer.valueOf(Tasktag[2]);
				taskList[index].col = col;
				if(type==0) System.out.println("Task"+index+":���ָ��("+taskList[index].row+","+taskList[index].col+")");
				if(type==1) System.out.println("Task"+index+":����ָ��("+taskList[index].row+","+taskList[index].col+")");
				index++;
			}
			br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return taskList;
		}
	

}
