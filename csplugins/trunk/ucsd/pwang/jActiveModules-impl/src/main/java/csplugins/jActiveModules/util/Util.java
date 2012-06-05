package csplugins.jActiveModules.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;

import csplugins.jActiveModules.ActiveModulesUI;

public class Util {

	public static File getFileFromFar(String fileNameInJar){

		File returnFile = null;
		
		// Get the file contents from Jar
		ArrayList<String> list = new ArrayList<String>();

		BufferedReader bf = null;
		String line;

		try {
			bf = new BufferedReader(new InputStreamReader(ActiveModulesUI.class.getResourceAsStream("/"+fileNameInJar) ));	
			while (null != (line = bf.readLine())) {
				list.add(line);
			}
		}
		catch (IOException e){
		}
		finally {
			try {
				if (bf != null) bf.close();
			}
			catch (IOException e) {
			}
		}

		if (list.size()==0){
			return null;
		}

		// output the file content to a tmp file
		Writer output;
		try {
			returnFile = File.createTempFile("jActiveModules_vs", ".props", new File(System.getProperty("java.io.tmpdir")));
			returnFile.deleteOnExit();
			
			//use buffering
			output = new BufferedWriter(new FileWriter(returnFile));
			//FileWriter always assumes default encoding is OK!
			for (int i=0; i< list.size(); i++){
				output.write( list.get(i)+ "\n");				
			}
		    output.close();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}

		return returnFile;
	}
}
