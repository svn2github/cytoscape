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
// GeneticInteraction.java: A representation of a genetic interaction
//-----------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------

package phenotypeGenetics;

import java.io.*;
import java.util.*;

/**
 * A representation of a genetic interaction, containing single mutant information,
 * the phenotype inequality and class, and the environment in which 
 * the genetic interaction was probed. A <code>GeneticInteraction</code> will 
 * typically be associated to an edge in the graph.
 *
 * @see SingleMutant
 * @see DiscretePhenoValueSet 
 * @see PhenoEnvironment
 */
public class GeneticInteraction implements Serializable {
  
  // Edge attribute names
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

  SingleMutant A;
  SingleMutant B;
  DiscretePhenoValueSet d;
  PhenoEnvironment pe;
  String geneticClass;

  /**
   * Empty constructor.
   */
  public GeneticInteraction () {}

  /**
   * @return a hash map containing all the attributes
   * that should be assigned to the edge
   */
  public HashMap getEdgeAttributes() {
    HashMap m = new HashMap();
    m.put(ATTRIBUTE_INEQUALITY, this.d.toString());
    m.put(ATTRIBUTE_GENETIC_CLASS, this.geneticClass);
    m.put(ATTRIBUTE_PHENOTYPE, this.pe.getPhenoName());
    m.put(ATTRIBUTE_ENVIRONMENT, this.pe.getEnvironment().toString());
    m.put(ATTRIBUTE_INTERACTION_NAME, ATTRIBUTE_INTERACTION_VALUE);
    m.put(ATTRIBUTE_ALLELE_A, this.A.getAllele());
    m.put(ATTRIBUTE_ALLELE_B, this.B.getAllele());
    m.put(ATTRIBUTE_ALLELE_FORM_A, Condition.alleleFormToString(this.A.getAlleleForm()) );
    m.put(ATTRIBUTE_ALLELE_FORM_B, Condition.alleleFormToString(this.B.getAlleleForm()) );
    //  m.put(ATTRIBUTE_ALLELE_FORM_A, this.A.getAlleleForm());
    //  m.put(ATTRIBUTE_ALLELE_FORM_B, this.B.getAlleleForm());
    m.put(ATTRIBUTE_MUTANT_A, this.A.getName());
    m.put(ATTRIBUTE_MUTANT_B, this.B.getName());
    m.put(ATTRIBUTE_PHENO_ENVIRONMENT_STRING, this.pe.toString());
    m.putAll(this.pe.getEnvironment());
    m.put(ATTRIBUTE_SELF, this);
    return(m);
  }

  //------------------------------------------------------------------------------------ 
  /**
   * Test for absence of all data
   */   
  public boolean isEmpty(){
    return ( A.isEmpty() && B.isEmpty() && d.isEmpty() &&
             pe.isEmpty() && geneticClass == null ); 

  }
  
  //------------------------------------------------------------------------------------
  /**
   * @return a String representation of the Genetic Interaction
   */
  public String toString () {
    StringBuffer sb = new StringBuffer();
    sb.append(A.toShortString()+" "+B.toShortString());
    sb.append(" ("+this.geneticClass+") ");
    sb.append(d.toString());
    return(sb.toString());
  }

  /**
   * @return the canonical name representing the genetic interaction
   * which should be assigned to the edge
   */
  public String getEdgeName () {
    StringBuffer sb = new StringBuffer();
    sb.append(A.toShortString()+" "+B.toShortString());
    sb.append(" ("+this.geneticClass+") ");
    sb.append(pe.toString());
    return(sb.toString());
  }

  /**
   * @return the Single Mutant A member
   */
  public SingleMutant getMutantA () {
    return(this.A);
  }

  /**
   * Sets the single Mutant A member
   */
  public void setMutantA (SingleMutant A) {
    this.A = A;
  }

  /**
   * Gets the single mutant B member
   */
  public SingleMutant getMutantB () {
    return(this.B);
  }

  /**
   * Sets the Single Mutant B member
   */
  public void setMutantB (SingleMutant B) {
    this.B = B;
  }

