package csplugins.ucsd.rmkelley.GeneticInteractions;
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

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import csplugins.ucsd.rmkelley.GeneticInteractions.GeneticInteractionsCommandLineParser;
import cytoscape.AbstractPlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.view.CyWindow;

/**
 * Finds interesting groups of genetic interactions
 */
public class GeneticInteractions extends AbstractPlugin{

  private CyWindow cyWindow;
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
  public static int MAX = 4;
  /**
   * Teh min length of lethal path to identify (in nodes)
   */
  public static int MIN = 3;
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
  public GeneticInteractions(CyWindow cyWindow) {
    this.cyWindow = cyWindow;
    GeneticInteractionsCommandLineParser parser = new GeneticInteractionsCommandLineParser(cyWindow.getCytoscapeObj().getConfiguration().getArgs());
    DEBUG = parser.getDebug();
    THREAD_COUNT = parser.getThreadCount();
    MAX = parser.getMax();
    MIN = parser.getMin();
    RESULTS = parser.getResults();
    cyWindow.getCyMenus().getOperationsMenu().add( new SamplePluginAction() );
    if(parser.getRun()){
      Thread t = new FindAndScoreLethalCoPPThread(cyWindow);
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
      Thread t = new FindAndScoreLethalCoPPThread(cyWindow); 
      t.start();
    }
  }
}

class FindAndScoreLethalCoPPThread extends Thread{
  CyWindow cyWindow;
  public FindAndScoreLethalCoPPThread(CyWindow cyWindow){
    this.cyWindow = cyWindow;
  }
  public void run(){
    GraphPerspective myPerspective = cyWindow.getView().getGraphPerspective();
    GraphObjAttributes edgeAttributes = cyWindow.getNetwork().getEdgeAttributes();
    GraphObjAttributes nodeAttributes = cyWindow.getNetwork().getNodeAttributes(); 
    System.out.println("Starting run");
    Date before = new Date();
    //create a set of all edges that represent genetic interactions
    if(GeneticInteractions.DEBUG){
      System.out.println("Finding all genetic interactions");
    }
    HashSet geneticInteractions = getGeneticInteractions(cyWindow);	
   
    
    //create a set of all nodes involved in some genetic interaction
    if(GeneticInteractions.DEBUG){
      System.out.println("Creating set of all nodes involved in genetic interaction");
    }
    HashSet geneticInteractionNodes = getGeneticInteractionNodes(geneticInteractions);

    //for each node involved in a genetic interaction, create a hashmap that maps
    //from lethal nodes to a vector of paths to that lethal
    if(GeneticInteractions.DEBUG){
      System.out.println("Finding all genetic interactions within "+GeneticInteractions.MAX+" of a lethal, using "+GeneticInteractions.THREAD_COUNT+" threads");
      System.out.println("and identifying lethal CoPPs by finding non-overlapping paths");	
    }
    LethalCoPPFinder lpf = new LethalCoPPFinder(cyWindow,geneticInteractionNodes,geneticInteractions);
    RestrictedMinHeap lethalCoPPs = lpf.findLethalCoPPs(GeneticInteractions.MAX,GeneticInteractions.MIN,GeneticInteractions.THREAD_COUNT);


    if(GeneticInteractions.DEBUG){
      System.out.println("Printing information about identified lethal CoPPs");
      Iterator lethalCoPPIt = lethalCoPPs.getList().iterator();
      while(lethalCoPPIt.hasNext()){
	System.out.println(lethalCoPPIt.next());
      }
    }
    //System.out.println("Identified "+lethalCoPPs.size()+" lethal CoPPs in "+(((new Date()).getTime()-before.getTime())/1000.0)+" seconds");	
    //initialize information about lethal CoPPs	
								
    //Construct an empty model
    CoPPModel model = new CoPPModel(geneticInteractions);
    List CoPPList = lethalCoPPs.getList();
    boolean improved = true;
    while(improved){
      improved = false;
      for(int idx=0;idx<CoPPList.size();idx++){
	if(model.toggleCoPP((LethalCoPP)CoPPList.get(idx))){
	  improved = true;
	  System.out.println("Found improvement");	
	}
      }
    }
    System.out.println(model);
    LethalCoPPFrame resultFrame = new LethalCoPPFrame(model.getList(),cyWindow);
    System.out.println("Finishing run, took "+(((new Date()).getTime()-before.getTime())/1000.0)+" seconds");
    //score each of the paired paths to genetic interactions

  }
  /**
   * Make a set containing all the nodes involved in genetic interactions.
   * @param geneticInteractions  A vector containing edges which are known to be genetic interactions
   * @return A HashSet containing all the nodes involved in those interactions
   */
  private HashSet getGeneticInteractionNodes(Set geneticInteractions){
    HashSet result = new HashSet();
    Iterator giIt = geneticInteractions.iterator();
    while(giIt.hasNext()){
      Edge currentEdge = (Edge)giIt.next();
      result.add(currentEdge.getSource());
      result.add(currentEdge.getTarget());
    }
    return result;
  }

