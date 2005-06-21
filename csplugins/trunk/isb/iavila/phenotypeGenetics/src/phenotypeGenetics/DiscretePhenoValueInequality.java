/**
 * An encoding of the observed phenotype values for the four strains:
 * <pre>
 * <b>strain</b> <b>(phenotypeValue)</b>
 * wild-type (pWT)
 * A mutant (pA)
 * B mutant (pB)
 * AB mutant (pAB)
 * </pre>
 * To each phenotype value is assigned an integer. The integers encode
 * the relative relations of the phenotype values. The range of the integers
 * reflects the number of distinct "levels" of the four phenotype values.  
 * For example, the relation <br><br>
 * <pre> pWT=pB&lt;pAB&lt;pA </pre> <br> 
 * is encoded: pWT=0, pA=2, pB=0, pAB=1 .
 *
 * @author original author not documented
 * @author Iliana Avila refactored and added new methods
 */

package phenotypeGenetics;

import java.io.*;
import java.util.*;
import java.awt.Color;
import phenotypeGenetics.view.*;
import cytoscape.*;
//import cytoscape.view.*;
//import cytoscape.visual.*;
//import cytoscape.visual.calculators.*;
//import cytoscape.visual.mappings.*;
//import cytoscape.visual.ui.*;


public class DiscretePhenoValueInequality implements Serializable {

  public final static String INTERACTION_NON_INTERACTING = "non-interacting";
  public final static int BASE_UNASSIGNED = -1;
  public final static int PHENOTYPEVALUE_UNASSIGNED = -1;
  public final static String EDGE_ATTRIBUTE = "phenotype inequality";
  public final static String NOT_DIRECTIONAL = "None";
  public final static String A_TO_B = "A->B";
  public final static String B_TO_A = "A<-B";
  public final static Mode UNASSIGNED_MODE = new Mode("unassigned");
  public final static String UNASSIGNED_MODE_NAME = "unsassigned";
  public final static int nObs = 4;
  
  protected static DiscretePhenoValueInequality[] P_INEQUALITIES;
  protected static Map ENCODING_TO_INEQUALITY;
    
  protected int pWT;
  protected int pA;
  protected int pB;
  protected int pAB;
  protected int base;
  
  // The mode to which this inequality has bee assigned
  protected Mode mode;
  
  // (iliana) this is hacky, but it will be fixed in the next release 
  // of PhenotypeGenetics 2.0:
  // Visual properties of edges that represent this kind of inequality
  protected Color color;
  protected String edgeType;
  protected String direction;
  //protected static DiscreteMapping edgeColorMapping;
  //protected static DiscreteMapping edgeTypeMapping;
  //protected static DiscreteMapping arrowMapping;
  
  /**
   * Construct a new <code>DiscretePhenoValueInequality</code>
   * Note that all possible DiscretePhenoValueInequality objects 
   * are contained in a static array called DiscretePhenoValueInequality.P_INEQUALITIES. 
   * This ensures space optimality.
   *
   * @see #getPhenoSet(int wt, int a, int b, int ab)
   */
  private DiscretePhenoValueInequality (){
    this.pWT = PHENOTYPEVALUE_UNASSIGNED; 
    this.pA  = PHENOTYPEVALUE_UNASSIGNED; 
    this.pB  = PHENOTYPEVALUE_UNASSIGNED; 
    this.pAB = PHENOTYPEVALUE_UNASSIGNED;
    this.base = BASE_UNASSIGNED;
    setMode(UNASSIGNED_MODE);
    UNASSIGNED_MODE.addPhenotypeInequality(this);
    setColor(Color.BLACK);
    setEdgeType("LINE_1");
    setDirection(NOT_DIRECTIONAL);
  }//DiscretePhenoValueInequality
  
  /**
   * Test whether <code>DiscretePhenoValueInequality</code> has no data. 
   */
  public boolean isEmpty (){
    return ( this.pWT == PHENOTYPEVALUE_UNASSIGNED && 
             this.pA  == PHENOTYPEVALUE_UNASSIGNED &&
             this.pB  == PHENOTYPEVALUE_UNASSIGNED && 
             this.pAB == PHENOTYPEVALUE_UNASSIGNED &&
             this.base == BASE_UNASSIGNED);
  }//isEmpty

