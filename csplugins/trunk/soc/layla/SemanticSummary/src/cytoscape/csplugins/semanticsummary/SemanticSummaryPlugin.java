/*
 File: SemanticSummaryPlugin.java

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import cytoscape.data.readers.TextFileReader;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import ding.view.DGraphView;

/**
 * This class defines the Semantic Summary Plugin.
 * It allows a user to create a tag cloud which displays semantic 
 * information from the selected nodes in a given network.
 * 
 * @author Layla Oesper
 * @version 1.0
 */

public class SemanticSummaryPlugin extends CytoscapePlugin 
{
	
	//Variables
	private static final String netNameSep = "SemanticSummaryNetworkSeparator";
	private static final String cloudNameSep = "SemanticSummaryCloudSeparator";
	
	//CONSTRUCTORS
	
	/**
	 * SemanticSummaryPlugin Constructor
	 */
	
	public SemanticSummaryPlugin()
	{
		//New actions for response to menu selections
		SemanticSummaryPluginAction settings = new SemanticSummaryPluginAction();
		CreateCloudAction create = new CreateCloudAction();
		create.setPreferredMenu("Plugins.WordCloud");
		settings.setPreferredMenu("Plugins.WordCloud");
	
		//Add to Plugin Menu
		Cytoscape.getDesktop().getCyMenus().addAction(create);
		Cytoscape.getDesktop().getCyMenus().addAction(settings);
		
		//Add to right click menus
		
		//Newly created networks - right click stuff
		SemanticSummaryNetworkListener netListener = 
			new SemanticSummaryNetworkListener();
		
		Cytoscape.getSwingPropertyChangeSupport().
		addPropertyChangeListener(netListener);
		
		//Loaded networks - right click stuff
		Set<CyNetwork> networkSet = Cytoscape.getNetworkSet();
		
		for (Iterator<CyNetwork> iter = networkSet.iterator(); iter.hasNext();)
		{
			CyNetwork network = (CyNetwork)iter.next();
			
			SemanticSummaryNodeContextMenuListener nodeMenuListener = 
				new SemanticSummaryNodeContextMenuListener();
			
			((DGraphView)Cytoscape.getNetworkView(network.getIdentifier()))
			.addNodeContextMenuListener(nodeMenuListener);
		}
	}
	

	
	//METHODS
	
