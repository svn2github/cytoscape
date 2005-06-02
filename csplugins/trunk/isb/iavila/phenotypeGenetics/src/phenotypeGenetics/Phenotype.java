/**
 * Some observable attribute of an organism, usually the consequence of
 * a genetic or environmental manipulation in an experiment.
 *
 * @author original author not documented
 * @author Iliana Avila refactored
 */

package phenotypeGenetics;

import java.io.*;
import java.util.*;

public class Phenotype {

  /**
   * A suffix for phenotype names, denoting variability/error
   */  
  public final static String deviationString = " error";
  
  /**
   * The name of the phenotype
   */
  protected String name;
  
  /**
   * The value of this phenotype, could be numerical or qualitative
   */
  protected String value;
  
  /**
   * Constructor, does nothing
   */
  public Phenotype (){}//Phenotype

  /**
   * @param name the name of this observation
   * @param value the value of the observation
   */
  public Phenotype (String name, String value){
    this.name = name;
    this.value = value;
  }//ctor
  
  /**
   * @return true if name or value are not set
   */
  public boolean isEmpty (){
    return(this.name == null || this.value == null);
  }//isEmpty
  
  /**
   * Sets the name fot this obsercation
   */
  public void setName (String newValue){
    this.name = newValue;
  }//setName

  /**
   * @return the name of this observation
   */
  public String getName (){
    return this.name;
  }//getName

  /**
   * Sets the value for this observation
   */
  public void setValue (String newValue){
    this.value = newValue;
  }//setValue
  
  /**
   * @return the value for this observation
   */
  public String getValue (){
    return value;
  }//getValue

  /**
   * @return the String description of this Phenotype
   */
  public String toString (){
    StringBuffer sb = new StringBuffer ();
    sb.append (name);
    sb.append (" = ");
    sb.append (value);
    
    return sb.toString ();
  } // toString
  
}//Phenotype
