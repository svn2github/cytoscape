// JarLoaderAction: prompts user for which Jar to load a plugin from.
//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.actions;
//--------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.plugin.jar.*;

/**
 * This class provides an action for loading Cytoscape plugins from a jar file.
 * When triggered, it prompts the user to select a jar file and passes this file
 * to a new instance of JarClassLoader to search for plugins in that jar file.
 */
public class LoadPluginAction extends CytoscapeAction {
   
  protected File file;  //the jar file selected by the user

    /**
     * create an instance linked to the shared plugin registry.
     * @param cyObj
     */
    public LoadPluginAction () {
      super ("Load Plugins from Jar File");
      setPreferredMenu( "Plugins" );
      setAcceleratorCombo( java.awt.event.KeyEvent.VK_J, ActionEvent.CTRL_MASK) ;
    }

    /**
     * process the action. Ask the user for a file and attempt
     * to open it and load plugins from it.
     * @param e
     */
    public void actionPerformed (ActionEvent e) {
      if(!getFile()) return;
      String jarString = file.getPath();
      System.out.println("Chose: " + jarString);
      try {
        //JarClassLoader jcl = new JarClassLoader("file:" + jarString );
        //jcl.loadRelevantClasses();
        JarLoader.loadJar( jarString );
      }
      catch (Exception e1) {
        System.err.println ("Error 1: " + e1.getMessage ());
	}
    }


    /**
     * file browser.
     *
     * @return true if a valid file was chosen, false otherwise
     */
    private boolean getFile() {
        JFileChooser fChooser =
	    new JFileChooser( Cytoscape.getCytoscapeObj().getCurrentDirectory());
        fChooser.setDialogTitle("Load Plugin from Jar");
        switch (fChooser.showOpenDialog(null)) {

        case JFileChooser.APPROVE_OPTION:
            file = fChooser.getSelectedFile();

            try {
                FileReader fin = new FileReader(file);
                fin.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.toString(),
					      "Error Reading \"" +
					      file.getName()+"\"",
					      JOptionPane.ERROR_MESSAGE);
                return false;
            }
	        Cytoscape.getCytoscapeObj().setCurrentDirectory(file);
            return true;
        default:
            // cancel or error
            return false;
        }
    }
}


