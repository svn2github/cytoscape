// ActivePath.java
//---------------------------------------------------------------------------------
// $Revision: 10563 $   
// $Date: 2007-06-21 15:12:11 -0700 (Thu, 21 Jun 2007) $ 
// $Author: slotia $
//-----------------------------------------------------------------------------------
package csplugins.jActiveModules;
//-----------------------------------------------------------------------------------
import giny.model.GraphPerspective;
import giny.model.Node;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
//-----------------------------------------------------------------------------------
/**
 * This object represent a set of connected nodes that we wish to score for signficance.
 * Although at no point in this class is it verified that the nodes passed into the 
 * constructors are actually connected. One important thing to note about this class
 * is that it only recalculates the score if its state has changed. If you make some change
 * to this class where the state changes, make sure to set scored=false (see code for
 * example of this situation. It implements comparable, it's sexy like that.
 */
public class Component implements Comparable{
  /**
   *The score of this component
   */
  double score = 0.0;
  /**
   *the nodes in this component
   */
  Vector nodes;
  /**
   * The nodes which will be returned for display purposes (due to 
   * regional scoring, this may be different than the actual node
   * set used for scoring
   */
  Vector displayNodes;
  /**
   *This hashmap maps to arrays of doubles which correspond
   *to the significance for each of several conditions
   */
  public static HashMap exHash;
  /**
   *This is a threshhold used in calculating the scoring
   *of the active path. It determines whether or not we need
   *to access the Ztable.
   */
  public static double threshhold = .1;
    
  /**
   *Has the score been calculated since the last state change?
   */
  private boolean scored = false;
  /**
   *Ths sum of z scores for each condition
   */
  protected double [] zSums;
  /**
   * The scores which will be displayed for each condition, thsi might not
   * be the same as the actual z sum score for that condition
   */
  protected double [] displayScores;
  /**
   *same as before, but now it's sorted. Who can resists a sorted array
   *of doubles? Not me.
   */
  private double [] zSumsSort;
  public static String [] attrNames;
  /**
   * Should we include our neighbors in calculating the score 
   *
   */
  public static boolean regionScoring;

  /**
   *should we use the pStats object to make size corrections?
   */
  public static boolean monteCorrection;
  /**
   *provides information regarding the mean and standard deviation of scores for components
   *of size 1 through n
   */
  public static ParamStatistics pStats;
  /**
   *provides information to correct a zscore based on how many conditions we are looking at.
   */
  public static ZStatistics zStats;
  /**
   *when we want to determin the significant condition, this keeps track of how many condition
   *we had to look at to get the max significance
   */
  private int min_i;
  /**
   * Makes for fast lookup of whether
   * component contains node or not
   */
  protected HashSet contains;
  /**
   * Makes for fast lookup of whether
   * component's neighborhood contains
   * a particular node
   */
  protected HashSet neighborhood;
  /**
   * Need this to be able to do the
   * regional scoring
   */
  //public static HashMap node2edges;
  public static GraphPerspective graph;
    
  /**
   * Creates an empty components
   */
  public Component ()
  {
    scored = true;
    score = Integer.MIN_VALUE;
    nodes = new Vector();
    zSums = new double [attrNames.length];
    zSumsSort = new double [attrNames.length];
    Arrays.fill(zSums,0);
    contains = new HashSet();
    if(regionScoring){
      neighborhood = new HashSet();
    }
  }
    
