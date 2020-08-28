package Ver1;

import java.util.Random;

public class RandomGroupMakerVer1 {
	/** 
	 * @param group int[][]类型，需要传入，是最终的结果输出位置
	 * @param scale	第一索引，表示种群规模
	 * @param length 第二索引，表示序列长度
	 */
	public static void run(int[][] group,int scale,int length) {
		System.out.println("scale:"+scale+",length:"+length);
		Random r = new Random(1);
		int x=0;
		while(x<scale) {
			int y=0;
			int[] list = {0,1,2,3,4,5,6,7,8,9};
			while(y<length) {
				int z=r.nextInt(length-y);
				group[x][y]=list[z];
				list[z]=list[length-y-1];
				y++;
			}
			x++;
		}
		System.out.println("初始种群生成完毕");
	}
}
