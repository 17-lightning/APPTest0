package Ver1;

public class HowManyAreYou {
	public static int run(Task[] taskList) {
		int result = 0;
		while(taskList[result]!=null) result++;
		System.out.println("任务清单处理完毕，一共需要处理"+result+"个任务");
		return result;
	}
}
