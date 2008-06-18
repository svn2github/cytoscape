/*  File: WattsStrogatzDialog.java
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

import java.awt.Font;
import javax.swing.*;




/*
 * This class is responsible for handling the user interface
 * to generate a random model according to watts-strogatz model
 * 
 */
public class WattsStrogatzDialog extends JPanel {
	
	int mode;

	//TextFields
	private javax.swing.JTextField nodeTextField;
	private javax.swing.JTextField betaTextField;
	private javax.swing.JTextField degreeTextField;

	//Buttons
	private javax.swing.JButton runButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JCheckBox directedCheckBox;
	private javax.swing.JCheckBox selfEdgeCheckBox;

	//Labels
	private javax.swing.JLabel titleLabel;
	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel betaLabel;
	private javax.swing.JLabel degreeLabel;


	/*
	 * Default constructor
	 */
	public WattsStrogatzDialog(int pMode ){
		super( );
		mode = pMode;
		initComponents();
	}
	
	
	/*
	 * Initialize the components
	 */
	private void initComponents() {
	
		//Create the TextFields
		nodeTextField = new javax.swing.JTextField();
		betaTextField = new javax.swing.JTextField();
		degreeTextField = new javax.swing.JTextField();

		//Create the buttons
		directedCheckBox = new javax.swing.JCheckBox();
		selfEdgeCheckBox = new javax.swing.JCheckBox();
		runButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		//Create the Labels
		titleLabel = new javax.swing.JLabel();
		nodeLabel = new javax.swing.JLabel();
		degreeLabel = new javax.swing.JLabel();
		betaLabel = new javax.swing.JLabel();

		//Set the text on the labels
		nodeLabel.setText("Number of Nodes:");
		betaLabel.setText("<html> &#x3B2; :</html>");
		degreeLabel.setText("Node Degree:");
		selfEdgeCheckBox.setText("Allow reflexive Edges (u,u)");
		directedCheckBox.setText("Undirected");

		//Set up the title for the panel
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Generate Barabasi-Albert Model");

		//Set up the Generat button
		runButton.setText("Generate");
		runButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				runButtonActionPerformed(evt);
			}
		});

		//Set up the Cancel Button
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		//Create the layout
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		setLayout(layout);
		
		layout.setHorizontalGroup(layout
			.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout.createSequentialGroup()
					.addContainerGap()
					.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
					.add(titleLabel,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
						350,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						
					//Add the node group
					.add(layout.createSequentialGroup()
						.add(nodeLabel,
							 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							 20,
							 170)
						.add(nodeTextField,
							 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							10,
							50))
							
					//Add the beta group 
					.add(layout.createSequentialGroup()
						.add(betaLabel,
							 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							 20,
							 170)
						.add(betaTextField,
							 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							 10,
							 50))
					//Add the group latout for degree
					.add(layout.createSequentialGroup()
						.add(degreeLabel,
							 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							 20,
							 170)
						.add(degreeTextField,
							org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
							10,
							50))
					//.add(layout.createSequentialGroup()
					.add(directedCheckBox)//)
					
					//Add the Run/Cancel buttons
					.add(org.jdesktop.layout.GroupLayout.TRAILING,
						 layout.createSequentialGroup()
						 .add(runButton)
						 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						 .add(cancelButton)))
				.addContainerGap()));

	
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
			.add(layout.createSequentialGroup()
				.addContainerGap()
				.add(titleLabel)
				.add(8, 8, 8)
				.add(7, 7, 7)
				.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(nodeLabel)
					.add(nodeTextField))

				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(betaLabel)
					.add(betaTextField))
				
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(degreeLabel)
					.add(degreeTextField))

				.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
								 3, Short.MAX_VALUE)

				//.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
				.add(directedCheckBox)//)
				
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(cancelButton)
					.add(runButton))
			.addContainerGap()));

	}



	/*
	 *  Callback for the cancel button
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {

		//Go to the Dialog and dispose this window
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JDialog dialog = (JDialog)p;
		dialog.dispose();
	}

	/*
	 *  Callback for the generate button
	 */
	private void runButtonActionPerformed(java.awt.event.ActionEvent evt) 
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
		} catch (Exception e) {
			//If an error occurs than change the colors
			degreeLabel.setForeground(java.awt.Color.BLACK);
			nodeLabel.setForeground(java.awt.Color.RED);
			betaLabel.setForeground(java.awt.Color.BLACK);
			return;
		}

		//Try to read string into an double
		try {
			beta = Double.parseDouble(betaString);

			//Check to make sure beta is a probability
			if((beta < 0) || (beta > 1))
				throw (new Exception());
		} catch (Exception e) {
			//If an error occurs than change the colors
			degreeLabel.setForeground(java.awt.Color.BLACK);
			nodeLabel.setForeground(java.awt.Color.BLACK);
			betaLabel.setForeground(java.awt.Color.RED);
			return;
		}

		//Try to read this string into an integer
		try {
			degree = Integer.parseInt(degreeString);
		} catch (Exception e) {
			//If an error occurs than change the colors		
			degreeLabel.setForeground(java.awt.Color.RED);
			nodeLabel.setForeground(java.awt.Color.BLACK);
			betaLabel.setForeground(java.awt.Color.BLACK);
			return;
		}
		
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
			AnalyzePanel analyzePanel = new AnalyzePanel(wsm, wsm.getDirected());
		
			//Get the TabbedPanel
			JTabbedPane parent = (JTabbedPane)getParent();
			int index = parent.getSelectedIndex();
			
			//Remove this Panel
			parent.remove(index);
			//Replace it with the panel
			parent.add(analyzePanel, index);
			//Set the title for this panel
			parent.setTitleAt(index,"Analyze network statistics");
			//Display this panel
			parent.setSelectedIndex(index);
			//Enforce this Panel
			parent.validate();
		
		
			//Re-pack the window based on this new panel
			java.awt.Container p = parent.getParent();
			p = p.getParent();
			p = p.getParent();
			p = p.getParent();
			JDialog dialog = (JDialog)p;
			dialog.pack();

			return;
		}

		
		//Generate the random network
		CyNetwork randomNet = wsm.generate();

		//Change to the Network view
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)
				.setSelectedIndex(0);


		//returns CytoscapeWindow's VisualMappingManager object
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		//gets the global catalog of visual styles and calculators
		CalculatorCatalog catalog = vmm.getCalculatorCatalog();
		//Get the visualStyle for random networks
		VisualStyle newStyle = catalog.getVisualStyle("random network");
		//set this as the current visualStyle
		vmm.setVisualStyle(newStyle);


		//Go up to the Dialog and close this window
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JDialog dialog = (JDialog)p;
		dialog.dispose();

	}

}
