// Utilites.java: Miscellaneous utility functions
//---------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------------
package phenotypeGenetics;
import java.util.*;
import java.io.*;

/**
 * Miscellaneous utilities
 */
public class Utilities {

  /**
   * The index of a <code>String</code> in array of <code>String</code>s.  
   *
   * @param sought The string to be searched for  
   * @param target The array of strings
   * 
   * @return The index 
   * @exception IllegalArgumentException If string is not a member of the array.
   */
  public static int stringArrayIndex ( String sought, String[] target ) 
    throws IllegalArgumentException { 

    int returnIndex=-1;
    boolean found=false;
  
    for( int i=0 ; i<target.length ; i++ ){
      if ( target[i].equals(sought) ){
        found=true;
        returnIndex=i;
      }
    }
    if (!found)throw new IllegalArgumentException ("Error: "+sought + " not found in array.");
    
    return returnIndex;

  }

  /**
   * Maps an array of integers onto an array of increasing levels (0,1,...,nlevels). 
   * A level corresponds to 
   * the value of one or more integers in the original array.
   *
   * @param nLevels The number of levels 
   * @param intArray The array to be mapped
   * @exception IllegalArgumentException If number of levels in intArray is 
   * inconsistent with nLevels.
   * @return The mapped array 
   */
  public static int[] bin ( int nLevels, int [] intArray ) throws IllegalArgumentException {
	
    int n = intArray.length;
    int[] binnedArray = new int[n];
    int minval = minimum(intArray);
    int maxval = maximum(intArray);
    int current = minval; 
    boolean[] isFound = new boolean[n]; 

    int [] drops = new int[n]; // array of yet-to-be-mapped array values
    for ( int i=0 ; i<n ; i++ ){
      drops[i]=intArray[i]; // initialize yet-to-be-mapped values with intArray
      isFound[i]=false; 
    }
    
    for ( int ilevel=0 ; ilevel<nLevels ; ilevel++ ){// for increasing levels
	
      for (int j=0;j<n;j++){//sweep through and identify values corresponding to those levels
        if (intArray[j]==current){
          binnedArray[j]=ilevel;
          if ( isFound[j] ) 
            System.out.println ("Error: value "+ intArray[j]+" was already found");
          isFound[j]=true; 
        }
      }
	
      if ( current != maxval ){
        drops=remove(current,drops); // find next level by removing the values just found
        current=minimum(drops);
      }
	
    }

    for ( int i=0 ; i<n ; i++ ){
      if ( !isFound[i] ) 
        throw new IllegalArgumentException ("Error: value "+ intArray[i]+" was not found");
    }
    if ( current != maxval ) 
      throw new IllegalArgumentException ("Error: Ended at incorrect level");
    
    return binnedArray; 
  }
  
  /**
   * Find the number of distinct levels in an integer array 
   *
   * @param intArray The array 
   *
   * @exception IllegalArgumentException If number of levels in intArray 
   * is greater than 1000000
   * @return The number of levels 
   */
  public static int levelCount ( int [] intArray ){

    int nLevels=0; 
    int minval = minimum(intArray);
    int maxval = maximum(intArray);
    int current = minval; 
    
    int [] drops = new int[intArray.length];// array of yet-to-be-found array values
    for ( int i=0 ; i<intArray.length ; i++ )drops[i]=intArray[i]; 

    for ( int i=0 ; current!=maxval && i<1000000 ; i++ ){ 
      drops = remove(current, drops);// find next level by removing the values just found
      current = minimum(drops);
      nLevels++;

    }
    nLevels++; 

    if ( current != maxval ) 
      throw new IllegalArgumentException ("Error: Number of levels exceeds 1000000"); 

    return nLevels; 

  }
  /**
   *  Integer array represented as a string
   */
  public static String stringRep ( int []intArray ){

    StringBuffer sb = new StringBuffer();
    for (int i=0 ; i<(intArray.length-1) ; i++) sb.append(intArray[i]+" ");
    sb.append(intArray[intArray.length-1]);
    return sb.toString();
  }
  
  /**
   * Remove values from array if they equal a given value 
   *
   * @param dropOut The value to be removed from the array 
   * @param intArray The integer array  
   *
   * @return an array equal to intArray, but with all instances of dropOut removed
   */
  public static int[] remove ( int dropOut , int [] intArray ){
 
    int ndrop=0;
    for ( int i=0 ; i<intArray.length ; i++){
      if ( intArray[i] == dropOut ) ndrop++; 
    }
    int[] returnArray = new int[intArray.length-ndrop]; 
    int index=0;
    for ( int i=0 ; i<intArray.length ; i++){
      if ( intArray[i] != dropOut ){
        returnArray[index]=intArray[i];
        index++;
      }
    }
    return returnArray ; 
    
  }
  /**
   *  Returns the minumum value of integer array
   */
  public static int minimum( int [] intArray ){
    
    int returnVal = -10000; // dummy value should never be returned
    if ( intArray.length == 0 ) System.out.println("Error: Array has length 0");
    returnVal = intArray[0]; 
    for ( int i=1 ; i<intArray.length ; i++) 
      returnVal = Math.min( returnVal, intArray[i]);
    
    return returnVal ; 
  }
  /**
   *  Returns the maximum value of integer array
   */
  public static int maximum( int [] intArray ){
    
    int returnVal = -10000; // dummy value should never be returned
    if ( intArray.length == 0 ) System.out.println("Error: Array has length 0");
    returnVal = intArray[0]; 
    for ( int i=1 ; i<intArray.length ; i++) returnVal = Math.max( returnVal, intArray[i]);
    
    return returnVal ; 
    
  }

