/**  Copyright (c) 2005 Institute for Systems Biology
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
 * Determine directionality of GeneticInteraction according to 
 * phenotype inequlity class and create edges representing genetic
 * interactions. Required before color display of inequality class as 
 * visual attribute. 
 * 
 * @author Greg Carter
 * @author Iliana Avila
 */
package phenotypeGenetics;
import java.util.*;
import java.io.*;
import java.lang.Math;
import cytoscape.*;
import cytoscape.data.*;

public class GeneticClassVisualizer {
  
  protected CyNetwork graph;
  
  /**
   * @param cy_network the CyNetwork on which edges will be created
   */
  public GeneticClassVisualizer (CyNetwork cy_network){
    this.graph = cy_network;
  }
  
  /**
   * Creates an edge with the correct direction 
   * for each GeneticInteraction in the array
   */
  public void visualize (GeneticInteraction [] interactions){
    
    List nodes = this.graph.nodesList();
    HashMap interactionToConnectors = new HashMap();
    
    // For any edge that has a GeneticInteraction attribute,
    // we can calculate the proper direction
    for(int e = 0; e < interactions.length; e++){
      
      GeneticInteraction interaction = interactions[e];
      DiscretePhenoValueSet d = interaction.getDiscretePhenoValueSet();
      String gclass = interaction.getGeneticClass();
      
      // Get nodes corresponding to single mutant A and B in that
      // order
      CyNode nodeA = Cytoscape.getCyNode(interaction.getMutantA().getName());
      if(nodeA == null){
        throw new IllegalStateException("Node w/name " 
                                        + interaction.getMutantA().getName()
                                        + " does not exist.");
      }
      CyNode nodeB = Cytoscape.getCyNode(interaction.getMutantB().getName());
      if(nodeB == null){
        throw new IllegalStateException("Node w/name " 
                                        + interaction.getMutantB().getName()
                                        + " does not exist.");
      }
      
      int pWT = d.getWT();
      int pA = d.getA();
      int pB = d.getB();
      int pAB = d.getAB();
            
      //System.out.println(pWT+" "+pA+" "+pB+" "+pAB);
    
      EdgeConnectors edgeConnectors = new EdgeConnectors();
      
      if(gclass.equals("suppression")){
      
        if(pA == pWT){
          // A suppresses B
          edgeConnectors.source = nodeA;
          edgeConnectors.target = nodeB;
        }else{
          // B suppresses A
          edgeConnectors.source = nodeB;
          edgeConnectors.target = nodeA;
        }
        
      }else if(gclass.equals("epistatic")){
        
        if(pA == pAB){
          // A is epistatic to B
          edgeConnectors.source = nodeA;
          edgeConnectors.target = nodeB;
        }else{
          // B is epistatic to A
          edgeConnectors.source = nodeB;
          edgeConnectors.target = nodeA;
        }
        
      }else if(gclass.equals("single-nonmonotonic")){
        
        if( Math.abs(pA-pWT+pAB-pWT) != Math.abs(pA-pWT)+Math.abs(pAB-pWT) ) {
          // Effect of A is changed in the B background
          edgeConnectors.source = nodeA;
          edgeConnectors.target = nodeB;
        }else{
          // Effect of B is changed in the A background
          edgeConnectors.source = nodeB;
          edgeConnectors.target = nodeA;
        }
        
      }else if(gclass.equals("conditional")){
              
        if(pA == pWT){
          // Effect of A is dependent on the B background
          edgeConnectors.source = nodeB;
          edgeConnectors.target = nodeA;
        }else{
          // Effect of B is dependent on the A background
          edgeConnectors.source = nodeA;
          edgeConnectors.target = nodeB;
        }
      
      }else if(gclass.equals("additive") ||
               gclass.equals("non-interacting") ||
               gclass.equals("synthetic") ||
               gclass.equals("asynthetic") ||
               gclass.equals("double-nonmonotonic")){
        // Non-directional
        edgeConnectors.source = nodeA;
        edgeConnectors.target = nodeB;
      }else{
        throw new IllegalStateException("Unknown genetic class: " + gclass);
      }
      
      interactionToConnectors.put(interaction, edgeConnectors);
      
    }//for each GeneticInteraction
    
    createEdges(interactionToConnectors);
    
  }//visualize

  /** 
   * @param interactionToConnector a map from GeneticInteractions to EdgeConnectors
   */
  protected void createEdges (HashMap interactionToConnector){
    
    ArrayList newEdges = new ArrayList();
    Iterator it = interactionToConnector.keySet().iterator();
    
    while(it.hasNext()){
      
      GeneticInteraction interaction = (GeneticInteraction)it.next();
      EdgeConnectors connectors = (EdgeConnectors)interactionToConnector.get(interaction);
      CyNode source = connectors.source;
      CyNode target = connectors.target;
      
      if(source == null || !this.graph.containsNode(source)){
        throw new IllegalStateException("Node "+ source + 
                                        " does not exist in CyNetwork!");
      }
      
      if(target == null || !this.graph.containsNode(target)){
        throw new IllegalStateException("Node "+ target + 
                                        " does not exist in CyNetwork!");
      }
    
      String sourceName = 
        (String)Cytoscape.getNodeAttributeValue(source, Semantics.CANONICAL_NAME);
    
      String targetName =
        (String)Cytoscape.getNodeAttributeValue(target, Semantics.CANONICAL_NAME);
    
      String edgeName = interaction.getEdgeName();  
    
      // this should create the edge in the RootGraph if it does not exist already:
      CyEdge edge = Cytoscape.getCyEdge(sourceName,
                                        edgeName,
                                        targetName,
                                        interaction.getGeneticClass() // type of interaction
                                        );
    
      // copy attributes to the new edge:
      HashMap attributeMap = interaction.getEdgeAttributes();
      Set entries = attributeMap.entrySet();
      Iterator entryIt = entries.iterator();
      while(entryIt.hasNext()){
        Map.Entry entry = (Map.Entry)entryIt.next();
        Cytoscape.setEdgeAttributeValue(edge, (String)entry.getKey(), entry.getValue()); 
      }
      
      newEdges.add(edge);
      
    }// for each interaction create an edge
    
    // restore the edges in the CyNetwork, since we created them in the RootGraph
    // note that it is a lot faster to restore all the edges at once than one by one
    this.graph.restoreEdges(newEdges);
    
  }//createEdge

  // Restoring edges in group is more efficient
  // than restoring them one by one, so this class
  // is used to store the source/target nodes for edges
  // to be created and then restored as a group
  protected class EdgeConnectors {
    public CyNode source, target;
    public EdgeConnectors (){}
  }//EdgeConnectors

}//GeneticClassVisualizer
