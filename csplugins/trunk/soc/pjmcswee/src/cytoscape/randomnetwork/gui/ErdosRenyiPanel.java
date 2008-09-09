/*  File: ErdosRenyiDialog.java
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
 * 
 *
 */
public class ErdosRenyiPanel extends RandomNetworkPanel implements ActionListener {


	/**
	 * 
	 */
	private int mode;
	
	/**
	 * Default values for the variables
	 */
	private static final int defaultNodeValue = 100;
	private static final int defaultEdgeValue = 300;
	private static final double defaultProbValue = .05;
	
	//TextFields
	private javax.swing.JTextField nodeTextField;
	private javax.swing.JTextField probabilityTextField;
	private javax.swing.JTextField edgeTextField;

	//Buttons
	private javax.swing.JCheckBox directedCheckBox;
	private javax.swing.JCheckBox selfEdgeCheckBox;
	private javax.swing.ButtonGroup group;
	private javax.swing.JCheckBox gnp;
	private javax.swing.JCheckBox gnm;

	//Labels
	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel probabilityLabel;
	private javax.swing.JLabel edgeLabel;
	private javax.swing.JLabel gnpExplain;
	private javax.swing.JLabel gnmExplain;
	private javax.swing.JSeparator line;

	/**
	 * Default Contructor
	 *
	 * @param pMode Gives the dialog context.  If mode == 0, then the dialog creates a 
	 */
	public ErdosRenyiPanel(int pMode){
		super(null);
		mode = pMode;
		initComponents();
	}

	/**
	 * Default Contructor
	 *
	 * @param pMode Gives the dialog context.  If mode == 0, then the dialog creates a 
	 */
	public ErdosRenyiPanel(int pMode, RandomNetworkPanel pPrevious){
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
		return new String("Erdos Renyi Model");
	}

	/**
	*
	*/
	public String getDescription()
	{
		return new String("Generate an Erdos-Renyi network." + 
		"  Erdos-Renyi networks have flat degree distributions and are therefore not scale-free."+
		"  Constraints:  0 &#8804; probability &#8804; 1, 0 < Nodes,  0 < Edges.");
	}



	/**
	 * Initialize the components
	 */
	private void initComponents() {



		//Create TextFields
		nodeTextField = new javax.swing.JTextField();
		probabilityTextField = new javax.swing.JTextField();
		edgeTextField = new javax.swing.JTextField();
	
		//Set up the default values for the textfields
		nodeTextField.setText("" + defaultNodeValue);
		nodeTextField.setHorizontalAlignment(JTextField.RIGHT);
		probabilityTextField.setText("" + defaultProbValue);
		probabilityTextField.setHorizontalAlignment(JTextField.RIGHT);
		edgeTextField.setText("" + defaultEdgeValue);			
		edgeTextField.setHorizontalAlignment(JTextField.RIGHT);
		edgeTextField.setPreferredSize(new Dimension(50,25)); 
		probabilityTextField.setPreferredSize(new Dimension(50,25)); 
		nodeTextField.setPreferredSize(new Dimension(50,25)); 				
		
		
		//Buttons
		directedCheckBox = new javax.swing.JCheckBox();
		selfEdgeCheckBox = new javax.swing.JCheckBox();
		group = new javax.swing.ButtonGroup();
		gnm = new javax.swing.JCheckBox();
		gnp = new javax.swing.JCheckBox();
		
		//Labels
		gnpExplain = new javax.swing.JLabel();
		gnmExplain = new javax.swing.JLabel();
		probabilityLabel = new javax.swing.JLabel();
		nodeLabel = new javax.swing.JLabel();
		edgeLabel = new javax.swing.JLabel();

		//Set up the group
		gnmExplain.setOpaque(true);
		gnpExplain.setOpaque(true);
		gnm.addActionListener(this);
		gnp.addActionListener(this);
		gnm.setSelected(true);
		group.add(gnm);
		group.add(gnp);

		JSeparator hLine =  new JSeparator(SwingConstants.HORIZONTAL);
		line = new JSeparator(SwingConstants.VERTICAL);
		
		//Set the text for the labels
		directedCheckBox.setText("Undirected");
		selfEdgeCheckBox.setText("Allow Reflexive Edges (u,u)");
		gnp.setText("G(n,p)");
		gnm.setText("G(n,m)");
		nodeLabel.setText("Number of Nodes (n):");
		edgeLabel.setText("Number of Edges (m):");
		probabilityLabel.setText("Edge Probability (p):   ");
		
		//Add the descriptions for each mode
		gnmExplain.setText("<html><font size=2 face=Verdana>Uniformly generate a random " +
				         "graph with n nodes and m edges.</font></html>");
		gnpExplain.setText("<html><font size=2 face=Verdana>Generate a random graph with n " +
				         "nodes and each edge has probability p to be included.</font></html>");
		gnmExplain.setPreferredSize(new Dimension(350,40));
		gnpExplain.setPreferredSize(new Dimension(350,40));
		gnmExplain.setMinimumSize(new Dimension(350,40));
		gnpExplain.setMinimumSize(new Dimension(350,40));
		
		
		//Initially turn off this textfield
		probabilityTextField.setEnabled(false);
		probabilityTextField.setBackground(Color.LIGHT_GRAY);
		
	
		
		

		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,0,5);		
		c.gridx = 0;
		c.gridy = 0;
		add(gnm,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,0,5);		
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 5;
		c.weightx = 1;
		c.weighty = 1;

