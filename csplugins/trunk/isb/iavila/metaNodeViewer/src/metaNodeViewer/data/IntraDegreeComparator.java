/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/

/**
 * A Comparator for CyNodes that compares the given nodes depending on their degrees.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 */
package metaNodeViewer.data;

import giny.model.RootGraph;
import java.util.Comparator;
import cern.colt.map.OpenIntIntHashMap;
import cytoscape.*;
import cytoscape.data.Semantics;
import java.util.*;

public class IntraDegreeComparator implements Comparator{
	
	protected static final boolean DEBUG = false;
	protected CyNetwork network;	
	protected OpenIntIntHashMap intraDegrees;
	protected boolean nodesAreInNetwork;
	
	/**
	 * Constructor.
	 * 
	 * @param cy_net the network where the nodes to be sorted reside
	 * @param nodes_in_network whether or not the nodes are in the network ?
	 * @param node_rindices the RootGraph indices of the nodes to be sorted
	 */
	public IntraDegreeComparator (CyNetwork cy_net, boolean nodes_in_network, int [] node_rindices){ // TODO: 2nd argument, needed?
		this.network = cy_net;
		this.intraDegrees = countIntraDegrees(this.network, node_rindices);
		this.nodesAreInNetwork = nodes_in_network;
	}//IntraDegreeComparator
	
	public int compare (Object object1, Object object2){
		
		if(object1 == object2){
			return 0;
		}
		
		CyNode node1, node2;
		String canonical1, canonical2;
		int rindex1, rindex2;
		RootGraph rootGraph = this.network.getRootGraph();
		if(object1 instanceof CyNode && object2 instanceof CyNode){
			node1 = (CyNode)object1;
			node2 = (CyNode)object2;
			canonical1 = (String)this.network.getNodeAttributeValue(node1,Semantics.CANONICAL_NAME);
			canonical2 = (String)this.network.getNodeAttributeValue(node2,Semantics.CANONICAL_NAME);
			rindex1 = rootGraph.getIndex(node1);
			rindex2 = rootGraph.getIndex(node2);
			if(rindex1 == rindex2){
				//System.out.println((CyNode)object1 + " and " + (CyNode)object2 + 
				//" are equal because they have the same RootGraph index");
				return 0;
			}
		}else{
			throw new IllegalStateException("The given objects are not instances of CyNode.");
			//return -11;
		}
		
		int degree1 = this.intraDegrees.get(rindex1);
		int degree2 = this.intraDegrees.get(rindex2);
		
		if(degree1 > degree2){
			return -1;
		}
		
		if(degree1 < degree2){
			return 1;
		}
		
		// The intra-degrees are the same.
		// First tie breaker: If one of these is a metabolite, 
		// and the other is a protein with a common name, then the protein wins.
		Object o1 = this.network.getNodeAttributeValue(node1,"nodeType");
		Object o2 = this.network.getNodeAttributeValue(node2,"nodeType");
		String node1MType = null;
		String node2MType = null;
		
		if(o1 != null && o2 != null){
			node1MType = (String)o1;
			node2MType = (String)o2;
			if(node1MType.equals("metabolite") && node2MType.equals("protein")){
				String commonName = null ;
				Object cn =  this.network.getNodeAttributeValue(node2,Semantics.COMMON_NAME);
				if(cn != null){
					commonName = (String)cn;
				}
				if( commonName != null && !commonName.equals(canonical2) ){
					// the second node is a protein and has a common name, so it wins
					return 1;
				}else{
					// the second node is a protein but does not have a common name, so it looses
					return -1;
				}
			}else if(node1MType.equals("protein") && node2MType.equals("metabolite")){
				String commonName = null;
				Object cn =  this.network.getNodeAttributeValue(node1,Semantics.COMMON_NAME);
				if(cn != null){
					commonName = (String)cn;
				}
				if( commonName != null && !commonName.equals(canonical1) ){
					// the first node is a protein and has a common name, so it wins
					return -1;
				}else{
					// the first node is a protein but does not have a common name, so it looses
					return 1;
				}
			}
		}// First tie breaker
		
		// From this point on we know that it is not the case that one of the nodes is a 
		// protein and the other one is a metabolite.
		
		// Third tie breaker: which has highest overall degree
		
		if(this.nodesAreInNetwork){
			if(this.network.getDegree(node1) > this.network.getDegree(node2)){
				return -1;
			}
			
			if(this.network.getDegree(node1) < this.network.getDegree(node2)){
				return 1;
			} 
		}else{
			if(rootGraph.getDegree(node1) > rootGraph.getDegree(node2)){
				return -1;
			}
			
			if(rootGraph.getDegree(node1) < rootGraph.getDegree(node2)){
				return 1;
			}
			
		}// End of overall degree test
		
		// The overall degrees are the same
		// Fourth tie breaker: which one has a common name (it is not the case that 
		// one is a protein and the other one a metabolite)
		String commonName1 = 
			(String)this.network.getNodeAttributeValue(node1, Semantics.COMMON_NAME);
		String commonName2 = 
			(String)this.network.getNodeAttributeValue(node2, Semantics.COMMON_NAME);
		
		boolean firstIsCommonName = (commonName1 != null && !commonName1.equals(canonical1));
		boolean secondIsCommonName = (commonName2 != null && !commonName2.equals(canonical2));
		
		if(firstIsCommonName && !secondIsCommonName){
			return -1;
		}else if(!firstIsCommonName && secondIsCommonName){
			return 1;
		}else{
			// Either neither has a common name, or they both have common names.
			// return the one that is lexicographically first
			if(commonName1 == null){
				commonName1 = canonical1;
			}
			if(commonName2 == null){
				commonName2 = canonical2;
			}
			int compareValue = commonName1.compareTo(commonName2);
			
			return compareValue;
		}
	}//compare
	
