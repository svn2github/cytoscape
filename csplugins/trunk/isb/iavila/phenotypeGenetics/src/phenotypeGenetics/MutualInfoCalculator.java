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
 *  For each node, make a list of Nearest Neighbors and then compare with 
 *  other nodes to find those with common NNs.
 *  Output is a #nodes x #nodes table showing the number of common NN's for
 *  every pair of nodes in the network, and a text output of node pairs with
 *  both a high number of nearest neighbors in common, both as a percentage of
 *  their total NNs and the total number of nodes in the network.  These 
 *  nodes are also selected.
 *
 * @author Greg Carter
 * @author Iliana Avila (transfered to C2.0)
 */
package phenotypeGenetics;

import java.util.*;
import java.util.List;
import java.io.*;
import cytoscape.*;
import cytoscape.view.*;
import annotations.HypDistanceCalculator;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.lang.Math;
import cytoscape.data.Semantics;

public class MutualInfoCalculator {

  CyNetwork graph;
  double minScore;	    // The minimum score for signficance.
  int    numRandomReps;	// The number of randomizations for calculation of p-value
  HashMap bkg;		      // The background probabilities for allele pair types

  protected static final String divider = "~";

  protected static final double DEFAULT_MINSCORE = 3.0;
  protected static final int DEFAULT_NUMRANDOMREPS = 100;
  protected static final int MIN_OVERLAP = 5;

  //---------------------------------------------------------------------
  /**
   *  Constructs a new MutualInfoCalculator given a project and a 
   *  CytoscapeWindow.
   *
   *  @param cy_network the CyNetwork to be analyzed
   */
  public MutualInfoCalculator (CyNetwork cy_network){
    this.graph = cy_network;
    this.bkg = this.buildBkg( );
    this.setMinScore( DEFAULT_MINSCORE );
    this.setNumRandomReps( DEFAULT_NUMRANDOMREPS );
  }

