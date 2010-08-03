/*
 File: SingleWordCluster.java

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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The SingleWordCluster class contains information about a single set of 
 * clustered words for a CloudParameters object.  These objects can be 
 * sorted / compared based on the total size of the fonts that would
 * be used to represent them in a CloudParameters.
 * @author Layla Oesper
 * @version 1.0
 */

public class SingleWordCluster implements Comparable<SingleWordCluster>
{
	
	//VARIABLES
	private ArrayList<String> wordList;
	private Integer totalSum;
	private Integer numItems;
	private CloudParameters params;
	private boolean initialized;
	
	//CONSTRUCTOR
	
	/**
	 * Creates a fresh instance of the SingleWordCluster.
	 */
	public SingleWordCluster()
	{
		wordList = new ArrayList<String>();
		totalSum = 0;
		numItems = 0;
		params = new CloudParameters();
		initialized = false;
	}
	
	
	//METHODS
	
	/**
	 * Initializes the SingleWordCluster for the given CloudParameters.
	 * @param CloudParameters this SingleWordCluster is for.
	 */
	public void initialize(CloudParameters cloudParams)
	{
		params = cloudParams;
		initialized = true;
	}
	
	/**
	 * Adds an element to the WordList and updates the totalSum.
	 * @param String - word to add to the SingleWordCluster
	 */
	public void add(String aWord)
	{
		//Do nothing if not initialized
		if(!initialized)
			return;
		
		Integer fontSize = params.calculateFontSize(aWord);
		totalSum = totalSum + fontSize;
		numItems = numItems + 1;
		wordList.add(aWord);
	}
	
	/**
	 * Removes a word from the WordList and updates the totalSum.
	 * @param String - word to remove from the SingleWordCluster
	 * @return String - word that was removed from the list
	 */
	public String remove(String aWord)
	{
		if (!wordList.contains(aWord))
			return null;
		
		Integer fontSize = params.calculateFontSize(aWord);
		totalSum = totalSum - fontSize;
		numItems = numItems - 1;
		wordList.remove(aWord);
		
		return aWord;
	}
	
	/**
	 * Computes the value of sum/sqrt(N) for this SingleWordCluster.
	 * @return Double - the value of sum/sqrt(N)
	 */
	public Double computeRootMean()
	{
		//Return 0 if sum or num items is 0
		if (totalSum == 0 || numItems == 0)
		{
			return 0.0;
		}
		else
		{
			return totalSum/Math.pow(numItems,0.5);
		}
		
	}
	
	/**
	 * Calculates the largest value for font size in cluster.
	 */
	public Integer getLargestFont()
	{
		Integer largest = 0;
		for (Iterator<String> iter = wordList.iterator(); iter.hasNext();)
		{
			String curWord = iter.next();
			Integer curSize = params.calculateFontSize(curWord);
			if (largest < curSize)
			{
				largest = curSize;
			}
		}
		return largest;
	}
	
	/**
	 * Calculates a weighted sum for all words.
	 */
	public Double calculateWeightedSum()
	{
		Double sum = 0.0;
		Double k = 2.0;
		for (Iterator<String> iter = wordList.iterator(); iter.hasNext();)
		{
			String curWord = iter.next();
			Integer curSize = params.calculateFontSize(curWord);
			
			sum = sum + Math.pow(curSize, k);
		}
		//Take kth Root
		sum = Math.pow(sum, 1/k);
		
		return sum;
	}
	
	
	/**
	 * Compares two SingleWordClusters based on the totalSum of the font sizes,
	 * and then breaks ties based upon alphabetical sorting of the words
	 * in the list.
	 */
	/*
	public int compareTo(SingleWordCluster o) 
	{
		//Integer thisCount = this.getTotalSum();
		//Integer compareCount = o.getTotalSum();
		
		Double thisCount = this.computeRootMean();
		Double compareCount = o.computeRootMean();
		
		if (thisCount < compareCount)
			{return -1;}
		else if (thisCount > compareCount)
			{return 1;}
		else
		{
			//In case of ties, break alphabetically by first word
			String thisWord = this.getWordList().get(0);
			String compareWord = this.getWordList().get(0);
			
			return thisWord.compareTo(compareWord);
		}
	}
	*/
	/**
	 * Largest word first, then total, then alphabetical
	 */
	/*
	public int compareTo(SingleWordCluster o) 
	{
		Integer thisLargest = this.getLargestFont();
		Integer compareLargest = o.getLargestFont();
		
		if (thisLargest < compareLargest)
			{return -1;}
		else if (thisLargest > compareLargest)
			{return 1;}
		else
		{
			//In case of ties, break by total
			Integer thisCount = this.getTotalSum();
			Integer compareCount = o.getTotalSum();
			
			if (thisCount < compareCount)
				{return -1;}
			else if (thisCount > compareCount)
				{return 1;}
			else
			{
			
				//In case of ties, break alphabetically by first word
				String thisWord = this.getWordList().get(0);
				String compareWord = this.getWordList().get(0);
			
				return thisWord.compareTo(compareWord);
			}
		}
	}
	*/
	
	//Weighted sum
	public int compareTo(SingleWordCluster o) 
	{
		//Integer thisCount = this.getTotalSum();
		//Integer compareCount = o.getTotalSum();
		
		Double thisCount = this.calculateWeightedSum();
		Double compareCount = o.calculateWeightedSum();
		
		if (thisCount < compareCount)
			{return -1;}
		else if (thisCount > compareCount)
			{return 1;}
		else
		{
			//In case of ties, break alphabetically by first word
			String thisWord = this.getWordList().get(0);
			String compareWord = this.getWordList().get(0);
			
			return thisWord.compareTo(compareWord);
		}
	}
	
	//Getters and Setters
	
	public ArrayList<String> getWordList()
	{
		return wordList;
	}
	
	public Integer getTotalSum()
	{
		return totalSum;
	}
	
	public Integer getNumItems()
	{
		return numItems;
	}

}
