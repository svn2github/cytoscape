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

import cytoscape.CytoscapeWindow;
import cytoscape.GraphObjAttributes;
import cytoscape.dialogs.EdgeControlDialog;
//-------------------------------------------------------------------------
public class EdgeManipulationAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public EdgeManipulationAction(CytoscapeWindow cytoscapeWindow) {
        super ("Select or hide by attributes...");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed (ActionEvent e) {
        GraphObjAttributes edgeAttributes = cytoscapeWindow.getEdgeAttributes();
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
            JDialog dialog = new EdgeControlDialog(cytoscapeWindow,
                                                   attributesTree, "Control Edges");
            dialog.pack();
            dialog.setLocationRelativeTo(cytoscapeWindow.getMainFrame());
            dialog.setVisible(true);
        }
        else {
            JOptionPane.showMessageDialog(null, 
            "There are no String edge attributes suitable for controlling edge display");
        }
    } // actionPerformed
}