  /**
   *this builds a new component off of a node and the neighboring
   *components ot that node, assuming that they are now connected.
   *It's primarily used as an optimization in update_add() in 
   *ActivePathsFinder.java. The neighbor components need to be
   *scored before you call this method.
   * @param nComponents the neighboring components
   * @param current the node which is next to all these components
   */
  public Component(Set nComponents, Node current){
    nodes = new Vector();
    contains = new HashSet();
    scored = false;
    zSums = new double[attrNames.length];
    zSumsSort = new double[attrNames.length];
    Arrays.fill(zSums,0);
    Iterator it = nComponents.iterator();
    //for each neighboring component, get all of the nodes
    //and add them into our list of nodes. Take advantage
    //of the fact that these nodes are already summed
    //and add them inot zSums.
    while(it.hasNext()){
      Component neighbor = (Component)it.next();
      nodes.addAll(neighbor.getNodes());
      contains.addAll(neighbor.contains);
      for(int i = 0;i<zSums.length;i++){
	zSums[i] += neighbor.zSums[i];
      }
    }
    nodes.add(current);
    contains.add(current);
	
    double [] sigs = (double [])exHash.get(current);
    for(int j = 0;j < zSums.length;j++){
      zSums[j] += sigs[j];
    }
	

    if(regionScoring){
      //update the contains hash by combining them all together
      //also update the neighbor hash, but remember to remove the new
      //linking node that is now no longer in the neighborhoold
      neighborhood = new HashSet();
      //add the nodes of the current node to the iterator
      //Edge [] e_array = (Edge [])node2edges.get(current);
      //for(int i = 0;i<e_array.length;i++){
      for(Iterator neighborIt = graph.neighborsList(current).iterator();neighborIt.hasNext();){
	//Node currentNeighbor = e_array[i].opposite(current);
	//Node currentNeighbor = (e_array[i].getSource().equals(current) ? e_array[i].getTarget() : e_array[i].getSource());
	Node currentNeighbor = (Node)neighborIt.next();
	if(!contains.contains(currentNeighbor)){
	  //this neighbor is not in the component, so it
	  //must be in the neighborhood. Add it in and add in
	  //its score
	  neighborhood.add(currentNeighbor);
	  sigs = (double [])exHash.get(currentNeighbor);
	  for(int j = 0;j < zSums.length;j++){
	    zSums[j] += sigs[j];
	  }
	}
      }
      it = nComponents.iterator();
      while(it.hasNext()){
	Component neighbor = (Component)it.next();
	//this will have to be a slow update because the neighborhoods
	//may be overlapping
	//don't add in the score for the current node, because it
	//is not in the neighborhood
	Iterator neighborIt = neighbor.neighborhood.iterator();
	while(neighborIt.hasNext()){
	  Node nodeNeighbor = (Node)neighborIt.next();
	  if(!neighborhood.add(nodeNeighbor)){
	    //the neighborhood didn't change as a result of this
	    //update, that means it must have been added before
	    //this means that the score must have been counted
	    //an additional time when I added all the z-scores
	    //together, must decrement the z-scores to correct
	    //for this
	    sigs = (double [])exHash.get(nodeNeighbor);
	    for(int j = 0;j < zSums.length;j++){
	      zSums[j] -= sigs[j];
	    }
			
	  }
	}
      }
	    
      //remove the new node from neighborhood, if it was there
      //in the first place
      if(neighborhood.remove(current)){
	sigs = (double [])exHash.get(current);
	for(int j = 0;j < zSums.length;j++){
	  zSums[j] -= sigs[j];
	}
      }
      //all the scoring has been done
	    
    }	
  }
  
  public final double [] getZSums(){
	  return zSums;
  }
  /**
   * Build a new component containing all the nodes in this list
   * @param nlist Nodes to constitute the new component
   */
  public Component(List nlist){
    scored = false;
    nodes = new Vector(nlist);
    zSums = new double [attrNames.length];
    zSumsSort = new double [attrNames.length];
    Arrays.fill(zSums,0);	
    if(nodes.size() == 0){
      scored = true;
      score = Integer.MIN_VALUE;
    }
    //first calculate the sum of zscores for each condition
    Iterator it = nodes.iterator();
    while(it.hasNext()){
      double [] sigs = (double [])exHash.get(it.next());
      for(int j = 0;j < zSums.length;j++){
	zSums[j] += sigs[j];
      }
    }
	

    contains = new HashSet(nodes);

    //if we are doing region scoring
    //update the neighborhood hash and add in the score for all of the
    //neighbors, since they will not be added in teh simple scoring
    //function
    if(regionScoring){
      neighborhood = new HashSet();
      it = nodes.iterator();
      while(it.hasNext()){
	Node current = (Node)it.next();
	//Edge [] e_array = (Edge [])node2edges.get(current);
	//for(int i=0;i<e_array.length;i++){
	//Node neighbor = e_array[i].opposite(current);
	//    Node neighbor = (e_array[i].getSource().equals(current) ? e_array[i].getTarget() : e_array[i].getSource());
	for(Iterator neighborIt = graph.neighborsList(current).iterator();neighborIt.hasNext();){
	  Node neighbor = (Node)neighborIt.next();
	  if(!contains.contains(neighbor) && !neighborhood.contains(neighbor)){
	    //found a new neighbor, add its score into the mix
	    //and add it to the neighbor hash
	    neighborhood.add(neighbor);
	    double [] sigs = (double [])exHash.get(neighbor);
	    for(int j = 0;j < zSums.length;j++){
	      zSums[j] += sigs[j];
	    }
	  }
	}

      }
		
    }

  }

