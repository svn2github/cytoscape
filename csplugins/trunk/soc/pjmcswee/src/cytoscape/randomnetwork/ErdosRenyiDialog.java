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

import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import giny.view.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.*;

/*

*/
public class ErdosRenyiDialog extends JPanel implements ActionListener {

	int numNodes; // number of nodes

	double probability;

	int numEdges;

	boolean directed;

	boolean allowSelfEdge;

	private javax.swing.JTextField nodeTextField;

	private javax.swing.JTextField probabilityTextField;

	private javax.swing.JTextField edgeTextField;

	private javax.swing.JCheckBox directedCheckBox;

	private javax.swing.JCheckBox selfEdgeCheckBox;

	private javax.swing.JButton runButton;

	private javax.swing.JButton cancelButton;

	private javax.swing.JLabel titleLabel;

	private javax.swing.JLabel nodeLabel;

	private javax.swing.JLabel probabilityLabel;

	private javax.swing.JLabel edgeLabel;


	private javax.swing.ButtonGroup group;

	private javax.swing.JCheckBox gnp;

	private javax.swing.JCheckBox gnm;

	private javax.swing.JLabel gnpExplain;

	private javax.swing.JLabel gnmExplain;

	public ErdosRenyiDialog( ){
		super( );
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

		gnmExplain
				.setText("<html><font size=2 face=Verdana>Uniformly generate a random <br> graph with n nodes and m edges.</font></html>");
		gnpExplain
				.setText("<html><font size=2 face=Verdana>Generate a random graph with n <br> nodes and each edge has probability p to be included.</font></html>");


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

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		setLayout(layout);

		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																titleLabel,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																350,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)

														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				gnm,
																				1,// org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				1,
																				80)
																		.add(
																				gnmExplain,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				Short.MAX_VALUE))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				gnp,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				70)
																		.addPreferredGap(
																				1)
																		.add(
																				gnpExplain,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				Short.MAX_VALUE))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				nodeLabel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				20,
																				150)
																		// .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				nodeTextField,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				70))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				probabilityLabel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				20,
																				150)
																		// .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				probabilityTextField,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				70))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				edgeLabel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				20,
																				150)
																		// .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				edgeTextField,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				70))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				directedCheckBox,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				Short.MAX_VALUE))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				selfEdgeCheckBox,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				Short.MAX_VALUE)) 
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																layout
																		.createSequentialGroup()
																		.add(
																				runButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				cancelButton)))
										.addContainerGap()));

		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(titleLabel)
										.add(8, 8, 8)

										.add(7, 7, 7)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)

														.add(gnm).add(
																gnmExplain))

										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED,
												3, Short.MAX_VALUE)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(gnp).add(
																gnpExplain))

										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(nodeLabel).add(
																nodeTextField))
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(probabilityLabel)
														.add(
																probabilityTextField))
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(edgeLabel).add(
																edgeTextField))
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														// .add(directedLabel)
														.add(
																directedCheckBox))
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														// .add(selfEdgeLabel)
														.add(
																selfEdgeCheckBox))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED,
												3, Short.MAX_VALUE)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(cancelButton).add(
																runButton))
										.addContainerGap()));

	}

	/*
	 *  Call back for the cancel button
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {

		//Go up to the JDialog parent and close this window
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JDialog dialog = (JDialog)p;
		dialog.dispose();
	}

	/*
	 * Call back for the generate button
	 */
	private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {

		String numNodeString = nodeTextField.getText();

		try {
			numNodes = Integer.parseInt(numNodeString);
		} catch (Exception e) {
			nodeLabel.setForeground(java.awt.Color.RED);
			return;
		}

		nodeLabel.setForeground(java.awt.Color.BLACK);

		directed = false;
		if (directedCheckBox.isSelected()) {
			directed = true;
		}

		allowSelfEdge = false;
		if (selfEdgeCheckBox.isSelected()) {
			allowSelfEdge = true;
		}

		System.out.println(directed);

		if (gnm.isSelected()) {
			String edgeString = edgeTextField.getText();

			try {
				numEdges = Integer.parseInt(edgeString);
			} catch (Exception e) {
				probabilityLabel.setForeground(java.awt.Color.BLACK);
				edgeLabel.setForeground(java.awt.Color.RED);
				//pack();
				return;
			}

			ErdosRenyiModel erm = new ErdosRenyiModel(numNodes, numEdges,
					allowSelfEdge, directed);
			erm.Generate();
		} else {
			String probabilityString = probabilityTextField.getText();

			try {
				probability = Double.parseDouble(probabilityString);
			} catch (Exception e) {
				probabilityLabel.setForeground(java.awt.Color.RED);
				edgeLabel.setForeground(java.awt.Color.BLACK);
				//pack();
				return;
			}

			ErdosRenyiModel erm = new ErdosRenyiModel(numNodes, allowSelfEdge,
					!directed, probability);
			erm.Generate();
		}

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



		//Go up to the parent window and close it		
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JDialog dialog = (JDialog)p;
		dialog.dispose();

	}

	/*
	 * Call back for when one of the gnm or gnp buttons is selected
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		//When pushed turn off one of the three 
		//TextFields

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