package Ver1;

public class WatchDog implements Runnable{
	
	Lock lock = null;
	
	public WatchDog(Lock lock) {
		this.lock = lock;
	}
	
	public void run() {
		try {
			Thread.sleep(10000);
			lock.lock=false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
