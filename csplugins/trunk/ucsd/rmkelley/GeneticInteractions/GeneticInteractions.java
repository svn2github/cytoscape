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
    //RestrictedMinHeap lethalCoPPs = lpf.findLethalCoPPs(GeneticInteractions.MAX,GeneticInteractions.MIN,GeneticInteractions.THREAD_COUNT);
    HashSet lethalCoPPs = lpf.findLethalCoPPs(GeneticInteractions.MAX,GeneticInteractions.MIN,GeneticInteractions.THREAD_COUNT);

    LethalCoPPOverlapper lco = new LethalCoPPOverlapper();
    lco.overlapPaths();
    lco.getCount();
}
    


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




