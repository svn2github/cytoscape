package org.cytoscape.command;

import java.io.File;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.myFile;


public class fileChoose implements Command {
	
	
	@Tunable(description = "File example",group={"Import Network File"})
	public myFile myfile;

	
	public fileChoose(){
		File[] files = new File[1];
		File file = new File("");
		myfile = new myFile(file);
	}
	
	
	public void execute() {}
}
