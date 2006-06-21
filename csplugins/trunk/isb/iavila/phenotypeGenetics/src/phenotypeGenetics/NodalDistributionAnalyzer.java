/**
 * For each node, compute the distribution of genetic interaction types.
 *
 * @author original author not documented
 * @author Iliana Avila (refactored)
 */
package phenotypeGenetics;
import java.util.*;
import java.util.List;
import java.io.*;
import cytoscape.*;
import cytoscape.data.*;

public class NodalDistributionAnalyzer {
    
  /**
   * For each allele, analyze the distribution of interaction classes in cy_net 
   * and return a HashMap to be put in a table
   *
   * @param use_common_names whether or not the map should contain as keys common
   * names or canonical names
   */
  public static HashMap calculateNodeDistribution (CyNetwork cy_network, 
                                                   boolean use_common_names){
    
    String attribute = GeneticInteraction.ATTRIBUTE_GENETIC_CLASS;
    
    //Get an array of edges
    Iterator edgeIterator = cy_network.edgesIterator();
    List edgeList = new ArrayList();
    while(edgeIterator.hasNext()){
    		edgeList.add(edgeIterator.next());
    }
    CyEdge [] edges = (CyEdge[])edgeList.toArray(new CyEdge[edgeList.size()]);
    
    //  The set of nodes
    Iterator nodesIterator = cy_network.nodesIterator();
  
    // A HashMap of interaction types (nodeDistribution) keyed on the nodes:
    HashMap nodeDistribution = new HashMap();
    
    // Get all the possible Modes
    
    // Cycle through the nodes
    CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
    CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();
    while(nodesIterator.hasNext()) {

      //  Identify the node at hand
      CyNode node = (CyNode)nodesIterator.next();
      String nodeName = nodeAtts.getStringAttribute(node.getIdentifier(), Semantics.COMMON_NAME);
      //   (String)Cytoscape.getNodeAttributeValue(node, Semantics.COMMON_NAME);
      String canonicalName = node.getIdentifier(); 
      //  (String)Cytoscape.getNodeAttributeValue(node, Semantics.CANONICAL_NAME);
      //  Get the alleleForms for this node with a method
      if(nodeName == null) nodeName = canonicalName;
      String[] alleleForms = Utilities.getAlleleForms(canonicalName,edges);
      //  Cycle through the alleleForms
      for(int af = 0; af < alleleForms.length; af++) {
        String alleleForm = alleleForms[af];
        
        //  Add the alleleForm to the nodeName, to make it an AlleleName
        String alleleName = new String();
        if(use_common_names) {
          alleleName = nodeName + "(" + alleleForm + ")";
        } else {
          alleleName = alleleForm + MutualInfoCalculator.divider + canonicalName;
        }
        
        //  Cycle through all edges to see if they belong to node
        for (int i = 0; i < edges.length; i++) {
          
          //  Identify the edge and get it's class, edgeType
          CyEdge edge = edges[i];
          String edgeType = edgeAtts.getStringAttribute(edge.getIdentifier(),attribute);
          //(String)Cytoscape.getEdgeAttributeValue(edge,attribute);
          String edgeA = edgeAtts.getStringAttribute(edge.getIdentifier(), GeneticInteraction.ATTRIBUTE_MUTANT_A); 
           // (String)Cytoscape.getEdgeAttributeValue(edge,
           //                                         GeneticInteraction.ATTRIBUTE_MUTANT_A);
          String edgeB = edgeAtts.getStringAttribute(edge.getIdentifier(),GeneticInteraction.ATTRIBUTE_MUTANT_B); 
          //  (String)Cytoscape.getEdgeAttributeValue(edge,
          //                                          GeneticInteraction.ATTRIBUTE_MUTANT_B);
          String edgeAlleleA = edgeAtts.getStringAttribute(edge.getIdentifier(), GeneticInteraction.ATTRIBUTE_ALLELE_FORM_A);
          //  (String)Cytoscape.getEdgeAttributeValue(edge,
          //                                      GeneticInteraction.ATTRIBUTE_ALLELE_FORM_A);
          String edgeAlleleB = edgeAtts.getStringAttribute(edge.getIdentifier(), GeneticInteraction.ATTRIBUTE_ALLELE_FORM_B); 
          //  (String)Cytoscape.getEdgeAttributeValue(edge,
          //                                       GeneticInteraction.ATTRIBUTE_ALLELE_FORM_B);
          if (((canonicalName.compareTo(edgeA)==0) && 
               (alleleForm.compareTo(edgeAlleleA)==0) ) |
              ((canonicalName.compareTo(edgeB)==0) && 
               (alleleForm.compareTo(edgeAlleleB)==0) ) ) {

            //  Check to see if it is a source or target edge and increment
            //  the appropriate counter in updateNodeDist.
            //  Implementation of the intIndex is clumsy, but the final
            //  categories do not exactly match the edgeTypes
            Mode mode = (Mode)Mode.modeNameToMode.get(edgeType);
            if(mode == null){
              throw new IllegalStateException("edgeType " + edgeType + " had no Mode!");
            }
            
            if(mode.isDirectional()){
              if (node == edge.getSource() ) {
                updateNodeDist( edgeType+"+", alleleName, nodeDistribution);
              } else if ( node == edge.getTarget() ) {
                updateNodeDist( edgeType+"-", alleleName, nodeDistribution);
              } else {
                throw new IllegalStateException(alleleName + " is neither source nor target.");
              }
            } else {
              updateNodeDist( edgeType, alleleName, nodeDistribution);
            }

          }// if allele is A or B
        }// over all edges
      }// over all alleleForms for the node
      System.out.print(":");
    }// over all nodes
    
    return nodeDistribution; 
    
  }//calculateNodeDistribution

  /**
   * Fills in the Nodal distribution table.
   * If there does not exist a distribution array for the node create one
   * and increment the value of the edgeType, else increment the value at the edgeType for
   * the given node.
   */
  private static void updateNodeDist (String edgeType, String nodeName, HashMap dist) {
    
    //  If the node is already in the distribution, increment the interaction
    //  type.  Otherwise, initialize a new key and array of types.
    if(dist.containsKey(nodeName)){
      HashMap distArray = (HashMap)dist.get(nodeName);
      Integer count = new Integer( 0 );
      if(distArray.containsKey(edgeType)){
        count = (Integer)distArray.get(edgeType);
      } 
      distArray.put( edgeType, new Integer( count.intValue()+1 ) );
      dist.put(nodeName, distArray);
    }else{
      HashMap distArray = new HashMap();
      distArray.put( edgeType, new Integer( 1 ) );
      dist.put(nodeName, distArray);
    }
    
  }//updateNodeDist
  
  /**
   * Fills in the Nodal distribution table.
   * If there does not exist a distribution array for the node create one
   * and increment the value at index, else increment the value at index for
   * the given node.
   */
  private void updateNodeDist (int index, String nodeName, HashMap dist) {
    //  The counter array, elements are the interaction types
    int[] distArray;
    
    //  If the node is already in the distribution, increment the interaction
    //  type.  Otherwise, initialize a new key and array of types.
    if(dist.containsKey(nodeName)){
      distArray = (int[])dist.get(nodeName);
      distArray[index]++;
      dist.put(nodeName, distArray);
    }else{
      distArray = new int[13];
      distArray[index]++;
      dist.put(nodeName, distArray);
    }
  }//updateNodeDist
  
}//NodalDistributionAnalyzer
