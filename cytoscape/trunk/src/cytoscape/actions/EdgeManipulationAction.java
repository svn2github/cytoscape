//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.util.HashMap;

import cytoscape.view.NetworkView;
import cytoscape.GraphObjAttributes;
//import cytoscape.dialogs.EdgeControlDialog;
import cytoscape.dialogs.GinyEdgeControlDialog;
//-------------------------------------------------------------------------
public class EdgeManipulationAction extends AbstractAction {
    NetworkView networkView;
    
    public EdgeManipulationAction(NetworkView networkView) {
        super ("Select or hide by attributes...");
        this.networkView = networkView;
    }
    
    public void actionPerformed (ActionEvent e) {
        GraphObjAttributes edgeAttributes = networkView.getNetwork().getEdgeAttributes();
        String[] edgeAttributeNames = edgeAttributes.getAttributeNames();
        HashMap attributesTree = new HashMap();
        for (int i=0; i < edgeAttributeNames.length; i++) {
            String name = edgeAttributeNames[i];
            if (edgeAttributes.getClass(name) == "string".getClass()) {
                String[] uniqueNames = edgeAttributes.getUniqueStringValues(name);
                attributesTree.put(name, uniqueNames);
            } // if a string attribute
        } // for i
        if (attributesTree.size() > 0) {
            JDialog dialog = new GinyEdgeControlDialog(networkView,
                                                       attributesTree, "Control Edges");
            dialog.pack();
            dialog.setLocationRelativeTo(networkView.getMainFrame());
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, 
            "There are no String edge attributes suitable for controlling edge display");
        }
    } // actionPerformed
}

