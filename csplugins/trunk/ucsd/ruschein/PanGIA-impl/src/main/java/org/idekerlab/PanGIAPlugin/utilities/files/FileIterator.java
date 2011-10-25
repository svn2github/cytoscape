package org.idekerlab.PanGIAPlugin.utilities.files;

import java.util.*;
import java.io.*;

public class FileIterator implements Iterator<String>, Iterable<String>
{
	private final BufferedReader br;
	private String nextLine;
	
	public FileIterator(String file)
	{
		br = FileUtil.getBufferedReader(file);
		this.readNext();
	}
	
	public FileIterator(BufferedReader br)
	{
		this.br = br;
		this.readNext();
	}
	
	private void readNext()
	{
		try
		{
			nextLine = br.readLine();
			
			if (nextLine==null) br.close();
		}catch (IOException e) {System.out.println("Error FileUtil.fileIterator(String): "+e.getMessage()); e.printStackTrace();}
	}
	
	
	public boolean hasNext()
	{
		return nextLine!=null;
	}
	
	public String next()
	{
		String out = nextLine;
		readNext();
		
		return out; 
	}
	
	public void remove()
	{
		throw new UnsupportedOperationException("FileIterator does not support removal.");
	}
	
	public Iterator<String> iterator()
	{
		return this;
	}
	
}
