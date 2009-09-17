package org.cytoscape.work.internal.tunables.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileChooserFilter extends FileFilter {
        	
    	private final String description;
    	private String extension;
    	private String[] extensions;
 
    	public FileChooserFilter(String description, String extension){
    		super();
    		this.description = description;
    		this.extension = extension;
    	}
    
    	public FileChooserFilter(String description, String[] extensions) {
    		super();
    		this.description = description;
    		this.extensions = extensions;
    	}
    	
    	public boolean accept(File file){
    		if (file.isDirectory()) return true;
        
    		String fileName = file.getName().toLowerCase();

    		
    		if (extensions != null){
    			for(int i=0; i<extensions.length; i++){
    				if (fileName.endsWith(extensions[i]))
    					return true;
    			}
    			
    			for(int i=0;i<extensions.length;i++){
    				if(fileName.contains(extensions[i]))
    					return true;
    			}
    		}
    		
    		else if(extension != null){
    			if(fileName.contains(extension))
    				return true;
    			else if (fileName.endsWith(extension))
    				return true;
    		}

    		else throw new IllegalArgumentException("No fileType specified");
    		//else if (extension.contains("attr")|| extension.contains("Attr")){
    		//System.out.println("Attribute type file detected");
    		//return true;
    		//}
//    		else
//    			return fileName.endsWith(extension);
    		return false;
    	}
    	
	    public String getDescription(){
	        return description;
	    }
	 
	    public String getExtension(){
	        return extension;
	    }
	 
	    public String[] getExtensions() {
	        return extensions;
	    }
    }