  /**
   * Sets the genetic class of the object based on
   * its DiscretePhenoValue set. Do not call unless
   * the DiscretePhenoValue set has been assigned!
   */
  public void setGeneticClass () {
    if (this.d != null) {
      this.geneticClass = d.classify();
    }
  }

  /**
   * @return the genetic class
   */
  public String getGeneticClass () {
    return(this.geneticClass);
  }

  /**
   * @return true if the interaction object is of the same class
   */
  public boolean geneticClassEquals (GeneticInteraction i) {
    if (this.geneticClass.equals(i.getGeneticClass())) {;
      return(true);
    } else {
      return(false);
    }
  }

  /**
   * @return true if this interaction object is an interaction
   * (ie. it's not non-interacting)
   */
  public boolean isInteracting () {
    if (this.geneticClass.equals(DiscretePhenoValueSet.INTERACTION_NON_INTERACTING)) {
      return(false);
    } else {
      return(true);
    }
  }

  /**
   * @return true if the interaction object has the same inequality
   */
  public boolean inequalityEquals (GeneticInteraction i) {
    if ( this.d.toString() .equals(i.getDiscretePhenoValueSet().toString())) {
      return(true);
    } else {
      return(false);
    }
  }
    
  /**
   * @return the DiscretePhenoValueSet
   */
  public DiscretePhenoValueSet getDiscretePhenoValueSet () {
    return(this.d);
  }

  /**
   * Sets the DiscretePhenoValueSet
   */
  public void setDiscretePhenoValueSet (DiscretePhenoValueSet d) {
    this.d = d;
  }

  /**
   * @return the PhenoEnvironment
   */
  public PhenoEnvironment getPhenoEnvironment () {
    return(this.pe);
  }

  /**
   * Sets the PhenoEnvironemnt
   */
  public void setPhenoEnvironment (PhenoEnvironment pe) {
    this.pe = pe;
  }

  //------------------------------------------------------------------------------------
  /**
   * Model genetic interaction
   * 
   * @return Name-value pairs for predicted relations between A, B, and phenotype
   */
  public HashMap interpret ()
  {
    String type = d.oldClassify(); 
    HashMap result = new HashMap (); 

    // Two-level
    if (type.equals("boolecombo")) result = boolecombo(); 
    if (type.equals("boolerescue")) result = boolerescue(); 
     
    // Three-level
    if (type.equals("hh")) result = hh(); 
    if (type.equals("linrep")) result = linrep(); 
    if (type.equals("regulatory")) result = regulatory(); 
    if (type.equals("linearcombo")) result = linearcombo(); 
    
    // Four-level
    if (type.equals("additive")) result = additive();

    return result; 
  }

  //------------------------------------------------------------------------------------

  private HashMap boolerescue (){
    
    HashMap returnMap = new HashMap ();

    //{1,0,1,1},
    //{1,1,0,1},
    //{0,1,0,0},
    //{0,0,1,0}

    int pWT = d.getWT();
    int pA  = d.getA();
    int pB  = d.getB();
    int pAB = d.getAB();
    int [] v = {pWT,pA,pB,pAB};

    int indexCentral=1000;  
    int indexRegulator=1000;  
    int deltaCentral=1000;  
    int deltaRegulator=1000;  
    String centralGene="";
    String regulatorGene="";
    if ( pA == pWT ){
      indexCentral = 2; 
      indexRegulator = 1;
      deltaCentral = mutantEffect(B.getAlleleForm());
      deltaRegulator = mutantEffect(A.getAlleleForm());
      centralGene = "B";
      regulatorGene = "A";
    } else if ( pB == pWT ){
      indexCentral = 1; 
      indexRegulator = 2; 
      deltaCentral = mutantEffect(A.getAlleleForm()); 
      deltaRegulator = mutantEffect(B.getAlleleForm());
      centralGene = "A";
      regulatorGene = "B";
    } else {
      System.out.println("Error: no regulator\n");
    }

    int centralEffect = ( v[indexCentral] - pWT )/Math.abs(v[indexCentral]-pWT);
    int signCentral = centralEffect/deltaCentral;
    int signRegulator = - centralEffect/deltaRegulator; // counteracts the central effect

    //System.out.println("Sign, Central: " + signCentral );
    //System.out.println("Sign, Regulator: "+ signRegulator);

    returnMap.put( centralGene+" to P", influence(signCentral) );
    returnMap.put( regulatorGene+" to "+centralGene, regInfluence(signRegulator) ); 
    //returnMap.put( regulatorGene+" to P", "none"); 

    return returnMap;
  }