  /**
   * Sets the Mode to which this inequality is assigned
   */
  public void setMode (Mode a_mode){
    if(this.mode == UNASSIGNED_MODE){
      UNASSIGNED_MODE.removePhenotypeInequality(this);
    }
    this.mode = a_mode;
  }//setMode
  
  /**
   * @return the Mode to which this inequality has been assigned, or null
   * if it has not been assigned
   */
  public Mode getMode (){
    return this.mode;
  }//getMode
  
  /**
   * Set number of levels
   *
   * @param nLevels the number of levels
   * @throws IllegalArgumentException if nLevels is less than one.
   */
  public void setBase ( int nLevels ) throws IllegalArgumentException{
    
    if ( nLevels < 1 )
      throw new IllegalArgumentException ("Illegal number of levels"+ nLevels );
    
    this.base = nLevels; 

  }//setBase
  
  /**
   * Set phenotype values
   *
   * @param values integer array with encoded values
   * @exception IllegalArgumentException if number of levels has not been specified, 
   * if integer array does not have four elements, if any value exceeds the number of levels,
   * or if an expected level is not present in values array.
   */
  public void setValues (int [] values) throws IllegalArgumentException{
    
    if(this.base == BASE_UNASSIGNED)
      throw new IllegalArgumentException("Number of levels has not been specified");

    if(values.length != nObs)
      throw new IllegalArgumentException("Illegal length of constructor argument: "
                                         + values.length );
    
    // confirm that levels are legal
    for(int i = 0; i < nObs; i++){
      int val = values[i];
      if(val < PHENOTYPEVALUE_UNASSIGNED || val >= this.base)
        throw new IllegalArgumentException ("Illegal value: "+ val );
    }//for i

    // confirm that each level is represented at least once 
    for(int j = 0; j < this.base; j++){
      boolean foundOne = false;
      for(int i = 0; i < nObs; i++) 
        if(values[i] == j) foundOne = true;
      if(foundOne == false)
        throw new IllegalArgumentException ("No level "+j);
    }//for j
	
    this.pWT = values[0];
    this.pA  = values[1];
    this.pB  = values[2];
    this.pAB = values[3];
  }//setValues
  
  /**
   * Get number of levels
   */
  public int getBase (){
    return this.base;
  }//getBase

  
  /**
   * Get encoded wild-type phenotype value  
   */
  public int getWT (){
    return this.pWT;
  }//getWT
  
  /**
   * Get encoded A mutant phenotype value  
   */
  public int getA (){
    return this.pA;
  }//getA
  
  /**
   * Get encoded B mutant phenotype value  
   */
  public int getB (){
    return this.pB;
  }//getB
  
  /**
   * Get encoded AB mutant phenotype value  
   */
  public int getAB (){
    return this.pAB;
  }//getAB
  
  /**
   * The equals method
   *
   * @param toCompare the <code>DiscretePhenoValueInequality</code> to compare to 
   */
  public boolean equals (DiscretePhenoValueInequality toCompare){
    if(toCompare.getWT() == this.pWT &&
       toCompare.getA() == this.pA &&
       toCompare.getB() == this.pB &&
       toCompare.getAB() == this.pAB){
      return true; 
    }
    return false;
  }//equals
  
  /**
   * @param d the DiscretePhenoValueInequality for which a String encoding will be returned
   * @return the integer encoding of this DiscretePhenoValueInequality as a String
   * for example, if the encoding is 0,0,0,1 for WT, A, B, AB it returns the
   * String "0001"
   */
  public static String getEncodingAsString (DiscretePhenoValueInequality d){
    String encoding = 
      Integer.toString( d.getWT() ) + 
      Integer.toString( d.getA()  ) +
      Integer.toString( d.getB()  ) +
      Integer.toString( d.getAB() );
    return encoding;
  }//getEncodingAsString

