/**  Copyright (c) 2005 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
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
/**
 * A set of experiments on an organism.
 *
 * @see Experiment
 * @see ProjectXmlReader
 *
 * @version %I%, %G% 
 * @author pshannon@systemsbiology.org
 * @author thorsson@systemsbiology.org
 */
package phenotypeGenetics;
import java.io.*;
import java.util.*;

public class Project {

  String name;
  String organism;
  Vector notes;
  Vector experiments;

  //-----------------------------------------------------------------------------
  /**
   * Constructs a new Project object specified by a name and organism.
   */
  public Project (String name, String organism)
  {
    this.name = name;
    this.organism = organism;

    notes = new Vector ();
    experiments = new Vector ();
  } // ctor
  
  public Project () {
    this.notes = new Vector();
    this.experiments = new Vector();
  }
  //-----------------------------------------------------------------------------
  /**
   * Sets the name of the Project.
   */
  public void setName (String newValue)
  {
    name = newValue;
  }
  //-----------------------------------------------------------------------------
  /**
   * Gets the name of the Project.
   */
  public String getName ()
  {
    return name;
  }
  //-----------------------------------------------------------------------------
  /**
   * Sets the name of the organism for the Project.
   */
  public void setOrganism (String newValue)
  {
    organism = newValue;
  }
  //-----------------------------------------------------------------------------
  /**
   * Gets the name of the organism for the Project.
   */
  public String getOrganism ()
  {
    return organism;
  }
  //-----------------------------------------------------------------------------
  /**
   * Add a text note to the Project.
   */
  public void addNote (String note)
  {
    notes.add (note);
  }
  //-----------------------------------------------------------------------------
  /**
   * Add an Experiment to the Project.
   */
  public void addExperiment (Experiment experiment)
  {
    experiments.add (experiment);
  }
  //-----------------------------------------------------------------------------
  /**
   * The number of notes in the Project.
   */
  public int numberOfNotes ()
  {
    return notes.size ();
  }
  //-----------------------------------------------------------------------------
  /**
   * Get notes on the Project.
   */
  public String [] getNotes ()
  {
    return (String []) notes.toArray (new String [0]);
  
  }
  //-----------------------------------------------------------------------------
  /**
   * The number of Experiment in the Project.
   */
  public int numberOfExperiments ()
  {
    return experiments.size ();
  }
  //-----------------------------------------------------------------------------
  /**
   * Returns all Experiments in the Project.
   */
  public Experiment [] getExperiments ()
  {
    return (Experiment []) experiments.toArray (new Experiment [0]);
  }
  //-----------------------------------------------------------------------------

  /**
   * Converts Project to String
   */
  public String toString ()
  {
    StringBuffer sb = new StringBuffer ();
    sb.append ("name: " + name + "\n");
    sb.append ("organism: " + organism + "\n");

    String [] notesArray = getNotes ();
    for (int i=0; i < notesArray.length; i++)
      sb.append ("note " + i + ") " + notesArray [i] + "\n");

    Experiment [] experimentsArray = getExperiments ();
    for (int i=0; i < experimentsArray.length; i++)
      sb.append ("experiment " + i + ") " + experimentsArray [i] + "\n");

    return sb.toString ();

  } // toString

  //-----------------------------------------------------------------------------
  /**
   * Add the contents of a project to this project
   * the organism and name are concatenated if they are not identical
   * The notes are added on to the current notes,
   * and all of the experiments are added
   *
   */
  public void concatenate (Project project) {
    String organism = project.getOrganism();
    if (this.organism  == null) {
      this.organism = new String(organism);
    } else if (!this.organism.equals(organism)) {
      this.organism = new String(this.organism+" "+organism);
    }
    String name = project.getName();
    if (this.name  == null) {
      this.name = new String(name);
    } else if (!this.name.equals(name)) {
      this.name = new String(this.name+" "+name);
    }
    int i;
    String[] notes = project.getNotes();
    for (i=0;i<notes.length;i++) {
      this.addNote(notes[i]);
    }
    Experiment[] exps = project.getExperiments();
    for (i=0;i<exps.length;i++) {
      this.addExperiment(exps[i]);
    }
  }

