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
// DiscretePhenoValueSet.java: A set of discete phenotype values
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
 */
public class DiscretePhenoValueSet implements Serializable {

  public final static String INTERACTION_NON_INTERACTING = "non-interacting";
    
  int pWT;
  int pA;
  int pB;
  int pAB;

  int base;

  final static public int BASE_UNASSIGNED = -1; // marks whether base has been specified 
  final static public int PHENOTYPEVALUE_UNASSIGNED = -1;
  final static public int nObs = 4;

  //-----------------------------------------------------------------------------------
  /**
   * Construct a new <code>DiscretePhenoValueSet</code>.
   */
  public DiscretePhenoValueSet ()
  {
    pWT = PHENOTYPEVALUE_UNASSIGNED; 
    pA  = PHENOTYPEVALUE_UNASSIGNED; 
    pB  = PHENOTYPEVALUE_UNASSIGNED; 
    pAB = PHENOTYPEVALUE_UNASSIGNED;
    base = BASE_UNASSIGNED;
  }
  //-----------------------------------------------------------------------------------
  /**
   * Test whether <code>DiscretePhenoValueSet</code> has no data. 
   */
  public boolean isEmpty ()
  {
    return ( pWT == PHENOTYPEVALUE_UNASSIGNED && 
             pA  == PHENOTYPEVALUE_UNASSIGNED &&
             pB  == PHENOTYPEVALUE_UNASSIGNED && 
             pAB == PHENOTYPEVALUE_UNASSIGNED &&
             base == BASE_UNASSIGNED);
  }
  //-----------------------------------------------------------------------------------
  /**
   * Set number of levels
   *
   * @param nLevels The number of levels
   * @exception IllegalArgumentException If nLevels is less than one.
   */
  public void setBase ( int nLevels ) throws IllegalArgumentException
  {

    if ( nLevels < 1 )
      throw new IllegalArgumentException ("Illegal number of levels"+ nLevels );

    base = nLevels; 

  }
  //-----------------------------------------------------------------------------------
  /**
   *
   * Set phenotype values
   *
   * @param values Integer array with encoded values
   * @exception IllegalArgumentException If: number of levels has not been specified. If integer array does not have four elements. If any value exceeds the number of levels. If an expected level is not present in values array.
   */
  public void setValues ( int [] values ) throws IllegalArgumentException
  {
    
    if ( base == BASE_UNASSIGNED )
      throw new IllegalArgumentException ("Number of levels has not been specified");

    if ( values.length != nObs )
      throw new IllegalArgumentException ("Illegal length of constructor argument: "+ values.length );

    // confirm that levels are legal
    for ( int i=0 ; i<nObs ; i++){
      int val = values[i];
      if ( val < PHENOTYPEVALUE_UNASSIGNED || val >= base )
        throw new IllegalArgumentException ("Illegal value: "+ val );
    }

    // confirm that each level is represented at least once 
    for ( int j=0 ; j<base ; j++){
      boolean foundOne = false;
      for ( int i=0 ; i<nObs ; i++) if ( values[i] == j ) foundOne = true;
      if ( foundOne == false )
        throw new IllegalArgumentException ("No level "+j);
    }
	
    pWT = values[0];
    pA  = values[1];
    pB  = values[2];
    pAB = values[3];

  }
  //------------------------------------------------------------------------------------
  /**
   * Get number of levels
   */
  public int getBase ()
  {
    return base;
  }
  //------------------------------------------------------------------------------------
  /**
   * Get encoded wild-type phenotype value  
   */
  public int getWT ()
  {
    return pWT;
  }
  //------------------------------------------------------------------------------------
  /**
   * Get encoded A mutant phenotype value  
   */
  public int getA ()
  {
    return pA;
  }
  //------------------------------------------------------------------------------------
  /**
   * Get encoded B mutant phenotype value  
   */
  public int getB ()
  {
    return pB;
  }
  //------------------------------------------------------------------------------------
  /**
   * Get encoded AB mutant phenotype value  
   */
  public int getAB ()
  {
    return pAB;
  }
  //------------------------------------------------------------------------------------
  /**
   * The equals method
   *
   * @param toCompare The <code>DiscretePhenoValueSet</code> to compare to 
   */
  public boolean equals ( DiscretePhenoValueSet toCompare )
  {
    if ( toCompare.getWT() == pWT &&
         toCompare.getA() == pA &&
         toCompare.getB() == pB &&
         toCompare.getAB() == pAB ) {
      return true; 
    } else {
      return false;
    }
  }


