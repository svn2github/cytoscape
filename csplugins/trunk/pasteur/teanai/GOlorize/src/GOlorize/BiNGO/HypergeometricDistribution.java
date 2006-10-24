package GOlorize.BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class that calculates the Hypergeometric probability P(x or more |X,N,n) for given x, X, n, N.    
 **/

/** Modified by Olivier Garcia (23/10/2006) :
 *  Changes : calculateHypergDistr() was modified by little optimization.
 */


import java.math.BigInteger;
import java.math.BigDecimal;


/********************************************************************
 * HypergeometricDistribution.java    Steven Maere & Karel Heymans (c) March 2005
 * -------------------------------
 *
 * Class that calculates the Hypergeometric probability P(x or more |X,N,n) for given x, X, n, N.
 ********************************************************************/


public class HypergeometricDistribution  {
    
    
    
    
    /*--------------------------------------------------------------
      FIELDS.
      --------------------------------------------------------------*/

    // x out of X genes in cluster A belong to GO category B which
    // is shared by n out of N genes in the reference set.
    /** number of successes in sample. */
    private static int x;
    /** sample size. */
    private static int bigX;
    /** number of successes in population. */
    private static int n;
    /** population size. */
    private static int bigN;
    /** scale of result. */
    private static final int SCALE_RESULT = 100; 
    
    
    
    
    
    /*--------------------------------------------------------------
      CONSTRUCTOR.
      --------------------------------------------------------------*/

    /**
     * constructor with as arguments strings containing numbers.
     * 
     * @param x number of genes with GO category B in cluster A.
     * @param bigX number of genes in cluster A.
     * @param n number of genes with GO category B in the whole genome.
     * @param bigN number of genes in whole genome.
     */
			
    public HypergeometricDistribution (int x, int bigX, int n, int bigN){
			this.x = x ;
			this.bigX = bigX ;
			this.n = n ;
			this.bigN = bigN ;
    }
    
    
    
    
    
    /*--------------------------------------------------------------
      METHODS.
      --------------------------------------------------------------*/
		/**
     * method that conducts the calculations.
     * P(x or more |X,N,n) = 1 - sum{[C(n,i)*C(N-n, X-i)] / C(N,X)} 
     * for i=0 ... x-1
     *
     * @return String with result of calculations.
     */
    public String calculateHypergDistr(){
	
			BigDecimal sum = new BigDecimal("0");
		
			BinomialCoefficients bi = new BinomialCoefficients();
                        BigInteger c_n_i;
                        BigInteger c_Nminn_Xmini;
                        BigInteger upperPartFraction;
                        BigInteger[] test=new BigInteger[2];
                        
                        int bignMinusSmallnMinusBign = bigN-n-bigX;
                        int coeff1;
                        int coeff2;
                        
			//int i = x-1 ;
                        
                        
                        /* 
			while((bigN-n >= bigX-i)&&(i >= 0)){
		  	  		BigInteger c_n_i = bi.calculateBinomialCoefficients(n, i);
		    		BigInteger c_Nminn_Xmini = bi.calculateBinomialCoefficients(bigN-n, bigX-i);    
		   			BigInteger upperPartFraction = c_n_i.multiply(c_Nminn_Xmini);
		    		sum = sum.add(new BigDecimal(upperPartFraction));
					i-- ;
			}   
                         //Substitued because, for H(bigN,smallN,bigX) :
                         //    P(X=i) == P(X=i+1) . (i+1)(bigN-smallN-bigx+i+1)/( (smallN-i)(bigX-i) )
                         
                         **/
                        if ((bigN-n >= bigX-(x-1))&&(x-1 >= 0)){
                            c_n_i = bi.calculateBinomialCoefficients(n, x-1);
                            c_Nminn_Xmini = bi.calculateBinomialCoefficients(bigN-n, bigX-(x-1));    
                            upperPartFraction = c_n_i.multiply(c_Nminn_Xmini);
                            sum = sum.add(new BigDecimal(upperPartFraction));
                            int i = x-2;
                                while((bigN-n >= bigX-i)&&(i >= 0)){
                                
                                    coeff1 = (i+1) * (bignMinusSmallnMinusBign + i + 1);
                                    coeff2 = (n - i) * (bigX-i) ;
                                    upperPartFraction = upperPartFraction.multiply(new BigInteger(Integer.toString(coeff1)));
                                    //upperPartFraction = upperPartFraction.divide(new BigInteger(Integer.toString(coeff2)));
                                    test = upperPartFraction.divideAndRemainder(new BigInteger(Integer.toString(coeff2)));
                                    upperPartFraction = test[0];
                                    if (test[1].intValue()!=0) {
                                        System.out.println("ca couille ya un reste a la division "+test[1]);
                                        i=0;
                                    }
                                    sum = sum.add(new BigDecimal(upperPartFraction));
                                    i--;
                                
                                    
                                }

                        }
                        
                        
		
	  		BigInteger c_N_X = bi.calculateBinomialCoefficients(bigN, bigX);
													  
			sum = sum.divide(new BigDecimal(c_N_X), SCALE_RESULT, BigDecimal.ROUND_HALF_UP);
			sum = sum.negate();		
			BigDecimal result = sum.add(new BigDecimal("1"));
		
			return result.toString();
    }
}
		
	    
	
	   
