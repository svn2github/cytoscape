//----------------------------------------------------------
// $Revision : $
// $Date : $
// $Author : iliana <iavila@systemsbiology.org>
//----------------------------------------------------------
package annotations;

import java.math.BigDecimal;
import java.lang.IllegalArgumentException;
import java.lang.String;

/**
 * This class calculates cumulative Hypergeometric distances, used for p-value
 * calculations.
 */

public class HypDistanceCalculator {

  /**
   * The number of digits after the first non-zero digit in the decimal part of
   * a number. This is used for division calculations.
   */
  public static final int SCALE = 10;

  /**
   * The rounding strategy for the calculations. The options are described in
   * java.math.BigDecimal.
   */
  public static final int ROUNDING_STRATEGY = BigDecimal.ROUND_HALF_DOWN;
  
  /**
   * @return the hypergeometric distance for the given parameters
   * @param population the total population
   * @param population_true the population that is true
   * @param sample the sample size
   * @param sample_true the items in sample that are in population_true
   * @param fast whether the calculation should be done fast, at
   *             expense of some precision
   */
  public static double calculateHypDistance (
                                             int population,
                                             int population_true,
                                             int sample,
                                             int sample_true,
                                             boolean fast
                                             )
  {

  
    // Check input
    if(
       population_true > population ||
       sample > population ||
       sample_true > sample ||
       sample_true > population_true ||
       population < 0 ||
       population_true < 0 ||
       sample < 0 ||
       sample_true < 0)
      {
        throw new IllegalArgumentException("The arguments are incorrect:" +
                                           "\npopulation = " + population +
                                           "\npopulation_true = " + population_true +
                                           "\nsample = " + sample +
                                           "\nsample_true = " + sample_true);
      }
    
    // Calculate population choose sample
    BigDecimal pop_choose_sample = combinations(population, sample);
    
    // Identify start of probabilities to add (since this is a cumulative calculation)
    int start;
    if(population_true > sample){
      start = sample;
    }else{
      start = population_true;
    }
    
    if(fast){
  
      // This method takes advantage of the following:
      // C(n, k) / C(n,k-1) = ( n - (k-1) ) / k 
      
      BigDecimal pop_minus_popt = new BigDecimal(population - population_true);
      pop_minus_popt = pop_minus_popt.setScale(0);
      int s_i;
      BigDecimal pt_choose_i, ppt_choose_si;
      BigDecimal p;
      BigDecimal pCum = new BigDecimal(0);
      int maxScale = 0;
      BigDecimal x;

      ppt_choose_si = combinations(pop_minus_popt.intValue(), sample - start);
      pt_choose_i = combinations(population_true, start);
    
      for(int i  = start; i >= sample_true; i--){
      
        s_i = sample - i;
        
        if(i != start){
          
          BigDecimal temp = new BigDecimal(i);
          BigDecimal popTrue = new BigDecimal(population_true);
          BigDecimal temp2 = popTrue.subtract(temp);
          BigDecimal temp3 = new BigDecimal(i+1); 
          // This is where precision is lost, since x is not exact due to
          // division
          x = temp3.divide(temp2,
                           approximateScale(temp2, temp3),
                           ROUNDING_STRATEGY
                           );
          pt_choose_i = pt_choose_i.multiply(x);
          pt_choose_i = pt_choose_i.setScale(0,BigDecimal.ROUND_HALF_DOWN);
          
                   
          temp = new BigDecimal(s_i - 1);
          temp2 = new BigDecimal(s_i);
          temp3 = pop_minus_popt.subtract(temp);
          // This is where precision is lost due to division.
          x = temp3.divide(temp2,
                           SCALE,
                           ROUNDING_STRATEGY);
          ppt_choose_si = ppt_choose_si.multiply(x);
          ppt_choose_si = ppt_choose_si.setScale(0, BigDecimal.ROUND_HALF_DOWN);
          
        }// if i != start
        
        // NOTE: Getting the scale once, instead of each loop improves
        // running time.
        if(maxScale == 0){
          // The first time we get the  scale we get the largest one
          maxScale = approximateScale (pop_choose_sample, ppt_choose_si);
        }
        p =  pt_choose_i.multiply(  ppt_choose_si.divide(pop_choose_sample, 
                                                         maxScale,
                                                         ROUNDING_STRATEGY ));
        
        
        pCum = pCum.add(p);
        
      }
      // pCum will sometimes be slightly > 1 due to the fast method
      if(pCum.doubleValue() > 1){
        return 1;
      }
      return pCum.doubleValue();
      
    }else{
      // Slooooowwww method, but more precise
      int pop_minus_popt = population - population_true;
      int s_i;
      BigDecimal pt_choose_i, ppt_choose_si;
      BigDecimal p;
      BigDecimal pCum = new BigDecimal(0);
      int maxScale = 0;
      
      for(int i  = start; i >= sample_true; i--){
        
        s_i = sample - i;
        
        pt_choose_i = combinations(population_true, i);
        ppt_choose_si = combinations(pop_minus_popt,s_i);
        
        // NOTE: Getting the scale once, instead of each loop improves
        // running time.
        if(maxScale == 0){
          // The first time we get the  scale we get the largest one
          maxScale = approximateScale (pop_choose_sample, ppt_choose_si);
        }
        p =  pt_choose_i.multiply(  ppt_choose_si.divide(pop_choose_sample, 
                                                         maxScale,
                                                         ROUNDING_STRATEGY)  );
        
        pCum = pCum.add(p);
      }
      return pCum.doubleValue();
    }
  }//calculateHypDistance
  
