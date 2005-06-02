/**
 * Determine pairwise genetic interactions from observed <code>Phenotypes</code>
 *
 * @author Vesteinn Th.
 * @author Iliana Avila refactored
 * @version 2.0
 */
package phenotypeGenetics;

import phenotypeGenetics.action.*;
import cytoscape.util.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.text.*;
import cytoscape.*;
import cytoscape.data.Semantics;
import cern.colt.list.IntArrayList;

public class GeneticInteractionCalculator{

  /**
   * Estimates how many units it will take to calculate genetic interactions
   * for the given Projects, mainly used for progress monitors
   *
   * @see cytoscape.util.CytoscapeProgressMonitor
   */
  public static int getLengthOfTask (Project [] projects){
    
    int count = 0;
    for(int i = 0; i < projects.length; i++){
      Project pDoubleMutant = projects[i].filterByNumberOfMutants(2);
      Experiment [] experimentsDoubleMutant = pDoubleMutant.getExperiments();
      count += experimentsDoubleMutant.length;
    }// for i
    
    return count;
  }//getLengthOfTask
  
  /**
   * Calculates genetic interactions.
   *
   * @param cy_net the CyNetwork to whose RootGraph genetic edges will be added
   * @param projects an array of Projects with phenotype data to be used for the
   * genetic interaction calculation
   * @param pheno_enviros an array of PhenoEnvironments for each Project in the
   * projects array
   * @param task_progress the TaskProgress used to monitor progress
   * @see phenotypeGenetics.action.TaskProgress
   * @return an array of RootGraph indices for edges that were created in the
   * RootGraph for the genetic interactions
   * @throws IllegalArgumentException if there are no wild-type experiment(s) in a 
   * given Project
   */
  public static int [] calculateInteractions 
    (CyNetwork cy_net,
     Project [] projects,
     PhenoEnvironment [] pheno_enviros,
     TaskProgress task_progress) throws IllegalArgumentException{
    
    IntArrayList newEdges = new IntArrayList();
    for(int i = 0; i < pheno_enviros.length; i++){
      int []edges = GeneticInteractionCalculator.calculateInteractions(cy_net, 
                                                                       projects[i],
                                                                       pheno_enviros[i],
                                                                       task_progress);
      for(int j = 0; j < edges.length; j++){
        newEdges.add(edges[j]);
      }//for j
    }//for i
    
    task_progress.done = true;
    newEdges.trimToSize();
    
    return newEdges.elements();
  }//calculateInteractions

