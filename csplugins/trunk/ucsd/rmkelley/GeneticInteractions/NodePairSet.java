package csplugins.ucsd.rmkelley.GeneticInteractions;


//java import statements
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;

//GINY import statements
import giny.model.Node;
import giny.model.Edge;

/**
 * This class keeps track of a set of unordered pairs of nodes
 */
public class NodePairSet{
  private HashMap node2NodeSet;
  public NodePairSet(){
    node2NodeSet = new HashMap();
  }

  public NodePairSet(Collection edgeSet){
    node2NodeSet = new HashMap();
    for (Iterator edgeIt = edgeSet.iterator();edgeIt.hasNext();) {
      Edge current = (Edge)edgeIt.next();
      add(current.getSource(),current.getTarget());
    } // end of for ()
    
  }

  public void remove(Node one,Node two){
    if(!isGreater(one,two)){
      Node temp = one;
      one = two;
      two = temp;
    }
    HashSet nodeSet = (HashSet)node2NodeSet.get(one);
    if (nodeSet == null) {
      return;
    } // end of if ()
    else {
      nodeSet.remove(two);
    } // end of if ()
  } // end of else

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
