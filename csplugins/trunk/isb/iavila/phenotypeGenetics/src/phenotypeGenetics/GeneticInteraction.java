/**
 * A representation of a genetic interaction, containing single mutant information,
 * the phenotype inequality and class, and the environment in which 
 * the genetic interaction was probed. A <code>GeneticInteraction</code> will 
 * typically be associated to an edge in the graph.
 *
 * @author original author undocumented
 * @author Iliana Avila
 * @see SingleMutant
 * @see DiscretePhenoValueInequality 
 * @see PhenoEnvironment
 */
package phenotypeGenetics;
import java.io.*;
import java.util.*;

public class GeneticInteraction implements Serializable {
  
	/**
	 * A Map form the String representation of an edge to the GeneticInteraction object that it represents
	 * Since in Cytoscape 2.3 CyAttributes cannot hold any Object as a value, we need this separate datastructure
	 */
	public static Map EDGE_NAME_GENETIC_INTERACTION_MAP = new HashMap();
	
  // Edge attribute names are encoded as public static variables
  public static final String ATTRIBUTE_INEQUALITY = "phenoRelation";
  public static final String ATTRIBUTE_GENETIC_CLASS = "geneticInteractionClass";
  public static final String ATTRIBUTE_MUTANT_A = "A";
  public static final String ATTRIBUTE_MUTANT_B = "B";
  public static final String ATTRIBUTE_ENVIRONMENT = "environment";
  public static final String ATTRIBUTE_PHENOTYPE = "assay";
  public static final String ATTRIBUTE_INTERACTION_NAME = "interaction";
  public static final String ATTRIBUTE_INTERACTION_VALUE = "genetic";
  public static final String ATTRIBUTE_ALLELE_FORM_A = "alleleFormA";
  public static final String ATTRIBUTE_ALLELE_FORM_B = "alleleFormB";
  public static final String ATTRIBUTE_SELF = "geneticInteractionObject";
  public static final String ATTRIBUTE_ALLELE_A = "alleleA";
  public static final String ATTRIBUTE_ALLELE_B = "alleleB";
  public static final String ATTRIBUTE_PHENO_ENVIRONMENT_STRING = "phenoEnvironment";
  public static final String ATTRIBUTE_PHENOTYPEVALUE_WT = "phenotypeValueWT";
  public static final String ATTRIBUTE_PHENOTYPEVALUE_A = "phenotypeValueA";
  public static final String ATTRIBUTE_PHENOTYPEVALUE_B = "phenotypeValueB";
  public static final String ATTRIBUTE_PHENOTYPEVALUE_AB = "phenotypeValueAB";

  protected SingleMutant A;
  protected SingleMutant B;
  protected DiscretePhenoValueInequality discreteInequality;
  protected PhenoEnvironment pe;
  protected String geneticClass;
  protected String phenotypeValueWT;
  protected String phenotypeValueA; 
  protected String phenotypeValueB;
  protected String phenotypeValueAB;
    
  /**
   * Sole constructor
   */
  public GeneticInteraction (){}//GeneticInteraction

  /**
   * @return a hash map containing all the attributes that should 
   * be assigned to the edge
   */
  public HashMap getEdgeAttributes() {
    HashMap m = new HashMap();
    m.put(ATTRIBUTE_INEQUALITY, this.discreteInequality.toString());
    m.put(ATTRIBUTE_GENETIC_CLASS, this.geneticClass);
    m.put(ATTRIBUTE_PHENOTYPE, this.pe.getPhenoName());
    m.put(ATTRIBUTE_ENVIRONMENT, this.pe.getEnvironment().toString());
    m.put(ATTRIBUTE_INTERACTION_NAME, ATTRIBUTE_INTERACTION_VALUE);
    m.put(ATTRIBUTE_ALLELE_A, this.A.getAllele());
    m.put(ATTRIBUTE_ALLELE_B, this.B.getAllele());
    m.put(ATTRIBUTE_ALLELE_FORM_A, Condition.alleleFormToString(this.A.getAlleleForm()) );
    m.put(ATTRIBUTE_ALLELE_FORM_B, Condition.alleleFormToString(this.B.getAlleleForm()) );
    m.put(ATTRIBUTE_MUTANT_A, this.A.getName());
    m.put(ATTRIBUTE_MUTANT_B, this.B.getName());
    m.put(ATTRIBUTE_PHENO_ENVIRONMENT_STRING, this.pe.toString());
    m.putAll(this.pe.getEnvironment());
    m.put(ATTRIBUTE_PHENOTYPEVALUE_WT, this.phenotypeValueWT.toString());
    m.put(ATTRIBUTE_PHENOTYPEVALUE_A, this.phenotypeValueA.toString());
    m.put(ATTRIBUTE_PHENOTYPEVALUE_B, this.phenotypeValueB.toString());
    m.put(ATTRIBUTE_PHENOTYPEVALUE_AB, this.phenotypeValueAB.toString());
    m.put(ATTRIBUTE_SELF, this);
    return m;
  }//getEdgeAttributes
  
