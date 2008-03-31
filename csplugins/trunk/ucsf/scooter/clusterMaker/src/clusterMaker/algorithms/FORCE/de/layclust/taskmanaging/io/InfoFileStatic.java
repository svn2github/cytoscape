package de.layclust.taskmanaging.io;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.layclust.taskmanaging.TaskConfig;


public class InfoFileStatic {
	
	private static final String NL=TaskConfig.NL;
	
	static BufferedWriter bw;
	static String fileName;
	
	public static void createFiles(String fileName) {
		
		try {
			if (TaskConfig.info) {
				
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
				bw.write("LogFile from: " + dateTime + NL);
				bw.write(NL);
				
			}
		} catch (IOException e) {
			System.out.println("Unable to write infofile: " + fileName);
			e.printStackTrace();
		}
		
	}
	
	public static void closeFiles() {
		try {
			if (TaskConfig.info) {
				bw.close();
			}
		} catch (IOException e) {
			System.out.println("Unable to write infofile: " + fileName);
			e.printStackTrace();
		}
	}
	
	public static void print(String p) {
		try {
			System.out.print(p);
			if (TaskConfig.info) {
				bw.write(p);
			}
		} catch (IOException e) {
			System.out.println("Unable to write to infofile: " + fileName);
			e.printStackTrace();
		}
	}
	
	public static void println(String p) {
		try {
			System.out.println(p);
			if (TaskConfig.info) {
				bw.write(p + NL);
			}
		} catch (IOException e) {
			System.out.println("Unable to write to infofile: " + fileName);
			e.printStackTrace();
		}
	}
	
	public static void println() {
		try {
			System.out.println();
			if (TaskConfig.info) {
				bw.write(NL);
			}
		} catch (IOException e) {
			System.out.println("Unable to write to infofile: " + fileName);
			e.printStackTrace();
		}
	}
	
	private static BufferedWriter myBufferedWriter(String file) throws IOException
    {       
        return new BufferedWriter(new FileWriter(file));
    }
	
}