  //------------------------------------------------------------------------------------
  /**
   *
   * Test for monotonic dependence on the mutants. 
   * Non-monotonic behavior is defined as follows:<pre><br>
   * The A mutant shows opposite behavior (increase/decrease) <br>in the wild-type and B background<br>
   * OR<br>
   * The B mutant shows opposite behavior (increase/decrease) <br>in the wild-type and A background<br><br></pre>
   * Monotonicity is the absence of this behavior.
   *
   */
  public boolean isMonotonic (){

    boolean isNonMonotonic = ( ((pWT<pA)&&(pAB<pB)) ||
                               ((pA<pWT)&&(pB<pAB)) ||
                               ((pWT<pB)&&(pAB<pA)) ||
                               ((pB<pWT)&&(pA<pAB)) ); 
    return( !isNonMonotonic );

  }
  /**
   * Tests the big nasty boolean that tells whether the phenoset is additive
   * or not.
   */
  public boolean isAdditive (){

    boolean isAdditive = ( ((((pA<pAB)&&(pB<pAB)) && ((pA>pWT)&&(pB>pWT))) ||
                            (((pA>pAB)&&(pB>pAB)) && ((pA<pWT)&&(pB<pWT)))) ||
                           ((((pWT<pB)&&(pAB<pB)) && ((pWT>pA)&&(pAB>pA))) ||
                            (((pWT>pB)&&(pAB>pB)) && ((pWT<pA)&&(pAB<pA)))) );
    return( isAdditive );

  }
  //------------------------------------------------------------------------------------
  /**
   *
   * Test for double non-montonic behavior.
   * Double non-monotonic behavior is defined as follows:<pre><br>
   * The A mutant shows opposite behavior (increase/decrease) <br>in the wild-type and B background<br>
   * <b>AND</b><br>
   * The B mutant shows opposite behavior (increase/decrease) <br>in the wild-type and A background<br><br></pre>
   *
   */
  public boolean isDoubleNonMonotonic (){

    boolean isDoubleNonMonotonic = ( (((pWT<pA)&&(pAB<pB)) || ((pA<pWT)&&(pB<pAB))) &&
                                     (((pWT<pB)&&(pAB<pA)) || ((pB<pWT)&&(pA<pAB))) ); 
    return( isDoubleNonMonotonic );

  }
  //------------------------------------------------------------------------------------
  public String toString()
  { 
    StringBuffer sb = new StringBuffer ();

    Vector atLevel; 
    for ( int j=0 ; j<base ; j++ ){
	
      atLevel=new Vector();
      if ( pWT==j ) atLevel.add("pWT");
      if ( pA==j ) atLevel.add("pA");
      if ( pB==j ) atLevel.add("pB");
      if ( pAB==j ) atLevel.add("pAB");
      String [] atLevelArray = (String [])atLevel.toArray(new String [0]);
	
      if ( atLevelArray.length==1 ) 
        sb.append(atLevelArray[0]);
      if ( atLevelArray.length>1){
        for (int k=0 ; k<(atLevelArray.length-1) ; k++ ) sb.append(atLevelArray[k]+"=");
        sb.append(atLevelArray[atLevelArray.length-1]);
      }
      if (j!=(base-1)) sb.append("<");
    }
    return sb.toString ();
    
    /** The following representation may also be considered
        sb.append ("pWT=" +pWT+", ");
        sb.append ("pA=" +pA+", ");
        sb.append ("pB=" +pB+", ");
        sb.append ("pAB=" +pAB);
    **/


  }
  //------------------------------------------------------------------------------------
  /**
   * Classify into a set of pre-specified relations
   * Decision tree created by AR. (New Version)
   * pWT, pA, pB, pAB
   */
  public String classify() {
    String returnString = "";
    if (((pA==pWT)&&(pB==pAB))||((pB==pWT)&&(pA==pAB))) {
      returnString = INTERACTION_NON_INTERACTING;
    } else {
      if (this.isAdditive()) {
        returnString = "additive";
      } else {
        if (this.isDoubleNonMonotonic()) {
          returnString = "double-nonmonotonic";
        } else if ((pWT==pAB)&&((pA==pAB)||(pB==pAB))) {
          // note: the above || should be XOR but does not matter because of tree
          returnString = "suppression";
        } else if ((pA==pAB)&&(pB==pAB)) {
          returnString = "asynthetic";
        } else if ((pA==pWT)&&(pB==pWT)) {
          returnString = "synthetic";
        } else if ((((pA==pAB)&&(pB!=pAB))||((pB==pAB)&&(pA!=pAB)))&&(pWT!=pAB)) {
          returnString = "epistatic";
        } else if ((((pA==pWT)&&(pB!=pWT))||((pB==pWT)&&(pA!=pWT)))&&(pAB!=pWT)) {
          returnString = "conditional";
        } else {
          // single non-monotonics do not exaust the single non-monotonics
          // some conditionals and epistatics also have the non-monotonic
          // property
          returnString = "single-nonmonotonic";
        }
      }
    }
    if (returnString.equals("")) System.out.println("Error: classifier not found"); 
    return returnString; 
  }
  //------------------------------------------------------------------------------------
  /**
   * Classify into a set of pre-specified relations
   * Decision tree created by AR. This is the old version
   * where epistatic/condional monotonic and epistantic/conditional non-monotonic
   * are classified separately.
   * pWT, pA, pB, pAB
   */
  public String oldARClassify() {
    String returnString = "";
    if ((pA == pWT && pB == pAB) || (pB == pWT && pA == pAB)) {
      returnString = "non-interacting";
    } else {
      if (!this.isMonotonic()) {
        if (this.isDoubleNonMonotonic()) {
          // non-monotonic
          returnString = "double-nonmonotonic";
        } else {
          // non-flagrant non-monotonic
          if (pA == pAB || pB == pAB) {
            returnString = "nonmonotonic-epistatic";
          } else if (pA==pWT || pB==pWT) {
            returnString = "nonmonotonic-conditional";
          } else {
            returnString = "nonmonotonic";
          }
        } 
      } else {
        if (this.isAdditive()) {
          returnString = "additive";
        } else {
          // not additive
          if ((pWT == pAB)&&((pA==pAB)||(pB==pAB))) {
            // note: the above || should be XOR but does not matter because of tree
            returnString = "suppression";
          }
          if ((pA==pAB)&&(pB==pAB)) {
            returnString = "asynthetic";
          }
          if ((pA==pWT)&&(pB==pWT)) {
            returnString = "synthetic";
          }
          if ((((pA==pAB)&&(pB!=pAB))||((pB==pAB)&&(pA!=pAB)))&&(pWT!=pAB)) {
            returnString = "monotonic-epistatic";
          }
          if ((((pA==pWT)&&(pB!=pWT))||((pB==pWT)&&(pA!=pWT)))&&(pAB!=pWT)) {
            returnString = "monotonic-conditional";
          }
        
        }
      }
    }
    if (returnString.equals("")) System.out.println("Error: classifier not found"); 
    return returnString; 
  }
  
