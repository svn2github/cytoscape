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
 * @author Iliana Avila (refactored)
 * @version 2.0
 */
package phenotypeGenetics;

import phenotypeGenetics.ui.*;
import phenotypeGenetics.action.*;
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
import phenotypeGenetics.Utilities;

public class MutualInfoCalculator{

  protected static final String divider = "~";
  protected static final double DEFAULT_MINSCORE = 3.0;
  protected static final int DEFAULT_NUMRANDOMREPS = 100;
  protected static final int MIN_OVERLAP = 5;
  
  /**
   * The minimum score for signficance
   */
  protected static double minScore = DEFAULT_MINSCORE;	
  

  /**
   * The number of randomizations for calculation of p-value
   */
  protected static int numRandomReps = DEFAULT_NUMRANDOMREPS;
  
  /**
   * The background probabilities for allele pair types
   */
  protected static HashMap bkg;
 
  /**
   * Sole constructor
   */
  public MutualInfoCalculator (){}//MutualInfoCalculator

  /**
   * @param cy_net the CyNetwork on which to generate mutual information
   * @param task_progress the TaskProgress that this method uses to keep
   * track of the progress of this algorithm
   * @see cytoscape.utils.CytoscapeProgressMonitor
   */
  public static MutualInfo[] findMutualInformation (CyNetwork cy_net, 
                                                    TaskProgress task_progress) {
    
    // get the edges
    Iterator edgesIt = cy_net.edgesIterator();
    ArrayList edgeList = new ArrayList();
    while(edgesIt.hasNext()){
      edgeList.add(edgesIt.next());
    }
    CyEdge [] edges = (CyEdge[])edgeList.toArray(new CyEdge[edgeList.size()]);
    
    //  get the nodes
    Iterator nodesIt = cy_net.nodesIterator();
    ArrayList nodeList = new ArrayList();
    while(nodesIt.hasNext()){
      nodeList.add(nodesIt.next());
    }
    CyNode[] nodes = (CyNode[])nodeList.toArray(new CyNode[nodeList.size()]);
    
    // we're going to select some nodes, so deselect currently selected ones
    CyNetworkView netView = Cytoscape.getNetworkView(cy_net.getIdentifier());
    if(netView != null){
      List selectedNodeViews = netView.getSelectedNodes();
      if(selectedNodeViews != null){
        for(Iterator it = selectedNodeViews.iterator(); it.hasNext();){
          CyNodeView nodeView = (CyNodeView)it.next();
          nodeView.setSelected(false);
        }
      }
    }
    
    // Set up the command line output
    System.out.print( "Minimum score = " + MutualInfoCalculator.minScore + 
                      ".  Calculating." );

    // Put selected node pairs in a MutualInfo
    MutualInfo[] pairs = getPairs( cy_net, nodes, edges, 
                                   MutualInfoCalculator.minScore, 
                                   false, task_progress );
    
    return (MutualInfo[]) pairs;
  }//findMutualInformation
  
