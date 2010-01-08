package utilities;

import java.util.concurrent.*;

public final class ThreadPriorityFactory implements ThreadFactory 
{
	private final int priority;  
	
	public ThreadPriorityFactory(int priority)
	{
		this.priority = priority;
	}
	
	
	public Thread newThread(Runnable r)
	{
		Thread t = new Thread(r);
		t.setPriority(priority);
		return t;
	}
	
}