  /**
   * @return the String representation of this DiscretePhenoValueInequality
   */
  public String toString(){
    StringBuffer sb = new StringBuffer ();
    
    Vector atLevel;
    
    for(int j = 0; j < this.base; j++){
      
      atLevel = new Vector();
      if(this.pWT == j) atLevel.add("pWT");
      if(this.pA == j) atLevel.add("pA");
      if(this.pB == j) atLevel.add("pB");
      if(this.pAB == j) atLevel.add("pAB");
      
      String [] atLevelArray = (String [])atLevel.toArray(new String [0]);
	
      if(atLevelArray.length == 1) 
        sb.append(atLevelArray[0]);
     
      if(atLevelArray.length>1){
        for(int k = 0; k < (atLevelArray.length-1); k++)sb.append(atLevelArray[k]+"=");
        sb.append(atLevelArray[atLevelArray.length-1]);
      }
       
      if (j != (this.base-1) ) sb.append("<");
    }
    
    return sb.toString ();
    
    /** The following representation may also be considered
        sb.append ("pWT=" +pWT+", ");
        sb.append ("pA=" +pA+", ");
        sb.append ("pB=" +pB+", ");
        sb.append ("pAB=" +pAB);
    **/
  }//toString
    
  /**
   * @return the DiscretePhenoValueInequality with the given phenotype discrete values for 
   * WT, A, B, and AB in that order
   */
  public static DiscretePhenoValueInequality getPhenoInequality (int wt, int a, int b, int ab){
    
    if(DiscretePhenoValueInequality.ENCODING_TO_INEQUALITY == null){
      createInequalities();
    }
    
    String encoding = 
      Integer.toString(wt) + 
      Integer.toString(a) + 
      Integer.toString(b) + 
      Integer.toString(ab);

    DiscretePhenoValueInequality phenoSet = 
      (DiscretePhenoValueInequality)DiscretePhenoValueInequality.ENCODING_TO_INEQUALITY.get(encoding);
    
    if(phenoSet != null){
      return phenoSet;
    }
    
    // In theory we should not get here, since all possibilities are represented!
    throw new IllegalStateException("No DiscretePhenoValueInequality for values " +
                                    "WT = " + wt +
                                    "A = " + a +
                                    "B = " + b +
                                    "AB = " + ab);
    
  }//getPhenoInequality

  /**
   * @return an array of 75 DiscretePhenoValueInequality representing all the possible
   * phenotype inequalities, the first 44 are reduced inequalities, the rest
   * are repeats that can be the same instances of some of the 1st 44 when
   * the A and B phenotypes are switched
   */
  public static DiscretePhenoValueInequality [] getInequalitiesSet (){
    if(DiscretePhenoValueInequality.P_INEQUALITIES == null){
      createInequalities();
    }
    return DiscretePhenoValueInequality.P_INEQUALITIES;
  }//getInequalitiesReducedSet

  /**
   * @return a DiscretePhenoValueInequality that is equivalent to the given inequality
   * if the A and B tags for the individual genes were switched, or null, if the
   * inequality does not have an equivalent
   */
  public static DiscretePhenoValueInequality getEquivalentInequality (DiscretePhenoValueInequality ineq){
    int wt = ineq.getWT();
    int a = ineq.getA();
    int b = ineq.getB();
    int ab = ineq.getAB();
    if(a == b){
      // equivalent inequalities result when 'a' and 'b' are not equal
      return null;
    }
    return getPhenoInequality(wt,b,a,ab); // note that a and b are switched
  }//getEquivalentInequality
  