  /**
   * Test for absence of all data
   */   
  public boolean isEmpty(){
    return (A.isEmpty() && B.isEmpty() && this.discreteInequality.isEmpty() &&
            pe.isEmpty() && geneticClass == null && 
            phenotypeValueWT == null && 
            phenotypeValueA == null && 
            phenotypeValueB == null && 
            phenotypeValueAB == null);
  }//isEmpty

  /**
   * @return a String representation of the Genetic Interaction
   */
  public String toString (){
    StringBuffer sb = new StringBuffer();
    sb.append(A.toShortString()+" "+B.toShortString());
    sb.append(" ("+this.geneticClass+") ");
    sb.append(this.discreteInequality.toString());
  
    return sb.toString();
  }//toString

  /**
   * @return the canonical name representing the genetic interaction
   * which should be assigned to the edge
   */
  public String getEdgeName (){
    StringBuffer sb = new StringBuffer();
    sb.append(A.toShortString()+" "+B.toShortString());
    sb.append(" ("+this.geneticClass+") ");
    sb.append(pe.toString());
    
    return sb.toString();
  }//getEdgeName

  /**
   * @return the Single Mutant A member
   */
  public SingleMutant getMutantA (){
    return this.A;
  }//getMutantA

  /**
   * Sets the single Mutant A member
   */
  public void setMutantA (SingleMutant A){
    this.A = A;
  }//setMutantA

  /**
   * @return the single mutant B member
   */
  public SingleMutant getMutantB (){
    return this.B ;
  }//getMutantB

  /**
   * Sets the Single Mutant B member
   */
  public void setMutantB (SingleMutant B){
    this.B = B;
  }//setMutantB

  /**
   * Sets the genetic class of the object based on
   * its DiscretePhenoValueInequality
   */
  public void setGeneticClass (){
    if(this.discreteInequality != null){
      this.geneticClass = this.discreteInequality.getMode().getName();
    }
  }//setGeneticClass

  /**
   * @return the genetic class
   */
  public String getGeneticClass (){
    return this.geneticClass;
  }//getGeneticClass

  /**
   * Sets the phenotype value of WT
   */
  public void setPhenotypeValueWT (String phenotypeValueWT){
    this.phenotypeValueWT = phenotypeValueWT; 
  }//setPhenotypeValueWT

  /**
   * @return the phenotype value of WT
   */
  public String getPhenotypeValueWT (){
    return this.phenotypeValueWT;
  }//getPhenotypeValueWT

  /**
   * Sets the phenotype value of A
   */
  public void setPhenotypeValueA (String phenotypeValueA){
    this.phenotypeValueA = phenotypeValueA; 
  }//setPhenotypeValueA

  /**
   * @return the phenotype value of A
   */
  public String getPhenotypeValueA (){
    return this.phenotypeValueA;
  }//getPhenotypeValueA

  /**
   * Sets the phenotype value of B
   */
  public void setPhenotypeValueB (String phenotypeValueB){
    this.phenotypeValueB = phenotypeValueB; 
  }//setPhenotypeValueB

  /**
   * @return the phenotype value of B
   */
  public String getPhenotypeValueB (){
    return this.phenotypeValueB;
  }//getPhenotypeValueB

  /**
   * Sets the phenotype value of AB
   */
  public void setPhenotypeValueAB (String phenotypeValueAB){
    this.phenotypeValueAB = phenotypeValueAB; 
  }//setPhenotypeValueAB

  /**
   * @return the phenotype value of AB
   */
  public String getPhenotypeValueAB (){
    return this.phenotypeValueAB;
  }//getPhenotypeValueAB

  /**
   * @return true if the interaction object is of the same class
   */
  public boolean geneticClassEquals (GeneticInteraction i){
    return this.geneticClass.equals(i.getGeneticClass());
  }//geneticClassEquals

  /**
   * @return true if this interaction object is an interaction
   * (ie. it's not non-interacting)
   */
  public boolean isInteracting (){
    return !this.geneticClass.equals(DiscretePhenoValueInequality.INTERACTION_NON_INTERACTING);
  }//isInteracting

  /**
   * @return true if the interaction object has the same inequality
   */
  public boolean inequalityEquals (GeneticInteraction i){
    return 
    this.discreteInequality.toString().equals(i.getDiscretePhenoValueInequality().toString());
  }//inequalityEquals
  
  /**
   * @return the DiscretePhenoValueInequality
   */
  public DiscretePhenoValueInequality getDiscretePhenoValueInequality (){
    return this.discreteInequality;
  }//getDiscretePhenoValueInequality

  /**
   * Sets the DiscretePhenoValueInequality
   */
  public void setDiscretePhenoValueInequality (DiscretePhenoValueInequality d){
    this.discreteInequality = d;
  }//setDiscretePhenoValueInequality

  /**
   * @return the PhenoEnvironment
   */
  public PhenoEnvironment getPhenoEnvironment (){
    return this.pe;
  }//getPhenoEnvironment

  /**
   * Sets the PhenoEnvironemnt
   */
  public void setPhenoEnvironment (PhenoEnvironment pe){
    this.pe = pe;
  }//setPhenoEnvironment

}//GeneticInteraction
