package csplugins.ucsd.rmkelley.GeneticInteractions;

//java import statements
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;

//giny import statements
import giny.model.Node;
import giny.model.Edge;
import giny.model.GraphPerspective;

//cytoscape import statements
import cytoscape.view.CyWindow;


/**
 * This class represents a lethal CoPP. This is a pair of paths form a genetic interaction
 * which both end in the same lethal deletion. These paths are assumed to be non-overlapping
 * (in terms of nodes)
 */
public class LethalCoPP implements Comparable{
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
