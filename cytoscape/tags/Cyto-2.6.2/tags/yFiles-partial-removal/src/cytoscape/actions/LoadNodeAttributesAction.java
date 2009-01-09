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

import cytoscape.CytoscapeObj;
import cytoscape.GraphObjAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
/* 
 * Added by T. Ideker April 16, 2003
 * to allow loading of node / edge attributes from the GUI
 */
public class LoadNodeAttributesAction extends AbstractAction {
    NetworkView networkView;
    
    public LoadNodeAttributesAction(NetworkView networkView) {
        super("Node Attributes...");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e)  {
        CytoscapeObj cytoscapeObj = networkView.getCytoscapeObj();
        File currentDirectory = cytoscapeObj.getCurrentDirectory();
        JFileChooser chooser = new JFileChooser(currentDirectory);
        String dialogTitle = "Load Node Attributes";
        chooser.setDialogTitle(dialogTitle);
        if (chooser.showOpenDialog (networkView.getMainFrame()) == chooser.APPROVE_OPTION) {
            currentDirectory = chooser.getCurrentDirectory();
            cytoscapeObj.setCurrentDirectory(currentDirectory);
            String attrFilename = chooser.getSelectedFile().toString();
            
            String callerID = "LoadNodeAttributesAction.actionPerformed";
            networkView.getNetwork().beginActivity(callerID);
            GraphObjAttributes nodeAttributes = networkView.getNetwork().getNodeAttributes();
            String species =
                Semantics.getDefaultSpecies(networkView.getNetwork(), networkView.getCytoscapeObj());
            boolean canonicalize = Semantics.getCanonicalize(cytoscapeObj);
            try {
                nodeAttributes.readAttributesFromFile(cytoscapeObj.getBioDataServer(),
                                                      species, attrFilename, canonicalize);
                // Added by iliana on May, 2003
                // We need to reapply appearances since this attribute could be 
                // mapped to a visual property
                networkView.redrawGraph(false, true);
                String lineSep = System.getProperty("line.separator");
                String okMessage = "Successfully read node attributes from file"
                        + lineSep + attrFilename;
                JOptionPane.showMessageDialog(networkView.getMainFrame(),
                                              okMessage, dialogTitle,
                                              JOptionPane.PLAIN_MESSAGE);
            } catch (Exception excp) {
                excp.printStackTrace();
                String lineSep = System.getProperty("line.separator");
                StringBuffer sb = new StringBuffer();
                sb.append("Exception when reading from node attributes file");
                sb.append(lineSep + attrFilename + lineSep);
                sb.append(excp.getMessage() + lineSep);
                JOptionPane.showMessageDialog(networkView.getMainFrame(),
                                              sb.toString(), dialogTitle,
                                              JOptionPane.ERROR_MESSAGE);
            }
                
            networkView.getNetwork().endActivity(callerID);
        } // if
    } // actionPerformed
}