  /**
   * Identify genetic interactios and create edges in cy_net's RootGraph
   * representing them
   *
   * @param cy_net the CyNetwork on which we are calculating interacions
   * @param project the Project that contains the phenotype information
   * @param pheno_environment the PhenoEnvironment
   * @param task_progress the TaskProgress for progress monitors that may run this method
   * @return an array of RootGraph indices of edges that where newly created to represent the
   * genetic interactions
   * @throws IllegalArgumentException if there are no wild-type experiments in the Project 
  */
  protected static int [] calculateInteractions 
    (CyNetwork cy_net,
     Project project,
     PhenoEnvironment pheno_environment,
     TaskProgress task_progress) throws IllegalArgumentException{
        
    Project wtExperiments = project.filterByNumberOfMutants(0);
    int numberWTExperiments = wtExperiments.numberOfExperiments();

    if(numberWTExperiments == 0){
      throw new IllegalArgumentException("No wild-type experiment in data.");
                                         
    }
            
    // arbitrary choice of "first" wildtype observation
    Experiment expWT =  wtExperiments.getExperiments()[0];
      
    // filter project by single mutant experiments
    Project pSingleMutant = project.filterByNumberOfMutants(1);
      
    // filter project by double mutant experiments
    Project pDoubleMutant = project.filterByNumberOfMutants(2);
    Experiment [] experimentsDoubleMutant = pDoubleMutant.getExperiments();
      
    // get all of the possible phenotype names
    String [] allPhenoNames = project.getDiscretePhenotypeRanks().getPhenotypeNames();
        
    // get the current phenotype name
    String currentPhenoName = pheno_environment.getPhenoName();
    
    // newly created edges for found genetic interactions
    IntArrayList newEdgeIndices = new IntArrayList();
        
    for(int i = 0; i < experimentsDoubleMutant.length; i++){

      Experiment expAB = experimentsDoubleMutant[i]; 
      
      // Get the names of the two genes that were deleted
      // A and B is an arbitrary ordering for the first and second manipulated genes
      Condition condA = expAB.getGeneticConditions()[0]; 
      Condition condB = expAB.getGeneticConditions()[1]; 
      String geneA = condA.gene;  
      String geneB = condB.gene;  
      
      Project pASingle = pSingleMutant.filterByManipulatedGenes(new String[] {geneA});
      Project pBSingle = pSingleMutant.filterByManipulatedGenes(new String[] {geneB});
    
      // filter by phenotype name the data for each gene mutation
      Project pApheno = pASingle.filterByPhenotypeName(currentPhenoName);
      Project pBpheno = pBSingle.filterByPhenotypeName(currentPhenoName);

      //alleleForms in the double mutant experiment
      int ab_Amanip = expAB.getGeneticConditionWithGene(geneA).getAlleleForm();
      int ab_Bmanip = expAB.getGeneticConditionWithGene(geneB).getAlleleForm();
      
      //alleles in the double mutant experiment
      String ab_Aalname = expAB.getGeneticConditionWithGene(geneA).getAllele();
      String ab_Balname = expAB.getGeneticConditionWithGene(geneB).getAllele();
      
      // find single mutant experiments with the given phenotype name
      Experiment expA = new Experiment(); 
      Experiment expB = new Experiment();
        
      // assume alleleForm and allele are not matched more than once
      for(int k = 0; k < pApheno.numberOfExperiments(); k++){
        Experiment expTry = pApheno.getExperiments()[k]; 
        int a_Amanip = expTry.getGeneticConditionWithGene(geneA).getAlleleForm();
        String a_Aalname = expTry.getGeneticConditionWithGene(geneA).getAllele();
        boolean matchTrue = (ab_Amanip == a_Amanip);
        if(ab_Aalname != null && a_Aalname != null){
          matchTrue = matchTrue && (ab_Aalname.compareTo(a_Aalname) == 0);
        } 
        if(matchTrue) expA = expTry;
      }//for k

      // assume alleleForm and allele are not matched more than once
      for(int k = 0; k < pBpheno.numberOfExperiments(); k++){
        Experiment expTry = pBpheno.getExperiments()[k]; 
        int a_Bmanip = expTry.getGeneticConditionWithGene(geneB).getAlleleForm();
        String a_Balname = expTry.getGeneticConditionWithGene(geneB).getAllele();
        boolean matchTrue = (ab_Bmanip == a_Bmanip);
        if(ab_Balname != null && a_Balname != null){
          matchTrue = matchTrue && (ab_Balname.compareTo(a_Balname) == 0);
        } 
        if(matchTrue) expB = expTry;
      }//for k
      
      if(!expA.isEmpty() && !expB.isEmpty()){ 
        // we found the appropriate single mutants: proceed		  
        
        // identify genetic interaction
        GeneticInteraction interaction;
         
        if(!project.getDiscretePhenotypeRanks().containsPhenotype(currentPhenoName)){
          // continuous phenotype
          interaction = geneticInteractionContinuous(pheno_environment,
                                                     expWT, expA, expB, 
                                                     expAB, currentPhenoName);
        }else{
          
          // discrete phenotype
          interaction = geneticInteractionDiscrete(pheno_environment,expWT, 
                                                   expA, expB, expAB, 
                                                   project.getDiscretePhenotypeRanks());
        }//else
	
        // add edge for the genetic interaction
        CyEdge newEdge = createEdgeForInteraction(cy_net, interaction);
        if(newEdge != null){
          newEdgeIndices.add(newEdge.getRootGraphIndex());
        }
        
      }//if expA and expB found
      
      task_progress.currentProgress++;
      int percent = (int)((task_progress.currentProgress * 100)/task_progress.taskLength);
      task_progress.message = "<html>Genetic Interaction Calculation:<br>Completed " 
        + Integer.toString(percent) + "%</html>";
      
    }//end loop over double mutants
    
    newEdgeIndices.trimToSize();
    
    return  newEdgeIndices.elements();
  }//calculateInteractions
    
