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
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 * @version %I%, %G%
 * @since 2.0
 */
package metaNodeViewer;

import metaNodeViewer.data.*;
import java.util.*;
import giny.model.*;
import cytoscape.data.GraphObjAttributes;
import cytoscape.*;
import cern.colt.list.IntArrayList;

/**
 * Creates meta-nodes for a given GraphPerspective and keeps track of which meta-nodes belong
 * to which GraphPerspectives (since all GraphPerspectives share the same RootGraph).
 */

public class GPMetaNodeFactory implements MetaNodeFactory {
  
  protected static final boolean DEBUG = true;
  
  /**
   * Key: GraphPerspective Value: IntArrayList
   * The IntArrayList contains RootGraph indices of nodes that have been
   * created by this factory for the GraphPerspective used as the key
   */
  protected Map gpToMetaNodes;
  
  /**
   * Specifies whether or not this class should assign a name to newly created meta-nodes
   */
  protected boolean assignDefaultName;
  
  /**
   * The MetaNodeAttributesHandler that names newly created meta-nodes
   */
  protected MetaNodeAttributesHandler attributesHandler;

  /**
   * Constructor.
   * Calls <code>this(new SimpleMetaNodeAttributesHandler(), true)</code>.
   */
  public GPMetaNodeFactory (){
    this(new SimpleMetaNodeAttributesHandler(),true);
  }//GPMetaNodeFactory

  /**
   * Constructor.
   *
   * @param attributes_handler the MetaNodeAttributesHandler that should be 
   * used to name meta-nodes and add a name--Node mapping into GraphObjAttributes
   * for nodes, calls GPMetaNodeFactory(attributes_handler,true)
   */
  public GPMetaNodeFactory (MetaNodeAttributesHandler attributes_handler){
    this(attributes_handler,true);
  }//GPMetaNodeFactory
  
  /**
   * Constructor.
   *
   * @param attributes_handler the MetaNodeAttributesHandler that should be 
   * used to name meta-nodes and add a name--Node mapping into GraphObjAttributes
   * for nodes
   * @param assign_names whether or not this class should assign new names
   * to newly created nodes and set this names in the node object attributes
   */
  public GPMetaNodeFactory (MetaNodeAttributesHandler attributes_handler,
                            boolean assign_names){
    this.assignDefaultName = assign_names;
    this.attributesHandler = attributes_handler;
    this.gpToMetaNodes = new HashMap();
  }//GPMetaNodeFactory
  
  /**
   * Sets whether or not a default name for newly created meta-nodes should be given
   */
  public void assignDefaultNames (boolean assign){
    this.assignDefaultName = assign;
  }//assignDefaultNames
  
  /**
   * Whether or not default names are being assigned to newly created meta-nodes
   */
  public boolean getAssignDefaultNames (){
    return this.assignDefaultName;
  }//getAssignDefaultNames
  