  /**
   * Find all edges annotated as a genetic interaction.
   * @param cyWindow The window in which to look for edges
   * @return A vector containing all the edges that are genetic interactions
   */
  private HashSet getGeneticInteractions(CyWindow cyWindow){
    HashSet result = new HashSet();
    GraphObjAttributes edgeAttributes = cyWindow.getNetwork().getEdgeAttributes();	
    Iterator edgeIt = cyWindow.getView().getGraphPerspective().edgesIterator();
    while(edgeIt.hasNext()){
      Edge currentEdge = (Edge)edgeIt.next();
      String interaction = (String)edgeAttributes.get("interaction",edgeAttributes.getCanonicalName(currentEdge));
      if(interaction.equals(GeneticInteractions.LETHAL_INTERACTION)){
	result.add(currentEdge);
      }
    }
    return result;
  }
}
/**
 * This class represents a set of CoPPs which define a CoPP
 * model. The score of this model is automatically updated
 * when elements are added and removed.
 */
class CoPPModel{
  /**
   * The current score of the model
   */
  double score;
  /**
   * A set of all CoPPs currently in hte model
   */
  HashSet CoPPSet;
  /**
   * Maps from pairs of nodes to a count of hte number of CoPPs
   * in hte mode that are spanned by this nodePair.
   */
  NodePairMap nodePairMap;
  /**
   * Keeps track of which node pairs have genetic interactions
   * between them
   */
  NodePairSet nodePairSet;
  /**
   * Create an empty model
   * @param geneticInteractions A HashSet containing the edges
   * correpsonding to all of the lethal interactions
   */
  public CoPPModel(Set geneticInteractions){
    CoPPSet = new HashSet();
    nodePairMap = new NodePairMap();
    nodePairSet = new NodePairSet();
    Iterator giIt = geneticInteractions.iterator();
    while(giIt.hasNext()){
      Edge current = (Edge)giIt.next();
      nodePairSet.add(current.getSource(),current.getTarget());
    }
    //since the scores are all relative anyway, just set the score
    //to be 0 to begin with.
    score = 0; 
  }

  /**
   * @return the current score of this model
   */
  public double getScore(){
    return score;
  }

  /**
   * Get a list of all the CoPPs currently
   * in the model
   */
  public List getList(){
    return new Vector(CoPPSet);
  }

  public String toString(){
    return CoPPSet.toString();
  }

  /**
   * Toggle the current inclusion of this copp in the model
   */
  public boolean toggleCoPP(LethalCoPP lethalCoPP){
    double differential;
    if(CoPPSet.contains(lethalCoPP)){
      differential = removalScoreDifferential(lethalCoPP);
      if(differential > 0){
	removeCoPP(differential,lethalCoPP);	
	return true;	
      }
      else{
	return false;
      }
    }
    else{
      differential = addScoreDifferential(lethalCoPP);
      if(differential > 0){
	addCoPP(differential,lethalCoPP);
	return true;	
      }
      else{
	return false;
      }
    }
  }

  private void addCoPP(double differential,LethalCoPP lethalCoPP){
    Iterator oneIt = lethalCoPP.getPathOne().iterator();
    while(oneIt.hasNext()){
      Node nodeOne = (Node)oneIt.next();
      Iterator twoIt = lethalCoPP.getPathTwo().iterator();
      while(twoIt.hasNext()){
	Node nodeTwo = (Node)twoIt.next();
	int count = nodePairMap.getCount(nodeOne,nodeTwo);
	nodePairMap.setCount(nodeOne,nodeTwo,count+1);
      }
    }
    score += differential;
    CoPPSet.add(lethalCoPP);
  }

  private void removeCoPP(double differential,LethalCoPP lethalCoPP){
    Iterator oneIt = lethalCoPP.getPathOne().iterator();
    while(oneIt.hasNext()){
      Node nodeOne = (Node)oneIt.next();
      Iterator twoIt = lethalCoPP.getPathTwo().iterator();
      while(twoIt.hasNext()){
	Node nodeTwo = (Node)twoIt.next();
	int count = nodePairMap.getCount(nodeOne,nodeTwo);
	nodePairMap.setCount(nodeOne,nodeTwo,count-1);
      }
    }
    score += differential;
    CoPPSet.remove(lethalCoPP);
  }