  /**
   * Creates 75 inequalities and stores them in 75 DiscretePhenoValueInequality objects that
   * are kept in DiscretePhenoValueInequality.P_INEQUALITIES, the first 44 are reduced 
   * inequalities, among the first 44, the first 26 (indices 0-25) are symmetric 
   * (should not have an edge direction), among the rest of the 75, the first 8 are 
   * symmetric (indices 44-51)
   */
  private static void createInequalities (){
    // not directional = nd
    int [][] inequalities = {
      // Reduced:
      {0,0,0,0}, //1 nd
      {0,0,1,1}, //2 nd
      {1,0,1,0}, //3 nd
      {1,1,1,0}, //4 nd
      {0,0,0,1}, //5 nd
      {0,1,1,1}, //6 nd
      {1,0,0,0}, //7 nd
      {0,1,1,0}, //8 nd
      {1,0,0,1}, //9 nd
      {0,1,2,0}, //10 nd
      {1,0,0,2}, //11 nd
      {2,0,0,1}, //12 nd
      {0,2,2,1}, //13 nd
      {1,2,2,0}, //14 nd
      {2,0,1,2}, //15 nd
      {0,2,3,1}, //16 nd
      {1,2,3,0}, //17 nd
      {2,0,1,3}, //18 nd
      {3,0,1,2}, //19 nd
      {0,1,1,2}, //20 nd
      {2,1,1,0}, //21 nd
      {1,0,2,1}, //22 nd
      {0,1,2,3}, //23 nd
      {1,0,3,2}, //24 nd
      {3,1,2,0}, //25 nd
      {2,0,3,1}, //26 nd
      {0,1,3,2}, //27 
      {1,0,2,3}, //28
      {2,1,3,0}, //29
      {3,0,2,1}, //30
      {2,0,1,0}, //31
      {0,1,2,2}, //32
      {1,0,2,0}, //33
      {1,0,2,2}, //34
      {0,1,2,1}, //35
      {2,0,1,1}, //36
      {0,0,1,2}, //37
      {0,0,2,1}, //38
      {2,1,2,0}, //39
      {2,0,2,1}, //40
      {1,0,1,2}, //41
      {1,1,2,0}, //42
      {1,0,1,1}, //43
      {0,0,1,0}, //44
      // Repeats:
      {0,1,0,1}, //45 nd
      {1,1,0,0}, //46 nd
      {0,2,1,0}, //47 nd
      {2,1,0,2}, //48 nd
      {0,3,2,1}, //49 nd
      {1,3,2,0}, //50 nd
      {2,1,0,3}, //51 nd
      {3,1,0,2}, //52 nd
      {2,3,1,0}, //53
      {3,2,0,1}, //54
      {0,3,1,2}, //55
      {1,2,0,3}, //56
      {2,1,0,0}, //57 
      {0,2,1,2}, //58
      {1,2,0,0}, //59
      {1,2,0,2}, //60
      {0,2,1,1}, //61
      {2,1,0,1}, //62
      {0,1,0,2}, //63
      {0,2,0,1}, //64
      {2,2,0,1}, //65
      {2,2,1,0}, //66
      {1,2,1,0}, //67
      {1,1,0,2}, //68
      {1,2,0,1}, //69
      {0,2,1,3}, //70
      {1,3,0,2}, //71
      {2,3,0,1}, //72
      {3,2,1,0}, //73
      {1,1,0,1}, //74
      {0,1,0,0}  //75
    };
    
    DiscretePhenoValueInequality.P_INEQUALITIES = new DiscretePhenoValueInequality[75];
    DiscretePhenoValueInequality.ENCODING_TO_INEQUALITY = new HashMap();
    for(int i = 0; i < inequalities.length; i++){
      int levels = Utilities.levelCount(inequalities[i]);
      DiscretePhenoValueInequality phenoValue = new DiscretePhenoValueInequality();
      phenoValue.setBase(levels);
      phenoValue.setValues(inequalities[i]);
      String encoding = getEncodingAsString(phenoValue);
      DiscretePhenoValueInequality.P_INEQUALITIES[i] = phenoValue;
      DiscretePhenoValueInequality.ENCODING_TO_INEQUALITY.put(encoding, phenoValue);
    }// for i

  }//createInequalities
  
  // --------------------- VISUAL METHODS -----------------------------//
  //(iliana)  Move this to a different class in 2.0 to separate view from model!
  
  public Color getColor (){
    return this.color;
  }//getColor
  
  public void setColor (Color new_color){
    this.color = new_color;
    PGVisualStyle.setColorMapping(EDGE_ATTRIBUTE,// the controlling attribute
                                  this.toString(),// value of attribute
                                  this.color);
  }//setColor
  
  public String getEdgeType (){
    return this.edgeType;
  }//getEdgeType
  
  public void setEdgeType (String new_type){ 
    this.edgeType = new_type;
    PGVisualStyle.setEdgeTypeMapping(EDGE_ATTRIBUTE,// the controlling attribute
                                     this.toString(),// value of attribute
                                     this.edgeType);
  }//setEdgeType
  
  public String getDirection (){
    return this.direction;
  }//getDirection
  
  public void setDirection (String new_dir){ 
    this.direction = new_dir;
    PGVisualStyle.setArrowMapping(EDGE_ATTRIBUTE, // controlling attribute
                                  this.toString(), // value of attribute
                                  this.direction);
  }//setDirection

}// class DiscretePhenoValueInequality