  //-----------------------------------------------------------------------------
  /**
   * Filter <code>Project</code>, requiring a fixed number of manipulated genes 
   * in each <code>Experiment</code>
   *
   * @param  numManip The number of manipulated genes. 
   * @author thorsson@systemsbiology.org
   */
  public Project filterByNumberOfMutants ( int numManip )
  {
    Experiment [] allExperiments = (Experiment []) experiments.toArray (new Experiment [0]); 
    String [] notesArray = (String []) notes.toArray( new String [0]);

    Project returnedProject = new Project( name, organism );
    for (int i=0;i<notes.size() ; i++)
      returnedProject.addNote( notesArray[i] );

    for ( int i=0 ; i<experiments.size() ; i++ ){
      Experiment exp = allExperiments[i];
      Condition [] conditionsGenetic = exp.getGeneticConditions ();
      if ( conditionsGenetic.length == numManip )
        returnedProject.addExperiment(exp); 
    }

    return returnedProject;

  }

  //-----------------------------------------------------------------------------
  /**
   * Filter <code>Project</code>, requiring certain genes to be manipulated 
   * in each <code>Experiment</code>
   *
   * @param  geneNames The names of manipulated genes. 
   * @author thorsson@systemsbiology.org
   */

  public Project filterByManipulatedGenes ( String [] geneNames )
  {
    Experiment [] allExperiments = (Experiment []) experiments.toArray (new Experiment [0]); 
    String [] notesArray = (String []) notes.toArray( new String [0]);

    Project returnedProject = new Project( name, organism );
    for (int i=0 ;i<notes.size() ; i++)
      returnedProject.addNote( notesArray[i] );

    for ( int i=0 ; i<experiments.size() ; i++ ){
      boolean cumulative = true; 
      Experiment exp = allExperiments[i];
      Condition [] conditionsGenetic = exp.getGeneticConditions ();
      for (int j=0 ; j<geneNames.length ; j++ ){
        Condition cond = exp.getGeneticConditionWithGene(geneNames[j]); 
        cumulative &=  !(cond.isEmpty()); 
      }
      if ( cumulative == true ) 
        returnedProject.addExperiment(exp); 
    }

    return returnedProject;

  }

  //-----------------------------------------------------------------------------
  /**
   * Filter <code>Project</code>, requiring given phenotype name in 
   * each <code>Experiment</code>
   *
   * @param  phenoName The name of phenotype. 
   * @author thorsson@systemsbiology.org
   */

