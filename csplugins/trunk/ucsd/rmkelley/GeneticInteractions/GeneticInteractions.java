package csplugins.ucsd.rmkelley.GeneticInteractions;

import giny.model.RootGraph;
import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.GraphView;
import java.awt.event.ActionEvent;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Random;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import csplugins.ucsd.rmkelley.GeneticInteractions.GeneticInteractionsCommandLineParser;
import cytoscape.data.GraphObjAttributes;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

/**
 * Finds interesting groups of genetic interactions
 */
public class GeneticInteractions extends CytoscapePlugin{
  /**
   * Print debugging information
   */
  public static boolean DEBUG = false;
  /**
   * The number of threads to use. Currently setting greater than 1 really doesn't help
   */
  public static int THREAD_COUNT = 1;
  /**
   * The max length of lethal path to identify (in nodes
   */
  public static int MAX = 3;
  /**
   * Teh min length of lethal path to identify (in nodes)
   */
  public static int MIN = 1;
  /**
   * The attribute name to use for identifying lethal nodes
   */
  public static String LETHAL = "lethal";
  /**
   * The interaction type identifier for synthetic lethals
   */
  public static String LETHAL_INTERACTION = "gl";
  /**
   * The interaction type identifier for observing lack of a 
   * synthetic lethal
   */
  public static String NO_LETHAL_INTERACTION = "nsl";
  /**
   * String indicating presence of lethal deletion for LETHAL attribute
   */
  public static String YES="yes";
  /**
   * String indicating absence of lethal deletion for LETHAL attribute
   */
  public static String NO="no";
  /**
   * When scoring CoPPs individually, this is the number of high scoring CoPPs to maintain
   */
  public static int RESULTS = 1000000;
  /**
   * This is a parameter used in scoring, which refers to the probability that a genetic interaction
   * spanning a CoPP will be a synthetic lethal
   */
  public static double COPP_LETHAL = 0.8;
  /**
   * This is  a parameter used in scoring, which refers to the probability
   * that a lethal interaction would have arisen spontaneously without being caused 
   * by a CoPP
   */
  public static double BACKGROUND_LETHAL = 0.01;
  /**
   * This is a parameter used in scoring, which refers to the probability that an unobserved interaction
   * would have been a synthetic lethal (ie, the background level of lethal interactions)
   */
  public static double LETHAL_ABUNDANCE = 2000/(130*4000); 
  /**
   * When scoring the CoPPs using probabilistic model, determines whether the CoPPs should be pruned
   * first, this is probably recommended. I should probably prune the paths as they are created in order
   * to save memory since I will have to do it eventually anyway.
   */
  public static boolean PRUNE = true;
  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
  public GeneticInteractions() {
    GeneticInteractionsCommandLineParser parser = new GeneticInteractionsCommandLineParser(Cytoscape.getCytoscapeObj().getConfiguration().getArgs());
    DEBUG = parser.getDebug();
    THREAD_COUNT = parser.getThreadCount();
    MAX = parser.getMax();
    MIN = parser.getMin();
    RESULTS = parser.getResults();
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( new SamplePluginAction() );
    if(parser.getRun()){
      Thread t = new FindAndScoreLethalCoPPThread();
      t.start();
    }
  }
  /**
   * This class gets attached to the menu item.
   */
  public class SamplePluginAction extends AbstractAction {

    /**
     * The constructor sets the text that should appear on the menu item.
     */
    public SamplePluginAction() {super("Find and Score Lethal CoPPs");}

    /**
     * Gives a description of this plugin.
     */
    public String describe() {
      StringBuffer sb = new StringBuffer();
      sb.append("Finds and scores lethal CoPPs");
      return sb.toString();
    }

    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {
      String inputValue = JOptionPane.showInputDialog("How many iterations?");
      Integer iterations = null;
      try {
	iterations = new Integer(inputValue);
      } catch ( Exception e) {
      }
      if ( iterations != null) {
	Thread t = new FindAndScoreLethalCoPPThread(iterations.intValue()); 
	t.start();
      }
    }
  }
}

