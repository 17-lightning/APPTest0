package Ver1;
//相似算法-类
public class SimilarVer0 {
	public static int[][] run(int[] father,int[] mother,int change,int i){
		int[][] result = new int[2][10];
		//两个子代先随父代的序列
		int x=0;
		while(x<i) {
			result[0][x]=father[x];
			result[1][x]=mother[x];
			x++;
		}
		//对交叉点后的子代序列做出调整，如果父代1的元素在父代2中存在，则将此元素与父代2对应位置的元素交换
		x=change;
		while(x<i) {
			int flag=lookfor(mother,change,i,father[x]);
			if(flag>=0) {
				int a=lookfor(result[0],0,i,father[x]);
				swap(result[0],a,flag);
			}
			x++;
		}
		//对子代2进行处理
		x=change;
		while(x<i) {
			int flag=lookfor(father,change,i,mother[x]);
			if(flag>=0) {
				int a=lookfor(result[1],0,i,mother[x]);
				swap(result[1],a,flag);
			}
			x++;
		}
		
		return result;
	}
	
	
	
	
	
	
	
	public static int lookfor(int[] team,int start,int end,int aim) {
		int flag = -1;
		int x = start;
		while(x<end) {
			if(team[x]==aim) flag=x;
			x++;
		}
		return flag;
	}
	
	
	public static void swap(int[] team,int a,int b) {
		int x=team[a];
		team[a]=team[b];
		team[b]=x;
	}
}