  /**
   * Remove this CoPP from the model. Precondition of this method is that
   * the CoPP is currently in the model. Score will also be updated
   */

  private double removalScoreDifferential(LethalCoPP lethalCoPP){
    double result = 0;
    Iterator oneIt = lethalCoPP.getPathOne().iterator();
    while(oneIt.hasNext()){
      Node nodeOne = (Node)oneIt.next();
      Iterator twoIt = lethalCoPP.getPathTwo().iterator();
      while(twoIt.hasNext()){
	Node nodeTwo = (Node)twoIt.next();
	boolean lethal = nodePairSet.contains(nodeOne,nodeTwo);
	int count = nodePairMap.getCount(nodeOne,nodeTwo);
	if(count > 1){
	  if(lethal){
	    //we are removing a CoPP that spans a genetic interaction, but there is another CoPP left
	    //in the model that is spanned by this node Pair
	    result += Math.log(1-Math.pow(1-GeneticInteractions.COPP_LETHAL,count-1))-Math.log(1-Math.pow(1-GeneticInteractions.COPP_LETHAL,count));
	  }
	  else{
	    result -= Math.log(1-GeneticInteractions.COPP_LETHAL);
	  }
	}
	else{
	  if(lethal){
	    //we are removing a CoPP and no now more CoPPs span this interaction
	    result += Math.log(GeneticInteractions.BACKGROUND_LETHAL)-Math.log(GeneticInteractions.COPP_LETHAL);
	  }
	  else{
	    result += Math.log(1-GeneticInteractions.BACKGROUND_LETHAL) - Math.log(1-GeneticInteractions.COPP_LETHAL);
	  }
	}
      }
    }
    return result;
  }
  /**
   * Add this CoPP from the model. Precondition of this method is that 
   * is that the CoPP is nto currently in the model. Score will also be
   * updated.
   */
  private double addScoreDifferential(LethalCoPP lethalCoPP){
    double result = 0;
    Iterator oneIt = lethalCoPP.getPathOne().iterator();
    while(oneIt.hasNext()){
      Node nodeOne = (Node)oneIt.next();
      Iterator twoIt = lethalCoPP.getPathTwo().iterator();
      while(twoIt.hasNext()){
	Node nodeTwo = (Node)twoIt.next();
	//for each node pair check to see if there is a genetic interaction
	//between that pair of nodes
	boolean lethal = nodePairSet.contains(nodeOne,nodeTwo);	
	int count = nodePairMap.getCount(nodeOne,nodeTwo);
	if(count > 0){
	  if(lethal){
	    //at least one CoPP already spans this interaciton, which
	    //is a lethal interaction
	    result +=  -Math.log(1-Math.pow(1-GeneticInteractions.COPP_LETHAL,count)) + Math.log(1-Math.pow(1-GeneticInteractions.COPP_LETHAL,count+1));
	  }
	  else{
	    //a CoPP already spans this node pair, which is not
	    //a lethal interaction
	    result += Math.log(1-GeneticInteractions.COPP_LETHAL);

	  }
	}
	else{
	  if(lethal){
	    //no CoPP already spans this interaction, which is a 
	    //lethal interaction
	    result += Math.log(GeneticInteractions.COPP_LETHAL) - Math.log(GeneticInteractions.BACKGROUND_LETHAL);
	  }
	  else{
	    //no CoPP oslready spans this interaction, which is not
	    //a lethal interaction
	    result += Math.log(1-GeneticInteractions.COPP_LETHAL) - Math.log(1-GeneticInteractions.BACKGROUND_LETHAL);
	  }
	}
      }
    }
    return result;
  }
  /**
   * Predicts how the score would be changed if this CoPP was removed from the network. 
   * Does not actually make any changes to the network.
   * @param newCoPP the CoPP to change
   * @return The score if the inclusion of this CoPP was toggled.
   */
  public double getScoreAfterRemove(LethalCoPP newCoPP){
    return score;	
  }
				
}
/**
 * This class keeps track of a set of unordered pairs of nodes
 */
class NodePairSet{
  private HashMap node2NodeSet;
  public NodePairSet(){
    node2NodeSet = new HashMap();
  }

  public void add(Node one,Node two){
    if(!isGreater(one,two)){
      Node temp = one;
      one = two;
      two = temp;
    }
    HashSet nodeSet = (HashSet)node2NodeSet.get(one);
    if(nodeSet == null){
      nodeSet = new HashSet();
      node2NodeSet.put(one,nodeSet);
    }

    nodeSet.add(two);
  }

