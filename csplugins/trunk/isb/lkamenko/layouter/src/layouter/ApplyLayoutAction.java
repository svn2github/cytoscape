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

import cytoscape.data.*;
import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class ApplyLayoutAction extends AbstractAction {
    NetworkView networkView;
    
    public ApplyLayoutAction (NetworkView networkView) {
        super("Apply Custom Layout");
        this.networkView = networkView;
    }

    public void actionPerformed(ActionEvent e) {
        File currentDirectory = networkView.getCytoscapeObj().getCurrentDirectory();
	JFileChooser chooser = new JFileChooser(currentDirectory);
	/*if (chooser.showOpenDialog(Dialog(networkView.getMainFrame()) == chooser.APPROVE_OPTION)) {
	    String name = chooser.getSelectedFile().toString();
	    //currentDirectory = chooser.getCurrentDirectory();
            //networkView.getCytoscapeObj().setCurrentDirectory(currentDirectory);
	    boolean itWorked =
                applyCustomLayout(networkView, networkView.getNetwork(), name);
	    Object[] options = {"OK"};
	    if(itWorked) {
		JOptionPane.showOptionDialog(null,
					 "Selected Nodes Saved.",
					 "Selected Nodes Saved.",
					 JOptionPane.DEFAULT_OPTION,
					 JOptionPane.PLAIN_MESSAGE,
					 null, options, options[0]);
	    }
	} */
    }
    /**
    */
    public boolean applyCustomLayout(NetworkView networkView, CyNetwork network, String filename) {
	    
	    
    if (network == null || filename == null) {return false;}
     // for giny:
	    String callerID = "ApplyLayoutAction.applyCustomLayout";
	    network.beginActivity(callerID);
	    
	   return true;
	    
} // applyCustomLayout
}