  public Project filterByPhenotypeName ( String phenoName )
  {
    
    Experiment [] allExperiments = (Experiment []) experiments.toArray (new Experiment [0]); 
    String [] notesArray = (String []) notes.toArray( new String [0]);

    Project returnedProject = new Project( name, organism );
    for (int i=0 ;i<notes.size() ; i++)
      returnedProject.addNote( notesArray[i] );

    for ( int i=0 ; i<experiments.size() ; i++ ){
      Experiment exp = allExperiments[i];
      Phenotype obs = exp.getPhenotypeWithName(phenoName);
      if ( obs.isEmpty() == false )
        returnedProject.addExperiment(exp); 
    }

    return returnedProject;

  }
  //-----------------------------------------------------------------------------
  /**
   * Obtain wild-type value for give phenotype name
   *
   * @param  phenoName The name of Phenotype. 
   *
   * @author thorsson@systemsbiology.org
   */
  public String getWildTypeValue ( String phenoName )
  {
    Project wtExperiments = filterByNumberOfMutants(0);
    System.out.println("---------------Wild-type Experiments-------------\n" + wtExperiments );
    // For now, assume one wild-type Experiment
    if ( wtExperiments.numberOfExperiments() > 1 ) 
      System.out.println("Warning, more than one wild-type experiment "+
                         "- check that wild-type phenotypevalues agree");
    Experiment wtExp =  wtExperiments.getExperiments()[0];
    Phenotype wtPheno = wtExp.getPhenotypeWithName(phenoName);
    String wildtypeValue = wtPheno.getValue();

    return wildtypeValue;
  }
  //-----------------------------------------------------------------------------
  /**
   * Find unique environments in a <code>Project</code>. An environment is a 
   * the set of unique environmental conditions for an <code>Experiment</code>.
   * Each environment is returned in the form of a <code>HashMap</code>.
   *
   * @author thorsson@systemsbiology.org
   */
  // The unique environments will be represented by an Experiment array 
  // Each element of the array represents a set of environmental Conditions 
  public HashMap [] getEnvironments () {

    Experiment [] allExperiments  = (Experiment []) experiments.toArray (new Experiment [0]); 
    Vector uniqueEnvironments = new Vector (); 
    
    for (int i = 0; i < experiments.size(); i++){ // Loop over all Experiments in Project

      // System.out.println("We are examining new experiment number " + i ) ;
      
      // Obtain Experiment and environmental conditions
      Experiment exp = allExperiments[i];
      HashMap envNew = exp.getEnvironmentalConditions ();
	
      //Was this environment found already ? 
      // array of all unique environments so far
      HashMap [] envsSoFar  = 
        (HashMap []) uniqueEnvironments.toArray (new HashMap [0]);
      //System.out.println("Beginning to search through the "+ uniqueEnvironments.size() +" environments found so far");
      boolean foundFlag = false ; 
      for ( int j=0 ; (j<uniqueEnvironments.size())&(foundFlag==false) ; j++ ){ 
        // Loop through set found so far, but stop if foundFlag==true
        HashMap envSoFar = envsSoFar[j] ; 
        //System.out.println("Comparing " + envNew + " to " + envSoFar + "\n");
        if ( envNew.equals(envSoFar) ) foundFlag = true ; 
      } // end loop over SoFar environments
    
      // If these environmental conditions have not been found earlier, 
      // include them in uniqueEnvironments
      if ( foundFlag == false  ){
        uniqueEnvironments.add(envNew);
        //System.out.println("We did not find the new experiment in the existing set, and have therefore added it.");
      } else if ( foundFlag == true ){
        //System.out.println("We did find the new experiment in the existing set, and have therefore not changed anything.");
      }
    }
    
    HashMap [] uniqueEnvironmentsArray = 
      (HashMap []) uniqueEnvironments.toArray (new HashMap[0]) ; 
    return uniqueEnvironmentsArray; // return Array of hashes?? hash of hashes ?? 
  }
  //-----------------------------------------------------------------------------
  /**
   * Filter a <code>Project</code> by environment. 
   * Only identical matches to the environment are returned.
   *
   * @author thorsson@systemsbiology.org
   */
  public Project filterByEnvironment ( HashMap envDesired ) {

    String [] notesArray = (String []) notes.toArray( new String [0]);
    Project returnedProject = new Project( name, organism );
    for (int i=0 ;i<notes.size() ; i++)
      returnedProject.addNote( notesArray[i] );

    Experiment [] allExperiments = (Experiment []) experiments.toArray (new Experiment [0]); 
    for ( int i=0 ; i<experiments.size() ; i++ ){
      //System.out.println("We are examining experiment number " + i ) ;
      // Obtain Experiment and environmental conditions
      Experiment exp = allExperiments[i];
      HashMap env = exp.getEnvironmentalConditions ();
      if ( env.equals(envDesired) ) returnedProject.addExperiment(exp); 
    }
    
    return returnedProject;
  }
  //-----------------------------------------------------------------------------
  /**
   * Find unique genes in a <code>Project</code>. 
   *
   * @author thorsson@systemsbiology.org
   */
  public HashSet getGenes () {

    Experiment [] allExperiments = (Experiment [])experiments.toArray (new Experiment [0]); 
    HashSet allGenes = new HashSet(); 
    
    for ( int i = 0; i < experiments.size(); i++ ){

      Experiment e = allExperiments[i] ; 
      Condition [] conds = e.getGeneticConditions() ; 

      for ( int j = 0; j<conds.length; j++ ){
        Condition c = conds[j]; 
        String gene = c.getGene() ; 
        allGenes.add(gene); // Adds gene to Set, but only if not present 
      }
      
    } // end loop over experiments
    
    return allGenes ;
  }
  //-----------------------------------------------------------------------------
  /**
   * Find phenotype names a <code>Project</code>. 
   *
   * @author thorsson@systemsbiology.org
   */
  public String [] getPhenotypeNames () {
    
    Experiment [] allExperiments = (Experiment []) experiments.toArray (new Experiment [0]); 
    HashSet pNames = new HashSet();

    for ( int i = 0; i < experiments.size(); i++ ){

      Experiment e = allExperiments[i] ; 
      Phenotype [] observations = e.getObservations(); 
      
      for(int j=0 ; j< observations.length ; j++ ){
        // Loop over all observations in single mutant experiment
        Phenotype obs = observations[j];
        String phenoName = obs.getName();
        pNames.add(phenoName); // Add phenotype name  to Set, if not found earlier
      }

    } // end loop over experiments

    return (String []) pNames.toArray (new String [0]);
  }
  //-----------------------------------------------------------------------------
  /**
   * Environments represented as a string array. One string for each environment.
   * 
   * @param environments the environments
   * @author thorsson@systemsbiology.org
   */
  public String [] getEnviroArray (HashMap [] environments) 
  {
    String [] enviroArray = new String[environments.length] ; 
    if ( environments[0].isEmpty() ){
      enviroArray[0] = "Generic";
    } else {
      for ( int i=0 ; i<environments.length ; i++ ){
        enviroArray[i] = environments[i].toString();
      }
    }
    return enviroArray;
  }

