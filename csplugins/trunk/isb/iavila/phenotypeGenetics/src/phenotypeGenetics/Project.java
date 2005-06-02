/**
 * A set of experiments on an organism.
 *
 * @author Paul Shannon
 * @author Vesteinn Thorsson
 * @author Iliana Avila (refactored)
 */
package phenotypeGenetics;
import java.io.*;
import java.util.*;

public class Project{
  
  protected String name;
  protected String organism;
  protected List notes;
  protected List experiments;
  /**
   * Contains a list of discrete phenotypes in this Project, they
   * can be unranked or ranked
   */
  protected DiscretePhenotypeRanking discretePhenotypeRanks;
 
  /**
   * Constructor, calls this(null, null)
   */
  public Project (){
    this(null,null);
  }//Project
  
  /**
   * Constructs a new Project specified by a name and organism.
   */
  public Project (String name, String organism){
    this.name = name;
    this.organism = organism;
    this.notes = new ArrayList();
    this.experiments = new ArrayList();
  }//Project

  /**
   * Sets the name of the Project.
   */
  public void setName (String newValue){
    this.name = newValue;
  }//setName

  /**
   * Gets the name of the Project.
   */
  public String getName (){
    return this.name;
  }//getName
  
  /**
   * Sets the name of the organism for the Project.
   */
  public void setOrganism (String newValue){
    this.organism = newValue;
  }//setOrganism
  
  /**
   * Gets the name of the organism for the Project.
   */
  public String getOrganism (){
    return this.organism;
  }//getOrganism

  /**
   * @param
   */
  public void setDiscretePhenotypeRanks (DiscretePhenotypeRanking ranking){
    this.discretePhenotypeRanks = ranking;
  }//setDiscretePhenotypeRanks

  /**
   * @return 
   */
  public DiscretePhenotypeRanking getDiscretePhenotypeRanks(){
    return this.discretePhenotypeRanks;
  }//getDiscretePhenotypeRanks
  
  /**
   * Add a text note to the Project.
   */
  public void addNote (String note){
    this.notes.add(note);
  }//addNote
  
  /**
   * Add an Experiment to the Project.
   */
  public void addExperiment (Experiment experiment){
    this.experiments.add(experiment);
  }//addExperiment
  
  /**
   * The number of notes in the Project.
   */
  public int numberOfNotes (){
    return this.notes.size();
  }//numberOfNotes
  
  /**
   * Get notes on the Project.
   */
  public String [] getNotes (){
    return (String [])this.notes.toArray(new String [0]);
  }//getNotes
  
  /**
   * The number of Experiment in the Project.
   */
  public int numberOfExperiments (){
    return this.experiments.size();
  }//numberOfExperiments
  
  /**
   * Returns all Experiments in the Project.
   */
  public Experiment [] getExperiments (){
    return (Experiment [])this.experiments.toArray(new Experiment [0]);
  }//getExperiments

  /**
   * Converts Project to String
   */
  public String toString (){
    
    StringBuffer sb = new StringBuffer ();
    sb.append("name: " + name + "\n");
    sb.append("organism: " + organism + "\n");

    String [] notesArray = getNotes ();
    for(int i=0; i < notesArray.length; i++)
      sb.append("note " + i + ") " + notesArray [i] + "\n");

    Experiment [] experimentsArray = getExperiments ();
    for(int i=0; i < experimentsArray.length; i++)
      sb.append ("experiment " + i + ") " + experimentsArray [i] + "\n");

    return sb.toString();

  }//toString

  /**
   * Add the contents of a project to this project
   * the organism and name are concatenated if they are not identical
   * The notes are added on to the current notes,
   * and all of the experiments are added
   */
  public void concatenate (Project other_project){
    
    String organism = other_project.getOrganism();
    if(this.organism  == null){
      this.organism = new String(organism);
    }else if(!this.organism.equals(organism)){
      this.organism = new String(this.organism+" "+organism);
    }
    
    String name = other_project.getName();
    if(this.name  == null){
      this.name = new String(name);
    }else if(!this.name.equals(name)){
      this.name = new String(this.name+" "+name);
    }
    
    String[] notes = other_project.getNotes();
    for(int i = 0; i < notes.length; i++){
      this.addNote(notes[i]);
    }
    
    Experiment[] exps = other_project.getExperiments();
    for(int i = 0; i < exps.length; i++){
      this.addExperiment(exps[i]);
    }
    
    if(this.discretePhenotypeRanks == null){
      this.discretePhenotypeRanks = other_project.getDiscretePhenotypeRanks();
    }else{
      this.discretePhenotypeRanks.merge(other_project.getDiscretePhenotypeRanks());
    }
    
  }//concatenate
  
