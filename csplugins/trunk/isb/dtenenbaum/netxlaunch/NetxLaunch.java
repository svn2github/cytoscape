import java.io.*;
import java.net.*;
import javax.jnlp.*;
import netx.jnlp.runtime.*;
import java.lang.reflect.*;

public class NetxLaunch  {
	
	String url;
	
	public NetxLaunch(String url) {
		this.url = url;
		
		String dirName = "c:/Documents and Settings";
		File dir = new File(dirName);
		String[] args;
				
		if (dir.exists()) {
			args = new String[4];
			args[0] = "-basedir";
			args[1] = dir.getAbsolutePath();
			args[2] = "-jnlp";
			args[3] = url;

			Class netx = Boot13.class;
		
			Method[] meths = netx.getDeclaredMethods();
			for (int i = 0; i < meths.length; i++) {
				if (meths[i].toString().indexOf("main") > -1) {
					try {
						meths[i].invoke(null, new Object[] { args });
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}


		} else { // not a windows system
			try {
				BasicService basicService = (BasicService)
					ServiceManager.lookup("javax.jnlp.BasicService");
				URL u = new URL(url);
				basicService.showDocument(u);
				System.exit(0);

			} catch (Exception e) {
				OpenBrowser.openURL(url);
				System.exit(0);			
				
			}
			
		}
		
		
				
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("you must supply a url");
			System.exit(0);
		}
		new NetxLaunch(args[0]);		
	}
	
}