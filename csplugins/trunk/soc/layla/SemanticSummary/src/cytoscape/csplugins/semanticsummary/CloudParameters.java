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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;

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

public class CloudParameters 
{

	//VARIABLES
	private String cloudName;
	private String attributeName;
	
	private SemanticSummaryParameters networkParams; //parent network
	
	private List<String> selectedNodes; //set of selected nodes for cloud
	private Integer selectedNumNodes;
	private Integer networkNumNodes;
	
	private HashMap<String, List<String>> stringNodeMapping;
	private HashMap<String, Integer> networkCounts; // counts for whole network
	private HashMap<String, Integer> selectedCounts; // counts for selected nodes
	private HashMap<String, Double> ratios;
	private ArrayList<CloudWordInfo> cloudWords;
	
	private WordFilter filter;
	
	private Double netWeightFactor;
	
	private Double minRatio;
	private Double maxRatio;
	
	private boolean countInitialized = false; //true when network counts are initialized
	private boolean selInitialized = false; //true when selected counts initialized
	private boolean ratiosInitialized = false; //true when ratios are computed
	
	//String Delimeters
	private static final String NODEDELIMITER = "CloudParamNodeDelimiter";
	
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
		this.ratios = new HashMap<String, Double>();
		this.cloudWords = new ArrayList<CloudWordInfo>();
		this.filter = new WordFilter();
		
		this.netWeightFactor = SemanticSummaryManager.getInstance().getDefaultNetWeight();
		this.attributeName = SemanticSummaryManager.getInstance().getDefaultAttName();
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
		this.attributeName = props.get("AttributeName");
		this.selectedNumNodes = new Integer(props.get("SelectedNumNodes"));
		this.networkNumNodes = new Integer(props.get("NetworkNumNodes"));
		this.netWeightFactor = new Double(props.get("NetWeightFactor"));
		this.countInitialized = Boolean.parseBoolean(props.get("CountInitialized"));
		this.selInitialized = Boolean.parseBoolean(props.get("SelInitialized"));
		this.ratiosInitialized = Boolean.parseBoolean(props.get("RatiosInitialized"));
		this.maxRatio = new Double(props.get("MaxRatio"));
		this.minRatio = new Double(props.get("MinRatio"));
		
		//Rebuild List
		String value = props.get("SelectedNodes");
		String[] nodes = value.split(NODEDELIMITER);
		ArrayList<String> nodeNameList = new ArrayList<String>();
		for (int i = 0; i < nodes.length; i++)
		{
			String nodeName = nodes[i];
			nodeNameList.add(nodeName);
		}
		this.selectedNodes = nodeNameList;
		
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
			
			String nodeValue = this.getNodeAttributeVal(curNode);
			if (nodeValue == null) // problem with nodes or attributes
				continue;
			
