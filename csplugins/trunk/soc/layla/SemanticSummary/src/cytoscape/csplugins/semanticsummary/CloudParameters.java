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

import java.awt.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSlider;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/**
 * The CloudParameters class defines all of the variables that are
 * needed to create a word Cloud for a particular network, attribute, 
 * and set of selected nodes.
 * @author Layla Oesper
 * @version 1.0
 *
 */

public class CloudParameters implements Comparable<CloudParameters>
{

	//VARIABLES
	private String cloudName;
	private ArrayList<String> attributeNames;
	private String displayStyle;
	
	private SemanticSummaryParameters networkParams; //parent network
	
	private List<String> selectedNodes; //set of selected nodes for cloud
	
	private Integer selectedNumNodes;
	private Integer networkNumNodes;
	private Integer maxWords;
	private Integer cloudNum; //Used to order the clouds for each network
	
	private HashMap<String, List<String>> stringNodeMapping;
	private HashMap<String, Integer> networkCounts; // counts for whole network
	private HashMap<String, Integer> selectedCounts; // counts for selected nodes
	private HashMap<String, Integer> networkPairCounts;
	private HashMap<String, Integer> selectedPairCounts;
	private HashMap<String, Double> ratios;
	private HashMap<String, Double> pairRatios;
	private ArrayList<CloudWordInfo> cloudWords;
	
	
	private Double netWeightFactor;
	private Double clusterCutoff;
	
	private Double minRatio;
	private Double maxRatio;
	private Double meanRatio;
	
	private Double meanWeight;
	private Double minWeight;
	private Double maxWeight;
	
	private boolean countInitialized = false; //true when network counts are initialized
	private boolean selInitialized = false; //true when selected counts initialized
	private boolean ratiosInitialized = false; //true when ratios are computed
	private boolean useNetNormal = false; //true when network counts are used
	
	//String Delimeters
	private static final String NODEDELIMITER = "CloudParamNodeDelimiter";
	private static final String WORDDELIMITER = "CloudParamWordDelimiter";
	private static final char controlChar = '\u001F';
	
	//Network Name creation variables
	private Integer networkCount = 1;
	private static final String NETWORKNAME = "Net";
	private static final String SEPARATER = "_";
	
	//Default Values for User Input
	private Double defaultNetWeight = 0.0;
	private String defaultAttName = "nodeID";
	private Integer defaultMaxWords = 250;
	private Double defaultClusterCutoff = 1.0;
	private String defaultStyle = CloudDisplayStyles.DEFAULT_STYLE;
	
	//CONSTRUCTORS
	
	/**
	 * Default constructor to create a fresh instance
	 */
	public CloudParameters()
	{
		this.selectedNodes = new ArrayList<String>();
		this.stringNodeMapping = new HashMap<String, List<String>>();
		this.networkCounts = new HashMap<String, Integer>();
		this.selectedCounts = new HashMap<String, Integer>();
		this.networkPairCounts = new HashMap<String, Integer>();
		this.selectedPairCounts = new HashMap<String, Integer>();
		this.ratios = new HashMap<String, Double>();
		this.pairRatios = new HashMap<String, Double>();
		this.cloudWords = new ArrayList<CloudWordInfo>();
		
		this.netWeightFactor = this.getDefaultNetWeight();
		this.clusterCutoff = this.getDefaultClusterCutoff();
		this.maxWords = this.getDefaultMaxWords();
		this.displayStyle = this.getDefaultDisplayStyle();
		
		this.attributeNames = new ArrayList<String>();
		this.attributeNames.add(this.getDefaultAttName());
	}
	