  /**
   * Filter <code>Project</code>, requiring a fixed number of manipulated genes 
   * in each <code>Experiment</code>
   *
   * @param  numManip the number of manipulated genes. 
   */
  public Project filterByNumberOfMutants (int numManip){
    Experiment [] allExperiments = (Experiment []) experiments.toArray (new Experiment [0]); 
    String [] notesArray = (String [])notes.toArray( new String [0]);

    Project returnedProject = new Project( name, organism );
    
    returnedProject.setDiscretePhenotypeRanks(getDiscretePhenotypeRanks());
    
    for(int i = 0; i < notes.size(); i++)
      returnedProject.addNote( notesArray[i] );
    
    for(int i = 0; i < experiments.size(); i++){
      Experiment exp = allExperiments[i];
      Condition [] conditionsGenetic = exp.getGeneticConditions ();
      if(conditionsGenetic.length == numManip)
        returnedProject.addExperiment(exp); 
    }//for i

    return returnedProject;
  }//filterByNumberOfMutants

  /**
   * Filter <code>Project</code>, requiring certain genes to be manipulated 
   * in each <code>Experiment</code>
   *
   * @param  geneNames the names of manipulated genes. 
   */
  public Project filterByManipulatedGenes (String [] geneNames){
    Experiment [] allExperiments = (Experiment [])experiments.toArray (new Experiment [0]); 
    String [] notesArray = (String [])notes.toArray( new String [0]);

    Project returnedProject = new Project( name, organism );
    
    returnedProject.setDiscretePhenotypeRanks(getDiscretePhenotypeRanks());
       
    for(int i = 0; i < notes.size(); i++)
      returnedProject.addNote(notesArray[i]);
    
    for(int i = 0; i < experiments.size(); i++){
      boolean cumulative = true; 
      Experiment exp = allExperiments[i];
      Condition [] conditionsGenetic = exp.getGeneticConditions ();
      for(int j = 0; j < geneNames.length; j++){
        Condition cond = exp.getGeneticConditionWithGene(geneNames[j]); 
        cumulative &= !(cond.isEmpty()); 
      }//for j
      if(cumulative == true) 
        returnedProject.addExperiment(exp); 
    }//for i
    
    return returnedProject;
    
  }//filterByManipulatedGenes
  
  /**
   * Filter <code>Project</code>, requiring given phenotype name in 
   * each <code>Experiment</code>
   *
   * @param  phenoName the name of phenotype. 
   */
  public Project filterByPhenotypeName (String phenoName){
    
    Experiment [] allExperiments = (Experiment [])experiments.toArray (new Experiment [0]); 
    String [] notesArray = (String [])notes.toArray( new String [0]);

    Project returnedProject = new Project( name, organism );
    returnedProject.setDiscretePhenotypeRanks(getDiscretePhenotypeRanks());
   
    for(int i = 0; i < notes.size(); i++)
      returnedProject.addNote(notesArray[i]);

    for (int i = 0; i < experiments.size(); i++){
      Experiment exp = allExperiments[i];
      Phenotype obs = exp.getPhenotypeWithName(phenoName);
      if(obs.isEmpty() == false)
        returnedProject.addExperiment(exp); 
    }//for i
    
    return returnedProject;
    
  }//filterByPhenotypeName
  
  /**
   * Obtain wild-type value for give phenotype name
   *
   * @param  phenoName the name of Phenotype. 
   */
  public String getWildTypeValue (String phenoName){
    Project wtExperiments = filterByNumberOfMutants(0);
    
    // For now, assume one wild-type Experiment
    if(wtExperiments.numberOfExperiments() > 1) 
      System.out.println("Warning, more than one wild-type experiment "+
                         "- check that wild-type phenotypevalues agree");
    Experiment wtExp =  wtExperiments.getExperiments()[0];
    Phenotype wtPheno = wtExp.getPhenotypeWithName(phenoName);
    String wildtypeValue = wtPheno.getValue();

    return wildtypeValue;
  }//getWildTypeValue
  
