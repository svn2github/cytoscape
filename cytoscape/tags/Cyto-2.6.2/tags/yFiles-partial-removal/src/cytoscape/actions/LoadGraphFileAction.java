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
import cytoscape.actions.CheckBoxFileChooser;

//-------------------------------------------------------------------------
public class LoadGraphFileAction extends AbstractAction {
    NetworkView networkView;
    
    public LoadGraphFileAction(NetworkView networkView) {
        super("Graph...");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e)  {
        CytoscapeObj cytoscapeObj = networkView.getCytoscapeObj();
        File currentDirectory = networkView.getCytoscapeObj().getCurrentDirectory();
        //JFileChooser chooser = new JFileChooser(currentDirectory);
        JFileChooser chooser = new CheckBoxFileChooser(currentDirectory, "append graph? (not implemented yet)");
	boolean appendFlag = false;
        //chooser.setApproveButtonText("TEST TEXT");
        CyFileFilter intFilter   = new CyFileFilter();
        CyFileFilter gmlFilter   = new CyFileFilter();
        CyFileFilter graphFilter = new CyFileFilter();
	CyNetwork newNetwork;
        gmlFilter.addExtension("gml");
        gmlFilter.setDescription("GML files");
        intFilter.addExtension("sif");
        intFilter.setDescription("Interaction files");
        graphFilter.addExtension("sif");
        graphFilter.addExtension("gml");
        graphFilter.setDescription("All graph files");
        chooser.addChoosableFileFilter(graphFilter);
        chooser.addChoosableFileFilter(intFilter);
        chooser.addChoosableFileFilter(gmlFilter);
        chooser.setFileFilter(graphFilter);
        if (chooser.showOpenDialog(networkView.getMainFrame()) == chooser.APPROVE_OPTION) {
            currentDirectory = chooser.getCurrentDirectory();
	    
	    //String fileType = chooser.getDescription(chooser.getSelectedFile());
	    //System.out.println("FILETYPE: " + fileType);
            networkView.getCytoscapeObj().setCurrentDirectory(currentDirectory);
	    //appendFlag = chooser.getCheckBoxState();
	    //if(appendFlag) System.out.println("appending graph");

            String  name = chooser.getSelectedFile().toString();
            boolean canonicalize = Semantics.getCanonicalize(cytoscapeObj);
            String  species = Semantics.getDefaultSpecies( networkView.getNetwork(), cytoscapeObj );

	    if( name.endsWith("gml") || name.endsWith("GML") )
		newNetwork = CyNetworkFactory.createNetworkFromGMLFile( name );
	    else
		newNetwork =
		    CyNetworkFactory.createNetworkFromInteractionsFile( name, 
									canonicalize,
									cytoscapeObj.getBioDataServer(), 
									species );
            if (newNetwork != null) {//valid read
                //apply the semantics we usually expect
                Semantics.applyNamingServices(newNetwork, networkView.getCytoscapeObj());
                //since we don't want to erase the old attributes or expression data,
                //we copy the new network into the existing one, replacing the graph
                networkView.getNetwork().setNewGraphFrom(newNetwork, false);
                networkView.setWindowTitle(name);
             } else {//give the user an error dialog
                String lineSep = System.getProperty("line.separator");
                StringBuffer sb = new StringBuffer();
                sb.append("Could not read graph from file " + name + lineSep);
                sb.append("This file may not be a valid GML or SIF file." + lineSep);
                JOptionPane.showMessageDialog(networkView.getMainFrame(),
                                              sb.toString(),
                                              "Error loading graph",
                                              JOptionPane.ERROR_MESSAGE);
            }
        } // if
    } // actionPerformed
}

