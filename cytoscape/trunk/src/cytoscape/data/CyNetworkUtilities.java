/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.data;
//-------------------------------------------------------------------------
import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;

import y.base.*;
import y.view.Graph2D;

import giny.model.RootGraph;

import cytoscape.CytoscapeObj;
import cytoscape.GraphObjAttributes;
import cytoscape.data.servers.BioDataServer;
import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
/**
 * This class provides static methods that operate on a CyNetwork to perform
 * various useful tasks. Many of these methods make assumptions about the
 * data types that are available in the node and edge attributes of the network.
 */
public class CyNetworkUtilities {
//-------------------------------------------------------------------------   
/**
 * Saves all selected nodes in the network to a file with the given name.
 */
public static boolean saveSelectedNodeNames(NetworkView networkView, CyNetwork network, String filename) {
    if (network == null || filename == null) {return false;}
    
    
    
    if ( networkView.getCytoscapeObj().getConfiguration().isYFiles()) {
	    String callerID = "CyNetworkUtilities.saveSelectedNodeNames";
	    network.beginActivity(callerID);
	    Graph2D theGraph = network.getGraph();
	    Node[] nodes = theGraph.getNodeArray();
	    GraphObjAttributes nodeAttributes = network.getNodeAttributes();
	    File file = new File(filename);
	    try {
		    FileWriter fout = new FileWriter(file);
		    for (int i=0; i < nodes.length; i++) {
			    Node node = nodes[i];
			    if(theGraph.isSelected(node)) {
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
    }
    else { // for giny:
	    String callerID = "CyNetworkUtilities.saveSelectedNodeNames";
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
    }// end of else for giny
	    
} // saveSelectedNodeNames
//-------------------------------------------------------------------------
public static boolean saveVisibleNodeNames(CyNetwork network, String filename, boolean isYFiles) {
    if (network == null || filename == null) {return false;}
    if ( isYFiles ) {
	     String callerID = "CyNetworkUtilities.saveVisibleNodeNames";
	    network.beginActivity(callerID);
	    
	    Graph2D theGraph = network.getGraph();
	    Node [] nodes = theGraph.getNodeArray();
	    GraphObjAttributes nodeAttributes = network.getNodeAttributes();
	    File file = new File(filename);
	    try {
		FileWriter fout = new FileWriter(file);
		for (int i=0; i < nodes.length; i++) {
		    Node node = nodes[i];
		    String canonicalName = nodeAttributes.getCanonicalName(node);
		    fout.write(canonicalName + "\n");
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
    }
    else {
	    String callerID = "CyNetworkUtilities.saveVisibleNodeNames";
	    network.beginActivity(callerID);
	    
	    RootGraph theGraph = network.getRootGraph();
	    List nodelist = theGraph.nodesList();
	    GraphObjAttributes nodeAttributes = network.getNodeAttributes();
	    giny.model.Node[] nodes = (giny.model.Node[])nodelist.toArray(new giny.model.Node[0]);
	    File file = new File(filename);
	    try {
		FileWriter fout = new FileWriter(file);
		for (int i=0; i < nodes.length; i++) {
		    giny.model.Node node = nodes[i];
		    String canonicalName = nodeAttributes.getCanonicalName(node);
		    fout.write(canonicalName + "\n");
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
	   
    }
	    
}
//-------------------------------------------------------------------------
public static void selectNodesStartingWith(CyNetwork network, String key,
                                           CytoscapeObj cytoscapeObj, NetworkView networkView) {
    if (network == null || key == null) {return;}
    key = key.toLowerCase();
    
    String callerID = "CyNetworkUtilities.selectNodesStartingWith";
    network.beginActivity(callerID);
    if (cytoscapeObj.getConfiguration().isYFiles()) {
	    Graph2D theGraph = network.getGraph();
	    Node[] nodes = theGraph.getNodeArray();
	    GraphObjAttributes nodeAttributes = network.getNodeAttributes();
	    
	    for(int i = 0; i < nodes.length; i++){
		String nodeLabel = theGraph.getLabelText(nodes[i]);
		String canonicalName = nodeAttributes.getCanonicalName(nodes[i]);
		boolean matched = false;
		if (nodeLabel != null && nodeLabel.toLowerCase().startsWith(key)) {
		    matched = true;
		} else {
		    List synonyms = Semantics.getAllSynonyms(canonicalName, network, cytoscapeObj);
		    for (Iterator synI = synonyms.iterator(); synI.hasNext(); ) {
			String synonym = (String)synI.next();
			if ( synonym.toLowerCase().startsWith(key) ) {
			    matched = true;
			    break;
			}
		    }
		}
		theGraph.setSelected(nodes[i], matched);
	    } // for i
    }
    else { //for giny
	    
	    RootGraph theGraph = network.getRootGraph();
	    List nodelist = theGraph.nodesList();
	    GraphObjAttributes nodeAttributes = network.getNodeAttributes();
	    giny.model.Node[] nodes = (giny.model.Node[])nodelist.toArray(new giny.model.Node[0]);
	    for(int i = 0; i < nodes.length; i++){
		String nodeLabel = nodes[i].getIdentifier();
		String canonicalName = nodeAttributes.getCanonicalName(nodes[i]);
		boolean matched = false;
		if (nodeLabel != null && nodeLabel.toLowerCase().startsWith(key)) {
		    matched = true;
		} else {
		    List synonyms = Semantics.getAllSynonyms(canonicalName, network, cytoscapeObj);
		    for (Iterator synI = synonyms.iterator(); synI.hasNext(); ) {
			String synonym = (String)synI.next();
			if ( synonym.toLowerCase().startsWith(key) ) {
			    matched = true;
			    break;
			}
		    }
		}
		//theGraph.setSelected(nodes[i], matched);
		networkView.getView().getNodeView(nodes[i]).setSelected(matched);
	    } // for i
    }
    network.endActivity(callerID);
}
//-------------------------------------------------------------------------
}

