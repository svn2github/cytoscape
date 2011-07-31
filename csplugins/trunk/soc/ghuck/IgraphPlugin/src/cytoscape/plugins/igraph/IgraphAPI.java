/**************************************************************************************
Copyright (C) Gerardo Huck, 2011


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

**************************************************************************************/

package cytoscape.plugins.igraph;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.util.*;

import cytoscape.Cytoscape;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

import giny.model.*;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;


public class IgraphAPI {
	
    /**
     * This function loads the current graph into Igraph.
     *
     */    
    public static HashMap<Integer,Integer> loadGraph(boolean selectedOnly, boolean directed){
	    
	CyNetwork network = Cytoscape.getCurrentNetwork();
	
	// Create a reverse mapping
	int nodeCount;
	if(selectedOnly) {
	    nodeCount = network.getSelectedNodes().size();
	} else {
	    nodeCount = network.getNodeCount();
	}
	HashMap<Integer, Integer> nodeIdMapping = new HashMap<Integer, Integer>(nodeCount);
	int j = 0;
	Iterator<Node> nodeIt;
	if(selectedOnly) {
	    nodeIt = network.getSelectedNodes().iterator();
	} else {
	    nodeIt = network.nodesIterator();
	}
	
	while(nodeIt.hasNext()){            
	    Node node = nodeIt.next();
	    nodeIdMapping.put(node.getRootGraphIndex(), j);
	    j++;
	}
	
	
	// Write edges (as pairs of consecutive nodes) in edgeArray
	int[] edgeArray = new int[network.getEdgeCount() * 2];
	int i = 0;
	
	Iterator<Edge> it = network.edgesIterator();
	while (it.hasNext()) {
	    Edge e = (CyEdge) it.next();

	    Node source = e.getSource();
	    Node target = e.getTarget();

	    if(!selectedOnly || (network.isSelected(source) && network.isSelected(target)) ){
		edgeArray[i]     = nodeIdMapping.get(source.getRootGraphIndex());
		edgeArray[i + 1] = nodeIdMapping.get(target.getRootGraphIndex());
		i += 2;
	    }
	}

	int directedInt;
	if (directed)
	    directedInt = 1;
	else
	    directedInt = 0;

	IgraphInterface.createGraph(edgeArray, i, directedInt);

	return nodeIdMapping;
    } // loadGraph()
	
    public static double[] computeWeights(String attrName, 
					  HashMap<Integer,Integer> mapping, 
					  boolean selectedOnly) {
	
	CyNetwork network = Cytoscape.getCurrentNetwork();
	CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
	double[] weights = new double[network.getEdgeCount() * 2];		

	boolean defaultSet = false;
	double defaultValue = 0.0;

	int i = 0;	
	byte type = edgeAttributes.getType(attrName);
	Iterator<Edge> it = network.edgesIterator();

	if (type == CyAttributes.TYPE_INTEGER) { 

	    while (it.hasNext()) {
		Edge e = (CyEdge) it.next();

		Node source = e.getSource();
		Node target = e.getTarget();


		if (!selectedOnly || 
		    (network.isSelected(source) && network.isSelected(target)) ){

		    Integer a = edgeAttributes.getIntegerAttribute(e.getIdentifier(), attrName);
		    if (null == a) {
			if (defaultSet) {
			    weights[i] = defaultValue;
			} else {
			    while (true) {
				String inputValue = JOptionPane.showInputDialog("Found an edge without attribute.\nPlease enter a default value to be used for all edges without attribute " + attrName);
				try {
				    Double d = Double.parseDouble(inputValue);
				    defaultValue = d.doubleValue();
				    defaultSet = true;
				    break;
				} catch (Exception ex) {
				    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Please enter a valid number");	    
				}
			    }

			}

		    } else {
			weights[i] = a.doubleValue();
		    }
		    i++;

		}
	    } // while
	    
	} else { // (type == CyAttributes.TYPE_FLOATING) 
	    while (it.hasNext()) {
		Edge e = (CyEdge) it.next();

		Node source = e.getSource();
		Node target = e.getTarget();

		if (!selectedOnly || 
		    (network.isSelected(source) && network.isSelected(target)) ){

		    Double a = edgeAttributes.getDoubleAttribute(e.getIdentifier(), attrName);
		    if (null == a) {
			if (defaultSet) {
			    weights[i] = defaultValue;
			} else {
			    while (true) {
				String inputValue = JOptionPane.showInputDialog("Found an edge without attribute.\nPlease enter a default value to be used for all edges without attribute " + attrName);
				try {
				    Double d = Double.parseDouble(inputValue);
				    defaultValue = d.doubleValue();
				    defaultSet = true;
				    break;
				
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Please enter a valid number");	    
				}
			    }

			}
		    } else {
			weights[i] = a.doubleValue();
		    }
		    i++;
		}
	    } // while
	    	    
	} 

 	return weights;
    }
}