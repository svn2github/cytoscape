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
public class RandomComparisonPanel extends JPanel {



	

	//Next Button
	private javax.swing.JButton runButton;
	//Cancel Button
	private javax.swing.JButton cancelButton;
	//Title Label
	private javax.swing.JLabel titleLabel;
	//Group together the different options
	private javax.swing.ButtonGroup group;

	//Checkbox for generating random networks
	private javax.swing.JCheckBox generateRandomNetwork;
	
	//Checkbox for randomizing an existing network
	private javax.swing.JCheckBox randomizeExistingNetwork;

	
	
	//Explain what this checkbox means
	private javax.swing.JLabel generateRandomExplain;
	
	//Explain what this checkbox means	
	private javax.swing.JLabel randomizeExistingExplain;

	/*
	 *  Default constructor
	 */
	public RandomComparisonPanel( ){
		
		super( ); 
		initComponents();
	}

	/*
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
		randomizeExistingExplain
				.setText("<html><font size=2 face=Verdana>Compare against a randomized existing network.</font></html>");


		//Set the randomize existing text
		generateRandomExplain
				.setText("<html><font size=2 face=Verdana>Compare against a new random network.</font></html>");

		
		//Add these buttons to the group
		group.add(generateRandomNetwork);
		group.add(randomizeExistingNetwork);
		
		
		
		randomizeExistingNetwork.setText("Randomize Existing Network");
		generateRandomNetwork.setText("Generate a Random Network");


		//set the labels to opaque
		generateRandomExplain.setOpaque(true);		
		randomizeExistingExplain.setOpaque(true);
		
		
		//Make randomize existing network
		randomizeExistingNetwork.setSelected(true);
		
		//Create the butons
		runButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		//Set up the title
		titleLabel = new javax.swing.JLabel();
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Compare to Random");


	
		//Set up the run button
		runButton.setText("Next");
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
																				randomizeExistingNetwork,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				170)
																		.addPreferredGap(1)
																		
																		.add(randomizeExistingExplain,
																			 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			 10,
																			 Short.MAX_VALUE))
															.add(
															layout
																	.createSequentialGroup()
																	.add(
																			generateRandomNetwork,
																			org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			10,
																			170)
																	.addPreferredGap(1)
																	
																	.add(generateRandomExplain,
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

														.add(randomizeExistingNetwork).add(
																randomizeExistingExplain))
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)

														.add(generateRandomNetwork).add(
																generateRandomExplain))
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
	 * cancelButtonActionPerformed call back when the cancel button is pushed
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		//Go up through the parents to the main window
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JFrame frame = (JFrame)p;
		frame.dispose();
	}
	
	/*
	 *  Callback for when the "Next" button is pushed
	 */
	private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
	
		JPanel displayPanel = null;
		//Title for this Panel
		String title = null;
		
		//See which checkbox is selected and then display the appropriate panel
		if (randomizeExistingNetwork.isSelected()) {
			displayPanel = new RandomizeExistingPanel(1);
			title = new String("Randomize Existing");
		} else if(generateRandomNetwork.isSelected()){
			displayPanel = new GenerateRandomPanel(1);
			title = new String("WattsStrogatz Random Network");
		}
		
		
		//Get the TabbedPanel
		JTabbedPane parent = (JTabbedPane)getParent();
		int index = parent.getSelectedIndex();
		//Remove this Panel
		parent.remove(index);
		//Replace it with the panel
		parent.add(displayPanel,index);
		//Set the title for this panel
		parent.setTitleAt(index,title);
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

	
			
	}
}