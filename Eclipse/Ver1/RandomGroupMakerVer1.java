package Ver1;

import java.util.Random;

public class RandomGroupMakerVer1 {
	/** 
	 * @param group int[][]���ͣ���Ҫ���룬�����յĽ�����λ��
	 * @param scale	��һ��������ʾ��Ⱥ��ģ
	 * @param length �ڶ���������ʾ���г���
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
		System.out.println("��ʼ��Ⱥ�������");
	}
}
