package ucsd.rmkelley.BetweenPathway;
import java.util.*;

public class NetworkModel implements Comparable{
  public NetworkModel(int ID,Set one,Set two,double score, double physical_score, double genetic_score){
    this.ID = ID;
    this.one = one;
    this.two = two;
    this.score = score;
    this.physical_score = physical_score;
    this.genetic_score = genetic_score;
  }

  public int compareTo(Object o){
    return -(new Double(score)).compareTo(new Double(((NetworkModel)o).score));
  }

  public Set one;
  public Set two;
  public int ID;
  public double score,physical_score,genetic_score;
}