  //-----------------------------------------------------------------------------
  /**
   * PhenotypeName and Environments represented as a string array. 
   * One string for each combination of PhenotypeName and Environment
   * 
   * @see PhenoEnvironment
   * @param environments the environments
   * @author thorsson@systemsbiology.org
   */
  public String [] getPhenoEnviroArray (PhenoEnvironment [] phenoEnvs) 
  {

    String [] enviroPhenoArray = new String[phenoEnvs.length] ; 
    if ( phenoEnvs[0].isEmpty() ){
      enviroPhenoArray[0] = "Generic";
    } else {
      for ( int i=0 ; i<phenoEnvs.length ; i++ ){
        enviroPhenoArray[i] = phenoEnvs[i].toString();
      }
    }
    return enviroPhenoArray;
  }
  //-----------------------------------------------------------------------------
  /**
   * Separate the <code>Project</code> into distinct environments
   * 
   * @author thorsson@systemsbiology.org
   */
  public Project []  separateEnvironments (HashMap [] environments)
  {
    Vector pBEs = new Vector() ;  
    for ( int i=0 ; i<environments.length ; i++ ){
      Project pBE = filterByEnvironment( environments[i] );
      System.out.println("There are "+pBE.getExperiments().length+
                         " Experiments in environment "+environments[i] );
      pBEs.add( pBE );
    }
    return( (Project []) pBEs.toArray (new Project[0] ) );

  }
  //---------------------------------------------------------------------------------
  /**
   * Find the combinations of phenotype names and environments in the project
   *
   * @see PhenoEnvironment
   */
  public PhenoEnvironment [] getPhenoEnvironments ()  
  {
    HashMap [] envs = getEnvironments() ; // get unique environments
    Project [] pE = separateEnvironments( envs ); // separate Project into environments
    Vector pes = new Vector ();  // for collecting PhenoEnvironments

    for ( int i=0 ; i<envs.length ; i++ ){ // loop through environments

      Project p = pE[i] ; // the subset of the project for this environment
      String [] phenoNames = p.getPhenotypeNames(); // the phenotype names

      for ( int j=0 ; j<phenoNames.length ; j++ ){ 
        // for each phenoname create PhenoEnvironment and include
        if ( !(phenoNames[j]).endsWith(Phenotype.deviationString) ){
          PhenoEnvironment pe = new PhenoEnvironment(); 
          pe.setPhenoName(phenoNames[j]); 
          pe.setEnvironment(envs[i]);
          pes.add(pe);
        }
      } 
    }

    return( (PhenoEnvironment []) pes.toArray( new PhenoEnvironment[0] ) );
  }