  /**
   *This function adds another node to the current active path
   *There is no sort of checking to see if this is a valid node
   *name or whether this node is connected to other nodes
   *in this path.
   * @param node The node be added to this component
   */
  public void addNode(Node node){	
    scored = false;
    nodes.add(node);
    contains.add(node);
	
    if(!regionScoring){
      double [] sigs = (double [])exHash.get(node);
      for(int j = 0;j < zSums.length;j++){
	zSums[j] += sigs[j];
      }
    }
    else{
      if(!neighborhood.remove(node)){
	//this node wasn't in the neighborhood, so we need to
	//account for its score now
	double [] sigs = (double [])exHash.get(node);
	for(int j = 0;j < zSums.length;j++){
	  zSums[j] += sigs[j];
	}
      }
      //need to add in the nodes neighbors to the hash
      //if they are not already present, if they are new
      //need to update z-scores, can't use the node.neighbors()
      //function because they have been removed from the graph
      //Edge [] e_array = (Edge[])node2edges.get(node);
      //for(int i=0;i<e_array.length;i++){
      //Node neighbor = e_array[i].opposite(node);
      for(Iterator neighborIt = graph.neighborsList(node).iterator();neighborIt.hasNext();){
	Node neighbor = (Node)neighborIt.next();
	//Node neighbor = (e_array[i].getSource().equals(node) ? e_array[i].getTarget() : e_array[i].getSource());
	//if this is a new neighbor, need to add in its score
	if(!contains.contains(neighbor) && !neighborhood.contains(neighbor)){
	  neighborhood.add(neighbor);
	  double [] sigs = (double [])exHash.get(neighbor);
	  for(int j = 0;j < zSums.length;j++){
	    zSums[j] += sigs[j];
	  }
	}
      }
    }
	
  }
  
  /**
   * This will remove the specified node from the component. Currently,
   * no effort is made to ensure that this removal will result in a 
   * connected component, or even if this node belong to this particular
   * component.
   */
  public void removeNode(Node node){
    scored = false;
	
    if(!regionScoring){
      double [] sigs = (double [])exHash.get(node);
      for(int j = 0;j < zSums.length;j++){
	zSums[j] -= sigs[j];
      }
    }
    nodes.remove(node);
    contains.remove(node);
    if(regionScoring){
      neighborhood.add(node);
      //find the neighbors that are no longer part of the neighborhood
      //and remove them and their score, in order to do this, need to find
      //it's neighbors and see if any of them are in the component
      //Edge [] e_array = (Edge [])node2edges.get(node);
      //for(int i=0;i<e_array.length;i++){
      for(Iterator neighborIt = graph.neighborsList(node).iterator();neighborIt.hasNext();){
	Node nextNeighbor = (Node)neighborIt.next();
	//Node nextNeighbor = e_array[i].opposite(node);
	//Node nextNeighbor = (e_array[i].getSource().equals(node) ? e_array[i].getTarget() : e_array[i].getSource());
	//only do this for nodes that are in the component
	if(neighborhood.contains(nextNeighbor)){
	  //Edge [] next_array = (Edge [])node2edges.get(nextNeighbor);
	  boolean stillNeighbor=false;
	  //int j = 0;
	  for(Iterator nextIt = graph.neighborsList(nextNeighbor).iterator();nextIt.hasNext() && !stillNeighbor;){
	    //while(!stillNeighbor && j<next_array.length){
	    Node myNode = (Node)nextIt.next();
	    //Node myNode = (next_array[j].getSource().equals(nextNeighbor) ? next_array[j].getTarget() : next_array[j].getSource());
	    if(contains.contains(myNode)){
	      stillNeighbor = true;
	    }
	    //j++;
	  }
	  if(!stillNeighbor){
	    //have to remove its score from the z-sums
	    double [] sigs = (double [])exHash.get(nextNeighbor);
	    for(int k = 0;k < zSums.length;k++){
	      zSums[k] -= sigs[k];
	    }
	    //also have to remove it from the neighborhood hash
	    neighborhood.remove(nextNeighbor);
	  }
	}
      }
    }
  }
  
  /**
   * Use a hash to determine if this component
   * contains this node
   * @param node The specified node
   * @return true if the component contains node
   */
  public boolean contains(Node node){
    return contains.contains(node);
  }
  /**
   * Get the score for this component. Will only recalculate the score if necessary
   * @return score for this component
   */
  public double getScore ()
  {
    if(!scored){
      calculateAdvancedScore();
    }
    return score;
  }

