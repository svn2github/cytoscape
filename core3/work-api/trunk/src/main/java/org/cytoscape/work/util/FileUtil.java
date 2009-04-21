/*
  File: FileUtil.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.work.util;

import java.awt.Component;
import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.cytoscape.work.TaskMonitor;


/**
 * Provides a platform-dependent way to open files. Mainly
 * because Mac would prefer that you use java.awt.FileDialog
 * instead of the Swing FileChooser.
 */
public interface FileUtil {

	/**
	 *
	 */
	int LOAD = FileDialog.LOAD;

	/**
	 *
	 */
	int SAVE = FileDialog.SAVE;

	/**
	 *
	 */
	int CUSTOM = LOAD + SAVE;

	/**
	 * A string that defines a simplified java regular expression for a URL.
	 * This may need to be updated to be more precise.
	 */
	String urlPattern = "^(jar\\:)?(\\w+\\:\\/+\\S+)(\\!\\/\\S*)?$";

	/**
	 * Returns a File object, this method should be used instead
	 * of rolling your own JFileChooser.
	 *
	 * @return the location of the selcted file
	 * @param title the title of the dialog box
	 * @param load_save_custom a flag for the type of file dialog
	 */
	File getFile(String title, int load_save_custom);

	/**
	 * Returns a File object, this method should be used instead
	 * of rolling your own JFileChooser.
	 *
	 * @return the location of the selcted file
	 * @param title the title of the dialog box
	 * @param load_save_custom a flag for the type of file dialog
	 * @param filters an array of CyFileFilters that let you filter
	 *                based on extension
	 * @param start_dir an alternate start dir, if null the default
	 *                  cytoscape MUD will be used
	 * @param custom_approve_text if this is a custom dialog, then
	 *                            custom text should be on the approve
	 *                            button.
	 */
	File getFile(String title, int load_save_custom,
	                           String start_dir, String custom_approve_text) ;


    /**
     * Returns an array of File objects, this method should be used instead
     * of rolling your own JFileChooser.
     * @return the location of the selcted file
     * @param parent the parent component of the JFileChooser dialog
     * @param title the title of the dialog box
     * @param load_save_custom a flag for the type of file dialog
     * @param filters an array of CyFileFilters that let you filter
     *                based on extension
     */
    File[] getFiles(Component parent, String title, int load_save_custom) ;
  

	/**
	 * Returns a list of File objects, this method should be used instead
	 * of rolling your own JFileChooser.
	 *
	 * @return and array of selected files, or null if none are selected
	 * @param title the title of the dialog box
	 * @param load_save_custom a flag for the type of file dialog
	 * @param filters an array of CyFileFilters that let you filter
	 *                based on extension
	 * @param start_dir an alternate start dir, if null the default
	 *                  cytoscape MUD will be used
	 * @param custom_approve_text if this is a custom dialog, then
	 *                            custom text should be on the approve
	 *                            button.
	 */
	File[] getFiles(String title, int load_save_custom,
	                              String start_dir, String custom_approve_text) ;
	 
	/**
	  * Returns a list of File objects, this method should be used instead
	  * of rolling your own JFileChooser.
	  *
	  * @return and array of selected files, or null if none are selected
	  * @param title the title of the dialog box
	  * @param load_save_custom a flag for the type of file dialog
	  * @param filters an array of CyFileFilters that let you filter
	  *                based on extension
	  * @param start_dir an alternate start dir, if null the default
	  *                  cytoscape MUD will be used
	  * @param custom_approve_text if this is a custom dialog, then
	  *                            custom text should be on the approve
	  *                            button.
	  * @param multiselect Enable selection of multiple files (Macs are
	  *                    still limited to a single file because we use
	  *                    FileDialog there -- is this fixed in Java 1.5?)
	  */	
	File[] getFiles(String title, int load_save_custom,
          String start_dir, String custom_approve_text, boolean multiselect) ;

	/**
	  * Returns a list of File objects, this method should be used instead
	  * of rolling your own JFileChooser.
	  *
	  * @return and array of selected files, or null if none are selected
	  * @param parent the parent of the JFileChooser dialog
	  * @param title the title of the dialog box
	  * @param load_save_custom a flag for the type of file dialog
	  * @param filters an array of CyFileFilters that let you filter
	  *                based on extension
	  * @param start_dir an alternate start dir, if null the default
	  *                  cytoscape MUD will be used
	  * @param custom_approve_text if this is a custom dialog, then
	  *                            custom text should be on the approve
	  *                            button.
	  * @param multiselect Enable selection of multiple files (Macs are
	  *                    still limited to a single file because we use
	  *                    FileDialog there -- is this fixed in Java 1.5?)
	  */
	File[] getFiles(Component parent, String title, int load_save_custom,
	                              String start_dir, String custom_approve_text, boolean multiselect) ;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	InputStream getInputStream(String name) ;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 * @param monitor DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	InputStream getInputStream(String name, TaskMonitor monitor) ;

	/**
	 *
	 * @param filename 	File to read in
	 *
	 * @return  The contents of the given file as a string.
	 */
	String getInputString(String filename) ;

	/**
	 * @param inputStream An InputStream
	 *
	 * @return  The contents of the given file as a string.
	 */
	String getInputString(InputStream inputStream) throws IOException ;

	/**
	 * Get the most recently used directory.
	 */
	File getMRUD();
 
	/**
	 * Set the most recently used directory.
	 * @param mrud The most recently used directory. 
	 */
	void setMRUD(File mrud);

}