  /**
   * @return the CyNetwork in this calculator
   */
  public CyNetwork getCyNetwork (){
    return this.graph;
  }
  //---------------------------------------------------------------------
  /**
   *  For each node, generate a ArrayList of its Nearest Neighbors.
   */
  public MutualInfo[] miFinder () {
    // get the edges
    Iterator edgesIt = this.graph.edgesIterator();
    ArrayList edgeList = new ArrayList();
    while(edgesIt.hasNext()){
      edgeList.add(edgesIt.next());
    }
    CyEdge [] edges = (CyEdge[])edgeList.toArray(new CyEdge[edgeList.size()]);
    
    //  get the nodes
    Iterator nodesIt = this.graph.nodesIterator();
    ArrayList nodeList = new ArrayList();
    while(nodesIt.hasNext()){
      nodeList.add(nodesIt.next());
    }
    CyNode[] nodes = (CyNode[])nodeList.toArray(new CyNode[nodeList.size()]);
    
    // we're going to select some nodes, so deselect currently selected ones
    CyNetworkView netView = Cytoscape.getNetworkView(this.graph.getIdentifier());
    List selectedNodeViews = netView.getSelectedNodes();
    if(selectedNodeViews != null){
      for(Iterator it = selectedNodeViews.iterator(); it.hasNext();){
        CyNodeView nodeView = (CyNodeView)it.next();
        nodeView.setSelected(false);
      }
    }
    
    // Set up the command line output
    System.out.print( "Minimum score = "+this.minScore+".  Calculating." );

    // Put selected node pairs in an MutualInfo
    MutualInfo[] pairs = getPairs( nodes, edges, this.minScore, true );

    return (MutualInfo[]) pairs;
  }
  //---------------------------------------------------------------------
  /**
   *
   *  For each pair of <code>CyNode</code>s in array nodes, work with given 
   *  <code>CyEdge</code>s to generate an array of <code>MutualInfo[]</code> of 
   *  mutual information
   **/
  public MutualInfo[] getPairs( CyNode[] nodes, 
                                CyEdge[] edges, 
                                double minScore,
                                boolean quiet ) {
    int numEdgeTypes = 0;

    //long before = System.currentTimeMillis();
    
    if (!quiet) { System.out.println(); System.out.print( "Finding alleles..." ); }
    
    // build an array of alleles which will be cycled over later
    Allele[] alleles = getAlleles( nodes, edges);
    
    if (!quiet) { System.out.print( alleles.length + " alleles found."); }
    //System.out.println( "time = "+((System.currentTimeMillis()-before))+" ms");

    //Get interaction class distributions
    NodalDistributionAnalyzer analyzer = new NodalDistributionAnalyzer();
    HashMap nodeDist = analyzer.getNodeDistribution(this.graph, false);
    if (nodeDist.keySet().size() > 0 ) {
      int[] distArray = (int[])nodeDist.get( (String)nodeDist.keySet().iterator().next() );
      numEdgeTypes = distArray.length;
    }

    //Turn them into probabilities
    HashMap probDist = buildProbDist( nodeDist );

    // A place to put important MutualInfos when we find them
    ArrayList miList = new ArrayList();

    // A hashmap keyed on alleles, pointing to an ArrayList of neighbor alleles
    HashMap alleleHash = new HashMap();
    // A hashmap keyed on alleles, pointing to an ArrayList of interaction types
    // for the edge which connects to the corresponding neighbor allele in alleleHash.
    HashMap edgeTypeHash = new HashMap();
    // A hashmap keyed on alleles, pointing to an ArrayList of flags identifying
    // the allele as a source ("s") or target ("t")
    HashMap edgeFlagHash = new HashMap();
    // A hashmap keyed on alleles, pointing to an ArrayList of relations identifying
    // the allele as having a single-mutant phenotype >, <, or = to WT
    HashMap edgeRelationHash = new HashMap();
    // A hashmap keyed on alleles, pointing to an ArrayList of edges themselves.
    HashMap interactionEdgesHash = new HashMap();

    //  Cycle over all edges, find connecting nodes A and B with alleleForms
    //  alleleformA and alleleformB, then put results into HashMaps
    for (int i=0; i<edges.length; i++) {

      CyEdge theEdge = edges[i];
      
      String edgeType = 
        (String)Cytoscape.getEdgeAttributeValue(theEdge,
                                                GeneticInteraction.ATTRIBUTE_GENETIC_CLASS);

      // Build the strings for keying
      String aKeyA = 
        (String)Cytoscape.getEdgeAttributeValue(theEdge,
                                                GeneticInteraction.ATTRIBUTE_ALLELE_FORM_A) 
        + divider + 
        (String)Cytoscape.getEdgeAttributeValue(theEdge,
                                                GeneticInteraction.ATTRIBUTE_MUTANT_A);
      
      String aKeyB = 
        (String)Cytoscape.getEdgeAttributeValue(theEdge,
                                                GeneticInteraction.ATTRIBUTE_ALLELE_FORM_B) + 
        divider + 
        (String)Cytoscape.getEdgeAttributeValue(theEdge,
                                                GeneticInteraction.ATTRIBUTE_MUTANT_B);

      // Find out which is the source and which is the target
      String nodeIdentifier = 
        (String)Cytoscape.getEdgeAttributeValue(theEdge, 
                                                GeneticInteraction.ATTRIBUTE_MUTANT_A);
      // the 2nd argument tells it to not create the node if it is not there
      // note that the returned node may belongs to the RootGraph, but not necessarily
      // to this CyNetwork
      CyNode nodeA = Cytoscape.getCyNode(nodeIdentifier, false);
      if(nodeA == null){
        // Figure out what to do here, for now just throw exception
        throw new IllegalStateException("Node with identifier " +
                                        nodeIdentifier + " does not exist in RootGraph!");
      }
      // we know that it is in RootGraph, now lets make sure that it is in this CyNetwork
      if(!this.graph.containsNode(nodeA)){
        //Figure out what to do here, for now just throw exception
        throw new IllegalStateException("Node with identifier " +
                                        nodeIdentifier + " does not exist in CyNetwork!");
      }
      
      // old code to get nodeA:
      //CyNode nodeA = 
      //this.cytoscapeWindow.getNode( (String)edgeHash.get() );
      
      String flagA = new String();
      String flagB = new String();
      if ( nodeA == theEdge.getSourceNode() ) {
        flagA = "s";
        flagB = "t";
      } else {
        flagA = "t";
        flagB = "s";
      } 

      // Get the single-mutants relative to wild type
      GeneticInteraction gi = 
        (GeneticInteraction)Cytoscape.getEdgeAttributeValue(theEdge,
                                                            GeneticInteraction.ATTRIBUTE_SELF);
      DiscretePhenoValueSet d = gi.getDiscretePhenoValueSet();

      int pA = d.getA();
      int pB = d.getB();
      int pWT = d.getWT();
      
      String opA = getSingleMutantRelation( pA, pWT );
      String opB = getSingleMutantRelation( pB, pWT );
      
      String relations = opA + opB;
      if ( !this.bkg.containsKey( relations ) ) {
        relations = opB + opA;
      }
      if ( !this.bkg.containsKey( relations ) ) {
        System.out.println("No instances of "+relations+" in background.");
      }
      
      // put B in A's list of fellow alleles
      alleleHash = updateList( aKeyA, aKeyB, alleleHash );
      // put A in B's list of fellow alleles
      alleleHash = updateList( aKeyB, aKeyA, alleleHash );
      // Put the edgeType in lists for A and B
      edgeTypeHash = updateList( aKeyA, edgeType, edgeTypeHash );
      edgeTypeHash = updateList( aKeyB, edgeType, edgeTypeHash );
      // Put the flags in lists for A and B
      edgeFlagHash = updateList( aKeyA, flagA, edgeFlagHash );
      edgeFlagHash = updateList( aKeyB, flagB, edgeFlagHash );
      // Put the relations in lists for A and B
      edgeRelationHash = updateList( aKeyA, relations, edgeRelationHash );
      edgeRelationHash = updateList( aKeyB, relations, edgeRelationHash );
      // Put the edges in lists for A and B
      interactionEdgesHash = updateList( aKeyA, theEdge, interactionEdgesHash );
      interactionEdgesHash = updateList( aKeyB, theEdge, interactionEdgesHash );

    }// i, the edge at hand

    if (!quiet) {System.out.println( "Done.  ");}

    //  Now cycle over all alleles and, drawing from the HashMaps we have just made,
    //  make a list of nearest-neighbor alleles for each allele.
    for (int i = 0; i < alleles.length; i++) { 
      System.out.print(".");   // Let the user know something's going on
      Allele allele1 = alleles[i];
      //  Find the allele's info to make a key
      String aKey1 = allele1.getAlleleForm() + divider +
        (String)Cytoscape.getNodeAttributeValue(allele1.getNode(),
                                                Semantics.CANONICAL_NAME);
      
      //  For this allele, get the list of alleles and interactions to those 
      //  alleles
      ArrayList list1 = (ArrayList)alleleHash.get( aKey1 );
      ArrayList typeList1 = (ArrayList)edgeTypeHash.get( aKey1 );
      ArrayList flagList1 = (ArrayList)edgeFlagHash.get( aKey1 );
      ArrayList relationList1 = (ArrayList)edgeRelationHash.get( aKey1 );
      ArrayList edgeList1 = (ArrayList)interactionEdgesHash.get( aKey1 );
     
      //  Now cycle over all possible neighbors
      for (int j = i+1; j < alleles.length; j++) {
        Allele allele2 = alleles[j];

        //  Find the allele's info to make a key
        String aKey2 = allele2.getAlleleForm() + divider +
          (String)Cytoscape.getNodeAttributeValue(allele2.getNode(),Semantics.CANONICAL_NAME);

        //  For this allele, get the list of alleles and interactions to those 
        //  alleles
        ArrayList list2 = (ArrayList)alleleHash.get( aKey2 );
        ArrayList typeList2 = (ArrayList)edgeTypeHash.get( aKey2 );
        ArrayList flagList2 = (ArrayList)edgeFlagHash.get( aKey2 );
        ArrayList relationList2 = (ArrayList)edgeRelationHash.get( aKey2 );
        ArrayList edgeList2 = (ArrayList)interactionEdgesHash.get( aKey2 );
    
        if ( list1 == null | list2 == null ) {continue;}

        //  The intersection of these lists gives a set of third-party alleles which
        //  have been tested with alleles 1 and 2
        ArrayList list12 = intersection( list1, list2 );
        int commonTests = list12.size();

        //  Make a sublist of neighbor alleles for allele1, which:
        //  1) are tested with both 1 and 2
        //  2) interact with allele 1
        ArrayList interacting1 = new ArrayList();
        HashMap typeHash1 = new HashMap();
        HashMap flagHash1 = new HashMap();
        HashMap relationHash1 = new HashMap();
        HashMap edgeHash1 = new HashMap();
        for ( int k=0; k<list1.size(); k++) {
          String allele = (String)list1.get(k);
          if ( list12.contains( allele ) && (!interacting1.contains( allele )) ) {
            interacting1.add( allele );
            typeHash1.put( allele, (String)typeList1.get(k) );
            flagHash1.put( allele, (String)flagList1.get(k) );
            relationHash1.put( allele, (String)relationList1.get(k) );
            edgeHash1.put( allele, (CyEdge)edgeList1.get(k) );
          }
        }
        //  This is the subgroup of alleles which 1 interacts with and has been tested
        //  for both 1 and 2
        int num1 = interacting1.size();

        //  Make a sublist of neighbor alleles for allele2, which:
        //  1) are tested with both 1 and 2
        //  2) interact with allele 2
        ArrayList interacting2 = new ArrayList();
        HashMap typeHash2 = new HashMap();
        HashMap flagHash2 = new HashMap();
        HashMap relationHash2 = new HashMap();
        HashMap edgeHash2 = new HashMap();
        for ( int k=0; k<list2.size(); k++) {
          String allele = (String)list2.get(k);
          if ( list12.contains( allele ) && (!interacting2.contains( allele )) ) {
            interacting2.add( allele );
            typeHash2.put( allele, (String)typeList2.get(k) );
            flagHash2.put( allele, (String)flagList2.get(k) );
            relationHash2.put( allele, (String)relationList2.get(k) );
            edgeHash2.put( allele, (CyEdge)edgeList2.get(k) );
          }
        }
        //  This is the subgroup of alleles which 2 interacts with and has been tested
        //  for both 1 and 2
        int num2 = interacting2.size();

        if ( num1 == 0 | num2 == 0 | commonTests==0 ) {continue;}
        // Take only the "hub" genes
        //if ( num1 < 20 | num2 < 20 | commonTests==0 ) {continue;}

        //  Find the list of third-party alleles which interact with
        //  both 1 and 2:
        ArrayList interacting12 = intersection( interacting1, interacting2 );
        int overlap = interacting12.size();

        if ( overlap >= MIN_OVERLAP ) {

          double score = calculateScore( interacting12, typeHash1, flagHash1, 
                                         typeHash2, flagHash2, numEdgeTypes );

          double[] rScores = new double[numRandomReps];
          for(int rep=0;rep<numRandomReps;rep++) {
            // Score a random configuration
            HashMap randomTypeHash1 = getRandomTypes( interacting12, relationHash1 );
            HashMap randomTypeHash2 = getRandomTypes( interacting12, relationHash2 );
            double randomScore = calculateScore( interacting12, randomTypeHash1, flagHash1, 
                                                 randomTypeHash2, flagHash2, numEdgeTypes );
            rScores[rep] = randomScore;
          }
          double meanRS = this.mean( rScores );
          double sdRS = this.standardDeviation( rScores );
          double pValue = calculatePValue( score, meanRS, sdRS );

          if ( ( pValue >= minScore ) ) {
            String nodeName1 = 
              (String)Cytoscape.getNodeAttributeValue(allele1.getNode(),
                                                      Semantics.CANONICAL_NAME);
            String nodeName2 = 
              (String)Cytoscape.getNodeAttributeValue(allele2.getNode(),
                                                      Semantics.CANONICAL_NAME);
            //  Identify the allele forms
            String alleleForm1 = allele1.getAlleleForm();
            String alleleForm2 = allele2.getAlleleForm();
            
            // Make a list of the nearest neighbors to send to MutualInfo
            ArrayList pairList = new ArrayList();
            // Parse the keyNames down to a Node name by pulling alleleForm off
            for (int l=0; l<interacting12.size(); l++) {
              String allele = (String)interacting12.get(l);
              String name = allele.substring( allele.indexOf(divider)+1 );
              pairList.add( name );
            }
            // Make a list of the involved edges to send to MutualInfo
            ArrayList edgeList = new ArrayList( );
            Iterator ie1 = edgeHash1.keySet().iterator();
            while ( ie1.hasNext() ) {
              edgeList.add( (CyEdge)edgeHash1.get( ie1.next() ) );
            }
            Iterator ie2 = edgeHash2.keySet().iterator();
            while ( ie2.hasNext() ) {
              edgeList.add( (CyEdge)edgeHash2.get( ie2.next() ) );
            }

            MutualInfo mi = new MutualInfo( 
                                           nodeName1, alleleForm1,
                                           nodeName2, alleleForm2,
                                           pairList, edgeList,
                                           commonTests, 
                                           score, pValue, meanRS, sdRS );
            //System.out.println( nodeName1+" "+nodeName2+" "+score+" "+pValue );
            miList.add( mi );
          }// if big p-value
        }// overlap>3
      }// j allele 2
    }// i allele 1 

    MutualInfo[] pairs = (MutualInfo[])miList.toArray( new MutualInfo[ miList.size() ] );

    if ( (pairs.length == 0) && !quiet ) {  
      System.out.println("No high-scoring pairs present."); 
    }

    System.out.println(); 

    return (MutualInfo[]) pairs;
  }
  //---------------------------------------------------------------------
  /**
   *  Get a random interaction type hashmap
   */
  public HashMap getRandomTypes( ArrayList interactingSet, HashMap relationHash ) {

    HashMap randomTypeHash = new HashMap();
 
    for ( int iS=0; iS < interactingSet.size(); iS++ ) {
      String alleleString = (String)interactingSet.get(iS);
      String key = (String)relationHash.get( alleleString );
      HashMap bkgProb = (HashMap)this.bkg.get( key );
      Iterator iP = bkgProb.keySet().iterator();
      double randomNum = Math.random();
      double prevProb = 0.0;
      while (iP.hasNext() ) {
        String eType = (String)iP.next();
        double prob = ((Double)bkgProb.get( eType )).doubleValue();
        if ( (randomNum >= prevProb) && (randomNum < (prevProb+prob)) ) {
          randomTypeHash.put( alleleString, eType );
        }
        prevProb += prob;
      }
    }
     
    return randomTypeHash;
  }
  //---------------------------------------------------------------------
  /**
   *  Get a random interaction type hashmap
   */
  public HashMap getRandomTypes(Allele allele1, 
                                ArrayList interactingSet,
                                CyEdge[] edges){
    
    HashMap randomTypeHash = new HashMap();
    
    for ( int iS=0; iS < interactingSet.size(); iS++ ) {
      String alleleString = (String)interactingSet.get(iS);
      String name = alleleString.substring( alleleString.indexOf(divider)+1 );
      CyNode node = Cytoscape.getCyNode(name,false);
      if(node == null){
        throw new IllegalStateException("Node with name "+name+" does not exist in RootGraph");
      }
      if(!this.graph.containsNode(node)){
        throw new IllegalStateException("Node with name "+name+" does not exist in CyNetwork");
      }
      String allForm = alleleString.substring( 0, alleleString.indexOf(divider) );
      Allele allele = new Allele( node, allForm );
      String op2 = getSingleMutantRelation( allele, allele1, edges);
      String op1 = getSingleMutantRelation( allele1, allele, edges);

      String key = op1 + op2;
      if ( !this.bkg.containsKey( key ) ) {
        key = op2 + op1;
      }
      if ( !this.bkg.containsKey( key ) ) {
        System.out.println("No instances of "+key+" in background.");
        continue;
      }
      HashMap bkgProb = (HashMap)this.bkg.get( key );
      Iterator iP = bkgProb.keySet().iterator();
      double randomNum = Math.random();
      double prevProb = 0.0;
      while (iP.hasNext() ) {
        String eType = (String)iP.next();
        double prob = ((Double)bkgProb.get( eType )).doubleValue();
        if ( (randomNum >= prevProb) && (randomNum < (prevProb+prob)) ) {
          randomTypeHash.put( alleleString, eType );
        }
        prevProb += prob;
      }
    }
     
    return randomTypeHash;
  }
  //---------------------------------------------------------------------
  /**
   *  Get the single-mutant value for an allele1 in its experiment with allele2
   */
  public String getSingleMutantRelation( int pA, int pWT ) {
    String opA = new String();

    if ( pA > pWT ) { opA = ">"; 
    } else if ( pA == pWT ) { opA = "="; 
    } else if ( pA < pWT ) { opA = "<"; 
    }
    return opA;
  }

