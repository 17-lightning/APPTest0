package Ver1;

public class Timer2Weight {
	public static int[] run(int[] timer,int length) {
		int[] result = new int[200];
		int weight = 0;
		int index = 0;
		while(index<length) {
			weight = (int)(50000.0/timer[index]);
			result[index] = weight;
			//System.out.println("序列"+index+"的得分是"+weight);
			index++;
		}
		return result;
	}
}