	/**
	 * Constructor to create CloudParameters from a cytoscape property file
	 * while restoring a session.  Property file is created when the session is saved.
	 * @param propFile - the name of the property file as a String
	 */
	public CloudParameters(String propFile)
	{
		this();
		
		//Create a hashmap to contain all the values in the rpt file
		HashMap<String, String> props = new HashMap<String,String>();
		
		String[] lines = propFile.split("\n");
		
		for (int i = 0; i < lines.length; i++)
		{
			String line = lines[i];
			String[] tokens = line.split("\t");
			//there should be two values in each line
			if(tokens.length == 2)
				props.put(tokens[0],tokens[1]);
		}
		
		this.cloudName = props.get("CloudName");
		this.displayStyle = props.get("DisplayStyle");
		this.selectedNumNodes = new Integer(props.get("SelectedNumNodes"));
		this.networkNumNodes = new Integer(props.get("NetworkNumNodes"));
		this.netWeightFactor = new Double(props.get("NetWeightFactor"));
		this.clusterCutoff = new Double(props.get("ClusterCutoff"));
		this.countInitialized = Boolean.parseBoolean(props.get("CountInitialized"));
		this.selInitialized = Boolean.parseBoolean(props.get("SelInitialized"));
		this.ratiosInitialized = Boolean.parseBoolean(props.get("RatiosInitialized"));
		this.maxRatio = new Double(props.get("MaxRatio"));
		this.minRatio = new Double(props.get("MinRatio"));
		this.maxWords = new Integer(props.get("MaxWords"));
		this.cloudNum = new Integer(props.get("CloudNum"));
		
		//Backwards compatibale useNetNormal
		String val = props.get("UseNetNormal");
		if (val == null)
		{this.useNetNormal = true;}
		else
		{this.useNetNormal = Boolean.parseBoolean(props.get("UseNetNormal"));}
		
		//Backwards compatible meanRatio
		val = props.get("MeanRatio");
		if (val == null)
		{this.ratiosInitialized = false;}
		else
		{this.meanRatio = new Double(props.get("MeanRatio"));}
		
		//Backwards compatible Weights
		val = props.get("MeanWeight");
		if (val != null)
		{this.meanWeight = new Double(props.get("MeanWeight"));}
		
		val = props.get("MinWeight");
		if (val != null)
		{this.minWeight = new Double(props.get("MinWeight"));}
		
		val = props.get("MaxWeight");
		if (val != null)
		{this.maxWeight = new Double(props.get("MaxWeight"));}
		
		val = props.get("NetworkCount");
		if (val != null)
		{this.networkCount = new Integer(props.get("NetworkCount"));}
		
		//Rebuild attribute List
		String value = props.get("AttributeName");
		String[] attributes = value.split(WORDDELIMITER);
		ArrayList<String> attributeList = new ArrayList<String>();
		for (int i = 0; i < attributes.length; i++)
		{
			String curAttribute = attributes[i];
			attributeList.add(curAttribute);
		}
		this.attributeNames = attributeList;
		
			
		
		//Rebuild List of Nodes
		value = props.get("SelectedNodes");
		String[] nodes = value.split(NODEDELIMITER);
		ArrayList<String> nodeNameList = new ArrayList<String>();
		for (int i = 0; i < nodes.length; i++)
		{
			String nodeName = nodes[i];
			nodeNameList.add(nodeName);
		}
		this.selectedNodes = nodeNameList;
		
		//Rebuild CloudWords
		if (props.containsKey("CloudWords")) //handle the empty case
		{
			String value2 = props.get("CloudWords");
			String[] words = value2.split(WORDDELIMITER);
			ArrayList<CloudWordInfo> cloudWordList = new ArrayList<CloudWordInfo>();
			for (int i = 0; i < words.length; i++)
			{
				String wordInfo = words[i];
				CloudWordInfo curInfo = new CloudWordInfo(wordInfo);
				curInfo.setCloudParameters(this);
				cloudWordList.add(curInfo);
			}
			this.cloudWords = cloudWordList;
		}
		else
			this.cloudWords = new ArrayList<CloudWordInfo>();
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
		this.networkPairCounts = new HashMap<String, Integer>();
		this.stringNodeMapping = new HashMap<String, List<String>>();
		
		
		//Retrieve needed variables from parent parameters
		SemanticSummaryParameters networkParams = this.getNetworkParams();
		List<String> networkNodes = networkParams.getNetworkNodes();
		
		//Iterate to retrieve CyNodes
		Iterator<String> iter = networkNodes.iterator();
		while(iter.hasNext())
		{
			String curNodeID = (String)iter.next();
			
			CyNode curNode = Cytoscape.getCyNode(curNodeID);
			
			if (curNode == null)
			{
				Component desktop = Cytoscape.getDesktop();
				JOptionPane.showMessageDialog(desktop, "Node no longer exists: " + curNodeID);
				continue;
			}
			
			for (int i = 0; i < attributeNames.size(); i++)
			{
				String curAttribute = attributeNames.get(i);
				String nodeValue = this.getNodeAttributeVal(curNode, curAttribute);
			
				//String nodeValue = this.getNodeAttributeVal(curNode);
				if (nodeValue == null) // problem with nodes or attributes
					continue;
			
				List<String> wordSet = this.processNodeString(nodeValue);
				String lastWord = ""; //Used for calculating pair counts
	        
				//Iterate through all words
				Iterator<String> wordIter = wordSet.iterator();
				while(wordIter.hasNext())
				{
					String curWord = wordIter.next();
				
					//Check filters
					WordFilter filter = networkParams.getFilter();
					if (!filter.contains(curWord))
					{
						//If this word has not been encountered, or not encountered
						//in this node, add it to our mappings and counts
						HashMap<String, List<String>> curMapping = this.getStringNodeMapping();
				
						//If we have not encountered this word, add it to the mapping
						if (!curMapping.containsKey(curWord))
						{
							curMapping.put(curWord, new ArrayList<String>());
							networkCounts.put(curWord, 0);
						}
					
						//Add node to mapping, update counts
						curMapping.get(curWord).add(curNode.toString());
						Integer num = networkCounts.get(curWord);
						num = num + 1;
						networkCounts.put(curWord, num);
					
					
						//Add to pair counts
						if (!lastWord.equals(""))
						{
							Integer curPairCount = 0;
							String pairName = lastWord + " " + curWord;
						
							if (networkPairCounts.containsKey(pairName))
								curPairCount = networkPairCounts.get(pairName);
						
							curPairCount = curPairCount + 1;
							networkPairCounts.put(pairName, curPairCount);
						}
					
						//Update curWord to be LastWord
						lastWord = curWord;
					
					}//end filter if
				}// word iterator
			}//end attribute iterator
		}//end node iterator
		
		networkNumNodes = networkNodes.size() * attributeNames.size();
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
		this.selectedPairCounts = new HashMap<String, Integer>();
		
		
		List<String> selectedNodes = this.getSelectedNodes();
		
		//Iterate to retrieve Cynodes
		Iterator<String> iter = selectedNodes.iterator();
		while(iter.hasNext())
		{
			
			String curNodeID = (String)iter.next();
			CyNode curNode = Cytoscape.getCyNode(curNodeID);
			
			
			if (curNode == null)
			{
				Component desktop = Cytoscape.getDesktop();
				JOptionPane.showMessageDialog(desktop, "Node no longer exists: " + curNodeID);
				continue;
			}
			
			for (int i = 0; i < attributeNames.size(); i++)
			{
				String curAttribute = attributeNames.get(i);
				String nodeValue = this.getNodeAttributeVal(curNode, curAttribute);
			
				//String nodeValue = this.getNodeAttributeVal(curNode);
			
				if (nodeValue == null) // problem with nodes or attributes
					continue;
			
				List<String> wordSet = this.processNodeString(nodeValue);
				String lastWord = ""; //Used for calculating pair counts
	        
				//Iterate through all words
				Iterator<String> wordIter = wordSet.iterator();
				while(wordIter.hasNext())
				{
					String curWord = wordIter.next();
				
					//Check filters
					WordFilter filter = networkParams.getFilter();
					if (!filter.contains(curWord))
					{
						//Add to selected Counts
					
						Integer curCount = 0; 
					
						if (selectedCounts.containsKey(curWord))
							curCount = selectedCounts.get(curWord);
					
						//Update Count
						curCount = curCount + 1;
					
						//Add updated count to HashMap
						selectedCounts.put(curWord, curCount);
					
						//Add to pair counts
						if (!lastWord.equals(""))
						{
							Integer curPairCount = 0;
							String pairName = lastWord + " " + curWord;
						
							if (selectedPairCounts.containsKey(pairName))
								curPairCount = selectedPairCounts.get(pairName);
						
							curPairCount = curPairCount + 1;
							selectedPairCounts.put(pairName, curPairCount);
						}
					
						//Update curWord to be LastWord
						lastWord = curWord;
					
					}//end filter if
				}// word iterator
			}// end attribute list
		}//end node iterator
		
		//selectedNumNodes = selectedNodes.size();
		selectedNumNodes = selectedNodes.size() * attributeNames.size();
		
		calculateWeights();
		
		selInitialized = true;
	}
	
