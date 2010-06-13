/*
 File: SemanticSummaryParameters.java

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
import cytoscape.CyNode;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	private HashMap<String, CloudParameters> clouds; //list of network's clouds
	private List<String> nodeList; //list of the IDs of all the nodes in the network
	
	//Name creation variables
	private Integer cloudCount = 1;
	private static final String CLOUDNAME = "Cloud";
	private static final String SEPARATER = "_";
	
	
	//Font Size Values
	private static final Integer MINFONTSIZE = 12; 
	private static final Integer MAXFONTSIZE = 56;
	
	//String Delimeters
	private static final String NODEDELIMITER = "SSParamNodeDelimiter";
	
	//CONSTRUCTORS
	
	/**
	 * Default constructor to create a fresh instance
	 */
	public SemanticSummaryParameters()
	{
		this.clouds = new HashMap<String,CloudParameters>();
		this.nodeList = new ArrayList<String>();
	}
	
	/**
	 * Constructor to create SemanticSummaryParameters from a cytoscape property file
	 * while restoring a session.  Property file is created when the session is saved.
	 * @param propFile - the name of the property file as a String
	 */
	public SemanticSummaryParameters(String propFile)
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
		
		this.networkName = props.get("NetworkName");
		this.cloudCount = new Integer(props.get("CloudCount"));
		
		//Rebuild List
		String value = props.get("NodeList");
		String[] nodes = value.split(NODEDELIMITER);
		ArrayList<String> nodeNameList = new ArrayList<String>();
		for (int i = 0; i < nodes.length; i++)
		{
			String nodeName = nodes[i];
			nodeNameList.add(nodeName);
		}
		this.nodeList = nodeNameList;
	}
	
	
	//METHODS
	
	//DATA MANIPULATIONS
	/**
	 * Adds a new cloud to the SemanticSummary HashMap of clouds for this
	 * network.
	 * @param String - the name of the new cloud.
	 * @param CloudParameters - parameters for this cloud.
	 */
	public void addCloud(String name, CloudParameters params)
	{
		if (!clouds.containsKey(name))
		{
			clouds.put(name, params);
		}
	}
	
	/**
	 * Removes a cloud from the HashMap of clouds for this network.
	 * @param String - name of the cloud to remove.
	 */
	public void removeCloud(String name)
	{
		if (clouds.containsKey(name))
			clouds.remove(name);
	}
	
	/**
	 * Returns true if the particular cloud named is contained in this
	 * SemanticSummaryParameters object.
	 * @return true if the specified cloud is contained in this object.
	 */
	public boolean containsCloud(String name)
	{
		if (clouds.containsKey(name))
			return true;
		else
			return false;
	}
	
	/**
	 * Returns the specified cloudParameters if it is contained in this object.
	 * Or returns, null if the cloud is not contained.
	 * @param String - name of the CloudParameters to return.
	 * @return CloudParameters associated with the given name.
	 */
	public CloudParameters getCloud(String name)
	{
		if (this.containsCloud(name))
			return clouds.get(name);
		else
			return null;	
	}
	/**
	 * Tells all the contained clouds that the network has changed and that
	 * they need to re-initialize.
	 */
	public void networkChanged()
	{
		Set<String> cloudNames = clouds.keySet();
		Iterator<String> iter = cloudNames.iterator();
		while (iter.hasNext())
		{
			String curCloud = iter.next();
			CloudParameters cloudParams = clouds.get(curCloud);
			cloudParams.setCountInitialized(false);
			cloudParams.setSelInitialized(false);
			cloudParams.setRatiosInitialized(false);
		}
	}
	
	/**
	 * Returns the name for the next cloud for this network.
	 * @return String - name of the next cloud
	 */
	public String getNextCloudName()
	{
		String name = CLOUDNAME + SEPARATER + cloudCount;
		cloudCount++;
		
		return name;
	}
	
	/**
	 * String representation of SemanticSummaryParameters.
	 * It is used to store the persistent Attributes as a property file.
	 * @return - String representation of this object
	 */
	public String toString()
	{
		StringBuffer paramVariables = new StringBuffer();
		
		paramVariables.append("NetworkName\t" + networkName + "\n");
		paramVariables.append("CloudCount\t" + cloudCount + "\n");
		
		//List of Nodes as a comma delimited list
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < nodeList.size(); i++)
		{
			output.append(nodeList.get(i).toString() + NODEDELIMITER);
		}
		
		paramVariables.append("NodeList\t" + output.toString() + "\n");
		
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
			result.append(key.toString() + "\t" + map.get(key).toString() + "\n");
		}
		return result.toString();
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
	
	public HashMap<String, CloudParameters> getClouds()
	{
		return clouds;
	}
	
	public void setClouds(HashMap<String, CloudParameters> cloudMap)
	{
		clouds = cloudMap;
	}
	
	public List<String> getNetworkNodes()
	{
		return nodeList;
	}
	
	public void setNetworkNodes(List<String> nodes)
	{
		nodeList = nodes;
	}
	
	public Integer getMaxFont()
	{
		return MAXFONTSIZE;
	}
	
	public Integer getMinFont()
	{
		return MINFONTSIZE;
	}
	
	public Integer getCloudCount()
	{
		return cloudCount;
	}
	
	public String getCloudName()
	{
		return CLOUDNAME;
	}
	
	public String getSeparater()
	{
		return SEPARATER;
	}
}