class FindAndScoreLethalCoPPThread extends Thread{
  protected int total = 1000;
  public FindAndScoreLethalCoPPThread(){
  }

  public FindAndScoreLethalCoPPThread(int total){
    this.total = total;
  }

  public void run(){
    System.out.println("Starting run");
    Date before = new Date();
    //HashMap edge2CoPPs = null;
    //NodePairMap pair2CoPPList;
    //NodePairSet geneticPairs;
    //HashSet geneticInteractions;
    //{
    //create a set of all edges that represent genetic interactions
    //geneticPairs = new NodePairSet(geneticInteractions);
      
    //get the set of baits
    
    CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
    System.out.println("Finding all physical interactions");
    HashMap physicalInteractions = getPhysicalInteractions(currentNetwork);
    System.out.println("Finished finding physical interactions");

    System.out.println("Finding all bait proteins");
    HashSet baits = new HashSet();
    System.out.println("Finished finding all bait proteins");
    getNodeCollection(currentNetwork,"Bait","YES",baits);
    
    System.out.println("Finding all genetic interactions");
    Set geneticInteractions = new HashSet();
    getGeneticInteractions(currentNetwork,geneticInteractions);
    System.out.println("Finished finding all genetic interactions");
    
    System.out.println("Finding all lethal nodes");
    Set lethalNodes = new HashSet();
    getNodeCollection(currentNetwork,"lethal","yes",lethalNodes);
    System.out.println("Finished finding all lethal nodes");
    
    System.out.println(baits.size());
    System.out.println(lethalNodes.size());
    int [] distanceSums = new int[PathFinder.MAX_DISTANCE];
    int [] distanceSquaredSums = new int[PathFinder.MAX_DISTANCE];
     for (int iteration=0;iteration<total;iteration++) {
      //randomize the genetic interactions
      System.out.println("Iteration: "+iteration);
      //System.out.println("Randomizing synthetic lethal interactions");
      geneticInteractions = randomizeLethalInteractions(currentNetwork,geneticInteractions);
      //System.out.println("Finished randomzing synthetic lethal interactions");
      
      //System.out.prinxotln("Mapping genetic interactions to bait proteins");
      HashMap bait2EdgeList = getBait2EdgeList(currentNetwork,geneticInteractions,baits);
      //System.out.println("Finished mapping genetic interactions to bait proteins");

      PathFinder pathFinder = new PathFinder(bait2EdgeList,physicalInteractions);
      int [] distanceCounts = pathFinder.getDistanceCounts();
      int prevCount = 0;
						for (int idx = 0;idx<distanceCounts.length;idx++) {
										prevCount += distanceCounts[idx];
										distanceSums[idx] += prevCount;
										distanceSquaredSums[idx] += prevCount*prevCount;
														//System.out.println(""+idx+" = "+distanceCounts[idx]);
														//distanceSums[idx] += distanceCounts[idx];
														//distanceSquaredSums[idx] += distanceCounts[idx]*distanceCounts[idx];
      } // end of for ()
    }

    for (int idx = 0;idx<distanceSums.length;idx++) {
      double mean = distanceSums[idx]/(float)total;
      double std = Math.sqrt(((distanceSquaredSums[idx]-total*mean*mean)/(float)(total-1)));
      System.out.println("for distance "+idx+", "+mean+", "+std);
    } // end of for ()
  }
  //if (GeneticInteractions.DEBUG) {
  //System.out.println("Identifying all lethal deletions");
  //} // end of if ()
  //List lethalNodes = getLethalNodes(cyWindow);
  //pair2CoPPList = new NodePairMap();
  //CoPPFinder lpf = new CoPPFinder(lethalNodes,physicalInteractions,geneticInteractions,pair2CoPPList);
  //edge2CoPPs = lpf.findCoPPs(GeneticInteractions.MAX,GeneticInteractions.MIN,GeneticInteractions.THREAD_COUNT);
  //}