  //------------------------------------------------------------------------------------

  private HashMap boolecombo (){  

    //{0,1,1,1},
    //{1,1,1,0},      
    //{1,0,0,0},
    //{0,0,0,1}

    HashMap returnMap = new HashMap(); 

    int pWT = d.getWT();
    int pA  = d.getA();
    int pB  = d.getB();
    int pAB = d.getAB();
    int [] v = {pWT,pA,pB,pAB};

    int deltaA = mutantEffect(A.getAlleleForm()); 
    int deltaB = mutantEffect(B.getAlleleForm()); 

    // Use overall effect  and mutant Effect

    int signA = ( pAB - pWT ) / deltaA ; 
    int signB = ( pAB - pWT ) / deltaB ; 

    //System.out.println("Sign, A: " + signA);
    //System.out.println("Sign, B: " + signB);
    
    String combinationThresholding = "";  
    if ( pA == pWT && pB == pWT ){
      combinationThresholding = "and";
    } else if ( pA == pAB && pB == pAB ){
      combinationThresholding = "or";
    }
    returnMap.put( "A to P", influence(signA) ); 
    returnMap.put( "B to P", influence(signB) ); 
    returnMap.put( "AB combined", combinationThresholding );  

    return returnMap; 

  }
  //------------------------------------------------------------------------------------

  private HashMap linrep (){
    
    HashMap returnMap = new HashMap ()  ;

    int pWT = d.getWT();
    int pA  = d.getA();
    int pB  = d.getB();
    int pAB = d.getAB();

    int [] v = {pWT,pA,pB,pAB};

    int deltaA = mutantEffect(A.getAlleleForm()); 
    int deltaB = mutantEffect(B.getAlleleForm()); 

    //Reminder of combinations
    //{1,2,0,0},
    //{1,0,2,0},
    //{1,2,0,2},
    //{1,0,2,2}

    int indexDown = 10000; 
    int indexUp = 10000; 
    int deltaDown = 10000; 
    int deltaUp = 10000; 
    String upGene ="";
    String downGene = ""; 

    if ( pA==pAB ){ // A downstream 
      indexDown = 1; 
      indexUp = 2;
      deltaDown = deltaA; 
      deltaUp = deltaB;
      upGene = "B";
      downGene = "A"; 
    }
    else if ( pB==pAB ){ // B downstream 
      indexDown = 2; 
      indexUp = 1; 
      deltaDown = deltaB; 
      deltaUp = deltaA;
      upGene = "A";
      downGene = "B"; 
    }
    // else complain
  
    int signDown = ( v[indexDown] - pWT )/deltaDown;
    int signUp = (v[indexUp] -pWT )/deltaUp * signDown; 

    //System.out.println("Sign, upstream: "+signUp );
    //System.out.println("Sign, downstream: "+signDown );

    returnMap.put( downGene+" to P", influence(signDown) );
    returnMap.put( upGene +" to "+downGene, influence(signUp)); 
    // Examine this later: can include this independence relation explicitly
    //returnMap.put( upGene +" to P", "none" ); 

    return returnMap; 
  }
  //------------------------------------------------------------------------------------
  private HashMap linearcombo (){

    HashMap returnMap = new HashMap(); 
    //{0,1,1,2},
    //{2,1,1,0},
    //{1,2,0,1},
    //{1,0,2,1}

    int pWT = d.getWT();
    int pA  = d.getA();
    int pB  = d.getB();
    int pAB = d.getAB();

    int [] v = {pWT,pA,pB,pAB};

    int deltaA = mutantEffect(A.getAlleleForm()); 
    int deltaB = mutantEffect(B.getAlleleForm()); 

    int signA = ( pA - pWT ) / deltaA ; 
    int signB = ( pB - pWT ) / deltaB ; 

    //System.out.println("Sign, A: "+ signA );
    //System.out.println("Sign, B: "+signB);

    returnMap.put( "A to P", influence(signA) ); 
    returnMap.put( "B to P", influence(signB) ); 
    returnMap.put( "AB combined", "sum" ); 

    return returnMap; 

  }

