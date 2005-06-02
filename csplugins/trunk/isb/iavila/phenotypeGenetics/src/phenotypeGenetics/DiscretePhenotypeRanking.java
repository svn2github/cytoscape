/**
 * An object that holds the names of discrete phenotypes and the ranks of their
 * discrete values. For example, for discrete phenotype "cell_aliveness" the ranked
 * values could be "dead, alive" where "alive" has a higher rank than "dead" because
 * it is to the right of "dead". It is possible for two values to have the same rank,
 * this is supported by 2D data structure (very much like a 2D array).
 *
 * @author Iliana Avila-Campillo
 * @version 1.0
 */

package phenotypeGenetics;
import java.util.*;


public class DiscretePhenotypeRanking {
  
  /**
   * A Map of Strings (phenotype names) to ArrayLists.
   * These ArrayLists contain ArrayLists themselves.
   * The 2nd level ArrayLists contain Strings representing
   * the discrete values of the phenotype. The positions of
   * the phenotype discrete values in the 2nd level ArrayLists
   * denote their ranking with respect to the other values for 
   * that phenotype. The higher the position, the higher the
   * ranking.
   */
  protected Map phenotypeBins;
  
  /**
   * A set of the discrete phenotypes that are ranked.
   */
  protected Set rankedPhenotypes;
  

  /**
   * Constructor
   */
  public DiscretePhenotypeRanking (){
    this.phenotypeBins = new HashMap();
    this.rankedPhenotypes = new HashSet();
  }//DiscretePhenotypeRanking

  /**
   * @return true if the values for the given phenotype have been ranked, false if
   * they have not been ranked, or if the phenotype does not exist
   */
  public boolean isPhenotypeRanked (String phenotype_name){
    return this.rankedPhenotypes.contains(phenotype_name);
  }//isPhenotypeRanked

  /**
   * @return an array of ranked phenotypes
   */
  public String [] getRankedPhenotypes (){
    return (String[])rankedPhenotypes.toArray(new String[0]);
  }//getRankedPhenotypes
  
  /**
   * @return a Map of Strings (phenotype names) to Lists of Collections
   * containing Strings representing discrete phenotype values
   */
  protected Map getPhenotypeBins (){
    return this.phenotypeBins;
  }//getPhenotypeBins

  /**
   * @return the currently highest rank for the given phenotype-name, 
   * the minimum rank is always 0, returns -1 if the phenotype-name does
   * not exist
   */
  public int getHighestRank (String phenotype_name){
    ArrayList phenotypeBin = (ArrayList)this.phenotypeBins.get(phenotype_name);
    if(phenotypeBin == null){
      return -1;
    }
    if(phenotypeBin.size() == 0){
      return 0;
    }
    return phenotypeBin.size()-1;
  }//getHighestRank

  /**
   * @return number of discrete phenotype entries
   */
  public int getNumPhenotypes (){
    return this.phenotypeBins.size();
  }//getNumPhenotypes

  /**
   * Sets the values for the given phenotype_name and remembers that the
   * values are not yet ranked
   */
  public void setUnrankedPhenotypeValues (String phenotype_name, String [] values){
    removePhenotypeName(phenotype_name);
    List allBins = new ArrayList();
    for(int i = 0; i < values.length; i++){
      List aBin = new ArrayList();
      aBin.add(values[i]);
      allBins.add(aBin);
    }//for i
    this.phenotypeBins.put(phenotype_name, allBins);
  }//setUnrankedPhenotypeValues

  /**
   * @return a List of Collections representing the ranking of the given phenotype
   */
  public List getPhenotypeRanking (String phenotype_ranking){
    return (List)this.phenotypeBins.get(phenotype_ranking);
  }//getPhenotypeRanking

