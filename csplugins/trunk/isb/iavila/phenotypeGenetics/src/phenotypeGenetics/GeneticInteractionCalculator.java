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
 * Determine pairwise genetic interactions from observed <code>Phenotypes</code>
 *
 * @author V. Thorsson
 */
// TODO: This class should contain static methods that take as an argument
// a CyNetwork

package phenotypeGenetics;
import java.util.*;
import java.io.*;
import cytoscape.*;
import cytoscape.data.*;


public class GeneticInteractionCalculator {

  public final static String GENETIC_INFLUENCE_EFFECT = "geneticInfluenceEffect";
  PhenoEnvironment phenotypeEnv;
  Project project;
  CyNetwork graph;

  /**
   * @param pe The PhenoEnvironment combination under consideration
   * @param project The Project under consideration
   * @param cy_network the CyNetwork for which genetic interactions 
   * will be calculated
   */
  public GeneticInteractionCalculator (PhenoEnvironment pe, 
                                       Project project,
                                       CyNetwork cy_network) {
    this.phenotypeEnv = pe;
    this.project = project;
    this.graph = cy_network;
  }
  //----------------------------------------------------------------------------------------
  /**
   * Identify genetic interactions, add edges attached to GeneticInteraction objects
   *
   * @return an array of calculated <code>GeneticInteraction</code> objects
   */
  // this method should take a CyNetwork as an argument and calculate interactions
  // on that network
  public GeneticInteraction [] calculate () throws Exception {

    Vector geneNames = new Vector ();
    Iterator nodeIt = this.graph.nodesIterator();
    while(nodeIt.hasNext()) {
      CyNode node = (CyNode)nodeIt.next();
      String nodeName = 
        (String)Cytoscape.getNodeAttributeValue(node, Semantics.CANONICAL_NAME);
      geneNames.add(nodeName);
    }
    
    // Total number of experiments in project
    int numExperiments = project.getExperiments().length; 
    
    Project wtExperiments = project.filterByNumberOfMutants(0);
    System.out.println("---------------Wild-type Experiments-------------\n" + wtExperiments );
    int numberWTExperiments = wtExperiments.numberOfExperiments();

    // For now, assume one wild-type Experiment
    if ( numberWTExperiments > 1 ){
      System.out.println("Warning, more than one wild-type experiment - "+
                         "check that wild-type phenotypevalues agree");
    }
    
    if ( numberWTExperiments == 0 ){
      System.out.println("Error: No wild-type experiment."+
                         " No genetic interactions will be reported ");
    }

    ArrayList calculatedInteractions = new ArrayList();
    
    if ( numberWTExperiments >= 1 ){

      Experiment expWT =  wtExperiments.getExperiments()[0];
      // Arbitrary choice of "first" wildtype observation
	
      //Filter project by single mutant experiments
      Project pSingleMutant = project.filterByNumberOfMutants(1);
      //Filter project by double mutant experiments
      Project pDoubleMutant = project.filterByNumberOfMutants(2);
      Experiment [] experimentsDoubleMutant = pDoubleMutant.getExperiments();
        
      // Loop over all double mutant experiments
      for (int i=0; i < experimentsDoubleMutant.length; i++) {

        Experiment expAB = experimentsDoubleMutant[i] ; 
	    
        // A and B are arbitrary names for the first and second manipulated gene, respectively
        Condition condA = expAB.getGeneticConditions()[0]; 
        Condition condB = expAB.getGeneticConditions()[1]; 
        String geneA = condA.gene ;  
        String geneB = condB.gene ;  
	  
        Project pASingle = pSingleMutant.filterByManipulatedGenes(new String[] {geneA});
        Project pBSingle = pSingleMutant.filterByManipulatedGenes(new String[] {geneB});
    
        // Get observed phenotype of the double mutant
        String phenoName = this.phenotypeEnv.getPhenoName();

        Phenotype phenoAB = expAB.getPhenotypeWithName(phenoName);
        String phenoValueAB = phenoAB.getValue();
	      
        // Determine if the phenotype can be converted to double
        // If double, we *assume* for now that the same is true of pWT pA and pB 
        // If not possible , we *assume* for now that the same is true of pWT pA and pB
        // i.e. these are all discrete (String)
        boolean isDouble = false; 
        try {
          Double.parseDouble (phenoValueAB);
          isDouble = true; 
        }catch (NumberFormatException e) {
          isDouble = false; 
        }
	      
        Project pApheno = pASingle.filterByPhenotypeName(phenoName);
        Project pBpheno = pBSingle.filterByPhenotypeName(phenoName);

        //AlleleForms in the double mutant experiment
        int ab_Amanip = expAB.getGeneticConditionWithGene(geneA).getAlleleForm();
        int ab_Bmanip = expAB.getGeneticConditionWithGene(geneB).getAlleleForm();
        //Alleles in the double mutant experiment
        String ab_Aalname = expAB.getGeneticConditionWithGene(geneA).getAllele();
        String ab_Balname = expAB.getGeneticConditionWithGene(geneB).getAllele();
      
        Experiment expA = new Experiment(); 
        Experiment expB = new Experiment();

        // Assume alleleForm and allele are not matched more than once
        for (int k = 0; k < pApheno.numberOfExperiments(); k++){
          Experiment expTry = pApheno.getExperiments()[k]; 
          int a_Amanip = expTry.getGeneticConditionWithGene(geneA).getAlleleForm();
          String a_Aalname = expTry.getGeneticConditionWithGene(geneA).getAllele();
          boolean matchTrue = (ab_Amanip == a_Amanip);
          if ( ab_Aalname != null && a_Aalname != null ) {
            matchTrue = matchTrue && ab_Aalname.compareTo(a_Aalname) == 0;
          } 
          if (matchTrue) expA = expTry;
        }
        
        // Assume alleleForm and allele are not matched more than once
        for (int k = 0 ; k < pBpheno.numberOfExperiments(); k++){
          Experiment expTry = pBpheno.getExperiments()[k]; 
          int a_Bmanip = expTry.getGeneticConditionWithGene(geneB).getAlleleForm();
          String a_Balname = expTry.getGeneticConditionWithGene(geneB).getAllele();
          boolean matchTrue = (ab_Bmanip == a_Bmanip);
          if ( ab_Balname != null && a_Balname != null ) {
            matchTrue = matchTrue && ab_Balname.compareTo(a_Balname) == 0;
          } 
          if (matchTrue) expB = expTry;
        }
	      
        if ( !expA.isEmpty() && !expB.isEmpty() ) { 
          // We found the appropriate single mutants: proceed		  
          // Identify genetic interaction
          GeneticInteraction interaction;
          if ( isDouble ) {
            interaction = geneticInteractionContinuous(expWT, expA, expB, expAB, phenoName);
          } else {
            //Get ranking of phenotype value strings
            PhenotypeTreeSelector phenotypeTreeSelector = new PhenotypeTreeSelector(project);
            phenotypeTreeSelector.read ();
            PhenotypeTree phenotypeTree = phenotypeTreeSelector.getPhenotypeTree();    
            PhenotypeRanking pRank = phenotypeTree.getPhenotypeWithName (phenoName);
            //Identify genetic interaction
            interaction = geneticInteractionDiscrete(expWT, expA, expB, expAB, pRank);
          }
	
          // iliana
          // Delay creation of the edge to the point when we know what
          // edge directions we want:
          // Add edge for the genetic interaction
          // (see GeneticClassVisualizer)
          //addEdgeForInteraction(interaction);
          // iliana
          
          calculatedInteractions.add(interaction);
        }
        
      } // End loop over double mutants
      
    }// End operations to perform if a wild-type experiment was found
    int size = calculatedInteractions.size();
    return 
      (GeneticInteraction[])calculatedInteractions.toArray(new GeneticInteraction[size]);
  }
  //----------------------------------------------------------------------------------------
  /**
   * Interpret all <code>GeneticInteractions</code>
   */
  //----------------------------------------------------------------------------------------
  public void interpret () throws Exception {
      

    String phenoName = this.phenotypeEnv.getPhenoName();
    // This creates a node for the phenotype if it does not exist 
    CyNode node = Cytoscape.getCyNode(phenoName, true);
    
    if(node == null){
      throw new IllegalStateException("Phenotype node not successfully created!");
    }
    
    // The node we created is in the RootGraph, so we need to restore it in the CyNetwork
    this.graph.restoreNode(node);
		Cytoscape.setNodeAttributeValue(node, Semantics.COMMON_NAME, phenoName);
    
    // Loop over all edges 
    int counter=0; 
    CyEdge [] edges = getEdgesByAttributeNameValue (this.graph, "interaction", "genetic" );
    for (int i=0; i < edges.length; i++) {
      CyEdge edge = edges[i]; 
      //Get genetic interaction
      Object value = Cytoscape.getEdgeAttributeValue(edge, GeneticInteraction.ATTRIBUTE_SELF);
      GeneticInteraction interaction = null;
      if(value != null && value instanceof GeneticInteraction){
        interaction = (GeneticInteraction)value;
      }else{
        throw new IllegalStateException("value for GeneticInteraction.ATTRIBUTE_SELF is not a" 
                                        + " GeneticInteraction or is null");
      }
	  
      if ( !interaction.isEmpty() ){

	      //Evaluate model prediction
	      HashMap ginfo = interaction.interpret(); 

	      // Add predicted edges. Each edge also inherits all information from interaction
        CyNode nodeA = Cytoscape.getCyNode(interaction.getMutantA().getName(), false);
        CyNode nodeB = Cytoscape.getCyNode(interaction.getMutantB().getName(), false);
	      CyNode nodeP = Cytoscape.getCyNode(phenoName, false);
	      addPredictedEdges ( nodeA, nodeB, nodeP, ginfo, interaction.getEdgeAttributes());
	      counter++; 
      } 

    }//end loop over edges
      
    //Print statistics to standard out 
    if ( counter != 0 ){
      System.out.println(counter  + " genetic interactions were interpreted.");
    } else {
      System.out.println("Warning: No genetic interactions interpreted. "+
                      "You may not have Identified the genetic interactions in your project");
    }
      
  }
  //-----------------------------------------------------------------------------
  /**
   * Add an edge in the graph corresponding to a genetic interaction
   */
  // NOT USED WITHIN THIS CLASS (OR ANY THAT I KNOW OF)
  public void addEdgeForInteraction (GeneticInteraction interaction){
    // At this point, the source and target nodes do not exist in CyNetwork!
    CyNode source = Cytoscape.getCyNode(interaction.getMutantA().getName(), false);
    if(source == null){
      throw new IllegalStateException("Node with name "+ interaction.getMutantA().getName() 
                                      + " does not exist in RootGraph!");
    }
    if(!this.graph.containsNode(source)){
      throw new IllegalStateException("Node with name "+ interaction.getMutantA().getName() 
                                      + " does not exist in CyNetwork!");
    }
    CyNode target = Cytoscape.getCyNode(interaction.getMutantB().getName(), false);   
    if(target == null){
      throw new IllegalStateException("Node with name "+ interaction.getMutantB().getName() 
                                      + " does not exist in RootGraph!");
    }
    if(!this.graph.containsNode(target)){
      throw new IllegalStateException("Node with name "+ interaction.getMutantB().getName() 
                                      + " does not exist in CyNetwork!");
    }
    String sourceName = 
      (String)Cytoscape.getNodeAttributeValue(source, Semantics.CANONICAL_NAME);
    String targetName =
      (String)Cytoscape.getNodeAttributeValue(target, Semantics.CANONICAL_NAME);
    String edgeName = interaction.getEdgeName();  
    // This should create the edge if it does not exist:
    CyEdge edge = Cytoscape.getCyEdge(sourceName,
                                      edgeName,
                                      targetName,
                                      interaction.getGeneticClass() // type of interaction
                                      );
    // Copy attributes to the new edge:
    HashMap attributeMap = interaction.getEdgeAttributes();
    Set entries = attributeMap.entrySet();
    Iterator entryIt = entries.iterator();
    while(entryIt.hasNext()){
      Map.Entry entry = (Map.Entry)entryIt.next();
      Cytoscape.setEdgeAttributeValue(edge, (String)entry.getKey(), entry.getValue()); 
    }
    // Restore the edge in the CyNetwork, since we created it in the RootGraph
    this.graph.restoreEdge(edge);
  }
  //-----------------------------------------------------------------------------
  /**
   *
   * Type of genetic interaction implied by <codea>Experiments</code> with a common 
   * <code>Phenotype</code> name. 
   * Phenotype values are real, and have a an associated error estimate.
   * 
   * @param expWT       Wild-type experiment
   * @param expA        An experiment in which gene A was manipulated 
   * @param expB        An experiment in which gene B was manipulated 
   * @param expAB       An experiment in which genes A and B were manipulated 
   * @param phenoName   The <code>Phenotype</code> name of interest 
   *
   * @return the GeneticInteraction object representing the interaction
   *                    
   * @exception IllegalArgumentException If phenotypes names do not all agree, or if 
   * number of genetic manipulations is wrong in any experiment. 
   *
   * @author thorsson@systemsbiology.org
   */
  public GeneticInteraction geneticInteractionContinuous 
    (Experiment expWT, 
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
    
    // Get phenotype values
    String phenoValueWT = phenoWT.getValue();
    String phenoValueA = phenoA.getValue(); 
    String phenoValueB = phenoB.getValue(); 	
    String phenoValueAB = phenoAB.getValue();
  
    String phenoNameDeviation = phenoName + Phenotype.deviationString;

    Phenotype phenoWTdev = expWT.getPhenotypeWithName(phenoNameDeviation);
    Phenotype phenoAdev = expA.getPhenotypeWithName(phenoNameDeviation);
    Phenotype phenoBdev = expB.getPhenotypeWithName(phenoNameDeviation);
    Phenotype phenoABdev = expAB.getPhenotypeWithName(phenoNameDeviation);

    // Get phenotype values
    String devValueWT = phenoWTdev.getValue();
    String devValueA  = phenoAdev.getValue(); 
    String devValueB  = phenoBdev.getValue(); 	
    String devValueAB = phenoABdev.getValue();

    
    /*System.out.println("\n"); 
      System.out.println("Summarizing results of relevant phenotypes"); 
      System.out.println("Experiment " + expAB.name + " is a double mutant" );
      System.out.println("With observed phenotype " + "phenoName: " + phenoName + " phenoValue: "+phenoValueAB);
      System.out.println("With observed deviation " + "phenoNameDeviation: " + phenoNameDeviation + " phenoValuedev: "+devValueAB);
      System.out.println("Experiment " + expA.name +" is the corresponding single mutant A" );
      System.out.println("With observed A phenotype " + "phenoNameA: " + phenoName + " phenoValueA: "+phenoValueA);
      System.out.println("With observed deviation " + "phenoNameDeviation: " + phenoNameDeviation + " phenoValuedev: "+devValueA);
      System.out.println("Experiment " + expB.name  +" is the corresponding single mutant B" );
      System.out.println("With observed B phenotype " + "phenoNameB: " + phenoName + " phenoValueB: "+phenoValueB);
      System.out.println("With observed deviation " + "phenoNameDeviation: " + phenoNameDeviation + " phenoValuedev: "+devValueB);
      System.out.println("Experiment " + expWT.name  +" is the corresponding wildtype " );
      System.out.println("With observed phenotype " + "phenoName: " + phenoName + " phenoValueWT: "+phenoValueWT);
      System.out.println("With observed deviation " + "phenoNameDeviation: " + phenoNameDeviation + " phenoValuedev: "+devValueWT);
      System.out.println("\n");*/
    
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
    int [] bins = Utilities.discretize (x,e);

    /*System.out.println("Bins:");
      StringBuffer sb = new StringBuffer();
      for (int i=0; i<x.length; i++) sb.append(bins[i]+" ");
      System.out.println(sb.toString());

      System.out.println("Vals:");
      sb = new StringBuffer();
      for (int i=0; i<x.length; i++) sb.append(x[i]+" ");
      System.out.println(sb.toString());

      System.out.println("Errs:");
      sb = new StringBuffer();
      for (int i=0; i<x.length; i++) sb.append(e[i]+" ");
      System.out.println(sb.toString());*/

    DiscretePhenoValueSet d = new DiscretePhenoValueSet();
    int nlevels = Utilities.levelCount(bins);
    d.setBase(nlevels);
    d.setValues(bins);

    // Create Genetic Interaction
    GeneticInteraction interaction = new GeneticInteraction();
    interaction.setDiscretePhenoValueSet(d);
    interaction.setPhenoEnvironment(this.phenotypeEnv);
    
    // Create Single mutantA and attach to genetic interaction
    SingleMutant m = new SingleMutant();
    m.setName(geneA);
    m.setCommonName(geneA);
    m.setAllele(alleleA);
    m.setAlleleForm(alleleFormA);
    m.setPhenoEnvironment(this.phenotypeEnv);
    interaction.setMutantA(m);

    // Create Single mutantB and attach to genetic interaction
    m = new SingleMutant();
    m.setName(geneB);
    m.setCommonName(geneB);
    m.setAllele(alleleB);
    m.setAlleleForm(alleleFormB);
    m.setPhenoEnvironment(this.phenotypeEnv);
    interaction.setMutantB(m);

    interaction.setGeneticClass();
    
    return(interaction); 
  }