  /*  SortedVector sortedVectors = new SortedVector();
      int unexplained = 0;
      int count = 0;
      for (Iterator edgeIt = edge2CoPPs.keySet().iterator();edgeIt.hasNext();) {
      Edge currentEdge = (Edge)edgeIt.next();
      SortedVector tree = (SortedVector)edge2CoPPs.get(currentEdge);
      count+=tree.size();
      if (tree.isEmpty()) {
      unexplained++;
      } // end of if ()
      else {
      sortedVectors.sortedAdd(tree);
      }
      } // end of for ()
    
      System.out.println("Found "+count+" total paths");
      System.out.println("With "+unexplained+" unexplained interactions");
        
      //while there are still unexplained interactions
      int explainedInteractions = 0;
      int badPairs = 0;
      int totalInteractions = geneticInteractions.size();
      NodePairSet explainedPairs = new NodePairSet();
      Vector result = new Vector();
      while (explainedInteractions < totalInteractions) {
      //take the path with the best ratio we have so far
      SortedVector bestVector = (SortedVector)sortedVectors.get(0);
      CoPP best = (CoPP)bestVector.get(0);
      result.add(best);
      HashSet resorted = new HashSet();
      for (Iterator oneIt = best.getPathOne().iterator();oneIt.hasNext();) {
      Node one = (Node)oneIt.next();
      for (Iterator twoIt = best.getPathTwo().iterator();twoIt.hasNext();) {
      Node two = (Node)twoIt.next();
      //check to see if this pair has already been dealt with in a previous iteration,
      //if this is the case, then we don't want to do anything
      if (!explainedPairs.contains(one,two)) {
      explainedPairs.add(one,two);
      boolean interaction = false;
      if (geneticPairs.contains(one,two)) {
      explainedInteractions++;
      //get the list of all path pair spanned by this pair
      interaction = true;
      }
      else {
      badPairs++;
      } // end of else
	    
      for (Iterator coPPIt = ((List)pair2CoPPList.get(one,two)).iterator();coPPIt.hasNext();) {
      //for each of these CoPPs, see if they share the same CaP as best, we don't care about it anymore
      //so just ignore it
      CoPP current = (CoPP)coPPIt.next();
      if (!current.cap.equals(best.cap) && edge2CoPPs.keySet().contains(current.cap)) {
      if (interaction) {
      current.decrementLethalCount();
      } // end of if ()
      else {
      current.decrementNonlethalCount();
      } // end of else
	//remove from it's associated vector and add to a hashset of ones that need to be resorted
	if (!resorted.contains(current)) {
	//((SortedVector)edge2CoPPs.get(current.cap)).remove(current);
	resorted.add(current);
	} // end of if ()
	} // end of if ()
	      
	} // end of for ()
	//remove the mapping for this node pair
	pair2CoPPList.remove(one,two);
	} // end of if ()
	    
	}
	}//end of outer for
	//drop the mapping for best, because we
	sortedVectors.remove(0);
	edge2CoPPs.remove(best.cap);
      
	HashSet resortedVectors = new HashSet();
	//now we have to pull the same trick for the sorted vectors themselves
	for (Iterator coPPIt = resorted.iterator();coPPIt.hasNext();) {
	CoPP current = (CoPP)coPPIt.next();
	SortedVector coPPs = (SortedVector)edge2CoPPs.get(current.cap);
	double score_before = ((CoPP)coPPs.get(0)).score;
	current.rescore();
	coPPs.remove(current);
	coPPs.sortedAdd(current);
	double score_after = ((CoPP)coPPs.get(0)).score;
	if (score_before != score_after) {
	if (!resortedVectors.contains(coPPs)) {
	sortedVectors.remove(coPPs);
	resortedVectors.add(coPPs);
	} // end of if ()
	  
	} // end of if ()
	} // end of for ()
      
	for (Iterator vectorIt = resortedVectors.iterator();vectorIt.hasNext();) {
	sortedVectors.sortedAdd(vectorIt.next());
	} // end of for ()
	}
	for( Iterator coPPIt = result.iterator(); coPPIt.hasNext();) {
	printCoPP((CoPP)coPPIt.next());
	}
	System.out.println(badPairs);
	System.out.println("Finished running");
  */

    