  //------------------------------------------------------------------------------------

  private HashMap hh (){

    HashMap returnMap = new HashMap(); 

    //{2,1,0,0},
    //{2,0,1,0}
    //{0,1,2,2},
    //{0,2,1,2}


    int pWT = d.getWT();
    int pA  = d.getA();
    int pB  = d.getB();
    int pAB = d.getAB();

    int [] v = {pWT,pA,pB,pAB};

    int deltaA = mutantEffect(A.getAlleleForm()); 
    int deltaB = mutantEffect(B.getAlleleForm()); 

    int indexDown = 10000; 
    int indexUp = 10000; 
    int deltaDown = 10000; 
    int deltaUp = 10000; 
    String upGene ="";
    String downGene ="";

    if ( pA==pAB ){ // A upstream 
      indexDown = 2; 
      indexUp = 1; 
      deltaDown = deltaB; 
      deltaUp = deltaA;
      upGene = "A";
      downGene = "B";
    }
    else if ( pB==pAB ){ // B upstream 
      indexDown = 1; 
      indexUp = 2;
      deltaDown = deltaA; 
      deltaUp = deltaB;
      upGene = "B";
      downGene = "A"; 
    }
    // else complain
  
    int signDown = ( v[indexDown] - pWT )/deltaDown;

    // pUpDown = pUp therefore 
    // (pUpDown-pWT) = deltaUp * signUp + signDown ( deltaDown )
    //            = deltaUp * signUp + signDown ( deltaUp * signUpDown )

    int signUpDown = deltaDown/deltaUp ; 
    int signUp =  ( pAB-pWT - signDown * deltaDown )/ deltaUp ; 

    int signUp2 = ( pAB- v[indexDown])/deltaUp; 
    if ( signUp != signUp2 ) System.out.println("Yell and scream");



    //System.out.println("Sign, upstream: "+signUp );
    //System.out.println("Sign from Up to Down: " +  signUpDown );
    //System.out.println("Sign, downstream: "+signDown );    

    returnMap.put( downGene+" to P", influence(signDown) );
    returnMap.put( upGene +" to "+downGene, influence(signUpDown)); 
    returnMap.put( upGene +" to P", influence(signUp) ); 

    return returnMap; 
  }
  //------------------------------------------------------------------------------------
  private HashMap regulatory (){

    //{0,0,1,2},
    //{0,0,2,1},
    //{0,1,0,2},
    //{0,2,0,1},
    //{2,2,0,1},
    //{2,1,2,0},
    //{2,0,2,1},
    //{2,2,1,0}

    HashMap returnMap = new HashMap(); 

    int pWT = d.getWT();
    int pA  = d.getA();
    int pB  = d.getB();
    int pAB = d.getAB();

    int [] v = {pWT,pA,pB,pAB};

    int indexCentral=1000;  
    int indexRegulator=1000;  
    int deltaCentral=1000;  
    int deltaRegulator=1000;
    String centralGene ="";
    String regulatorGene ="";
    if ( pA == pWT ){ // B is the central gene, and A is the regulator
      indexCentral = 2; 
      indexRegulator = 1;
      deltaCentral = mutantEffect(B.getAlleleForm()); 
      deltaRegulator = mutantEffect(A.getAlleleForm()); 
      centralGene = "B";
      regulatorGene = "A";
    } else if ( pB == pWT ){ // A is the central gene, and B is the regulator
      indexCentral = 1; 
      indexRegulator = 2; 
      deltaCentral = mutantEffect(A.getAlleleForm()); 
      deltaRegulator = mutantEffect(B.getAlleleForm());
      centralGene = "A";
      regulatorGene = "B";
    } else {
      System.out.println("Error: no regulator\n");
    }

    int signCentral = ( v[indexCentral]-pWT)/Math.abs(v[indexCentral]-pWT)/ deltaCentral;
    int signRegulator = ( pAB - v[indexCentral] )/Math.abs(pAB - v[indexCentral])/deltaRegulator; // the effect of the regulator mutant in Central background


    //System.out.println("Sign, Central: " + signCentral );
    //System.out.println("Sign, Regulator: "+ signRegulator);

    returnMap.put( centralGene+" to P", influence(signCentral) );
    returnMap.put( regulatorGene+" to "+centralGene, regInfluence(signRegulator) ); 
    // returnMap.put( regulatorGene+" to P", "none"); Leave out for now

    return returnMap;
  }
  //------------------------------------------------------------------------------------
  private HashMap additive (){  

    //{0,1,2,3},
    //{0,2,1,3},
    //{1,3,0,2},
    //{2,3,0,1},
    //{1,0,3,2},
    //{3,1,2,0},
    //{2,0,3,1},
    //{3,2,1,0}

    HashMap returnMap = new HashMap(); 

    int pWT = d.getWT();
    int pA  = d.getA();
    int pB  = d.getB();
    int pAB = d.getAB();
    int [] v = {pWT,pA,pB,pAB};
    
    int deltaA = mutantEffect(A.getAlleleForm()); 
    int deltaB = mutantEffect(B.getAlleleForm()); 
    int signA = (pA-pWT)/Math.abs(pA-pWT)/deltaA ; 
    int signB = (pB-pWT)/Math.abs(pB-pWT)/deltaB ; 

    //System.out.println("Sign, A: " + signA);
    //System.out.println("Sign, B: " + signB);
    
    returnMap.put( "A to P", influence(signA) ); 
    returnMap.put( "B to P", influence(signB) ); 
    returnMap.put( "AB combined", "sum" ); 

    return returnMap;
  }