  /**
   * Find unique environments in a <code>Project</code>. An environment is
   * the set of unique environmental conditions for an <code>Experiment</code>.
   * Each environment is returned in the form of a <code>HashMap</code>.
   */
  // The unique environments will be represented by an Experiment array 
  // Each element of the array represents a set of environmental Conditions 
  public HashMap [] getEnvironments (){

    Experiment [] allExperiments  = (Experiment [])experiments.toArray(new Experiment[0]); 
    Vector uniqueEnvironments = new Vector(); 
    
    for(int i = 0; i < experiments.size(); i++){

      // Obtain Experiment and environmental conditions
      Experiment exp = allExperiments[i];
      Map envNew = exp.getEnvironmentalConditions();
	
      // Was this environment found already ? 
      // array of all unique environments so far
      HashMap [] envsSoFar  = 
        (HashMap [])uniqueEnvironments.toArray (new HashMap [0]);
      
      boolean foundFlag = false ; 
      for(int j = 0; j < uniqueEnvironments.size()&& !foundFlag; j++){ 
        HashMap envSoFar = envsSoFar[j]; 
        //System.out.println("Comparing " + envNew + " to " + envSoFar + "\n");
        if( envNew.equals(envSoFar) ) foundFlag = true ; 
      }// for j
      
      // If these environmental conditions have not been found earlier, 
      // include them in uniqueEnvironments
      if(!foundFlag){
        uniqueEnvironments.add(envNew);
      }
    }
    
    HashMap [] uniqueEnvironmentsArray = 
      (HashMap []) uniqueEnvironments.toArray (new HashMap[0]) ; 
    
    return uniqueEnvironmentsArray; // return Array of hashes?? hash of hashes ?? 
  }//getEnvironments
  
  /**
   * Filter a <code>Project</code> by environment. 
   * Only identical matches to the environment are returned.
   */
  public Project filterByEnvironment (Map envDesired){

    String [] notesArray = (String [])notes.toArray(new String [0]);
    Project returnedProject = new Project(name, organism);
    
    returnedProject.setDiscretePhenotypeRanks(getDiscretePhenotypeRanks());
    
    for(int i = 0; i < notes.size(); i++)
      returnedProject.addNote(notesArray[i]);
    
    Experiment [] allExperiments = (Experiment [])experiments.toArray(new Experiment [0]); 
    for(int i = 0; i < experiments.size(); i++){
      //System.out.println("We are examining experiment number " + i ) ;
      // Obtain Experiment and environmental conditions
      Experiment exp = allExperiments[i];
      Map env = exp.getEnvironmentalConditions();
      if ( env.equals(envDesired) )returnedProject.addExperiment(exp); 
    }//for i
    
    return returnedProject;
  }//filterByEnvironment
  
  /**
   * Find unique genes in a <code>Project</code>. 
   */
  public HashSet getGenes (){

    Experiment [] allExperiments = (Experiment [])experiments.toArray (new Experiment [0]); 
    HashSet allGenes = new HashSet(); 
    
    for(int i = 0; i < experiments.size(); i++){
      
      Experiment e = allExperiments[i] ; 
      Condition [] conds = e.getGeneticConditions() ; 

      for(int j = 0; j < conds.length; j++){
        Condition c = conds[j]; 
        String gene = c.getGene() ; 
        allGenes.add(gene); // Adds gene to Set, but only if not present 
      }//for j
      
    } //for i
    
    return allGenes ;
  }//getGenes
  
  /**
   * Find phenotype names a <code>Project</code>. 
   */
  public String [] getPhenotypeNames (){
    
    Experiment [] allExperiments = (Experiment []) experiments.toArray (new Experiment [0]); 
    HashSet pNames = new HashSet();

    for(int i = 0; i < experiments.size(); i++){

      Experiment e = allExperiments[i] ; 
      Phenotype [] observations = e.getObservations(); 
      
      for(int j = 0; j < observations.length; j++){
        // Loop over all observations in single mutant experiment
        Phenotype obs = observations[j];
        String phenoName = obs.getName();
        pNames.add(phenoName); // Add phenotype name  to Set, if not found earlier
      }//for j

    }//for j

    return (String [])pNames.toArray (new String [0]);
  }//getPhenotypeNames
  
  /**
   * Environments represented as a string array. One string for each environment.
   * 
   * @param environments the environments
   */
  public String [] getEnviroArray (HashMap [] environments){
    String [] enviroArray = new String[environments.length] ; 
    if ( environments[0].isEmpty() ){
      enviroArray[0] = "Generic";
    } else {
      for(int i = 0; i < environments.length; i++){
        enviroArray[i] = environments[i].toString();
      }//for i
    }
    return enviroArray;
  }//getEnviroArray

  /**
   * PhenotypeName and Environments represented as a string array. 
   * One string for each combination of PhenotypeName and Environment
   * 
   * @see PhenoEnvironment
   * @param environments the environments
   */
  public String [] getPhenoEnviroArray (PhenoEnvironment [] phenoEnvs){
    
    String [] enviroPhenoArray = new String[phenoEnvs.length] ; 
    if(phenoEnvs[0].isEmpty()){
      enviroPhenoArray[0] = "Generic";
    }else{
      for(int i = 0; i < phenoEnvs.length; i++){
        enviroPhenoArray[i] = phenoEnvs[i].toString();
      }//for i
    }
    return enviroPhenoArray;
  }//getPhenoEnviroArray
  
