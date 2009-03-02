package Utils;

import java.io.File;
import java.util.List;

public class myFile{
	
	File file;
	File[] files;
	boolean modal;
	java.util.List<String> paths;
	

	public myFile(File file,boolean modal){
//		this.filters = filters;
		this.modal = modal;
		this.file = file;
	}
	
	public myFile(File[] files,boolean modal){
		//this.filters = filters;
		this.modal = modal;
		this.files = files;
	}
	
	public List<String> getPaths() {
		return paths;
	}

	public void setPaths(List<String> path){
		paths = path;
	}
	
	
	public void setFiles(File[] infiles){
		files = infiles;
	}
	
	public String getPath(File infile){
		return file.getAbsolutePath();
	}
	
	public File[] getFiles(){
		return files;
	}
	
	
	public void setFile(File infile){
		file = infile;
	}
	
	public File getFile(){
		return file;
	}

	public boolean getModal(){
		return modal;
	}
}