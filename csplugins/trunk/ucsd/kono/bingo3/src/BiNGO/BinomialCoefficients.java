package BiNGO;

/**
 * * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
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
 * * Description: Class that calculates the value of the combination: C(n,k).          
 **/

import java.math.BigDecimal;
import cern.jet.stat.*;

/**
 * ***************************************************************************
 * BinomialCoefficients.java:      Steven Maere & Karel Heymans (c) March 2005
 * ---------------------------
 * <p/>
 * Class that calculates the value of the combination: C(n,k).
 * ****************************************************************************
 */

public class BinomialCoefficients {

    /*--------------------------------------------------------------
    CONSTRUCTOR.
    --------------------------------------------------------------*/

    public BinomialCoefficients() {
    }

    /*--------------------------------------------------------------
    METHODS.
    --------------------------------------------------------------*/

    /**
     * Method that calculates the value of the combination: C(n,k).
     *
     * @param nInput the n from C(n,k).
     * @param kInput the k from C(n,k).
     * @return BigInteger the value of the combination: C(n,k).
     */
	
	public BigDecimal calculateBinomialCoefficients(int n, int k) {
		//gamma(k) = (k-1)! for integers 
		double tmp = Math.exp(Gamma.logGamma(n+1)-Gamma.logGamma(k+1)-Gamma.logGamma(n-k+1)) ;
		BigDecimal product = new BigDecimal(tmp);	
		return product ;
	}
	
	
    /*public BigInteger calculateBinomialCoefficients(int nInput, int kInput) {

        int n = nInput;
        int k = kInput;

        if (n == k - 1 || k == 1)
            return new BigInteger(n + "");
        else if (n == k || k == 0)
            return new BigInteger("1");
        else {

            if (k > n / 2)
                k = n - k;

            BigInteger product = new BigInteger((n - k + 1) + "");

            for (int i = 2; i <= k; i++) {
                product = product.multiply(new BigInteger((n - k + i) + ""));
                product = product.divide(new BigInteger(i + ""));
            }
            return product;
        }
    }*/

}
			