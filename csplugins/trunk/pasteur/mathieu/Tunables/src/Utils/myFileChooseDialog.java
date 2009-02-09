package Utils;

import java.io.File;
import org.cytoscape.io.CyFileFilter;
import cytoscape.util.FileUtil;

public class myFileChooseDialog{
	
	CyFileFilter[] filters;
	FileUtil fileUtil;
	File file;
	public myFileChooseDialog(File file,CyFileFilter[] filters, FileUtil fileUtil){
		this.filters = filters;
		this.fileUtil = fileUtil;
		this.file = new File("");
	}
	
	public File getFile(){
		return file;
	}
	
}