  public boolean contains(Node one, Node two){
    if(!isGreater(one,two)){
      Node temp = one;
      one = two;
      two = temp;
    }
    HashSet nodeSet = (HashSet)node2NodeSet.get(one);
    if(nodeSet == null){
      return false;
    }
    else{
      return nodeSet.contains(two);
    }
  }



  /**
   * Determines the ordering of nodes for the purpose of this nodePari map.
   * Since we are interested in unorder paris, we have to know which one out of
   * the pair to look up first (so that getCount(one,two)==getCount(two,one)))
   */
  private boolean isGreater(Node one, Node two){
    int hash1 = one.hashCode();
    int hash2 = two.hashCode();
    if(hash1<hash2){
      return false;	
    }
    else if(hash1 == hash2){
      //I'm betting most of this code is never executed, since the default hashCode of object
      //should do a pretty thourough job of distinguishing between the two
      String ident1 = one.toString();
      String ident2 = two.toString();
      if(ident1.equals(ident2)){
	throw new IllegalArgumentException("Members of node pair not distinct");
      }
      else if(ident1.compareTo(ident2)<0){
	return false;	
      }
    }
    return true;
  }

}
/**
 * This class maps from unorderpairs of nodes to an integer count. This is used to count the number
 * of copps that span any particular node pair in the CoPP model.
 */
class NodePairMap{
  /**
   * This hashmap maps from a node to a hashmap. If we are trying to figure out the count of CoPP from node A to B.
   * We would look up node A in this hashmap, and then node B in the hashmap that is returned.
   */
  private HashMap node2HashMap;
  /**
   * This constructs a node pair map that contains no mappings
   */
  public NodePairMap(){
    node2HashMap = new HashMap();	
  }

  /**
   * Determines the ordering of nodes for the purpose of this nodePari map.
   * Since we are interested in unorder paris, we have to know which one out of
   * the pair to look up first (so that getCount(one,two)==getCount(two,one)))
   */
  private boolean isGreater(Node one, Node two){
    int hash1 = one.hashCode();
    int hash2 = two.hashCode();
    if(hash1<hash2){
      return false;	
    }
    else if(hash1 == hash2){
      //I'm betting most of this code is never executed, since the default hashCode of object
      //should do a pretty thourough job of distinguishing between the two
      String ident1 = one.toString();
      String ident2 = two.toString();
      if(ident1.equals(ident2)){
	throw new IllegalArgumentException("Members of node pair not distinct");
      }
      else if(ident1.compareTo(ident2)<0){
	return false;	
      }
    }
    return true;
  }
  public int getCount(Node one, Node two){
    //figure out the order in which we want to access the hash
    //first look at the hashCode, since that should be pretty quick
    if(!isGreater(one,two)){
      Node temp = one;
      one = two;
      two = temp;
    }
    HashMap node2Int = (HashMap)node2HashMap.get(one);
    if(node2Int == null){
      return 0;
    }
    else{
      Integer count = (Integer)node2Int.get(two);
      if(count == null){
	return 0;
      }
      else{
	return count.intValue();
      }
    }
  }

  public void setCount(Node one, Node two,int count){
    if(!isGreater(one,two)){
      Node temp = one;
      one = two;
      two = temp;
    }
    HashMap node2Int = (HashMap)node2HashMap.get(one);
    if(node2Int == null){
      node2Int = new HashMap();
      node2HashMap.put(one,node2Int);
    }
    node2Int.put(two,new Integer(count));	
  }
}
/**
 * This class represents a lethal CoPP. This is a pair of paths form a genetic interaction
 * which both end in the same lethal deletion. These paths are assumed to be non-overlapping
 * (in terms of nodes)
 */
