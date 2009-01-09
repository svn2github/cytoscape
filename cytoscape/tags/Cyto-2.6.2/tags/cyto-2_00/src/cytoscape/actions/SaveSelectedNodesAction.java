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

import cytoscape.data.CyNetworkUtilities;
import cytoscape.view.CyNetworkView;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;


public class SaveSelectedNodesAction extends CytoscapeAction {
    
  public SaveSelectedNodesAction () {
    super("Selected Nodes as List...");
    setPreferredMenu( "File.Save" );
  }

  public void actionPerformed ( ActionEvent e ) {
    File currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
    JFileChooser chooser = new JFileChooser( currentDirectory );
    if (chooser.showSaveDialog( Cytoscape.getDesktop() ) == chooser.APPROVE_OPTION) {
	    String name = chooser.getSelectedFile().toString();
	    currentDirectory = chooser.getCurrentDirectory();
      Cytoscape.getCytoscapeObj().setCurrentDirectory(currentDirectory);
	    boolean itWorked =
        CyNetworkUtilities.saveSelectedNodeNames( Cytoscape.getCurrentNetworkView(),
                                                  Cytoscape.getCurrentNetwork(),
                                                  name);
	    Object[] options = {"OK"};
	    if(itWorked) {
        JOptionPane.showOptionDialog(null,
                                     "Selected Nodes Saved.",
                                     "Selected Nodes Saved.",
                                     JOptionPane.DEFAULT_OPTION,
                                     JOptionPane.PLAIN_MESSAGE,
                                     null, options, options[0]);
	    }
    }
  }
}

