/*
 File: WordClusters.java

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

package cytoscape.csplugins.wordcloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * The WordClusters class contains information about the current clustering
 * state for a CloudParameters object.  When it is first initialized every
 * word that appears in the selected nodes for the CloudParameters has its
 * own cluster.  This class provides the ability to merge clusters based
 * upon a WordPair object.
 * @author Layla Oesper
 * @version 1.0
 */

public class WordClusters 
{
	//VARIABLES
	private ArrayList<SingleWordCluster> clusters;
	private CloudParameters params;
	boolean initialized;
	
	//CONSTRUCTORS
	
	/**
	 * Creates a fresh instance of a WordClusters object.
	 */
	public WordClusters()
	{
		clusters = new ArrayList<SingleWordCluster>();
		params = new CloudParameters();
		initialized = false;
	}
	
	//METHODS
	/**
	 * Initializes the WordClusters object for the supplied CloudParameters.
	 * @param CloudParameters that this WordClusters represents.
	 */
	public void initialize(CloudParameters cloudParams)
	{
		params = cloudParams;
		
		//Initialize params if necessary
		if (!params.getSelInitialized())
			params.updateSelectedCounts();
		
		//Get list of words
		Set<String> words = params.getSelectedCounts().keySet();
		
		//Initialize as singletons
		clusters = new ArrayList<SingleWordCluster>();
		for (Iterator<String> iter = words.iterator(); iter.hasNext();)
		{
			//Create a list for each word and add to main list
			String curWord = iter.next();
			SingleWordCluster curList = new SingleWordCluster();
			curList.initialize(cloudParams);
			curList.add(curWord);
			clusters.add(curList);
		}
		initialized = true;
	}
	
	/**
	 * Joins together the two clusters implicated by the WordPair object
	 * parameter supplied to the the method.
	 * @param WordPair that defines which clusters to join.
	 */
	public void combineClusters(WordPair aPair)
	{
		//If not valid, do nothing
		if (!this.isValidPair(aPair))
			return;
		
		String firstWord = aPair.getFirstWord();
		String secondWord = aPair.getSecondWord();
		SingleWordCluster firstCluster = null;
		SingleWordCluster secondCluster = null;
		
		for(Iterator<SingleWordCluster> iter = clusters.iterator(); iter.hasNext();)
		{
			SingleWordCluster curCluster = iter.next();
			if (curCluster != null)
			{
				//Find the Lists that have the first word at the end, and the second word at
				//the beginning
				
				ArrayList<String> curList = curCluster.getWordList();
				int size = curList.size();
				String firstItem = curList.get(0);
				String lastItem = curList.get(size - 1);
				
				if(firstItem.equals(secondWord))
					secondCluster = curCluster;
				
				if(lastItem.equals(firstWord))
					firstCluster = curCluster;
			}//end non null
		}//end iterator
		
		SingleWordCluster newCluster = new SingleWordCluster();
		newCluster.initialize(params);
		ArrayList<String> firstList = firstCluster.getWordList();
		ArrayList<String> secondList = secondCluster.getWordList();
		
		for (int i = 0; i< firstList.size(); i++)
		{
			String curWord = firstList.get(i);
			newCluster.add(curWord);
		}
		
		for (int i = 0; i< secondList.size(); i++)
		{
			String curWord = secondList.get(i);
			newCluster.add(curWord);
		}
		
		//Remove old lists and add new
		clusters.remove(firstCluster);
		clusters.remove(secondCluster);
		clusters.add(newCluster);
	}
	
	/**
	 * Verifies whether or not a supplied WordPair is valid for the current
	 * state of the WordClusters
	 * @return true if the WordPair is a valid candidate to define the 
	 * conglomoration of clusters.
	 */
	private boolean isValidPair(WordPair aPair)
	{
		boolean isValid = false;
		String firstWord = aPair.getFirstWord();
		String secondWord = aPair.getSecondWord();
		SingleWordCluster firstCluster = null;
		SingleWordCluster secondCluster = null;
		
		for(Iterator<SingleWordCluster> iter = clusters.iterator(); iter.hasNext();)
		{
			SingleWordCluster curCluster = iter.next();
			if (curCluster != null)
			{
				//Find the Lists that have the first word at the end, and the second word at
				//the beginning
				
				ArrayList<String> curList = curCluster.getWordList();
				int size = curList.size();
				String firstItem = curList.get(0);
				String lastItem = curList.get(size - 1);
				
				if(firstItem.equals(secondWord))
					secondCluster = curCluster;
				
				if(lastItem.equals(firstWord))
					firstCluster = curCluster;
			}//end non null
		}//end iterator
		
		if (firstCluster != null && secondCluster != null && (!firstCluster.equals(secondCluster)))
			isValid = true;
		
		return isValid;
	}
	
	/**
	 * Sorts the clusters in decreasing size of the sum of the font sizes
	 * for each cluster.
	 * @return
	 */
	public void orderClusters()
	{
		Collections.sort(clusters);
		Collections.reverse(clusters);
	}
	
	//Getters and Setters
	public ArrayList<SingleWordCluster> getClusters()
	{
		return clusters;
	}
	
	public void setClusters(ArrayList<SingleWordCluster> clusterSet)
	{
		clusters = clusterSet;
	}
	
	public CloudParameters getCloudParameters()
	{
		return params;
	}
	
	public void setCloudParameters(CloudParameters cloudParams)
	{
		params = cloudParams;
	}
}
