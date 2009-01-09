// JarPluginDirectoryAction: prompts user for which dir to load Jars from.
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
import cytoscape.plugin.jar.JarLoader;

/**
 * This class provides an action for loading Cytoscape plugins from jar files
 * found in a directory selected by the user. When triggered, it prompts the
 * user to select a directory, gets every jar file within that directory, and
 * passes each jar file to a new instance of JarClassLoader to search for
 * plugins.
 */
public class LoadPluginDirectoryAction extends CytoscapeAction {
   
    protected File file;
    protected boolean ready=false;

   
    public LoadPluginDirectoryAction () {
        super ("Load Plugins from Jar Directory");
        setPreferredMenu( "Plugins" );
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_J, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK) ;
    }

    /**
     * process the action event. Try to set the directory and then
     * load plugins from contained jar files.
     * @param e
     */
    public void actionPerformed (ActionEvent e) {
        if(!getDir()) return;
        ready=true;
        tryDirectory();
    }

    /**
     * sets directory manually
     */
    public void setDir(String newFile) {
        try {
            file = new File(newFile);
            if(file.isDirectory()) ready=true;
            else {
                String s = file.getParent();
                file = new File(s);
                if(file.isDirectory()) ready=true;
                else { ready=false; }
            }
        }
        catch (Exception e) {
            System.err.println ("Not file: "+newFile+"\n"+e.getMessage());
            ready=false;
        }
    }

    /**
    * tries the currently selected directory. Can be called manually or
    * automatically from actionPerformed. If calling manually, you are
    * first required to call setDir().
    */
    public void tryDirectory() {
        if(ready==false) return;
        String[] fileList = file.list();

        String slashString="";
        if(!(file.getPath().endsWith("/"))) slashString="/";

        for(int i=0; i<fileList.length; i++) {
            if(!(fileList[i].endsWith(".jar"))) continue;
            String jarString = file.getPath() + slashString + fileList[i];
            try {
              JarLoader.loadJar( jarString );
              //JarClassLoader jcl = new JarClassLoader("file:" + jarString );
              //jcl.loadRelevantClasses();
            }
            catch (Exception e1) {
                System.err.println ("Error loading jar: " + e1.getMessage ());
            }
        }
    }

    /**
     * file browser
     */
    private boolean getDir() {
        JFileChooser fChooser =
                new JFileChooser(  Cytoscape.getCytoscapeObj().getCurrentDirectory());
        fChooser.setDialogTitle("Load Plugin from Jar Directory");
        fChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        switch (fChooser.showOpenDialog(null)) {

            case JFileChooser.APPROVE_OPTION:
                file = fChooser.getSelectedFile();
                 Cytoscape.getCytoscapeObj().setCurrentDirectory(file);
                return true;
            default:
                // cancel or error
                return false;
        }
    }
} // JarPluginDirectoryAction


