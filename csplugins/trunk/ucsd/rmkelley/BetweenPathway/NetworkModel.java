package ucsd.rmkelley.BetweenPathway;
import java.util.*;

public class NetworkModel implements Comparable{
  public NetworkModel(int ID,Set one,Set two,double score, double physical_source_score, double physical_target_score, double genetic_score){
    this.ID = ID;
    this.one = one;
    this.two = two;
    this.score = score;
    this.physical_source_score = physical_source_score;
    this.physical_target_score = physical_target_score;
    this.genetic_score = genetic_score;
  }

  public int compareTo(Object o){
    return -(new Double(score)).compareTo(new Double(((NetworkModel)o).score));
  }

  public Set one;
  public Set two;
  public int ID;
  public double score,physical_source_score,physical_target_score,genetic_score;
}