class LethalCoPP implements Comparable{
  /**
   * The score of this CoPP, haven't yet decided how this will be determined
   */
  public double score = 0;
  /**
   * See constructor
   */
  public Node lethal;
  /**
   * See constructor
   */
  private Set pathOne;
  /**
   * See constructor
   */
  private Set pathTwo;
  /**
   * Constructor
   * @param lethal The convergent lethal node of this lethal CoPP
   * @param one The first path of this pair of paths (represnted as a set)
   * @param two The second path of this pair of paths (repersented as a set);
   */
  public LethalCoPP(CyWindow cyWindow, Node lethal,Set pathOne,Set pathTwo){
    this.lethal = lethal;
    this.pathOne = pathOne;
    this.pathTwo = pathTwo;
    int lethalCount = 0;
    Iterator pathOneIt = pathOne.iterator();
    GraphPerspective myPerspective = cyWindow.getView().getGraphPerspective();
    HashMap name2Interaction = cyWindow.getNetwork().getEdgeAttributes().getAttribute("interaction");
    HashMap edge2Name = cyWindow.getNetwork().getEdgeAttributes().getNameMap();
    while(pathOneIt.hasNext()){
      Node nodeOne = (Node)pathOneIt.next();
      Iterator pathTwoIt = pathTwo.iterator();
      while(pathTwoIt.hasNext()){
	Node nodeTwo = (Node)pathTwoIt.next();
	if(myPerspective.edgeExists(nodeOne,nodeTwo)){
	  Iterator edgeIt = myPerspective.edgesList(nodeOne,nodeTwo).iterator(); 	
	  while(edgeIt.hasNext()){
	    Edge currentEdge = (Edge)edgeIt.next();
	    String interaction = (String)name2Interaction.get(edge2Name.get(currentEdge));
	    if(interaction.equals(GeneticInteractions.LETHAL_INTERACTION)){
	      lethalCount++;	
	    }
	  }
	}
      }
    }
    //calculate the total number of observations that could have been made
    //when calculating the number of lethal observations, we pretend like for the unobserved interactions, we observed
    //a number of synthetic lethals consistent with the background level
    //double lethalObservations = geneticInteractions.size()+(pathOne.size()*pathTwo.size()-observationCount)*GeneticInteractions.LETHAL_ABUNDANCE;
    double totalPairs = pathOne.size()*pathTwo.size();
							
    //pretend like we observed a number of interactions consistent with background for the non-observations
    lethalCount += (totalPairs-lethalCount)*GeneticInteractions.BACKGROUND_LETHAL;	
    //any observation that was not a lethal interaction, must be a non-observance of a lethal
    //interaction
    double nonLethalCount = totalPairs - lethalCount;
    score = lethalCount*Math.log(GeneticInteractions.COPP_LETHAL)+nonLethalCount*Math.log(1-GeneticInteractions.COPP_LETHAL);	
  }

  /**
   * Get the set of nodes in path one
   */
  public Set getPathOne(){
    return pathOne;
  }

  /**
   * Get the set of nodes in path two
   */
  public Set getPathTwo(){
    return pathTwo;
  }

  /**
   * Comparable implementation, just compares the value of the score field
   */
  public int compareTo(Object o){
    LethalCoPP other = (LethalCoPP)o;
    if(this.score < other.score){
      return -1;
    }
    else if(this.score > other.score){
      return 1;
    }
    return 0;
  }


  /**
   * Return string repersentation of this object.
   * Basically just prints out the two sets
   */
  public String toString(){
    return ""+lethal+" , "+pathOne+" , "+pathTwo+": Score = "+score;
  }
}

/**
 *This class is responsible for finding lethal CoPPs in the network. It does this by examining
 *each lethal node and the network, and finding any node with a genetic interaction that is near
 *that node. It then looks for genetic interactions that span two nodes that are close to the lethal
 *Each pair of paths ot those two nodes that do not overlap form a LethalCoPP
 *nodes with a lethal annotation.
 */
class LethalCoPPFinder{
  /**
   * The window in which we are looking for paths
   */
  private CyWindow cyWindow;
  /**
   * Contains all nodes known to be involved in genetic interactions
   */
  private HashSet geneticInteractionNodes;
  /**
   * A list of all the lethal nodes in hte network
   */
  private List lethalNodes;
  /**
   * A set of all genetic interactions in this graph
   */
  private HashSet geneticInteractions;
  /**
   * Constructor.
   * @param cyWindow The window in which we are looking for paths
   * @param geneticInteractionNodes a HashSet containing all nodes involved in a genetic interaction.
   * @param geneticInteractions a set of all lethal interactions in  the graph
   */
  public LethalCoPPFinder(CyWindow cyWindow, HashSet geneticInteractionNodes, HashSet geneticInteractions){
    this.cyWindow = cyWindow;
    this.geneticInteractionNodes = geneticInteractionNodes;
    this.geneticInteractions = geneticInteractions;	
  }

