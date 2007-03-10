package cytoscape.plugin.util;

import java.net.URL;
import java.net.HttpURLConnection;
import java.lang.StringBuffer;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.File;

import java.io.*;

/**
 * @author skillcoy
 * 
 */
public class HttpUtils
	{
	public static boolean STOP = false;
	
	private static HttpURLConnection buildConnection(URL url)
			throws java.io.IOException
		{
		HttpURLConnection Connect = (HttpURLConnection) url.openConnection();
		Connect.setDoInput(true);
		Connect.setDoOutput(true);
		Connect.setUseCaches(false);
		Connect.setAllowUserInteraction(false);
		Connect.setRequestMethod("POST");
		return Connect;
		}

	public static InputStream getInputStream(String url) throws java.io.IOException
		{
		HttpURLConnection Connection = buildConnection(new URL(url));
		return Connection.getInputStream();
		}
	
	
//	public static String download(String url) throws java.io.IOException
//		{
//		InputStream is = getInputStream(url);
//		
//		StringBuffer buffer = new StringBuffer();
//		int c;
//		while ((c = is.read()) != -1)
//			{
//			buffer.append((char) c);
//			}
//		return buffer.toString();
//		}

	public static File downloadFile(String url, String fileName, String Dir) throws java.io.IOException
		{
		InputStream is = getInputStream(url);
		// er..not sure how to do this

		java.util.List<Byte> FileBytes = new java.util.ArrayList<Byte>();
		
		byte[] buffer = new byte[1];
		while ( (is.read(buffer)) != -1 && !STOP)
			{
			FileBytes.add( buffer[0] );
			}

		System.out.println("total bytes: " + FileBytes.size());
		File Download = new File(Dir + fileName);
		FileOutputStream os = new FileOutputStream( Download );
		
		for (int i=0; i<FileBytes.size(); i++)
			{
			if (!STOP) os.write( new byte[] {FileBytes.get(i)});
			else break;
			}
		os.flush();
		os.close();
		
		if (STOP) Download.delete();
		
		return Download;
		}
	
	}
