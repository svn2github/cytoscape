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

//TODO: Implement removeFromAttributes()
//TODO: Imeplemnt removeMetaEdgesFromAttributes()

package org.isb.metanodes.data;

import java.util.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import cern.colt.map.*;
import giny.view.*;

/**
 * Specialized version of SimpleMetaNodeAttributesHandler.<br>
 * 
 * 1. nodeLabel = member with highest intra-connections in the meta-node<br>
 * 2. Meta-node Area = proportional to number of children<br>
 * 3. Edges = same as member edges<br>
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version 1.0
 */
public class AbstractMetaNodeAttsHandler extends SimpleMetaNodeAttributesHandler {
  public static final boolean DEBUG = false;
  
  /**
   * Constructor.
   */
  public AbstractMetaNodeAttsHandler (){}//AbstractMetaNodeAttsHandler
  
  /**
   * Assigns a canonical name and a common name to the given node. The common name
   * is the same as the child node with highest number of connections to other
   * children. The canonical name is "MetaNode_<node root index>".
   *
   * @param cy_net the CyNetwork the CyNetwork that is being modified for meta-node representation
   * @param node the meta-node to be named
   * @return the name, or null if something went wrong
   */
  public String assignName (CyNetwork cy_net, CyNode node){
    // Check arguments
    if(cy_net == null || node == null){
      return null;
    }
    int metaNodeRindex = node.getRootGraphIndex();
    String uniqueName = createMetaNodeUI(metaNodeRindex);
    String alias = createMetaNodeAlias(cy_net,metaNodeRindex);
    node.setIdentifier(uniqueName);
    Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), getNodeLabelAttribute(), alias);
    
    if(DEBUG){
      System.err.println("meta-node " + metaNodeRindex + " canonical name = " + uniqueName 
                        + " common name = " + alias);
    }
    return uniqueName;
  }//assignName

  /**
   * @return a String with the form "MetaNode_(metanode_root_index)>".
   */
  protected String createMetaNodeUI (int metanode_root_index){
    return "MetaNode_" + Integer.toString(metanode_root_index);
  }//createMetaNodeUI

  /**
   * @return the alias (common-name) for the given meta-node
   */
  protected String createMetaNodeAlias (CyNetwork cy_net, int metanode_root_index){
  	int [] children = cy_net.getRootGraph().getNodeMetaChildIndicesArray(metanode_root_index);
  	if(children == null || children.length == 0){
  		throw new IllegalArgumentException("Node with index [" + metanode_root_index + "] has no children");
  	}
    SortedSet sortedNodes = IntraDegreeComparator.sortNodes(cy_net, children);
    CyNode highestNode = (CyNode)sortedNodes.first();
    return highestNode.getIdentifier();
  }//createMetaNodeAlias
  
  /**
   * Sets the node and edge attributes of the given meta-node and assigns a unique name to it
   *
   * @param cy_network the CyNetwork that is being modified to represent the meta-node
   * @param node the meta-node
   * @param children an array of CyNodes that are the children nodes of the meta-node
   * @param meta_edge_to_child_edge maps a meta-edge (edge connected to the meta-node)
   * to a child edge (edge that connects a child of the meta-node to another
   * node), so that classes implementing this interface know which child edge
   * corresponds to which meta-edge
   * @return true if all went well, false if there was an error
   */
  public boolean setAttributes (CyNetwork cy_network, CyNode node,
                                ArrayList children,
                                AbstractIntIntMap meta_edge_to_child_edge){
    boolean nodesOk = setNodeAttributes(cy_network,node,children);
    boolean edgesOk = setEdgeAttributes(cy_network,node,meta_edge_to_child_edge);
    return nodesOk && edgesOk;
  }//setAttributes
  
  /**
   * Sets the node attributes of the meta-node
   *
   * @param cy_network the CyNetwork that is being modified for meta-node representation
   * @param node the meta-node
   * @param children an array of CyNodes that are the children nodes of the meta-node
   * @return true if all went well, false if there was an error
   */
  public boolean setNodeAttributes (CyNetwork cy_network, CyNode node, ArrayList children){
    
    if(children == null || cy_network == null){
      return false;
    }
    CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
    // Set the 'nodeType' attribute to 'metaNode'
    nodeAtts.setAttribute(node.getIdentifier(),"nodeType", "metaNode");
    
    // Set the node-height and node-width attributes so that the area
    // of the meta-node is proportional to the number of members within it
    double area = 0;
    CyNetworkView netView = Cytoscape.getNetworkView(cy_network.getIdentifier());
    if(netView == null){
      if(DEBUG){
        System.err.println("AbstractMetaNodeAttsHandler.setNodeAttributes(): netView is null");
      }
      return false;
    }
    for(int i = 0; i < children.size(); i++){
      NodeView nodeView = netView.getNodeView((CyNode)children.get(i));
      if(nodeView == null){
        if(DEBUG){
          System.err.println("Node " + children.get(i)  +
                             " does not have a NodeView");
        }
        continue;
      }
      double height = nodeView.getHeight();
      double width = nodeView.getWidth();
      area = area + height * width;
    }//for i
    if(area == 0){
      // none of the nodes have a view?
      if(DEBUG){
        System.err.println("The area is 0.");
      }
      return false;
    }
    // NOTE: This assumes a circular shape for meta-nodes.
    double diameter = 2 * Math.sqrt(area/Math.PI);
    String diameterAsString = new Double(diameter).toString();
    nodeAtts.setAttribute(node.getIdentifier(),
            NodeAppearanceCalculator.nodeWidthBypass,
            diameterAsString);
    nodeAtts.setAttribute(node.getIdentifier(),
            NodeAppearanceCalculator.nodeHeightBypass,
            diameterAsString);
    return true;
  }//setNodeAttributes

  /**
   * Removes attributes for the given meta-node. Does nothing right now.
   * TODO: Implement
   */
  public boolean removeFromAttributes (CyNetwork cy_network,
                                    CyNode node,
                                     ArrayList metaEdges){
    return false;
  }//removeFromAttributes

  /**
   * Removes attributes created for meta-edges. Does nothing right now.
   * TODO: Implement.
   */
  public boolean removeMetaEdgesFromAttributes (CyNetwork cy_network,
                                                CyNode node,
                                               ArrayList metaEdges){
    return false;
  }//removeMetaEdgesFromAttributes
  
}//AbstractMetaNodeAttsHandler
