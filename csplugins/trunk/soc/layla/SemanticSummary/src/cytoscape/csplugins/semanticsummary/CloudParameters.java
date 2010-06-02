/*
 File: CloudParameters.java

 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.csplugins.semanticsummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cytoscape.CyNode;

/**
 * The CloudParameters class defines all of the variables that are
 * needed to create a word Cloud for a particular network, attribute, 
 * and set of selected nodes.
 * @author Layla Oesper
 * @version 1.0
 *
 */

public class CloudParameters 
{

	//VARIABLES
	private String cloudName;
	private String networkName;
	private String attributeName;
	
	private SemanticSummaryParameters networkParams; //parent network
	
	private Set<CyNode> selectedNodes; //set of selected nodes for cloud
	private Integer selectedNumNodes;
	
	private HashMap<String, List<CyNode>> stringNodeMapping;
	private HashMap<String, Integer> networkCounts; // counts for whole network
	private HashMap<String, Integer> selectedCounts; // counts for selected nodes
	private HashMap<String, Double> ratios;
	
	private WordFilter filter;
	
	private Double minRatio;
	private Double maxRatio;
	
	private boolean countInitialized = false; //true when network counts are initialized
	private boolean selInitialized = false; //true when selected counts initialized
	
	//CONSTRUCTORS
	
	/**
	 * Default constructor to create a fresh instance
	 */
	public CloudParameters()
	{
		this.selectedNodes = new HashSet<CyNode>();
		this.stringNodeMapping = new HashMap<String, List<CyNode>>();
		this.networkCounts = new HashMap<String, Integer>();
		this.selectedCounts = new HashMap<String, Integer>();
		this.ratios = new HashMap<String, Double>();
		this.filter = new WordFilter();
	}
	
	/**
	 * Constructor that includes the name for the cloud.
	 * @param String - name for the cloud.
	 */
	public CloudParameters(String name)
	{
		this();
		this.cloudName = name;
	}
	
	//METHODS
	
	//Calculate Counts
	
	/**
	 * Constructs stringNodeMapping and networkCounts based on the list of
	 * nodes contained in networkParams.
	 */
	public void initializeNetworkCounts()
	{
		//do nothing if already initialized
		if (countInitialized)
			return;
		
		//Retrieve needed variables from parent parameters
		SemanticSummaryParameters networkParams = this.getNetworkParams();
		List<CyNode> networkNodes = networkParams.getNetworkNodes();
		
		//Iterate to retrieve CyNodes
		Iterator<CyNode> iter = networkNodes.iterator();
		while(iter.hasNext())
		{
			CyNode curNode = (CyNode)iter.next();
			
			//This line will be different for different attributes
			//MILESTONE 2 UPDATE THIS
			String nodeName = curNode.toString();
			
			//Only deal with lower case
			nodeName = nodeName.toLowerCase();
			
			//replace all punctuation with white spaces except ' and -
			nodeName = nodeName.replaceAll("[\\p{Punct} && [^'-]", " ");
			
			//Remove duplicate words, create set
			String[] words = nodeName.split("\b");
			Set<String> wordSet = new HashSet<String>();
	        for (String a : words)
	            wordSet.add(a);
	        
	        //Iterate through all words
	        Iterator<String> wordIter = wordSet.iterator();
	        while(wordIter.hasNext())
	        {
				String curWord = wordIter.next();
				
				//Check filters
				if (!filter.contains(curWord))
				{
					//If this word has not been encountered, or not encountered
					//in this node, add it to our mappings and counts
					HashMap<String, List<CyNode>> curMapping = this.getStringNodeMapping();
				
					//If we have not encountered this word, add it to the mapping
					if (!curMapping.containsKey(curWord))
					{
						curMapping.put(curWord, new ArrayList<CyNode>());
						networkCounts.put(curWord, 0);
					}
					
					//Add node to mapping, update counts
					curMapping.get(curWord).add(curNode);
					Integer num = networkCounts.get(curWord);
					num = num + 1;
					networkCounts.put(curWord, num);
					
				}//end filter if
			}// word iterator
		}//end node iterator
		countInitialized = true;
	}
	
	
	/**
	 * Constructs selectedCounts based on the list of nodes contained in 
	 * selectedNodes list.
	 */
	public void updateSelectedCounts()
	{
		
		//do nothing if selected hasn't changed initialized
		if (selInitialized)
			return;
		
		//Initialize if needed
		if (!countInitialized)
			this.initializeNetworkCounts();
		
		//Retrieve needed variables from parent parameters
		SemanticSummaryParameters networkParams = this.getNetworkParams();
		
		Set<CyNode> selectedNodes = this.getSelectedNodes();
		
		//Iterate to retrieve Cynodes
		Iterator<CyNode> iter = selectedNodes.iterator();
		while(iter.hasNext())
		{
			CyNode curNode = (CyNode)iter.next();
			
			//This line will be different for different attributes
			//MILESTONE 2 UPDATE THIS
			String nodeName = curNode.toString();
			
			//Only deal with lower case
			nodeName = nodeName.toLowerCase();
			
			//replace all punctuation with white spaces except ' and -
			nodeName = nodeName.replaceAll("[\\p{Punct} && [^'-]", " ");
			
			//Remove duplicate words, create set
			String[] words = nodeName.split("\b");
			Set<String> wordSet = new HashSet<String>();
	        for (String a : words)
	            wordSet.add(a);
	        
	        //Iterate through all words
	        Iterator<String> wordIter = wordSet.iterator();
	        while(wordIter.hasNext())
	        {
				String curWord = wordIter.next();
				
				//Check filters
				if (!filter.contains(curWord))
				{
					Integer curCount = 0; //if never encountered before
					
					if (selectedCounts.containsKey(curWord))
						curCount = selectedCounts.get(curWord);
					
					//Update Count
					curCount = curCount + 1;
					
					//Add updated count to HashMap
					selectedCounts.put(curWord, curCount);
					
				}//end filter if
			}// word iterator
		}//end node iterator
		selectedNumNodes = selectedNodes.size();
		selInitialized = true;
	}
	