  /**
   * Separate the <code>Project</code> into distinct environments
   */
  public Project []  separateEnvironments (HashMap [] environments){
    ArrayList pBEs = new ArrayList() ;  
    for(int i = 0; i < environments.length; i++){
      Project pBE = filterByEnvironment(environments[i]);
      pBEs.add(pBE);
    }//for i
    return( (Project []) pBEs.toArray (new Project[0] ) );
  }//separateEnvironments
  
  /**
   * Find the combinations of phenotype-names and environments in the project
   */
  public PhenoEnvironment [] getPhenoEnvironments (){
    
    HashMap [] envs = getEnvironments();
    Project [] pE = separateEnvironments(envs);
    ArrayList pes = new ArrayList();

    for(int i = 0; i < envs.length; i++){

      Project p = pE[i];
      String [] phenoNames = p.getPhenotypeNames();

      for (int j = 0; j < phenoNames.length ; j++){ 
        if ( !(phenoNames[j]).endsWith(Phenotype.deviationString) ){
          PhenoEnvironment pe = new PhenoEnvironment(); 
          pe.setPhenoName(phenoNames[j]); 
          pe.setEnvironment(envs[i]);
          pes.add(pe);
        }//if
      }//for j
    }// for i

    return( (PhenoEnvironment []) pes.toArray( new PhenoEnvironment[0] ) );
  
  }//getPhenoEnvironments

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
  public Project []  separatePhenoEnvironments (PhenoEnvironment [] phenoEnvs){
    
    ArrayList ps = new ArrayList();
    
    for (int i = 0; i < phenoEnvs.length; i++){
      Project p = restrictToPhenoEnvironment(phenoEnvs[i]);
      //System.out.println("There are "+
      //                 p.getExperiments().length+
      //                 " Experiments in pheno-environment "+phenoEnvs[i] );
      ps.add(p);
    }//for i
    return( (Project []) ps.toArray (new Project[0] ) );
  }//separatePhenoEnvironments
  
  /**
   * The restriction of <code>Experiments</code> in the <code>Project</code> to only those 
   * with a specific environment and observation of specific phenotype
   *
   * @param phenoEnvs A specified environment and a phenotype name
   */
  public Project restrictToPhenoEnvironment (PhenoEnvironment phenoEnv){
    String phenoName = phenoEnv.getPhenoName() ; 
    Map env = phenoEnv.getEnvironment() ; 
    Project penv = filterByEnvironment(env);
    Project penvPhenoName = penv.restrictToPhenotypeName(phenoName);
    return penvPhenoName;
  }//restrictToPhenoEnvironment

  /**
   * A <code>Project</code> restricting <code>Experiment</code> to only 
   * include a give phenotype name
   *
   * @param  phenoName The name of phenotype. 
   */
  // Warning: Not compatible to measurments in the absensce of error assessment!
  public Project restrictToPhenotypeName (String phenoName){
    
    Experiment [] allExperiments = (Experiment []) experiments.toArray (new Experiment [0]); 
    String [] notesArray = (String []) notes.toArray( new String [0]);

    Project returnedProject = new Project( name, organism );
    
    returnedProject.setDiscretePhenotypeRanks(getDiscretePhenotypeRanks());
   
    for(int i = 0; i < notes.size(); i++)
      returnedProject.addNote( notesArray[i] );

    for(int i = 0; i < experiments.size(); i++){
      Experiment exp = allExperiments[i];
      Phenotype obs = exp.getPhenotypeWithName(phenoName);


      if(obs.isEmpty() == false){
        // construct experiment with *only* that observation
        Experiment expRestricted = 
          new Experiment(exp.getName());// use same name, notes, condtions as exp 
        String [] notes = exp.getNotes();
        for(int j = 0; j < notes.length; j++) expRestricted.addNote(notes[j]);
        Condition [] conds = exp.getConditions();
        for(int j = 0; j < conds.length; j++) expRestricted.addCondition(conds[j]);
        //add only the observation with the desired phenotype name
        expRestricted.addObservation(obs);

        String errorString = phenoName + Phenotype.deviationString; 	  
        Phenotype obsError = exp.getPhenotypeWithName(errorString);
        if(obsError.isEmpty() == false){
          // add corresponding error value, if one was found
          expRestricted.addObservation(obsError); 
        }
        returnedProject.addExperiment(expRestricted);
      } 
    }
    
    return returnedProject;
  }//restrictToPhenotypeName
  
} // Project
