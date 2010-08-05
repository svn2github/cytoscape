/*
 File: WordPair.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.csplugins.semanticsummary;

/**
 * A WordPair object contains information about adjacent pairs of words that
 * appear in a selected node.
 * @author Layla Oesper
 * @version 1.0
 */

public class WordPair implements Comparable<WordPair>
{
	//VARIABLES
	private String firstWord;
	private String secondWord;
	private Double probability;
	private CloudParameters params;
	private static final char controlChar = '\u001F';
	
	//CONSTRUCTOR
	/**
	 * Creates a fresh instance of a WordPair object for the specified words
	 * and CloudParameters.
	 */
	public WordPair(String aWord, String nextWord, CloudParameters cloudParams)
	{
		firstWord = aWord;
		secondWord = nextWord;
		params = cloudParams;
		probability = 0.0;
	}
	
	//METHODS
	
	/**
	 * Calculates the probability for the given WordPair.
	 */
	public void calculateProbability()
	{
		/**
		 * For two words, A and B, we are calculating the following:
		 * (P(B|A)P(A))/(P(A)P(B)).  To simplify this statement in terms of counts
		 * we have: ((#(A,B)/#A)(#A/#Total))/((#A/#Total)(#B/#Total))
		 * Mathematically we can simplify this to be the following expression, 
		 * which is what we actually calculate:
		 * (#(A,B)* #Total)/ (#A * #B)
		 */
		Integer total = params.getSelectedNumNodes();
		Integer firstCount = params.getSelectedCounts().get(firstWord);
		Integer secondCount = params.getSelectedCounts().get(secondWord);
		String pairName = firstWord + controlChar + secondWord;
		Integer pairCount = params.getSelectedPairCounts().get(pairName);
		
		Integer numerator = pairCount * total;
		Double doubleNumerator = numerator.doubleValue();
		Integer denominator = firstCount * secondCount;
		Double doubleDenom = denominator.doubleValue();
		
		probability = doubleNumerator/doubleDenom;
	}
	
	public int compareTo(WordPair second) 
	{
		Double firstProb = this.probability;
		Double secondProb = second.probability;
		
		if (firstProb < secondProb)
			return -1;
		else if (firstProb > secondProb)
			return 1;
		else //They are the same - so now compare with ratios
		{
			//Assumes that Ratios have been calculated
			String firstWordPair = this.getWordPairing();
			String secondWordPair = second.getWordPairing();
			
			Double firstRatio = this.getCloudParameters().getPairRatios().get(firstWordPair);
			Double secondRatio = second.getCloudParameters().getPairRatios().get(secondWordPair);
			
			if (firstRatio < secondRatio)
				return -1;
			else if (firstRatio > secondRatio)
				return 1;
			else
				//Third level of tie break - alphabetical of words
			{
				return firstWordPair.compareTo(secondWordPair);
			}
		}//end probability else
	}//end compareTo
	
	//Getters and Setters
	public void setFirstWord(String aWord)
	{
		firstWord = aWord;
	}
	
	public String getFirstWord()
	{
		return firstWord;
	}
	
	public void setSecondWord(String aWord)
	{
		secondWord = aWord;
	}
	
	public String getSecondWord()
	{
		return secondWord;
	}
	
	public void setCloudParameters(CloudParameters aParam)
	{
		params = aParam;
	}
	
	public CloudParameters getCloudParameters()
	{
		return params;
	}
	
	public Double getProbability()
	{
		return probability;
	}
	
	public String getWordPairing()
	{
		return firstWord + controlChar + secondWord;
	}

}
