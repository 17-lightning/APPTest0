package Ver1;

import java.util.Random;

public class NaturalSelectionVer1 {
	public static int[][] run(int[][] group,Task[] taskList,int scale,int length,Random r){
		int[][] result = new int[200][100];
		int[][] parent = new int[100][2];
		//自然选择第一步，对每一个个体进行评判，得到它们各自的权重
		int[] weight = Timer2Weight.run(JudgeVer2.run(group, taskList, scale, length), scale);
		//自然选择第二步，根据个体适应度，决定谁能产生后代
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
			//System.out.print("第"+index+"对幸运儿是"+indexi+"号和");
			
			boundi=r.nextInt(bound);
			indexi=0;
			while(boundi>0) {
				boundi=boundi-weight[indexi];
				indexi++;
			}
			indexi--;
			parent[index][1]=indexi;
			//System.out.println(indexi+"号");
			
			index++;
		}
		//自然选择第三步：子代基因的交叉与变异
		index = 0;
		while(index<100) {
			int next = r.nextInt(2);
			if(next==0) {
				//此时不进行交叉
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
				//此时执行变异操作，方法是任取两个位置上的元素进行交换
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
