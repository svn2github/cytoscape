/**
 * A named collection of manipulations (<code>Condition</code>s) and their 
 * observed outcomes (<code>Phenotype</code>s) along with any number of 
 * annotations.
 *
 * @author Paul Shannon
 * @author Iliana Avila
 * @version 1.0
 * @see Condition
 * @see Phenotype
 */

package phenotypeGenetics;
import java.io.*;
import java.util.*;

public class Experiment {

  protected String name;
  protected List notes;
  protected List conditions;
  protected List observations;
  
  /**
   * Constructor, calls Experiment(null)
   */
  public Experiment (){
    this(null);
  }//Experiment
  
  /**
   * @param name the name of this Experiment
   */
  public Experiment (String name){
    this.name = name;
    this.notes = new ArrayList();
    this.conditions = new ArrayList();
    this.observations = new ArrayList();
  }//Experiment
  
  /**
   * @return true if no name assigned, no notes assigned, or no observations assigned
   * false otherwise
   */
  public boolean isEmpty (){
    return( this.name == null &&  
            this.notes.size() == 0 && 
            this.conditions.size() == 0 && 
            this.observations.size() == 0);
  }//isEmpty
  
  /**
   * Sets the name of this Experiment
   */
  public void setName (String newValue){
    this.name = newValue;
  }//setName
  
  /**
   * @return the name of this Experiment
   */
  public String getName (){
    return this.name;
  }//getName

  /**
   * Adds a note to this Experiment
   */
  public void addNote (String note){
    this.notes.add(note);
  }//addNote

  /**
   * Adds a Condition to this Experiment
   */
  public void addCondition (Condition condition){
    this.conditions.add(condition);
  }//addCondition

  /**
   * Adds a Phenotype to this Experiment
   */
  public void addObservation (Phenotype phenotype){
    this.observations.add (phenotype);
  }//addObservation

  /**
   * @return the notes in this Experiment
   */
  public String [] getNotes(){
    return (String[]) this.notes.toArray (new String [0]);
  }//getNotes

  /**
   * @return the Condition in this Experiment
   */
  public Condition [] getConditions (){
    return (Condition []) this.conditions.toArray (new Condition [0]);
  }//getConditions

  /**
   * @return the Phenotype in this Experiment
   */
  public Phenotype [] getObservations (){
    return (Phenotype []) this.observations.toArray (new Phenotype [0]);
  }//getObservations

  /**
   * @return the String representation of this Experiment
   */
  public String toString (){
    
    StringBuffer sb = new StringBuffer ();
    sb.append ("name: " + this.name + "\n");

    String [] notesArray = getNotes ();
    for (int i=0; i < notesArray.length; i++)
      sb.append ("note " + i + ") " + notesArray [i] + "\n");
    
    Condition [] conditionsArray = getConditions ();
    for (int i=0; i < conditionsArray.length; i++)
      sb.append ("condition " + i + ") " + conditionsArray [i] + "\n");
    
    Phenotype [] phenotypesArray = getObservations ();
    for (int i=0; i < phenotypesArray.length; i++)
      sb.append ("observation " + i + ") " + phenotypesArray [i] + "\n");
    
    return sb.toString ();

  }//toString

  /**
   * @return <code>Condition</code>s in which the category is GENETIC;
   */
  public Condition [] getGeneticConditions (){
    
    Condition [] allConditions = (Condition []) this.conditions.toArray (new Condition [0]);
    List genConditions = new ArrayList() ;
    int numConditions = this.conditions.size(); 

    for(int i = 0; i < numConditions; i++){
      Condition cond = allConditions[i] ; 
      if(cond.getCategory () == Condition.GENETIC )
        genConditions.add(cond); 
    }//for i
  
    Condition [] genConditionsArray =  
      (Condition [])genConditions.toArray(new Condition [0]);

    return genConditionsArray; 
  }//getGeneticConditions

  /**
   * @return <code>Condition</code>s in which the category is ENVIRONMENTAL 
   * as name-value pairs.  
   */
  public Map getEnvironmentalConditions () {
    
    Map returnMap = new HashMap() ; 

    Condition [] allConditions = (Condition [])this.conditions.toArray(new Condition [0]);
    List genConditions = new ArrayList() ;
    int numConditions = this.conditions.size(); 

    for(int i = 0; i < numConditions; i++){
      Condition cond = allConditions[i] ; 
      if(cond.getCategory() == Condition.ENVIRONMENTAL){
        returnMap.put(cond.getName(),cond.getValue());
      }//if
    }//for i
    return returnMap ; 
  }//getEnvironmentalConditions

  /**
   * @return <code>Condition</code>s in which a specific genetic manipulation has been 
   * performed
   * @param geneName the name of the manipulated gene
   */
  public Condition getGeneticConditionWithGene (String geneName){
    
    Condition [] allConditions = (Condition []) this.conditions.toArray (new Condition [0]);
    List genConditions = new ArrayList();
    int numConditions = this.conditions.size(); 
    Condition returnedCondition = new Condition(); 
  
    Condition cond = new Condition(); 
    for(int i = 0; i < numConditions; i++){
      cond = allConditions[i] ; 
      if(cond.getCategory () == Condition.GENETIC && cond.getGene().equals(geneName))
        genConditions.add(cond); 
    }//for i

    if(genConditions.size() == 1) 
      returnedCondition = (Condition)genConditions.get(0); 
   
    return returnedCondition; 
  }//getGeneticConditionWithGene

  /**
   * @return <code>Phenotype</code>s with a specific phenotype name 
   * @param phenoName   The name of the manipulated gene
   */
  public Phenotype getPhenotypeWithName ( String phenoName ){

    Phenotype [] allObservations = 
      (Phenotype [])this.observations.toArray(new Phenotype [0]);
    
    List phenoObservations = new ArrayList();
    int numObservations = this.observations.size(); 
    Phenotype returnedObservation = new Phenotype (); 
    
    Phenotype obs = new Phenotype(); 
    for(int i = 0; i < numObservations; i++){
      obs = allObservations[i] ; 
      if(obs.getName().equals(phenoName))
        phenoObservations.add(obs); 
    }//for i
    
    Phenotype [] obsWithPhenoName = 
      (Phenotype []) phenoObservations.toArray (new Phenotype [0]);
    
    if(phenoObservations.size() == 1) 
      returnedObservation = obsWithPhenoName[0];
    
    return returnedObservation ; 
  }//getPhenotypeWithName

}//Experiment