  //-----------------------------------------------------------------------------
  /**
   *
   * For interpreted genetic interaction, attach predicted edge attributes to edges 
   * between A, B, and a phenotype node. 
   *
   * @param nodeA The A node
   * @param nodeB The B node
   * @param nodeP The phenotype node
   * @param result set of edge annotations found by interpreting a genetic interaction
   * @param info addtional information to be included as edge attributes
   *
   * @see GeneticInteraction
   */
  protected void addPredictedEdges ( CyNode nodeA, 
                                     CyNode nodeB, 
                                     CyNode nodeP, 
                                     HashMap result, 
                                     HashMap info ){

    //Ensure that the additional info attribute set specifies that the interaction
    //is of the type geneticInfluence. This may mean overwriting existing interaction 
    //attributes in info 
    info.put("interaction", "geneticInfluence");

    String effect, sourceName, targetName;

    if ( result.get("A to B") != null ){ 
      effect = (String) result.get("A to B"); 
      sourceName = (String)Cytoscape.getNodeAttributeValue(nodeA, Semantics.CANONICAL_NAME);
      targetName = (String)Cytoscape.getNodeAttributeValue(nodeB, Semantics.CANONICAL_NAME );
      CyEdge edge = createEdge(sourceName, targetName, effect, info);
      Cytoscape.setEdgeAttributeValue(edge,GENETIC_INFLUENCE_EFFECT, effect);
    }

    if ( result.get("B to A") != null ){ 
      effect = (String)result.get("B to A"); 
      sourceName = (String)Cytoscape.getNodeAttributeValue(nodeB, Semantics.CANONICAL_NAME);
      targetName = (String)Cytoscape.getNodeAttributeValue(nodeA, Semantics.CANONICAL_NAME);
      CyEdge edge = createEdge(sourceName, targetName, effect, info);
      Cytoscape.setEdgeAttributeValue(edge,GENETIC_INFLUENCE_EFFECT, effect);
    }// if B to A

    if ( result.get("AB combined") != null ){ 
      effect = (String) result.get("AB combined"); 
      sourceName = (String)Cytoscape.getNodeAttributeValue(nodeA, Semantics.CANONICAL_NAME);
      targetName = (String)Cytoscape.getNodeAttributeValue(nodeB, Semantics.CANONICAL_NAME);
      CyEdge edge = createEdge(sourceName, targetName, effect, info);
      Cytoscape.setEdgeAttributeValue(edge,GENETIC_INFLUENCE_EFFECT, effect);
    }// if AB combined


    if ( result.get("A to P") != null ){ 
      effect = (String) result.get("A to P"); 
      sourceName = (String)Cytoscape.getNodeAttributeValue(nodeA, Semantics.CANONICAL_NAME);
      targetName = (String)Cytoscape.getNodeAttributeValue(nodeP, Semantics.CANONICAL_NAME);
      CyEdge edge = createEdge(sourceName, targetName, effect, info);
      Cytoscape.setEdgeAttributeValue(edge,GENETIC_INFLUENCE_EFFECT, effect);
    }// if A to P

    if ( result.get("B to P") != null ){ 
      effect = (String) result.get("B to P"); 
      sourceName = (String)Cytoscape.getNodeAttributeValue(nodeB, Semantics.CANONICAL_NAME);
      targetName = (String)Cytoscape.getNodeAttributeValue(nodeP, Semantics.CANONICAL_NAME);
      CyEdge edge = createEdge(sourceName, targetName, effect, info);
      Cytoscape.setEdgeAttributeValue(edge,GENETIC_INFLUENCE_EFFECT, effect);
    }// if B to P

    
  }
  /**
   * Creates an edge in this.graph with the given interaction type and source and 
   * target nodes, and it copies the attribute->value entries in the hash-map to 
   * the edge's attributes.
   *
   * @return the created edge or null if the edge was not created
   */
  protected CyEdge createEdge 
    (String source_name, 
     String target_name,
     String interaction_type,
     HashMap attributes){
    
    if(source_name != null && target_name != null && interaction_type != null){
      String edgeName = 
        source_name + " (" + interaction_type + ") " + target_name + " " + 
        this.phenotypeEnv.toString();
      // This should create the edge if it does not exist already:
      CyEdge edge = Cytoscape.getCyEdge(source_name,
                                        edgeName,
                                        target_name,
                                        interaction_type); // type of interaction
      // Should not need to do this:
      //edgeAttributes.addNameMapping (edgeName, edge); 
      Set entries = attributes.entrySet();
      Iterator entryIt = entries.iterator();
      while(entryIt.hasNext()){
        Map.Entry entry = (Map.Entry)entryIt.next();
        Cytoscape.setEdgeAttributeValue(edge, (String)entry.getKey(), entry.getValue());
      }    
      this.graph.restoreEdge(edge);
      return edge;
    }// if sourceName, targetName and effect are not null
    return null;
  }
  //-----------------------------------------------------------------------------
  /**
   * Type of genetic interaction implied by <codea>Experiments</code> with a 
   * common <code>Phenotype</code> name.
   * Phenotype values are strings. 
   * 
   * @param expWT       Wild-type experiment
   * @param expA        An experiment in which gene A was manipulated 
   * @param expB        An experiment in which gene B was manipulated 
   * @param expAB       An experiment in which genes A and B were manipulated 
   * @param pRank       The <code>PhenotypeRanking</code> giving the relation 
   *                    of phenotype values 
   *                    
   * @exception IllegalArgumentException If phenotypes names do not all agree, or if number 
   * of genetic manipulations is wrong in any experiment. 
   *
   * @see PhenotypeRanking
   * @author thorsson@systemsbiology.org
   */
  public GeneticInteraction geneticInteractionDiscrete 
    (Experiment expWT, 
     Experiment expA, 
     Experiment expB, 
     Experiment expAB, 
     PhenotypeRanking pRank ) throws IllegalArgumentException
  {
    String phenoName = pRank.getName();
    illegalGeneticInteractionInputs (expWT, expA, expB, expAB, phenoName);

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
  
    //System.out.println("\n"); 
    //System.out.println("Summarizing results of relevant phenotypes"); 
    //System.out.println("Experiment " + expAB.name + " is a double mutant" );
    //System.out.println("With observed phenotype " + "phenoName: " + phenoName + " phenoValue: "+phenoValueAB);
    //System.out.println("Experiment " + expA.name +" is the corresponding single mutant A" );
    //System.out.println("With observed A phenotype " + "phenoNameA: " + phenoName + " phenoValueA: "+phenoValueA);
    //System.out.println("Experiment " + expB.name  +" is the corresponding single mutant B" );
    //System.out.println("With observed B phenotype " + "phenoNameB: " + phenoName + " phenoValueB: "+phenoValueB);
    //System.out.println("\n");

    // Look for blocking of phenotype
    // alleleFormA, alleleFormB, phenoA, phenoB, phenoAB; 


    // Get phenotype values and relations specific to phenoName
    DiscretePhenoValueSet d = 
      encodedPhenoValues(phenoValueWT, phenoValueA, phenoValueB, phenoValueAB, pRank );
    //System.out.println(d); 

    // Create Genetic Interaction
    GeneticInteraction interaction = new GeneticInteraction();
    interaction.setDiscretePhenoValueSet(d);
    interaction.setPhenoEnvironment(this.phenotypeEnv);
    
    // Create Single mutantA and attach to genetic interaction
    SingleMutant m = new SingleMutant();
    m.setName(geneA);
    m.setCommonName(geneA);
    m.setAllele(alleleA);
    m.setAlleleForm(alleleFormA);
    m.setPhenoEnvironment(this.phenotypeEnv);
    interaction.setMutantA(m);

    // Create Single mutantB and attach to genetic interaction
    m = new SingleMutant();
    m.setName(geneB);
    m.setCommonName(geneB);
    m.setAllele(alleleB);
    m.setAlleleForm(alleleFormB);
    m.setPhenoEnvironment(this.phenotypeEnv);
    interaction.setMutantB(m);

    interaction.setGeneticClass();
    
    return interaction; 
  }
  //---------------------------------------------------------------------------------
  /**
   *
   * Integer enoding of string-valued phenotype values 
   *
   * @param phenoWT the wild-type phenotype
   * @param phenoA the phenotype value of geneA single mutant
   * @param phenoB the phenotype value of geneB single mutant
   * @param phenoAB the phenotype value of geneA geneB combined mutant
   * @param pRank the ranking of the phenotypes under consideration
   *
   * @return the encoded phenotype values
   *
   * @see PhenotypeRanking
   * @see DiscretePhenoValueSet
   */
  public DiscretePhenoValueSet encodedPhenoValues (String phenoWT, 
                                                   String phenoA, 
                                                   String phenoB, 
                                                   String phenoAB, 
                                                   PhenotypeRanking pRank )
  {
    
    DiscretePhenoValueSet returnSet = new DiscretePhenoValueSet(); 

    String[] vals = pRank.getPhenotypeValues ();
    int[] intVals = new int[4];
    String[] obs = {phenoWT, phenoA, phenoB, phenoAB};
    for (int i=0;i<obs.length;i++) intVals[i]=Utilities.stringArrayIndex(obs[i],vals);

    //System.out.println( phenoWT +" "+phenoA+" "+phenoB+" "+phenoAB);
    //System.out.println( Utilities.stringRep(intVals) );
    
    int nlevels = Utilities.levelCount(intVals);
    int[] binned = Utilities.bin(nlevels,intVals);
    returnSet.setBase(nlevels);
    returnSet.setValues(binned);
					
    return returnSet;  
  }
  //------------------------------------------------------------------------------------
  /**
   *
   * A utility to identify potential problems with double mutant data
   *
   * @param expWT the wild-type experiment
   * @param expA the A experiment
   * @param expB the B experiment
   * @param expAB the double mutant experiment
   * @param phenoName the phenotype name of the phenotype under consideration 
   *
   * @exception IllegalArgumentException If phenoName is not found in all experiments. 
   * If number of genetic mutations is incorrect. 
   * If A or B alleleForms or genes are incosistent with AB experiment. 
   */
  public void illegalGeneticInteractionInputs 
    (
     Experiment expWT, 
     Experiment expA, 
     Experiment expB, 
     Experiment expAB, 
     String phenoName) throws IllegalArgumentException
  {

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

  }
  //---------------------------------------------------------------------------------------
  /**
   * Get edges in the given CyNetwork having a given attributeName attributeValue pair, 
   * assuming the latter is a String.
   * This is a general utility function and should eventually be housed elsewhere, or removed 
   * if a corresponding cytoscape function is made available.
   */
  public static CyEdge[] getEdgesByAttributeNameValue 
    (CyNetwork cy_network,
     String attributeName, 
     String attributeValue){
    
    Iterator edgeIt = cy_network.edgesIterator();
    Vector  edgesDesired = new Vector () ; 
 
    while(edgeIt.hasNext()){
      CyEdge edge = (CyEdge)edgeIt.next();
      Object value = Cytoscape.getEdgeAttributeValue(edge, attributeName);
      if(value != null && value.equals(attributeValue)){
        edgesDesired.add(edge);
      }
    }// while there are more edges

    return ( (CyEdge[]) edgesDesired.toArray( new CyEdge[edgesDesired.size()] ) ); 
  } 

}// class GeneticInteractionCalculator
