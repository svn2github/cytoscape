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
import java.io.File;

import cytoscape.CytoscapeWindow;
import cytoscape.data.readers.FileReadingAbstractions;
//-------------------------------------------------------------------------
/* 
 * Added by T. Ideker April 16, 2003
 * to allow loading of node / edge attributes from the GUI
 */

public class LoadNodeAttributesAction extends AbstractAction {
  CytoscapeWindow cytoscapeWindow;
  
  public LoadNodeAttributesAction(CytoscapeWindow cytoscapeWindow) {
    super("Node Attributes...");
    this.cytoscapeWindow = cytoscapeWindow;
  }
    
  public void actionPerformed (ActionEvent e)  {
    File currentDirectory = cytoscapeWindow.getCurrentDirectory();
    JFileChooser chooser = new JFileChooser (currentDirectory);
    chooser.setDialogTitle("Load Node Attributes");
    if (chooser.showOpenDialog (cytoscapeWindow) == chooser.APPROVE_OPTION) {
      currentDirectory = chooser.getCurrentDirectory();
      cytoscapeWindow.setCurrentDirectory(currentDirectory);
      String [] attrFileNames = new String [1]; 
      attrFileNames[0] = chooser.getSelectedFile ().toString ();
      FileReadingAbstractions.readAttribs(cytoscapeWindow.getBioDataServer(), 
                                          cytoscapeWindow.getDefaultSpecies(), 
                                          cytoscapeWindow.getGraph(), 
                                          cytoscapeWindow.getNodeAttributes(), 
                                          null, 
                                          attrFileNames, 
                                          null,
                                          cytoscapeWindow.getConfiguration().getCanonicalize());
      // Added by iliana on May, 2003
      // We need to reapply appearances since this attribute could be 
      // mapped to a visual property
      cytoscapeWindow.redrawGraph(false, true);
    } // if
  } // actionPerformed
}