  // private void printCoPP(CoPP copp){
//     GraphObjAttributes nodeAttributes = cyWindow.getNetwork().getNodeAttributes();
//     System.out.print(nodeAttributes.getCanonicalName(copp.omega)+": ");
//     for (Iterator oneIt = copp.getPathOne().iterator();oneIt.hasNext();) {
//       System.out.print(","+nodeAttributes.getCanonicalName(oneIt.next()));
//     } // end of for ()
//     System.out.print(": ");

//     for (Iterator twoIt = copp.getPathTwo().iterator();twoIt.hasNext();) {
//       System.out.print(","+nodeAttributes.getCanonicalName(twoIt.next()));
//     } // end of for ()
//     System.out.print(": ");
//     System.out.println(copp.score);
//   }

  //   public void run(){
  //     GraphPerspective myPerspective = cyWindow.getView().getGraphPerspective();
  //     GraphObjAttributes edgeAttributes = cyWindow.getNetwork().getEdgeAttributes();
  //     GraphObjAttributes nodeAttributes = cyWindow.getNetwork().getNodeAttributes(); 
  //     System.out.println("Starting run");
  //     Date before = new Date();
  //     //create a set of all edges that represent genetic interactions
  //     if(GeneticInteractions.DEBUG){
  //       System.out.println("Finding all genetic interactions");
  //     }
  //     HashSet geneticInteractions = getGeneticInteractions(cyWindow);	
   
    
  //     //create a set of all nodes involved in some genetic interaction
  //     if(GeneticInteractions.DEBUG){
  //       System.out.println("Creating set of all nodes involved in genetic interaction");
  //     }
  //     HashSet geneticInteractionNodes = getGeneticInteractionNodes(geneticInteractions);

  //     //for each node involved in a genetic interaction, create a hashmap that maps
  //     //from lethal nodes to a vector of paths to that lethal
  //     if(GeneticInteractions.DEBUG){
  //       System.out.println("Finding all genetic interactions within "+GeneticInteractions.MAX+" of a lethal, using "+GeneticInteractions.THREAD_COUNT+" threads");
  //       System.out.println("and identifying lethal CoPPs by finding non-overlapping paths");	
  //     }
  //     LethalCoPPFinder lpf = new LethalCoPPFinder(cyWindow,geneticInteractionNodes,geneticInteractions);
  //     RestrictedMinHeap lethalCoPPs = lpf.findLethalCoPPs(GeneticInteractions.MAX,GeneticInteractions.MIN,GeneticInteractions.THREAD_COUNT);


  //     if(GeneticInteractions.DEBUG){
  //       System.out.println("Printing information about identified lethal CoPPs");
  //       Iterator lethalCoPPIt = lethalCoPPs.getList().iterator();
  //       while(lethalCoPPIt.hasNext()){
  // 	System.out.println(lethalCoPPIt.next());
  //       }
  //     }
  //     //System.out.println("Identified "+lethalCoPPs.size()+" lethal CoPPs in "+(((new Date()).getTime()-before.getTime())/1000.0)+" seconds");	
  //     //initialize information about lethal CoPPs	
								
  //     //Construct an empty model
  //     CoPPModel model = new CoPPModel(geneticInteractions);
  //     List CoPPList = lethalCoPPs.getList();
  //     boolean improved = true;
  //     while(improved){
  //       improved = false;
  //       for(int idx=0;idx<CoPPList.size();idx++){
  // 	if(model.toggleCoPP((LethalCoPP)CoPPList.get(idx))){
  // 	  improved = true;
  // 	  System.out.println("Found improvement");	
  // 	}
  //       }
  //     }
  //     System.out.println(model);
  //     LethalCoPPFrame resultFrame = new LethalCoPPFrame(model.getList(),cyWindow);
  //     System.out.println("Finishing run, took "+(((new Date()).getTime()-before.getTime())/1000.0)+" seconds");
  //     //score each of the paired paths to genetic interactions

