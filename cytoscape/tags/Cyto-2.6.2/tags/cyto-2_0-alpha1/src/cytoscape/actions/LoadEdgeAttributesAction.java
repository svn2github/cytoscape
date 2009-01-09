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

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
/* 
 * Added by T. Ideker April 16, 2003
 * to allow loading of node / edge attributes from the GUI
 */

 public class LoadEdgeAttributesAction extends AbstractAction {
     NetworkView networkView;
     
     public LoadEdgeAttributesAction(NetworkView networkView) {
         super("Edge Attributes...");
         this.networkView = networkView;
     }
    
    public void actionPerformed(ActionEvent e)  {
        File currentDirectory = networkView.getCytoscapeObj().getCurrentDirectory();
        JFileChooser chooser = new JFileChooser(currentDirectory);
        String dialogTitle = "Load Edge Attributes";
        chooser.setDialogTitle(dialogTitle);
        if (chooser.showOpenDialog(networkView.getMainFrame()) == chooser.APPROVE_OPTION) {
            currentDirectory = chooser.getCurrentDirectory();
            networkView.getCytoscapeObj().setCurrentDirectory(currentDirectory);
            String attrFilename = chooser.getSelectedFile().toString();
            String callerID = "LoadEdgeAttributesAction.actionPerformed";
            networkView.getNetwork().beginActivity(callerID);
            try {
                networkView.getNetwork().getEdgeAttributes().readAttributesFromFile(attrFilename);
                String lineSep = System.getProperty("line.separator");
                String okMessage = "Successfully read edge attributes from file"
                                   + lineSep + attrFilename;
                JOptionPane.showMessageDialog(networkView.getMainFrame(),
                                              okMessage, dialogTitle,
                                              JOptionPane.PLAIN_MESSAGE);
            } catch (Exception excp) {
                excp.printStackTrace();
                String lineSep = System.getProperty("line.separator");
                StringBuffer sb = new StringBuffer();
                sb.append("Exception when reading from edge attributes file");
                sb.append(lineSep + attrFilename + lineSep);
                sb.append(excp.getMessage() + lineSep);
                JOptionPane.showMessageDialog(networkView.getMainFrame(),
                                              sb.toString(), dialogTitle,
                                              JOptionPane.ERROR_MESSAGE);
            }
            // Added by iliana on May, 2003
            // We need to reapply appearances since this attribute could be 
            // mapped to a visual property
            networkView.redrawGraph(false, true);
            networkView.getNetwork().endActivity(callerID);
        } // if
    } // actionPerformed
 }

