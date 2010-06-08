/*
 File: CloudParameters.java

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;

import cytoscape.CyNode;
import cytoscape.Cytoscape;

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
	private ArrayList<CloudWordInfo> cloudWords;
	
	private WordFilter filter;
	
	private Double netWeightFactor = 1.0;
	
	private Double minRatio;
	private Double maxRatio;
	
	private boolean countInitialized = false; //true when network counts are initialized
	private boolean selInitialized = false; //true when selected counts initialized
	private boolean ratiosInitialized = false; //true when ratios are computed
	
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
		this.cloudWords = new ArrayList<CloudWordInfo>();
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
		
		//Clear old counts
		this.networkCounts = new HashMap<String, Integer>();
		this.stringNodeMapping = new HashMap<String, List<CyNode>>();
		
		
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
			nodeName = nodeName.replaceAll("[[\\p{Punct}] && [^'-]]", " ");
	        
	        //Separate into non repeating set of words
			Set<String> wordSet = new HashSet<String>();
	        StringTokenizer token = new StringTokenizer(nodeName);
	        while (token.hasMoreTokens())
	        {
	        	String a = token.nextToken();
	        	wordSet.add(a);
	        }
	        
	        
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
		
		//Clear old counts
		this.selectedCounts = new HashMap<String, Integer>();
		
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
			nodeName = nodeName.replaceAll("[[\\p{Punct}] && [^'-]]", " ");
	        
	        //Separate into non repeating set of words
			Set<String> wordSet = new HashSet<String>();
	        StringTokenizer token = new StringTokenizer(nodeName);
	        while (token.hasMoreTokens())
	        {
	        	String a = token.nextToken();
	        	wordSet.add(a);
	        }
	        
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
		//already up to date
		if (ratiosInitialized)
			return;
		
		//Check that selected counts are up to date
		if(!selInitialized)
			this.updateSelectedCounts();
		
		//Clear old counts
		this.ratios = new HashMap<String, Double>();
		
		Double curMin = 0.0;
		Double curMax = 0.0;
		
		//Get all words appearing in selected nodes
		Set<String> words = selectedCounts.keySet();
		
		//Iterate through to calculate ratios
		Iterator<String> iter = words.iterator();
		boolean initialized = false;
		while (iter.hasNext())
		{
			String curWord = (String)iter.next();
			
			/* Ratio: (selCount/selTotal)/((netCount/netTotal)^netWeightFactor)
			 * But, to avoid underflow from small probabilities we calculate it as follows:
			 * (selCount * (netTotal^netWeightFactor))/(selTotal * (netCount^netWeightFactor))
			 * This is the same as the original definition of ratio, just with some
			 * different algebra.
			 */
			Integer selTotal = this.getSelectedNumNodes();
			Integer selCount = selectedCounts.get(curWord);
			Integer netCount = networkCounts.get(curWord);
			Double newNetCount = Math.pow(netCount, netWeightFactor);
			Integer netTotal = this.getNetworkParams().getNetworkNumNodes();
			Double newNetTotal = Math.pow(netTotal, netWeightFactor);
			
			Double numerator = selCount * newNetTotal;
			Double denominator = selTotal * newNetCount;
			Double ratio = numerator/denominator;
			
			ratios.put(curWord, ratio);
			
			//Update max/min ratios
			if (!initialized)
			{
				curMax = ratio;
				curMin = ratio;
				initialized = true;
			}
			
			if (ratio > curMax)
				curMax = ratio;
			
			if (ratio < curMin)
				curMin = ratio;
		}
		
		this.setMaxRatio(curMax);
		this.setMinRatio(curMin);
		
		ratiosInitialized = true;
	}
	
	/**
	 * Calculates the proper font size for words in the selected nodes.
	 */
	public void calculateFontSizes()
	{
		if (!ratiosInitialized)
			this.updateRatios();
		
		//Clear old fonts
		this.cloudWords = new ArrayList<CloudWordInfo>();
		
		Set<String> words = ratios.keySet();
		Iterator<String> iter = words.iterator();
		while(iter.hasNext())
		{
			String curWord = (String)iter.next();
			Integer fontSize = calculateFontSize(curWord);
			CloudWordInfo curInfo = new CloudWordInfo(curWord, fontSize);
			curInfo.setCloudParameters(this);
			cloudWords.add(curInfo);
		}//end while loop
		
		//Sort cloudWords in reverse order by fontsize
		Collections.sort(cloudWords);
		Collections.reverse(cloudWords);
	}
	
	
	/**
	 * Calculates the font for a given word by using its ratio, the max and
	 * min ratios as well as the max and min font size in the parent 
	 * parameters object.  Assumes ratios are up to date and that word
	 * is in the selected nodes.
	 * @return Integer - the calculated font size for the specified word.
	 */
	private Integer calculateFontSize(String aWord)
	{
		//Sanity check
		if (!ratios.containsKey(aWord))
			return 0;
		
		Double ratio = ratios.get(aWord);
		
		//Map the interval minRatio to maxRatio to the new interval 
		//minFont to maxFont using a linear transformation
		Integer maxFont = networkParams.getMaxFont();
		Integer minFont = networkParams.getMinFont();
		
		//Check if maxRatio and minRatio are the same
		if (maxRatio == minRatio)
			return (maxFont - minFont)/2;
		
		Double slope = (maxFont - minFont)/(maxRatio - minRatio);
		Double yIntercept = maxFont - (slope*maxRatio); //maxRatio maps to maxFont
		
		//Round up to nearest Integer
		Double temp = Math.ceil((slope*ratio) + yIntercept);
		Integer fontSize = temp.intValue();
		
		//Debug code //TODO - remove
		//System.out.println("Word:" + aWord + " Ratio:" + ratio + " Font: " + fontSize);
		
		return fontSize;
	}
	
	/**
	 * Retrieves values from Input panel and stores in correct places.
	 * @return
	 */
	public void retrieveInputVals()
	{
		SemanticSummaryInputPanel inputPanel = 
			SemanticSummaryManager.getInstance().getInputWindow();
		
		JFormattedTextField netWeightTextField = inputPanel.getNetWeightTextField();
		
		Number value = (Number) netWeightTextField.getValue();
		if ((value != null) && (value.doubleValue() >= 0.0) && (value.doubleValue() <= 1))
		{
			netWeightFactor = value.doubleValue();
		}
		else
		{
			netWeightTextField.setValue(1.0);
			netWeightFactor = 1.0;
			String message = "The network weight factor must be greater than or equal to 0 and less than or equal to 1";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, "Parameter out of bounds", JOptionPane.WARNING_MESSAGE);
		}
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
		selInitialized = false; // need to recalculate
		ratiosInitialized = false; // need to recalculate
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
		//Copy nodes individually, so that when Set updates, this set
		//stays constant
		Iterator<CyNode> iter = nodes.iterator();
		while (iter.hasNext())
		{
			CyNode curNode = iter.next();
			selectedNodes.add(curNode);
		}
		
		selInitialized = false; //So we update when SelectedNodes change
		ratiosInitialized = false; //need to update ratios
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
	
	public ArrayList<CloudWordInfo> getCloudWordInfoList()
	{
		return cloudWords;
	}
	
	public void setCloudWordInfoList(ArrayList<CloudWordInfo> words)
	{
		cloudWords = words;
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
	
	public boolean getRatiosInitialized()
	{
		return ratiosInitialized;
	}
	
	public void setRatiosInitialized(boolean val)
	{
		ratiosInitialized = val;
	}
	
	public Double getNetWeightFactor()
	{
		return netWeightFactor;
	}
	
	public void setNetWeightFactor(Double val)
	{
		netWeightFactor = val;
	}
	
}
