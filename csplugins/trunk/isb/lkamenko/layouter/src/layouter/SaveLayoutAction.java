//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package csplugins.layouter;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.*;
import java.util.*;

import giny.model.RootGraph;
import cytoscape.data.*;
import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class SaveLayoutAction extends AbstractAction {
    NetworkView networkView;
    
    public SaveLayoutAction (NetworkView networkView) {
        super("Save current Layout");
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
                saveCurrentLayout(networkView, networkView.getNetwork(),name);
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
    
    /**
 
 */
public boolean saveCurrentLayout(NetworkView networkView, CyNetwork network, String filename) {
    if (network == null || filename == null) {return false;}
    
	    String callerID = "SaveLayoutAction.saveCurrentLayout";
	    network.beginActivity(callerID);
	    RootGraph theGraph = network.getRootGraph();
	    List nodelist = theGraph.nodesList();
	    GraphObjAttributes nodeAttributes = network.getNodeAttributes();
	    giny.model.Node[] nodes = (giny.model.Node[])nodelist.toArray(new giny.model.Node[0]);
	    File file = new File(filename);
	    try {
		    FileWriter fout = new FileWriter(file);
		    for (int i=0; i < nodes.length; i++) {
			    giny.model.Node node = (giny.model.Node)nodes[i];
			    if(networkView.getView().getNodeView(node).isSelected()) {
				    String canonicalName = nodeAttributes.getCanonicalName(node);
				    fout.write(canonicalName + "\n");
			    }
		    } // for i
		    fout.close();
		    network.endActivity(callerID);
		    return true;
	    }  catch (IOException e) {
		    JOptionPane.showMessageDialog(null, e.toString(),
		    "Error Writing to \"" + file.getName()+"\"",
		    JOptionPane.ERROR_MESSAGE);
		    network.endActivity(callerID);
		    return false;
	    }
    
	} // saveCurrentLayout
}