	/**
	 * For each node in the array it counts how many edges it has to other nodes within the array, 
	 * and puts it in a map where the key is the node's <code>RootGraph</code> index, and the
	 * value is its intra-degree.
	 *
	 * @return a <code>cern.colt.map.OpenIntIntHashMap</code>, keys are <code>RootGraph</code>
	 * indices, and values are intra-degrees
	 */
	protected static OpenIntIntHashMap countIntraDegrees (CyNetwork network, int [] node_rindices){
		if(DEBUG){
			System.err.println("---------- countIntraDegrees(network,"
					+ node_rindices +") ----------");
		}
		
		OpenIntIntHashMap nodeToDegree = new OpenIntIntHashMap();
		
		// We have two situations:
		// 1. The nodes are in network
		// 2. The nodes are not in the network, but they are in RootGraph
		// We need to take two different approaches. If the nodes are in network, then
		// count the edges between them that are in network. If the nodes are not in network
		// then count the edges between them that are in RootGraph.
		boolean nodesAreInNetwork = nodesAreInNetwork(network,node_rindices);
		int [] connectingEdges = null; 
		if(nodesAreInNetwork){
			connectingEdges = network.getConnectingEdgeIndicesArray(node_rindices);// left here!!!!!!!
		}else{
			// TODO: Take care of this later, RootGraph does not have getConnectingEdgeIndicesArray
			// method (talk to Rowan???)
			throw new IllegalStateException ("The nodes are not in network, not implemented solution.");
		}
		if(connectingEdges == null || connectingEdges.length == 0){
			// No edges between the nodes
			if(DEBUG){
				System.err.println("The nodes have no edges between them.");
			}
			return nodeToDegree;
		}
		
		for(int i = 0; i < connectingEdges.length; i++){
			int edgeIndex = connectingEdges[i];
			boolean directed = network.isEdgeDirected(edgeIndex);
			int sourceIndex = network.getEdgeSourceIndex(edgeIndex);
			int targetIndex = network.getEdgeTargetIndex(edgeIndex);
			sourceIndex = network.getRootGraphNodeIndex(sourceIndex);
			targetIndex = network.getRootGraphNodeIndex(targetIndex);
			//if(DEBUG){
			//System.err.println("sourceIndex = " + sourceIndex + " targetIndex = " + targetIndex);
			//}
			int degree = nodeToDegree.get(sourceIndex);
			nodeToDegree.put(sourceIndex,degree+1);
			degree = nodeToDegree.get(targetIndex);
			nodeToDegree.put(targetIndex,degree+1);
		}//for i
		//if(DEBUG){
		//for(int i = 0; i < nodes.length; i++){
		//  int rindex = network.getRootGraphNodeIndex(nodes[i]);
		//  int degree = nodeToDegree.get(rindex);
		//  System.err.println("rindex = " + rindex + " degree = " + degree);
		//}
		//}
		return nodeToDegree;
	}//countIntraDegrees
	
	/**
	 * @returns true if *all* of the nodes in the array are contained in the network,
	 * false otherwise
	 */
	protected static boolean nodesAreInNetwork (CyNetwork network, int [] nodes){
		for(int i = 0; i < nodes.length; i++){
			if(nodes[i] < 0){
				if(network.getNodeIndex(nodes[i]) == 0){
					return false;
				}
			}else if(nodes[i] > network.getNodeCount()){
				return false;
			}else if (nodes[i] == 0){
				return false;
			}
		}//for i
		
		return true;
	}//nodesAreInNetwork
	
	/**
	 * Given a CyNode, it returns its intradegree.
	 * 
	 * @param node_rindex the RootGraph index of the node
	 * @return the intradegree of the given node
	 */
	public int getIntraDegree (int node_rindex){
		return this.intraDegrees.get(node_rindex);
	}//getIntraDegree
	
	/**
	 * Sorts the nodes with the given RootGraph indices according to their intradegree.
	 * 
	 * @param cy_net the network where the nodes to be sorted reside
	 * @param nodes_to_be_sorted the RootGraph indices of the nodes to be sorted
	 * @return the sorted CyNodes that correspond to the RootGraph indices
	 */
	public static SortedSet sortNodes (CyNetwork cy_net, int [] nodes_to_be_sorted){
		IntraDegreeComparator icomparator = new IntraDegreeComparator(cy_net, true, nodes_to_be_sorted);
		SortedSet sortedNodes = new TreeSet(icomparator);
	    RootGraph rootGraph = cy_net.getRootGraph();
	    for(int i = 0; i < nodes_to_be_sorted.length; i++){
	    	CyNode node = (CyNode)cy_net.getNode(nodes_to_be_sorted[i]);
	    	sortedNodes.add(node);
	    	cy_net.setNodeAttributeValue(node,
	                                    "intra degree", 
	                                    new Integer(icomparator.getIntraDegree(nodes_to_be_sorted[i]))); 
	    }//for i
	    return sortedNodes;
	}//sortNodes
	
}//internal class IntraDegreeComparator