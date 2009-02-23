package org.cytoscape.command;



import org.cytoscape.io.CyFileFilter;

import java.io.File;
import org.cytoscape.work.util.*;
import org.cytoscape.work.*;

public class fileChoose implements Command {
	
	
	@Tunable(description = "File example",group={"Import Network File"})
	public myFile myfile;

	
	public fileChoose(){
		File[] files = new File[1];
		File file = new File("");
//		myfile = new myFile(file,false,new CyFileFilter[3]);
	}
	
	
	public void execute() {}
}
