/**
 * A "mode" that identifies a set of phenotype inequalities.
 *
 * @author Iliana Avila-Campillo
 */
package phenotypeGenetics;
import java.util.*;

public class Mode {

  /**
   * The name of the Mode when none has been set
   */
  public static final String UNASIGNED_NAME = "unasigned";

  /**
   * A static Map that maps the name of a Mode (String), 
   * to the Mode that represents that String
   */
  public static Map modeNameToMode = new HashMap();
  
  /**
   * The name of this Mode
   */
  protected String name;
  
  /**
   * The phenotype inequalities that this Mode represents
   * (list of DiscretePhenoValueInequality objects)
   */
  protected List phenotypeInequalities;

  
  /**
   * Initializes the name of this mode to UNASIGNED_NAME and
   * it creates an empty list of phenotype inequalities.
   */
  public Mode (){
    this(UNASIGNED_NAME);
  }//constructor

  /**
   * Constructor
   *
   * @param name the name for this Mode
   */
  public Mode (String name){
    this.phenotypeInequalities = new ArrayList();
    this.name = name;
    Mode.modeNameToMode.put(this.name, this);
  }//constructor
  
  /**
   * Sets the name of this Mode
   */
  public void setName (String new_name){
    this.name = new_name;
    Mode.modeNameToMode.put(this.name, this);
  }//setName

  /**
   * @return the name of this Mode
   */
  public String getName (){
    return this.name;
  }//getName

  /**
   * Sets a list of phenotype inequalities that this 
   * Mode represents
   * 
   * @param pheno_inequalities
   */
  public void setPhenotypeInequalities (List pheno_inequalities){
    this.phenotypeInequalities = pheno_inequalities;
  }//setPhenotypeInequalities

  /**
   * @return the phenotype inequalities that this Mode represents
   */
  public List getPhenotypeInequalities (){
    return this.phenotypeInequalities;
  }//getPhenotypeInequalities

  /**
   * @return true of this Mode contains at least one DiscretePhenoValueInequality
   * that is directional
   */
  public boolean isDirectional (){
    Iterator it = getPhenotypeInequalities().iterator();
    while(it.hasNext()){
      DiscretePhenoValueInequality ineq = (DiscretePhenoValueInequality)it.next();
      if(ineq.getDirection().equals(DiscretePhenoValueInequality.A_TO_B) ||
         ineq.getDirection().equals(DiscretePhenoValueInequality.B_TO_A)){
        return true;
      }
    }//while it
    return false;
  }//isDirectional

  /**
   * Removes the given DiscretePhenoValueInequality from this Mode
   */
  public void removePhenotypeInequality (DiscretePhenoValueInequality ineq){
    this.phenotypeInequalities.remove(ineq);
  }//removePhenotypeInequality
  
  /**
   * Adds the given DiscretePhenoValueInequality to this Mode
   */
  public void addPhenotypeInequality (DiscretePhenoValueInequality ineq){
    this.phenotypeInequalities.add(ineq);
  }//addPhenotypeInequality

  /**
   * @return the name of this Mode
   */
  public String toString (){
    return getName();
  }//toString

  /**
   * @return true if the other Mode and this Mode have the same name
   */
  public boolean equals (Object other_object){
    if(other_object instanceof Mode){
      boolean equals = getName().equals(((Mode)other_object).getName());
      return equals;
    }
    return false;
  }//equals
  
}//class mode
