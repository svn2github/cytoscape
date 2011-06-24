package org.cytoscape.work.internal.tunables.utils;


import java.io.File;
import java.util.Arrays;
import javax.swing.filechooser.FileFilter;


public class FileChooserFilter extends FileFilter {
    	private final String description;
    	private final String[] extensions;

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
    		if (file.isDirectory())
			return true;

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

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof FileChooserFilter))
			return false;

		final FileChooserFilter otherFilter = (FileChooserFilter)other;
		if (!otherFilter.description.equals(description))
			return false;

		if (otherFilter.extensions.length != extensions.length)
			return false;

		Arrays.sort(otherFilter.extensions);
		Arrays.sort(extensions);

		for (int i = 0; i < extensions.length; ++i) {
			if (!extensions[i].equals(otherFilter.extensions[i]))
				return false;
		}

		return true;
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