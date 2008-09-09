/*  File: WattsStrogatzPanel.java
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
import cytoscape.layout.algorithms.*;
import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import giny.view.*;
import cytoscape.graph.dynamic.*;

import java.awt.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

import java.util.*;
import javax.swing.*;



/**
 * This class is responsible for handling the user interface
 * to generate a random model according to watts-strogatz model
 * 
 */
public class WattsStrogatzPanel extends RandomNetworkPanel {
	
	
	private static final int defaultNodeValue = 100;
	private static final double defaultBetaValue = .3;
	private static final int defaultDegreeValue =  2;
	
	int mode;

	//TextFields
	private javax.swing.JTextField nodeTextField;
	private javax.swing.JTextField betaTextField;
	private javax.swing.JTextField degreeTextField;

	//Buttons
	private javax.swing.JCheckBox directedCheckBox;
	private javax.swing.JCheckBox selfEdgeCheckBox;

	//Labels
	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel betaLabel;
	private javax.swing.JLabel degreeLabel;

	/*
	 * Default constructor
	 */
	public WattsStrogatzPanel(int pMode ){
		super(null);
		mode = pMode;
		initComponents();
	}
	
	
	/*
	 * Default constructor
	 */
	public WattsStrogatzPanel(int pMode, RandomNetworkPanel pPrevious ){
		super(pPrevious);
		mode = pMode;
		initComponents();
	}
	
	/**
	 *
	 */
		public String getNextText()
	{
		if(mode == 0)
		{
			return new String("Generate");
		}
		else
			return new String("Next");
			
	}
	
	
	/**
 	 *
	 */	
	public String getTitle()
	{
		return new String("Watts-Strogatz Model");
	}
	
	
	/**
 	 *
	 */
	public String getDescription()
	{
		return new String("The Watts-Strogtatz model linearly interpolates between a complete lattice and "+
							"an erdos-renyi network using the  &#x3B2; value.  As  &#x3B2; increases the clustering coefficent " +
							"will decrease but the small world property will increase. Constraints: N > 2, 0 &#8804; &#x3B2; &#8804; 1, degree > 1. ");
	}
	
	/*
	 * Initialize the components
	 */
	private void initComponents() {
	
		//Create the TextFields
		nodeTextField = new javax.swing.JTextField();
		betaTextField = new javax.swing.JTextField();
		degreeTextField = new javax.swing.JTextField();
		nodeTextField.setPreferredSize(new Dimension(50,25)); 
		betaTextField.setPreferredSize(new Dimension(50,25)); 
		degreeTextField.setPreferredSize(new Dimension(50,25)); 
		nodeTextField.setText("" + defaultNodeValue);
		betaTextField.setText("" + defaultBetaValue);
		degreeTextField.setText("" + defaultDegreeValue);
		nodeTextField.setHorizontalAlignment(JTextField.RIGHT);
		betaTextField.setHorizontalAlignment(JTextField.RIGHT);
		degreeTextField.setHorizontalAlignment(JTextField.RIGHT);
		

		//Create the buttons
		directedCheckBox = new javax.swing.JCheckBox();
		selfEdgeCheckBox = new javax.swing.JCheckBox();

		//Create the Labels
		nodeLabel = new javax.swing.JLabel();
		degreeLabel = new javax.swing.JLabel();
		betaLabel = new javax.swing.JLabel();

		
		//Set the text on the labels
		nodeLabel.setText("Number of Nodes:");
		betaLabel.setText("<html> &#x3B2; (Linear Interpolation):</html>");
		degreeLabel.setText("Node Degree:");
		selfEdgeCheckBox.setText("Allow reflexive Edges (u,u)");
		directedCheckBox.setText("Undirected");
	

		setLayout(new GridBagLayout());

		//Setup the titel
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0,10,0,0);		
		c.gridx = 0;
		c.gridy = 0;
//		c.weightx = 1;
//		c.weighty = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(nodeLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
//		c.weightx = 1;
//		c.weighty = 1;
//		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.LINE_START;		
		add(nodeTextField,c);


		c = null;
		c = new GridBagConstraints();
		c.insets = new Insets(0,10,0,0);		
		c.gridx = 0;
		c.gridy = 1;
//		c.weightx = 1;
//		c.weighty = 1;
		c.anchor = GridBagConstraints.LINE_START;		
		add(betaLabel,c);

		c = null;
		c = new GridBagConstraints();
//		c.weightx = 1;
//		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(betaTextField,c);