		add(gnmExplain,c);
		
		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,0,5);		
		c.gridx = 0;
		c.gridy = 1;
		add(gnp,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(15,5,0,5);		
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 5;
		c.weightx = 1;
		c.weighty = 1;

		add(gnpExplain,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 20;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		add(hLine,c);


		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,10,0,0);		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 3;
		add(nodeLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.insets = new Insets(0,10,0,0);		
		c.anchor = GridBagConstraints.LINE_START;	
		c.gridx = 3;
		c.gridy = 3;
		add(nodeTextField,c);


		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,10,0,0);		
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 3;
		add(edgeLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.insets = new Insets(0,10,0,10);		
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 3;
		c.gridy = 4;
		add(edgeTextField,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,10,0,10);		
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 3;
		add(probabilityLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,10,5,10);		
		c.gridx = 3;
		c.gridy = 5;
		c.gridwidth = 1;
		c.gridheight = 1;
		add(probabilityTextField,c);

		//add the line
		c = null;
		c = new GridBagConstraints();
		c.insets = new Insets(5,0,0,0);		
		c.gridx = 4;
		c.gridheight = 5;
		c.gridy = 2;
		c.fill = GridBagConstraints.VERTICAL;

		add(line, c);




		//Set up the directed checkbox
		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,10,0,0);		
		c.gridx = 5;
		c.gridy = 3;
		c.weightx = 1;
		c.weighty = 1;
		add(directedCheckBox,c);

		//Set up the allow self edge checkbox
		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,10,0,0);		
		c.gridx = 5;
		c.gridy = 4;

		c.weightx = 1;
		c.weighty = 1;
		add(selfEdgeCheckBox,c);

	}




	/**
	 * Call back for the generate button
	 */
	public RandomNetworkPanel next()
	{
		
		// number of nodes
		int numNodes; 
	
		//Edge probability
		double probability;

		//Number of edges
		int numEdges;

		//Is directed?
		boolean directed;

		//Allow reflexive edges?
		boolean allowSelfEdge;


		//Get the string from the node text field
		String numNodeString = nodeTextField.getText();
		
		//Try to parse the string as an integer
		//If there is an error color the label and exit
		try {
			numNodes = Integer.parseInt(numNodeString);
			if(numNodes < 0)
			{
				throw new Exception("There must be at least one node in the network.");
			}			
		} catch (Exception e) {
			nodeLabel.setForeground(java.awt.Color.RED);
			return this;
		}
		
		//If we passed this set the label to black
		nodeLabel.setForeground(java.awt.Color.BLACK);

		//Read the value of the directed check box
		directed = false;
		if (directedCheckBox.isSelected()) {
			directed = true;
		}

		//read the value of the reflexive check box
		allowSelfEdge = false;
		if (selfEdgeCheckBox.isSelected()) {
			allowSelfEdge = true;
		}

		//Create a mode 
		ErdosRenyiModel erm = null;


		//If G(n,m) model
		if (gnm.isSelected()) {
		
			//Get the string from the number of edges text field
			String edgeString = edgeTextField.getText();

			//Try to parse this as an integer
			//If there is an error color the label and exit
			try {
				numEdges = Integer.parseInt(edgeString);
				if(numEdges < 0)
				{
					throw new Exception("No such thing as negative edges.");
				}

				
			} catch (Exception e) {
				probabilityLabel.setForeground(java.awt.Color.BLACK);
				edgeLabel.setForeground(java.awt.Color.RED);
				return this;
			}
			edgeLabel.setForeground(java.awt.Color.BLACK);
			
			//Create the model using the number of edges 
			erm = new ErdosRenyiModel(numNodes, numEdges,
					allowSelfEdge, !directed);
	
		} 
		else 
		{
			//Get the string from the probability text field
			String probabilityString = probabilityTextField.getText();

			//try to parse the string as a double
			//If there is an error color the label and exit
			try {
				probability = Double.parseDouble(probabilityString);
				if((probability < 0) || (probability > 1))
				{
					throw new Exception("Must be a probability");
				}

			} catch (Exception e) {
				probabilityLabel.setForeground(java.awt.Color.RED);
				edgeLabel.setForeground(java.awt.Color.BLACK);
				return this;
			}
			
			probabilityLabel.setForeground(java.awt.Color.BLACK);
			
			//Create the model
			erm = new ErdosRenyiModel(numNodes, probability, allowSelfEdge,
					!directed);
		}
		
		
		
		
		if(mode == 1)
		{
			
			erm.setCreateView(false);
			
			if(mNext == null)
			{
				mNext = new AnalyzePanel(this,erm, erm.getDirected());
			}
			else
			{
				((AnalyzePanel)mNext).setDirected(erm.getDirected());
				((AnalyzePanel)mNext).setGenerator(erm);		
			}

			return mNext;
		}
		
		
		//Generate the network
		RandomNetwork graph = erm.generate();
		CyNetwork network = graph.toCyNetwork();
		graph = null;
		
				
			
		
		return this;
	}

	/*
	 * Call back for when one of the gnm or gnp buttons is selected
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		//When pushed turn off one of the three 
		//TextFields, according to the model that was selected
		if ("G(n,m)".equals(e.getActionCommand())) {
			probabilityTextField.setEnabled(false);
			probabilityTextField.setBackground(Color.LIGHT_GRAY);
			edgeTextField.setBackground(Color.WHITE);
			edgeTextField.setEnabled(true);
		} else if ("G(n,p)".equals(e.getActionCommand())) {
			probabilityTextField.setEnabled(true);
			probabilityTextField.setBackground(Color.WHITE);
			edgeTextField.setBackground(Color.LIGHT_GRAY);
			edgeTextField.setEnabled(false);
		}
	}

}