  /**
   * Sets the ranking for the given phenotype (removes old one) and remembers
   * that the phenotype has been ranked
   *
   * @param phenotype_name the name of the phenotype
   * @param ranking a List of Collections that contain the phenotype's
   * discrete values, the position of the Collections in the list reflect
   * the rank of their members: the smaller their index in the List, the lower 
   * their rank
   */
  public void setPhenotypeRanking (String phenotype_name, List ranking){
    
    removePhenotypeName(phenotype_name);
    
    Collection [] collections = (Collection[])ranking.toArray(new Collection[0]);
    for(int i = 0; i < collections.length; i++){
      Collection currentCollection = collections[i];
      Iterator it = currentCollection.iterator();
      while(it.hasNext()){
        String phenoValue = (String)it.next();
        setPhenotypeValueRank(phenotype_name,phenoValue,i);
      }//for j
    }//for i
    
    this.rankedPhenotypes.add(phenotype_name);
  }//setPhenotypeRanking

  /**
   * For the given phenotype_name it adds the given phenotype_value with the given
   * rank.
   */
  protected void setPhenotypeValueRank (String phenotype_name, 
                                        String phenotype_value, 
                                        int rank){
    // adds the phenotype if it is not there:
    addPhenotypeName(phenotype_name);
    
    ArrayList allBins = (ArrayList)this.phenotypeBins.get(phenotype_name);
    ArrayList aBin;
    if(allBins.size() <= rank){
      aBin = new ArrayList();
      allBins.add(rank,aBin);
    }else{
      aBin = (ArrayList)allBins.get(rank);
    }
    aBin.add(phenotype_value);
  }//setPhenotypeValueRank

  /**
   * Adds the given phenotype-name (if not added already) to this object
   */
  public void addPhenotypeName (String phenotype_name){
    ArrayList phenotypeBin = (ArrayList)this.phenotypeBins.get(phenotype_name);
    if(phenotypeBin == null){
      phenotypeBin = new ArrayList();
      this.phenotypeBins.put(phenotype_name,phenotypeBin);
    }//if
  }//addPhenotypeName
  
  /**
   * Removes the given phenotype-name from this object
   */
  public void removePhenotypeName (String phenotype_name){
    this.phenotypeBins.remove(phenotype_name);
    this.rankedPhenotypes.remove(phenotype_name);
  }//removePhenotypeName

  /**
   * @return the rank for the given phenotype-value of the given phenotype-name
   * -1 if the phenotype-name does not exist or the value is not in that phenotype
   */
  public int getRank (String phenotype_name, String phenotype_value){
    ArrayList phenotypeB = (ArrayList)this.phenotypeBins.get(phenotype_name);
    if(phenotypeB == null){
      return -1;
    }
    ArrayList [] bins = (ArrayList [])phenotypeB.toArray(new ArrayList[0]);
    for(int i = 0; i < bins.length; i++){
      ArrayList values = bins[i];
      if(values.contains(phenotype_value)){
        return i;
      }
    }//for i

    return -1;
  }//getRank

  /**
   * @param phenotype_name the phenotype-name in which to look for the
   * given phenotype-values
   * @param wt_val the wild-type value
   * @param a_val the a value
   * @param b_val the b value
   * @param ab_val the ab value
   * @return the DiscretePhenoValueInequality that encodes the given set of
   * phenotype-values for the given phenotype-name
   */
  public DiscretePhenoValueInequality getDiscretePhenoValueIneq 
    (
     String phenotype_name,
     String wt_val, String a_val,
     String b_val, String ab_val){
    
    DiscretePhenoValueInequality ineq = 
      DiscretePhenoValueInequality.getPhenoInequality(
                                                      getRank(phenotype_name,wt_val),
                                                      getRank(phenotype_name,a_val),
                                                      getRank(phenotype_name,b_val),
                                                      getRank(phenotype_name,ab_val));
    return ineq;
  }//getDiscretePhenoValueIneq

