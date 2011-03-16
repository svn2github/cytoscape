package Thread1;

public class runnable_test1 implements Runnable{

	public void run(){
		int i;
		try {
			for(i = 0;i < 5;i ++){
				System.out.println("Hello!");
				Thread.sleep(1000);
			}
		}
		catch (InterruptedException e){}
	}
	
	/**
	 * @param args

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	 */
}