			Set<String> wordSet = this.processNodeString(nodeValue);
	        
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
				return;
			}
			
			String nodeValue = this.getNodeAttributeVal(curNode);
			if (nodeValue == null) // problem with nodes or attributes
				return;
			
			Set<String> wordSet = this.processNodeString(nodeValue);
	        
	        
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
			Integer netTotal = this.getNetworkNumNodes();
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
		
		//Network Weight Value
		JFormattedTextField netWeightTextField = inputPanel.getNetWeightTextField();
		
		Number value = (Number) netWeightTextField.getValue();
		if ((value != null) && (value.doubleValue() >= 0.0) && (value.doubleValue() <= 1))
		{
			setNetWeightFactor(value.doubleValue()); //sets all necessary flags
		}
		else
		{
			Double defaultNetWeight = SemanticSummaryManager.getInstance().getDefaultNetWeight();
			netWeightTextField.setValue(defaultNetWeight);
			setNetWeightFactor(defaultNetWeight);
			String message = "The network weight factor must be greater than or equal to 0 and less than or equal to 1";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, "Parameter out of bounds", JOptionPane.WARNING_MESSAGE);
		}
		
		//Attribute
		Object attribute = inputPanel.getCMBAttributes().getSelectedItem();
		if (attribute instanceof String)
			setAttributeName((String) attribute);
		else
		{
			setAttributeName(SemanticSummaryManager.getInstance().getDefaultAttName());
			inputPanel.getCMBAttributes().setSelectedItem(attributeName);
			String message = "You must select a valid String attribute or use the node ID.";
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
		paramVariables.append("AttributeName\t" + attributeName + "\n");
		
		//List of Nodes as a comma delimited list
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < selectedNodes.size(); i++)
		{
			output.append((selectedNodes.get(i)).toString() + NODEDELIMITER);
		}
		
		paramVariables.append("SelectedNodes\t" + output.toString() + "\n");
		
		paramVariables.append("NetworkNumNodes\t" + networkNumNodes + "\n");
		paramVariables.append("SelectedNumNodes\t" + selectedNumNodes + "\n");
		paramVariables.append("NetWeightFactor\t" + netWeightFactor + "\n");
		paramVariables.append("CountInitialized\t" + countInitialized + "\n");
		paramVariables.append("SelInitialized\t" + selInitialized + "\n");
		paramVariables.append("RatiosInitialized\t" + ratiosInitialized + "\n");
		paramVariables.append("MinRatio\t" + minRatio + "\n");
		paramVariables.append("MaxRatio\t" + maxRatio + "\n");
		
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
		return newMap;
	}
	
	/**
	 * This method takes in the ID of a node and returns the string that is associated
	 * with that node and the current attribute of this CloudParameters.
	 * @param CyNode - node we are interested in 
	 * @return String - value stored in the current attribute for the given node.
	 */
	private String getNodeAttributeVal(CyNode curNode)
	{
		//Retrieve value based on attribute
		String nodeValue = "";
		
		//if we should use the ID
		if (this.attributeName.equals("nodeID"))
		{
			nodeValue = curNode.toString();
		}
		
		//Use a different attribute
		else
		{
			CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
			Object attribute = cyNodeAttrs.getAttribute(curNode.getIdentifier(), attributeName);
			
			if (attribute == null)//nothing set for this attribute
				return null;
			
			//String
			if (attribute instanceof String)
				nodeValue = (String)attribute;
			
			//List of Strings
			else if (attribute instanceof List)
			{
				List attList = (List)attribute;
				for (Iterator iter = attList.iterator(); iter.hasNext();)
				{
					Object curObj = iter.next();
					if (curObj instanceof String)
					{
						//Turn list into space separated string
						String curObjString = (String)curObj;
						nodeValue = nodeValue + curObjString + " ";
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
	 * punctuation and separates the words into a set.
	 * @param String from a node that we are processing.
	 * @return Set of distinct words.
	 */
	private Set<String> processNodeString(String nodeValue)
	{
		//Only deal with lower case
		nodeValue = nodeValue.toLowerCase();
		
		//replace all punctuation with white spaces except ' and -
		nodeValue = nodeValue.replaceAll("[[\\p{Punct}] && [^'-]]", " ");
        
        //Separate into non repeating set of words
		Set<String> wordSet = new HashSet<String>();
        StringTokenizer token = new StringTokenizer(nodeValue);
        while (token.hasMoreTokens())
        {
        	String a = token.nextToken();
        	wordSet.add(a);
        }
        
        return wordSet;
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
	
	
	public String getAttributeName()
	{
		return attributeName;
	}
	
	public void setAttributeName(String name)
	{
		//Set flags if it is changing
		if (!attributeName.equals(name))
		{
			countInitialized = false;
			selInitialized = false;
			ratiosInitialized = false;
		}
		
		attributeName = name;

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
	
	public Integer getNetworkNumNodes()
	{
		return networkNumNodes;
	}

	public void setNetworkNumNodes(Integer num)
	{
		networkNumNodes = num;
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
		//Reset flags if net Weight changes
		if (!netWeightFactor.equals(val))
			ratiosInitialized = false;
		
		netWeightFactor = val;
	}
	
}