  public RestrictedMinHeap findLethalCoPPs(int distance, int min,int thread_count){
    RestrictedMinHeap result = new RestrictedMinHeap(GeneticInteractions.RESULTS);
    Thread [] threads = new Thread [thread_count];
    //create a hashmap that maps from each node to a set of physicall interacting nodes
    HashMap node2Set = new HashMap();
    GraphPerspective myPerspective = cyWindow.getView().getGraphPerspective();
    Iterator nodeIt = myPerspective.nodesIterator();
    GraphObjAttributes edgeAttributes = cyWindow.getNetwork().getEdgeAttributes();
    while(nodeIt.hasNext()){
      Node current = (Node)nodeIt.next();
      Set currentSet = new HashSet();
      node2Set.put(current,currentSet);
      Iterator edgeIt = myPerspective.getAdjacentEdgesList(current, true, true, true).iterator();
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

    //create a list of all lethal nodes
    lethalNodes = new Vector();
    GraphObjAttributes nodeAttributes = cyWindow.getNetwork().getNodeAttributes();
    nodeIt = cyWindow.getView().getGraphPerspective().nodesIterator();
    while(nodeIt.hasNext()){
      Node current = (Node)nodeIt.next();
      String lethal = (String)nodeAttributes.get(GeneticInteractions.LETHAL,nodeAttributes.getCanonicalName(current));
      if(lethal == null){
	if(GeneticInteractions.DEBUG){
	  System.out.println("Unable to lookup value for "+current);
	}
      }
      else{
	if(lethal.toLowerCase().equals(GeneticInteractions.YES)){
	  lethalNodes.add(current);
	}
	else if(!lethal.toLowerCase().equals(GeneticInteractions.NO)){
	  throw new RuntimeException("Expected "+GeneticInteractions.YES+" or "+GeneticInteractions.NO+" for attribute "+GeneticInteractions.LETHAL);
	}
      }
    }


    Iterator lethalIt = lethalNodes.iterator();
    for(int i=0;i<thread_count;i++){
      threads[i] = new LethalCoPPFinderThread(cyWindow,geneticInteractions,node2Set,lethalIt,geneticInteractionNodes,result,distance,min);
    }
    for(int i=0;i<thread_count;i++){
      threads[i].start();
    }

    for(int i=0;i<thread_count;i++){
      try{
	threads[i].join();
      }
      catch(InterruptedException ie){
	ie.printStackTrace();
	System.exit(-1);
      }
    }
    return result;
  }
}

/**
 * This is a worker thread that will find paths from genetic interactions
 * to lethal nodes.
 */
class LethalCoPPFinderThread extends Thread{
  /**
   * Maps from a node to a set of physical neighbors 
   */
  private HashMap node2Set;

  /**
   * See constructor
   */
  private Iterator lethalIt;

  /**
   * See constructor
   */
  private HashSet geneticInteractionNodes;
  /**
   * See constructor
   */
  private RestrictedMinHeap result;
  /**
   * See constructor
   */
  private int depth;
  /**
   * Keeps track of the nodes seen already in the search
   */
  private HashSet seenNodes;
  /**
   * HashSet of all the lethal nodes, hopefully faster than trying to do the lookup
   * in the reviled GraphObjAttributes every time
   */
  private HashSet lethalNodes;
  /**
   * Node that we are currently searching from, need this to be able to correctly associate
   * results
   */
  private Node currentSearch;
  /**
   * See constructor
   */
  private int min;
  /**
   * Maps from a giNode to a set of paths, used to store the found paths in the search
   * from a particular lethal node.
   */
  private HashMap node2Paths;
  /**
   * Set of all lethal interactions in the graph
   */
  private HashSet geneticInteractions;	
  /**
   * the window containing the graph
   */
  private CyWindow cyWindow;
  /**
   * Constructor
   * @param cyWindow the window containing the graph, need this to pass to constructor for LethalCoPP
   * @param geneticInteractions A set of all lethal interactions 
   * @param node2set hashmap from a node to list of nodes that physically interact
   * @param lethalIt An iterator over nodes with lethal deletion
   * @param geneticInteractionNodes a set of nodes that are involved in some genetic interaction
   * @param result A restricted min heap where the results will be stored 
   * @param depth The depth of the greedy search
   * @param min The minimum depth of the greedy search (ignore results closer than this)
   */
  public LethalCoPPFinderThread(CyWindow cyWindow,HashSet geneticInteractions,HashMap node2Set,Iterator lethalIt, HashSet geneticInteractionNodes, RestrictedMinHeap result,int depth,int min){
    this.cyWindow = cyWindow;
    this.geneticInteractions = geneticInteractions;
    this.node2Set = node2Set; 
    this.lethalIt = lethalIt;
    this.geneticInteractionNodes = geneticInteractionNodes;
    this.result = result;
    this.depth = depth;
    this.min = min;
    this.node2Paths = new HashMap();
  }