  //---------------------------------------------------------------------
  /**
   *  Get the single-mutant value for an allele1 in its experiment with allele2
   */
  public String getSingleMutantRelation(Allele allele1, 
                                        Allele allele2, 
                                        CyEdge[] edges){
    boolean nodeFound = false;
    int iE = -1;
    String opA = new String();
    CyEdge theEdge = edges[0];

    while ( !nodeFound ){
      iE ++;
      theEdge = edges[iE];
      if ( ( (theEdge.getSourceNode() == allele1.getNode()) && 
             (theEdge.getTargetNode() == allele2.getNode()) ) 
           ||
           ( (theEdge.getSourceNode() == allele2.getNode()) 
             && (theEdge.getTargetNode() == allele1.getNode()) ) ) {
        nodeFound = true;
      }
    }

    GeneticInteraction gi = 
      (GeneticInteraction)Cytoscape.getEdgeAttributeValue(theEdge,
                                                          GeneticInteraction.ATTRIBUTE_SELF );
    DiscretePhenoValueSet d = gi.getDiscretePhenoValueSet();

    int pA = 0;
    String a1CanonicalName =
      (String)Cytoscape.getNodeAttributeValue(allele1.getNode(), Semantics.CANONICAL_NAME);
    String edgeMutantA = 
      (String)Cytoscape.getEdgeAttributeValue(theEdge,GeneticInteraction.ATTRIBUTE_MUTANT_A);
    
    if(a1CanonicalName.compareTo(edgeMutantA) == 0){
      pA = d.getA();
    }else{
      pA = d.getB();
    }
    int pWT = d.getWT();
    
    if (pA > pWT){ 
      opA = ">"; 
    }else if(pA == pWT){ 
      opA = "="; 
    }else if(pA < pWT){ 
      opA = "<"; 
    }
    
    return opA;
  }
  //---------------------------------------------------------------------
  /**
   *  Compute Mutual Info p-value given a score and a mean and st from random scores
   *
   * MI Scores are normally distributed. Need to find the mean random score and
   * standard deviation to get a p-value for the actual score.
   * The p-value will be the 1 - the Cumulative normal distribution at score x, or
   * pVal = 1-CDF[x] = 1/2 * ( 1 - Erf[ (x-m)/(Sqrt[2]*sd) ] )
   * where m = mean and sd = standard deviation of the distribution.
   */
  public double calculatePValue( double score, double m, double sd ) {
    double pval;
    // erf approximation is really only for score > mean, but it works ok for a
    // sd below.
    if ( score < m ) {
      pval = 1.0;  // The actual values are really on [1.0, 0.5) but close enough.
    } else {
      pval = 0.5 * ( 1.0 - erf( (score-m)/(Math.sqrt(2.0)*sd) ) );
    } 
    // Take the absolute value to avoid annoying -0.0 output
    return Math.abs( -Math.log( pval )/Math.log( 10 ) );
    //return ( pval );
  }//calculatePValue
  //---------------------------------------------------------------------
  /**
   *  Compute Mutual Info score
   */
  public double calculateScore( ArrayList interacting12,
                                HashMap typeHash1, HashMap flagHash1, 
                                HashMap typeHash2, HashMap flagHash2, 
                                int numEdgeTypes ) {
     
    int overlap = interacting12.size();
    //  Find the number of each edgeType combination and build a joint probability matrix
    double[][] jointProb = new double[numEdgeTypes][numEdgeTypes];
    double[] prob1 = new double[numEdgeTypes];
    double[] prob2 = new double[numEdgeTypes];
    for (int r=0; r < jointProb.length; r++) { 
      for (int c=0; c < jointProb.length; c++) {
        jointProb[r][c] = 0.0;
      }
      prob1[r] = 0.0;
      prob2[r] = 0.0;
    }
    for (int k=0; k < interacting12.size(); k++) {
      String allele3 = (String)interacting12.get(k);
      String eType1 = (String)typeHash1.get( allele3 );
      String fType1 = (String)flagHash1.get( allele3 );
      String eType2 = (String)typeHash2.get( allele3 );
      String fType2 = (String)flagHash2.get( allele3 );
      int n1 = getTypeNumber( eType1, fType1 );
      int n2 = getTypeNumber( eType2, fType2 );
      jointProb[n1][n2] += 1.0/overlap;
      prob1[n1] += 1.0/overlap;
      prob2[n2] += 1.0/overlap;
    }

    // Now compute the score
    double score = 0;
    double log2e = 1.442695041;  // Log_2(e) to convert Ln to Log_2
        
    for (int r=0; r < jointProb.length; r++) { 
      for (int c=0; c < jointProb.length; c++) {
        double p12 = jointProb[r][c];
        double p1 = prob1[r];
        double p2 = prob2[c];
        if ( p12 != 0.0 ) {
          score += p12 * log2e * Math.log( p12/(p1*p2) );
        }
      } 
    }
    return score;
  }
  //---------------------------------------------------------------------
  /**
   *  For each node, generate a ArrayList of its Nearest Neighbors.
   */
  public HashMap buildProbDist( HashMap nodeDist ) {

    HashMap probDist = new HashMap();

    Iterator it = nodeDist.keySet().iterator();
    while (it.hasNext()) {
      String alleleName = (String)it.next();
      int[] distArray = (int[])nodeDist.get(alleleName);
      ArrayList probArray = new ArrayList();

      //  Add them up 
      double total = 0;
      for ( int k = 0; k < distArray.length; k++ ) { 
        total += distArray[k]; 
      }
      for ( int k = 0; k < distArray.length; k++ ) {
        probArray.add( new Double( distArray[k]/total ) );
      }

      probDist.put(alleleName, probArray);
    }
    return probDist;
  }//buildProbDist
  //---------------------------------------------------------------------
  /**
   *  For each node, generate a ArrayList of its Nearest Neighbors.
   */
  public HashMap buildBkg( ) {
    
    String opA = new String();
    String opB = new String();

    System.out.print("Building background data...");
    
    //  The edges
    Iterator edgesIt = this.graph.edgesIterator();
    ArrayList edgeList = new ArrayList();
    while(edgesIt.hasNext()){
      edgeList.add(edgesIt.next());
    }
    CyEdge [] edges = (CyEdge[])edgeList.toArray(new CyEdge[edgeList.size()]);
    
    /*
     *  There are six possible combinations for any allele pair with
     *  respect to the wild type: ==, >=, <=, >>, <<, ><.  How many
     *  of each is there in the data?
     */

    HashMap bkg = new HashMap();

    for (int i=0; i<edges.length; i++) {

      CyEdge theEdge = edges[i];

      String edgeType = 
        (String)Cytoscape.getEdgeAttributeValue(theEdge,
                                                GeneticInteraction.ATTRIBUTE_GENETIC_CLASS);
      GeneticInteraction gi = 
        (GeneticInteraction)Cytoscape.getEdgeAttributeValue(theEdge, 
                                                            GeneticInteraction.ATTRIBUTE_SELF);
      DiscretePhenoValueSet d = gi.getDiscretePhenoValueSet();
      
      int pWT = d.getWT();
      int pA = d.getA();
      int pB = d.getB();
      
      if ( pA > pWT ) { 
        opA = ">"; 
      } else if ( pA == pWT ) { 
        opA = "="; 
      } else if ( pA < pWT ) { 
        opA = "<"; 
      }
      if ( pB > pWT ) { 
        opB = ">"; 
      } else if ( pB == pWT ) { 
        opB = "="; 
      } else if ( pB < pWT ) { 
        opB = "<"; 
      }
      
      String key = opA + opB;
      if ( key.compareTo( ">=" ) == 0 ) {
        key = "=>";
      }
      if ( key.compareTo( "<=" ) == 0 ) {
        key = "=<";
      }
      if ( key.compareTo( "<>" ) == 0 ) {
        key = "><";
      }

      if ( bkg.containsKey( key ) ) {
        HashMap counts = (HashMap)bkg.get( key );
        if ( counts.containsKey( edgeType ) ) {
          Integer theCount = (Integer)counts.get( edgeType );
          counts.put( edgeType, new Integer( theCount.intValue() + 1 ) );
        } else {  
          counts.put( edgeType, new Integer( 1 ) );
        }
        bkg.put( key, counts );
      } else {
        HashMap counts = new HashMap();
        counts.put( edgeType, new Integer( 1 ) );
        bkg.put( key, counts );
      }
    }

    // Go through and make them fractional
    Iterator iB = (Iterator)bkg.keySet().iterator();
    while (iB.hasNext()) {
      double total = 0;
      String theKey = (String)iB.next();
      HashMap counts = (HashMap)bkg.get( theKey );
      Iterator iC = (Iterator)counts.keySet().iterator();
      while (iC.hasNext()) {
        String key = (String)iC.next();
        int num = ((Integer)counts.get( key )).intValue();
        total += num;
      }
      Iterator iD = (Iterator)counts.keySet().iterator();
      while (iD.hasNext()) {
        String key = (String)iD.next();
        int num = ((Integer)counts.get( key )).intValue();
        double pct = num/total;
        counts.put( key, new Double( pct ) );
      }
      bkg.put( theKey, counts );
    }
 
    System.out.println( "done." );

    return bkg;
  }// buildBkg
  //---------------------------------------------------------------------------
  /**
   * 
   */
  public int getTypeNumber( String edgeType, String stFlag ) {

    int intIndex = 0;
    if (edgeType.compareTo("non-interacting") == 0 ) { intIndex = 0; }
    else if (edgeType.compareTo("synthetic") == 0 ) { intIndex = 1; }
    else if (edgeType.compareTo("asynthetic") == 0 ) { intIndex = 2; }
    else if (edgeType.compareTo("suppression") == 0 ) { 
      if ( stFlag.compareTo("s") == 0 ) { intIndex = 3; } else { intIndex = 4; }
    }
    else if (edgeType.compareTo("conditional") == 0 ) { 
      if ( stFlag.compareTo("s") == 0 ) { intIndex = 5; } else { intIndex = 6; }
    }
    else if (edgeType.compareTo("epistatic") == 0 ) { 
      if ( stFlag.compareTo("s") == 0 ) { intIndex = 7; } else { intIndex = 8; };
    }
    else if (edgeType.compareTo("additive") == 0 ) { intIndex = 9; }
    else if (edgeType.compareTo("single-nonmonotonic") == 0 ) {
      if ( stFlag.compareTo("s") == 0 ) { intIndex = 10; } else { intIndex = 11; }
    }
    else if (edgeType.compareTo("double-nonmonotonic") == 0 ) { intIndex = 12; }
    return intIndex;

  }//getTypeNumber
  //---------------------------------------------------------------------------
  /**
   *  Update the alleleHash or edgeTypeHash
   */
  public HashMap updateList( String key, String item, HashMap theMap ) {

    if ( theMap.containsKey( key ) ) {
      ArrayList list = (ArrayList)theMap.get( key );
      list.add( item );
      theMap.put( key, list );
    } else {
      ArrayList list = new ArrayList();
      list.add( item );
      theMap.put( key, list );
    }
    return theMap;
  }//updateList
  //---------------------------------------------------------------------------
  // THIS METHOD IS THE SAME AS THE ONE ABOVE (?)
  /**
   *  Update the interactionEdgeHash
   */
  public HashMap updateList( String key, CyEdge item, HashMap theMap ) {

    if ( theMap.containsKey( key ) ) {
      ArrayList list = (ArrayList)theMap.get( key );
      list.add( item );
      theMap.put( key, list );
    } else {
      ArrayList list = new ArrayList();
      list.add( item );
      theMap.put( key, list );
    }
    return theMap;
  }//updateList
  //---------------------------------------------------------------------------
  /**
   * Finds the edge between two nodes within an array of edges.
   */
  public static CyEdge getCommonEdge( CyNode node1, CyNode node2, CyEdge[] edges ) {

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
  //---------------------------------------------------------------------------
  /**
   * Finds the edge between two nodes within an array of edges, taking alleleForms into
   * account.
   */
  public CyEdge getCommonEdge(Allele allele1, 
                              Allele allele2,
                              CyEdge[] edges){
    
    CyEdge theEdge = null;

    //  Cycle through the edges
    for (int i = 0; i < edges.length; i++) {
      CyEdge testEdge = edges[i];
      String edgeMutantA =
        (String)Cytoscape.getEdgeAttributeValue(testEdge,
                                                GeneticInteraction.ATTRIBUTE_MUTANT_A);
      
      CyNode nodeA = Cytoscape.getCyNode(edgeMutantA,false);
      if(nodeA == null){
        throw new IllegalStateException("Node with name " + edgeMutantA + 
                                        " does not exist in RootGraph");
      }
      if(!this.graph.containsNode(nodeA)){
        throw new IllegalStateException("Node with name " + edgeMutantA + 
                                        " does not exist in CyNetwork");
      }
      
      String edgeMutantB =
        (String)Cytoscape.getEdgeAttributeValue(testEdge,
                                                GeneticInteraction.ATTRIBUTE_MUTANT_B);
      CyNode nodeB = Cytoscape.getCyNode(edgeMutantB, false);
      if(nodeB == null){
        throw new IllegalStateException("Node with name "+edgeMutantB+
                                        " does not exist in RootGraph");
      }
      if(!this.graph.containsNode(nodeB)){
        throw new IllegalStateException("Node with name "+edgeMutantB+
                                        " does not exist in CyNetwork");
      }
      
      Allele alleleA = 
        new Allele(
          nodeA, 
          (String)Cytoscape.getEdgeAttributeValue(testEdge,
                                                  GeneticInteraction.ATTRIBUTE_ALLELE_FORM_A)
          );
      Allele alleleB = 
        new Allele(
          nodeB, 
          (String)Cytoscape.getEdgeAttributeValue(testEdge,
                                                  GeneticInteraction.ATTRIBUTE_ALLELE_FORM_B)
          );
      boolean edge12 = ( alleleA == allele1 && alleleB == allele2 );
      boolean edge21 = ( alleleA == allele2 && alleleB == allele1 );
      if ( edge12 | edge21 ) {
        theEdge = testEdge;
      }
    }
    return theEdge;
  }
  //---------------------------------------------------------------------------
  /**
   * Finds the edge between two nodes within an array of edges, taking alleleForms into
   * account.
   */
  public static CyEdge getCommonEdge( String node1, String allele1, 
                                    String node2, String allele2, 
                                    CyEdge[] edges){
    
    CyEdge theEdge = null;

    //  Cycle through the edges
    for (int i = 0; i < edges.length; i++) {
      CyEdge testEdge = edges[i];
      String nameA = 
        (String)Cytoscape.getEdgeAttributeValue(testEdge,
                                                GeneticInteraction.ATTRIBUTE_MUTANT_A);
      String nameB = 
        (String)Cytoscape.getEdgeAttributeValue(testEdge,
                                                GeneticInteraction.ATTRIBUTE_MUTANT_B);
      String alleleA = 
        (String)Cytoscape.getEdgeAttributeValue(testEdge,
                                                GeneticInteraction.ATTRIBUTE_ALLELE_FORM_A);
      String alleleB = 
        (String)Cytoscape.getEdgeAttributeValue(testEdge,
                                                GeneticInteraction.ATTRIBUTE_ALLELE_FORM_B);
      boolean edge12 = ( ( node1.compareTo(nameA) == 0 && allele1.compareTo(alleleA) == 0 ) &&
                         ( node2.compareTo(nameB) == 0 && allele2.compareTo(alleleB) == 0 ) );
      boolean edge21 = ( ( node1.compareTo(nameB) == 0 && allele1.compareTo(alleleB) == 0 ) &&
                         ( node2.compareTo(nameA) == 0 && allele2.compareTo(alleleA) == 0 ) );
      if ( edge12 | edge21 ) {
        theEdge = testEdge;
      }
    }
    return theEdge;
  }
  //---------------------------------------------------------------------------
  /**
   *  Finds a set of alleleForms for a given node -- usually one of them but maybe not
   */
  public static String[] getAlleleForms(String theNode, CyEdge[] theEdges){

    ArrayList theAlleleForms = new ArrayList();
    for (int i = 0; i < theEdges.length; i++){
      String nodeA = 
        (String)Cytoscape.getEdgeAttributeValue(theEdges[i],
                                                GeneticInteraction.ATTRIBUTE_MUTANT_A);
      String nodeB = 
        (String)Cytoscape.getEdgeAttributeValue(theEdges[i],
                                                GeneticInteraction.ATTRIBUTE_MUTANT_B);
      if ( theNode.compareTo(nodeA) == 0 ) {
        String alleleForm = 
          (String)Cytoscape.getEdgeAttributeValue(theEdges[i],
                                                  GeneticInteraction.ATTRIBUTE_ALLELE_FORM_A);
        if ( !theAlleleForms.contains( alleleForm ) ) { theAlleleForms.add( alleleForm ); }
      } else if ( theNode.compareTo(nodeB) == 0 ) {
        String alleleForm = 
          (String)Cytoscape.getEdgeAttributeValue(theEdges[i],
                                                  GeneticInteraction.ATTRIBUTE_ALLELE_FORM_B);
        if ( !theAlleleForms.contains( alleleForm ) ) { theAlleleForms.add( alleleForm ); }
      }
    }
    
    return (String[])theAlleleForms.toArray( new String[0] );
  }
  //---------------------------------------------------------------------------
  /**
   * Finds the edge between two nodes within an array of edges.
   */
  public CyEdge[] getEdgesWithAllele( String nodeName, 
                                      String theAlleleForm, 
                                      CyEdge[] edges){
    
    ArrayList edgeList = new ArrayList();
    for (int i = 0; i < edges.length; i++){
      String nodeA = 
        (String)Cytoscape.getEdgeAttributeValue(edges[i],
                                                GeneticInteraction.ATTRIBUTE_MUTANT_A);
      String nodeB = 
        (String)Cytoscape.getEdgeAttributeValue(edges[i],
                                                GeneticInteraction.ATTRIBUTE_MUTANT_B);
      String alleleFormA = 
        (String)Cytoscape.getEdgeAttributeValue(edges[i],
                                                GeneticInteraction.ATTRIBUTE_ALLELE_FORM_A);
      String alleleFormB = 
        (String)Cytoscape.getEdgeAttributeValue(edges[i],
                                                GeneticInteraction.ATTRIBUTE_ALLELE_FORM_B);
      if (( nodeName.compareTo(nodeA) == 0 ) && 
          (theAlleleForm.compareTo(alleleFormA) == 0 )){
        edgeList.add( edges[i] );
      } else if ((nodeName.compareTo(nodeB)==0) && (theAlleleForm.compareTo(alleleFormB)==0)){
        edgeList.add( edges[i] );
      }
    }
    return (CyEdge[])edgeList.toArray( new CyEdge[edgeList.size()] );
  }
  //---------------------------------------------------------------------------
  /**
   * Sets the minimum score for significance
   */
  public void setMinScore (double min_score){
    this.minScore = min_score;
  }//setMinScore
  //---------------------------------------------------------------------------
  /**
   * Sets the number of random repetitions
   */
  public void setNumRandomReps (int reps){
    this.numRandomReps = reps;
  }//set
  //---------------------------------------------------------------------------
  /**
   * Gets the minimum score for significance
   */
  public double getMinScore (){
    return this.minScore;
  }//getMinScore
  //---------------------------------------------------------------------------
  /**
   * Gets the number of random repetitions
   */
  public int getNumRandomReps (){
    return this.numRandomReps;
  }//
  //---------------------------------------------------------------------------
  /**
   * Gets an array of <code>Allele</code>s given an array of Nodes and a 
   * GraphObjAttributes.
   */
  public Allele[] getAlleles(CyNode[] nodes, CyEdge[] edges){

    ArrayList alleleList = new ArrayList();
    for(int i=0; i<nodes.length; i++) {
      CyNode node = nodes[i];
      String nodeName = 
        (String)Cytoscape.getNodeAttributeValue(node,Semantics.CANONICAL_NAME);
      String[] alleleForm = getAlleleForms(nodeName,edges);
      for(int af = 0; af < alleleForm.length; af++ ) {
        Allele newAllele = new Allele( node, alleleForm[af] );
        alleleList.add( newAllele );
      }
      System.out.print(".");
    }
    return (Allele[])alleleList.toArray( new Allele[0] );
  }//getAlleles
  //---------------------------------------------------------------------------
  public ArrayList intersection( ArrayList list1, ArrayList list2 ) {
    
    ArrayList list = new ArrayList();
    for ( int i1 = 0; i1 < list1.size(); i1++ ) {
      Object item = (Object)list1.get( i1 );
      if ( list2.contains( item ) && !list.contains(item) ) {
        list.add( item );
      }
    }
    return list;
  }//intersection

  //---------------------------------------------------------------------------
  public double mean( double[] list ) {
    double m = 0;
    for(int i=0; i<list.length; i++){
      m += list[i];
    }
    return m/list.length;
  }//mean
  //---------------------------------------------------------------------------
  public double standardDeviation( double[] list ) {
    double s = 0;
    double m = mean( list );
    for(int i=0; i<list.length; i++){
      s += Math.pow( ( list[i] - m ), 2);
    }
    s = Math.sqrt( s/(list.length-1) );
    return s;
  }//standardDeviation
  //---------------------------------------------------------------------------
  public double erf( double x ) {
    double a1 = 0.254829592;
    double a2 = -0.284496736;
    double a3 = 1.421413741;
    double a4 = -1.453152027;
    double a5 = 1.061405429;
    double p = 0.3275911;
    double t = 1.0/( 1.0 + p*x );
    double ex = 
      1.0 - 
      Math.exp(-Math.pow(x,2))*( a1*t + a2*Math.pow(t,2) +
                                 a3*Math.pow(t,3) + 
                                 a4*Math.pow(t,4) + 
                                 a5*Math.pow(t,5) );
    return ex;
  }

  //---------------------------------------------------------------------------
  class Allele {
    CyNode   node;
    String alleleForm;
    public Allele( CyNode node, String allele) {
      setNode(node);
      setAlleleForm(allele);
    }// Allele constructor
    public void setNode( CyNode node ) { this.node = node; }
    public CyNode getNode() { return this.node; }
    public void setAlleleForm( String alleleForm ) { this.alleleForm = alleleForm; }
    public String getAlleleForm() { return this.alleleForm; }
  }//class Allele

}
//---------------------------------------------------------------------
/**
 *  Update the probDist hashmap given new typeHashs for interacting12 alleles.
 */

/* Old, no longer used
   public HashMap updateProbDist( HashMap nodeDist, HashMap newProbDist, 
   Allele allele1, Allele allele2, 
   HashMap nTH1, HashMap nTH2, HashMap tH1, HashMap tH2,
   HashMap fH1, HashMap fH2 ) {

   String distKey1 = allele1.getAlleleForm() + divider + 
   this.cytoscapeWindow.getCanonicalNodeName( allele1.getNode() );
   ArrayList probArray1 = (ArrayList)newProbDist.get( distKey1 );
   double total1 = 0.0;
   int[] oldArray1 = (int[])nodeDist.get( distKey1 );
   for(int iT=0; iT<oldArray1.length; iT++ ) { total1 += oldArray1[iT]; }
   Iterator i1 = nTH1.keySet().iterator();
   while (i1.hasNext()) {
   String theTarget = (String)i1.next();
   String newType = (String)nTH1.get( theTarget ); 
   String oldType = (String)tH1.get( theTarget );
   if (oldType.compareTo(newType) != 0 ) {
   String theFlag = (String)fH1.get( theTarget ); 
   int newIndex = getTypeNumber( newType, theFlag );
   int oldIndex = getTypeNumber( oldType, theFlag );
   Double upval = (Double)probArray1.get(newIndex);
   probArray1.set(newIndex, new Double( upval.doubleValue() + (1.0/total1) ) ); 
   Double downval = (Double)probArray1.get(oldIndex);
   probArray1.set(oldIndex, new Double( downval.doubleValue() - (1.0/total1) ) ); 
   }
   }
   newProbDist.put( distKey1, probArray1 );

   String distKey2 = allele2.getAlleleForm() + divider + 
   this.cytoscapeWindow.getCanonicalNodeName( allele2.getNode() );
   ArrayList probArray2 = (ArrayList)newProbDist.get( distKey2 );
   double total2 = 0.0;
   int[] oldArray2 = (int[])nodeDist.get( distKey2 );
   for(int iT=0; iT<oldArray2.length; iT++ ) { total2 += oldArray2[iT]; }
   Iterator i2 = nTH2.keySet().iterator();
   while (i2.hasNext()) {
   String theTarget = (String)i2.next();
   String newType = (String)nTH2.get( theTarget ); 
   String oldType = (String)tH2.get( theTarget );
   if (oldType.compareTo(newType) != 0 ) {
   String theFlag = (String)fH2.get( theTarget ); 
   int newIndex = getTypeNumber( newType, theFlag );
   int oldIndex = getTypeNumber( oldType, theFlag );
   Double upval = (Double)probArray2.get(newIndex);
   probArray2.set(newIndex, new Double( upval.doubleValue() + (1.0/total2) ) ); 
   Double downval = (Double)probArray2.get(oldIndex);
   probArray2.set(oldIndex, new Double( downval.doubleValue() - (1.0/total2) ) ); 
   }
   }
   newProbDist.put( distKey2, probArray2 );
  
   return newProbDist;
   }
*/

//---------------------------------------------------------------------
/**
 *
 *  Get the single-mutant value for an allele
 *
 **/

/*
  public String getSingleMutantRelation( Allele allele, Edge[] edges,
  GraphObjAttributes edgeAttributes ) {
  boolean nodeFound = false;
  int iE = -1;
  String opA = new String();
  Edge theEdge = edges[0];

  while ( !nodeFound ){
  iE ++;
  theEdge = edges[iE];
  if ( ( theEdge.source() == allele.getNode() ) ||
  ( theEdge.target() == allele.getNode() ) ) {
  nodeFound = true;
  }
  }
  HashMap edgeHash = edgeAttributes.getAttributes( edgeAttributes.getCanonicalName( theEdge ) );
  GeneticInteraction gi = (GeneticInteraction)edgeHash.get( GeneticInteraction.ATTRIBUTE_SELF );
  DiscretePhenoValueSet d = gi.getDiscretePhenoValueSet();

  int pA = 0;
  if ( (allele.getNode().toString()).compareTo( (String)edgeHash.get("A") ) == 0 ) {
  pA = d.getA();
  } else {
  pA = d.getB();
  }
  int pWT = d.getWT();

  if ( pA > pWT ) { opA = ">"; 
  } else if ( pA == pWT ) { opA = "="; 
  } else if ( pA < pWT ) { opA = "<"; 
  }

  return opA;
  }


  //---------------------------------------------------------------------
  /**
  *
  *  Compute Mutual Info score
  *
  **/

/*
  public double calculateScore( Allele allele1, Allele allele2, ArrayList interacting12,
  HashMap typeHash1, HashMap flagHash1, 
  HashMap typeHash2, HashMap flagHash2, 
  HashMap pDist, int numEdgeTypes ) {

     
  int overlap = interacting12.size();
  //  Find the number of each edgeType combination and build a joint probability matrix
  double[][] jointProb = new double[numEdgeTypes][numEdgeTypes];
  double[] prob1 = new double[numEdgeTypes];
  double[] prob2 = new double[numEdgeTypes];
  for (int r=0; r < jointProb.length; r++) { for (int c=0; c < jointProb.length; c++) {
  jointProb[r][c] = 0.0;
  }
  prob1[r] = 0.0;
  prob2[r] = 0.0;
  }
  for (int k=0; k < interacting12.size(); k++) {
  String allele3 = (String)interacting12.get(k);
  String eType1 = (String)typeHash1.get( allele3 );
  String fType1 = (String)flagHash1.get( allele3 );
  String eType2 = (String)typeHash2.get( allele3 );
  String fType2 = (String)flagHash2.get( allele3 );
  int n1 = getTypeNumber( eType1, fType1 );
  int n2 = getTypeNumber( eType2, fType2 );
  jointProb[n1][n2] += 1.0/overlap;
  prob1[n1] += 1.0/overlap;
  prob2[n2] += 1.0/overlap;
  }

  // Now compute the score
	double score = 0;
  double log2e = 1.442695041;  // Log_2(e) to convert Ln to Log_2

  /*ArrayList probArray1 = (ArrayList)pDist.get( allele1.getAlleleForm() + divider +
  this.cytoscapeWindow.getCanonicalNodeName( allele1.getNode() ) );
  ArrayList probArray2 = (ArrayList)pDist.get( allele2.getAlleleForm() + divider +
  this.cytoscapeWindow.getCanonicalNodeName( allele2.getNode() ) );
*/
        
/*
  for (int r=0; r < jointProb.length; r++) { 
  for (int c=0; c < jointProb.length; c++) {
  double p12 = jointProb[r][c];
  //double p1 = ((Double)probArray1.get(r)).doubleValue();
  //double p2 = ((Double)probArray2.get(c)).doubleValue();
  double p1 = prob1[r];
  double p2 = prob2[c];
  if ( p12 != 0.0 ) {
  score += p12 * log2e * Math.log( p12/(p1*p2) );
  }
  } 
	}
  return score;

  }

*/
