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
//-------------------------------------------------------------------------
public class SaveSelectedNodesAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public SaveSelectedNodesAction (CytoscapeWindow cytoscapeWindow) {
        super("Selected Nodes");
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public void actionPerformed(ActionEvent e) {
        File currentDirectory = cytoscapeWindow.getCurrentDirectory();
	JFileChooser chooser = new JFileChooser (currentDirectory);
	if (chooser.showSaveDialog(cytoscapeWindow) == chooser.APPROVE_OPTION) {
	    String name = chooser.getSelectedFile().toString();
	    currentDirectory = chooser.getCurrentDirectory();
            cytoscapeWindow.setCurrentDirectory(currentDirectory);
	    boolean itWorked = cytoscapeWindow.saveSelectedNodeNames(name);
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

