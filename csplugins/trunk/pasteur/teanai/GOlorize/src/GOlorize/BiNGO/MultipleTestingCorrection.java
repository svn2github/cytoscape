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
 * * Description: Class that redirects the calculation of the multiple testing correction 
 * * of raw p-values obtained through e.g. multiple hypergeometric tests.       
 **/

import java.util.*; 

/***************************************************************
 * MultipleTestingCorrection.java   Steven Maere & Karel Heymans (c) March 2005
 * ------------------------
 *
 * Class that redirects the calculation of the multiple testing correction 
 * of raw p-values obtained through e.g. multiple hypergeometric tests.  
 ***************************************************************/


public class MultipleTestingCorrection {

    
	/*--------------------------------------------------------------
      FIELDS.
      --------------------------------------------------------------*/
	/** significance level.*/
	private static String alpha;
	/** hashmap with the test results as values and the GO labels as keys.*/
	private static HashMap map;
	/** type of correction*/
	private static String type ;
	/** hashmap with the results (adjusted p-values) as values and the GO labels as keys.*/
	private static HashMap correctionMap;
	/** constant string for the Benjamini & Hochberg FDR correction.*/
	private final String BENJAMINI_HOCHBERG_FDR = "Benjamini & Hochberg False Discovery Rate (FDR) correction";
	/** constant string for the Bonferroni FWER correction. */
	private final String BONFERRONI = "Bonferroni Family-Wise Error Rate (FWER) correction";

 
    
	/*--------------------------------------------------------------
      CONSTRUCTOR.
      --------------------------------------------------------------*/

	public MultipleTestingCorrection (String alpha, HashMap map, String type){
		this.type = type ;
		this.alpha = alpha ;
		this.map = map ;
		this.correctionMap = null ;
	}
    
    
    
	/*--------------------------------------------------------------
		METHODS.
      --------------------------------------------------------------*/	
	
	/**
	 * method that redirects the calculation of the multiple testing correction.
	 */
	public void calculate(){
		
		HashSet goLabelsSet = new HashSet(map.keySet());

		Iterator iteratorGoLabelsSet = goLabelsSet.iterator();
		String [] pvalues = new String [map.size()];
		String [] goLabels = new String [map.size()];
		for(int i = 0; iteratorGoLabelsSet.hasNext(); i++){
			goLabels[i] = iteratorGoLabelsSet.next().toString();
			pvalues[i] = map.get(new Integer(goLabels[i])).toString();
		}
		
		String [] adjustedPvalues ;
		String [] sortedGOLabels ;
		
		if (type.equals(BONFERRONI)){
			Bonferroni bonferroni = new Bonferroni(pvalues, goLabels, alpha);
			TestCalculator tc = new TestCalculator(bonferroni) ;
			tc.run() ;
			adjustedPvalues = bonferroni.getAdjustedPvalues();
			sortedGOLabels = bonferroni.getOrdenedGOLabels();
			correctionMap = new HashMap();
			for (int i = 0; i < adjustedPvalues.length && i < sortedGOLabels.length; i++){
				correctionMap.put(sortedGOLabels[i], adjustedPvalues[i]);
			}
    	}	
		else if(type.equals(BENJAMINI_HOCHBERG_FDR)){
			BenjaminiHochbergFDR fdr = new BenjaminiHochbergFDR(pvalues, goLabels, alpha);
			TestCalculator tc = new TestCalculator(fdr) ;
			tc.run() ;
			adjustedPvalues = fdr.getAdjustedPvalues();
			sortedGOLabels = fdr.getOrdenedGOLabels();
			correctionMap = new HashMap();
			for (int i = 0; i < adjustedPvalues.length && i < sortedGOLabels.length; i++){
				correctionMap.put(sortedGOLabels[i], adjustedPvalues[i]);
			}
		}
		else{
			correctionMap = null ;
		}	
		
	}
   
    
	/*--------------------------------------------------------------
		GETTER.
      --------------------------------------------------------------*/	
	
	/** 
	 * getter for the map of corrected p-values.
	 *
	 * @return HashMap correctionMap.
	 */
	public HashMap getCorrectionMap(){
		return correctionMap;
	}
	
}

