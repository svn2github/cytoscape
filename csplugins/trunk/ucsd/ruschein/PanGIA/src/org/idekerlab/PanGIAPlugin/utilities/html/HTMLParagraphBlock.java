package org.idekerlab.PanGIAPlugin.utilities.html;

import java.io.BufferedWriter;
import java.io.IOException;

public class HTMLParagraphBlock extends HTMLTextBlock
{
	
	public HTMLParagraphBlock()
	{
		super();
	}
	
	public HTMLParagraphBlock(int numLines)
	{
		super(numLines);
	}
	
	protected void write(BufferedWriter bw, int depth)
	{
		String tabs = "";
		for (int i=0;i<depth;i++) tabs+="\t";
		
		try
		{
			bw.write(tabs+"<P>\n");
			
			for (String line : text)
				bw.write(tabs+line+"<BR>\n");
			
			bw.write(tabs+"</P>\n");
		}catch (IOException e) {e.printStackTrace();}
	}
}
