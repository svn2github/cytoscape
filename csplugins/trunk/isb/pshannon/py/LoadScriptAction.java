package csplugins.isb.pshannon.py;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.*;

/**
 * This action will display the FileChooser dialog and allow the user
 * to select a Jython script. Once selected, the Jython script is entered into
 * the console.
 *
 * @author Jeff Davies
 * @version 1.0
 */

public class LoadScriptAction extends AbstractAction {

   SPyConsole console = null;
   static String driver_dir = System.getProperty("user.dir");

   /** Used to remember the last directory accessed by the user */
   static File lastDirectoryAccessed = new File(driver_dir,"../UserCode/");

   public LoadScriptAction(SPyConsole con) {
      super("Load Script...");
      console = con;
   }
 
   public static File getScriptFile( JFrame f ) {
      // This model has never been saved. Prompt the user for a filename
		JFileChooser chooser = new JFileChooser();
		PythonScriptFileFilter filter = new PythonScriptFileFilter();
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);

		if(lastDirectoryAccessed != null) {
         // Go the the last directory we opened
			chooser.setCurrentDirectory(lastDirectoryAccessed);
		}

		int option = chooser.showOpenDialog(f);
		// If not approved, return
		if(option != JFileChooser.APPROVE_OPTION)
			return null;

		// If nothing's selected, return
		if(chooser.getSelectedFile() == null)
			return null;

		// If file does not exist (typo kinda stuff), return
		File file = chooser.getSelectedFile();
		if(! file.exists()) {
			// Undone: redo action!
		  return null;
		}

		lastDirectoryAccessed = file; 
		return  file; 
   }
   
   public void actionPerformed(ActionEvent parm1) {
	  File file = getScriptFile( console.getAppFrame() );
      // OK. We have a valid file. Lets read it into a String object
      String data = loadFile(file);
      if( data != null ) {
		console.executeCommandSet(data);
	  }
   }


   /**
    * Read in the specified file and return its contents as a String
    * @param script The script as a File
    * @return String The contents of the specified script
    */
   private String loadFile(File script) {
		if( script == null ) return null;
		String line;
		StringBuffer data = new StringBuffer();

		System.out.println("Loading JPython script [ " + script.getName() + " ]");
		try {
			FileReader fr = new FileReader(script);
			BufferedReader br = new BufferedReader(fr);
			while((line = br.readLine()) != null) {
				//System.out.println("--- " + line);
				data.append(line + "\r\n");
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find specified file: " +  script.getName() + ". Please check the spelling");
		} catch (IOException e) {
			System.out.println("IO Exception!");
			e.printStackTrace();
		} finally {
			return data.toString();
		}
	}
}