	/**
	 * Provides a description of the SemanticSummaryPlugin
	 * @return String that describes the SemanticSummaryPlugin
	 */
	public String describe()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("For every node in the current network, this plugin ");
		sb.append("displays a word cloud of the selected ");
		sb.append("cyNode attribute.  The node ID is the defuault ");
		sb.append("attribute.");
		return sb.toString();
	}
	
	public void onCytoscapeExit()
	{
		//Needs to be overridden here
	}
	
	/**
	 * SaveSessionStateFiles collects all the data stored in the Semantic 
	 * Summary data structures and creates property files for each network 
	 * listing the variables needed to rebuild the Semantic Summary.
	 * 
	 * @param pFileList - pointer to the set of files to be added to the session
	 */
	public void saveSessionStateFiles(List<File> pFileList)
	{
		//Create an empty file on system temp directory
		String tmpDir = System.getProperty("java.io.tmpdir");
		System.out.println("java.io.tmpdir: [" + tmpDir + "]");
		
		//get the networks
		HashMap<String, SemanticSummaryParameters> networks = 
			SemanticSummaryManager.getInstance().getCyNetworkList();
		
		//Create a props file for each network
		for (Iterator<String> iter = networks.keySet().iterator(); iter.hasNext();)
		{
			String networkID = iter.next().toString();
			SemanticSummaryParameters params = networks.get(networkID);
			String networkName = params.getNetworkName();
			
			//Update the network if it has changed
			CyNetwork network = Cytoscape.getNetwork(networkID);
			if (params.networkHasChanged(network));
				params.updateParameters(network);
			
			//property file
			File session_prop_file = new File(tmpDir, netNameSep + networkName + netNameSep + ".props");
			
			//write out files.
			try 
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter(session_prop_file));
				writer.write(params.toString());
				writer.close();
				
				File current_filter = new File(tmpDir, netNameSep + networkName + netNameSep + ".FILTER.txt");
				BufferedWriter filterWriter = new BufferedWriter(new FileWriter(current_filter));
				filterWriter.write(params.getFilter().toString());
				filterWriter.close();
				pFileList.add(current_filter);
				
				File current_delimiter = new File(tmpDir, netNameSep + networkName + netNameSep + ".DELIMITER.txt");
				BufferedWriter delimiterWriter = new BufferedWriter(new FileWriter(current_delimiter));
				delimiterWriter.write(params.getDelimiter().toString());
				delimiterWriter.close();
				pFileList.add(current_delimiter);
				
				//Loop on Clouds
				if (!params.getClouds().isEmpty())
				{
					HashMap<String, CloudParameters> all_clouds = params.getClouds();
					
					for (Iterator<String> j=all_clouds.keySet().iterator(); j.hasNext();)
					{
						String cloud_name = j.next().toString();
						
						CloudParameters cloud = all_clouds.get(cloud_name);
						
						//File for CloudParameters
						File current_cloud = new File(tmpDir, netNameSep + networkName + netNameSep + 
								cloudNameSep + cloud_name + cloudNameSep + ".CLOUDS.txt");
						BufferedWriter subCloud1Writer = new BufferedWriter(new FileWriter(current_cloud));
						subCloud1Writer.write(cloud.toString());
						subCloud1Writer.close();
						pFileList.add(current_cloud);
						
						//File for String/Word Mapping
						File current_mapping = new File(tmpDir, netNameSep + networkName + netNameSep + 
								cloudNameSep + cloud_name + cloudNameSep + ".MAPPING.txt");
						BufferedWriter subCloud2Writer = new BufferedWriter(new FileWriter(current_mapping));
						subCloud2Writer.write(cloud.printHashMap(cloud.getStringNodeMapping()));
						subCloud2Writer.close();
						pFileList.add(current_mapping);
						
						//File for Network Counts
						File current_net_counts = new File(tmpDir, netNameSep + networkName + netNameSep + 
								cloudNameSep + cloud_name + cloudNameSep + ".NETCOUNTS.txt");
						BufferedWriter subCloud3Writer = new BufferedWriter(new FileWriter(current_net_counts));
						subCloud3Writer.write(cloud.printHashMap(cloud.getNetworkCounts()));
						subCloud3Writer.close();
						pFileList.add(current_net_counts);
						
						//File for Selected Counts
						File current_sel_counts = new File(tmpDir, netNameSep + networkName + netNameSep + 
								cloudNameSep + cloud_name + cloudNameSep + ".SELCOUNTS.txt");
						BufferedWriter subCloud4Writer = new BufferedWriter(new FileWriter(current_sel_counts));
						subCloud4Writer.write(cloud.printHashMap(cloud.getSelectedCounts()));
						subCloud4Writer.close();
						pFileList.add(current_sel_counts);
						
						//File for Ratios
						File current_ratios = new File(tmpDir, netNameSep + networkName + netNameSep +
								cloudNameSep + cloud_name + cloudNameSep + ".RATIOS.txt");
						BufferedWriter subCloud5Writer = new BufferedWriter(new FileWriter(current_ratios));
						subCloud5Writer.write(cloud.printHashMap(cloud.getRatios()));
						subCloud5Writer.close();
						pFileList.add(current_ratios);
						
						//File for NetworkPair Counts
						File current_net_pairCounts = new File(tmpDir, netNameSep + networkName + netNameSep + 
								cloudNameSep + cloud_name + cloudNameSep + ".NETPAIRCOUNTS.txt");
						BufferedWriter subCloud6Writer = new BufferedWriter(new FileWriter(current_net_pairCounts));
						subCloud6Writer.write(cloud.printHashMap(cloud.getNetworkPairCounts()));
						subCloud6Writer.close();
						pFileList.add(current_net_pairCounts);
						
						//File for SelectedPair Counts
						File current_sel_pairCounts = new File(tmpDir, netNameSep + networkName + netNameSep + 
								cloudNameSep + cloud_name + cloudNameSep + ".SELPAIRCOUNTS.txt");
						BufferedWriter subCloud7Writer = new BufferedWriter(new FileWriter(current_sel_pairCounts));
						subCloud7Writer.write(cloud.printHashMap(cloud.getSelectedPairCounts()));
						subCloud7Writer.close();
						pFileList.add(current_sel_pairCounts);
						
						//File for Ratios
						File current_pairRatios = new File(tmpDir, netNameSep + networkName + netNameSep +
								cloudNameSep + cloud_name + cloudNameSep + ".PAIRRATIOS.txt");
						BufferedWriter subCloud8Writer = new BufferedWriter(new FileWriter(current_pairRatios));
						subCloud8Writer.write(cloud.printHashMap(cloud.getPairRatios()));
						subCloud8Writer.close();
						pFileList.add(current_pairRatios);
						
					}//end iteration over clouds
				}//end if clouds exist for network
			}//end try
			catch (Exception ex)
			{
				ex.printStackTrace();
			}//end catch
			pFileList.add(session_prop_file);
		}//end network iterator
	}//end save session method
	
	/**
	 * Restore Semantic Summaries
	 * 
	 * @param pStateFileList - list of files associated with the session
	 */

	public void restoreSessionState(List<File> pStateFileList)
	{
		
		if ((pStateFileList == null) || (pStateFileList.size() == 0))
		{
			return; //no previous state to restore
		}
		
		//Initialize and load panels
		SemanticSummaryManager.getInstance(); //Initialize the manager
		SemanticSummaryPluginAction init = new SemanticSummaryPluginAction();
		init.loadInputPanel();
		init.loadCloudPanel();
		
		try
		{
			//Go through the prop files first to create networks to add
			//other files to
			for (int i = 0; i < pStateFileList.size(); i++)
			{
				File prop_file = pStateFileList.get(i);
				
				if (prop_file.getName().contains(".props"))
				{
					TextFileReader reader = new TextFileReader(prop_file.getAbsolutePath());
					reader.read();
					String fullText = reader.getText();
					
					//Given the file with all the parameters, create a new parameters
					SemanticSummaryParameters params = new SemanticSummaryParameters(fullText);
					
					//Get the networkID from the props file
					String[] fullname = prop_file.getName().split(netNameSep);
					String props_name = fullname[1];
					
					//Register network
					SemanticSummaryManager.getInstance().registerNetwork(Cytoscape.getNetwork(props_name), params);
					
				}//end if .props file
			}//end loop through all props files
			
			//Go through the prop files to create the clouds and set filters
			for (int i = 0; i < pStateFileList.size(); i++)
			{
				File prop_file = pStateFileList.get(i);
				
				if (prop_file.getName().contains(".CLOUDS.txt"))
				{
					TextFileReader reader = new TextFileReader(prop_file.getAbsolutePath());
					reader.read();
					String fullText = reader.getText();
					
					//Given the file with all the parameters, create a new parameters
					CloudParameters params = new CloudParameters(fullText);
					
					//Get the networkID from the props file
					String[] fullname = prop_file.getName().split(netNameSep);
					String net_name = fullname[1];
					
					//Get the cloudID from the props file
					String[] fullname2 = prop_file.getName().split(cloudNameSep);
					String cloud_name = fullname2[1];
					
					//Get the Network Parameters
					SemanticSummaryParameters networkParams = 
						SemanticSummaryManager.getInstance().getCyNetworkList().get(net_name);
					
					params.setNetworkParams(networkParams);
					networkParams.addCloud(cloud_name, params);
					
				}//end if .CLOUDS.txt file
				
				if (prop_file.getName().contains(".FILTER.txt"))
				{
					TextFileReader reader = new TextFileReader(prop_file.getAbsolutePath());
					reader.read();
					String fullText = reader.getText();
					
					//Get the networkID from the props file
					String[] fullname = prop_file.getName().split(netNameSep);
					String net_name = fullname[1];
					
					//Get the Network Parameters
					SemanticSummaryParameters networkParams = 
						SemanticSummaryManager.getInstance().getCyNetworkList().get(net_name);
					
					//Recreate the Filter and set pointer in cloud
					WordFilter curFilter = new WordFilter(fullText);
					networkParams.setFilter(curFilter);
				}
				
				if (prop_file.getName().contains(".DELIMITER.txt"))
				{
					TextFileReader reader = new TextFileReader(prop_file.getAbsolutePath());
					reader.read();
					String fullText = reader.getText();
					
					//Get the networkID from the props file
					String[] fullname = prop_file.getName().split(netNameSep);
					String net_name = fullname[1];
					
					//Get the Network Parameters
					SemanticSummaryParameters networkParams = 
						SemanticSummaryManager.getInstance().getCyNetworkList().get(net_name);
					
					//Recreate the Delimiter and set pointer in cloud
					WordDelimiters curDelimiter = new WordDelimiters(fullText);
					networkParams.setDelimiter(curDelimiter);
				}
			}//end loop through all props files
			
			
			//Go through the remaining files to update the clouds
			for (int i = 0; i < pStateFileList.size(); i++)
			{
				File prop_file = pStateFileList.get(i);
				
				if (prop_file.getName().contains(".CLOUDS.txt") ||
						prop_file.getName().contains(".props") || 
						prop_file.getName().contains(".FILTER.txt") ||
						prop_file.getName().contains(".DELIMITER.txt"))
					continue;
				
				TextFileReader reader = new TextFileReader(prop_file.getAbsolutePath());
				reader.read();
				String fullText = reader.getText();
				
				//Get the networkID from the props file
				String[] fullname = prop_file.getName().split(netNameSep);
				String net_name = fullname[1];
				
				//Get the cloudID from the props file
				String[] fullname2 = prop_file.getName().split(cloudNameSep);
				String cloud_name = fullname2[1];
				
				//Get the Network Parameters
				SemanticSummaryParameters networkParams = 
					SemanticSummaryManager.getInstance().getCyNetworkList().get(net_name);
				
				//Get the Cloud Parameters
				CloudParameters cloudParams = networkParams.getCloud(cloud_name);
				
				if (prop_file.getName().contains(".MAPPING.txt"))
				{
					//Recreate the HashMap and store
					HashMap<String, List<String>> mappings = 
						cloudParams.repopulateHashmap(fullText,2);
					cloudParams.setStringNodeMapping(mappings);
				}//end if .MAPPING.txt file
				
				if (prop_file.getName().contains(".NETCOUNTS.txt"))
				{
					//Recreate the HashMap and store
					HashMap<String,Integer> netCounts = cloudParams.repopulateHashmap(fullText, 1);
					cloudParams.setNetworkCounts(netCounts);
				}
				
				if (prop_file.getName().contains(".NETPAIRCOUNTS.txt"))
				{
					//Recreate the HashMap and store
					HashMap<String,Integer> netPairCounts = cloudParams.repopulateHashmap(fullText, 1);
					cloudParams.setNetworkPairCounts(netPairCounts);
				}
				
				if (prop_file.getName().contains(".SELCOUNTS.txt"))
				{
					//Recreate the HashMap and store
					HashMap<String, Integer> selCounts = cloudParams.repopulateHashmap(fullText, 1);
					cloudParams.setSelectedCounts(selCounts);
				}
				
				if (prop_file.getName().contains(".SELPAIRCOUNTS.txt"))
				{
					//Recreate the HashMap and store
					HashMap<String, Integer> selPairCounts = cloudParams.repopulateHashmap(fullText, 1);
					cloudParams.setSelectedPairCounts(selPairCounts);
				}
				
				if (prop_file.getName().contains(".RATIOS.txt"))
				{
					//Recreate the Ratios and store
					HashMap<String, Double> ratios = cloudParams.repopulateHashmap(fullText, 3);
					cloudParams.setRatios(ratios);
				}
				
				if (prop_file.getName().contains(".PAIRRATIOS.txt"))
				{
					//Recreate the Ratios and store
					HashMap<String, Double> pairRatios = cloudParams.repopulateHashmap(fullText, 3);
					cloudParams.setPairRatios(pairRatios);
				}
				
			}//end loop through all props files
			
			//Set current network and Initialize the panel appropriately
			SemanticSummaryManager.getInstance().setupCurrentNetwork();
			
		}//end try
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}//end restore session method
	
}