	/**
	 * Sets the mean weight value to be the average of all ratios if a network normalization
	 * factor of 0 were to be used.  The values are also translated so the min value is 0.
	 */
	public void calculateWeights()
	{
		Double curMin = 0.0;
		Double curMax = 0.0;
		Double total = 0.0;
		int count = 0;
		
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
			Double newNetCount = Math.pow(netCount, 0.0);
			Integer netTotal = this.getNetworkNumNodes();
			Double newNetTotal = Math.pow(netTotal, 0.0);
			
			Double numerator = selCount * newNetTotal;
			Double denominator = selTotal * newNetCount;
			Double ratio = numerator/denominator;
			
			total = total + ratio;
			count = count + 1;
			
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
		
		//store
		this.setMinWeight(curMin);
		this.setMeanWeight(total/count);
		this.setMaxWeight(curMax);
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
		
		//Setup weights if needed
		if (meanWeight == null || minWeight == null || maxWeight == null)
			this.calculateWeights();
		
		//SINGLE COUNTS
		//Clear old counts
		this.ratios = new HashMap<String, Double>();
		
		Double curMin = 0.0;
		Double curMax = 0.0;
		Double total = 0.0;
		int count = 0;
		
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
			Integer netTotal = this.getNetworkNumNodes();
			Double newNetTotal = Math.pow(netTotal, netWeightFactor);
			
			Double numerator = selCount * newNetTotal;
			Double denominator = selTotal * newNetCount;
			Double ratio = numerator/denominator;
			
			ratios.put(curWord, ratio);
			
			total = total + ratio;
			count = count + 1;
			
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
		this.setMeanRatio(total/count);
		
		//PAIR COUNTS
		//Clear old counts
		this.pairRatios = new HashMap<String, Double>();
		
		//Get all word pairs appearing in selected nodes
		words = selectedPairCounts.keySet();
		
		//Iterate through to calculate ratios
		Iterator<String> pairIter = words.iterator();
		while (pairIter.hasNext())
		{
			String curWord = (String)pairIter.next();
			
			/* Ratio: (selCount/selTotal)/((netCount/netTotal)^netWeightFactor)
			 * But, to avoid underflow from small probabilities we calculate it as follows:
			 * (selCount * (netTotal^netWeightFactor))/(selTotal * (netCount^netWeightFactor))
			 * This is the same as the original definition of ratio, just with some
			 * different algebra.
			 */
			Integer selTotal = this.getSelectedNumNodes();
			Integer selPairCount = selectedPairCounts.get(curWord);
			Integer netPairCount = networkPairCounts.get(curWord);
			Double newNetCount = Math.pow(netPairCount, netWeightFactor);
			Integer netTotal = this.getNetworkNumNodes();
			Double newNetTotal = Math.pow(netTotal, netWeightFactor);
			
			Double numerator = selPairCount * newNetTotal;
			Double denominator = selTotal * newNetCount;
			Double ratio = numerator/denominator;
			
			pairRatios.put(curWord, ratio);
		}
		ratiosInitialized = true;
	}
	
