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

import javax.swing.JMenu;

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
	
	//CONSTRUCTORS
	
	/**
	 * SemanticSummaryPlugin Constructor
	 */
	
	public SemanticSummaryPlugin()
	{
		//New actions for response to menu selections
		SemanticSummaryPluginAction settings = new SemanticSummaryPluginAction();
		CreateCloudAction create = new CreateCloudAction();
		settings.setPreferredMenu("Plugins.Semantic Network Summary");
		create.setPreferredMenu("Plugins.Semantic Network Summary");
	
		//Add to Plugin Menu
		Cytoscape.getDesktop().getCyMenus().addAction(settings);
		Cytoscape.getDesktop().getCyMenus().addAction(create);
		
		//Add to right click menus
		
		//Newly created networks
		SemanticSummaryNetworkListener netListener = 
			new SemanticSummaryNetworkListener();
		
		Cytoscape.getSwingPropertyChangeSupport().
		addPropertyChangeListener(netListener);
		
		//Loaded networks
		Set networkSet = Cytoscape.getNetworkSet();
		
		for (Iterator iter = networkSet.iterator(); iter.hasNext();)
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
		sb.append("displays a word cloud semantic summary of the selected ");
		sb.append("cyNode attribute.  The node name is the defuault ");
		sb.append("attribute.");
		return sb.toString();
	}
	
	/**
	 * SaveSessionStateFiles collects all the data stored in the Semantic 
	 * Summary data structures and creates property files for each network 
	 * listing the variables needed to rebuild the Semantic Summary.
	 * 
	 * @param pFileList - pointer to the set of files to be added to the session
	 */
	/*
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
			
			//property file
			File session_prop_file = new File(tmpDir, networkID + ".props");
			
			//write out files.
			try 
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter(session_prop_file));
				writer.write(params.toString());
				writer.close();
				
				//Loop on Clouds
				if (!params.getClouds().isEmpty())
				{
					HashMap<String, CloudParameters> all_clouds = params.getClouds();
					
					for (Iterator j=all_clouds.keySet().iterator(); j.hasNext();)
					{
						String cloud_name = j.next().toString();
						String old_cloud_name = cloud_name;
						//Cloud names that contain periods will cause problems
						if (cloud_name.contains("."))
							cloud_name.replace('.', '_');
						
						CloudParameters cloud = all_clouds.get(old_cloud_name);
						
						//File for CloudParameters
						File current_cloud = new File(tmpDir, networkID + "." + cloud_name + ".CLOUD.txt");
						BufferedWriter subCloud1Writer = new BufferedWriter(new FileWriter(current_cloud));
						subCloud1Writer.write(cloud.toString());
						subCloud1Writer.close();
						pFileList.add(current_cloud);
						
						//File for String/Word Mapping
						//I DONT THINK THAT THIS WORKS!
						File current_mapping = new File(tmpDir, networkID + "." + cloud_name + ".MAPPING.txt");
						BufferedWriter subCloud2Writer = new BufferedWriter(new FileWriter(current_mapping));
						subCloud2Writer.write(params.printHashMap(cloud.getStringNodeMapping()));
						subCloud2Writer.close();
						pFileList.add(current_mapping);
						
						//File for Network Counts
						File current_net_counts = new File(tmpDir, networkID + "." + cloud_name + ".NETCOUNTS.txt");
						BufferedWriter subCloud3Writer = new BufferedWriter(new FileWriter(current_net_counts));
						subCloud3Writer.write(params.printHashMap(cloud.getNetworkCounts()));
						subCloud3Writer.close();
						pFileList.add(current_net_counts);
						
						//File for Selected Counts
						File current_sel_counts = new File(tmpDir, networkID + "." + cloud_name + ".SELCOUNTS.txt");
						BufferedWriter subCloud4Writer = new BufferedWriter(new FileWriter(current_sel_counts));
						subCloud4Writer.write(params.printHashMap(cloud.getSelectedCounts()));
						subCloud4Writer.close();
						pFileList.add(current_sel_counts);
						
						
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
	*/
	
	/**
	 * Restore Semantic Summaries
	 * 
	 * @param pStateFileList - list of files associated with the session
	 */
	/*
	public void restoreSessionState(List<File> pStateFileList)
	{
		if ((pStateFileList == null) || (pStateFileList.size() == 0))
		{
			return; //no previous state to restore
		}
		
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
					
					//Get the network name
					String param_name = params.getNetworkName();
					
					//TODO - Finish this!
					
				}//end if .props file
			}//end loop through all props files
		}//end try
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}//end restore session method
	*/
	
}
