package Ver1;

public class DelayException extends Exception {
	
	String str = "��ʱ�쳣";
	
	public DelayException(String str) {
		this.str = str;
	}
	
	public void printStackTrace() {
		P.out(str);
	}
}
