/* File: RandomizeExistingPanel.java
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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

package cytoscape.randomnetwork.gui;

import cytoscape.randomnetwork.*;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;
import java.util.*;
import java.awt.*;




/**
 * RandomizeExistingPanel is used for selecting which randomizing 
 * network model to use.
 */
public class RandomComparisonPanel extends RandomNetworkPanel {

	//Group together the different options
	private javax.swing.ButtonGroup group;

	//Checkbox for generating random networks
	private javax.swing.JCheckBox generateRandomNetwork;
	
	//Checkbox for randomizing an existing network
	private javax.swing.JCheckBox randomizeExistingNetwork;


	private int nextState;
	
	
	//Explain what this checkbox means
	private javax.swing.JLabel generateRandomExplain;
	
	//Explain what this checkbox means	
	private javax.swing.JLabel randomizeExistingExplain;

	/**
	 *  Default constructor
	 */
	public RandomComparisonPanel(){		
		super(null); 
		initComponents();
		nextState = -1;
	}

	/**
	*
	*/
	public String getTitle()
	{
		return new String("Compare to Random Network");
	}

	/**
	*
	*/
	public String getDescription()
	{
		return new String("Which type of random network would you like to compare the existing network against.");
	}


	/**
	 * Initialize the components
	 */
	private void initComponents() {
	
		//Create the group 
		group = new javax.swing.ButtonGroup();

		//Create the randomizeExisting checkbox
		randomizeExistingNetwork = new javax.swing.JCheckBox();
	
		//Create the generate random graph check box
		generateRandomNetwork = new javax.swing.JCheckBox();
		
		
		//create the generate random label
		generateRandomExplain = new javax.swing.JLabel();
		
		//Create the randomize existing  label
		randomizeExistingExplain = new javax.swing.JLabel();

		//Set the randomize existing text
		randomizeExistingExplain.setText("<html><font size=2 face=Verdana>Compare against a randomized existing network.</font></html>");

		//Set the randomize existing text
		generateRandomExplain.setText("<html><font size=2 face=Verdana>Compare against a random network models.</font></html>");
		randomizeExistingExplain.setPreferredSize(new Dimension(200,40));
		generateRandomExplain.setPreferredSize(new Dimension(200,40));
		randomizeExistingExplain.setMinimumSize(new Dimension(200,40));
		generateRandomExplain.setMinimumSize(new Dimension(200,40));

		
		//Add these buttons to the group
		group.add(generateRandomNetwork);
		group.add(randomizeExistingNetwork);
		
		
		
		randomizeExistingNetwork.setText("Compare against randomized versions of an existing network");
		generateRandomNetwork.setText("Compare against generated random networks");


		//set the labels to opaque
		generateRandomExplain.setOpaque(true);		
		randomizeExistingExplain.setOpaque(true);
		
		
		//Make randomize existing network
		randomizeExistingNetwork.setSelected(true);
	
		setLayout(new GridBagLayout());
		
		//
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0,5,0,0);	
		c.anchor =  GridBagConstraints.LINE_START;
		c.weightx = 1;
///		c.weighty = .5;
		add(randomizeExistingNetwork,c);

		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = new Insets(0,0,0,30);	
		c.anchor =  GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.weighty = 1;
		add(generateRandomNetwork,c);

	}


	
	/**
	 *  Callback for when the "Next" button is pushed
	 */
	public RandomNetworkPanel next()
	{
		
		
		//See which checkbox is selected and then display the appropriate panel
		if (randomizeExistingNetwork.isSelected()) 
		{
			if((mNext == null)||(nextState != 0))
			{
				mNext = null;
				mNext= new RandomizeExistingPanel(1,this);			
			}
			nextState = 0;
		} 
		else if(generateRandomNetwork.isSelected())
		{
			if((mNext == null)||(nextState != 1))
			{
				mNext = null;
				mNext = new GenerateRandomPanel(1,this);			
			}
			nextState = 1;
		}
		
		return mNext;
	}
}