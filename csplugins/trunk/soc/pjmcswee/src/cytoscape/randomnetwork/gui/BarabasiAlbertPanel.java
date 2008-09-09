/*  File: BarabasiAlbertPanel.java
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
 * to generate a random model according to barabasi-albert.
 * 
 */
public class BarabasiAlbertPanel extends RandomNetworkPanel {

	/**
	 * Specifies a context for this gui.
	 */
	private int mode;
	
	private static final int defaultNodeValue = 100;
	private static final int defaultEdgeValue = 2;
	private static final int defaultInitValue = 3;


	// TextFields
	private javax.swing.JTextField nodeTextField;
	private javax.swing.JTextField initTextField;
	private javax.swing.JTextField edgeTextField;

	// Buttons
	private javax.swing.JCheckBox directedCheckBox;
	
	// Labels
	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel initLabel;
	private javax.swing.JLabel edgeLabel;
	private javax.swing.JLabel explainLabel;

	/*
	 * Default Constructor 
	 */
	public BarabasiAlbertPanel(int pMode ){
		super(null);
		mode = pMode;
		initComponents();
	}
	
	/*
	 * Default Constructor 
	 */
	public BarabasiAlbertPanel(int pMode, RandomNetworkPanel pPrevious ){
		super(pPrevious);
		mode = pMode;
		initComponents();
	}
	
	public String getNextText()
	{
		if(mode == 0)
		{
			return new String("Generate");
		}
		else
			return new String("Next");
			
	}
	
	public String getTitle()
	{
		return new String("Barabasi-Albert Model");
	}
	
	public String getDescription()
	{
		return new String("The Barabasi-Albert model begins with a connected seed network of s nodes. " +
						"Every other node (n - s) is added one at a time, " +
						"and initially connected to m existing nodes. " +
						"Each existing node u has probability  degree(u)/(2*E), E is the number of edges. "+
						"The resulting network has a power-law degree distribution (scale-free). Constraints: 1 < n, 2 &#8804; s &#8804; n, 1 &#8804; m  ");
	}


