package org.idekerlab.PanGIAPlugin.utilities.html;

import java.io.*;
import org.idekerlab.PanGIAPlugin.data.*;
import org.idekerlab.PanGIAPlugin.utilities.files.*;

public class HTMLPage extends HTMLHyperBlock
{
	private HTMLHead head;
	private HTMLBody body;
	
	public HTMLPage()
	{
		super(2);
		this.head = new HTMLHead();
		this.body = new HTMLBody();
		
		this.add(head);
		this.add(body);
	}
	
	
	public void setTitle(String title)
	{
		head.setTitle(title);
	}
	
	public void add(StringTable st, int border, Alignment al, int width)
	{
		body.add(new HTMLTable(st,border,al,width));
	}
	
	public void addToBody(HTMLBlock b)
	{
		body.add(b);
	}
	
	public void write(String file)
	{
		BufferedWriter bw = FileUtil.getBufferedWriter(file, false);
		
		try
		{
			bw.write("<HTML>\n");
		
			super.write(bw, 0);
						
			bw.write("</HTML>\n");
			bw.close();
		}catch (IOException e) {System.out.println("Error HTMLPage.write(String): "+e);System.exit(0);}
	}
}
