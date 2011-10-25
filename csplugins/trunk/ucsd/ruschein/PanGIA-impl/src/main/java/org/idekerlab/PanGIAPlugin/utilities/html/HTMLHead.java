package org.idekerlab.PanGIAPlugin.utilities.html;

import java.util.*;

public class HTMLHead extends HTMLTextBlock
{
	
	public HTMLHead()
	{
		
	}
	
	public HTMLHead(String title)
	{
		super(3);
		this.add("<HEAD>");
		this.add("\t<TITLE>"+title+"</TITLE>");
		this.add("</HEAD>");
	}
	
	public void setTitle(String title)
	{
		this.text = new ArrayList<String>(3);
		this.add("<HEAD>");
		this.add("\t<TITLE>"+title+"</TITLE>");
		this.add("</HEAD>");
	}
}