  /**
   * Create an edge in cy_net's RootGraph representing the given GeneticInteraction
   * but do not restore it in cy_net
   *
   * @param cy_net the CyNetwork for which to create the interaction
   * @param interaction the interaction to add
   * @return the newly created CyEdge in cy_net's RootGraph
   */
  protected static CyEdge createEdgeForInteraction (CyNetwork cy_net,
                                                    GeneticInteraction interaction){
    
    DiscretePhenoValueInequality ineq = interaction.getDiscretePhenoValueInequality();
    Mode mode = ineq.getMode();
    if(mode.getName().equals(DiscretePhenoValueInequality.UNASSIGNED_MODE_NAME)){
      // no edge for this interaction!
      return null;
    }
    String direction = ineq.getDirection();
    
    CyNode source, target;
    String sourceName, targetName;
    if(direction.equals(DiscretePhenoValueInequality.NOT_DIRECTIONAL) ||
       direction.equals(DiscretePhenoValueInequality.A_TO_B)){
      sourceName = interaction.getMutantA().getName();
      targetName = interaction.getMutantB().getName();
      source = Cytoscape.getCyNode(sourceName, false);
      target = Cytoscape.getCyNode(targetName, false);   
    }else{
      // B_TO_A
      sourceName = interaction.getMutantB().getName();
      targetName = interaction.getMutantA().getName();
      target = Cytoscape.getCyNode(targetName, false);
      source = Cytoscape.getCyNode(sourceName, false);
    }
    
    if(source == null || !cy_net.containsNode(source)){
      System.out.println("Node " + source + " does not exist in cyNetwork!!!");
    }

    if(target == null || !cy_net.containsNode(target)){
      System.out.println("Node " + target + " does not exist in cyNetwork!!!");
    }
    
    String edgeName = interaction.getEdgeName();
    // This creates the edge:
    CyEdge newEdge = Cytoscape.getCyEdge( sourceName,
                                          edgeName,
                                          targetName,
                                          interaction.getGeneticClass() );
       
    HashMap edgeAttributes = interaction.getEdgeAttributes();
    Iterator it = edgeAttributes.entrySet().iterator();
    while(it.hasNext()){
      Map.Entry entry = (Map.Entry)it.next();
      if(entry.getKey() instanceof String){
        Cytoscape.setEdgeAttributeValue(newEdge, 
                                        (String)entry.getKey(), 
                                        entry.getValue());
      }
    }//it.hasNext()
    
    Cytoscape.setEdgeAttributeValue(newEdge,
                                    DiscretePhenoValueInequality.EDGE_ATTRIBUTE, 
                                    interaction.getDiscretePhenoValueInequality().toString()
                                    );
  
    return newEdge;
  }//createEdgeForInteraction
  