  //   }




  /**
   * Find all edges annotated as a genetic interaction.
   * @param cyWindow The window in which to look for edges
   * @return A vector containing all the edges that are genetic interactions
   */
  private void getGeneticInteractions(CyNetwork cyNetwork, Collection result){
    GraphObjAttributes edgeAttributes = cyNetwork.getEdgeAttributes();	
    Iterator edgeIt = cyNetwork.edgesIterator();
    while(edgeIt.hasNext()){
      Edge currentEdge = (Edge)edgeIt.next();
      String interaction = (String)edgeAttributes.get("interaction",edgeAttributes.getCanonicalName(currentEdge));
      if(interaction.equals(GeneticInteractions.LETHAL_INTERACTION)){
	result.add(currentEdge);
      }
    }
  }

  private HashMap getPhysicalInteractions(CyNetwork cyNetwork){
    HashMap result = new HashMap();
    Iterator nodeIt = cyNetwork.nodesIterator();
    GraphObjAttributes edgeAttributes = cyNetwork.getEdgeAttributes();
    while(nodeIt.hasNext()){
      Node current = (Node)nodeIt.next();
      Set currentSet = new HashSet();
      result.put(current,currentSet);
      Iterator edgeIt = cyNetwork.getAdjacentEdgesList(current, true, true, true).iterator();
      while(edgeIt.hasNext()){
	Edge currentEdge = (Edge)edgeIt.next();
	String interaction = (String)edgeAttributes.get("interaction",edgeAttributes.getCanonicalName(currentEdge));
	if(interaction.equals("pp") || interaction.equals("pd")){
	  if(currentEdge.getSource() == current){
	    currentSet.add(currentEdge.getTarget());
	  }
	  else{
	    currentSet.add(currentEdge.getSource());
	  }
	}
	if(interaction.equals("pd")){
	  if(currentEdge.getTarget() == current){
	    currentSet.add(currentEdge.getSource());
	  }
	}
      }	
    }
    return result;
  }

  private HashMap getBait2EdgeList(CyNetwork cyNetwork, Set geneticInteractions, Set baitNodes){
    HashMap bait2EdgeList = new HashMap();
    for ( Iterator baitIt = baitNodes.iterator();baitIt.hasNext();) {
      Node bait = (Node)baitIt.next();
      Vector edgeList = new Vector();
      bait2EdgeList.put(bait,edgeList);
      List adjacentEdges = cyNetwork.getAdjacentEdgesList(bait, true, true, true);
      for ( Iterator edgeIt = adjacentEdges.iterator();edgeIt.hasNext();) {
	Edge current = (Edge)edgeIt.next();
	if (geneticInteractions.contains(current)) {
	  if (baitNodes.contains(current.getSource())&&baitNodes.contains(current.getTarget())) {
	    //this genetic interaction is between baits, only
	    //take it for this list if this bait is classified as the source
	    if (current.getSource().equals(bait)) {
	      edgeList.add(current);
	    } // end of if ()
	  } // end of if ()
	  else {
	    edgeList.add(current);
	  } // end of else
	} // end of if ()
      }	    
    } // end of if ()
    return bait2EdgeList;
  }

