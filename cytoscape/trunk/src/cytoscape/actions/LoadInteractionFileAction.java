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
import cytoscape.data.CyNetwork;
import cytoscape.data.CyNetworkFactory;
import cytoscape.data.Semantics;
import cytoscape.view.NetworkView;
import cytoscape.util.CyFileFilter;
//-------------------------------------------------------------------------
public class LoadInteractionFileAction extends AbstractAction {
    NetworkView networkView;
    
    public LoadInteractionFileAction(NetworkView networkView) {
        super("Interaction...");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e)  {
        CytoscapeObj cytoscapeObj = networkView.getCytoscapeObj();
        File currentDirectory = cytoscapeObj.getCurrentDirectory();
        JFileChooser chooser = new JFileChooser(currentDirectory);
        CyFileFilter filter = new CyFileFilter();
        filter.addExtension("sif");
        filter.setDescription("Interaction files");
        chooser.setFileFilter(filter);
        chooser.addChoosableFileFilter(filter);
        if (chooser.showOpenDialog(networkView.getMainFrame()) == chooser.APPROVE_OPTION) {
            currentDirectory = chooser.getCurrentDirectory();
            cytoscapeObj.setCurrentDirectory(currentDirectory);
            String name = chooser.getSelectedFile().toString();
            boolean canonicalize = Semantics.getCanonicalize(cytoscapeObj);
            String species = Semantics.getDefaultSpecies(networkView.getNetwork(),
                                                         cytoscapeObj );
            CyNetwork newNetwork =
                CyNetworkFactory.createNetworkFromInteractionsFile(name, canonicalize,
                            cytoscapeObj.getBioDataServer(), species);
            if (newNetwork != null) {//valid read
                //apply the semantics we usualy expect
                Semantics.applyNamingServices(newNetwork, cytoscapeObj);
                //set the new graph, don't erase old attributes
                networkView.getNetwork().setNewGraphFrom(newNetwork, false);
            } else {//give the user an error dialog
                String lineSep = System.getProperty("line.separator");
                StringBuffer sb = new StringBuffer();
                sb.append("Could not read graph from file " + name + lineSep);
                JOptionPane.showMessageDialog(networkView.getMainFrame(),
                                              sb.toString(),
                                              "Error loading graph",
                                              JOptionPane.ERROR_MESSAGE);
            }
        } // if
    } // actionPerformed
    
}