  //------------------------------------------------------------------------------------
  /**
   * Integer encoding of the effect of the alleleForm
   */
  private int mutantEffect ( int alleleForm ){

    int e=0; 

    if ( alleleForm == Condition.LF ||
         alleleForm == Condition.LF_PARTIAL ||
         alleleForm == Condition.DN ) e=-1 ;
    
    if ( alleleForm == Condition.GF ||
         alleleForm == Condition.GF_PARTIAL ) e=1 ; 

    return e;
  }

  //------------------------------------------------------------------------------------
  /** 
   * String encoding of gene influence, represented as integer
   * 
   * @exception IllegalArgumentException If integerInfluence is not 1 or -1
   */
  private String influence ( int integerInfluence ) throws IllegalArgumentException {

    if ( integerInfluence !=1 && integerInfluence != -1 )
      System.out.println("Error: expected influence coded as 1 or -1");
    String returnInf = "";
    if ( integerInfluence == 1 ) returnInf = "positive"; 
    else if ( integerInfluence == -1 ) returnInf = "negative"; 
	
    return returnInf; 
			     
  }
  //------------------------------------------------------------------------------------
  /** 
   * String encoding of reglatory gene influence, represented as integer
   * 
   * @exception IllegalArgumentException If integerInfluence is not 1 or -1
   */
  private String regInfluence ( int integerInfluence ) throws IllegalArgumentException {

    if ( integerInfluence !=1 && integerInfluence != -1 )
      System.out.println("Error: expected influence coded as 1 or -1");
    String returnInf = "";
    if ( integerInfluence == 1 ) returnInf = "posreg"; 
    else if ( integerInfluence == -1 ) returnInf = "negreg"; 
	
    return returnInf; 
			     
  }
  //------------------------------------------------------------------------------------

}
