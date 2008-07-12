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

package cytoscape.randomnetwork;

import cytoscape.layout.algorithms.*;
import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import giny.view.*;
import cytoscape.graph.dynamic.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

import java.util.*;
import javax.swing.*;

/*

*/
public class ErdosRenyiDialog extends JPanel implements ActionListener {

	private int mode;

	
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
	private javax.swing.JButton runButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton backButton;

	//Labels
	private javax.swing.JLabel titleLabel;
	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel probabilityLabel;
	private javax.swing.JLabel edgeLabel;
	private javax.swing.JLabel gnpExplain;
	private javax.swing.JLabel gnmExplain;


	/**
	* Default Contructor
	*/
	public ErdosRenyiDialog(int pMode){
		super( );
		mode = pMode;

		initComponents();
	}

	/*
	 * Initialize the components
	 */
	private void initComponents() {

		//Create TextFields
		nodeTextField = new javax.swing.JTextField();
		probabilityTextField = new javax.swing.JTextField();
		edgeTextField = new javax.swing.JTextField();
	
		//Buttons
		directedCheckBox = new javax.swing.JCheckBox();
		selfEdgeCheckBox = new javax.swing.JCheckBox();
		group = new javax.swing.ButtonGroup();
		gnm = new javax.swing.JCheckBox();
		gnp = new javax.swing.JCheckBox();
		runButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		backButton = new javax.swing.JButton();
		
		//Labels
		gnpExplain = new javax.swing.JLabel();
		gnmExplain = new javax.swing.JLabel();
		titleLabel = new javax.swing.JLabel();
		probabilityLabel = new javax.swing.JLabel();
		nodeLabel = new javax.swing.JLabel();
		edgeLabel = new javax.swing.JLabel();

		//Set up the group
		gnmExplain.setOpaque(true);
		gnpExplain.setOpaque(true);
		gnm.addActionListener(this);
		gnp.addActionListener(this);
		gnp.setSelected(true);
		group.add(gnm);
		group.add(gnp);


		//Set the text for the labels
		directedCheckBox.setText("Undirected");
		selfEdgeCheckBox.setText("Allow Reflexive Edges (u,u)");
		gnp.setText("G(n,p)");
		gnm.setText("G(n,m)");
		nodeLabel.setText("Number of Nodes (n):");
		edgeLabel.setText("Number of Edges (m):");
		probabilityLabel.setText("Edge Probability (p):   ");
		
		//Add the descriptions for each mode
		gnmExplain
				.setText("<html><font size=2 face=Verdana>Uniformly generate a random <br>" +
				         "graph with n nodes and m edges.</font></html>");
		gnpExplain
				.setText("<html><font size=2 face=Verdana>Generate a random graph with n <br>" +
				         "nodes and each edge has probability p to be included.</font></html>");


		//Initially turn off this textfield
		edgeTextField.setEnabled(false);
		edgeTextField.setBackground(Color.LIGHT_GRAY);
		

		//setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Generate Erdos-Renyi Model");


		//Setup the generate button
		runButton.setText("Generate");
		runButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				runButtonActionPerformed(evt);
			}
		});

		//Setup the cancel button
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



		//Create the layout
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		setLayout(layout);


		layout.setHorizontalGroup(
			layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout.createSequentialGroup()
					.addContainerGap()
					.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
					.add(titleLabel,
						 org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
						 350,
						 org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)

					//Add the G(n,m) group						 
					.add(layout.createSequentialGroup()
						.add(gnm,1,1,80)
						.add(gnmExplain,
							org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							10,
							Short.MAX_VALUE))
							
					//Add the G(n,p) group
					.add(layout.createSequentialGroup()
						.add(gnp,
							org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							10,
							70)
						.add(gnpExplain,
							 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							 10,
							 Short.MAX_VALUE))

					//Add the node group
					.add(layout	.createSequentialGroup()
						.add(nodeLabel,
							org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							20,
							150)
						.add(nodeTextField,
							org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							10,
							70))
					//Add the probability group
					.add(layout.createSequentialGroup()
						.add(probabilityLabel,
							org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							20,
							150)
						.add(probabilityTextField,
							org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							10,
							70))
							
					//Add the edge group
					.add(layout.createSequentialGroup()
						.add(edgeLabel,
							org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							20,
							150)
						.add(edgeTextField,
							org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							10,
							70))
					//Add the directed checkbox
					//.add(layout.createSequentialGroup()
					.add(directedCheckBox,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						10,
						Short.MAX_VALUE)//)
					//.add(layout.createSequentialGroup()
					//Add the reflexive edge checkbox
					.add(selfEdgeCheckBox,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						10,
						Short.MAX_VALUE)//) 
					//Add the Run/Cancel button	
					.add(org.jdesktop.layout.GroupLayout.TRAILING,
						layout.createSequentialGroup()
							.add(backButton)
							.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
							.add(runButton)
							.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
							.add(cancelButton)))
				.addContainerGap()));

		//Set the vertical layout
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
			.add(layout.createSequentialGroup()
				.addContainerGap()
				.add(titleLabel) //Add the title
				.add(8, 8, 8)
				.add(7, 7, 7)
				.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
				//Add the G(n,m) group
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(gnm)
					.add(gnmExplain))
					
				//Add the G(n,p) group
				.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
								 3, Short.MAX_VALUE)
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(gnp)
					.add(gnpExplain))
				
				//Add the node group
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(nodeLabel)
					.add(nodeTextField))
					
				//Add the probability group
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(probabilityLabel)
					.add(probabilityTextField))

				//Add the edge group
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(edgeLabel)
					.add(edgeTextField))

				//.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
				.add(directedCheckBox)//)

				//.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
				.add(selfEdgeCheckBox)//)

				.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
								 3, Short.MAX_VALUE)
								 
				//Add the Run/Cancel group
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(cancelButton)
					.add(runButton)
					.add(backButton))
			.addContainerGap()));

	}

	/**
	 *  Call back for the cancel button
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {

		//Go up to the JFrame parent and close this window
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JFrame frame = (JFrame)p;
		frame.dispose();
	}


	/**
	 *  Call back for the cancel button
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
		parent.setTitleAt(index,"Generate Random Networks");
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
	 * Call back for the generate button
	 */
	private void runButtonActionPerformed(java.awt.event.ActionEvent evt) 
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
		} catch (Exception e) {
			nodeLabel.setForeground(java.awt.Color.RED);
			return;
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
			} catch (Exception e) {
				probabilityLabel.setForeground(java.awt.Color.BLACK);
				edgeLabel.setForeground(java.awt.Color.RED);
				return;
			}
			
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
			} catch (Exception e) {
				probabilityLabel.setForeground(java.awt.Color.RED);
				edgeLabel.setForeground(java.awt.Color.BLACK);
				return;
			}
		
			//Create the model
			erm = new ErdosRenyiModel(numNodes, allowSelfEdge,
					!directed, probability);
		}
		
		
		
		
		
		
		
		if(mode == 1)
		{
			
			erm.setCreateView(false);
			AnalyzePanel analyzePanel = new AnalyzePanel(erm, erm.getDirected(), 01);
		
			//Get the TabbedPanel
			JTabbedPane parent = (JTabbedPane)getParent();
			int index = parent.getSelectedIndex();
			System.out.println(index);
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
		
		
		//Generate the network
		DynamicGraph graph = erm.generate();
		CyNetwork network = CytoscapeConversion.DynamicGraphToCyNetwork(graph,null);
		graph = null;
				
		//Set the network pane as active
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)
				.setSelectedIndex(0);
		
		
		//returns CytoscapeWindow's VisualMappingManager object
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		//gets the global catalog of visual styles and calculators
		CalculatorCatalog catalog = vmm.getCalculatorCatalog();
		//Get the random network vistualStyle
		VisualStyle newStyle = catalog.getVisualStyle("random network");
		//Set this as the current visualStyle
		vmm.setVisualStyle(newStyle);
		

		GridNodeLayout alg = new GridNodeLayout();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		view.applyLayout(alg); 
		

		//Go up to the parent window and close it		
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JFrame frame = (JFrame)p;
		frame.dispose();

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