  //------------------------------------------------------------------------------------
  /**
   * Classify into a set of pre-specified relations
   * (this is the old version)
   */ 
  public String oldClassify(){


    String returnString = ""; 

    switch ( base ) {

    case 1: // One level

      returnString =  "non-interacting";
      break; 

    case 2: // Two levels
	
      if ( !isMonotonic() ){
        returnString =  "non-monotonic"; 
      } else {
        int [][] boolecombo = {
          {0,1,1,1},
          {1,1,1,0},	
          {1,0,0,0},
          {0,0,0,1}
        };
        int[][] boolerescue   = {
          {1,0,1,1},
          {1,1,0,1},
          {0,1,0,0},
          {0,0,1,0}
        };
        int[][] doublebubble = {
          {0,0,1,1},
          {0,1,0,1},
          {1,1,0,0},
          {1,0,1,0}
        }; 
	    
        DiscretePhenoValueSet t = new DiscretePhenoValueSet(); 
        t.setBase(2);
        boolean found = false; 
        for ( int i=0 ; i<boolecombo.length && !found; i++){
          t.setValues(boolecombo[i]);
          if ( equals(t) ) {
            returnString =  "boolecombo"; 
            found = true; 
          }
        };
        for ( int i=0 ; i<boolerescue.length && !found ; i++){
          t.setValues(boolerescue[i]);
          if ( equals(t) ) {
            returnString =  "boolerescue";
            found = true; 
          }
        };
        for ( int i=0 ; i<doublebubble.length && !found ; i++){
          t.setValues(doublebubble[i]);
          if ( equals(t) ) {
            returnString = "non-interacting"; 
            found = true; 
          }
        };
      };
      break; 
   
    case 3: // Three levels
	
      if ( !isMonotonic() ){
        returnString =  "non-monotonic"; 
      } else {
        int [][] hh = {
          {2,1,0,0},
          {2,0,1,0},
          {0,1,2,2},
          {0,2,1,2}
        };
	    
        int [][] linrep = {
          {1,2,0,0},
          {1,0,2,0},
          {1,2,0,2},
          {1,0,2,2}
        };
	    
        int [][] regulatory = {
          {0,0,1,2},
          {0,0,2,1},
          {0,1,0,2},
          {0,2,0,1},
          {2,2,0,1},
          {2,1,2,0},
          {2,0,2,1},
          {2,2,1,0}
        };
	    
        int [][] linearcombo = {
          {0,1,1,2},
          {2,1,1,0},
          {1,2,0,1},
          {1,0,2,1}
        };

        boolean found = false; 
        DiscretePhenoValueSet t = new DiscretePhenoValueSet(); 
        t.setBase(3); 
        for ( int i=0 ; i<hh.length && !found ; i++){
          t.setValues(hh[i]);
          if ( equals(t) ) {
            returnString =  "hh";
            found = true; 
          }
        };
        for ( int i=0; i<linrep.length && !found; i++){
          t.setValues(linrep[i]);
          if ( equals(t) ) {
            returnString =  "linrep";
            found=true;
          }
        };
        for ( int i=0; i<regulatory.length && !found ; i++){
          t.setValues(regulatory[i]);
          if ( equals(t) ) {
            returnString = "regulatory"; 
            found=true;
          }
        };
        for ( int i=0; i<linearcombo.length && !found; i++){
          t.setValues(linearcombo[i]);
          if ( equals(t) ) {
            returnString = "linearcombo";
            found=true;
          }
        };

      };// end search for monotonic class, three level
      break; 

    case 4: // Four levels 
	
      if ( !isMonotonic() ){
        returnString =  "non-monotonic"; 
      } else {
        returnString = "additive";
      };
      break; 
	
    }// end switch
    
    if ( returnString.equals("") )
      System.out.println("Error: classifier not found"); 
	
    return returnString; 
    
  }
  //------------------------------------------------------------------------------------
} // DiscretePhenoValueSet
