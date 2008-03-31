package de.layclust.taskmanaging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TestFile {
	
	private static final String NL=TaskConfig.NL;
	
	static BufferedWriter bw;
	static String fileName;
	
	public static void initiateFile(String fileName) {
		
		try {
//			if (TaskConfig.log) {
				
				bw = myBufferedWriter(fileName);
				
				// write init stuff
				
				SimpleDateFormat fmt = new SimpleDateFormat();
				fmt.applyPattern( "MM'/'dd'/'yyyy '-' HH:mm" );
				Calendar cal = new GregorianCalendar();
				
				String dateTime = fmt.format(cal.getTime());
				
				bw.write("-----------------------------------------------------------------" + NL);
				bw.write(TaskConfig.NAME + " v" + TaskConfig.VERSION + NL);
				bw.write("Copyright (c) 2006 by " + TaskConfig.AUTHORS + NL);
				bw.write("-----------------------------------------------------------------" + NL);
				bw.write(NL);
				bw.write("Clusters file from: " + dateTime + NL);
				bw.write(NL);
				
//				if (TaskConfig.flushLogFileImmediately) {
//					bw.flush();
//				}
//			}
		} catch (IOException e) {
			System.out.println("Unable to write clusters file: " + fileName);
			e.printStackTrace();
		}
		
	}
	
	public static void closeFile() {
		try {
			bw.flush();
				bw.close();
		} catch (IOException e) {
			System.out.println("Unable to write clusters file: " + fileName);
			e.printStackTrace();
		}
	}
	
	public static void print(String p) {
		try {
				bw.write(p);
		} catch (IOException e) {
			System.out.println("Unable to write to clusters file: " + fileName);
			e.printStackTrace();
		}
	}
	
	public static void println(String p) {
		try {
				bw.write(p + NL);
		} catch (IOException e) {
			System.out.println("Unable to write to clusters file: " + fileName);
			e.printStackTrace();
		}
	}
	
	public static void println() {
		try {
				bw.write(NL);
		} catch (IOException e) {
			System.out.println("Unable to write to clusters file: " + fileName);
			e.printStackTrace();
		}
	}
	
	private static BufferedWriter myBufferedWriter(String file) throws IOException
    {       
        return new BufferedWriter(new FileWriter(file));
    }
	

}
