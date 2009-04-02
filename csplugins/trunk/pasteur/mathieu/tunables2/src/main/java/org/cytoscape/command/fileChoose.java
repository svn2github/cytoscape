package org.cytoscape.command;

import java.io.File;

import org.cytoscape.work.Tunable;


public class fileChoose implements Command {
	
	
	@Tunable(description = "File example",group={"Import Network File"})
	public File file;	
	
	public void execute() {}
}
