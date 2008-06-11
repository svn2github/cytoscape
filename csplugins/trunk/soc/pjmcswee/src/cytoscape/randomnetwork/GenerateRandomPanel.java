/* File: GenerateRandomPanel.java
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;


/*
 * GenerateRandomPanel is used for selecting which random 
 * network model to use.
 */
public class GenerateRandomPanel extends JPanel {

	//Next Button
	private javax.swing.JButton runButton;
	//Cancel Button
	private javax.swing.JButton cancelButton;
	//Title Label
	private javax.swing.JLabel titleLabel;
	//Group together the different options
	private javax.swing.ButtonGroup group;

	//Checkbox for erdos-renyi model
	private javax.swing.JCheckBox erm;
	//Checkbox for watts-strogatz model
	private javax.swing.JCheckBox wsm;
	//Checkbox for barabasi-albert model
	private javax.swing.JCheckBox bam;

	//Label to describe erdos-renyi model
	private javax.swing.JLabel ermExplain;
	//Label to describe watts-strogatz model
	private javax.swing.JLabel wsmExplain;
	//Checkbox for barabasi-albert model
	private javax.swing.JLabel bamExplain;

	/*
	 *  Default constructor
	 */
	public GenerateRandomPanel( ){
		super( ); 
		initComponents();
	}

	/*
	 * Initialize the components
	 */
	private void initComponents() {

		//Create the group 
		group = new javax.swing.ButtonGroup();
		//Create the erdos-renyi checkbox
		erm = new javax.swing.JCheckBox();
		//Create the watts-strogatz checkbox
		wsm = new javax.swing.JCheckBox();
		//Create the barabasi-albert checkbox		
		bam = new javax.swing.JCheckBox();
		//Create the erdos-renyi label
		ermExplain = new javax.swing.JLabel();
		//Create the watts-strogatz label
		wsmExplain = new javax.swing.JLabel();
		//Create the barabasi-albert  label
		bamExplain = new javax.swing.JLabel();

		//Set the erdos-renyi text
		ermExplain
				.setText("<html><font size=2 face=Verdana>Uniformly generate a random <br> graph with n nodes and m edges.</font></html>");

		//Set the watts-strogatz text
		wsmExplain
				.setText("<html><font size=2 face=Verdana>Generate a random graph with n <br> nodes and each edge has probability p to be included.</font></html>");

		//Set the barabasi-albert text
		bamExplain
				.setText("<html><font size=2 face=Verdana>Generate a random graph with n <br> nodes and each edge has probability p to be included.</font></html>");


		//set the labels to opaque
		ermExplain.setOpaque(true);
		wsmExplain.setOpaque(true);
		bamExplain.setOpaque(true);
		
		//Set the text for the checkboxes
		erm.setText("Erdos-Renyi Model");
		wsm.setText("Watts-Strogatz Model");
		bam.setText("Barabasi-Albert Model");

		//Make barabasi-albert the default
		bam.setSelected(true);
		
		//Add each checkbox to the group
		group.add(bam);
		group.add(wsm);
		group.add(erm);

		//Create the butons
		runButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		//Set up the title
		titleLabel = new javax.swing.JLabel();
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Generate Random Network");

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
																				erm,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				170)
																		.addPreferredGap(1)
																		
																		.add(ermExplain,
																			 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																			 10,
																			 Short.MAX_VALUE))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				wsm,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																			170)
																		.addPreferredGap(
																				1)
																		.add(
																				wsmExplain,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				Short.MAX_VALUE))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				bam,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				170)
																		.addPreferredGap(
																				1)
																		.add(
																				bamExplain,
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

														.add(erm).add(
																ermExplain))

										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED,
												3, Short.MAX_VALUE)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(wsm).add(
																wsmExplain))
										
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED,
												3, Short.MAX_VALUE)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(bam).add(
																bamExplain))
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
		JDialog dialog = (JDialog)p;
		dialog.dispose();
	}
	
	/*
	 *  Callback for when the "Next" button is pushed
	 */
	private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		//Panel to display	
		JPanel displayPanel = null;
		//Title for this Panel
		String title = null;
		
		//See which checkbox is selected and then display the appropriate panel
		if (erm.isSelected()) {
			displayPanel = new ErdosRenyiDialog();
			title = new String("Erdos-Renyi Random Network");
		} else if(wsm.isSelected()){
			displayPanel = new WattsStrogatzDialog();
			title = new String("WattsStrogatz Random Network");
		}else{
			displayPanel = new BarabasiAlbertDialog();
			title = new String("Barabasi-Albert Random Network");
		}
		
		//Get the TabbedPanel
		JTabbedPane parent = (JTabbedPane)getParent();
		//Remove this Panel
		parent.remove(0);
		//Replace it with the panel
		parent.add(displayPanel,0);
		//Set the title for this panel
		parent.setTitleAt(0,title);
		//Display this panel
		parent.setSelectedIndex(0);
		//Enforce this Panel
		parent.validate();
		
		
		//Re-pack the window based on this new panel
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JDialog dialog = (JDialog)p;
		dialog.pack();
	}
}