package utilities;

import java.util.ArrayList;
import java.util.List;

public class Timing {

	private long starttime;
	private long runtime=0;
	private String name="";
	private List<Timing> blocks=new ArrayList<Timing>();
	private boolean running=false;
	
	public Timing(String name)
	{
		this.name = name;
	}
	
	public Timing()
	{
	}
	
	public void start()
	{
		running = true;
		starttime = System.nanoTime();
	}
	
	public void start(String name)
	{
		Timing t = get(name);
		
		if (t==null)
		{
			Timing newt = new Timing(name);
			blocks.add(newt);
			newt.start();
			
			//return newt;
		}else
		{
			t.start();
		}
		
		//return this;
	}
	
	public void startblock(String name)
	{
		if (blocks.size()>0)
		{
			Timing runner = getRunner();
			if (runner!=null) runner.startblock(name);
			else this.start(name);
		}else this.start(name);
	}
	
	public void stopblock()
	{
		if (blocks.size()>0)
		{
			Timing runner = getRunner();
			if (runner!=null) runner.stopblock();
			else this.stop();
		}else this.stop();
	}
	
	private Timing getRunner()
	{
		for (Timing t : blocks)
			if (t.running) return t;
		
		return null;
	}
	
	public void stop()
	{
		if (!this.running) return;
		
		runtime += System.nanoTime()-starttime;
		
		this.running = false;
		
		for (Timing t : blocks)
			t.stop();
	}
	
	public String toString()
	{
		return this.name+": "+getReadableTime(this.runtime);
	}
	
	/*
	public void stop(String name)
	{
		this.get(name).stop();
	}*/
	
	public Timing get(String name)
	{
		for (Timing t : blocks)
			if (t.name == name) return t;
		
		return null;
	}
	
	public long runtime()
	{
		return runtime;
	}
	
	public void printResults()
	{
		printResults(0,this.runtime);
	}
	
	private void printResults(int level, long totalruntime)
	{
		for (int leveltemp = level;leveltemp>0;leveltemp--)
			System.out.print("->");
		
		System.out.printf(this.name+": "+getReadableTime(this.runtime)+", %3.3f%%\n",((double)this.runtime/totalruntime*100.0));
		
		for (Timing t : blocks)
			t.printResults(level+1,totalruntime);
	}
	
	public static String getReadableTime(long time)
	{
		if (time>8.64e13) return time/8.64e13 + " (day)";
		if (time>3.6e12) return time/3.6e12 + " (hr)";
		if (time>6e10) return time/6e10 + " (min)";
		if (time>1e9) return time/1e9+" (sec)";
		if (time>1e6) return time/1e6+" (ms)";
		if (time>1e3) return time/1e3+" (us)";
		return time+" (ns)";
	}
	
	public static String getReadableTime(int milliseconds)
	{
		if (milliseconds>8.64e7) return milliseconds/8.64e7 + " (day)";
		if (milliseconds>3.6e6) return milliseconds/3.6e6 + " (hr)";
		if (milliseconds>6e4) return milliseconds/6e4 + " (min)";
		if (milliseconds>1e3) return milliseconds/1e3+" (sec)";
		return milliseconds+" (ms)";
	}
}