   /**
    * Type of genetic interaction implied by <code>Experiments</code> with a common 
    * <code>Phenotype</code> name. 
    * Phenotype values are real, and have a an associated error estimate.
    * 
    * @param pheno_environment the PhenoEnvironment
    * @param expWT       Wild-type experiment
    * @param expA        an experiment in which gene A was manipulated 
    * @param expB        an experiment in which gene B was manipulated 
    * @param expAB       an experiment in which genes A and B were manipulated 
    * @param phenoName   the <code>Phenotype</code> name of interest 
    * @return the GeneticInteraction object representing the interaction
    * @exception IllegalArgumentExceptionrgumentException if phenotypes names do 
    * not all agree, or if number of genetic manipulations is wrong in any experiment. 
    */
  protected static GeneticInteraction geneticInteractionContinuous 
    (PhenoEnvironment pheno_environment,
     Experiment expWT, 
     Experiment expA,
     Experiment expB, 
     Experiment expAB,
     String phenoName) throws IllegalArgumentException {

    illegalGeneticInteractionInputs(expWT, expA, expB, expAB, phenoName);

    Phenotype phenoWT = expWT.getPhenotypeWithName(phenoName);
    Phenotype phenoA = expA.getPhenotypeWithName(phenoName);
    Phenotype phenoB = expB.getPhenotypeWithName(phenoName);
    Phenotype phenoAB = expAB.getPhenotypeWithName(phenoName);
    String returnString = ""; 

    //Get alleleForm of genetic modifications
    Condition condA = expA.getGeneticConditions()[0];
    Condition condB = expB.getGeneticConditions()[0];
    String geneA = condA.getGene();
    String geneB = condB.getGene();
    String alleleA = condA.getAllele();
    String alleleB = condB.getAllele();
    int alleleFormA = condA.getAlleleForm(); 
    int alleleFormB = condB.getAlleleForm(); 
    
    //Get phenotype values
    String phenoValueWT = phenoWT.getValue();
    String phenoValueA = phenoA.getValue(); 
    String phenoValueB = phenoB.getValue(); 	
    String phenoValueAB = phenoAB.getValue();
  
    String phenoNameDeviation = phenoName + Phenotype.deviationString;

    Phenotype phenoWTdev = expWT.getPhenotypeWithName(phenoNameDeviation);
    Phenotype phenoAdev = expA.getPhenotypeWithName(phenoNameDeviation);
    Phenotype phenoBdev = expB.getPhenotypeWithName(phenoNameDeviation);
    Phenotype phenoABdev = expAB.getPhenotypeWithName(phenoNameDeviation);

    //Get phenotype values
    String devValueWT = phenoWTdev.getValue();
    String devValueA  = phenoAdev.getValue(); 
    String devValueB  = phenoBdev.getValue(); 	
    String devValueAB = phenoABdev.getValue();

    double pWT = Double.parseDouble(phenoValueWT);
    double pA  = Double.parseDouble(phenoValueA);
    double pB  = Double.parseDouble(phenoValueB);
    double pAB = Double.parseDouble(phenoValueAB);

    double pWTe = Double.parseDouble(devValueWT);
    double pAe  = Double.parseDouble(devValueA);
    double pBe  = Double.parseDouble(devValueB);
    double pABe = Double.parseDouble(devValueAB);

    double [] x = {pWT, pA, pB, pAB};
    double [] e = {pWTe, pAe, pBe, pABe};
    int [] bins = Utilities.discretize(x,e);

    DiscretePhenoValueInequality d = 
      DiscretePhenoValueInequality.getPhenoInequality(bins[0], 
                                                      bins[1], 
                                                      bins[2], 
                                                      bins[3]);
    // Create Genetic Interaction
    GeneticInteraction interaction = new GeneticInteraction();
    interaction.setDiscretePhenoValueInequality(d);
    interaction.setPhenoEnvironment(pheno_environment);
    
    // Create Single mutantA and attach to genetic interaction
    SingleMutant m = new SingleMutant();
    m.setName(geneA);
    CyNode nodeA = Cytoscape.getCyNode(geneA, false); // should exist already!
    String geneAcommonName = 
      (String)Cytoscape.getNodeAttributeValue(nodeA, Semantics.COMMON_NAME);
    m.setCommonName(geneAcommonName);
    m.setAllele(alleleA);
    m.setAlleleForm(alleleFormA);
    //m.setPhenoEnvironment(pheno_environment);
    interaction.setMutantA(m);

    // Create Single mutantB and attach to genetic interaction
    m = new SingleMutant();
    m.setName(geneB);
    CyNode nodeB = Cytoscape.getCyNode(geneB, false);
    String geneBcommonName = 
      (String)Cytoscape.getNodeAttributeValue(nodeB, Semantics.COMMON_NAME);
    m.setCommonName(geneBcommonName);
    m.setAllele(alleleB);
    m.setAlleleForm(alleleFormB);
    //m.setPhenoEnvironment(pheno_environment);
    interaction.setMutantB(m);

    interaction.setGeneticClass();

    // Set observed phenotype value, including error
    String pattern = ".####";//Allow four significant digits 
    DecimalFormat myFormatter = 
      new DecimalFormat(pattern);//Plus minus. 177 in Decimal. B1 in hex. 
    //Unicode: Plus minus sign. 177 in Decimal. B1 in  hexadecimal. 
    String plusMinus = "\u00B1"; 
    
    String pveWT = myFormatter.format(pWT)+ " " + plusMinus + " " + myFormatter.format(pWTe);
    String pveA = myFormatter.format(pA)+ " " + plusMinus + " " + myFormatter.format(pAe);
    String pveB = myFormatter.format(pB)+ " " + plusMinus + " " + myFormatter.format(pBe);
    String pveAB = myFormatter.format(pAB)+ " " + plusMinus + " " + myFormatter.format(pABe);
    interaction.setPhenotypeValueWT(pveWT);
    interaction.setPhenotypeValueA(pveA);
    interaction.setPhenotypeValueB(pveB);
    interaction.setPhenotypeValueAB(pveAB);

    return interaction; 
  }//geneticInteractionContinuous