		c = null;
		c = new GridBagConstraints();
//		c.weightx = 1;
//		c.weighty = 1;
		c.insets = new Insets(0,10,0,0);	
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(degreeLabel,c);

		c = null;
		c = new GridBagConstraints();	
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 1;		
		c.anchor = GridBagConstraints.LINE_START;
		add(degreeTextField,c);


		//add the line
		c = null;
		c = new GridBagConstraints();
		c.insets = new Insets(0,10,0,10);		
		//c.anchor = GridBagConstraints.CENTER;
		c.gridx = 2;
		c.gridheight = 5;
		c.gridy = 0;
		c.fill = GridBagConstraints.VERTICAL;
		//c.weightx = 1;
		//c.weighty = 1;
		add(new JSeparator(SwingConstants.VERTICAL), c);


		c = null;
		c = new GridBagConstraints();
		c.insets = new Insets(0,10,0,0);		
		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(directedCheckBox,c);
		
		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,10,0,0);		
		c.gridx = 3;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(selfEdgeCheckBox,c);




	}



		

	/*
	 *  Callback for the generate button
	 */
	public RandomNetworkPanel next()
	{
		//The degree of each node
		int degree;

		//How much to interpolate between a lattice
		//and erdos-renyi model
		double beta;

		//The number of nodes in the network
		int numNodes;

		//Whether or not the network is directed
		boolean directed;

		//Whether or not the network allows reflexive edge
		boolean allowSelfEdge;



		//Get the strings from the textfields
		String numNodeString = nodeTextField.getText();
		String betaString = betaTextField.getText();
		String degreeString = degreeTextField.getText();

		//Try to read the string into an integer
		try {
			numNodes = Integer.parseInt(numNodeString);

			if(numNodes < 3)
			{
				throw new Exception("Nodes must be > 2");
			}

		} catch (Exception e) {
			//If an error occurs than change the colors
			nodeTextField.grabFocus();
			degreeLabel.setForeground(java.awt.Color.BLACK);
			nodeLabel.setForeground(java.awt.Color.RED);
			betaLabel.setForeground(java.awt.Color.BLACK);
			return this;
		}

		//Try to read string into an double
		try {
			beta = Double.parseDouble(betaString);

			//Check to make sure beta is a probability
			if((beta < 0) || (beta > 1))
			{
				throw (new Exception("Beta must be a probability"));
			}
		} catch (Exception e) {
			//If an error occurs than change the colors
			betaTextField.grabFocus();
			degreeLabel.setForeground(java.awt.Color.BLACK);
			nodeLabel.setForeground(java.awt.Color.BLACK);
			betaLabel.setForeground(java.awt.Color.RED);
			return this;
		}

		//Try to read this string into an integer
		try {
			degree = Integer.parseInt(degreeString);
			if(degree < 1)
			{
				throw new Exception("Degree must be positive.");
			}

		} catch (Exception e) {
			//If an error occurs than change the colors	
			degreeTextField.grabFocus();	
			degreeLabel.setForeground(java.awt.Color.RED);
			nodeLabel.setForeground(java.awt.Color.BLACK);
			betaLabel.setForeground(java.awt.Color.BLACK);
			return this;
		}
		
		
		//If we got this far reset all to black
		degreeLabel.setForeground(java.awt.Color.BLACK);
		nodeLabel.setForeground(java.awt.Color.BLACK);
		betaLabel.setForeground(java.awt.Color.BLACK);
		
		
		//Get the directed/undirected from the checkbox
		directed = false;
		if (directedCheckBox.isSelected()) {
			directed = true;
		}
		
		//Get the boolean for set 
		allowSelfEdge = false;
		if (selfEdgeCheckBox.isSelected()) {
			allowSelfEdge = true;
		}

		//Create the model
		WattsStrogatzModel wsm = new WattsStrogatzModel(numNodes,
				allowSelfEdge, !directed, beta, degree);
		
		
		
		
		if(mode == 1)
		{
			
			wsm.setCreateView(false);
			
			if(mNext == null)
			{
				mNext = new AnalyzePanel(this,wsm, wsm.getDirected());
			}
			else
			{
				((AnalyzePanel)mNext).setDirected(wsm.getDirected());
				((AnalyzePanel)mNext).setGenerator(wsm);				
			}
			
			return mNext;
		}

		
		//Generate the random network
		RandomNetwork network = wsm.generate();
		CyNetwork randomNet = network.toCyNetwork();


		//Go up to the Dialog and close this window
	
		return this;

	}

}
