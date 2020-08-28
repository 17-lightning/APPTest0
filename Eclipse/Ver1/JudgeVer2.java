package Ver1;

public class JudgeVer2 {
	public static int[] run(int[][] group,Task[] taskList,int scale,int length) {
		int[] result = new int[100];
		int x=0;
		while(x<scale) {
			int time=0;
			int row=0,col=0;
			int nextrow=0,nextcol=0;
			int y=0;
			while(y<length) {
				nextrow = taskList[group[x][y]].row;
				nextcol = taskList[group[x][y]].col;
				if(taskList[group[x][y]].typ==true) {
					time=time+(row*5+col*2);
					time=time+(nextrow*5+nextcol*2);
					row=nextrow;col=nextcol;
				}
				else {
					time=time+Math.abs(nextrow-row)*5+Math.abs(nextcol-col)*2;
					time=time+nextrow*5+nextcol*2;
					row=0;col=0;
				}
				y++;
			}
			result[x] = time;
			x++;
		}
		
		 x=0;
		 int sum = 0;
		 int best = 0;
		 while(x<scale){
			sum = sum + result[x];
			if(result[best]>result[x]) best=x;
		 	int y=0;
		 	//System.out.print("序列"+x+"：");
		 	while(y<length){
		 		//System.out.print(group[x][y]);
		 		y++;
		 	}
		 	//System.out.println("的运行耗时为"+result[x]);
		 	x++;
		 }
		 sum = sum / scale;
		 System.out.println("本种群的运行耗时平均值为"+sum+",其中表现最好的序列是第"+best+"条");
		 System.out.print("这条序列是");
		 x=0;
		 while(x<length) {
			 System.out.print(group[best][x]);
			 x++;
		 }
		 System.out.println("，它的耗时是"+result[best]+"，它的得分是"+(int)(50000.0/result[best]));
		return result;
	}
}
