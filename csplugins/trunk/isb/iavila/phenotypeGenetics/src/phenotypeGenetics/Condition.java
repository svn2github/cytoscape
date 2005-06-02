/**
 *  An experimental condition, typically an environmental or genetic manipulation
 *  whose consequences are a change in the organism's phenotype. More specifically,
 *  a class that contains the name of a condition and its value, its category, and
 *  allele information (if genetic).
 *
 * @author original author not documented
 * @author Iliana Avila refactored
 * @version 2.0
 */

package phenotypeGenetics;
import java.io.*;
import java.util.*;

public class Condition {
    
  // define the recognized categories:
  public final static int CATEGORY_UNASSIGNED = 0;
  public final static int GENETIC = 1;
  public final static int ENVIRONMENTAL = 2;
  public final static int CATEGORY_SENTINEL = 3;  // marks end-of-range
  
  // define the recognized alleleForms
  public final static int ALLELEFORM_UNASSIGNED = 100;
  public final static int LF = 101;
  public final static int LF_PARTIAL = 102;
  public final static int GF = 103;
  public final static int GF_PARTIAL = 104;
  public final static int DN = 105;
  public final static int ALLELEFORM_SENTINEL = 106;  // marks end-of-range
  
  protected int alleleForm;
  protected String allele;
  
  protected String name;
  protected String value;

  protected String gene;
  protected int category;
  
  /**
   * Constructor
   */
  public Condition (){
    this.category = CATEGORY_UNASSIGNED;
    this.alleleForm = ALLELEFORM_UNASSIGNED;
    this.allele = null;
  }//ctor
  
  /**
   * @return true if one of category, alleleForm, allele, nameToValue are not set
   */
  public boolean isEmpty(){
 
    return ( this.category == CATEGORY_UNASSIGNED  
             && this.alleleForm == ALLELEFORM_UNASSIGNED 
             && this.name == null
             && this.value == null
             && this.allele == null );
  }//isEmpty

  /**
   * Sets the category for this Condition
   *
   * @throws IllegalArgumentException if the new category is not recognized
   */
  public void setCategory (int newValue) throws IllegalArgumentException{
    if (newValue < CATEGORY_UNASSIGNED || newValue >= CATEGORY_SENTINEL)
      throw new IllegalArgumentException ("illegal category value: " + newValue);
    this.category = newValue;
  }//setCategory
  
  /**
   * Sets the allele form for this Condition
   *
   * @throws IllegalArgumentException if the new alleleForm is not recognized
   */
  public void setAlleleForm (int newValue) throws IllegalArgumentException{
    if (newValue < ALLELEFORM_UNASSIGNED || newValue >= ALLELEFORM_SENTINEL)
      throw new IllegalArgumentException ("illegal alleleForm value: " + newValue);
    this.alleleForm = newValue;
  }//setAlleleForm

  /**
   * Sets the allele for this Condition
   */
  public void setAllele (String newValue) {
    this.allele = newValue;
  }//setAllele

  /**
   * @return a String description for a given category
   */
  public static String categoryToString (int value){
    switch (value) {
    case CATEGORY_UNASSIGNED:
      return "UNASSIGNED";
    case GENETIC:
      return "GENETIC";
    case ENVIRONMENTAL:
      return "ENVIRONMENTAL";
    default:
      return "ILLEGAL VALUE";
    } // switch on value
  }//categoryToString
  
  /**
   * @return a String description of the allele form
   */
  public static String alleleFormToString (int value){
    
    switch (value) {
    case ALLELEFORM_UNASSIGNED:
      return "UNASSIGNED";
    case LF:
      return "lf";
    case LF_PARTIAL:
      return "lf(partial)";
    case GF:
      return "gf";
    case GF_PARTIAL:
      return "gf(partial)";
    case DN:
      return "dn";
    default:
      return "ILLEGAL VALUE";
    } // switch on value
  } // alleleFormToString
  
  /**
   * Set the name of this condition
   */
  public void setName (String name){
    this.name = name;
  }//setName

  /**
   * @return the name of this condition
   */
  public String getName (){
    return this.name;
  }//getName

  /**
   * Set the value of this condition, for example, for condition "cell type"
   * set "2n"
   */
  public void setValue (String value){
    this.value = value;
  }//setValue

  /**
   * @return the value of this condition
   */
  public String getValue (){
    return this.value;
  }//getValue

  /**
   * Sets the gene for this Condition
   */
  public void setGene (String newValue){
    this.gene = newValue;
  }//setGene

  /**
   * @return the gene in this Condition
   */
  public String getGene (){
    return this.gene;
  }//getGene

  /**
   * @return the category for this Condition
   */
  public int getCategory (){
    return this.category;
  }//getCategory

  /**
   * @return the allele form for this experiment
   */
  public int getAlleleForm (){
    return this.alleleForm;
  }//getAlleleForm

  /**
   * @return the allele in this Condition
   */
  public String getAllele (){
    return this.allele;
  }//getAllele

  /**
   * @return the String description of this Condition
   */
  public String toString (){
    StringBuffer sb = new StringBuffer ();
    
    sb.append("name: " + this.name);
    sb.append("value: " + this.value);
    if(gene != null) sb.append ("gene: " + gene + "\n");
    sb.append ("category: " + categoryToString (this.category) + "\n");
    sb.append ("alleleForm: " + alleleFormToString (this.alleleForm) + "\n");
    sb.append ("allele: " + this.allele + "\n");
  
    return sb.toString ();
  }//toString
  
}//Condition
