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
public class LoadGMLFileAction extends AbstractAction {
    NetworkView networkView;
    
    public LoadGMLFileAction(NetworkView networkView) {
        super("GML...");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e)  {
        CytoscapeObj cytoscapeObj = networkView.getCytoscapeObj();
        File currentDirectory = networkView.getCytoscapeObj().getCurrentDirectory();
        JFileChooser chooser = new JFileChooser(currentDirectory);
        CyFileFilter gmlFilter = new CyFileFilter();

        gmlFilter.addExtension("gml");
        gmlFilter.setDescription("GML files");
        chooser.addChoosableFileFilter(gmlFilter);
        chooser.setFileFilter(gmlFilter);
        if (chooser.showOpenDialog(networkView.getMainFrame()) == chooser.APPROVE_OPTION) {
            currentDirectory = chooser.getCurrentDirectory();
            networkView.getCytoscapeObj().setCurrentDirectory(currentDirectory);

            String  name = chooser.getSelectedFile().toString();
            boolean canonicalize = Semantics.getCanonicalize(cytoscapeObj);
            String  species = Semantics.getDefaultSpecies( networkView.getNetwork(), cytoscapeObj );
	    boolean isYFiles = networkView.getCytoscapeObj().getConfiguration().isYFiles();
	    
	    CyNetwork newNetwork = CyNetworkFactory.createNetworkFromGMLFile( name, isYFiles );

            if (newNetwork != null) {//valid read
                //apply the semantics we usually expect
                Semantics.applyNamingServices(newNetwork, networkView.getCytoscapeObj());
                //set the new graph, don't erase old attributes
		if ( isYFiles ) {
		    networkView.getNetwork().setNewGraphFrom(newNetwork, false);
		    networkView.setWindowTitle(name);
		}
		else {
		    networkView.setNewNetwork(newNetwork);
		    networkView.setWindowTitle(name);
		}
             } else {//give the user an error dialog
                String lineSep = System.getProperty("line.separator");
                StringBuffer sb = new StringBuffer();
                sb.append("Could not read graph from file " + name + lineSep);
                sb.append("This file may not be a valid GML file." + lineSep);
                JOptionPane.showMessageDialog(networkView.getMainFrame(),
                                              sb.toString(),
                                              "Error loading graph",
                                              JOptionPane.ERROR_MESSAGE);
            }
        } // if
    } // actionPerformed
}

