package ucsd.rmkelley.ComplexFinder;
import java.util.*;
import ucsd.rmkelley.Util.*;

public class NetworkModel extends Pathway implements Comparable{
  public NetworkModel(int ID,Collection nodes,double score){
    this.ID = ID;
    super.nodes = nodes;
    super.score = score;
  }

  public int compareTo(Object o){
    return -(new Double(score)).compareTo(new Double(((NetworkModel)o).score));
  }

  public int ID;
}
