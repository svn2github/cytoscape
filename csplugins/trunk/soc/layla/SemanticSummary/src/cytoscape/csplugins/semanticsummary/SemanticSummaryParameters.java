/*
 File: SemanticSummaryParameters.java

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
import cytoscape.CyNetwork;
import cytoscape.CyNode;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This SemanticSummaryParameters class defines all of the variables that are
 * needed to create and manipulate an individual Semantic Summary for a 
 * particular network and attribute.
 * @author Layla Oesper
 * @version 1.0
 *
 */

public class SemanticSummaryParameters 
{
	
	//VARIABLES
	private String networkName;
	private String attributeName;
	private String networkAttributeKey;
	
	private WordFilter filter;
	
	private HashMap<String, ArrayList<CyNode>> stringNodeMapping;
	private HashMap<String, Integer> networkCounts; // counts for whole network
	private HashMap<String, Integer> selectedCounts; // counts for selected nodes
	private HashMap<String, Double> ratios;
	
	private Double minRatio;
	private Double maxRatio;
	
	private int networkNumNodes;
	private int selectedNumNodes; 
	
	private boolean isInitialized;//set to true when original data counts set
	private boolean useName; //set to true when cloud is build on node names
	
	//FONT SIZE AND NUM BINS GOES HERE
	//TODO
	private static final Integer NUMBINS = 7; //NEED TO DECIDE ON THIS
	private static final String UNDERSCORE = "_";
	
	//CONSTRUCTORS
	
	/**
	 * Default constructor to create a fresh instance
	 */
	public SemanticSummaryParameters()
	{
		this.filter = new WordFilter();
		
		this.stringNodeMapping = new HashMap<String, ArrayList<CyNode>>();
		this.networkCounts = new HashMap<String, Integer>();
		this.selectedCounts = new HashMap<String, Integer>();
		this.ratios = new HashMap<String, Double>();
		
		this.useName = true; //for now we always use the Name as the attribute
		this.isInitialized = false;
		
		//KEEP OR REMOVE???
		//this.initialize();
		//this.update();
	}
	
	/**
	 * Constructor to create a fresh instance when the network is specified.  
	 * In this case, there is no attribute, so we assume that the name of the
	 * nodes is the attribute in question.
	 * 
	 * @param String name of CyNetwork
	 */
	public SemanticSummaryParameters(String network)
	{
		this();
		this.networkName = network;
		this.networkAttributeKey = network;
	}
	
	/**
	 * Constructor to create a fresh instance when the network and attribute
	 * are both specified.
	 * 
	 * @param String name of CyNetwork
	 * @param String name of the attribute
	 */
	public SemanticSummaryParameters(String network, String attribute)
	{
		this();
		this.networkName = network;
		this.attributeName = attribute;
		this.networkAttributeKey = network + UNDERSCORE + attribute;
		this.useName = false;
	}
	
	
	//METHODS
	
	//DATA MANIPULATIONS
	