  /**
   *Returns the nodes which are currently a member of this component,
   *not guarenteed to be non-null
   * @return Vector of Nodes
   */
  public Vector getNodes(){
    return nodes;
  }

  /**
   * Determines the corrected score for this component. This function will return
   * the corrected simple score for this component. If monte-carlo correction is not
   * desired, this is equivalent to the simple score. The score is set to a global
   * class variable so that it doesn't need to be recalculated each time.
   * @return The corrected score
   */
  public double calculateAdvancedScore(){
    double simple_score = calculateSimpleScore();
    if(monteCorrection){
      //if(nodes.size()==1){
      //score = pStats.getOneNodeZ((Node)nodes.get(0));
      //}
      //correct the score based on the mean and standard deviation
      if(regionScoring){
	score = (simple_score-pStats.getMean(nodes.size()+neighborhood.size()))/(pStats.getStd(nodes.size()+neighborhood.size()));
      }
      else{
	score = (simple_score-pStats.getMean(nodes.size()))/(pStats.getStd(nodes.size()));
      }
    }
    else{
      score = simple_score;
    }
    scored = true;
    return score;
  }
  /**
   *This method will calculate a simple score for the active path. The score is simple in
   *that it is not corrected for the expected mean and variance of a path of this size. This
   *scoring routine is used to generate the expected mean and variance of active paths.
   * @return the uncorrected score
   */
  public double calculateSimpleScore() {
    int numConds = zSums.length;
    double nodeSqrt;
    if(regionScoring){
      nodeSqrt = Math.sqrt(nodes.size()+neighborhood.size());
      //nodeSqrt = nodes.size()+neighborhood.size();	
    }
    else{
      nodeSqrt = Math.sqrt(nodes.size());
      //nodeSqrt = nodes.size();	
    }
    //sort the array of summed zscores
    for(int i = 0;i<zSums.length;i++){
      zSumsSort[i] = zSums[i];
    }
    Arrays.sort(zSumsSort);
    int index = zSumsSort.length;
    double zSumOverSqrt = zSumsSort[index-1]/nodeSqrt;
    //now we calculate the appropriate z-score for this pathway
    //this is mostly copied from BasicScoringSystem.cc
    double simple_score;
    //keep track of how many conditions we have looked at
    min_i = attrNames.length;
    //even the max zscore sum is pretty bad, screw it
    if(zSumOverSqrt < threshhold){
      simple_score = zStats.rankAdjustedZUsingLog(zSumOverSqrt,numConds,1);
    }
    else{
      //while we are still seing good zscore sums, try to find the best zscore sum
      //note that these sums have to be corrected based on how many conditions we got
      //to choose from to try to get that maximum.
      simple_score = zStats.get_adj_z(numConds-index+1,zSumOverSqrt);	  
      while(--index > 0  && (threshhold < (zSumOverSqrt=zSumsSort[index-1]/nodeSqrt))){
	double temp = zStats.get_adj_z(zSums.length-index+1,zSumOverSqrt);
	if(simple_score < temp){
	  simple_score = temp;
	  min_i = index;
	}
      }
    }
    return simple_score;
  }

  /**
   * compares components based on their score (and memory address if necessary)
   * Function is implemented such that if components arranged in ascending order
   * the highest scoring one will come first. If two components have the same score,
   * we don't want to return 0, because then the SortedSet won't work like we want
   * it to. Instead, I break ties using the default object toString(), which is basically
   * the memory address
   * @param other The component to which to compare
   * @return -1 if this has a higher scoring, 1 otherwise
   */
  public int compareTo(Object other){
    double this_score,other_score;
    this_score = this.getScore();
    other_score = ((Component)other).getScore();
    if(this_score < other_score){
      return 1;
    }
    else if(this_score > other_score){
      return -1;
    }
    else{
      return 0;
    }
	
  }
   

  /**
   * Return a list of condition which contributed to the most significant
   * score for this component.
   * @return A array of string which represent the condition names
   */
  public String [] getConditions(){
    String [] result = new String [attrNames.length-min_i+1];
    //after we call simple score, min_i lets us know how many
    //conditions were signficant in the sorted array. We have to
    //map these sorted scores back to their location in the unsorted
    //array. Then we can figure out the condition name which
    //corresponds to this score.
    scored = false;
    Integer [] integerArray = new Integer[zSums.length];
    for(int i=0;i < integerArray.length;i++){
      integerArray[i] = new Integer(i);
    }

    Arrays.sort(integerArray,new MyComparator());
	
    for(int i=0;i <= attrNames.length-min_i;i++){
      result[i]=attrNames[integerArray[zSums.length-i-1].intValue()];
    }
    return result;
	    
  }
    