  /**
   *  For each pair of <code>Node</code>s in array nodes, work with given 
   *  <code>Edge</code>s to generate an array of <code>MutualInfo[]</code> of 
   *  mutual information
   */
  public static MutualInfo[] getPairs(CyNetwork cy_net, 
                                      CyNode[] nodes, 
                                      CyEdge[] edges, 
                                      double minScore,
                                      boolean quiet,
                                      TaskProgress task_progress){

    task_progress.message = "Initializing...";
    task_progress.taskName = "Mutual Information Calculation";

    int numEdgeTypes = 0;
        
    if (!quiet) { System.out.println(); System.out.print( "Finding alleles..." ); }
    
    if(MutualInfoCalculator.bkg == null){
      MutualInfoCalculator.bkg = buildBkg(cy_net);
    }

    // build an array of alleles which will be cycled over later
    Allele[] alleles = getAlleles( nodes, edges);
    if (!quiet) { System.out.print( alleles.length + " alleles found."); }
    
    //Get interaction class distributions
    HashMap nodeDist = NodalDistributionAnalyzer.calculateNodeDistribution(cy_net,false);
    
    TableDialog td = new TableDialog(nodeDist);
    TableDialog.TableDialogModel tdm = td.makeTableDialogModel(nodeDist);
    
    // Turn them into probabilities
    HashMap probDist = buildProbDist( tdm );
    
    // Get the list of edge types
    numEdgeTypes = tdm.getColumnCount() - 2;
    ArrayList edgeTypes = new ArrayList();
    for (int iE = 1; iE < tdm.getColumnCount()-1; iE++ ){
      edgeTypes.add( tdm.getColumnNameNoTotal( iE ) );
    }//iE

    // A place to put important MutualInfos when we find them
    ArrayList miList = new ArrayList();

    // A hashmap keyed on alleles, pointing to an ArrayList of neighbor alleles
    HashMap alleleHash = new HashMap();
  
    // A hashmap keyed on alleles, pointing to an ArrayList of interaction types
    // for the edge which connects to the corresponding neighbor allele in alleleHash.
    HashMap edgeTypeHash = new HashMap();
    
    // A hashmap keyed on alleles, pointing to an ArrayList of flags identifying
    // the allele as a source ("s") or target ("t")
    // This is only here to keep track for the background randomizations.
    HashMap edgeFlagHash = new HashMap();
    
    // A hashmap keyed on alleles, pointing to an ArrayList of relations identifying
    // the allele as having a single-mutant phenotype >, <, or = to WT
    HashMap edgeRelationHash = new HashMap();

    //  Cycle over all edges, find connecting nodes A and B with alleleForms
    //  alleleformA and alleleformB, then put results into HashMaps
    
    if(task_progress.taskLength == 0){
      task_progress.taskLength = getLengthOfTask(cy_net);
    }
    task_progress.currentProgress = 0;
    
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
                                                GeneticInteraction.ATTRIBUTE_ALLELE_FORM_B) 
        + divider + 
        (String)Cytoscape.getEdgeAttributeValue(theEdge,
                                                GeneticInteraction.ATTRIBUTE_MUTANT_B);
      
      // If necessary, find out which is the source and which is the target
      Mode mode = (Mode)Mode.modeNameToMode.get(edgeType);
      if(mode == null){
        throw new IllegalStateException("edgeType " + edgeType + " had no Mode!");
      }
    
      String edgeTypeA = edgeType;
      String edgeTypeB = edgeType;
      String flagA = new String();
      String flagB = new String();
      
      if(mode.isDirectional()){
        String nodeA_id = 
          (String)Cytoscape.getEdgeAttributeValue(theEdge,
                                                  GeneticInteraction.ATTRIBUTE_MUTANT_A);
        // the 2nd argument tells it to not create the node if it is not there
        // note that the returned node may belongs to the RootGraph, but not necessarily
        // to this CyNetwork
        CyNode nodeA = Cytoscape.getCyNode(nodeA_id, false);
        if(nodeA == null){
          // Figure out what to do here, for now just throw exception
          throw new IllegalStateException("Node with identifier " +
                                          nodeA_id + " does not exist in RootGraph!");
        }
        // we know that it is in RootGraph, now lets make sure that it is in this CyNetwork
        if(!cy_net.containsNode(nodeA)){
          //Figure out what to do here, for now just throw exception
          throw new IllegalStateException("Node with identifier " +
                                          nodeA_id + " does not exist in CyNetwork!");
        }
        
        if ( nodeA == theEdge.getSourceNode() ) {
          edgeTypeA = edgeType + "+";
          edgeTypeB = edgeType + "-";
          flagA = "s";
          flagB = "t";
        } else {
          edgeTypeA = edgeType + "-";
          edgeTypeB = edgeType + "+";
          flagA = "t";
          flagB = "s";
        } 
      }//isDirectional

