/*
 File: ClusterPriorityQueue.java

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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The ClusterPriorityQueue builds a priority queue of WordPairs from
 * a specified CloudParameters object.  This is a max priority queue based
 * on the probability of each word pair, with ties being broken using the the
 * selected/network ratio of each pair, and then ties are broken again 
 * alphabetically.  This priority queue is used to hierarchically cluster
 * the words appearing in a cloud.
 * @author Layla Oesper
 * @version 1.0
 */

public class ClusterPriorityQueue 
{
	//VARIABLES
	private ArrayList<WordPair> queue;
	private CloudParameters params;
	private boolean initialized;
	
	//CONSTRUCTORS
	/**
	 * Creates a fresh instance of the priority queue.
	 */
	public ClusterPriorityQueue()
	{
		queue = new ArrayList<WordPair>();
		params = new CloudParameters();
		initialized = false;
	}
	
	//METHODS
	/**
	 * Initializes the priority queue for the specified cloud parameters
	 * @param CloudParameters that this queue is for.
	 */
	public void initialize(CloudParameters cloudParams)
	{
		params = cloudParams;
		queue = new ArrayList<WordPair>();
		
		//Initialize params if necessary
		if (!params.getRatiosInitialized())
			params.updateRatios();
		
		Set<String> pairNames = params.getSelectedPairCounts().keySet();
		for (Iterator<String> iter = pairNames.iterator(); iter.hasNext();)
		{
			String curName = iter.next();
			String first = curName.split(" ")[0];
			String second = curName.split(" ")[1];
			
			WordPair curPair = new WordPair(first, second, params);
			curPair.calculateProbability();
			queue.add(curPair);
		}
		
		//Sort the Priority Queue so items with the largest probability are first
		Collections.sort(queue);
		Collections.reverse(queue);
		
		initialized = true;
	}
	
	/**
	 * Returns the WordPair located at the top of the queue, without removing it.
	 * @return WordPair with the highest probability remaining in the queue. If the 
	 * queue is empty null, is returned.
	 */
	public WordPair peak()
	{
		if (!queue.isEmpty())
			return queue.get(0);
		else
			return null;
	}
	
	/**
	 * Returns the WordPair located at the top of the queue and removes it along
	 * with all other entries in the queue that are now obsolete.
	 * @return WordPair with the highest probability remaining in the queue.  If the 
	 * queue is empty, null is returned.
	 */
	public WordPair remove()
	{
		WordPair removedPair;
		if (!queue.isEmpty())
			removedPair = queue.remove(0);
		else
			removedPair = null;
		
		//Remove all other entries from queue necessary
		if (removedPair != null)
		{
			String firstWord = removedPair.getFirstWord();
			String secondWord = removedPair.getSecondWord();
			List<WordPair> toRemove = new ArrayList<WordPair>();
			
			//Create list to remove
			for(Iterator<WordPair> iter = queue.iterator(); iter.hasNext();)
			{
				WordPair curPair = iter.next();
				String curFirst = curPair.getFirstWord();
				String curSecond = curPair.getSecondWord();
				
				if (firstWord.equals(curFirst) || secondWord.equals(curSecond))
					toRemove.add(curPair);
			}
			
			//Remove everything from list
			for(Iterator<WordPair> iter = toRemove.iterator(); iter.hasNext();)
			{
				WordPair curPair = iter.next();
				
				if (queue.contains(curPair))
					queue.remove(curPair);
			}
		}
		return removedPair;	
	}
	
	/**
	 * Returns the size of the queue.
	 * @return int size of the queue
	 */
	public int size()
	{
		return queue.size();
	}
	
	/**
	 * Returns true if the queue is empty.
	 * @return boolean - indicating whether or not the queue is emtpy.
	 */
	public boolean isEmpty()
	{
		return queue.isEmpty();
	}
	
	
	//Getters and Setters
	public ArrayList<WordPair> getQueue()
	{
		return queue;
	}
	
	public void setQueue(ArrayList<WordPair> aQueue)
	{
		queue = aQueue;
	}
	
	public CloudParameters getCloudParameters()
	{
		return params;
	}
	
	public void setCloudParameters(CloudParameters cloudParams)
	{
		params = cloudParams;
	}
	
	public boolean isInitialized()
	{
		return initialized;
	}
	
	public void setInitialialized(boolean val)
	{
		initialized = val;
	}
}
