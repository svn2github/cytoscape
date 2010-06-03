/*
 File: SemanticSummaryInputPanel.java

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
	private void clearCloud()
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
		ArrayList<CloudWordInfo> wordInfo = curCloud.getCloudWordInfoList();
		Iterator<CloudWordInfo> iter = wordInfo.iterator();
		while(iter.hasNext())
		{
			CloudWordInfo curWordInfo = iter.next();
			JLabel curLabel = curWordInfo.createCloudLabel();
			
			tagCloudFlowPanel.add(curLabel);
		}
		tagCloudFlowPanel.revalidate();
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
