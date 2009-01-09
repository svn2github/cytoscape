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

import java.util.*;
import java.io.*;

import giny.model.RootGraph;
import giny.view.*;

import cytoscape.view.NetworkView;
import cytoscape.CytoscapeObj;
import cytoscape.data.CyNetwork;
import cytoscape.data.Semantics;
//-------------------------------------------------------------------------
public class ListFromFileSelectionAction extends AbstractAction {
    NetworkView networkView;
    
    public  ListFromFileSelectionAction (NetworkView networkView) {
        super("From File...");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {
        boolean cancelSelectionAction = !selectFromFile();
    }
    
    private boolean selectFromFile() {
        CytoscapeObj cytoscapeObj = networkView.getCytoscapeObj();
        File currentDirectory = cytoscapeObj.getCurrentDirectory();
        JFileChooser fChooser = new JFileChooser(currentDirectory);     
        fChooser.setDialogTitle("Load Gene Selection File");
        switch (fChooser.showOpenDialog(networkView.getMainFrame())) {
                
        case JFileChooser.APPROVE_OPTION:
            File file = fChooser.getSelectedFile();
            currentDirectory = fChooser.getCurrentDirectory();
            cytoscapeObj.setCurrentDirectory(currentDirectory);
            
            CyNetwork network = networkView.getNetwork();
            String callerID = "ListFromFileSelectionAction.useSelectionFile";
            network.beginActivity(callerID);
            
            try {
                FileReader fin = new FileReader(file);
                BufferedReader bin = new BufferedReader(fin);
                List fileNodes = new ArrayList();
                String s;
                while ((s = bin.readLine()) != null) {
                    String trimName = s.trim();
                    if (trimName.length() > 0) {fileNodes.add(trimName);}
                }
                fin.close();

                // loop through all the node of the graph
                // selecting those in the file

                RootGraph graph = networkView.getNetwork().getRootGraph();
		List nodeList = graph.nodesList();
                giny.model.Node [] nodes = (giny.model.Node [])nodeList.toArray(new giny.model.Node[0]);
                for (int i=0; i < nodes.length; i++) {
                    giny.model.Node node = nodes[i];
                    boolean select = false;
                    String canonicalName =
                            network.getNodeAttributes().getCanonicalName(node);
                    List synonyms =
                            Semantics.getAllSynonyms(canonicalName, network, cytoscapeObj);
                    for (Iterator synI=synonyms.iterator(); synI.hasNext(); ) {
                        if ( fileNodes.contains( (String)synI.next() ) ) {
                            select = true;
                            break;
                        }
                    }
                    if (select) {
		    	GraphView view = networkView.getView();
		    	NodeView nv = view.getNodeView(node.getRootGraphIndex());
			nv.setSelected(true);
		    }
                }
                networkView.redrawGraph(false, true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.toString(),
                                 "Error Reading \"" + file.getName()+"\"",
                                               JOptionPane.ERROR_MESSAGE);
                network.endActivity(callerID);
                return false;
            }

            network.endActivity(callerID);
            return true;

        default:
            // cancel or error
            return false;
        }
    }
    
}