  private Set randomizeLethalInteractions(CyNetwork cyNetwork, Set geneticInteractions){
    RootGraph root = cyNetwork.getRootGraph();
    Random rand = new Random();
    NodePairSet npSet = new NodePairSet(geneticInteractions);
    Node [] sources = new Node[geneticInteractions.size()];
    Node [] targets = new Node[geneticInteractions.size()];

    int idx = 0;
    for (Iterator edgeIt = geneticInteractions.iterator();edgeIt.hasNext();) {
      Edge edge = (Edge)edgeIt.next();
      sources[idx] = edge.getSource();
      targets[idx] = edge.getTarget();
      idx++;
    } // end of for ()

    int iteration = 0;
    int size = geneticInteractions.size();
    while (iteration < size) {
      int intOne = rand.nextInt(geneticInteractions.size());
      int intTwo = rand.nextInt(geneticInteractions.size());
      if (sources[intOne]==targets[intTwo] || targets[intOne]==sources[intTwo]){
	if (GeneticInteractions.DEBUG) {
	  System.out.println("Bad edge pair choice on iteration "+iteration+": crossing will result in self loop");
	} // end of if ()
	continue;
      } // end of if ()
      if (npSet.contains(sources[intOne],targets[intTwo]) || npSet.contains(sources[intTwo],targets[intOne])) {
	if ( GeneticInteractions.DEBUG) {
	  System.out.println("Bad edge pair choice on iteration "+iteration+": crossed edge already present");
      	} // end of if ()
      	continue;
      } // end of if ()

      //the conditions have been satisfied, now cross the edges
      //remove the old edges from the data structures
      npSet.remove(sources[intOne],targets[intOne]);
      npSet.remove(sources[intTwo],targets[intTwo]);
      
      //cross the edges
      Node temp = targets[intOne];
      targets[intOne] = targets[intTwo];
      targets[intTwo] = temp;

      //add the new edges to the data structures
      npSet.add(sources[intOne],targets[intOne]);
      npSet.add(sources[intTwo],targets[intTwo]);
      iteration++;
    } // end of while ()
    
    //now at the very end, go through and blizt all the edges, and generate the new ones
    //we want
    HashSet result = new HashSet(geneticInteractions.size());
    Vector oldEdges = new Vector(geneticInteractions);
    //for (Iterator edgeIt = geneticInteractions.iterator();edgeIt.hasNext();) {
    //  root.removeEdge((Edge)edgeIt.next());
    //} // end of for ()
    //cyNetwork.removeEdges(oldEdges);
    //cyNetwork.hideEdges(oldEdges);
    //for ( Iterator oldEdgeIt = geneticInteractions.iterator();oldEdgeIt.hasNext();) {
    //  Edge edge = (Edge)oldEdgeIt.next();
    //  cyNetwork.removeEdge(root.getIndex(edge),true);
    //} // end of for ()
    root.removeEdges(oldEdges);

    int [] sourceInts = new int[sources.length];
    int [] targetInts = new int[targets.length];
    for ( idx = 0;idx<sourceInts.length;idx++) {
      sourceInts[idx] = sources[idx].getRootGraphIndex();
      targetInts[idx] = targets[idx].getRootGraphIndex();
    } // end of for ()
   
    int [] newEdges = root.createEdges(sourceInts,targetInts,false);
    cyNetwork.restoreEdges(newEdges);
    for (idx=0;idx<newEdges.length;idx++) {
      result.add(root.getEdge(newEdges[idx]));
    } // end of for ()  
    return result;
  }

  private void getNodeCollection(CyNetwork cyNetwork, String attribute, Object value,Collection lethalNodes){
    GraphObjAttributes nodeAttributes = cyNetwork.getNodeAttributes();
    Iterator nodeIt = cyNetwork.nodesIterator();
    while(nodeIt.hasNext()){
      Node current = (Node)nodeIt.next();
      String lethal = (String)nodeAttributes.get(attribute,nodeAttributes.getCanonicalName(current));
      if(lethal == null){
	if(GeneticInteractions.DEBUG){
	  System.out.println("Unable to lookup value for "+current);
	}
      }
      else{
	if(lethal.equals(value)){
	  if ( GeneticInteractions.DEBUG) {
	    System.out.println(""+current+"has value "+value+" for attribute "+attribute);
	  } // end of if ()
	  lethalNodes.add(current);
	}
      }
    }
  }
}




