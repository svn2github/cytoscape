package ucsd.rmkelley.BetweenPathway;
import java.util.*;

public class NetworkModel implements Comparable{
  public NetworkModel(int ID,Set one,Set two,double score){
    this.ID = ID;
    this.one = one;
    this.two = two;
    this.score = score;
  }

  public int compareTo(Object o){
    return -(new Double(score)).compareTo(new Double(((NetworkModel)o).score));
  }

  public Set one;
  public Set two;
  public int ID;
  public double score;
}
