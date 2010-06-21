/*
 File: SemanticSummaryClusterBuilder.java

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
import java.util.List;
import java.util.Set;

/**
 * The SemanticSummaryClusterBuilder class contains the methods and
 * variables necessary for clustering the data contained in a 
 * CloudParameter object using a greedy hierarchical style algorithm.
 * @author Layla Oesper
 * @version 1.0
 */

public class SemanticSummaryClusterBuilder 
{
	
	//VARIABLES
	private CloudParameters params;
	private ArrayList<String> words;
	private ArrayList<ArrayList<String>> clusters;
	private ArrayList<CloudWordInfo> cloudWords;
	
	//CONSTRUCTORS
	
	/** 
	 * Default constructor to create a fresh instance.  
	 * @param CloudParameters - the cloud parameters that this builder
	 * will be working on.
	 */
	public SemanticSummaryClusterBuilder(CloudParameters cloudParams)
	{
		params = cloudParams;
		
		//Create ArrayList of words
		words = new ArrayList<String>();
		
		if (cloudParams.getSelInitialized())
		{
			Set<String> wordSet = cloudParams.getSelectedCounts().keySet();
			for(Iterator<String> iter = wordSet.iterator(); iter.hasNext();)
			{
				words.add(iter.next());
			}
		}
		
		clusters = new ArrayList<ArrayList<String>>();
		cloudWords = new ArrayList<CloudWordInfo>();
	}
	
	//METHODS
	
	/**
	 * Creates a matrix containing the counts of all pairs of words
	 * that appear next to each other in the selected nodes in the 
	 * CloudParameters for this object.  The entry in the (i,j) space in 
	 * the matrix counts, is how many times the pair of words associated
	 * with i and j appear in a selected node in that order.
	 */
	public void calculatePairCounts()
	{
		//Loop through for all words appearing in selected nodes
		for (int i = 0; i < words.size(); i++)
		{
			String firstWord = words.get(i);
			
			//Retrieve the set of nodes containing the i'th word
			List<String> nodes = params.getStringNodeMapping().get(firstWord);
		}
	}
	
	//Getters and Setters
	public void setCloudParameters(CloudParameters cloudParams)
	{
		params = cloudParams;
	}
	
	public CloudParameters getCloudParameters()
	{
		return params;
	}
	
	public void setListOfWords(ArrayList<String> wordList)
	{
		words = wordList;
	}
	
	public ArrayList<String> getListOfWords()
	{
		return words;
	}
	
	public void setCloudWords(ArrayList<CloudWordInfo> cloudWordInput)
	{
		cloudWords = cloudWordInput;
	}
	
	public ArrayList<CloudWordInfo> getCloudWords()
	{
		return cloudWords;
	}
	
	public void setClusters(ArrayList<ArrayList<String>> clusterSet)
	{
		clusters = clusterSet;
	}
	
	public ArrayList<ArrayList<String>> getClusters()
	{
		return clusters;
	}
	

}