      // Get the single-mutants relative to wild type
      GeneticInteraction gi = 
       (GeneticInteraction)Cytoscape.getEdgeAttributeValue(theEdge,
                                                           GeneticInteraction.ATTRIBUTE_SELF);
      
      DiscretePhenoValueInequality d = gi.getDiscretePhenoValueInequality();

      int pA = d.getA();
      int pB = d.getB();
      int pWT = d.getWT();
  
      String opA = getSingleMutantRelation(pA, pWT);
      String opB = getSingleMutantRelation(pB, pWT);

      String relations = opA + opB;
      if ( !MutualInfoCalculator.bkg.containsKey( relations ) ) {
        relations = opB + opA;
      }
      if ( !MutualInfoCalculator.bkg.containsKey( relations ) ) {
        System.out.println("No instances of "+relations+" in background.");
      }

      // put B in A's list of fellow alleles
      alleleHash = updateList( aKeyA, aKeyB, alleleHash );
      
      // put A in B's list of fellow alleles
      alleleHash = updateList( aKeyB, aKeyA, alleleHash );
      
      // Put the edgeType in lists for A and B
      edgeTypeHash = updateList( aKeyA, edgeTypeA, edgeTypeHash );
      edgeTypeHash = updateList( aKeyB, edgeTypeB, edgeTypeHash );
      
      // Put the flags in lists for A and B
      edgeFlagHash = updateList( aKeyA, flagA, edgeFlagHash );
      edgeFlagHash = updateList( aKeyB, flagB, edgeFlagHash );
      
      // Put the relations in lists for A and B
      edgeRelationHash = updateList( aKeyA, relations, edgeRelationHash );
      edgeRelationHash = updateList( aKeyB, relations, edgeRelationHash );
      
