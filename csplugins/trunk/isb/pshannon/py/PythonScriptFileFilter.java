package csplugins.isb.pshannon.py;

import javax.swing.filechooser.*;
import java.io.File;


/**
 * File filter for selecting python scripts
 * @author Jeff Davies
 * @version 1.0
 */

public class PythonScriptFileFilter extends FileFilter {

   public PythonScriptFileFilter() {
   }


   /**
    * Determines in the given <CODE>pathname</CODE> is acceptable
    * @return <CODE>true</CODE> if the file is accaptable. Otherwise it returns <CODE>false</CODE>
    */
   public boolean accept(File f) {
      // The name of a directory is also acceptable
   	if(f.isDirectory()) {
			return true;
		}
   	String fileName = f.getName().toLowerCase();
   	if(fileName.endsWith(".py"))
      	return true;
      else
      	return false;
   }

   /**
    * Returns a description of this file type.
    * @return String A description of this fuile type
    */
   public String getDescription() {
      return "Python script (*.py)";
   }
}
