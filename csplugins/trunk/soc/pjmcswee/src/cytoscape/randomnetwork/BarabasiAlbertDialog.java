/*  File: BarabasiAlbertDialog
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

package cytoscape.randomnetwork;


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
public class BarabasiAlbertDialog extends JPanel {

	/**
	 * Specifies a context for this gui.
	 */
	private int mode;
	
	private static final int defaultNodeValue = 100;
	private static final int defaultEdgeValue = 3;
	private static final int defaultInitValue = 2;


	// TextFields
	private javax.swing.JTextField nodeTextField;
	private javax.swing.JTextField initTextField;
	private javax.swing.JTextField edgeTextField;

	// Buttons
	private javax.swing.JButton runButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JCheckBox directedCheckBox;
	private javax.swing.JButton backButton;
	
	// Labels
	private javax.swing.JLabel titleLabel;
	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel initLabel;
	private javax.swing.JLabel edgeLabel;
	private javax.swing.JLabel explainLabel;

	/*
	 * Default Constructor 
	 */
	public BarabasiAlbertDialog(int pMode ){
		super();
		mode = pMode;
		initComponents();
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
		runButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		backButton = new javax.swing.JButton();

		// Label creation
		titleLabel = new javax.swing.JLabel();
		nodeLabel = new javax.swing.JLabel();
		initLabel = new javax.swing.JLabel();
		edgeLabel = new javax.swing.JLabel();
		explainLabel = new javax.swing.JLabel();

		//Add the explanation label
		explainLabel
				.setText("<html><font size=2 face=Verdana>" +
				"The Barabasi-Albert model begins with a connected seed network of s nodes " +
				"Every other node (n - s) is added one at a time, " +
				"and initially connected to m existing nodes. " +
				"Each existing node u has probability  degree(u)/(2*E), E is the number of edges </font></html>");

		explainLabel.setPreferredSize(new Dimension(380, 80));
		explainLabel.setMinimumSize(new Dimension(380, 80));
		
		//Set the text for the labels
		directedCheckBox.setText("Undirected");
		nodeLabel.setText("Number of Nodes (n):");
		initLabel.setText("Initial Number of Nodes (s):");
		edgeLabel.setText("Minimum Edges per node (m):");
		
		//Set the title for the panel
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Generate Barabasi-Albert Model");

		//Set up the generate button
		runButton.setText("Generate");
		runButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				runButtonActionPerformed(evt);
			}
		});

		//Set up the cancel button
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});
		
		//Set up the Cancel Button
		backButton.setText("Back");
		backButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				backButtonActionPerformed(evt);
			}
		});


		setLayout(new GridBagLayout());

		//Setup the titel
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0,10,0,0);		
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 5;
		c.weightx = 1;
		c.weighty = 1;
		add(titleLabel,c);

	
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5,10,10,0);		
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 6;
		c.weightx = 1;
		c.weighty = 1;
		add(explainLabel,c);

		
		c = null;
		c = new GridBagConstraints();
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


		//Setup the 
		c = null;
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = 10;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,0,0,0);
		c.weightx = 1;
		c.weighty = 1;
		add(cancelButton,c);
		
		
		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 10;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0,0,0,0);
		add(backButton,c);

		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 10;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(0,0,0,0);
		c.weightx = 1;
		c.weighty = 1;
		add(runButton,c);

	}

	/**
	 *  Callback for the cancel button
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {

		//Go up through the parents and close the window
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JFrame frame = (JFrame)p;
		frame.dispose();
	}
	
	/**
	 *  Call back for the back button
	 */
	private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
	
		GenerateRandomPanel generateRandomPanel = new GenerateRandomPanel(mode);

		//Get the TabbedPanel
		JTabbedPane parent = (JTabbedPane)getParent();
		int index = parent.getSelectedIndex();
			
		//Remove this Panel
		parent.remove(index);
		
		//Replace it with the panel
		parent.add(generateRandomPanel, index);
		//Set the title for this panel
		parent.setTitleAt(index,"Generate Random Network");
		//Display this panel
		parent.setSelectedIndex(index);
		//Enforce this Panel
		parent.validate();
		
		
		//Re-pack the window based on this new panel
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JFrame frame = (JFrame)p;
		frame.pack();

		return;

	}

	
	
	/**
	 *  Callback for the generate button
	 */
	private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {

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
		} catch (Exception e) {
			//If there is an error change the colors
			nodeLabel.setForeground(java.awt.Color.RED);
			initLabel.setForeground(java.awt.Color.BLACK);
			edgeLabel.setForeground(java.awt.Color.BLACK);
			return;
		}

		//Try to read this string into a number
		try {
			initNumNodes = Integer.parseInt(initString);
		} catch (Exception e) {
			//If there is an error change the colors to red	
			nodeLabel.setForeground(java.awt.Color.BLACK);
			initLabel.setForeground(java.awt.Color.RED);
			edgeLabel.setForeground(java.awt.Color.BLACK);
			return;
		}

		//Try read this string into a number
		try {
			edgesToAdd = Integer.parseInt(edgeString);
		} catch (Exception e) {
			//If there is an error change the colors to red	
			nodeLabel.setForeground(java.awt.Color.BLACK);
			initLabel.setForeground(java.awt.Color.BLACK);
			edgeLabel.setForeground(java.awt.Color.RED);
			return;
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
		
		
			
		if(mode == 1)
		{
			
			bam.setCreateView(false);
			AnalyzePanel analyzePanel = new AnalyzePanel(bam, bam.getDirected(),0);
		
			//Get the TabbedPanel
			JTabbedPane parent = (JTabbedPane)getParent();
			int index = parent.getSelectedIndex();
			
			//Remove this Panel
			parent.remove(index);
			//Replace it with the panel
			parent.add(analyzePanel, index);
			//Set the title for this panel
			parent.setTitleAt(index,"Analyze Network Statistics");
			//Display this panel
			parent.setSelectedIndex(index);
			//Enforce this Panel
			parent.validate();
		
		
			//Re-pack the window based on this new panel
			java.awt.Container p = parent.getParent();
			p = p.getParent();
			p = p.getParent();
			p = p.getParent();
			JFrame frame = (JFrame)p;
			frame.pack();

			return;
		}

		
		
		//Create the network
		DynamicGraph graph = bam.generate();
		CyNetwork randomNet = CytoscapeConversion.DynamicGraphToCyNetwork(graph,null);
		graph = null;
		
		//Switch to the Network view
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)
				.setSelectedIndex(0);

		//returns CytoscapeWindow's VisualMappingManager object
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
    
		//gets the global catalog of visual styles and calculators
		CalculatorCatalog catalog = vmm.getCalculatorCatalog();
		
		//get the random network visual style
		VisualStyle newStyle = catalog.getVisualStyle("random network");
		
		//Set this as the current visual style
		vmm.setVisualStyle(newStyle);


		GridNodeLayout alg = new GridNodeLayout();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		view.applyLayout(alg); 

		//Traverse to the JFrame parent and close this window
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JFrame frame = (JFrame)p;
		frame.dispose();

	}

}
