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
 *  Generates a set of biological statements with a subject (an allele),
 *  a verb (an interaction mode), and an object (a functional ontology). 
 *  
 *  Finds the set of nearest neighbors for each node and then looks within
 *  this set for overrepresented functional ontolgies.  
 *  Output is in the command line and a table.
 *
 * @author gcarter
 */
package phenotypeGenetics;

import java.util.*;
import java.util.List;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;

import cytoscape.*;
import cytoscape.data.annotation.*;
import cytoscape.data.servers.*;
import cytoscape.data.Semantics;
import annotations.HypDistanceCalculator;
import cytoscape.view.*;
import giny.view.*;

public class StatementCalculator{

  double minNegLogP;
  int minPopulation;

  protected static final double DEFAULT_MINNEGLOGP = 1.8;
  protected static final int DEFAULT_MINPOPULATION = 4;

  protected Annotation  annotation;
  protected String attribute;

  /**
   *  Constructs a new StatementCalculator given a project and a CytoscapeWindow.
   *
   * @param theAttribute
   */
  public StatementCalculator (String theAttribute) {
    this.attribute = theAttribute;
    this.setMinNegLogP( DEFAULT_MINNEGLOGP );
    this.setMinPopulation( DEFAULT_MINPOPULATION );
  }

  /**
   *  For each node, generate a ArrayList of its Nearest Neighbors and
   *  look for over-represented ontologies among them.
   *
   * @param cyNet the CyNetwork to be analized
   */
  public Statement[] calculateStatements ( CyNetwork cyNet, boolean isSpecific ) {
    
    // get the edges
    Iterator edgesIt = cyNet.edgesIterator();
    ArrayList edgeList = new ArrayList();
    while(edgesIt.hasNext()){
      edgeList.add(edgesIt.next());
    }
    CyEdge [] edges = (CyEdge[])edgeList.toArray(new CyEdge[edgeList.size()]);
    
    // get the nodes
    Iterator nodeIt = cyNet.nodesIterator();
    ArrayList nodeList = new ArrayList();
    while(nodeIt.hasNext()){
      nodeList.add(nodeIt.next());
    }
    CyNode [] nodes = (CyNode[])nodeList.toArray(new CyNode[nodeList.size()]);
                                    
    // deselect all currently selected nodes
    CyNetworkView netView = Cytoscape.getNetworkView(cyNet.getIdentifier());
    List selectedNodeViews = netView.getSelectedNodes();
    if(selectedNodeViews != null){
      for(Iterator it = selectedNodeViews.iterator(); it.hasNext();){
        NodeView nodeView = (NodeView)it.next();
        nodeView.setSelected(false);
      }
    }
    
    System.out.print( "Minimum -log(p) for statements = "+
                      this.minNegLogP+".  Calculating." );

    // Get the statements given these nodes, edges, and the annotation:
    Statement[] statements = getStatements( nodes, edges, this.annotation, this.attribute,
                                            this.minNegLogP, this.minPopulation, isSpecific );
    System.out.println();
 
    return statements;
  } // calculateStatements

