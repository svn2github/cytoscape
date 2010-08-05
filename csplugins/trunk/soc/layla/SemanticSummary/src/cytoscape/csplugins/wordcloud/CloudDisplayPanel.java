/*
 File: SemanticSummaryInputPanel.java
 
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

package cytoscape.csplugins.wordcloud;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 * The CloudDisplayPanel class defines the panel that displays a Semantic 
 * Summary tag cloud in the South data panel.
 * 
 * @author Layla Oesper
 * @version 1.0
 */

public class CloudDisplayPanel extends JPanel
{

	private static final long serialVersionUID = 5996569544692738989L;
	
	//VARIABLES
	JPanel tagCloudFlowPanel;//add JLabels here for words
	JScrollPane cloudScroll;
	CloudParameters curCloud;
	
	
	//CONSTRUCTORS
	public CloudDisplayPanel()
	{
		setLayout(new BorderLayout());
		
		//Create JPanel containing tag words
		tagCloudFlowPanel = initializeTagCloud();
		cloudScroll = new JScrollPane(tagCloudFlowPanel);
		cloudScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(cloudScroll, BorderLayout.CENTER);
	}
	
	//METHODS
	
	/**
	 * Initialized a blank tag cloud JPanel object.
	 * @return JPanel
	 */
	private JPanel initializeTagCloud()
	{
		JPanel panel = new JPanel(new ModifiedFlowLayout(ModifiedFlowLayout.CENTER,30,25));
		return panel;
	}
	
	/**
	 * Clears all words from the CloudDisplay.
	 */
	public void clearCloud()
	{
		tagCloudFlowPanel.removeAll();
		tagCloudFlowPanel.setLayout(new ModifiedFlowLayout(ModifiedFlowLayout.CENTER, 30, 25));
		tagCloudFlowPanel.revalidate();
		cloudScroll.revalidate();
		tagCloudFlowPanel.updateUI();
		curCloud = null;
		
	}
	
	/**
	 * Updates the tagCloudFlowPanel to include all of the words at the size they
	 * are defined for in params.
	 * @param CloudParameters - parameters of the cloud we want to display.
	 */
	public void updateCloudDisplay(CloudParameters params)
	{
		//clear old info
		this.clearCloud();
		curCloud = params;
		
		//Create a list of the words to include based on MaxWords parameters
		ArrayList<CloudWordInfo> copy = new ArrayList<CloudWordInfo>();
		ArrayList<CloudWordInfo> original = curCloud.getCloudWordInfoList();
		
		for (int i = 0; i < original.size(); i++)
		{
			CloudWordInfo curInfo = original.get(i);
			copy.add(curInfo);
		}
		Collections.sort(copy);
		
		Integer max = params.getMaxWords();
		Integer numWords = copy.size();
		if (max < numWords)
		{
			copy.subList(max, numWords).clear();
		}
		
		
		//Loop through to create labels and add them
		int count = 0;
		
		HashMap<Integer,JPanel> clusters = new HashMap<Integer, JPanel>();
		ArrayList<CloudWordInfo> wordInfo = curCloud.getCloudWordInfoList();
		Iterator<CloudWordInfo> iter = wordInfo.iterator();
		
		//Loop while more words exist and we are under the max
		while(iter.hasNext() && (count < params.getMaxWords()))
		{
			CloudWordInfo curWordInfo = iter.next();
			
			//Check that word in in our range
			if (copy.contains(curWordInfo))
			{
				Integer clusterNum = curWordInfo.getCluster();
				JLabel curLabel = curWordInfo.createCloudLabel();
			
				//Retrieve proper Panel
				JPanel curPanel;
				if (clusters.containsKey(clusterNum))
				{
					curPanel = clusters.get(clusterNum);
				}
				else
				{
					if (params.getDisplayStyle().equals(CloudDisplayStyles.NO_CLUSTERING))
					{
						//curPanel =  new JPanel(new ModifiedFlowLayout(ModifiedFlowLayout.CENTER,10,0));
						curPanel = tagCloudFlowPanel;
						curPanel.setLayout(new ModifiedFlowLayout(ModifiedFlowLayout.CENTER, 10, 0));
					}
					else
					{
					curPanel = new JPanel(new ModifiedClusterFlowLayout(ModifiedFlowLayout.CENTER,10,0));
					}
					
					if (params.getDisplayStyle().equals(CloudDisplayStyles.CLUSTERED_BOXES))
					{
						curPanel.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.GRAY), new EmptyBorder(10,10,10,10)));
					}
				}
			
				curPanel.add(curLabel);
				clusters.put(clusterNum, curPanel);
				count++;
			}
		}
		
		//Add all clusters to flow panel
		SortedSet<Integer> sortedSet = new TreeSet<Integer>(clusters.keySet());
		
		for(Iterator<Integer> iter2 = sortedSet.iterator(); iter2.hasNext();)
		{
			Integer clusterNum = iter2.next();
			JPanel curPanel = clusters.get(clusterNum);
			
			if (!curPanel.equals(tagCloudFlowPanel))
				tagCloudFlowPanel.add(curPanel);
		}
		
		tagCloudFlowPanel.revalidate();
		this.revalidate();
		this.updateUI();
		this.repaint();
	}
	

	//Getters and Setters
	
	public JPanel getTagCloudFlowPanel()
	{
		return tagCloudFlowPanel;
	}
	
	public void setTagCloudFlowPanel(JPanel aPanel)
	{
		tagCloudFlowPanel = aPanel;
	}
	
	public CloudParameters getCloudParameters()
	{
		return curCloud;
	}
	
	public void setCloudParameters(CloudParameters params)
	{
		curCloud = params;
	}

}
