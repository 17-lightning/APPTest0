package Ver1;

import java.util.Random;

public class NaturalSelectionVer1 {
	public static int[][] run(int[][] group,Task[] taskList,int scale,int length,Random r){
		int[][] result = new int[200][100];
		int[][] parent = new int[100][2];
		//��Ȼѡ���һ������ÿһ������������У��õ����Ǹ��Ե�Ȩ��
		int[] weight = Timer2Weight.run(JudgeVer2.run(group, taskList, scale, length), scale);
		//��Ȼѡ��ڶ��������ݸ�����Ӧ�ȣ�����˭�ܲ������
		int index = 0;
		int bound=0;
		index=0;
		while(index<100) {
			bound=bound+weight[index];
			index++;
		}
		index=0;
		while(index<(scale/2)) {
			int boundi=r.nextInt(bound);
			int indexi=0;
			while(boundi>0) {
				boundi=boundi-weight[indexi];
				indexi++;
			}
			indexi--;
			parent[index][0]=indexi;
			//System.out.print("��"+index+"�����˶���"+indexi+"�ź�");
			
			boundi=r.nextInt(bound);
			indexi=0;
			while(boundi>0) {
				boundi=boundi-weight[indexi];
				indexi++;
			}
			indexi--;
			parent[index][1]=indexi;
			//System.out.println(indexi+"��");
			
			index++;
		}
		//��Ȼѡ����������Ӵ�����Ľ��������
		index = 0;
		while(index<100) {
			int next = r.nextInt(2);
			if(next==0) {
				//��ʱ�����н���
				result[index]=group[parent[index/2][0]];
				result[index+1]=group[parent[index/2][1]];
			}
			else {
				int boundi = r.nextInt(10);
				result[index]=SimilarVer0.run(group[parent[index/2][0]],group[parent[index/2][1]],boundi,length)[0];
				result[index+1]=SimilarVer0.run(group[parent[index/2][0]],group[parent[index/2][1]],boundi,length)[1];
			}
			index+=2;
		}
		index=0;
		
		int x=0;
		while(x<100) {
			int vary = r.nextInt(2);
			if(vary==0) {
				//��ʱִ�б����������������ȡ����λ���ϵ�Ԫ�ؽ��н���
				int a = r.nextInt(10);
				int b = r.nextInt(10);
				if(a!=b) {
					int c = result[x][a];
					result[x][a]=result[x][b];
					result[x][b]=c;
				}
			}
			x++;
		}
		
		return result;
	}
}
