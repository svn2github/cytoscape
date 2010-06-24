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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	private ClusterPriorityQueue queue;
	private WordClusters clusters;
	private Double cutoff;
	private ArrayList<CloudWordInfo> cloudWords;
	boolean initialized;
	private final static Integer NUMCOLORS = 8;
	
	//CONSTRUCTORS
	
	/** 
	 * Default constructor to create a fresh instance.  
	 */
	public SemanticSummaryClusterBuilder()
	{
		params = new CloudParameters();
		queue = new ClusterPriorityQueue();
		clusters = new WordClusters();
		cutoff = 0.0;
		cloudWords = new ArrayList<CloudWordInfo>();
		initialized = false;
		
	}
	
	//METHODS
	/**
	 * Initializes all elements of this SemanticSummaryClusterBuilder
	 * for the provided CloudParameter.
	 * @param CloudParameters - defines the cloud that this cluster builder is for.
	 */
	public void initialize(CloudParameters cloudParams)
	{
		params = cloudParams;
		queue.initialize(cloudParams);
		clusters.initialize(cloudParams);
		initialized = true;
	}
	
	/**
	 * Clusters the data from the CloudParameters stored in the variables
	 * using the input cutoff if this object has been initialized.
	 * @param Double - the cutoff value to use for clustering.
	 */
	public void clusterData(Double cutoffVal)
	{
		if (!initialized)
			return;
		
		cutoff = cutoffVal;
		
		boolean isDone = false; //flag for when we pass the cutoff value
		
		while (!isDone && !queue.isEmpty())
		{
			WordPair curPair = queue.peak();
			
			//Check cutoff
			if (curPair.getProbability()< cutoffVal)
			{
				isDone = true;
				continue;
			}
			
			curPair = queue.remove();
			clusters.combineClusters(curPair);
		}//end while
	}
	
	/**
	 * Maps a cluster number (based on its index in clusters) to the color that will
	 * be used to display that cluster in the Semantic Summary.
	 * @assumes that the cluster number provided is a valid number.
	 * @param cluster number
	 */
	private Color getClusterColor(Integer clusterNum)
	{
		Integer rem = clusterNum % NUMCOLORS;
		Color textColor;
		
        switch (rem) 
        {
            case 0:  textColor = Color.BLACK; break;
            case 1:  textColor = Color.CYAN; break;
            case 2:  textColor = Color.GRAY; break;
            case 3:  textColor = Color.GREEN; break;
            case 4:  textColor = Color.MAGENTA; break;
            case 5:  textColor = Color.ORANGE; break;
            case 6:  textColor = Color.PINK; break;
            case 7:  textColor = Color.RED; break;
            default: textColor = Color.BLACK;break; //Black by default
        }
        return textColor;
	}
	
	/**
	 * Builds an array of CloudWords based on the current state of clustering
	 * for this CloudParameters.
	 */
	public void buildCloudWords()
	{
		if (!initialized)
			return;
		
		//Clear old values
		cloudWords = new ArrayList<CloudWordInfo>();
		
		for(int i = 0; i < clusters.getClusters().size(); i++)
		{
			ArrayList<String> curCluster = clusters.getClusters().get(i);
			Color clusterColor = getClusterColor(i);
			
			//Iterate through the words
			for (int j = 0; j < curCluster.size(); j++)
			{
				String curWord = curCluster.get(j);
				Integer fontSize = params.calculateFontSize(curWord);
				CloudWordInfo curInfo = new CloudWordInfo(curWord, fontSize);
				curInfo.setCloudParameters(params);
				curInfo.setTextColor(clusterColor);
				curInfo.setCluster(i);
				
				//Add to list
				cloudWords.add(curInfo);
			}//End iterate through cluster
		}//End iterate through list of clusters
	}//end method
	
	//Getters and Setters
	public void setCloudParameters(CloudParameters cloudParams)
	{
		params = cloudParams;
	}
	
	public CloudParameters getCloudParameters()
	{
		return params;
	}
	
	public void setCloudWords(ArrayList<CloudWordInfo> cloudWordInput)
	{
		cloudWords = cloudWordInput;
	}
	
	public ArrayList<CloudWordInfo> getCloudWords()
	{
		return cloudWords;
	}
	
	public void setClusters(WordClusters clusterSet)
	{
		clusters = clusterSet;
	}
	
	public WordClusters getClusters()
	{
		return clusters;
	}
	
	public void setQueue(ClusterPriorityQueue aQueue)
	{
		queue = aQueue;
	}
	
	public ClusterPriorityQueue getQueue()
	{
		return queue;
	}
	
	public void setInitialized(boolean val)
	{
		initialized = val;
	}
	
	public boolean getInitialized()
	{
		return initialized;
	}
	
	public Integer getNumColors()
	{
		return NUMCOLORS;
	}
	

}