  /**
   * This will start the pathfinding procedure and store any results
   * found into "result";
   */
  public void run(){
    boolean done = true;
    Node current = null;
    synchronized(lethalIt){
      if(lethalIt.hasNext()){
	current = (Node)lethalIt.next();
	if(GeneticInteractions.DEBUG){
	  System.out.println(current);
	}
	done = false;
      }
    }
    while(!done){
      done = true;
      //search from current and store the results
      seenNodes = new HashSet();
      currentSearch = current;
      node2Paths.clear();
      findPaths(current,depth,min);
      //using the paths we just found, go through and indentify
      //lethal CoPPs. iteratte through the geneteic interactions,
      //and see which ones have both a source and target in the paths
      //hashmap, try to create CoPPs from these nodes
      Iterator giIt = geneticInteractions.iterator();
      Set reachedNodes = node2Paths.keySet();
      while(giIt.hasNext()){
	Edge lethal = (Edge)giIt.next();
	Node source = lethal.getSource();
	Node target = lethal.getTarget();
	if(reachedNodes.contains(source) && reachedNodes.contains(target)){
	  findCoPPs(source,target);
	}
      }

      //check to see if there is another node from which
      //to search
      synchronized(lethalIt){
	if(lethalIt.hasNext()){
	  current = (Node)lethalIt.next();
	  if(GeneticInteractions.DEBUG){
	    System.out.println(current);
	  }
	  done = false;
	}
      }			
    }
  }

  /**
   * Given a source and target node, find all combinations
   * of paths from a single lethal node that do not overlap
   * and therefore form a lethal CoPP. This adds the identified
   * lethal CoPPs to result.
   * @param source the source node of a lethal interaction
   * @param target the target node of a genetic interaction
   */
  private void findCoPPs(Node source, Node target){
    List sourcePaths = (List)node2Paths.get(source);	
    List targetPaths = (List)node2Paths.get(target);
    //Iteratore through each possible combination of paths
    //from the node and source
    Iterator sourcePathsIt = sourcePaths.iterator();
    while(sourcePathsIt.hasNext()){
      HashSet sourcePath = (HashSet)sourcePathsIt.next();
      Iterator targetPathsIt = targetPaths.iterator();
      while(targetPathsIt.hasNext()){
	HashSet targetPath = (HashSet)targetPathsIt.next();
	//check to see if there is overlap between the 
	//two paths
	Iterator sourceNodeIt = sourcePath.iterator();
	boolean overlap = false;
	while(sourceNodeIt.hasNext() && !overlap){
	  if(targetPath.contains(sourceNodeIt.next())){
	    overlap = true;
	  }
	}
	if(!overlap){
	  //there is no overlap between these two paths
	  //create a new lethal copp and add it to the list
	  //of results
	  LethalCoPP lethalCoPP = new LethalCoPP(cyWindow,currentSearch,sourcePath,targetPath);
	  synchronized(result){
	    result.restrictedInsert(lethalCoPP);
	  }
	}
      }
    }
  }
  /**
   * Recursive function to find genetic interaction nodes
   * @param current The current location of the search
   * @param distance The remaining depth in the search
   */
  private void findPaths(Node current, int distance, int min){
    //base case, no more depth left in search
    if(distance > 0){
      seenNodes.add(current);
      //check to see if the current node is a genetic interaction 
      if(min <= 1 && geneticInteractionNodes.contains(current)){
	//add the current path to the results
	List pathList = (List)node2Paths.get(current);
	if(pathList == null){
	  pathList = new Vector();
	  node2Paths.put(current,pathList);
	}
	//create a copy of the current path so far
	HashSet currentPath = new HashSet(seenNodes);
	currentPath.remove(currentSearch);
	pathList.add(currentPath);
      }
      //get the list of next nodes and initiate a
      //search from each of those, if they have not 
      //already been seen
      Iterator neighborIt = ((Set)node2Set.get(current)).iterator();
      while(neighborIt.hasNext()){
	Node neighbor = (Node)neighborIt.next();
	if(!seenNodes.contains(neighbor)){
	  findPaths(neighbor,distance-1,min-1);
	}
      }
      //remove the current
      seenNodes.remove(current);
    }
  }
}

/**
 * This class is meant to store the top scoring results from a large list. Ideally, the
 * each result is added to the list as it's score is calculated, and only the top scoring
 * ones will remain. It is backed up by a MinHeap.
 */
class RestrictedMinHeap{ // Min heap class

  /**
   * An array used to back the minheap
   */
  private Comparable [] heap;  // Pointer to the heap array
  /**
   * The number of elements currently in hte heap
   */
  private int n;        // Number of elements now in the heap