  /**
   * Implements MetaNodeFactory.createMetaNode()
   * Creates a MetaNode in the GraphPercpective that lives within CyNetwork.
   * This means that a node in GraphPerspective's RootGraph needs to be created.
   * TODO: Since many GraphPerspectives share the same RootGraph, we need to keep track of
   * which meta-node in RootGraph belong to which GraphPerspective.
   */
  public int createMetaNode (CyNetwork cy_net, int [] children_node_indices){
    
    if(children_node_indices == null || 
       cy_net == null || 
       children_node_indices.length == 0){
      if(DEBUG){
        System.err.println("GPMetaNodeFactory.createMetaNode(CyNetwork=" + cy_net + 
                           ", children_node_indices=" + children_node_indices + 
                           ") : wrong input, returning 0");
      }
      return 0;
    }// check args
    
    GraphPerspective mainGP = cy_net.getGraphPerspective();
    
    // Make sure that the node indices are RootGraph indices
    for(int i = 0; i < children_node_indices.length; i++){
      if(children_node_indices[i] > 0){
        // This is a GP index
        int rgIndex = mainGP.getRootGraphNodeIndex(children_node_indices[i]);
        if(rgIndex >= 0){
          // Something went wrong!
          if(DEBUG){
            System.err.println("GPMetaNodeFactory.createMetaNode(CyNetwork,int[]): input node " +
                               children_node_indices[i] +
                               " has no RootGraph index, returning 0");
          }
          return 0;
        }// if rgIndex >= 0
        
        children_node_indices[i] = rgIndex;
      }// if children_node_indices[i] > 0
    }// for i
    
    // Get the RootGraph indices of the edges that connect to the selected nodes
    RootGraph rootGraph = mainGP.getRootGraph();
    int[] edgeIndices = mainGP.getConnectingEdgeIndicesArray(children_node_indices);
    for(int i = 0; i < edgeIndices.length; i++){
      if(edgeIndices[i] > 0){
        int rootEdgeIndex = mainGP.getRootGraphEdgeIndex(edgeIndices[i]);
        if(rootEdgeIndex >= 0){
          // Something went wrong!
          if(DEBUG){
            System.err.println("GPMetaNodeFactory.createMetaNode(CyNetwork,int[]): connecting edge " +
                               edgeIndices[i] +
                               " has no RootGraph index, returning 0");
          }
          return 0; 
        }// if rootEdgeIndex >= 0
        edgeIndices[i] = rootEdgeIndex;
      }// if rootEdgeIndex > 0
    }//for i
    
    // Create a node in RootGraph that contains inside it the selected nodes
    // and their connected edges
    int rgParentNodeIndex = rootGraph.createNode(children_node_indices, edgeIndices);

    // Remember that this RootGraph node belongs to the GraphPerspective in cyNetwork
    IntArrayList rootNodes = (IntArrayList)this.gpToMetaNodes.get(mainGP);
    if(rootNodes == null){
      rootNodes = new IntArrayList();
      this.gpToMetaNodes.put(mainGP,rootNodes);
    }
    rootNodes.add(rgParentNodeIndex);
    
    // Assign a default name if necessary
    if(getAssignDefaultNames()){
      if(this.attributesHandler.assignName(cy_net, rgParentNodeIndex) == null){
        // Failed to assign a default name, but the node has been created, so just
        // print a debug statement
        if(DEBUG){
          System.err.println("Alert! GPMetaNodeFactory.createMetaNode: No assigned name for new node");
        }
      }// if(!assignName)
    }// if(getAssignDefaultNames())
    
    return rgParentNodeIndex;
  }//createMetaNode

  /**
   * Creates a new name of the form MetaNode_<abs(root_node_index)> and adds an
   * object-name mapping in the node attributes contained in CyNetwork for the
   * given node.
   *
   * @return false if it failed to assign a name, or true if successful
   */
  //TODO: Move somewhere else
  protected boolean assignDefaultName (CyNetwork cy_net, int root_node_index){
    
    GraphPerspective mainGP = cy_net.getGraphPerspective();
    RootGraph rootGraph = mainGP.getRootGraph();
    Node node = rootGraph.getNode(root_node_index);
    
    if(node == null){
      if(DEBUG){
        System.err.println("GPMetaNodeFactory.assignDefaultName: failed because there is no Node " +
                           " in RootGraph with root index " + root_node_index);
        
      }
      return false;
    }// node == null
    
    String unique_name = "MetaNode_" + Integer.toString( (root_node_index*-1) );
    GraphObjAttributes nodeAtts = cy_net.getNodeAttributes();
    nodeAtts.addNameMapping(unique_name, node);
    nodeAtts.set("canonicalName", unique_name, unique_name);

    return true;
  }//assignDefaultName

  /**
   * Clears the Factory.
   */
  public void clear (){
    this.gpToMetaNodes.clear();
  }//clear

  /**
   * @return the RootGraph indices of the nodes that are parent nodes of nodes
   * in the given graph and that were created using this factory
   */
  public int [] getParentNodesInNet (GraphPerspective graphPerspective){
    IntArrayList parents = (IntArrayList)this.gpToMetaNodes.get(graphPerspective);
    if(parents == null){
      return new int[0];
    }
    parents.trimToSize();
    return parents.elements();
  }//getParentNodesInNet

}//GPMetaNodeFactory
