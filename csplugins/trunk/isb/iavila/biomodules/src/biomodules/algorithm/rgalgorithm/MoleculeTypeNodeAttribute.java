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

package biomodules.algorithm.rgalgorithm;

import java.lang.String;
import java.util.*;
import cytoscape.*;
import cytoscape.data.Semantics;
import cytoscape.data.CyAttributes;

/**
 * This class offers a static method that adds to nodes an attribute called "moleculeType". 
 * It assigns values to this attribute from the interaction types contained attributes for 
 * edges.
 * The formant supported by this class is the following.
 * The possible interactions are:
 * pp
 * pm
 * pd
 * pr
 * Each letter in this interaction specifiers stand for:
 * p = protein
 * m = metabolite
 * d = DNA
 * r = RNA
 * So the value of the moleculeType attribute will be one of these 3, plus "unknown", 
 * if the interaction type is not supported. 
 */

public class MoleculeTypeNodeAttribute {

  // Atribute name:
  static public String ATTRIBUTE_NAME = Semantics.MOLECULE_TYPE;
  // Attribute values:
  static public String PROTEIN = Semantics.PROTEIN;
  static public String DNA = Semantics.DNA;
  static public String RNA = Semantics.RNA;
  static public String METABOLITE = "metabolite";
  static public String UNKNOWN = "unknown";
  
  /**
   * Adds to the nodes in the given network an attribute called 
   * MoleculeTypeNodeAttribute.ATTRIBUTE_NAME, and assigns to it one of:
   * MoleculeTypeNodeAttribute.PROTEIN, MoleculeTypeNodeAttribute.DNA, 
   * MoleculeTypeNodeAttribute.METABOLITE, MoleculeTypeNodeAttribute.RNA, or 
   * MoleculeTypeNodeAttribute.UNKNOWN.
   * These values are obtained from the edge attribute "interaction". 
   * Recognized interaction types are:
   * "pp", "pd", "pr", and "pm"
   *
   * @return the number of nodes with a known molecule type
   */
  static public int addMoleculeTypeAttribute (CyNetwork cy_net){
    
    CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();
    CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
    
    // Get an array of CyEdges in cy_net
    Iterator it = cy_net.edgesIterator();
    ArrayList edgeList = new ArrayList();
    while(it.hasNext()){
      CyEdge edge = (CyEdge)it.next();
      edgeList.add(edge);
    }//while it.hasNext()
    CyEdge [] edges = (CyEdge[])edgeList.toArray(new CyEdge[0]);
    
    // Iterate over the edges to get their interaction type and assign the
    // molecule attribute to the connecting nodes
    HashSet nodesWithType = new HashSet();
    HashSet nodesWithKnownType = new HashSet();
    for(int i = 0; i < edges.length; i++){
      String edgeType = edgeAtts.getStringAttribute(edges[i].getIdentifier(),Semantics.INTERACTION);
        //(String)cy_net.getEdgeAttributeValue(edges[i], Semantics.INTERACTION);
      if(edgeType == null){
        System.err.println("Edge" + edges[i] + " does not have an interaction type.");
        continue;
      }
      CyNode source = (CyNode)edges[i].getSource();
      CyNode target = (CyNode)edges[i].getTarget();
      
      String sourceType = nodeAtts.getStringAttribute(source.getIdentifier(), ATTRIBUTE_NAME);//(String)cy_net.getNodeAttributeValue(source,ATTRIBUTE_NAME);
      String targetType = nodeAtts.getStringAttribute(target.getIdentifier(), ATTRIBUTE_NAME); //(String)cy_net.getNodeAttributeValue(target,ATTRIBUTE_NAME);
      
      nodesWithType.add(source);
      nodesWithType.add(target);
      
      if(sourceType == null || !sourceType.equals(PROTEIN)){
        // If a node has already been determined to be a protein, leave it alone
        sourceType = getSourceMoleculeType(edgeType);
        nodeAtts.setAttribute(source.getIdentifier(), ATTRIBUTE_NAME, sourceType);
        //cy_net.setNodeAttributeValue(source,ATTRIBUTE_NAME,sourceType);
      }
      
      if(targetType == null || !targetType.equals(PROTEIN)){
        // If a node has already been determined to be a protein, leave it alone
        targetType = getTargetMoleculeType(edgeType);
        nodeAtts.setAttribute(target.getIdentifier(), ATTRIBUTE_NAME, sourceType);
        //cy_net.setNodeAttributeValue(target,ATTRIBUTE_NAME,sourceType);
      }
      
      if(!sourceType.equals(UNKNOWN)){
        nodesWithKnownType.add(source);
      }

      if(!targetType.equals(UNKNOWN)){
        nodesWithKnownType.add(target);
      }
            
    }//for i
    
    // If there are nodes that do not have a molecule type, assign UNKNOWN to them
    if(nodesWithType.size() < cy_net.getNodeCount()){
      
      it = cy_net.nodesIterator();
      while(it.hasNext()){
        CyNode node = (CyNode)it.next();
        String nodeType = nodeAtts.getStringAttribute(node.getIdentifier(), ATTRIBUTE_NAME); //(String)cy_net.getNodeAttributeValue(node,ATTRIBUTE_NAME);
        
        if(nodeType == null){
         nodeAtts.setAttribute(node.getIdentifier(), ATTRIBUTE_NAME, UNKNOWN); 
        	//cy_net.setNodeAttributeValue(node,ATTRIBUTE_NAME,UNKNOWN);
        }
      }//while it
    }// if


    return nodesWithKnownType.size();

        
  }//addMoleculeTypeAttribute

  /**
   * For now, it returns PROTEIN, since all the supported interaction
   * types start with a "p" for protein, or UNKNOWN if the given interaction
   * type is not supported.
   */
  static public String getSourceMoleculeType (String interactionType){
    if(interactionType.equals("pp") ||
       interactionType.equals("pd") ||
       interactionType.equals("pr") ||
       interactionType.equals("pm")){
      return PROTEIN;
    }
    return UNKNOWN;
  }//getSourceMoleculeType
  
  /**
   * @return the node-type given the interaction.
   */
  static public String getTargetMoleculeType (String interactionType){
    
    if(interactionType.equals("pp")){
      return PROTEIN;
    }
    
    if(interactionType.equals("pd")){
      return DNA;
    }

    if(interactionType.equals("pm")){
      return METABOLITE;
    }

    if(interactionType.equals("pr")){
      return RNA;
    }

    return UNKNOWN;
    
  }//getSourceMoleculeType
  
}//class MoleculeTypeNodeAttribute

