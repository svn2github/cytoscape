package csplugins.isb.pshannon.py;

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.*;
import java.awt.event.*;

/**
 * This action will display the FileChooser dialog and allow the user
 * to save a Jython script. 
 *
 * @authors Jeff Davies, Tom Maxwell
 * @version 1.0
 */

public class SaveScriptAction extends AbstractAction {

   SPyConsole _console = null;
   boolean _debug = true;
  /** Used to remember the last directory accessed by the user */
   protected static File _currentFile;
   protected static JFileChooser _fileChooser;
   protected boolean _prompt;

   public SaveScriptAction(SPyConsole con, boolean prompt ) {
      super( prompt ? "Save Script As..." : "Save Script..." );
      _console = con;
      _prompt = prompt;
   }
   public void actionPerformed(ActionEvent parm1) {
	  if( _prompt ) saveDialog();
	  else saveFile();
   }


  public void saveFile(String filename) throws IOException {
    File file = (filename == null ? null : new File(filename));
    saveFile(file);
  }

  /** saves the given file */
  public void  saveFile(File file) throws IOException {
    byte[] bytes = _console.getText().getBytes();
    FileOutputStream out = new FileOutputStream(file);
    out.write(bytes);
    out.close();
    _currentFile = file;
  }
 

  /** pops up a dialog box for the user to select a file to save */
  public boolean saveDialog() {
	 if( _fileChooser == null ) {
		_fileChooser = new JFileChooser(System.getProperty("user.dir"));
		_fileChooser.addChoosableFileFilter( new PythonScriptFileFilter());
		if(_currentFile != null) {
         // Go the the last directory we opened
			_fileChooser.setCurrentDirectory(_currentFile);
		}
	}
    _fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    if (_fileChooser.showSaveDialog(_console.getAppFrame()) != JFileChooser.APPROVE_OPTION) {
      // user has canceled request
      return false;
    }
    try {
      saveFile(_fileChooser.getSelectedFile());
    }
    catch (IOException exc) {
      if (_debug) exc.printStackTrace();
      return false;
    }
    return true;
  }

  /** saves the file under its current name */
  public boolean saveFile() {
    boolean success = false;
    if (_currentFile == null) success = saveDialog();
    else {
      try {
        saveFile(_currentFile);
        success = true;
      }
      catch (IOException exc) {
        // display error box
        JOptionPane.showMessageDialog(_console.getAppFrame(), "Could not save the file.",
          "SME Python Interface", JOptionPane.ERROR_MESSAGE);
      }
    }
    return success;
  }
}
