package org.idekerlab.PanGIAPlugin.utilities.html;

import java.io.BufferedWriter;
import java.io.IOException;

public class HTMLBody extends HTMLHyperBlock
{
	protected void write(BufferedWriter bw, int depth)
	{
		String tabs = "";
		for (int i=0;i<depth;i++) tabs+="\t";
		
		try
		{
			bw.write(tabs+"<BODY>\n");
		
			super.write(bw, depth+1);
						
			bw.write(tabs+"</BODY>\n");
		}catch (IOException e) {System.out.println("Error HTMLPage.write(String): "+e);System.exit(0);}
	}
}
