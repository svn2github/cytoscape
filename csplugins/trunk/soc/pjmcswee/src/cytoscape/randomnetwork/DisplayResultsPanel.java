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

package cytoscape.randomnetwork;

import java.util.*;
import cytoscape.plugin.*;
import cytoscape.layout.algorithms.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import giny.view.*;
import cytoscape.graph.dynamic.*;
import cytoscape.graph.dynamic.util.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;


/*
 * RandomizeExistingPanel is used for selecting which randomizing 
 * network model to use.
 */
public class DisplayResultsPanel extends JPanel {

	private int mode;

	//Next Button
	private javax.swing.JButton runButton;
	//Cancel Button
	private javax.swing.JButton cancelButton;
	//Back Button
	private javax.swing.JButton backButton;
	//Title Label
	private javax.swing.JLabel titleLabel;
	//Group together the different options
	private javax.swing.ButtonGroup group;

	//Treat this network as directed
	private javax.swing.JCheckBox directedCheckBox;

	//Checkbox for erdos-renyi model
	private javax.swing.JCheckBox degreePreserving;
	//Checkbox for barabasi-albert model
	private javax.swing.JLabel degreePreservingExplain;
	

	/*
	 *  Default constructor
	 */
	public DisplayResultsPanel(int pMode ){
		
		super( ); 
		mode = pMode;
		initComponents();
	}

	/*
	 * Initialize the components
	 */
	private void initComponents() {

		//Create the erdos-renyi checkbox
		degreePreserving = new javax.swing.JCheckBox();
	
		
		//Create the barabasi-albert  label
		degreePreservingExplain = new javax.swing.JLabel();

		//Set the erdos-renyi text
		degreePreservingExplain
				.setText("<html><font size=2 face=Verdana>Generate a random network <br> graph with n nodes and m edges.</font></html>");

		
		
		directedCheckBox = new javax.swing.JCheckBox();

		//set the labels to opaque
		degreePreservingExplain.setOpaque(true);
		
		//Set the text for the checkboxes
		degreePreserving.setText("shuffle edges keeping degree Model");

		//Make barabasi-albert the default
		degreePreserving.setSelected(true);
		
		//Create the butons
		runButton = new javax.swing.JButton();
		backButton = new javax.swing.JButton(); 
		cancelButton = new javax.swing.JButton();

		//Set up the title
		titleLabel = new javax.swing.JLabel();
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Randomize Network");

	

			

	
		//Set up the run button
		runButton.setText("Next");
		runButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				runButtonActionPerformed(evt);
			}
		});
		
		//Set up the run button
		backButton.setText("Back");
		backButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				backButtonActionPerformed(evt);
			}
		});

		if(mode == 0)
		{
			backButton.setVisible(false);
		}

		//Set up the cancel button
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});


		//Set up the layout
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
																				degreePreserving,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				170)
																		.addPreferredGap(1)
																		
																		.add(degreePreservingExplain,
																			 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			 10,
																			 Short.MAX_VALUE))
																											
													
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																layout
																		.createSequentialGroup()
																		.add(
																				backButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
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

														.add(degreePreserving).add(
																degreePreservingExplain))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED,
												3, Short.MAX_VALUE)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(cancelButton).add(
																runButton).add(backButton))
										.addContainerGap()));
	}




	
	/**
	 *  Call back for the cancel button
	 */
	private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
	
		RandomComparisonPanel compareToRandom = new RandomComparisonPanel();

		//Get the TabbedPanel
		JTabbedPane parent = (JTabbedPane)getParent();
		int index = parent.getSelectedIndex();
			
		//Remove this Panel
		parent.remove(index);
		
		//Replace it with the panel
		parent.add(compareToRandom, index);
		//Set the title for this panel
		parent.setTitleAt(index,"Compare to Random Network");
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



	/*
	 * cancelButtonActionPerformed call back when the cancel button is pushed
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		//Go up through the parents to the main window
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JDialog dialog = (JDialog)p;
		dialog.dispose();
	}
	
	/*
	 *  Callback for when the "Next" button is pushed
	 */
	private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		
		CyNetwork net = Cytoscape.getCurrentNetwork();

		LinkedList network = CytoscapeConversion.CyNetworkToDynamicGraph(net,false);

		DynamicGraph graph = (DynamicGraph)network.get(0);

		String ids[] = (String[])network.get(1);
		DegreePreservingNetworkRandomizer dpnr = new DegreePreservingNetworkRandomizer(graph,ids,false);


		boolean directed = directedCheckBox.isSelected();


		if(mode == 1)
		{
			AnalyzePanel analzyePanel = new AnalyzePanel(dpnr, directed);
			
			//Get the TabbedPanel
			JTabbedPane parent = (JTabbedPane)getParent();
			int index = parent.getSelectedIndex();
			
			//Remove this Panel
			parent.remove(index);
			//Replace it with the panel
			parent.add(analzyePanel, index);
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




		DynamicGraph randGraph = dpnr.generate();
		
		CyNetwork randNetwork = CytoscapeConversion.DynamicGraphToCyNetwork(randGraph,ids);
				
		
		
		
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
		

		
		//Go up through the parents to the main window
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JDialog dialog = (JDialog)p;
		dialog.dispose();

		
	}
}