  //public static int [] discretize (double[] x, double[] e) {
  //  double val = min(x);
  
  /**
   * Discretize double valued array, with errors, using overlap. Overlap dictates equality.
   * 
   * @param x Array of values
   * @param e Array of error corresponding to each value
   * @return Array where each original value is mapped onto an array of increasing 
   * levels (0,1,...,nLevels). nLevels is the number of non-overlapping regions
   * @exception IllegalArgumentException If arrays differ in length.
   * @author arives@systemsbiology.org
   */
  public static int [] discretize (double [] x, double [] e) throws IllegalArgumentException {

    int n = x.length; 
    if ( e.length != n ) {
      throw new IllegalArgumentException ("Error: values and error differ in number");
    }
    
    // Go through the input array x, at each iteration finding the minimum value
    // if the minimum is in a contig with the previous minimum then assign it the
    // discrete value of the previous, otherwise increment its discrete value.

    double[] nx = (double[])x.clone();
    double[] ne = (double[])e.clone();

    double val, err;
    int disc;

    // Keep track of the minimum, error and discrete value of the last
    // iteration. Set intitial values so that the first minimum found
    // will be assigned a discrete value of 0.
    double oldval = Double.NEGATIVE_INFINITY;
    double olderr = 0;
    int olddisc = -1;

    int[] returnval = new int[nx.length];

    for (int j=0; j < nx.length; j++) {

      // Find minimum
      double min = nx[0]-ne[0]; 
      int index = 0;
      for (int i=1;i<nx.length;i++) {
        if ( (nx[i]-ne[i]) < min ) {
          min = nx[i]-ne[i];
          index = i;
        }
      }

      // store val and error
      val = nx[index];
      err = ne[index];

      // identify a contig

      /* Display for debugging if needed 
         System.out.println("Considering index " + index ); 
         double vme = val-err; 
         double opo = oldval+olderr;
         System.out.println("val-err: "+ vme);
         System.out.println("oldval+olderr: " + opo);
      */

      if ((val - err) <= (oldval + olderr)) {
        // we have a contig, therefore assign this index
        // the same discretized val as previous
        disc = olddisc;
        //System.out.println("in a contig"); 
      } else {
        // no contig assign a new discrete val
        disc = olddisc+1;
        //System.out.println("not in a contig");
      }

      // overwrite this index so that it will not be considered
      // as minimum again, store the value, error, and discrete val
      // for the next iteration.
      nx[index] = Double.POSITIVE_INFINITY;
      ne[index] = 0;

      
      if ( val+err > oldval+olderr){ 
        oldval = val;
        olderr = err;
        olddisc = disc;
      }// we will keep the old val,err, disc if this condition is not met 

      // save the discrete value for this index
      returnval[index] = disc;

    }
    
    return(returnval);
  }
  
  /**
   * Old Discretize: discretize double valued array, with errors, using overlap. 
   * Overlap dictates equality.
   * 
   * @param x Array of values
   * @param e Array of error corresponding to each value
   *
   * @return Array where each original value is mapped onto an array of increasing 
   * levels (0,1,...,nLevels). nLevels is the number of non-overlapping regions
   * @exception IllegalArgumentException If arrays differ in length.  
   */