	/**
	 * Creates a cloud clustering object and clusters based on the parameter
	 * in this CloudParameters.
	 * @throws  
	 */
	public void calculateFontSizes()
	{
		if (!ratiosInitialized)
			this.updateRatios();
		
		//Clear old fonts
		this.cloudWords = new ArrayList<CloudWordInfo>();
		
		if (displayStyle.equals(CloudDisplayStyles.NO_CLUSTERING))
		{
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
			
			//Sort cloudWords in order by fontsize
			Collections.sort(cloudWords);
		}
		else
		{
			SemanticSummaryClusterBuilder builder = new SemanticSummaryClusterBuilder();
			builder.initialize(this);
			builder.clusterData(this.getClusterCutoff());
			builder.buildCloudWords();
			cloudWords = builder.getCloudWords();
		}
	}
	
	
	/**
	 * Calculates the font for a given word by using its ratio, the max and
	 * min ratios as well as the max and min font size in the parent 
	 * parameters object.  Assumes ratios are up to date and that word
	 * is in the selected nodes.
	 * @return Integer - the calculated font size for the specified word.
	 */
	public Integer calculateFontSize(String aWord)
	{
		//Sanity check
		if (!ratios.containsKey(aWord))
			return 0;
		
		Double ratio = ratios.get(aWord);
				
		//Zeroed mapping
		//Get zeroed values for calculations
		Double zeroedMinWeight = minWeight - minWeight;
		Double zeroedMeanWeight = meanWeight - minWeight;
		Double zeroedMaxWeight = maxWeight - minWeight;
		
		Double zeroedMinRatio = minRatio - minRatio;
		Double zeroedMeanRatio = meanRatio - minRatio;
		Double zeroedMaxRatio = maxRatio - minRatio;
		
		Double zeroedRatio = ratio - minRatio;
		
		Double newRatio = zeroedRatio * zeroedMeanWeight / zeroedMeanRatio;
		
		//Weighted Average
		Integer maxFont = networkParams.getMaxFont();
		Integer minFont = networkParams.getMinFont();
		
		//Check if maxRatio and minRatio are the same
		if (zeroedMaxRatio.equals(zeroedMinRatio))
			return (minFont + (maxFont - minFont)/2);
		
		Double slope = (maxFont - minFont)/(zeroedMaxWeight - zeroedMinWeight);
		Double yIntercept = maxFont - (slope*zeroedMaxWeight); //maxRatio maps to maxFont
		
		//Round up to nearest Integer
		long temp = Math.round((slope*newRatio) + yIntercept);
		Integer fontSize = Math.round(temp);
		
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
		
		//Network Weight Stuff
		SliderBarPanel panel = inputPanel.getSliderBarPanel();
		JSlider slider = panel.getSlider();
		Double netNorm = slider.getValue()/panel.getPrecision();
		this.setNetWeightFactor(netNorm);
		Boolean selected = inputPanel.getUseNetworkCounts().isSelected();
		this.useNetNormal = selected;
		
		
		//Attribute
		Object[] attributes = inputPanel.getAttributeList().getSelectedValues();
		ArrayList<String> attributeList = new ArrayList<String>();
		
		for (int i = 0; i < attributes.length; i++)
		{
			Object curAttribute = attributes[i];
			
			if (curAttribute instanceof String)
			{
				attributeList.add((String) curAttribute);
			}

			this.setAttributeNames(attributeList);
		}
			
		//Max Words
		JFormattedTextField maxWordsTextField = inputPanel.getMaxWordsTextField();
		
		Number value = (Number) maxWordsTextField.getValue();
		if ((value != null) && (value.intValue() >= 0))
		{
			setMaxWords(value.intValue()); 
		}
		else
		{
			maxWordsTextField.setValue(defaultMaxWords);
			setMaxWords(defaultMaxWords);
			String message = "The maximum number of words to display must be greater than or equal to 0.";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, "Parameter out of bounds", JOptionPane.WARNING_MESSAGE);
		}
		
