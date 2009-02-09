package Command;

import java.io.File;
import org.cytoscape.io.CyFileFilter;
import cytoscape.util.FileUtil;
import Tunable.Tunable;
import Utils.myFileChooseDialog;


public class fileChoose implements command {
	
	
	@Tunable(description = "File example",group={"Import Network File"})
	public myFileChooseDialog file1;

	
	public fileChoose(){
		CyFileFilter[] filters = null;
		FileUtil fileUtil = null;
		File file = null;
		file1 = new myFileChooseDialog(file,filters,fileUtil);
		
	}
	
	
	public void execute() {}
}