  //-----------------------------------------------------------------------------
  /**
   * Subdivide <code>Project</code> into smaller <code>Project</code>s 
   * such that each subdivision contains
   * only <code>Experiment</code>s with a specific environment and observation
   * of a certain phenotype
   *
   * @param phenoEnvs An array for which each array element is a specified 
   * environment and a phenotype name
   * @see PhenoEnvironment
   */
  public Project []  separatePhenoEnvironments (PhenoEnvironment [] phenoEnvs )
  {
    Vector ps = new Vector() ;  
    for ( int i=0 ; i<phenoEnvs.length ; i++ ){
      Project p = restrictToPhenoEnvironment( phenoEnvs[i] );
      System.out.println("There are "+
                         p.getExperiments().length+
                         " Experiments in pheno-environment "+phenoEnvs[i] );
      ps.add( p );
    }
    return( (Project []) ps.toArray (new Project[0] ) );
  }
  //-----------------------------------------------------------------------------
  /**
   * The restriction of <code>Experiments</code> in the <code>Project</code> to only those 
   * with a specific environment and observation of specific phenotype
   *
   * @param phenoEnvs A specified environment and a phenotype name
   * @see PhenoEnvironment
   *
   */
  public Project restrictToPhenoEnvironment ( PhenoEnvironment phenoEnv )  
  {
    String phenoName = phenoEnv.getPhenoName() ; 
    HashMap env = phenoEnv.getEnvironment() ; 
    Project penv = filterByEnvironment(env);
    Project penvPhenoName = penv.restrictToPhenotypeName(phenoName);
    return( penvPhenoName );
  }
  //---------------------------------------------------------------------------------

  /**
   * A <code>Project</code> restricting <code>Experiment</code> to only 
   * include a give phenotype name
   *
   * @param  phenoName The name of phenotype. 
   *
   * @author thorsson@systemsbiology.org
   */
  // Warning: Not compatible to measurments in the absensce of error assessment!
  public Project restrictToPhenotypeName ( String phenoName )
  {
    
    Experiment [] allExperiments = (Experiment []) experiments.toArray (new Experiment [0]); 
    String [] notesArray = (String []) notes.toArray( new String [0]);

    Project returnedProject = new Project( name, organism );
    for (int i=0 ;i<notes.size() ; i++)
      returnedProject.addNote( notesArray[i] );

    for ( int i=0 ; i<experiments.size() ; i++ ){
      Experiment exp = allExperiments[i];
      Phenotype obs = exp.getPhenotypeWithName(phenoName);


      if ( obs.isEmpty() == false ){
        // construct experiment with *only* that observation
        Experiment expRestricted = 
          new Experiment(exp.getName());// use same name, notes, condtions as exp 
        String [] notes = exp.getNotes();
        for ( int j=0 ; j<notes.length ; j++ ) expRestricted.addNote(notes[j]);
        Condition [] conds = exp.getConditions();
        for ( int j=0 ; j<conds.length ; j++ ) expRestricted.addCondition(conds[j]);
        //add only the observation with the desired phenotype name
        expRestricted.addObservation(obs);

        String errorString = phenoName + Phenotype.deviationString; 	  
        Phenotype obsError = exp.getPhenotypeWithName(errorString);
        if ( obsError.isEmpty() == false ){
          // add corresponding error value, if one was found
          expRestricted.addObservation(obsError); 
        }
        returnedProject.addExperiment(expRestricted);
      } 
    }
    
    return returnedProject;
  }
} // Project
