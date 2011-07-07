package main.java.org.cytoscape.komal;

import java.io.*;

public class  GetCurrentDir{
	public static void dirlist(String fname){
		File dir = new File(fname);
		System.out.println("Current Working Directory : "+ dir);
	}

	public static void main(String[] args){
		String currentdir = System.getProperty("user.dir");
		dirlist(currentdir);
	}
}