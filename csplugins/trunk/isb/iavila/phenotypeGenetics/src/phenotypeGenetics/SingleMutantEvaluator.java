// SingleMutantEvaluator.java
//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------------------
package csplugins.phenotypeGenetics;
//----------------------------------------------------------------------------------------
import java.util.*;
import java.io.*;
import y.base.*;
import y.view.*;
import cytoscape.*;

//----------------------------------------------------------------------------------------
/**
 *
 * Add attributes to nodes based on their observed effect on phenotype under single mutations.
 *
 * @see GraphObjAttributes
 */
// Programming notes (Oct 1,2003,VT)
// In next code iteration, consider using SingleMutant class
// Also consider introducing a new classes, i.e. 'two-level' analogs to DiscretePhenoValueSet
// and GeneticInteraction. 
// Nodes should really receive additional attributes relating to experimental evidence, 
// and this may be the right way to introduce them
// 
public class SingleMutantEvaluator {

    Project project ;  
    Graph2D graph ;
    CytoscapeWindow cytoscapeWindow;
    GraphObjAttributes nodeAttributes;

//----------------------------------------------------------------------------------------
/**
* Constructs a new SingleMutantEvaluator given a project, relation among phenotypes, and a CytoscapeWindow.
*
* @param project the <code>Project</code> under consideration
* @param cytoscapeWindow the <code>CytoscapeWindow</code> to be modified
*/
//
//  ToDo: PhenotypeTree should not be a necessary argument for real-valued phenotypes 
//
public SingleMutantEvaluator ( Project project, CytoscapeWindow cytoscapeWindow )
{

    this.project = project; 
    this.cytoscapeWindow = cytoscapeWindow ; 
    this.graph = cytoscapeWindow.getGraph(); 
    this.nodeAttributes =cytoscapeWindow.getNodeAttributes (); 

}
//----------------------------------------------------------------------------------------
/**
 *
 * Identify effect of single mutant, and incorporate as a node attribute.
 *
 **/
public void calculate () throws Exception 
{

    String [] phenoNames = project.getPhenotypeNames();

    for ( int k=0 ; k<phenoNames.length ; k++ ){

	String phenoName = phenoNames[k]; 
	
	boolean isErrorPhenotype = phenoName.endsWith(Phenotype.deviationString);
	if ( isErrorPhenotype ) continue; 

	System.out.println("---- Examining phenotype: "+phenoName ); 

	// Compute dependencies
	HashMap relations = singleGeneDependence ( phenoName, project );
	
	//
	// Update node attributes
	//
	nodeAttributes.deleteAttribute("interaction"); // Clear all interaction attributes for nodes
	String  [] genes = (String [])relations.keySet().toArray (new String [0]);
	for (int i=0 ; i<genes.length ; i++ ){
	    String geneName = genes[i];
	    Node node = cytoscapeWindow.getNode(geneName);
	    String impliedRelation = (String) relations.get(geneName);
	    
	    // Get existing relation, if any 
	    String existingRelation = nodeAttributes.getStringValue("interaction", geneName ) ;
	    
		
	    //If no relation exists or none was found in earlier experiment add impliedRelation to node attributes
	    if ( existingRelation ==null || existingRelation.equals("none")  ){
		nodeAttributes.set ("interaction", geneName, impliedRelation );
		nodeAttributes.addNameMapping(geneName, node);
		// do nothing if agreement with current one
	    } else if ( impliedRelation.equals(existingRelation) ) { // do nothing if agreement with current one
		//Warning: If implied relation contradicts existing relation and implied Relation is not"none"
	    } else if ( !impliedRelation.equals(existingRelation ) && !impliedRelation.equals("none") ) {
		System.out.println("For gene " + geneName + " there are two different implied effects " +
				   impliedRelation + " and " + existingRelation );
	    }
	}
	
    }

	
}// End method calculate   
//----------------------------------------------------------------------------------------
/**
 * For a given phenotype name, the dependence of the phenotype on genes, based on all single mutants in a project
 * 
 * @param phenoName The phenotype name
 * @param project   The project
 **/
public HashMap singleGeneDependence ( String phenoName, Project project ) throws Exception {

    Project pN = project.filterByPhenotypeName( phenoName );

    Project pSingleMutant = pN.filterByNumberOfMutants(1);
    Experiment [] experimentArray = pSingleMutant.getExperiments (); 

    Project wtExperiments = project.filterByNumberOfMutants(0);
    System.out.println("---------------Wild-type Experiments-------------\n" + wtExperiments );
    // For now, assume one wild-type Experiment
    if ( wtExperiments.numberOfExperiments() > 1 ) 
	System.out.println("Warning, more than one wild-type experiment - check that wild-type phenotypevalues agree");
    Experiment expWT =  wtExperiments.getExperiments()[0];

    // Get wild-type values 
    Phenotype wtPheno = expWT.getPhenotypeWithName(phenoName);
    String phenoValueWT = wtPheno.getValue();
    // Determine if the phenotype can be converted to double
    // If so, we *assume* for now that the same is true of single mutant phenotypes 
    // If not, we *assume* for now that the same is true of single mutant phenotypes
    boolean isDouble = false; 

    try {
	Double.parseDouble (phenoValueWT);
	isDouble = true; 
    }
    catch (NumberFormatException e) {
	isDouble = false; // Phenotype values are strings
    }
	
    HashMap relations = new HashMap(); 

    // Loop over all single mutants
    for (int i=0 ; i<pSingleMutant.numberOfExperiments() ; i++ ){

	// Get single mutant alleleForm
	Experiment exp = experimentArray[i];
	Condition [] mutantArray = exp.getGeneticConditions();
	Condition mutant = mutantArray[0]; // Later, add dimensionality check
	String geneName = mutant.getGene();
	int alleleForm = mutant.getAlleleForm(); 
	
	// Get single mutant phenotype value
	Phenotype obs = exp.getPhenotypeWithName(phenoName);
	String phenoValue = obs.getValue();

	// Proceed to determine impliedRelation 
	String impliedRelation="none"; 
	if ( isDouble ){ // If data is real-valued (w/error) determine single mutant effect as follows
	    impliedRelation = singleGeneEffectContinuous (phenoName, exp, expWT );
	} // End operations for real-valued phenotypes
	if ( !isDouble ){ // If pheno values are strings, process as follows
	    //Read phenotype relations
	    PhenotypeTreeSelector phenotypeTreeSelector = new PhenotypeTreeSelector(project);
	    phenotypeTreeSelector.read ();
	    PhenotypeTree phenotypeTree = phenotypeTreeSelector.getPhenotypeTree();    
	    PhenotypeRanking pRank = phenotypeTree.getPhenotypeWithName (phenoName);
	    impliedRelation = singleGeneEffectDiscrete (alleleForm, pRank, phenoValue, phenoValueWT);
	} 
	
	relations.put(geneName, impliedRelation) ; 
	
	    
    }// End loop over single mutant experiments 

    
    return relations;
}

//----------------------------------------------------------------------------------------
/**
 * The implied dependence (positive/negative/none) of a phenotype on a gene, 
 * based the mutant alleleForm and phenotype value.
 *
 * @param alleleForm The mutant alleleForm
 * @param phenoName The phenotype name 
 * @param phenoValue The mutant phenotype value 
 * @param phenoValueWT The wild-type phenotype value
 **/
public String singleGeneEffectDiscrete (int alleleForm, PhenotypeRanking pRank , String phenoValue, String phenoValueWT ){

    String phenoName = pRank.getName(); 
    String impliedRelation = "none";
    if ( !pRank.isEmpty() ){
	String phRel_WT = pRank.relativePhenotypeValue(phenoValue, phenoValueWT   ) ;
	String manip=""; // computue impliedRelation
	if ( alleleForm == Condition.LF || alleleForm == Condition.LF_PARTIAL || alleleForm == Condition.DN )  manip = "down";
	if ( alleleForm == Condition.GF || alleleForm == Condition.GF_PARTIAL )  manip = "up";
	if( manip.equals("up") && phRel_WT.equals ("GREATER") ) impliedRelation = "positive";
	if( manip.equals("down") && phRel_WT.equals ("LESS") ) impliedRelation = "positive";
	if( manip.equals("up") && phRel_WT.equals ("LESS") ) impliedRelation = "negative";
	if( manip.equals("down") && phRel_WT.equals ("GREATER") ) impliedRelation = "negative";
	if( manip.equals("down") && phRel_WT.equals ("OPPOSITE") ) impliedRelation = "negative";
    }
    return impliedRelation;

}
//----------------------------------------------------------------------------------------
/**
 * The implied dependence (positive/negative/none) of a phenotype on a gene, 
 * based the mutant alleleForm and phenotype value. Phenotypes are real values, with errors.
 *
 * @param phenoName The phenotype name 
 * @param exp The single mutant <code>Experiment</code> 
 * @param expWT The wild-type experiment <code>Experiment</code>
 **/
public String singleGeneEffectContinuous (String phenoName, Experiment exp, Experiment expWT ) throws Exception {

    String impliedRelation="none";

    Condition [] mutantArray = exp.getGeneticConditions();
    Condition mutant = mutantArray[0]; // Later, add dimensionality check
    String geneName = mutant.getGene();
    int alleleForm = mutant.getAlleleForm(); 


    // Get value for wild-type and single mutant observation
    Phenotype phenoWT = expWT.getPhenotypeWithName(phenoName);
    String valueWT = phenoWT.getValue();
    double pWT = Double.parseDouble (valueWT);
    Phenotype pheno = exp.getPhenotypeWithName(phenoName);
    String value  = pheno.getValue(); 
    double p = Double.parseDouble (value);

    // Get error for wild-type and single mutant observation
    String phenoNameDeviation = phenoName + Phenotype.deviationString;
    Phenotype phenoWTdev = expWT.getPhenotypeWithName(phenoNameDeviation);
    String devValueWT = phenoWTdev.getValue();
    double pWTe = Double.parseDouble (devValueWT);
    Phenotype phenodev = exp.getPhenotypeWithName(phenoNameDeviation);
    String devValue  = phenodev.getValue(); 
    double pe  = Double.parseDouble (devValue);

    //System.out.println("Experiment " + exp.name  +" is the single mutant" );
    //System.out.println("With observed phenotype " + "phenoName: " + phenoName + " phenoValue: "+phenoValue);
    //System.out.println("With observed deviation " + "phenoNameDeviation: " + phenoNameDeviation + " phenoValuedev: "+devValue);
    //System.out.println("Experiment " + expWT.name  +" is the corresponding wildtype " );
    //System.out.println("With observed phenotype " + "phenoName: " + phenoName + " phenoValueWT: "+phenoValueWT);
    //System.out.println("With observed deviation " + "phenoNameDeviation: " + phenoNameDeviation + " phenoValuedev: "+devValueWT);
    //System.out.println("\n");
		    
    // Vector versions of data
    double [] x = {pWT, p};
    double [] e = {pWTe, pe};
    // Divide into bins. bins can only be {0,0}, {1,0}, or {0,1}. Some error checking below. 
    int [] bins = Utilities.discretize (x,e);

    // Integer coding of manipulation
    int manip=99999999; 
    if ( alleleForm == Condition.LF || alleleForm == Condition.LF_PARTIAL || alleleForm == Condition.DN )  manip = -1;
    if ( alleleForm == Condition.GF || alleleForm == Condition.GF_PARTIAL )  manip = 1;
		    
    // The implied effect, encoded as integer
    int implied = (bins[1]-bins[0])/manip;

    // String encoding of implied effect
    if ( implied == 1){
	impliedRelation = "positive";
    } else if ( implied == -1 ){
	impliedRelation = "negative";
    } else if ( implied == 0 ){
	impliedRelation = "none";
    } else {
	System.out.println("Error: Single mutant changes by more than one level");
    }

    return impliedRelation;


}

//--------------------------------------------------------------------------------------
/**
 *
 * Identify the effect of single mutant, and compare with current node attributes.
 * No modification of the node attributes is made.
 * Results are printed to standard output.
 *
 **/
public void compare () throws Exception 
{

    String [] phenoNames = project.getPhenotypeNames();

    for ( int k=0 ; k<phenoNames.length ; k++ ){

	String phenoName = phenoNames[k]; 
	
	boolean isErrorPhenotype = phenoName.endsWith(Phenotype.deviationString);
	if ( isErrorPhenotype ) continue; 

	System.out.println("---- Examining phenotype: "+phenoName ); 

	// Compute dependencies
	HashMap relations = singleGeneDependence ( phenoName, project );
	
	//
	// Compare implied relation to existingRelation
	//

	String  [] genes = (String [])relations.keySet().toArray (new String [0]);
	for (int i=0 ; i<genes.length ; i++ ){
	    String geneName = genes[i];
	    Node node = cytoscapeWindow.getNode(geneName);
	    String impliedRelation = (String) relations.get(geneName);
	    
	    // Get existing relation, if any 
	    String existingRelation = nodeAttributes.getStringValue("interaction", geneName ) ;
	    String displayedName = nodeAttributes.getStringValue("commonName", geneName ) ;

	    if ( impliedRelation.equals(existingRelation) ) { // Report if agreement with current one
		System.out.println("The "+impliedRelation + " effect of the gene " + displayedName + " agrees with the earlier result.");
	    } else if ( existingRelation ==null || existingRelation.equals("none")  ){
		System.out.println("The "+impliedRelation + " effect of the gene " + displayedName + " was not found earlier.");
		//Warning: If implied relation contradicts existing relation and implied Relation is not"none"
	    } else if ( !impliedRelation.equals(existingRelation ) && !impliedRelation.equals("none") ) {
		System.out.println("The "+impliedRelation + " effect of the gene " + displayedName + " differs from the earlier result: "+existingRelation);
	    }


	    
	}
    }

}// End method compare   



//----------------------------------------------------------------------------------------
} // class SingleMutantEvaluator

