package csplugins.ucsd.rmkelley.GeneticInteractions;

//java import statements
import java.util.HashSet;
import java.util.Vector;
import java.util.Set;
import java.util.List;
import java.util.Iterator;

//giny import statements
import giny.model.Edge;
import giny.model.Node;

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