  /**
   *
   *  For each <code>Node</code> in array nodes, work with given edges to 
   *  generate an array of <code>Statement</code>s which find large
   *  correlations between interaction classes and annotations.
   *
   */
  public Statement[] getStatements ( CyNode[] nodes, 
                                     CyEdge[] allEdges,
                                     Annotation annotation, 
                                     String attribute, 
                                     double minNegLogP, 
                                     int minPopulation, 
                                     boolean isSpecific ){
    
    ArrayList statList = new ArrayList();

    Ontology ontology = annotation.getOntology();
    HashMap ontID_to_ontValue = new HashMap();

    Integer one = new Integer( 1 );
    String divider = "~";

    for (int i = 0; i < nodes.length; i++){
      System.out.print(".");

      CyNode theNode = nodes[i];
      
      String nodeName = 
        (String)Cytoscape.getNodeAttributeValue(theNode,Semantics.CANONICAL_NAME);

      String[] nodeAlleleForms = 
        MutualInfoCalculator.getAlleleForms( nodeName, allEdges );
      
      for ( int nAF = 0; nAF < nodeAlleleForms.length; nAF++){
        String theAlleleForm = nodeAlleleForms[nAF];

        // Three parallel ArrayLists which, for each neighbor, keep track of 
        //   - a neighboring node ( the canonical name )
        //   - a neighboring allele  ( the canonical name + the alleleForm )
        //   - the edge type to that allele ( String edgeType )
        //   - an array of ints (the ontology IDs)
        ArrayList neighbors = new ArrayList();
        ArrayList neighborAlleles = new ArrayList();
        ArrayList neighborEdgeTypes = new ArrayList();
        ArrayList neighborAnnotations = new ArrayList();
  
        //  Now fill these arrays by cycling over edges:
        for (int j=0; j < allEdges.length; j++ ) {
          CyEdge theEdge = allEdges[j];
          String edgeType = (String)Cytoscape.getEdgeAttributeValue(theEdge,attribute);
          String nameA = (String)Cytoscape.getEdgeAttributeValue(theEdge,"A");
          String nameB = (String)Cytoscape.getEdgeAttributeValue(theEdge,"B");
          String alleleFormA = (String)Cytoscape.getEdgeAttributeValue(theEdge,"alleleFormA");
          String alleleFormB = (String)Cytoscape.getEdgeAttributeValue(theEdge,"alleleFormB");
          
          // If this allele is A
          if ( (nameA.compareTo(nodeName)==0) && 
               (alleleFormA.compareTo(theAlleleForm)==0) ) {
            //  Record B's node name
            neighbors.add( nameB );
            //  Record B's exact allele
            neighborAlleles.add( nameB + divider + alleleFormB );
            //  Record the edgeType to B
            neighborEdgeTypes = updateEdgeTypes( theNode, theEdge,
                                                 edgeType, neighborEdgeTypes );
            // Record B's list of annotations
            neighborAnnotations = updateAnnotations( nameB, neighborAnnotations,
                                                     annotation, ontology );
          }
          // If this allele is B
          if ( (nameB.compareTo(nodeName)==0) && 
               (alleleFormB.compareTo(theAlleleForm)==0) ) {
            //  Record A's node name
            neighbors.add( nameA );
            //  Record A's exact allele
            neighborAlleles.add( nameA + divider + alleleFormA );
            //  Record the edgeType to A
            neighborEdgeTypes = updateEdgeTypes( theNode, theEdge,
                                                 edgeType, neighborEdgeTypes );
            // Record A's list of annotations
            neighborAnnotations = updateAnnotations( nameA, neighborAnnotations,
                                                     annotation, ontology );
          }
        }

        // find the populations of each interaction type
        HashMap interactionNumbers = new HashMap();
        for ( int j=0; j < neighborEdgeTypes.size(); j++ ) {
          String type = (String)neighborEdgeTypes.get(j);
          if ( interactionNumbers.containsKey( type ) ) {
            int counter = ( (Integer)interactionNumbers.get( type ) ).intValue();
            counter++;
            Integer theInteger = new Integer( counter );
            interactionNumbers.put( type, theInteger );
          } else {
            interactionNumbers.put( type, one );
          }
        } // j over all interactions
  
        // find the populations of each annotation type
        HashMap annotationNumbers = new HashMap();
        for ( int j=0; j < neighborAnnotations.size(); j++ ) {
          int[] annots = (int[])neighborAnnotations.get(j);
          for (int k=0; k < annots.length; k++ ) {
            Integer annotNum = new Integer( annots[k] );
            if ( annotationNumbers.containsKey( annotNum ) ) {
              int counter = ( (Integer)annotationNumbers.get( annotNum ) ).intValue();
              counter++;
              Integer theInteger = new Integer( counter );
              annotationNumbers.put( annotNum, theInteger );
            } else {
              annotationNumbers.put( annotNum, one );
            }
          } // k annotations for a given neighbor
        } // j over all neighbors
  
        // A HashMap to store the nodes with both an interacton type and annotation
        //  key=intName+annotationID, element=ArrayList of nearest neighbor names
        HashMap neighborsSet = new HashMap();
        // cycle through all neighboring alleles (not nodes), but we only need to keep the 
        // node names (so some neighborSets might have duplicated members, if that node (gene)
        // comes in multiple alleleForms)
        for ( int j=0; j < neighborAlleles.size(); j++ ) {
          String neighborName = (String)neighbors.get(j);
          String intType = (String)neighborEdgeTypes.get(j);
          int[] annots = (int[])neighborAnnotations.get(j);
          for (int k=0; k < annots.length; k++ ) {
            Integer annotNum = new Integer( annots[k] );

            // put the node into the neighborsSet HashMap.
            String keyName = intType + annotNum.toString();
            if ( neighborsSet.containsKey( keyName ) ) {           
              ArrayList names = (ArrayList)neighborsSet.get( keyName );
              names.add( neighborName );
              neighborsSet.put( keyName, names );
            } else {
              ArrayList names = new ArrayList();
              names.add( neighborName );
              neighborsSet.put( keyName, names );
            }
          }
        }
  
        // Compute the p-value for each intType and each annotationType
        int pop_NNs = neighborAlleles.size();
        Iterator typeIt = interactionNumbers.keySet().iterator();
        while (typeIt.hasNext()){
          String intType = (String)typeIt.next();
          int pop_intType = ((Integer)interactionNumbers.get(intType)).intValue();
          Iterator annIt = annotationNumbers.keySet().iterator();
          while (annIt.hasNext()){
            Integer annotNum = (Integer)annIt.next();
            int pop_annType = ( (Integer)annotationNumbers.get(annotNum) ).intValue();

            // now see if there is any overlap
            String keyName = intType + annotNum.toString();
            if ( neighborsSet.containsKey( keyName ) ) {
              ArrayList neighborsSubSet = (ArrayList)neighborsSet.get( keyName );
              int pop_both = neighborsSubSet.size();
 
              // now compute the p-value
              double pValue = 
                HypDistanceCalculator.calculateHypDistance( pop_NNs,
                                                            pop_intType,
                                                            pop_annType, 
                                                            pop_both, true );
              // compute the -log(pValue), using abs() to avoid silly "-0.0"
              double negLogP = Math.abs( - Math.log( pValue )/Math.log( 10. ) );
              // if the -log(p) is big, write it to a statement and add that to statList
              if ( (pop_both >= minPopulation-1) &&  (negLogP >= minNegLogP) ) {
                
                String theNodeName = 
                  (String)Cytoscape.getNodeAttributeValue(theNode,Semantics.CANONICAL_NAME);
                OntologyTerm ontTerm = ontology.getTerm(annotNum.intValue());
                String[] nearestNeighbors = (String[])neighborsSubSet.toArray(new String[0]);
                // find the common name of theNode
                String theCommonName =
                  (String)Cytoscape.getNodeAttributeValue(theNode, Semantics.COMMON_NAME);
                Statement theStatement = new Statement( theCommonName, 
                                                        theAlleleForm, 
                                                        nearestNeighbors, 
                                                        intType, 
                                                        ontTerm, 
                                                        negLogP );
                theStatement.setCanonicalName( theNodeName );
                statList.add( theStatement );
              } // if negLogP is big
            }
          } // annotation types
        } // interaction types

      } //nAF the alleleForms for the node
    } //i the nodes
    Statement[] statements = (Statement[])statList.toArray( new Statement[0] );

    if (isSpecific){ 
      statements = makeSpecific( statements );
    }
    
    return statements;
  }//getStatements

