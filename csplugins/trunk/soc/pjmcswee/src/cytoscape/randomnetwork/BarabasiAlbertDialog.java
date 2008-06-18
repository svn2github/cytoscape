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

import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import giny.view.*;

import java.awt.*;
import java.awt.Font;
import javax.swing.*;

/*
 * This class is responsible for handling the user interface
 * to generate a random model according to barabasi-albert.
 * 
 */
public class BarabasiAlbertDialog extends JPanel {

	private int mode;

	// TextFields
	private javax.swing.JTextField nodeTextField;
	private javax.swing.JTextField initTextField;
	private javax.swing.JTextField edgeTextField;

	// Buttons
	private javax.swing.JButton runButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JCheckBox directedCheckBox;

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
	
	
		final int textFieldStart = 10;
		final int textFieldLength = 50;
		final int labelStart = 20;
		final int labelLength = 170;
 
	
	
		// TextField creation
		nodeTextField = new javax.swing.JTextField();
		initTextField = new javax.swing.JTextField();
		edgeTextField = new javax.swing.JTextField();

		// Button creation
		directedCheckBox = new javax.swing.JCheckBox();
		runButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		// Label creation
		titleLabel = new javax.swing.JLabel();
		nodeLabel = new javax.swing.JLabel();
		initLabel = new javax.swing.JLabel();
		edgeLabel = new javax.swing.JLabel();
		explainLabel = new javax.swing.JLabel();

		//Add the explanation label
		explainLabel
				.setText("<html><font size=2 face=Verdana>" +
				"The Barabasi-Albert model begins with a connected seed network of s nodes.<br>" +
				"Every other node (n - s) is added one at a time,<br>" +
				"and initially connected to m existing nodes.<br>" +
				"Each existing node u has probability  degree(u)/(2*E), E is the number of edges </font></html>");

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

		//Create the layout for the dialog (really a panel)
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		//Make the laout active.
		setLayout(layout);
		
		//Set the Horizontal Layout
		layout.setHorizontalGroup(layout
			.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout.createSequentialGroup()
					.addContainerGap()
						.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
							.add(titleLabel,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								350,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)

							//Add the dscription
							.add(explainLabel)
							
							//Add the node label & textField
							.add(layout.createSequentialGroup()
								.add(nodeLabel,
									org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
									labelStart,
									labelLength)
								.add(nodeTextField,
									org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
									textFieldStart,
									textFieldLength))
							//Add the initial graph size: s
							.add(layout.createSequentialGroup()
								.add(initLabel,
									 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
									 labelStart,
									 labelLength)
								.add(initTextField,
									 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
									 textFieldStart,
									 textFieldLength))
							//Add the number of edges to add edges: m
							.add(layout.createSequentialGroup()
								.add(edgeLabel,
									 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
									 labelStart,
									 labelLength)
								.add(edgeTextField,
									org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
									textFieldStart,
									textFieldLength))
							//Add the directed button
							//.add(layout.createSequentialGroup()
							.add(directedCheckBox) //)
							//Add the Run/Cancel buttons
							.add(org.jdesktop.layout.GroupLayout.TRAILING,
								layout.createSequentialGroup()
									.add(runButton)
									.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
									.add(cancelButton)))
					.addContainerGap()));
		
		//Set the vertical Layout
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
			.add(layout.createSequentialGroup()
				.addContainerGap()
				.add(titleLabel) //Add title
				.add(8, 8, 8)
				.add(explainLabel)  //Add the description
				.add(7, 7, 7)
				.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
				//Add the node label textfield pair: n
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(nodeLabel)
					.add(nodeTextField))
				//add the initial number of nodes: s
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(initLabel)
					.add(initTextField))
				//add the number of edges to add at each time step: m
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(edgeLabel)
					.add(edgeTextField))
				.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
								  3, Short.MAX_VALUE)
				//Add the directed checkbox
				//.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
				.add(directedCheckBox)//)
				//Add the Run/Cancel buttons
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
					.add(cancelButton)
					.add(runButton))
			.addContainerGap()));

	}

	/*
	 *  Callback for the cancel button
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {

		//Go up through the parents and close the window
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
			AnalyzePanel analyzePanel = new AnalyzePanel(bam, bam.getDirected());
		
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

		
		
		//Create the network
		CyNetwork randomNet = bam.generate();

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

		//Traverse to the JDialog parent and close this window
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JDialog dialog = (JDialog)p;
		dialog.dispose();

	}

}
