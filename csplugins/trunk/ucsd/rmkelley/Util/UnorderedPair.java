package ucsd.rmkelley.Util;
import giny.model.Node;

public class UnorderedPair extends OrderedPair {
  public UnorderedPair(Node one, Node two){
    if(one.getRootGraphIndex() > two.getRootGraphIndex()){
      super.one = one;
      super.two = two;
    }
    else{
      super.one = two;
      super.two = one;
    }
  }
}
