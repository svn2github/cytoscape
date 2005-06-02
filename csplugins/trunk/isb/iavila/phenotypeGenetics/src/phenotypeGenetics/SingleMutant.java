/**
 * A representation of a single mutant containing information about gene name, 
 * allele, and allele form.
 *
 * @author thorsson
 */
package phenotypeGenetics;

import java.io.*;
import java.util.*;

public class SingleMutant {
  
  protected String name;       // canonical name
  protected String commonName; 
  protected String allele;     
  protected int alleleForm;    
  // environment VT:Consider whether to keep this
  // iliana wrote: I got rid of this one
  //protected PhenoEnvironment pe; 
  
  /**  
   * Constructor
   */ 
  public SingleMutant (){
    this.alleleForm = Condition.ALLELEFORM_UNASSIGNED;
  }//SingleMutant

  /**
   * Test for absence of all data
   */
  public boolean isEmpty (){
    return (name == null && commonName == null && allele == null &&
            alleleForm == Condition.ALLELEFORM_UNASSIGNED);
  }//isEmpty

  /**
   * Returns a short representation of the Single Mutant
   */
  public String toShortString (){
    return new String(commonName + "(" + Condition.alleleFormToString(alleleForm) + ")");
  } //toShortString

  /**
   * Returns the canonical name of the Mutant
   */
  public String getName (){
    return this.name;
  }//getName

  /**
   * Sets the canonical name of the mutant
   */
  public void setName (String n){
    this.name = n;
  }//setName

  /**
   * Returns the common Name of the mutant
   */
  public String getCommonName (){
    return this.commonName;
  }//getCommonName
  
  /**
   * Sets the common name of the mutant
   */
  public void setCommonName (String n){
    this.commonName = n;
  }//setCommonName

  /**
   * Gets the allele of the mutant
   */
  public String getAllele (){
    return this.allele;
  }//getAllele

  /**
   * Sets the allele of the mutant
   */
  public void setAllele (String a){
    this.allele = a;
  }//setAllele

  /**
   * Gets the allele form of the mutant
   */
  public int getAlleleForm (){
    return this.alleleForm;
  }//getAlleleForm

  /**
   * Sets the allele form of the mutant
   */
  public void setAlleleForm (int f){
    this.alleleForm = f;
  }//setAlleleForm

}//class SingleMutant
