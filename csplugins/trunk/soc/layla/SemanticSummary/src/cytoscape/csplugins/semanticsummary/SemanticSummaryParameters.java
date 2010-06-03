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
	private List<CyNode> nodeList; //list of all CyNodes in network
	private Integer networkNumNodes;
	
	//Name creation variables
	private Integer cloudCount = 1;
	private static final String CLOUDNAME = "Cloud";
	private static final String SEPARATER = "_";
	
	
	//private boolean isInitialized;//set to true when original data counts set
	
	//DO THESE GO HERE??
	private static final Integer NUMBINS = 7; //NEED TO DECIDE ON THIS
	private static final Integer MINFONTSIZE = 12; //TODO
	private static final Integer MAXFONTSIZE = 56; //TODO
	
	//CONSTRUCTORS
	
	/**
	 * Default constructor to create a fresh instance
	 */
	public SemanticSummaryParameters()
	{
		this.clouds = new HashMap<String,CloudParameters>();
		this.nodeList = new ArrayList<CyNode>();
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
	
	public List<CyNode> getNetworkNodes()
	{
		return nodeList;
	}
	
	public void setNetworkNodes(List<CyNode> nodes)
	{
		nodeList = nodes;
	}
	
	public Integer getNetworkNumNodes()
	{
		return networkNumNodes;
	}

	public void setNetworkNumNodes(Integer num)
	{
		networkNumNodes = num;
	}
	
	public Integer getNumBins()
	{
		return NUMBINS;
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
