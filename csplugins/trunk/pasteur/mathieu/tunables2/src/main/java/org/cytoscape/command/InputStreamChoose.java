package org.cytoscape.command;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.cytoscape.work.*;

public class InputStreamChoose implements Command{

	@Tunable(description = "URL choose",group = {"Import Network File"})
	public InputStream is;
	
	public InputStreamChoose(){
//		try{
//			url = new URL("");
//		}catch(MalformedURLException e){e.printStackTrace();}
	}

	public void execute(){}
	
}