  class MyComparator implements Comparator{
    public int compare(Object o1, Object o2){
      //return a negative if the first one is less
      Integer int1 = (Integer)o1;
      Integer int2 = (Integer)o2;
      if(zSums[int1.intValue()]<zSums[int2.intValue()]){
	return -1;
      }
      else if(zSums[int1.intValue()]>zSums[int2.intValue()]){
	return 1;
      }
      else{
	return 0;
      }
    }
    public boolean equals(Object obj){
      return super.equals(obj);
    }
  }

  public String [] getNodeNames(){
    String [] result = new String[nodes.size()];
    Iterator it = displayNodes.iterator();
    for(int i=0;i<result.length;i++){
      //result[i] = (String)Cytoscape.getNodeAttributeValue((Node)it.next(),Semantics.CANONICAL_NAME);
      result[i] = ((Node)it.next()).getIdentifier();
    }
    return result;
	
  }
  /**
   * Return a string containing the score of this component and its
   * nodes
   */
  public String toString(){
    String result="";
    result += "Score: "+getScore()+"\n";
    result += "Nodes";
    int width = 5;
    for(int i=0;i<nodes.size();i++){
      if(i%width == 0){
	result += "\n";
      }
      result += nodes.get(i).toString()+" ";
    }
    result += "\n";
    return result;
  }
  
  public List getDisplayNodes(){
	  return displayNodes;
  }

  public List<Node> getDisplayNodesGeneric()
  {
  	List<Node> list = new java.util.ArrayList(displayNodes.size());
	Iterator iterator = displayNodes.iterator();
	while (iterator.hasNext())
		list.add((Node) iterator.next());
	return list;
  }
  
  public double [] getDisplayScores(){
	  return displayScores;
  }
  /**
   * If region scoring is in use, the returned set of nodes
   * tends to not make much sense, because the score is actually
   * dependent on nodes in hte region. This function adds those nodes
   * into the display set of nodes, also update the displayed scores to
   * something that may be more useful for the actual user.
   */
  public void finalizeDisplay(){
	  /*
	   * This is only applicable to the regional scoring
	   * case
	   */
	  if(!regionScoring){
		  displayNodes = new Vector(nodes);
	  }
	  if(regionScoring){
		  /*
		   * This is the general algorithm
		   * 1. store the old scoring array so this info doesnt
		   * 	get lost
		   * 2. sum the score of all the nodes in the the core
		   * 3. For each node in the neighborhood, see if adding
		   * 	it will increase the score
		   */
		  /*
		   * Since we are using regional scoring, the zsums matrix should
		   * already account for every node in the neighborhood.
		   */
		  regionScoring = false;
		  Component tempComponent = new Component(nodes);
		  OpenIntDoubleHashMap node2Increase = new OpenIntDoubleHashMap(neighborhood.size());
		  double previous_score = tempComponent.getScore();
		  for(Iterator nodeIt = neighborhood.iterator();nodeIt.hasNext();){
			  Node current = (Node)nodeIt.next();
			  tempComponent.addNode(current);
			  double new_score = tempComponent.getScore();
			  node2Increase.put(current.getRootGraphIndex(),new_score-previous_score);
			  tempComponent.removeNode(current);
		  }
		  
		  IntArrayList neighborNodes = new IntArrayList(neighborhood.size());
		  node2Increase.keysSortedByValue(neighborNodes);
		  neighborNodes.reverse();
		  
		  int idy = 0;
		  double score_increase = 1.0;
		  while(idy < neighborNodes.size() && score_increase > 0){
			  Node current = graph.getNode(neighborNodes.get(idy));
			  tempComponent.addNode(current);
			  double new_score = tempComponent.getScore();
			  score_increase = new_score - previous_score;
			  if(score_increase > 0){
				  previous_score = new_score;
			  }
			  else{
				  tempComponent.removeNode(current);
			  }
			  idy += 1;
		  }
		  
		  displayNodes = tempComponent.getNodes();
		  displayScores = new double[zSums.length];
		  for(int idx = 0;idx<displayScores.length;idx++){
			  displayScores[idx] = ZStatistics.oneMinusNormalCDF(tempComponent.zSums[idx]);
		  }
		  
		  regionScoring = true;
		  
	  }
	  
	 
  }

}