  //---------------------------------------------------------------------------
  /**
   * Updates a <code>ArrayList</code> of edge types.
   */
  public ArrayList updateEdgeTypes (CyNode node, 
                                    CyEdge edge, 
                                    String edgeType, 
                                    ArrayList edgeList ){

    if ( edgeType == "suppression" || edgeType == "conditional" ||
         edgeType == "epistatic" ) {
      if ( node == edge.getSourceNode() ) {
        edgeType += "+";
      } else {
        edgeType += "-";
      }
    }
    edgeList.add( edgeType );
    return edgeList;
  }
  //---------------------------------------------------------------------------
  /**
   * Updates a <code>ArrayList</code> of annotations.
   */
  public ArrayList updateAnnotations(String nameB, 
                                     ArrayList annotList,
                                     Annotation annotation, 
                                     Ontology ontology ) {

    int [] neighborClassifications = annotation.getClassifications(nameB);
    HashSet seenOntologyIDs = new HashSet();
    ArrayList integerOntIDs = new ArrayList();
    //  Each of these classification elements has a path(s) to the Ontology root
    for (int nCl = 0; nCl < neighborClassifications.length; nCl++) {
      int[][] hierarchyPaths = 
        ontology.getAllHierarchyPaths(neighborClassifications[nCl]);
      //  Get statistics for each node to the root
      for( int hPath = 0; hPath < hierarchyPaths.length; hPath++) {
        for( int hNode = 0; hNode < hierarchyPaths[hPath].length; hNode++) {
          Integer intOntID = new Integer( hierarchyPaths[hPath][hNode] );
          if ( seenOntologyIDs.contains(intOntID) ){
            continue;  // don't double-count
          } else {
            seenOntologyIDs.add( intOntID );
          }
          integerOntIDs.add( intOntID );
        } //hNode
      } //hPath
    } //nCl
    int[] intOntIDs = new int[integerOntIDs.size()];
    for (int cc=0;cc<integerOntIDs.size();cc++){
      intOntIDs[cc] = ( (Integer)integerOntIDs.get( cc ) ).intValue();
    }
    annotList.add( intOntIDs );
    return annotList;         
  }
  //---------------------------------------------------------------------------
  /**
   * Sets the <code>String</code> attribute.
   */
  public void setAttribute(String theAttribute ){
    this.attribute = theAttribute;
  }//setAttribute