  /**
   * @return an unranked array of phenotype values for the given phenotype
   */
  public String [] getUnrankedValues (String phenotype_name){
    
    ArrayList phenotypeB = (ArrayList)this.phenotypeBins.get(phenotype_name);
    List vals = new ArrayList();
    if(phenotypeB == null){
      return (String[])vals.toArray(new String[0]);
    }
    ArrayList [] allBins = (ArrayList[])phenotypeB.toArray(new ArrayList[0]);
    for(int i = 0; i < allBins.length; i++){
      ArrayList bin = allBins[i];
      Iterator it = bin.iterator();
      while(it.hasNext()){
        String value = (String)it.next();
        vals.add(value);
      }//while it
    }//for i
    
    return (String[])vals.toArray(new String[0]);
  }//getUnrankedValues

  /**
   * @return true of the given phenotype-name is in this DiscretePhenotypeRanking
   */
  public boolean containsPhenotype (String phenotype_name){
    return this.phenotypeBins.containsKey(phenotype_name);
  }//containsPhenotype

  /**
   * Merges other_ranking to this DiscretePhenotypeRanking
   *
   * @return an array of phenotype_names for which there were conflicts (the ranking
   * for same phenotype-names is not equal, in this case, the ranking of this 
   * DiscretePhenotypeRanking is kept)
   */
  public String [] merge (DiscretePhenotypeRanking other_ranking){
    
    ArrayList unmatchedPhenos = new ArrayList();
    Map otherPhenotypeBins = other_ranking.getPhenotypeBins();
    Set keySet = otherPhenotypeBins.keySet();
    String [] otherPhenotypeNames = (String[])keySet.toArray(new String[0]);
    
    for(int i = 0; i < otherPhenotypeNames.length; i++){
    
      if(containsPhenotype(otherPhenotypeNames[i])){
        
        List otherBins = (List)otherPhenotypeBins.get(otherPhenotypeNames[i]);
        List thisBins = (List)this.phenotypeBins.get(otherPhenotypeNames[i]);
        
        // are they ranked in both?
        if(  (isPhenotypeRanked(otherPhenotypeNames[i]) && 
              !other_ranking.isPhenotypeRanked(otherPhenotypeNames[i]))
             ||
             (!isPhenotypeRanked(otherPhenotypeNames[i]) &&
              other_ranking.isPhenotypeRanked(otherPhenotypeNames[i]))
             ){
          unmatchedPhenos.add(otherPhenotypeNames[i]);
          break;
        }// ranked in both

        if(otherBins.size() != thisBins.size()){
          unmatchedPhenos.add(otherPhenotypeNames[i]);
        }else{
        
          // same size, same contents?
          Iterator otherIt = otherBins.iterator();
          Iterator thisIt = thisBins.iterator();
          while(otherIt.hasNext()){
            List otherB = (List)otherIt.next();
            List thisB = (List)thisIt.next();
            if(otherB.size() != thisB.size()){
              unmatchedPhenos.add(otherPhenotypeNames[i]);
              break;
            }
            // same size, same contents?
            Iterator otherBit = otherB.iterator();
            while(otherBit.hasNext()){
              String otherValue = (String)otherBit.next();
              if(!thisB.contains(otherValue)){
                unmatchedPhenos.add(otherPhenotypeNames[i]);
                break;
              }
            }//while otherBit
          }//while otherIt
          
        }//else same size
        
      }else{
        if(other_ranking.isPhenotypeRanked(otherPhenotypeNames[i])){
          System.out.println("setting ranked phenotypes for " + otherPhenotypeNames[i]);
          setPhenotypeRanking(otherPhenotypeNames[i],
                              (List)otherPhenotypeBins.get(otherPhenotypeNames[i]) );
        }else{
          System.out.println("setting UNranked phenotypes for " + otherPhenotypeNames[i]);
          setUnrankedPhenotypeValues(otherPhenotypeNames[i],
                                     other_ranking.getUnrankedValues(otherPhenotypeNames[i]));
        }
      }
    }//for i
    
    return (String[])unmatchedPhenos.toArray(new String[0]);
  }//merge

  /**
   * @return an array of the phenotype names in this DiscretePhenotypeRanking
   */
  public String [] getPhenotypeNames (){
    Set keys = this.phenotypeBins.keySet();
    return (String[])keys.toArray(new String[0]);
  }//getPhenotypeNames

}//class DiscretePhenotypeRanking
