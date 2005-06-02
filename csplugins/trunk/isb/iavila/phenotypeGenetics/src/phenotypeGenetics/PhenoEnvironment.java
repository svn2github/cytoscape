/**
 * An enviroment consisting of multiple name-value pairs and the name of a phenotype
 *
 * @author original not documented
 * @author Iliana Avila (refactored)
 */
package phenotypeGenetics;

import java.io.*;
import java.util.*;

public class PhenoEnvironment implements Serializable {

  public final static String phenoNameString = "assay";
  
  /**
   * A Map of phenotype-names to values
   */
  protected Map environment;
  
  // Later: consider promoting to Vector or String array (iliana wrote: why?)
  protected String phenoName; 
     
  /**
   * Constructor
   */
  public PhenoEnvironment (){
    this.environment = new HashMap();
  }//PhenoEnvironment
  
  /**
   * Constructor
   */
  public PhenoEnvironment (String phenoName, Map environment){
    this.phenoName = phenoName;
    this.environment = environment; 
  }//PhenoEnvironment
  
  /**
   * Test for absence of all data
   */   
  public boolean isEmpty (){
    return (this.environment.isEmpty() && this.phenoName == null); 
  }//isEmpty
  
  /**
   * Set the environment
   */
  public void setEnvironment (Map newEnv){
    this.environment = newEnv ; 
  }//setEnvironment
  
  /**
   * Set the phenotype name 
   */
  public void setPhenoName (String newPhenoName ){
    this.phenoName = newPhenoName ;
  }//setPhenoName
  
  /**
   * Get the environment
   */
  public Map getEnvironment (){
    return this.environment;
  }//getEnvironment
  
  /**
   * Get the phenotype name 
   */
  public String getPhenoName (){
    return this.phenoName;
  }//getPhenoName
  
  /**
   * Represent as a <code>String</code> 
   */
  public String toString (){
    StringBuffer sb = new StringBuffer ();
    sb.append( this.phenoName  +", " );
    sb.append( this.environment.toString() );
    return sb.toString ();
  }//toString
 
}// PhenoEnvironment
