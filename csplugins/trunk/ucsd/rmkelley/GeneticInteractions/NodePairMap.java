package csplugins.ucsd.rmkelley.GeneticInteractions;


//java import statements
import java.util.HashMap;

//Giny import statements
import giny.model.Node;

/**
 * This class maps from unorderpairs of nodes to an integer count. This is used to count the number
 * of copps that span any particular node pair in the CoPP model.
 */
public class NodePairMap{
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

  public void remove(Node one, Node two){
    if (!isGreater(one,two)) {
      Node temp = one;
      one = two;
      two = one;
    } // end of if ()
    HashMap node2Object = (HashMap)node2HashMap.get(one);
    if (node2Object == null) {
      return;
    } // end of if ()
    else {
      node2Object.remove(two);
    } // end of else
  }
  
  public Object get(Node one, Node two){
    //figure out the order in which we want to access the hash
    //first look at the hashCode, since that should be pretty quick
    if(!isGreater(one,two)){
      Node temp = one;
      one = two;
      two = temp;
    }
    HashMap node2Object = (HashMap)node2HashMap.get(one);
    if(node2Object == null){
      return null;
    }
    else{
      return node2Object.get(two);
    }
  }

  public void put(Node one, Node two,Object value){
    if(!isGreater(one,two)){
      Node temp = one;
      one = two;
      two = temp;
    }
    HashMap node2Object = (HashMap)node2HashMap.get(one);
    if(node2Object == null){
      node2Object = new HashMap();
      node2HashMap.put(one,node2Object);
    }
    node2Object.put(two,value);	
  }
}