  public static int [] oldDiscretize ( double [] x, double [] e ) 
    throws IllegalArgumentException {
	
    int n = x.length; 
    if ( e.length != n ) 
      throw new IllegalArgumentException ("Error: values and error differ in number");
    
    // Adding tiny amount of jitter cures error in leftmost tokens calculation
    // for exact equalities without adding a lot of new code
    double epsilon = 1e-12;
    for (int i=0 ; i<n ; i++ ){
      x[i] += epsilon * ( Math.random()-0.5);
    }

    double [] xPlusE = new double [n];
    double [] xMinusE = new double [n];
    
    for (int i=0 ; i<n ; i++ ){
      xPlusE[i] = x[i] + e[i];
      xMinusE[i] = x[i] - e[i];
    }
    
    // Find leftmost tokens
    int [] lefties = new int [n];// the lefties, at most n
    int nlefties = 0 ; 
    for (int i=0 ; i<n ; i++ ){
	
      double xme = xMinusE[i];
      // Walk through others, and for each, see if it overlaps
      boolean foundOverlap = false ;
	
      for( int j=0 ; j<i; j++ ){
        double yme = xMinusE[j];
        double ype = xPlusE[j]; 
        if ( yme < xme && ype >= xme ) foundOverlap = true ; 
      }
      for( int j=i+1 ; j<n; j++ ){
        double yme = xMinusE[j];
        double ype = xPlusE[j]; 
        if ( yme < xme && ype >= xme ) foundOverlap = true ; 
      }
	
      if ( foundOverlap == false ){
        lefties[nlefties] = i ; 
        nlefties++;
        //System.out.println("Number " +i + " is left-most in a group  ");
      }
    }
    
    //Sort the lefties from lowest to highest
    int [] leftiesIncreasing = new int [nlefties]; // the new index array 
    Double [] xlefts = new Double [nlefties];
    for ( int i=0 ; i<nlefties ; i++ ) xlefts[i] = new Double ( xMinusE[lefties[i]] ); 
    Vector lvec = new Vector(); 
    for ( int i=0 ; i<nlefties ; i++ ) lvec.add( xlefts[i] ); 
    Collections.sort(lvec); 
    for ( int i=0 ; i<nlefties ; i++ ){
      leftiesIncreasing[i] = lefties[lvec.indexOf(xlefts[i])];  
    }
    
    // Initialization for next step
    int [] groups = new int [n];// the array containing final groups
    boolean [] found = new boolean [n]; // has each element been located?
    for ( int i=0 ; i<n ; i++ ) found[i] = false; 
    for ( int i=0 ; i<nlefties ; i++ ){
      groups[ leftiesIncreasing[i] ] = i;
      found[ leftiesIncreasing[i] ] = true;
    } 
    
    // for each left token, find its right partners 
    for ( int i=0 ; i<nlefties ; i++ ){
      boolean keepLooking = true ; 
      int currentFocus = leftiesIncreasing[i];
      //System.out.println("Looking at token number " + leftiesIncreasing[i] );
      for ( int k=0 ; ( k<n &&  keepLooking==true); k++ ){
        // the loop over potential right partners

        int iRight = findRightPartner (currentFocus, xMinusE, xPlusE, found);

        if ( iRight == -1 ){
          keepLooking = false ; 
          //System.out.println("There is no token to the right of " + currentFocus );
        } else {
          found[iRight] = true; // iRight has now been found
          groups[iRight] = i; // iRight belongs to the group i 
          //System.out.println("To the right of " + currentFocus +  " is " + iRight );
          if ( xPlusE[iRight] >= xPlusE[currentFocus]  ){ 
            // Shift focus to next token if its right boundary is greater
            currentFocus = iRight; 
          }
          keepLooking = true ; // keep looking for potential right partners
        } 
      }

    }
    
    for (int i=0 ; i<n ; i++ ){ 
      // Alert for elements not found. (Not really an IllegalArgumentException!)
      if ( found[i] == false ) 
        throw new IllegalArgumentException ("Error: element "+i+" not found");
    }
    
    return ( groups ); 
  }// discretize
  
  /**
   * Find the element to the right. This method is used only by discretize.
   * 
   * @param index the index of current focus
   * @param xLeftBound the left boundaries
   * @param xRightBound the right boundaries
   * @param found flags indicating if element has been found already
   * @return -1 if no index found, else the index of the right partner 
   */    
  private static int findRightPartner ( int index, 
                                        double [] xLeftBound, 
                                        double [] xRightBound , 
                                        boolean [] found ){
	
	
    double xClosest = 1e10 ;
    String rightPartner = null ; 
    int indexRightPartner = -1 ; 
    double xl = xLeftBound[index]; 
    double xr = xRightBound[index];

    // Walk through all elements excluding the current focus
    // Find the "closest" element, from the perspective of the left boundary

    // In some cases, the closest may have been found in an earlier call to this function
    // Use the found array to skip this and take the next closest

    for ( int i=0 ; i<index ; i++ ){
      double xLeft = xLeftBound[i] ;
      double xRight = xRightBound[i];
      double xShift = xLeft - xl ; 
      if ( (xShift>0) && (xLeft<xClosest) && (xLeft < xr) && !found[i] ){
        xClosest = xLeft ; 
        indexRightPartner = i;
      }
    }
    
    for ( int i=index+1  ; i<xLeftBound.length ; i++ ){
      double xLeft = xLeftBound[i] ;
      double xRight = xRightBound[i];
      double xShift = xLeft - xl ; 
      if ( (xShift>0) && (xLeft<xClosest) && (xLeft < xr) && !found[i] ){
        xClosest = xLeft ; 
        indexRightPartner = i;
      }
	
    }
    
    return indexRightPartner ; 
    
  } // findRightPartner

} // Utilities