		//Cluster Cutoff
		JFormattedTextField clusterCutoffTextField = inputPanel.getClusterCutoffTextField();
		
		value = (Number) clusterCutoffTextField.getValue();
		if ((value != null) && (value.doubleValue() >= 0.0))
		{
			setClusterCutoff(value.doubleValue()); //sets all necessary flags
		}
		else
		{
			clusterCutoffTextField.setValue(defaultClusterCutoff);
			setClusterCutoff(defaultClusterCutoff);
			String message = "The cluster cutoff must be greater than or equal to 0";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, "Parameter out of bounds", JOptionPane.WARNING_MESSAGE);
		}
		
		//Style
		Object style = inputPanel.getCMBStyle().getSelectedItem();
		if (style instanceof String)
			setDisplayStyle((String) style);
		else
		{
			setDisplayStyle(defaultStyle);
			inputPanel.getCMBStyle().setSelectedItem(defaultStyle);
			String message = "You must select one of the available styles.";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, "Parameter out of bounds", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * String representation of CloudParameters.
	 * It is used to store the persistent Attributes as a property file.
	 * @return - String representation of this object
	 */
	public String toString()
	{
		StringBuffer paramVariables = new StringBuffer();
		
		paramVariables.append("CloudName\t" + cloudName + "\n");
		paramVariables.append("DisplayStyle\t" + displayStyle + "\n");
		
		//List of Nodes as a comma delimited list
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < selectedNodes.size(); i++)
		{
			output.append((selectedNodes.get(i)).toString() + NODEDELIMITER);
		}
		paramVariables.append("SelectedNodes\t" + output.toString() + "\n");
		
		
		//List of attributes as a delimited list
		output = new StringBuffer();
		for (int i = 0; i < attributeNames.size(); i++)
		{
			output.append(attributeNames.get(i) + WORDDELIMITER);
		}
		paramVariables.append("AttributeName\t" + output.toString() + "\n");
		
		paramVariables.append("NetworkNumNodes\t" + networkNumNodes + "\n");
		paramVariables.append("SelectedNumNodes\t" + selectedNumNodes + "\n");
		paramVariables.append("NetWeightFactor\t" + netWeightFactor + "\n");
		paramVariables.append("ClusterCutoff\t" + clusterCutoff + "\n");
		paramVariables.append("CountInitialized\t" + countInitialized + "\n");
		paramVariables.append("SelInitialized\t" + selInitialized + "\n");
		paramVariables.append("RatiosInitialized\t" + ratiosInitialized + "\n");
		paramVariables.append("MinRatio\t" + minRatio + "\n");
		paramVariables.append("MaxRatio\t" + maxRatio + "\n");
		paramVariables.append("MaxWords\t" + maxWords + "\n");
		paramVariables.append("MeanRatio\t" + meanRatio + "\n");
		paramVariables.append("MeanWeight\t" + meanWeight + "\n");
		paramVariables.append("MaxWeight\t" + maxWeight + "\n");
		paramVariables.append("MinWeight\t" + minWeight + "\n");
		paramVariables.append("CloudNum\t" + cloudNum + "\n");
		paramVariables.append("UseNetNormal\t" + useNetNormal + "\n");
		paramVariables.append("NetworkCount\t" + networkCount + "\n");
		
		//List of Nodes as a comma delimited list
		StringBuffer output2 = new StringBuffer();
		for (int i = 0; i < cloudWords.size(); i++)
		{
			output2.append(cloudWords.get(i).toString() + WORDDELIMITER);
		}
		
		paramVariables.append("CloudWords\t" + output2.toString() + "\n");
		
		return paramVariables.toString();
	}
	
	/**
	 * Goes through Hashmap and prints all of the objects it contains.
	 * @param map - any type of hashmap
	 * @return string representation of the hash with "key tab object newline" representation
	 */
	public String printHashMap(HashMap map)
	{
		StringBuffer result = new StringBuffer();
		
		for (Iterator iter = map.keySet().iterator(); iter.hasNext(); )
		{
			Object key = iter.next();
			Object value = map.get(key);
			StringBuffer stringValue = new StringBuffer();
			
			if (value instanceof List)
			{
				List valueList = (List)value;
				for(int i = 0; i < valueList.size(); i++)
				{
					String name = (String)valueList.get(i);
					stringValue.append(name + NODEDELIMITER);
				}
			}
			else if (value instanceof Integer)
			{
				stringValue.append(value.toString());
			}
			else if (value instanceof Double)
			{
				stringValue.append(value.toString());
			}
			else
				stringValue.append(value.toString());
			
			result.append(key.toString() + "\t" + stringValue.toString() + "\n");
		}
		return result.toString();
	}
	
	
	/**
	 * This method repopulates a properly specified Hashmap from the given file and type.
	 * @param fileInput - file name where the has map is stored
	 * @param type - the type of hashmap in the file.  The hashes are repopulated
	 * based on the property file stored in the session file.  The property file
	 * specifieds the type of objects contained in each file and this is needed in order
	 * to create the proper has in the current set of parameters.
	 * types are Counts(1) and Mapping(2)
	 * @return properly constructed Hashmap repopulated from the specified file.
	 */
	public HashMap repopulateHashmap(String fileInput, int type)
	{
		//Hashmap to contain values from the file
		HashMap newMap;
		
		//Counts (network or selected)
		if (type == 1)
			newMap = new HashMap<String, Integer>();
		//Mapping
		else if (type == 2)
			newMap = new HashMap<String, List<String>>();
		//Ratios
		else if (type == 3)
			newMap = new HashMap<String, Double>();
		else
			newMap = new HashMap();
		
		//Check that we have input
		if (!fileInput.equals(""))
		{
			String [] lines = fileInput.split("\n");
		
			for (int i = 0; i < lines.length; i++)
			{
				String line = lines[i];
				String [] tokens = line.split("\t");
			
				//the first token is the key and the rest is the object
				//Different types have different data
			
				//Counts
				if (type == 1)
					newMap.put(tokens[0], Integer.parseInt(tokens[1]));
			
				//Mapping
				if (type == 2)
				{
					//Create List
					String [] nodes = tokens[1].split(NODEDELIMITER);
					ArrayList nodeNames = new ArrayList<String>();
					for (int j =0; j < nodes.length; j++)
						nodeNames.add(nodes[j]);
				
					newMap.put(tokens[0], nodeNames);
				}
			
				//Ratios
				if (type == 3)
					newMap.put(tokens[0], Double.parseDouble(tokens[1]));
			}//end line loop
		}//end if data exists check
		return newMap;
	}
	
	/**
	 * This method takes in the ID of a node and returns the string that is associated
	 * with that node and the current attribute of this CloudParameters.
	 * @param CyNode - node we are interested in 
	 * @param String - name of the attribute we are interested in
	 * @return String - value stored in the current attribute for the given node.
	 */
	private String getNodeAttributeVal(CyNode curNode, String attributeName)
	{
		//Retrieve value based on attribute
		String nodeValue = "";
		
		//if we should use the ID
		if (attributeName.equals("nodeID"))
		{
			nodeValue = curNode.toString();
		}
		
		//Use a different attribute
		else
		{
			CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
			
			if (cyNodeAttrs.getType(attributeName)== CyAttributes.TYPE_STRING)
			{
				nodeValue = cyNodeAttrs.getStringAttribute(curNode.getIdentifier(), attributeName);
			}
			
			else if (cyNodeAttrs.getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST)
			{
				List attribute = cyNodeAttrs.getListAttribute(curNode.getIdentifier(), attributeName);
				
				if (attribute == null)
					return null;
				
				else
				{
					for (Iterator iter = attribute.iterator(); iter.hasNext();)
					{
						Object curObj = iter.next();
						if (curObj instanceof String)
						{
							//Turn list into space separated string
							String curObjString = (String)curObj;
							nodeValue = nodeValue + curObjString + " ";
						}//
					}
				}
			}
			else
			{
				//Don't currently handle non string attributes
				//This code should currently never be accessed
				Component desktop = Cytoscape.getDesktop();
				
				JOptionPane.showMessageDialog(desktop, 
				"Current implementation does not handle non-String attributes.");
				return null;
			}//end else
		}//end else
		
		return nodeValue;
	}//end method
	
	
	/**
	 * This method takes in a string from a node and processes it to lower case, removes
	 * punctuation and separates the words into a non repeated list.
	 * @param String from a node that we are processing.
	 * @return Set of distinct words.
	 */
	private List<String> processNodeString(String nodeValue)
	{
		//Only deal with lower case
		nodeValue = nodeValue.toLowerCase();
		
		//replace all punctuation with white spaces except ' and -
		//nodeValue = nodeValue.replaceAll("[[\\p{Punct}] && [^'-]]", " ");
		String controlString = Character.toString(controlChar);
		
		//Remove all standard delimiters and replace with controlChar
		WordDelimiters delims = this.getNetworkParams().getDelimiter();
		nodeValue = nodeValue.replaceAll(delims.getRegex(),controlString);
        
		//Remove all user stated delimiters and replace with controlChar
		for (Iterator<String> iter = delims.getUserDelims().iterator(); iter.hasNext();)
		{
			String userDelim = iter.next();
			nodeValue = nodeValue.replaceAll(userDelim, controlString);
		}
		
        //Separate into non repeating set of words
		List<String> wordSet = new ArrayList<String>();
		StringTokenizer token = new StringTokenizer(nodeValue, controlString);
        while (token.hasMoreTokens())
        {
        	String a = token.nextToken();
        	if (!wordSet.contains(a))
        		wordSet.add(a);
        }
        
        return wordSet;
	}
	
	/**
	 * Compares two CloudParameters objects based on the order in which they
	 * were created.
	 * @param CloudParameters object to compare this object to
	 * @return
	 */
	public int compareTo(CloudParameters compare) 
	{	
		Integer thisCount = this.getCloudNum();
		Integer compareCount = compare.getCloudNum();
		return thisCount.compareTo(compareCount);
	}
	
	/**
	 * Returns the name for the next network for this cloud.
	 * @return String - name of the next cloud
	 */
	public String getNextNetworkName()
	{
		String name = networkParams.getNetworkName() + "-" + cloudName + "-" + NETWORKNAME + SEPARATER + networkCount;
		networkCount++;
		
		return name;
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
	
	public ArrayList<String> getAttributeNames()
	{
		return attributeNames;
	}
	
	public void setAttributeNames(ArrayList<String> names)
	{
		//Check if we need to reset flags
		Boolean changed = false;
		if (names.size() != attributeNames.size())
			changed = true;
		else
		{
			for (int i = 0; i < names.size(); i++)
			{
				String curAttribute = names.get(i);
				
				if (!attributeNames.contains(curAttribute))
				{
					changed = true;
					continue;
				}
			}
		}
		
		//Set flags
		if (changed)
		{
			countInitialized = false;
			selInitialized = false;
			ratiosInitialized = false;
		}
		
		//Set to new value
		attributeNames = names;
	}
	
	public void addAttributeName(String name)
	{
		if (!attributeNames.contains(name))
		{
			attributeNames.add(name);
			countInitialized = false;
			selInitialized = false;
			ratiosInitialized = false;
		}
	}
	

	
	public SemanticSummaryParameters getNetworkParams()
	{
		return networkParams;
	}
	
	public void setNetworkParams(SemanticSummaryParameters params)
	{
		networkParams = params;
	}
	
	public List<String> getSelectedNodes()
	{
		return selectedNodes;
	}
	
	public void setSelectedNodes(List<String> nodes)
	{
		selectedNodes = nodes;
		
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
	
	public HashMap<String, List<String>> getStringNodeMapping()
	{
		return stringNodeMapping;
	}
	
	public void setStringNodeMapping(HashMap<String, List<String>> mapping)
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
	
	public HashMap<String,Integer> getSelectedPairCounts()
	{
		return selectedPairCounts;
	}
	
	public void setSelectedPairCounts(HashMap<String, Integer> counts)
	{
		selectedPairCounts = counts;
	}
	
	public HashMap<String,Integer> getNetworkPairCounts()
	{
		return networkPairCounts;
	}
	
	public void setNetworkPairCounts(HashMap<String, Integer> counts)
	{
		networkPairCounts = counts;
	}
	
	public HashMap<String,Double> getRatios()
	{
		return ratios;
	}
	
	public void setRatios(HashMap<String, Double> r)
	{
		ratios = r;
	}
	
	public HashMap<String,Double> getPairRatios()
	{
		return pairRatios;
	}
	
	public void setPairRatios(HashMap<String, Double> r)
	{
		pairRatios = r;
	}
	
	public ArrayList<CloudWordInfo> getCloudWordInfoList()
	{
		return cloudWords;
	}
	
	public void setCloudWordInfoList(ArrayList<CloudWordInfo> words)
	{
		cloudWords = words;
	}
	
	public Integer getNetworkNumNodes()
	{
		return networkNumNodes;
	}

	public void setNetworkNumNodes(Integer num)
	{
		networkNumNodes = num;
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
	
	public Double getMeanRatio()
	{
		return meanRatio;
	}
	
	public void setMeanRatio(Double ratio)
	{
		meanRatio = ratio;
	}
	
	public Double getMinWeight()
	{
		return minWeight;
	}
	
	public void setMinWeight(Double val)
	{
		minWeight = val;
	}
	
	public Double getMaxWeight()
	{
		return maxWeight;
	}
	
	public void setMaxWeight(Double val)
	{
		maxWeight = val;
	}
	
	public Double getMeanWeight()
	{
		return meanWeight;
	}
	
	public void setMeanWeight(Double val)
	{
		meanWeight = val;
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
		//Reset flags if net Weight changes
		if (!netWeightFactor.equals(val))
			ratiosInitialized = false;
		
		netWeightFactor = val;
	}
	
	public Double getClusterCutoff()
	{
		return clusterCutoff;
	}
	
	public void setClusterCutoff(Double val)
	{
		clusterCutoff = val;
	}
	
	public Integer getMaxWords()
	{
		return maxWords;
	}
	
	public void setMaxWords(Integer val)
	{
		maxWords = val;
	}
	
	public Double getDefaultNetWeight()
	{
		return defaultNetWeight;
	}
	
	public String getDefaultAttName()
	{
		return defaultAttName;
	}
	
	public Integer getDefaultMaxWords()
	{
		return defaultMaxWords;
	}
	
	public Double getDefaultClusterCutoff()
	{
		return defaultClusterCutoff;
	}
	
	public String getDefaultDisplayStyle()
	{
		return defaultStyle;
	}
	
	public Integer getCloudNum()
	{
		return cloudNum;
	}
	
	public void setCloudNum(Integer num)
	{
		cloudNum = num;
	}	
	
	public String getDisplayStyle()
	{
		return displayStyle;
	}
	
	public void setDisplayStyle(String style)
	{
		displayStyle = style;
	}
	
	public boolean getUseNetNormal()
	{
		return useNetNormal;
	}
	
	public void setUseNetNormal(boolean val)
	{
		useNetNormal = val;
	}
	
}