  /**
   * Type of genetic interaction implied by 
   * <code>Experiments</code> with a common <code>Phenotype</code> name.
   * Phenotype values are strings. 
   *
   * @param pheno_environment the PhenoEnvironment
   * @param expWT wild-type experiment
   * @param expA an experiment in which gene A was manipulated 
   * @param expB an experiment in which gene B was manipulated 
   * @param expAB an experiment in which genes A and B were manipulated 
   * @param pRank the <code>PhenotypeRanking</code> giving the relation of phenotype values 
   * @throws IllegalArgumentException If phenotypes names do not all agree, or if number 
   * of genetic manipulations is wrong in any experiment. 
   * @see PhenotypeRanking
   */
  protected static GeneticInteraction geneticInteractionDiscrete 
    (PhenoEnvironment pheno_environment,
     Experiment expWT, 
     Experiment expA, 
     Experiment expB, 
     Experiment expAB, 
     DiscretePhenotypeRanking pRank) throws IllegalArgumentException{
    //PhenotypeRanking pRank ) throws IllegalArgumentException{
    
    String phenoName = pheno_environment.getPhenoName();
    illegalGeneticInteractionInputs(expWT, expA, expB, expAB, phenoName);

    Phenotype phenoWT = expWT.getPhenotypeWithName(phenoName);
    Phenotype phenoA = expA.getPhenotypeWithName(phenoName);
    Phenotype phenoB = expB.getPhenotypeWithName(phenoName);
    Phenotype phenoAB = expAB.getPhenotypeWithName(phenoName);
    String returnString = ""; 

    //Get alleleForm of genetic modifications
    Condition condA = expA.getGeneticConditions()[0];
    Condition condB = expB.getGeneticConditions()[0];
    String geneA = condA.getGene(); 
    String geneB = condB.getGene(); 
    String alleleA = condA.getAllele();
    String alleleB = condB.getAllele();
    int alleleFormA = condA.getAlleleForm(); 
    int alleleFormB = condB.getAlleleForm(); 
    
    //Get phenotype values
    String phenoValueWT = phenoWT.getValue();
    String phenoValueA = phenoA.getValue(); 
    String phenoValueB = phenoB.getValue(); 	
    String phenoValueAB = phenoAB.getValue();
  
    // Get phenotype values and relations specific to phenoName
    DiscretePhenoValueInequality d = 
      pRank.getDiscretePhenoValueIneq(phenoName, phenoValueWT, 
                                      phenoValueA, phenoValueB, 
                                      phenoValueAB);
    
    // Create Genetic Interaction
    GeneticInteraction interaction = new GeneticInteraction();
    interaction.setDiscretePhenoValueInequality(d);
    interaction.setPhenoEnvironment(pheno_environment);
    
    // Create Single mutantA and attach to genetic interaction
    SingleMutant m = new SingleMutant();
    m.setName(geneA);
    CyNode nodeA = Cytoscape.getCyNode(geneA, false); // should exist already
    String geneAcommonName = 
      (String)Cytoscape.getNodeAttributeValue(nodeA, Semantics.COMMON_NAME);
    m.setCommonName(geneAcommonName);
    m.setAllele(alleleA);
    m.setAlleleForm(alleleFormA);
    //m.setPhenoEnvironment(pheno_environment);
    interaction.setMutantA(m);

    // Create Single mutantB and attach to genetic interaction
    m = new SingleMutant();
    m.setName(geneB);
    CyNode nodeB = Cytoscape.getCyNode(geneB, false); // should exist already
    String geneBcommonName = 
      (String)Cytoscape.getNodeAttributeValue(nodeB, Semantics.COMMON_NAME);
    m.setCommonName(geneBcommonName);
    m.setAllele(alleleB);
    m.setAlleleForm(alleleFormB);
    //m.setPhenoEnvironment(pheno_environment);
    interaction.setMutantB(m);

    interaction.setGeneticClass();

    interaction.setPhenotypeValueWT(phenoValueWT);
    interaction.setPhenotypeValueA(phenoValueA);
    interaction.setPhenotypeValueB(phenoValueB);
    interaction.setPhenotypeValueAB(phenoValueAB);
    
    return interaction; 

  }//geneticInteractionDiscrete
  
