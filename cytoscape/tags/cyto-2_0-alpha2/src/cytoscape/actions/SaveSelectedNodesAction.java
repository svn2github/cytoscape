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
import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class SaveSelectedNodesAction extends AbstractAction {
    NetworkView networkView;
    
    public SaveSelectedNodesAction (NetworkView networkView) {
        super("Selected Nodes as List...");
        this.networkView = networkView;
    }

    public void actionPerformed(ActionEvent e) {
        File currentDirectory = networkView.getCytoscapeObj().getCurrentDirectory();
	JFileChooser chooser = new JFileChooser(currentDirectory);
	if (chooser.showSaveDialog(networkView.getMainFrame()) == chooser.APPROVE_OPTION) {
	    String name = chooser.getSelectedFile().toString();
	    currentDirectory = chooser.getCurrentDirectory();
            networkView.getCytoscapeObj().setCurrentDirectory(currentDirectory);
	    boolean itWorked =
                CyNetworkUtilities.saveSelectedNodeNames(networkView, networkView.getNetwork(),name);
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

