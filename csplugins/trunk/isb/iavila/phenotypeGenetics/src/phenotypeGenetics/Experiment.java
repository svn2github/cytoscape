/**  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
// Experiment.java: an experiment with conditions and observations
//------------------------------------------------------------------------------
// $Revision$  
// $Date$
// $Author$
//------------------------------------------------------------------------------
package phenotypeGenetics;
//------------------------------------------------------------------------------
import java.io.*;
import java.util.*;
//------------------------------------------------------------------------------
/**
 * A named collection of manipulations (<code>Condition</code>s) and their 
 * observed outcomes (<code>Phenotype</code>s) along with any number of 
 * annotations.
 *
 * @see Condition
 * @see Phenotype
 *
 * @version %I%, %G%
 * @author pshannon@systemsbiology.org
 */
public class Experiment {

  String name;
  Vector notes;
  Vector conditions;
  Vector observations;

  //-----------------------------------------------------------------------------
  public Experiment ()
  {
    this (null);
  }
  //-----------------------------------------------------------------------------
  public Experiment (String name)
  {
    this.name = name;
    notes = new Vector ();
    conditions = new Vector ();
    observations = new Vector ();

  } // ctor
  //-----------------------------------------------------------------------------
  public boolean isEmpty ()
  {
    return( name==null &&  notes.size()==0 && conditions.size()==0 && observations.size()==0 );
  }
  //-----------------------------------------------------------------------------
  public void setName (String newValue)
  {
    name = newValue;
  }
  //-----------------------------------------------------------------------------
  public String getName ()
  {
    return name;
  }
  //-----------------------------------------------------------------------------
  public void addNote (String note)
  {
    notes.add (note);
  }
  //-----------------------------------------------------------------------------
  public void addCondition (Condition condition)
  {
    conditions.add (condition);
  }
  //-----------------------------------------------------------------------------
  public void addObservation (Phenotype phenotype)
  {
    observations.add (phenotype);
  }
  //-----------------------------------------------------------------------------
  public String [] getNotes ()
  {
    return (String[]) notes.toArray (new String [0]);
  }
  //-----------------------------------------------------------------------------
  public Condition [] getConditions ()
  {
    return (Condition []) conditions.toArray (new Condition [0]);
  }
  //-----------------------------------------------------------------------------
  public Phenotype [] getObservations ()
  {
    return (Phenotype []) observations.toArray (new Phenotype [0]);
  }
  //-----------------------------------------------------------------------------
  public String toString ()
  {
    StringBuffer sb = new StringBuffer ();
    sb.append ("name: " + name + "\n");

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

  } // toString
  //-----------------------------------------------------------------------------
  /**
   * <code>Condition</code>s in which the category is GENETIC;
   *
   * @see Condition
   *
   * @author thorsson@systemsbiology.org
   */
  public Condition [] getGeneticConditions () {

    Condition [] allConditions = (Condition []) conditions.toArray (new Condition [0]);
    Vector genConditions = new Vector () ;
    int numConditions = conditions.size(); 

    for ( int i=0 ; i<numConditions ; i++ ){
      Condition cond = allConditions[i] ; 
      if ( cond.getCategory () == Condition.GENETIC  )
        genConditions.add(cond); 
    }
  
    Condition [] genConditionsArray =  
      (Condition []) genConditions.toArray (new Condition [0] );

    return genConditionsArray ; 

  }

  //-----------------------------------------------------------------------------
  /**
   * <code>Condition</code>s in which the category is ENVIRONMENTAL are returned as name-value pairs.  
   *
   * @see Condition
   *
   * @author thorsson@systemsbiology.org
   */
  public HashMap getEnvironmentalConditions () {

    HashMap returnMap = new HashMap() ; 

    Condition [] allConditions = (Condition []) conditions.toArray (new Condition [0]);
    Vector genConditions = new Vector () ;
    int numConditions = conditions.size(); 

    for ( int i=0 ; i<numConditions ; i++ ){
      Condition cond = allConditions[i] ; 
      if ( cond.getCategory () == Condition.ENVIRONMENTAL  ){

        // This condition itself containts a HashMap 
        // In practice (?), there is only one name-value pair, which we pull out and place in the returnMap
        HashMap map=cond.getMap();
        String  [] keys = (String [])map.keySet().toArray (new String [0]);
        if ( keys.length != 1 )
          System.out.println("Error: more than one environment in this single condition");
        String environment = keys[0];
        String value = (String) map.get( environment );
        returnMap.put(environment,value);

      }
      //genConditions.add(cond); 
    }
  
    //Condition [] genConditionsArray =  (Condition []) genConditions.toArray (new Condition [0] );
    
    //return genConditionsArray ; 
    return returnMap ; 


  }

  //-----------------------------------------------------------------------------
  /**
   * <code>Condition</code> in which a specific genetic manipulation has been performed
   *
   * @param geneName   The name of the manipulated gene
   * @exception        Should occur if gene is found in more than one condition 
   *                   This has yet to be implemented
   * @see Condition
   *
   * @author thorsson@systemsbiology.org
   */
  public Condition getGeneticConditionWithGene ( String geneName ) {

    Condition [] allConditions = (Condition []) conditions.toArray (new Condition [0]);
    Vector genConditions = new Vector () ;
    int numConditions = conditions.size(); 
    Condition returnedCondition = new Condition (); 
  
    Condition cond = new Condition(); 
    for ( int i=0 ; i<numConditions ; i++ ){
      cond = allConditions[i] ; 
      if ( cond.getCategory () == Condition.GENETIC && cond.getGene().equals(geneName) )
        genConditions.add(cond); 
    }
  
    // Here, throw exception if genConditions.length is not >1

    if ( genConditions.size() == 1) returnedCondition = (Condition) genConditions.elementAt(0); 
    // If no matching conditions were found, returnedCondition remains empty

    return returnedCondition ; 

  }
  //-----------------------------------------------------------------------------

  /**
   * <code>Phenotype</code> with a specific phenotype name 
   *
   * @param phenoName   The name of the manipulated gene
   * @exception        Should occur if phenotype is found in more than one observation
   *                   This has yet to be implemented
   * @see Phenotype
   *
   * @author thorsson@systemsbiology.org
   */
  public Phenotype getPhenotypeWithName ( String phenoName ) {

    Phenotype [] allObservations = (Phenotype []) observations.toArray (new Phenotype [0]);
    Vector phenoObservations = new Vector () ;
    int numObservations = observations.size(); 
    Phenotype returnedObservation = new Phenotype (); 

    Phenotype obs = new Phenotype(); 
    for ( int i=0 ; i<numObservations ; i++ ){
      obs = allObservations[i] ; 
      if ( obs.getName().equals(phenoName) )
        phenoObservations.add(obs); 
    }
  
    // Here, throw exception if genObservations.length is not >1

    Phenotype [] obsWithPhenoName = 
      (Phenotype []) phenoObservations.toArray (new Phenotype [0]);
    if ( phenoObservations.size() == 1) returnedObservation = obsWithPhenoName[0];
    // If no matching observations were found, Observation remains unassigned
  
    return returnedObservation ; 

  }

  //-----------------------------------------------------------------------------

} // Experiment
