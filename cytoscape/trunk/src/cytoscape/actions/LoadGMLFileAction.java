//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;

import cytoscape.CytoscapeWindow;
import cytoscape.util.CyFileFilter;
//-------------------------------------------------------------------------
public class LoadGMLFileAction extends AbstractAction {
  CytoscapeWindow cytoscapeWindow;
  
  public LoadGMLFileAction (CytoscapeWindow cytoscapeWindow) {
    super("GML...");
    this.cytoscapeWindow = cytoscapeWindow;
  }
    
  public void actionPerformed (ActionEvent e)  {
    File currentDirectory = cytoscapeWindow.getCurrentDirectory();
    JFileChooser chooser = new JFileChooser (currentDirectory);
    CyFileFilter filter = new CyFileFilter();
    filter.addExtension("gml");
    filter.setDescription("GML files");
    chooser.setFileFilter(filter);
    chooser.addChoosableFileFilter(filter);
    if (chooser.showOpenDialog (cytoscapeWindow) == chooser.APPROVE_OPTION) {
      currentDirectory = chooser.getCurrentDirectory();
      cytoscapeWindow.setCurrentDirectory(currentDirectory);
      String name = chooser.getSelectedFile ().toString ();
      boolean couldRead = cytoscapeWindow.loadGML (name);
      if (!couldRead) {//give the user an error dialog
          String lineSep = System.getProperty("line.separator");
          StringBuffer sb = new StringBuffer();
          sb.append("Could not read graph from file " + name + lineSep);
          sb.append("This file may not be a valid GML file." + lineSep);
          JOptionPane.showMessageDialog(cytoscapeWindow.getMainFrame(),
                                        sb.toString(),
                                        "Error loading graph",
                                        JOptionPane.ERROR_MESSAGE);
      }
    } // if
  } // actionPerformed

}

