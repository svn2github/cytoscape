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
		wordList.remove(aWord);
		
		return aWord;
	}
	
	
	/**
	 * Compares two SingleWordClusters based on the totalSum of the font sizes,
	 * and then breaks ties based upon alphabetical sorting of the words
	 * in the list.
	 */
	public int compareTo(SingleWordCluster o) 
	{
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
	
	//Getters and Setters
	
	public ArrayList<String> getWordList()
	{
		return wordList;
	}
	
	public Integer getTotalSum()
	{
		return totalSum;
	}

}