      if( (edges.length % 100) == 0){
        task_progress.currentProgress++;
        int percent = (int)((task_progress.currentProgress*100)/task_progress.taskLength);
        task_progress.message =  "<html>Mutual Information:<br>Completed " 
          + Integer.toString(percent) + "%</html>";
      }
          
    }// i, the edge at hand

    if (!quiet) {System.out.println( "Done.  ");}

    //  Now cycle over all alleles and, drawing from the HashMaps we have just made,
    //  make a list of nearest-neighbor alleles for each allele.
    for (int i = 0; i < alleles.length; i++) { 
      System.out.print(".");   // Let the user know something's going on
      Allele allele1 = alleles[i];
      
      //  Find the allele's info to make a key
      String aKey1 = allele1.getAlleleForm() + divider +
        Cytoscape.getNodeAttributeValue(allele1.getNode(),Semantics.CANONICAL_NAME);
      
      //  For this allele, get the list of alleles and interactions to those 
      //  alleles
      ArrayList list1 = (ArrayList)alleleHash.get( aKey1 );
      ArrayList typeList1 = (ArrayList)edgeTypeHash.get( aKey1 );
      ArrayList flagList1 = (ArrayList)edgeFlagHash.get( aKey1 );
      ArrayList relationList1 = (ArrayList)edgeRelationHash.get( aKey1 );
     
      //  Now cycle over all possible neighbors
      for (int j = i+1; j < alleles.length; j++) {
        Allele allele2 = alleles[j];

        //  Find the allele's info to make a key
        String aKey2 = allele2.getAlleleForm() + divider +
          Cytoscape.getNodeAttributeValue(allele2.getNode(), Semantics.CANONICAL_NAME);
        
        //  For this allele, get the list of alleles and interactions to those 
        //  alleles
        ArrayList list2 = (ArrayList)alleleHash.get( aKey2 );
        ArrayList typeList2 = (ArrayList)edgeTypeHash.get( aKey2 );
        ArrayList flagList2 = (ArrayList)edgeFlagHash.get( aKey2 );
        ArrayList relationList2 = (ArrayList)edgeRelationHash.get( aKey2 );
    
        if ( list1==null | list2==null ) {continue;}

        //  The intersection of these lists gives a set of third-party alleles which
        //  have been tested with alleles 1 and 2
        ArrayList list12 = Utilities.intersection( list1, list2 );
        int commonTests = list12.size();

        //  Make a sublist of neighbor alleles for allele1, which:
        //  1) are tested with both 1 and 2
        //  2) interact with allele 1
        ArrayList interacting1 = new ArrayList();
        HashMap typeHash1 = new HashMap();
        HashMap flagHash1 = new HashMap();
        HashMap relationHash1 = new HashMap();
        for ( int k=0; k<list1.size(); k++) {
          String allele = (String)list1.get(k);
          if ( list12.contains( allele ) && (!interacting1.contains( allele )) ) {
            interacting1.add( allele );
            typeHash1.put( allele, (String)typeList1.get(k) );
            flagHash1.put( allele, (String)flagList1.get(k) );
            relationHash1.put( allele, (String)relationList1.get(k) );
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
        for ( int k=0; k<list2.size(); k++) {
          String allele = (String)list2.get(k);
          if ( list12.contains( allele ) && (!interacting2.contains( allele )) ) {
            interacting2.add( allele );
            typeHash2.put( allele, (String)typeList2.get(k) );
            flagHash2.put( allele, (String)flagList2.get(k) );
            relationHash2.put( allele, (String)relationList2.get(k) );
          }
        }
        
        //  This is the subgroup of alleles which 2 interacts with and has been tested
        //  for both 1 and 2
        int num2 = interacting2.size();

        if ( num1 == 0 | num2 == 0 | commonTests==0 ) {continue;}

        //  Find the list of third-party alleles which interact with
        //  both 1 and 2:
        ArrayList interacting12 = Utilities.intersection( interacting1, interacting2 );
        int overlap = interacting12.size();

        if ( overlap >= MIN_OVERLAP ) {

          double score = calculateScore( interacting12, typeHash1, flagHash1, 
                                         typeHash2, flagHash2, edgeTypes );
          
          double[] rScores = new double[numRandomReps];
          for(int rep=0;rep<numRandomReps;rep++) {
            // Score a random configuration
            HashMap randomTypeHash1 = getRandomTypes( interacting12, 
                                                      relationHash1, 
                                                      flagHash1 );
            HashMap randomTypeHash2 = getRandomTypes( interacting12, 
                                                      relationHash2, 
                                                      flagHash2 );
            double randomScore = calculateScore( interacting12, randomTypeHash1, flagHash1, 
                                                 randomTypeHash2, flagHash2, edgeTypes );
            rScores[rep] = randomScore;
          }
          double meanRS = Utilities.mean( rScores );
          double sdRS = Utilities.standardDeviation( rScores );
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
            MutualInfo mi = new MutualInfo( 
                                           nodeName1, alleleForm1,
                                           nodeName2, alleleForm2,
                                           pairList,
                                           commonTests,
                                           score, pValue, meanRS, sdRS );
            //System.out.println( nodeName1+" "+nodeName2+" "+score+" "+pValue );
            miList.add( mi );
          }// if big p-value
        }// overlap>3
      }// j allele 2
      
      task_progress.currentProgress++;
      int percent = (int)((task_progress.currentProgress * 100)/task_progress.taskLength);
      task_progress.message = "<html>Mutual Information:<br>Completed " 
        + Integer.toString(percent) + "%</html>";
      //System.out.println(task_progress.message);
    
    }// i allele 1 

    MutualInfo[] pairs = (MutualInfo[])miList.toArray( new MutualInfo[ miList.size() ] );

    if ( (pairs.length == 0) && !quiet ) {  
      System.out.println("No high-scoring pairs present."); 
    }

    System.out.println(); 
    
    task_progress.done = true;
    
    return pairs;
  }//getPairs

  /**
   *  Get a random interaction type hashmap
   */
  protected static HashMap getRandomTypes( ArrayList interactingSet, 
                                           HashMap relationHash, 
                                           HashMap flagHash ){
    
    HashMap randomTypeHash = new HashMap();
    for ( int iS=0; iS < interactingSet.size(); iS++ ) {
      String alleleString = (String)interactingSet.get(iS);
      String key = (String)relationHash.get( alleleString );
      HashMap bkgProb = (HashMap)MutualInfoCalculator.bkg.get( key );
      Iterator iP = bkgProb.keySet().iterator();
      double randomNum = Math.random();
      double prevProb = 0.0;
      while (iP.hasNext() ) {
        String eType = (String)iP.next();
        Mode mode = (Mode)Mode.modeNameToMode.get(eType);
        double prob = ((Double)bkgProb.get( eType )).doubleValue();
        if ( mode.isDirectional() ) { 
          String flag = (String)flagHash.get( alleleString );
          if ( flag.compareTo("s") == 0 ) {
            eType += "+";
          } else {
            eType += "-";
          }
        }
        if ( (randomNum >= prevProb) && (randomNum < (prevProb+prob)) ) {
          randomTypeHash.put( alleleString, eType );
        }
        prevProb += prob;
      }
    }
     
    return randomTypeHash;
  }//getRandomTypes
  
  /**
   * Get the single-mutant value for an allele1 in its experiment with allele2
   */
  protected static String getSingleMutantRelation (int pA, int pWT){
    String opA = new String();

    if(pA > pWT){ 
      opA = ">"; 
    }else if(pA == pWT){ 
      opA = "="; 
    }else if(pA < pWT){ 
      opA = "<"; 
    }
    return opA;
  }//getSingleMutantRelation

  /**
   *  Get the single-mutant value for an allele1 in its experiment with allele2
   */
  protected static String getSingleMutantRelation (Allele allele1, Allele allele2, 
                                                   CyEdge[] edges){
    boolean nodeFound = false;
    int iE = -1;
    String opA = new String();
    CyEdge theEdge = edges[0];
    
    while ( !nodeFound ){
      iE ++;
      theEdge = edges[iE];
      if ( ( (theEdge.getSourceNode() == allele1.getNode()) && 
             (theEdge.getTargetNode() == allele2.getNode()) ) ||
           ( (theEdge.getSourceNode() == allele2.getNode()) && 
             (theEdge.getTargetNode() == allele1.getNode()) ) ) {
        nodeFound = true;
      }
    }
    
    GeneticInteraction gi = 
      (GeneticInteraction)Cytoscape.getEdgeAttributeValue(theEdge, 
                                                          GeneticInteraction.ATTRIBUTE_SELF);
    DiscretePhenoValueInequality d = gi.getDiscretePhenoValueInequality();
    
    int pA = 0;
    String allele1Name = (String)Cytoscape.getNodeAttributeValue(allele1.getNode(),
                                                                 Semantics.CANONICAL_NAME
                                                                 );
    String mutant1Name = 
      (String)Cytoscape.getEdgeAttributeValue(theEdge,
                                              GeneticInteraction.ATTRIBUTE_MUTANT_A
                                              );
    if(allele1Name.compareTo(mutant1Name ) == 0 ){
      pA = d.getA();
    }else{
      pA = d.getB();
    }
    int pWT = d.getWT();
    
    if(pA > pWT){ 
      opA = ">"; 
    }else if(pA == pWT){ 
      opA = "="; 
    }else if(pA < pWT){ 
      opA = "<"; 
    }
    
    return opA;
  }//getSingleMutantRelation

  /**
   * Compute Mutual Info p-value given a score and a mean and st from random scores
   *
   * MI Scores are normally distributed. Need to find the mean random score and
   * standard deviation to get a p-value for the actual score.
   * The p-value will be the 1 - the Cumulative normal distribution at score x, or
   * pVal = 1-CDF[x] = 1/2 * ( 1 - Erf[ (x-m)/(Sqrt[2]*sd) ] )
   * where m = mean and sd = standard deviation of the distribution.
   */
  protected static double calculatePValue (double score, double m, double sd){
    double pval;
    // erf approximation is really only for score > mean, but it works ok for a
    // sd below.
    if(score < m){
      // The actual values are really on [1.0, 0.5) but close enough.
      pval = 1.0;  
    }else{
      pval = 0.5 * ( 1.0 - erf( (score-m)/(Math.sqrt(2.0)*sd) ) );
    } 
    // Take the absolute value to avoid annoying -0.0 output
    return Math.abs( -Math.log( pval )/Math.log( 10 ) );
  }//calculatePValue
  
  /**
   *  Compute Mutual Info score
   */
  protected static double calculateScore( ArrayList interacting12,
                                          HashMap typeHash1, HashMap flagHash1, 
                                          HashMap typeHash2, HashMap flagHash2, 
                                          ArrayList edgeTypes ) {
    
     
    int overlap = interacting12.size();
    int numEdgeTypes = edgeTypes.size();
    
    //  Find the number of each edgeType combination and build a joint probability matrix
    double[][] jointProb = new double[numEdgeTypes][numEdgeTypes];
    double[] prob1 = new double[numEdgeTypes];
    double[] prob2 = new double[numEdgeTypes];
    
    //  Initalize probabilities
    for(int r = 0; r < jointProb.length; r++){ 
      for(int c = 0; c < jointProb.length; c++){
        jointProb[r][c] = 0.0;
      }//for c
      prob1[r] = 0.0;
      prob2[r] = 0.0;
    }//for r
    for(int k = 0; k < interacting12.size(); k++){
      String allele3 = (String)interacting12.get(k);
      String eType1 = (String)typeHash1.get( allele3 );
      String eType2 = (String)typeHash2.get( allele3 );
      int n1 = edgeTypes.indexOf( eType1 );
      int n2 = edgeTypes.indexOf( eType2 );
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
  }//calculateScore

  /**
   * For each node, generate a HashMap of probabilities
   */
  protected static HashMap buildProbDist (TableDialog.TableDialogModel tdm){
    
    HashMap probDist = new HashMap();
    
    int numCols = tdm.getColumnCount();
    int numRows = tdm.getRowCount();
    
    for( int r = 0; r < numRows; r++ ) {
      String alleleName = (String)tdm.getValueAt( r, 0 );
      HashMap probHash = new HashMap();
      double total = ((Integer)tdm.getValueAt( r, numCols-1 )).doubleValue();
      for( int c = 1; c < numCols-1; c++ ) {
        String intType = (String)tdm.getColumnNameNoTotal( c );
        double num = ((Integer)tdm.getValueAt( r,c )).doubleValue();
        Double prob = new Double( num/total );
        probHash.put( intType, prob );
      }//c
      probDist.put( alleleName, probHash );
    }//r
    
    return probDist;
  }//buildProbDist

  /**
   *  Compute background probabilities of interction types given two single mutants.
   */
  protected static HashMap buildBkg (CyNetwork cy_net){

    String opA = new String();
    String opB = new String();
    
    System.out.print("Building background data...");
    
    /*
     *  There are six possible combinations for any allele pair with
     *  respect to the wild type: ==, >=, <=, >>, <<, ><.  How many
     *  of each is there in the data?
     */

    HashMap bkg = new HashMap();
    Iterator it = cy_net.edgesList().iterator();
    while(it.hasNext()) {

      CyEdge theEdge = (CyEdge)it.next();

      String edgeType =
        (String)Cytoscape.getEdgeAttributeValue(theEdge,
                                                GeneticInteraction.ATTRIBUTE_GENETIC_CLASS);
      GeneticInteraction gi = 
      (GeneticInteraction)Cytoscape.getEdgeAttributeValue(theEdge,
                                                          GeneticInteraction.ATTRIBUTE_SELF);
      DiscretePhenoValueInequality d = gi.getDiscretePhenoValueInequality();
      
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

  /**
   *  Update the alleleHash or edgeTypeHash
   */
  protected static HashMap updateList (String key, String item, HashMap theMap){
    
    if (theMap.containsKey( key )){
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
  
  /**
   * Sets the minimum score for significance
   */
  public static void setMinScore (double min_score){
    MutualInfoCalculator.minScore = min_score;
  }//setMinScore

  /**
   * Sets the number of random repetitions
   */
  public static void setNumRandomReps (int reps){
    MutualInfoCalculator.numRandomReps = reps;
  }//setNumRandomReps

  /**
   * Gets the minimum score for significance
   */
  public static double getMinScore (){
    return MutualInfoCalculator.minScore;
  }//getMinScore

  /**
   * Gets the number of random repetitions
   */
  public static int getNumRandomReps (){
    return MutualInfoCalculator.numRandomReps;
  }//getNumRandomReps

  /**
   * Estimates how many units it will take to calculate mutual information
   * for the given network, used for progress monitors
   *
   * @see cytoscape.util.CytoscapeProgressMonitor
   */
  public static int getLengthOfTask (CyNetwork cy_net){
    // get the edges
    Iterator edgesIt = cy_net.edgesIterator();
    ArrayList edgeList = new ArrayList();
    while(edgesIt.hasNext()){
      edgeList.add(edgesIt.next());
    }
    CyEdge [] edges = (CyEdge[])edgeList.toArray(new CyEdge[edgeList.size()]);
    
    //  get the nodes
    Iterator nodesIt = cy_net.nodesIterator();
    ArrayList nodeList = new ArrayList();
    while(nodesIt.hasNext()){
      nodeList.add(nodesIt.next());
    }
    CyNode[] nodes = (CyNode[])nodeList.toArray(new CyNode[nodeList.size()]);
   
    Allele [] alleles = getAlleles(nodes,edges); 
    
    int l = alleles.length + edges.length/100;
    return l;
  }//getLengthOfTask
  
  /**
   * Gets an array of <code>Allele</code>s given an array of Nodes and a 
   * GraphObjAttributes.
   */
  public static Allele[] getAlleles(CyNode[] nodes, CyEdge[] edges){

    ArrayList alleleList = new ArrayList();
    for(int i=0; i<nodes.length; i++) {
      CyNode node = nodes[i];
      String nodeName = 
        (String)Cytoscape.getNodeAttributeValue(node,Semantics.CANONICAL_NAME);
      String[] alleleForm = Utilities.getAlleleForms(nodeName,edges);
      for(int af = 0; af < alleleForm.length; af++ ) {
        Allele newAllele = new Allele( node, alleleForm[af] );
        alleleList.add( newAllele );
      }
      System.out.print(".");
    }
    return (Allele[])alleleList.toArray( new Allele[0] );
  }//getAlleles
 
  /**
   * @return ???
   */
  public static double erf ( double x ){
    double a1 = 0.254829592;
    double a2 = -0.284496736;
    double a3 = 1.421413741;
    double a4 = -1.453152027;
    double a5 = 1.061405429;
    double p = 0.3275911;
    double t = 1.0/( 1.0 + p*x );
    double ex = 1.0 - Math.exp(-Math.pow(x,2))*( a1*t + a2*Math.pow(t,2) + 
                                                 a3*Math.pow(t,3) + a4*Math.pow(t,4) + 
                                                 a5*Math.pow(t,5) );
    return ex;
  }//erf

  // ------------------- Internal classes -----------------------//
  
  static protected class Allele {
    protected CyNode node;
    protected String alleleForm;
    
    public Allele(CyNode node, String allele) {
      setNode(node);
      setAlleleForm(allele);
    }// Allele constructor
    
    public void setNode( CyNode node ) { this.node = node; }
    
    public CyNode getNode() { return this.node; }
    
    public void setAlleleForm( String alleleForm ) { this.alleleForm = alleleForm; }
    
    public String getAlleleForm() { return this.alleleForm; }
  }//internal class Allele
  
}//class MutualInfoCalculator