  //---------------------------------------------------------------------------
  /**
   * Sets the <code>Annotation</code> object.
   */
  public void setAnnotation (Annotation theAnnotation){
    this.annotation = theAnnotation;
  }//setAnnotation
  //---------------------------------------------------------------------------
  /**
   * Sets the minimum -log(p) for over-representation.
   */
  public void setMinNegLogP (double min_p_value){
    this.minNegLogP = min_p_value;
  }//setMinNegLogP
  //---------------------------------------------------------------------------
  /**
   * Gets the minimum -log(p) for over-representation.
   */
  public double getMinNegLogP (){
    return this.minNegLogP;
  }//setMinNegLogP
  //---------------------------------------------------------------------------
  /**
   * Sets the minimum common population for over-representation.
   */
  public void setMinPopulation(int min_pop){
    this.minPopulation = min_pop;
  }//setMinPopulation
  //---------------------------------------------------------------------------
  /**
   * Gets the minimum common population for over-representation.
   */
  public int getMinPopulation(){
    return this.minPopulation;
  }//setMinPopulation
  //---------------------------------------------------------------------------
  /**
   * Finds the edge between two nodes within an array of edges.
   */
  public CyEdge[] getEdgesWithAllele( String nodeName, 
                                      String theAlleleForm, 
                                      CyEdge[] edges){
    ArrayList edgeList = new ArrayList();
    for (int i = 0; i < edges.length; i++){
      String nodeA = (String)Cytoscape.getEdgeAttributeValue(edges[i],"A");
      String nodeB = (String)Cytoscape.getEdgeAttributeValue(edges[i],"B");
      String alleleFormA = (String)Cytoscape.getEdgeAttributeValue(edges[i],"alleleFormA");
      String alleleFormB = (String)Cytoscape.getEdgeAttributeValue(edges[i],"alleleFormB");
      if (( nodeName.compareTo(nodeA) == 0 ) && (theAlleleForm.compareTo(alleleFormA) == 0 )){
        edgeList.add( edges[i] );
      } else if ((nodeName.compareTo(nodeB)==0) && (theAlleleForm.compareTo(alleleFormB)==0)){
        edgeList.add( edges[i] );
      }
    }
    
    return (CyEdge[])edgeList.toArray( new CyEdge[edgeList.size()] );
  }
  //---------------------------------------------------------------------------
  /**
   * Finds the edge between two nodes within an array of edges.
   */
  public CyEdge getCommonEdge( CyNode node1, CyNode node2, CyEdge[] edges ) {
    
    CyEdge theEdge = null;

    //  Cycle through the edges
    for (int i = 0; i < edges.length; i++) {
      CyEdge testEdge = edges[i];
      boolean edge12 = ( (node1 == testEdge.getSourceNode()) &&
                         (node2 == testEdge.getTargetNode()) );
      boolean edge21 = ( (node2 == testEdge.getSourceNode()) &&
                         (node1 == testEdge.getTargetNode()) );
      if ( edge12 | edge21 ) {
        theEdge = testEdge;
      }
    }
    return theEdge;
  }
  
  /**
   * Removes from the given <code>Statement</code> array those <code>Statement</code>
   * objects that have lower -log(p) than other <code>Statement</code> objects
   * in the <code>Statement</code> array, but that are more general (meaning that their 
   * <code>OntologyTerm</code> object is a parent or a container of the 
   * <code>OntologyTerm</code> object of the other <code>Statement</code> objects).
   *
   * @param statements a <code>Statement[]</code> list found in calcuateStatements
   * @return the shortened array of <code>Statment</code> objects.
   */
  public Statement[] makeSpecific(Statement[] statements){

    ArrayList statList = new ArrayList( );
    for (int i = 0; i < statements.length; i++){
      statList.add(statements[i]);
    }

    //for(int i = statements.length - 1; i >= 0; i--){
    // for(int j = i-1; j >= 0; j--){

    for(int i = 0; i < statements.length; i++){
      for(int j = 0; j < statements.length; j++){
        if ( j != i ) {
          OntologyTerm ot1 = statements[i].getOntologyTerm();
          OntologyTerm ot2 = statements[j].getOntologyTerm();
          int [] ot2pc = ot2.getParentsAndContainers();
          ArrayList ot2pcList = new ArrayList();
          for (int k=0; k<ot2pc.length; k++) { ot2pcList.add(new Integer(ot2pc[k])); }
          boolean parentOrContainer = ot2pcList.contains( new Integer( ot1.getId() ) );
          boolean sameName=
            (statements[i].getCanonicalName()).compareTo(statements[j].getCanonicalName())==0;
          if( sameName && parentOrContainer &&
              ( statements[i].getNegLogP() <= statements[j].getNegLogP() )  ) {
            statList.remove(statements[i]);
            break;
          }
        }
      }
    }
    return (Statement[])statList.toArray( new Statement[0] );
  }//makeSpecific 

}// StatementTable
                                                                                  
