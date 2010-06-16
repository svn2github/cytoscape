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

package cytoscape.csplugins.semanticsummary;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * The CloudDisplayPanel class defines the panel that displays a Semantic 
 * Summary tag cloud in the South data panel.
 * 
 * @author Layla Oesper
 * @version 1.0
 */

public class CloudDisplayPanel extends JPanel
{
	
	//VARIABLES
	//TODO
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
	//TODO
	
	/**
	 * Initialized a blank tag cloud JPanel object.
	 * @return JPanel
	 */
	private JPanel initializeTagCloud()
	{
		JPanel panel = new JPanel(new ModifiedFlowLayout(ModifiedFlowLayout.CENTER,10,1));
		return panel;
	}
	
	/**
	 * Clears all words from the CloudDisplay.
	 */
	public void clearCloud()
	{
		tagCloudFlowPanel.removeAll();
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
		
		//Loop through to create labels and add them
		int count = 0;
		
		ArrayList<CloudWordInfo> wordInfo = curCloud.getCloudWordInfoList();
		Iterator<CloudWordInfo> iter = wordInfo.iterator();
		
		//Loop while more words exist and we are under the max
		while(iter.hasNext() && (count < params.getMaxWords()))
		{
			CloudWordInfo curWordInfo = iter.next();
			JLabel curLabel = curWordInfo.createCloudLabel();
			
			tagCloudFlowPanel.add(curLabel);
			count++;
		}
		tagCloudFlowPanel.revalidate();
		this.updateUI();
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
