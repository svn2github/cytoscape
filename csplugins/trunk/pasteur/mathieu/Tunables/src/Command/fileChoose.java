package Command;

import java.io.File;

//import org.cytoscape.io.CyFileFilter;

import Tunable.Tunable;
import Utils.myFile;


public class fileChoose implements command {
	
	
	@Tunable(description = "File example",group={"Import Network File"})
	public myFile myfile;

	
	public fileChoose(){
		File[] files = new File[1];
		File file = new File("");
//		myfile = new myFile(file,false,new CyFileFilter[3]);
	}
	
	
	public void execute() {}
}