  /**
   * A utility to identify potential problems with double mutant data
   *
   * @param expWT the wild-type experiment
   * @param expA the A experiment
   * @param expB the B experiment
   * @param expAB the double mutant experiment
   * @param phenoName the phenotype name of the phenotype under consideration 
   * @exception IllegalArgumentException If phenoName is not found in all experiments. 
   * If number of genetic mutations is incorrect. 
   * If A or B alleleForms or genes are incosistent with AB experiment. 
   */
  protected static void illegalGeneticInteractionInputs (
            Experiment expWT, 
            Experiment expA, 
            Experiment expB, 
            Experiment expAB,
            String phenoName) throws IllegalArgumentException{
    
    if( expWT.getPhenotypeWithName(phenoName).isEmpty() )
      throw new IllegalArgumentException ("Phenotype name "+phenoName+ 
                                          " not found in experiment "+expWT.getName());
    if( expA.getPhenotypeWithName(phenoName).isEmpty() )
      throw new IllegalArgumentException ("Phenotype name "+phenoName+ 
                                          " not found in experiment "+expA.getName());
    if( expB.getPhenotypeWithName(phenoName).isEmpty() )
      throw new IllegalArgumentException ("Phenotype name "+phenoName+ 
                                          " not found in experiment "+expB.getName());
    if( expAB.getPhenotypeWithName(phenoName).isEmpty() )
      throw new IllegalArgumentException ("Phenotype name "+phenoName+ 
                                          " not found in experiment "+expAB.getName());

    // Check number of genetic manipulations 
    if ( expWT.getGeneticConditions().length != 0 )
      throw new IllegalArgumentException ("expWT is not a wild-type experiment");
    if ( expA.getGeneticConditions().length != 1 )
      throw new IllegalArgumentException ("expA is not a single genetic modification");
    if ( expB.getGeneticConditions().length != 1 )
      throw new IllegalArgumentException ("expB is not a single genetic modification");
    if ( expAB.getGeneticConditions().length != 2 )
      throw new IllegalArgumentException ("expAB is not a double genetic modification");

    //Get alleleForm of genetic modifications 
    Condition condA = expA.getGeneticConditions()[0];
    Condition condB = expB.getGeneticConditions()[0];
    String geneA = condA.getGene(); 
    String geneB = condB.getGene(); 
    int alleleFormA = condA.getAlleleForm(); 
    int alleleFormB = condB.getAlleleForm(); 
    int alleleFormAB_A = expAB.getGeneticConditionWithGene(geneA).getAlleleForm();
    if ( alleleFormA != alleleFormAB_A )
      throw new IllegalArgumentException 
        ("expAB("+expAB.getName()+"): geneA alleleForm("+alleleFormAB_A +
         ") differs from that in expA("+alleleFormA+")"); 
    if ( alleleFormB != expAB.getGeneticConditionWithGene(geneB).getAlleleForm() ) 
      throw new IllegalArgumentException ("expAB: geneB alleleForm differs from that in expB");

    //Get allele of genetic modifications 
    String alleleA = condA.getAllele(); 
    String alleleB = condB.getAllele(); 
    String alleleAB_A = expAB.getGeneticConditionWithGene(geneA).getAllele();
    String alleleAB_B = expAB.getGeneticConditionWithGene(geneB).getAllele();
    if ( alleleA != null && alleleB != null && alleleAB_A != null && alleleAB_B != null) {
      if ( alleleA.compareTo(alleleAB_A) != 0 )
        throw new IllegalArgumentException 
          ("expAB("+expAB.getName()+"): geneA allele("+alleleAB_A +
           ") differs from that in expA("+alleleA+")"); 
      if ( alleleB.compareTo(alleleAB_B) != 0 ) 
        throw new IllegalArgumentException ("expAB: geneB allele differs from that in expB");
    }
    
    //Better have same gene names in AB!
    if ( expAB.getGeneticConditionWithGene(geneA).isEmpty() )
      throw new IllegalArgumentException ("expAB does not contain a modification of "+geneA);
    if ( expAB.getGeneticConditionWithGene(geneB).isEmpty() )
      throw new IllegalArgumentException ("expAB does not contain a modification of "+geneB);

  }//illegalGeneticInteractionInputs
   
}// class GeneticInteractionCalculator

