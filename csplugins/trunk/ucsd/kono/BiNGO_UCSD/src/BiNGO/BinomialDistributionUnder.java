package BiNGO;

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
 * * Description: Class that calculates the value of 1 - Cumulative Binomial Distribution for given parameters.         
 **/


import java.math.BigDecimal;
import java.math.BigInteger;
import cern.jet.stat.*;

/**
 * *****************************************************************
 * BinomialDistribution.java     Steven Maere & Karel Heymans (c) March 2005
 * --------------------------
 * <p/>
 * Class that calculates the value of 1 - Cumulative Binomial Distribution for given parameters.
 * ******************************************************************
 */

public class BinomialDistributionUnder {

    /*--------------------------------------------------------------
    FIELDS.
    --------------------------------------------------------------*/

    // x out of X genes in a cluster A belong to GO category B which
    // is shared by n out of N genes in the reference set.
    /**
     * number of successes in sample.
     */
    private static int x;
    /**
     * sample size.
     */
    private static int bigX;
    /**
     * number of successes in population.
     */
    private static int n;
    /**
     * population size.
     */
    private static int bigN;
	/*chance of success*/
	private static double p;

    /**
     * scale of result.
     */
    private static final int SCALE_RESULT = 100;

    /*--------------------------------------------------------------
    CONSTRUCTOR.
    --------------------------------------------------------------*/

    /**
     * constructor with as arguments strings containing numbers.
     *
     * @param x    number of genes with GO category B in cluster A.
     * @param bigX number of genes in cluster A.
     * @param n    number of genes with GO category B in the whole genome.
     * @param bigN number of genes in whole genome.
     */
    public BinomialDistributionUnder(int x, int bigX,
                                     int n, int bigN) {
        this.x = x;
        this.bigX = bigX;
        this.n = n;
        this.bigN = bigN;
		this.p = n*1.0/bigN;
    }

    /*--------------------------------------------------------------
    METHODS.
    --------------------------------------------------------------*/

    /**
     * method that conducts the calculations.
     * p = 1 - sum{C(X,i)*(n/N)^i*(1-n/N)^(X-i)}
     * for i=0 ... x-1
     *
     * @return String with value of calculations.
     */

    public String calculateBinomialDistribution() {

       	double tmp = Probability.binomial(x, bigX, p);		
        BigDecimal sum = new BigDecimal(tmp);
        return sum.toString();

    }


    /**
     * Method that calulates a bigdecimal to a certain power (BigInteger).
     * e.g. 0.002^3.
     *
     * @param x   the BigDecimal
     * @param pow the power
     * @return BigDecimal the result
     */
    BigDecimal decimalPow(BigDecimal x, BigInteger pow) {

        if (pow.equals(new BigInteger("0"))) {
            return new BigDecimal("1");
        } else {
            BigDecimal product = x;

            for (BigInteger i = new BigInteger("1"); !i.equals(pow); i = i.add(new BigInteger("1"))) {
                product = product.multiply(x);
            }
            return product.setScale(SCALE_RESULT, BigDecimal.ROUND_HALF_UP);
        }
    }


}
		
	    
	
	   