	/**
	 * Initialize all of the swing components
	 */
	private void initComponents() {
	

	
		// TextField creation
		nodeTextField = new javax.swing.JTextField();
		initTextField = new javax.swing.JTextField();
		edgeTextField = new javax.swing.JTextField();
		nodeTextField.setPreferredSize(new Dimension(30,25)); 
		edgeTextField.setPreferredSize(new Dimension(30,25)); 
		initTextField.setPreferredSize(new Dimension(30,25)); 
		nodeTextField.setText("" + defaultNodeValue);
		edgeTextField.setText("" + defaultEdgeValue);
		initTextField.setText("" + defaultInitValue);
		edgeTextField.setHorizontalAlignment(JTextField.RIGHT);
		initTextField.setHorizontalAlignment(JTextField.RIGHT);
		nodeTextField.setHorizontalAlignment(JTextField.RIGHT);
				// Button creation
		directedCheckBox = new javax.swing.JCheckBox();
	
		// Label creation
		nodeLabel = new javax.swing.JLabel();
		initLabel = new javax.swing.JLabel();
		edgeLabel = new javax.swing.JLabel();
		
		//Set the text for the labels
		directedCheckBox.setText("Undirected");
		nodeLabel.setText("Number of Nodes (n):");
		initLabel.setText("Initial Number of Nodes (s):");
		edgeLabel.setText("Minimum Edges per node (m):");
		


		setLayout(new GridBagLayout());

		//Setup the titel
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5,5,5,5);		
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.gridheight = 1;
		add(nodeLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,5,5);		
		c.gridx = 2;
		c.gridy = 4;
		c.gridwidth = 3;
		c.ipadx = 20;
		c.weightx = 1;
		c.weighty = 1;
		c.gridheight = 1;
		add(nodeTextField,c);


		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,5,5);		
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(edgeLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,5,5);		
		c.gridx = 2;
		c.gridy = 5;
		c.gridwidth = 3;
		c.ipadx = 20;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(edgeTextField,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,5,5);		
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(initLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,5,5);		
		c.gridx = 2;
		c.gridy = 6;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.ipadx = 20;
		c.weightx = 1;
		c.weighty = 1;
		add(initTextField,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,5,5);		
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.ipadx = 20;
		c.weightx = 1;
		c.weighty = 1;
		add(directedCheckBox,c);

	}
	
	
	/**
	 *  Callback for the generate button
	 */
	public RandomNetworkPanel next()
	{
		// The number of initial nodes
		int initNumNodes; 
		//number of nodes in the network
		int numNodes;
		//If the network is directed/undirected
		boolean directed;
		//The number of edges to add for each node
		int edgesToAdd;
		
		//Allow reflexive edges N/A for Barabasi-Albert
		boolean allowSelfEdge = false;

		//Get the string values for each of the textfields
		String numNodeString = nodeTextField.getText();
		String initString = initTextField.getText();
		String edgeString = edgeTextField.getText();

		//Try to read the number of nodes from the textfield
		try {
			numNodes = Integer.parseInt(numNodeString);
			if(numNodes < 2)
			{
				throw new Exception("Need at least 2 nodes.");
			}
		} catch (Exception e) {
			//If there is an error change the colors
			nodeTextField.grabFocus();
			nodeLabel.setForeground(java.awt.Color.RED);
			initLabel.setForeground(java.awt.Color.BLACK);
			edgeLabel.setForeground(java.awt.Color.BLACK);
			return this;
		}

		//Try to read this string into a number
		try {
			initNumNodes = Integer.parseInt(initString);
			if(initNumNodes < 2)
			{
				throw new Exception("There must be at least 2 nodes in the initial nework.");
			}	

		} catch (Exception e) {
			//If there is an error change the colors to red
			initTextField.grabFocus();	
			nodeLabel.setForeground(java.awt.Color.BLACK);
			initLabel.setForeground(java.awt.Color.RED);
			edgeLabel.setForeground(java.awt.Color.BLACK);
			return this;
		}

		//Try read this string into a number
		try {
			edgesToAdd = Integer.parseInt(edgeString);
			if(edgesToAdd < 1)
			{
				throw new Exception("Must add at least one edge per round.");
			}	
		} catch (Exception e) {
			//If there is an error change the colors to red
			edgeTextField.grabFocus();	
			nodeLabel.setForeground(java.awt.Color.BLACK);
			initLabel.setForeground(java.awt.Color.BLACK);
			edgeLabel.setForeground(java.awt.Color.RED);
			return this;
		}

		//Set the boolean based on the checkbox
		directed = false;
		if (directedCheckBox.isSelected()) {
			directed = true;
		}

		//Set to no reflexive edges for now
		allowSelfEdge = false;

		//Create the model
		BarabasiAlbertModel bam = new BarabasiAlbertModel(numNodes, allowSelfEdge, !directed, initNumNodes, edgesToAdd);
		
		
		
		//If we got this far reset all error codeings
		nodeLabel.setForeground(java.awt.Color.BLACK);
		initLabel.setForeground(java.awt.Color.BLACK);
		edgeLabel.setForeground(java.awt.Color.BLACK);
			
		if(mode == 1)
		{
			
			bam.setCreateView(false);

			if(mNext == null)
			{
				mNext = new AnalyzePanel(this,bam, bam.getDirected());
			}
			else
			{
				((AnalyzePanel)mNext).setDirected(bam.getDirected());
				((AnalyzePanel)mNext).setGenerator(bam);				
			}
			
			return mNext;			
		}

		
		
		//Create the network
		RandomNetwork graph = bam.generate();
		CyNetwork randomNet = graph.toCyNetwork();
		graph = null;
		
		
		return this;

	}

}
