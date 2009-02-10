package Command;

import java.io.File;

import org.cytoscape.io.CyFileFilter;
import Tunable.Tunable;
import Utils.myFile;


public class fileChoose implements command {
	
	
	@Tunable(description = "File example",group={"Import Network File"})
	public myFile file;

	
	public fileChoose(){
		File[] files = new File[1];
		file = new myFile(files,false,new CyFileFilter[3]);
	}
	
	
	public void execute() {}
}
