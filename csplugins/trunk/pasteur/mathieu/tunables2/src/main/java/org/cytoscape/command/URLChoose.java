package org.cytoscape.command;

import java.net.MalformedURLException;
import java.net.URL;
import org.cytoscape.work.*;

public class URLChoose implements Command{

	@Tunable(description = "URL choose",group = {"Import Network File"})
	public URL url;
	
	public URLChoose(){
//		try{
//			url = new URL("");
//		}catch(MalformedURLException e){e.printStackTrace();}
	}

	public void execute(){}
	
}