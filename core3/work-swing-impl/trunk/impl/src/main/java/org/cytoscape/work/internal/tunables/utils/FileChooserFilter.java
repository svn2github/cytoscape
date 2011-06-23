package org.cytoscape.work.internal.tunables.utils;


import java.io.File;
import javax.swing.filechooser.FileFilter;


public class FileChooserFilter extends FileFilter {
    	private final String description;
    	private String[] extensions;

    	public FileChooserFilter(final String description, final String extension) {
    		super();
    		this.description = description;
    		this.extensions = new String[] { extension };
    	}

    	public FileChooserFilter(final String description, final String[] extensions) {
    		super();
    		this.description = description;
    		this.extensions = extensions;
    	}

    	//accept or not the file from jfilechooser
    	public boolean accept(final File file) {
    		if (file.isDirectory()) return true;

    		String fileName = file.getName().toLowerCase();

    		if (extensions != null) {
    			for(int i = 0; i < extensions.length; i++) {
    				if (fileName.endsWith(extensions[i]))
    					return true;
    			}

    			for(int i = 0; i < extensions.length; i++) {
    				if (fileName.contains(extensions[i]))
    					return true;
    			}
    		} else
			throw new IllegalArgumentException("No fileType specified");

    		return false;
    	}

	public String getDescription(){
	        return description;
	}
	
	public String[] getExtensions() {
	        return extensions;
	}

	static String toString(final String[] strings) {
		final StringBuilder setAsString = new StringBuilder();
		setAsString.append('{');
		for (final String item : strings) {
			setAsString.append(item + " ");
		}
		setAsString.append('}');
		return setAsString.toString();
	}
}