package external.linuxCaller;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.List;

public class RCaller {

	private String Rpath="/cellar/users/ghannum/Software/R-2.5.1/bin/R";
	private boolean started = false;
	private Process rp;
	private OutputStreamWriter osw;
	private BufferedWriter bw;
	
	public RCaller()
	{
	}
	
	public RCaller(String Rpath)
	{
		this.Rpath = Rpath;
	}
	
	public void start()
	{
		if (started) stop();
		
		System.out.println("Attempting to start R.");
		rp = LCaller.start(Rpath+" --slave --vanilla");

		//osw = new OutputStreamWriter(rp.getOutputStream());
		//bw = new BufferedWriter(osw);
		
		
		try
		{
			Thread.sleep(1000);
			int excessData = rp.getInputStream().available(); 
			if (excessData>0) rp.getInputStream().read(new byte[excessData], 0, excessData);
		}catch(Exception e) {System.out.println("Error RCaller.start(): "+e);};
		
		
		started = true;
		System.out.println("R successfully started.");
	}
	
	public void stop()
	{
		try
		{
			if (bw!=null) bw.close();
			if (osw!=null) osw.close();
		}catch(Exception e) {System.out.println("Error RCaller.stop(): "+e);};
		
		started = false;
	}
	
	public List<String> callR(String cmd)
	{
		if (!started) 
		{
			System.err.println("Error RCaller.callR(String): RCaller not started.");
			System.exit(0);
		}
		
		try
		{
			int ssize = rp.getInputStream().available();
						
			osw = new OutputStreamWriter(rp.getOutputStream());
			bw = new BufferedWriter(osw);
			bw.write(cmd);
			bw.newLine();
			bw.flush();
			//bw.close();
			//osw.close();
			
			//Need to wait for a response
			while (rp.getInputStream().available()==ssize);
	         
		}catch (Exception e) {System.out.println("Error RCaller.callR(): "+e);}
		
		StreamGobbler outputGobbler = new StreamGobbler(rp.getInputStream(), "OUTPUT");
        
        outputGobbler.start();
        
        try
        {
        	outputGobbler.join();
        } catch (Exception e) {}
        
		return outputGobbler.getOut();
	}
	
	public double hyperGeometric(int blackdrawn, int numblack, int samplesize, int total, boolean log)
	{
		String cmd = "phyper("+(blackdrawn-1)+","+numblack+","+(total-numblack)+","+samplesize+",lower.tail=FALSE";
		
		if (log) cmd += ",log.p=TRUE";
		cmd+=")";
		
		String ans = callR(cmd).get(0).substring(4);
		
		return Double.valueOf(ans);
	}
	
	public double poisson(int count, double distance, boolean lowertail, boolean log)
	{
		String cmd = "ppois(";
		if (!lowertail) cmd += (count-1)+","+distance+", lower.tail=FALSE";
		else cmd += count+","+distance+", lower.tail=TRUE";
		
		if (log) cmd+= ",log=TRUE";
		
		cmd+=")";
		List<String> out = callR(cmd);
		
		return Double.valueOf(out.get(0).substring(4));
	}
	
	public double gamma(double distance, int count, double rate, boolean log)
	{
		String cmd = "pgamma("+distance+","+count+",rate="+rate+",lower.tail=FALSE";
		
		if (log) cmd+= ",log=TRUE";
		
		cmd+=")";
		List<String> out = callR(cmd);
		
		return Double.valueOf(out.get(0).substring(4));
	}
	
	public double normal(double mean, double sd, double x, boolean log)
	{
		String cmd = "pnorm("+x+","+mean+","+sd+",lower.tail=FALSE";
		
		if (log) cmd+= ",log=TRUE";
		
		cmd+=")";
		
		List<String> out = callR(cmd);
		
		return Double.valueOf(out.get(0).substring(4));
	}
	
	
	/*
	 * 	public double GetMI (DoubleVector x1, DoubleVector x2, int bins, int splineorder)
	{
		String cmd = "source(\""+scriptpath+"/mutual_information2.R\");mutual.information2(c(";
		
		for (int i=0;i<x1.size()-1;i++)
			cmd+=x1.get(i)+",";
		
		cmd+=x1.get(x1.size()-1)+"),c(";
		
		for (int i=0;i<x2.size()-1;i++)
			cmd+=x2.get(i)+",";
		
		cmd+=x2.get(x2.size()-1)+"),"+bins+","+splineorder+")";
		
		ArrayList<String> out = CallR(cmd);
		
		return Double.valueOf(out.get(0));
	}
	*/
}