	/**
	 * Calculates ratios given the current selectedNode counts.
	 */
	public void updateRatios()
	{
		//Check that selected counts are up to date
		if(!selInitialized)
			this.updateSelectedCounts();
		
		Double curMin = 0.0;
		Double curMax = 0.0;
		
		//Get all words appearing in selected nodes
		Set<String> words = selectedCounts.keySet();
		
		//Iterate through to calculate ratios
		Iterator<String> iter = words.iterator();
		while (iter.hasNext())
		{
			String curWord = (String)iter.next();
			
			Integer selTotal = this.getSelectedNumNodes();
			Integer selCount = selectedCounts.get(curWord);
			Integer netCount = networkCounts.get(curWord);
			Integer netTotal = this.getNetworkParams().getNetworkNumNodes();
			
			Integer numerator = selCount * netTotal;
			Double numeratorDoub = numerator.doubleValue();
			Integer denominator = selTotal * netCount;
			Double denominatorDoub = denominator.doubleValue();
			Double ratio = numeratorDoub/denominatorDoub;
			
			ratios.put(curWord, ratio);
			
			//Update max/min ratios
			if (curMax == 0.0)
				curMax = ratio;
			
			if (curMin == 0.0)
				curMin = ratio;
			
			if (ratio > curMax)
				curMax = ratio;
			
			if (ratio < curMin)
				curMin = ratio;
		}
		
		this.setMaxRatio(curMax);
		this.setMinRatio(curMin);
	}
	
	//Getters and Setters
	public String getCloudName()
	{
		return cloudName;
	}
	
	public void setCloudName(String name)
	{
		cloudName = name;
	}
	
	public String getNetworkName()
	{
		return networkName;
	}
	
	public void setNetworkName(String name)
	{
		networkName = name;
	}
	
	public String getAttributeName()
	{
		return attributeName;
	}
	
	public void setAttributeName(String name)
	{
		attributeName = name;
		countInitialized = false; //need to recalculate counts
	}
	
	public SemanticSummaryParameters getNetworkParams()
	{
		return networkParams;
	}
	
	public void setNetworkParams(SemanticSummaryParameters params)
	{
		networkParams = params;
	}
	
	public Set<CyNode> getSelectedNodes()
	{
		return selectedNodes;
	}
	
	public void setSelectedNodes(Set<CyNode> nodes)
	{
		selectedNodes = nodes;
		selInitialized = false; //So we update when SelectedNodes change
	}
	
	public Integer getSelectedNumNodes()
	{
		return selectedNumNodes;
	}
	
	public void setSelectedNumNodes(Integer num)
	{
		selectedNumNodes = num;
	}
	
	public HashMap<String, List<CyNode>> getStringNodeMapping()
	{
		return stringNodeMapping;
	}
	
	public void setStringNodeMapping(HashMap<String, List<CyNode>> mapping)
	{
		stringNodeMapping = mapping;
	}
	
	public HashMap<String,Integer> getNetworkCounts()
	{
		return networkCounts;
	}
	
	public void setNetworkCounts(HashMap<String, Integer> counts)
	{
		networkCounts = counts;
	}
	
	public HashMap<String,Integer> getSelectedCounts()
	{
		return selectedCounts;
	}
	
	public void setSelectedCounts(HashMap<String, Integer> counts)
	{
		selectedCounts = counts;
	}
	
	public HashMap<String,Double> getRatios()
	{
		return ratios;
	}
	
	public void setRatios(HashMap<String, Double> r)
	{
		ratios = r;
	}
	
	public WordFilter getFilter()
	{
		return filter;
	}
	
	public void setFilter(WordFilter aFilter)
	{
		filter = aFilter;
	}

	
	public Double getMinRatio()
	{
		return minRatio;
	}
	
	public void setMinRatio(Double ratio)
	{
		minRatio = ratio;
	}
	
	public Double getMaxRatio()
	{
		return maxRatio;
	}
	
	public void setMaxRatio(Double ratio)
	{
		maxRatio = ratio;
	}
	
	public boolean getCountInitialized()
	{
		return countInitialized;
	}
	
	public void setCountInitialized(boolean val)
	{
		countInitialized = val;
	}
	
	public boolean getSelInitialized()
	{
		return selInitialized;
	}
	
	public void setSelInitialized(boolean val)
	{
		selInitialized = val;
	}
}
