package Thread1;

public class thread_invoker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		runnable_test1 rtest = new runnable_test1();
		Thread thread1 = new Thread(rtest);
		thread1.start();
	}

}
