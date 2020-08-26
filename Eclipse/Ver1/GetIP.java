package Ver1;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetIP {
	public static void main(String[] args) {
		try {
			InetAddress ip = InetAddress.getLocalHost();
			System.out.println(ip.getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