  protected static int approximateScale (BigDecimal divisor, BigDecimal dividend){
    //TODO: Remove
    //System.out.println("divisor = " + divisor);
    //System.out.println("dividend = " + dividend);

    if(dividend.compareTo(divisor) == -1){
      // The dividend is smaller than the divisor
      //TODO: Remove
      //System.out.println("dividend < divisor");
      BigDecimal temp = dividend.movePointRight(1);
      int numRightMoves = 1; // the number of times the decimal point was moved right
      while(temp.compareTo(divisor) == -1){
        temp = temp.movePointRight(1);
        numRightMoves++;
      }//while the dividend is smaller than the divisor
      // Add one to the scale because the next digit after numRightMoves
      // won't be a zero
      numRightMoves++;
      //TODO: Remove
      //System.out.println("numRightMoves = " + numRightMoves);
      return numRightMoves + SCALE;
    }else{
      // Either they are equal or the dividend is larger than the divisor
      // System.out.println("------- Returning " + SCALE + " ----------");
      return SCALE;
    }
    
  }//approximateScale
                 
  /**
   * @return n choose k
   */
  public static BigDecimal combinations (int n, int k){
    
    // Check input
    if( n < 0 || k < 0 || n < k){
      throw new IllegalArgumentException("Incorrect input " + n + ", " + k + ".");
    }
    BigDecimal comb = new BigDecimal(1.0);
    BigDecimal operand;
    double d;
    
    //TODO: Remove
    //System.out.print("calculating " + n + " choose " + k + " ...");
    //System.out.flush();
    
    for(int i = 0; i < k; i++){
      d = ( (double)(n - i) ) / ( (double)(k - i) );
      operand = new BigDecimal(d);
      comb = comb.multiply(operand);
    }
    // Need to round up comb!!! This is probably due to inexactitude of double arithmetic
    BigDecimal result = comb.setScale(0, BigDecimal.ROUND_HALF_UP);
    //TODO: Remove
    //System.out.println(result);
    return result; 
  
  }//combinations
  
  //----- MAIN ------//
  public static void main (String [] args){
    long before = System.currentTimeMillis();
   
    int population = Integer.parseInt(args[0]);
    System.out.println("population = " + population);
    int population_true = Integer.parseInt(args[1]);
    System.out.println("population_true = " + population_true);
    int sample = Integer.parseInt(args[2]);
    System.out.println("sample = " + sample);
    int sample_true = Integer.parseInt(args[3]);
    System.out.println("sample_true = " + sample_true);
    
    double pVal = HypDistanceCalculator.calculateHypDistance(population,
                                                             population_true,
                                                             sample,
                                                             sample_true,
                                                             false);
    System.out.println("Time = " + ( (System.currentTimeMillis() - before)/1000) + " secs");
    System.out.println("P-Value = [" + pVal + "]");
    
  }// MAIN
  
}//HypDistanceCalculator
