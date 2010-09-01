package org.idekerlab.PanGIAPlugin.utilities.files;

import java.io.*;
import java.util.zip.*;
import java.util.*;

import org.idekerlab.PanGIAPlugin.data.StringVector;
import org.idekerlab.PanGIAPlugin.utilities.files.FileUtil;


public class FileUtil
{
	public static BufferedReader getGZBufferedReader(String file)
	{
		try
		{
			return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
		} catch (Exception e) {System.out.println(e.getMessage());e.printStackTrace();}
		
		return null;
	}
	
	public static BufferedWriter getGZBufferedWriter(String file, boolean append)
	{
		try
		{
			return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file))));
		} catch (Exception e) {System.out.println(e.getMessage());e.printStackTrace();}
		
		return null;
	}
	
	public static void gUnzip(String infile, String outfile)
	{
		try {
	        // Open the compressed file
	        GZIPInputStream in = new GZIPInputStream(new FileInputStream(infile));
	        
	        // Open the output file
	        OutputStream out = new FileOutputStream(outfile);
	    
	        // Transfer bytes from the compressed file to the output file
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	    
	        // Close the file and stream
	        in.close();
	        out.close();
	    } catch (IOException e)
	    {
	    	System.out.println("Error FileUtil.gUnzip(String,String):");
	    	System.out.println(e);
	    	System.exit(0);
	    }
	}
	
	public static int countLines(String file)
	{
		int count = 0;
		for (String line : new FileIterator(file))
			count++;
			
		return count;
	}
	
	public static int countColumns(String file)
	{
		BufferedReader br = getBufferedReader(file);
		
		int cols = -1;
		try
		{
			cols = br.readLine().split("\t").length;
		} catch (IOException e) {System.out.println("Error: FileUtil.countColumns(String): "+e);}
		
		return cols;
	}
	
	public static void copyfile(String srFile, String dtFile)
	{
		copyfile(srFile,dtFile,false);
	}
	
	public static void appendfile(String srFile, String dtFile)
	{
		copyfile(srFile,dtFile,true);
	}
	
	/**
	 * http://www.roseindia.net/java/beginners/CopyFile.shtml
	 */
	private static void copyfile(String srFile, String dtFile, boolean append){
	    try{
	      File f1 = new File(srFile);
	      File f2 = new File(dtFile);
	      InputStream in = new FileInputStream(f1);
	      
	      OutputStream out = new FileOutputStream(f2,append);

	      byte[] buf = new byte[1024];
	      int len;
	      while ((len = in.read(buf)) > 0){
	        out.write(buf, 0, len);
	      }
	      in.close();
	      out.close();
	      System.out.println("File copied.");
	    }
	    catch(FileNotFoundException ex){
	      System.out.println(ex.getMessage() + " in the specified directory.");
	      System.exit(0);
	    }
	    catch(IOException e){
	      System.out.println(e.getMessage());      
	    }
	  }
	
	public static void sortFile(String srFile, String dtFile)
	{
		StringVector sv = new StringVector(FileUtil.countLines(srFile));
		
		
		FileReader fr = null;
		try
		{
			fr = new FileReader(srFile);
		}catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		BufferedReader br = new BufferedReader(fr);
			
		String line="";
		while (line!=null); 
		{
			try
			{
				line = br.readLine();
			
				if (line!=null)	{System.out.println(line);sv.add(line);}
				
			}catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		try {br.close();fr.close();} catch (IOException ioe){ioe.printStackTrace();}
		
		sv = sv.sort();
		
		sv.save(dtFile);
	}
	
	public static void makeDirectory(String dir)
	{
		new File(dir).mkdir();
	}
	
	public static void cutFileHead(String srFile, String dtFile, int numLines)
	{
		BufferedReader br = getBufferedReader(srFile);
		BufferedWriter bw = getBufferedWriter(dtFile,false);
				
		int lcount = 0;
		
		String line="";
		while (line!=null)
		{
			try
			{
				line = br.readLine();
			
				if (line!=null)	
				{
					if (lcount<numLines) lcount++;
					else
					{
						bw.write(line+"\n");
					}
				}
				
			}catch (IOException e)
			{
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}
		
		try {br.close();bw.close();} catch (IOException ioe){System.out.println(ioe.getMessage());System.exit(0);}
		
	}
	
	public static BufferedWriter getBufferedWriter(String dtFile, boolean append)
	{
		FileWriter fw = null;
		try
		{
			fw = new FileWriter(dtFile,append);
		}catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}catch (IOException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		return bw;
	}
	
	public static BufferedReader getBufferedReader(String srFile)
	{
		FileReader fr = null;
		try
		{
			fr = new FileReader(srFile);
		}catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		BufferedReader br = new BufferedReader(fr);
		
		return br;
	}
	
	public static boolean exists(String file)
	{
		return new File(file).exists();
	}
	
	public static void delete(String file)
	{
		File f = new File(file);
		
		if (!f.exists()) return;
		
		if (!f.delete())
		{
			for (File fin : f.listFiles())
				delete(fin);
			
			f.delete();
		}
	}
	
	public static void delete(File f)
	{
		if (!f.delete())
		{
			for (File fin : f.listFiles())
				delete(fin);
			
			f.delete();
		}
	}

}
