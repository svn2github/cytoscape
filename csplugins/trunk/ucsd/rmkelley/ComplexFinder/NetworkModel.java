package ucsd.rmkelley.ComplexFinder;
import java.util.*;

public class NetworkModel implements Comparable{
  public NetworkModel(int ID,Set one,double score){
    this.ID = ID;
    this.one = one;
    this.score = score;
  }

  public int compareTo(Object o){
    return -(new Double(score)).compareTo(new Double(((NetworkModel)o).score));
  }

  public Set one;
  public int ID;
  public double score;
}