	/**
	 * Initializes Parameters for the supplied network.  Assumes we are looking
	 * at node names rather than an attribute.
	 * 
	 * @param CyNetwork - the CyNetwork associated with this object
	 */
	public void initialize(CyNetwork curNetwork)
	{
		//Check that network is not null and for initialization
		if (curNetwork == null || isInitialized)
			return;
		
		//Get List of CyNodes
		List<CyNode> allNodes = curNetwork.nodesList();
		
		//Iterate to retrieve nodes
		Iterator<CyNode> iter = allNodes.iterator();
		while(iter.hasNext())
		{
			CyNode curNode = (CyNode)iter.next();
			
			//This line will be different for different attributes
			String nodeName = curNode.toString();
			
			//Only deal with lower case
			nodeName = nodeName.toLowerCase();
			
			//replace all punctuation with white spaces except ' and -
			nodeName = nodeName.replaceAll("[\\p{Punct} && [^'-]", " ");
			
			//Remove duplicate words
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
				if (!this.getFilter().contains(curWord))
				{
					//If this word has not been encountered, or not encountered
					//in this node, add it to our mappings and counts
					HashMap<String, ArrayList<CyNode>> curMapping = this.getStringNodeMapping();
				
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
		
		this.setNetworkNumNodes(allNodes.size());
		isInitialized = true;
	}
	
	/**
	 * Initializes Parameters for the supplied network and attribute combination.
	 */
	public void initialize(CyNetwork curNetwork, String attribute)
	{
		//TODO FOR MILESTONE 2
	}
	
	
	/**
	 * Updates Parameters for the selected nodes of the supplied network.  
	 * Assumes we are looking at node names rather than an attribute.
	 * 
	 * @param CyNetwork - the CyNetwork associated with this object
	 */
	public void update(CyNetwork curNetwork)
	{
		//Check that network is not null and for initialization
		if (curNetwork == null || isInitialized)
			return;
		
		//Get List of Selected Nodes
		Set<CyNode> selNodes = curNetwork.getSelectedNodes();
		
		//Iterate to retrieve nodes
		Iterator<CyNode> iter = selNodes.iterator();
		while(iter.hasNext())
		{
			CyNode curNode = (CyNode)iter.next();
			
			//This line will be different for different attributes
			String nodeName = curNode.toString();
			
			//Only deal with lower case
			nodeName = nodeName.toLowerCase();
			
			//replace all punctuation with white spaces except ' and -
			nodeName = nodeName.replaceAll("[\\p{Punct} && [^'-]", " ");
			
			//Remove duplicate words
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
				if (!this.getFilter().contains(curWord))
				{
					Integer num = 0;
					if(selectedCounts.containsKey(curWord))
						num = selectedCounts.get(curWord);
					
					num = num + 1;
					
					selectedCounts.put(curWord, num);
				}//end filter if
			}// word iterator
		}//end node iterator
		
		//UPDATE RATIOS AND OTHER VARIABLES
		
		this.setSelectedNumNodes(selNodes.size());
	}
	
	//DATA CALCULATIONS
	
	/**
	 * Returns the number of different words in the network.
	 * 
	 * @return Integer - the number of word types in the network.
	 */
	public Integer getNumNetworkWords()
	{
		return networkCounts.size();
	}
	
	/**
	 * Returns the number of different words in the selected nodes.
	 * 
	 * @return Integer - the number of word types in the selection.
	 */
	public Integer getNumSelectedWords()
	{
		return selectedCounts.size();
	}
	
	/**
	 * Returns the total number of words in the whole network
	 * 
	 * @return Integer - total number of word tokens in network.
	 */
	public Integer getNetworkTotalCount()
	{
		Integer total = 0;
		HashMap<String,Integer> network = this.getNetworkCounts();
		
		Iterator<Integer> iter = network.values().iterator();
		while (iter.hasNext())
		{
			Integer val = iter.next();
			total = total + val;
		}
		return total;
	}
	
	
	/**
	 * Returns the total number of words in the whole selection
	 * 
	 * @return Integer - total number of word tokens in the selection.
	 */
	public Integer getSelectedTotalCount()
	{
		Integer total = 0;
		HashMap<String,Integer> selected = this.getSelectedCounts();
		
		Iterator<Integer> iter = selected.values().iterator();
		while (iter.hasNext())
		{
			Integer val = iter.next();
			total = total + val;
		}
		return total;
	}
	
	
	/**
	 * Returns a list of all the words in the Network.
	 * 
	 * @return Set - of the words in the network.
	 */
	public Set<String> getNetworkWords()
	{
		HashMap<String,Integer> network = this.getNetworkCounts();
		Set<String> words = network.keySet();
		
		return words;
	}
	
	/**
	 * Returns a list of all the words in the Selection.
	 * 
	 * @return Set - of the words in the selected nodes.
	 */
	public Set<String> getSelectedWords()
	{
		HashMap<String,Integer> selected = this.getSelectedCounts();
		Set<String> words = selected.keySet();
		
		return words;
	}
	
	
	public Double getNetworkWordProb(String word)
	{
		//TODO
	}
	
	public Double getSelectedProb(String word)
	{
		//TODO
	}
	
	public Double calculateRatio(String word)
	{
		//TODO
	}
	
	public Integer calculateFontSize(String word)
	{
		//TODO
	}
	
	
	//GETTERS and SETTERS
	
	public String getNetworkName()
	{
		return networkName;
	}
	
	public void setNetworkName(String network)
	{
		networkName = network;
	}
	
	public String getAttributeName()
	{
		return attributeName;
	}
	
	public void setAttributeName(String attribute)
	{
		attributeName = attribute;
	}
	
	public String getNetworkAttributeKey()
	{
		return networkAttributeKey;
	}
	
	public void setNetworkAttributeKey(String key)
	{
		networkAttributeKey = key;
	}
	
	public WordFilter getFilter()
	{
		return filter;
	}
	
	public void setFilter(WordFilter aFilter)
	{
		filter = aFilter;
	}
	
	//EXCESSIVE???
	public boolean isAttributeName(String attribute)
	{
		return attributeName.equals(attribute);
	}
	
	public HashMap<String, ArrayList<CyNode>> getStringNodeMapping()
	{
		return stringNodeMapping;
	}
	
	public void setStringNodeMapping(HashMap<String, ArrayList<CyNode>> mapping)
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
	
	public Integer getNetworkNumNodes()
	{
		return networkNumNodes;
	}
	
	public void incNetworkNumNodes(Integer num)
	{
		networkNumNodes = networkNumNodes + num;
	}
	
	public void decNetworkNumNodes(Integer num)
	{
		networkNumNodes = networkNumNodes - num;
	}
	
	public void setNetworkNumNodes(Integer num)
	{
		networkNumNodes = num;
	}
	
	public Integer getSelectedNumNodes()
	{
		return selectedNumNodes;
	}
	
	public void incSelectedNumNodes(Integer num)
	{
		selectedNumNodes = selectedNumNodes + num;
	}
	
	public void decSelectedNumNodes(Integer num)
	{
		selectedNumNodes = selectedNumNodes - num;
	}
	
	public void setSelectedNumNodes(Integer num)
	{
		selectedNumNodes = num;
	}
	
	public Double getMinRatio()
	{
		return minRatio;
	}
	
	public Double getMaxRatio()
	{
		return maxRatio;
	}
	
	public Integer getNumBins()
	{
		return NUMBINS;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
