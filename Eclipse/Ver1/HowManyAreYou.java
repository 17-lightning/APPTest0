package Ver1;

public class HowManyAreYou {
	public static int run(Task[] taskList) {
		int result = 0;
		while(taskList[result]!=null) result++;
		System.out.println("�����嵥������ϣ�һ����Ҫ����"+result+"������");
		return result;
	}
}
