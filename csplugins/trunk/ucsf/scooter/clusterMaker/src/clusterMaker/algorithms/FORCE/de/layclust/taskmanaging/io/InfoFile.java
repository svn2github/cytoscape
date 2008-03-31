package de.layclust.taskmanaging.io;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.layclust.taskmanaging.TaskConfig;

public class InfoFile extends Outfile {
	
	private static StringBuffer projectDetails = null;
	
	public void createAndCloseInfoFile(){
		printInfoHeader();
		printProjectDetails();
		closeFile();	
	}
	
	public void printInfoHeader(){
		//TODO
		SimpleDateFormat fmt = new SimpleDateFormat();
		fmt.applyPattern( "MM'/'dd'/'yyyy '-' HH:mm" );
		Calendar cal = new GregorianCalendar();
		
		String dateTime = fmt.format(cal.getTime());
		
		println("-------------------------------------------------------------------------------");
		println(TaskConfig.NAME + " v" + TaskConfig.VERSION + NL);
		println("Copyright (c) 2008 by");
		for (int i = 0; i < TaskConfig.AUTHORS.length; i++) {
			println(TaskConfig.AUTHORS[i]);
		}
		println("-------------------------------------------------------------------------------");
		println(NL);
		println("InfoFile from: " + dateTime);
		println(NL);
		
	}
	
	public static void apppendLnToProjectDetails(String string){
		if(TaskConfig.info){
			if(projectDetails == null){
				projectDetails = new StringBuffer();
			}
			projectDetails.append(string);
			projectDetails.append(NL);
		}
	}
	
	public static void appendToProjectDetails(String string){
		if(TaskConfig.info){
			if(projectDetails == null){
				projectDetails = new StringBuffer();
			}
			projectDetails.append(string);
		}
	}
	
	public static void appendNewLnToProjectDetails(){
		if(TaskConfig.info){
			if(projectDetails == null){
				projectDetails = new StringBuffer();
			}
			projectDetails.append(NL);
		}
	}
	
	public static void appendHeaderToProjectDetails(String header){
		if(TaskConfig.info){
			if(projectDetails == null){
				projectDetails = new StringBuffer();
			}
			projectDetails.append(NL);
			projectDetails.append("##  ");
			projectDetails.append(header);
			projectDetails.append("  ##");
			projectDetails.append(NL);
		}
	}
	
	public void printProjectDetails(){
		printnewln();

		println("######  PROJECT DETAILS  ######");

		printnewln();
		print(projectDetails.toString());

	}
	

}