  /**
   * Constructor
   * @param capacity This is hte initial and final capacity of the heap. If a number of elements greater than capacity is added to the heap, the lower scoring ones will be discarded.
   */
  public RestrictedMinHeap(int capacity){
    heap = new Comparable[capacity];
    n = 0;		
  }

  /**
   * Move an element to its correct location in the heap.
   * @param pos The current position of the parameter
   */
  private void siftdown(int pos) { // Put element in its correct place
    while(pos < n/2){
      int minChild = 2*pos + 1;
      if(minChild < (n-1) && heap[minChild].compareTo(heap[minChild+1])>0){
	minChild++;
      }
      if(heap[pos].compareTo(heap[minChild]) <= 0){
	return;
      }
      else{
	Comparable temp = heap[minChild];
	heap[minChild] = heap[pos];
	heap[pos] = temp;
	pos = minChild;
      }
    }
  }

  /**
   * Insert this value into the restricted heap. This is for when the heap is not full,
   * so it just does a regular old insert.
   * @param val The value to be inserted
   */
  private void insert(Comparable val) { // Insert value into heap
    int current = n++;
    int parent = (current-1)/2;
    heap[current] = val;        
    // Now sift up until curr's parent's < curr's key
    while ((current!=0) && heap[current].compareTo(heap[parent])<0){
      Comparable temp = heap[parent];
      heap[parent] = heap[current];
      heap[current] = heap[parent];
      current = parent;
    }
  }

  /**
   * Helper function for insert. If the heap is already full, then we remove the current
   * min value and then do an insert. Only called if we know we want to do the insert
   * @param val The value to be optionally inserted or discarded
   */
  private void removeMinAndInsert(Comparable val){
    heap[0] = val;
    siftdown(0);
  }

  /**
   * Perform an insert that may optionally remove the current min element or fail to insert. If the
   * inserted value is better than the current min the current min will be ejected. Otherwise, the current
   * insert will be discarded
   * @param val The value to be inserted
   */
  public void restrictedInsert(Comparable val){ 
    if(n ==heap.length){
      //need to do a remove min and insert
      if(val.compareTo(heap[0]) > 0){
	//we would rather have the
	//value being inserted than
	//the min
	removeMinAndInsert(val);
      }
      //otherwise, we're not interested in
      //the value so just forget about it
    }
    else{
      //just do an insert
      insert(val);
    }
  }

  /**
   * Return a list view of the elements currently in the heap. 
   * @return A list of elements currently in the heap. I wouldn't use this
   * to remove anything if I were you.
   */
  public List getList(){
    return Arrays.asList(heap).subList(0,n);
  }
}

class LethalCoPPFrame extends JFrame {
  List lethalCoPPs;
  CyWindow cyWindow;
  JTable table;
  public LethalCoPPFrame(List lethalCoPPs, CyWindow cyWindow){
    super();
    this.lethalCoPPs = lethalCoPPs;
    this.cyWindow = cyWindow;	
    Object [][] data = new Object [lethalCoPPs.size()][2];
    for(int i=0;i<lethalCoPPs.size();i++){
      data[i][0] =  lethalCoPPs.get(i);
      data[i][1] = new Double(((LethalCoPP)lethalCoPPs.get(i)).score);	
    }
    Object [] headers = new Object [2];
    headers[0] = "CoPP";
    headers[1] = "Score";
    table = new JTable(data,headers); //data has type Object[]
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel rowSM = table.getSelectionModel();
    rowSM.addListSelectionListener(new PathListListener());

    JScrollPane tableScroller = new JScrollPane(table);
    getContentPane().add(tableScroller);
    this.setTitle("High Scoring CoPPs");
    this.setVisible(true);
    this.pack();
  } 

  private class PathListListener implements ListSelectionListener{
    GraphView myView = cyWindow.getView();
    public void valueChanged(ListSelectionEvent e) {
      //if(e.getValueIsAdjusting() == false){
      ListSelectionModel lsm = (ListSelectionModel)e.getSource();
      if (lsm.getMinSelectionIndex() != -1) {
	LethalCoPP selectedCoPP = (LethalCoPP)lethalCoPPs.get(lsm.getMinSelectionIndex());
	myView.getNodeView(selectedCoPP.lethal).setSelected(true);
	Iterator oneIt = selectedCoPP.getPathOne().iterator();
	while(oneIt.hasNext()){
	  myView.getNodeView((Node)oneIt.next()).setSelected(true);
	}
	Iterator twoIt = selectedCoPP.getPathTwo().iterator();
	while(twoIt.hasNext()){
	  myView.getNodeView((Node)twoIt.next()).setSelected(true);
	}

      }
    }
  }
}
