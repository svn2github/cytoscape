/**
 * 
 */
package cytoscape.plugin.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author skillcoy
 * 
 */
public class UnzipUtil
	{
	public static boolean STOP = false;
	
	private static void print(String S)
		{
		System.out.println(S);
		}

	/**
   * 
   * @param is
   *   InputStream for a zip file
   */
	public static List<String> unzip(InputStream is) throws java.io.IOException
		{
		ArrayList<String> UnzippedFiles = new ArrayList<String>();

		ZipInputStream zis = new ZipInputStream(is);
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null && !STOP)
			{
			int BUFFER = 2048;
			File ZipFile = new File(entry.getName());
			System.out.println("Extracting ZIP entry " + entry.getName() + " to " + ZipFile.getAbsolutePath());
			if (!entry.isDirectory())
				{
				File ParentDirs = new File(ZipFile.getParent());
				if (!ParentDirs.exists()) ParentDirs.mkdirs();
				}
			else 
				{ // entry is a directory, create it and move on
				if (!ZipFile.exists()) ZipFile.mkdirs();
				continue;
				}
			FileOutputStream fos = new FileOutputStream(ZipFile);
			BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

			// write the files to the disk
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = zis.read(data, 0, BUFFER)) != -1)
				{ dest.write(data, 0, count); }
			dest.flush();
			dest.close();
			
			UnzippedFiles.add(ZipFile.getAbsolutePath());
			}
		return UnzippedFiles;
		}
	
	// this is kinda cheap/hacky but it might be expandable/more useful later
	public static boolean zipContains(InputStream is, String Regex) throws java.io.IOException
		{
		boolean found = false;
		Pattern p = Pattern.compile(Regex);
		
		ZipInputStream zis = new ZipInputStream(is);
		ZipEntry entry; 
		while (( entry = zis.getNextEntry()) != null && !STOP)
			{
			System.out.println("  * Matching " + entry.getName() + " " + p.pattern());
			Matcher m = p.matcher(entry.getName());
			if (m.matches()) found = true;
			}
		return found;
		}
	
	
	}
