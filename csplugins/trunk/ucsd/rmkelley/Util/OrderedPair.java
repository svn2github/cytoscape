package ucsd.rmkelley.Util;
import giny.model.Node;

public class OrderedPair{
  protected Node one;
  protected Node two;
  public OrderedPair(Node one,Node two){
    this.one = one;
    this.two = two;
  }

  protected OrderedPair(){}
  public boolean equals(Object o){
    OrderedPair other = (OrderedPair)o;
    return (one.getRootGraphIndex() == other.one.getRootGraphIndex()) &&
      (two.getRootGraphIndex() == other.two.getRootGraphIndex());
  }

  public int hashCode(){
    return one.getRootGraphIndex() + two.getRootGraphIndex();
  }

  public String toString(){
    return ""+one+","+two;
  }
}
