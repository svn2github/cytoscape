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
import cytoscape.layout.*;
import cytoscape.layout.algorithms.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import java.awt.*;
import cytoscape.visual.*;
import giny.view.*;
import cytoscape.graph.dynamic.*;
import cytoscape.graph.dynamic.util.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;


/**
 * RandomizeExistingPanel is used for selecting which randomizing 
 * network model to use.
 */
public class RandomizeExistingPanel extends JPanel implements PropertyChangeListener {

	private int mode;
	private int visualRounds = 1;

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


	private javax.swing.JTextField numCreateTextField;
	private javax.swing.JLabel numCreateLabel;


	private javax.swing.JTextField numIterTextField;
	private javax.swing.JLabel numIterLabel;


	//Treat this network as directed
	private javax.swing.JCheckBox directedCheckBox;

	//Checkbox for erdos-renyi model
	private javax.swing.JCheckBox degreePreserving;
	//Checkbox for barabasi-albert model
	private javax.swing.JLabel degreePreservingExplain;
	

	/*
	 *  Default constructor
	 */
	public RandomizeExistingPanel(int pMode ){
		
		super( ); 
		mode = pMode;
		initComponents();
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		Cytoscape.getDesktop().getNetworkViewManager().getSwingPropertyChangeSupport().addPropertyChangeListener(this);
	}

	/**
	 * Initialize the components
	 */
	private void initComponents() {

		//Create the erdos-renyi checkbox
		degreePreserving = new javax.swing.JCheckBox();
	
		
		//Create the barabasi-albert  label
		degreePreservingExplain = new javax.swing.JLabel();

		//Set the erdos-renyi text
		degreePreservingExplain
				.setText("<html><font size=2 face=Verdana>Shuffle edges while keeping <br>in/out degree of each node.</font></html>");
		degreePreservingExplain.setPreferredSize(new Dimension(200,50));
		degreePreservingExplain.setMinimumSize(new Dimension(200,50));
		
		
		directedCheckBox = new javax.swing.JCheckBox();
		directedCheckBox.setText("Treat as undirected?");
		//set the labels to opaque
		degreePreservingExplain.setOpaque(true);
		
		//Set the text for the checkboxes
		degreePreserving.setText("Edge Shuffle");

		//Make barabasi-albert the default
		degreePreserving.setSelected(true);
		
		//Create the butons
		runButton = new javax.swing.JButton();
		backButton = new javax.swing.JButton(); 
		cancelButton = new javax.swing.JButton();

		//Set up the title
		titleLabel = new javax.swing.JLabel();
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Degree Preserving Random Shuffle");



		CyNetwork net = Cytoscape.getCurrentNetwork();
		if(net == Cytoscape.getNullNetwork())
		{
			runButton.setEnabled(false);

		}


		numCreateTextField = new javax.swing.JTextField();
		numCreateTextField.setText("1");
		numCreateTextField.setPreferredSize(new Dimension(30,25)); 	
		numCreateTextField.setHorizontalAlignment(JTextField.RIGHT);
		numCreateLabel = new javax.swing.JLabel();
		numCreateLabel.setText("Number of networks to create:");

		int E = net.getEdgeCount();
		numIterTextField = new javax.swing.JTextField();
		numIterTextField.setText("" + E);
		numIterTextField.setPreferredSize(new Dimension(30,25)); 	
		numIterTextField.setHorizontalAlignment(JTextField.RIGHT);
		numIterLabel = new javax.swing.JLabel();
		numIterLabel.setText("Num Shuffles:");
		
		
	
	
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
		else
		{
			numCreateLabel.setVisible(false);
			numCreateTextField.setVisible(false);
		}
		//Set up the cancel button
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});


		setLayout(new GridBagLayout());
		
		
		
		//
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 6;
		c.insets = new Insets(10,5,5,5);	
		c.anchor =  GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		c.weighty = 1;
		add(titleLabel,c);

		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5,5,5,5);	
		c.anchor =  GridBagConstraints.LINE_START;
		//c.weightx = 1;
		c.weighty = 1;
		add(degreePreserving, c);
		
		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5,5,5,5);	
		c.anchor =  GridBagConstraints.LINE_START;
		//c.weightx = 1;
		c.weighty = 1;
		add(degreePreservingExplain, c);
		
		
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(15,5,15,5);	
		c.gridwidth = 2;
		c.anchor =  GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.weighty = 1;
		add(numCreateLabel, c);
		
		c = null;
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = new Insets(15,5,15,5);	
		c.anchor =  GridBagConstraints.LINE_START;
		//c.fill =  GridBagConstraints.HORIZONTAL;
		c.ipadx = 20;
		c.weightx = 1;
		c.weighty = 1;
		add(numCreateTextField, c);
		
		
			c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(15,5,15,5);	
		c.gridwidth = 2;
		c.anchor =  GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.weighty = 1;
		add(numIterLabel, c);
		
		c = null;
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 3;
		c.gridwidth = 1;
		c.insets = new Insets(15,5,15,5);	
		c.anchor =  GridBagConstraints.LINE_START;
		//c.fill =  GridBagConstraints.HORIZONTAL;
		c.ipadx = 20;
		c.weightx = 1;
		c.weighty = 1;
		add(numIterTextField, c);
		

		
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;	
		c.gridwidth = 4;
		c.anchor =  GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.weighty = 1;
		add(directedCheckBox, c);
		
		
		
		c = null;
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 5;	
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.weighty = 1;
		add(cancelButton, c);



		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 5;		
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.weighty = 1;
		add(backButton, c);
		
		
		c = null;
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 5;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 1;
		c.weighty = 1;
		add(runButton, c);

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
		JFrame frame = (JFrame)p;
		frame.pack();

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
		JFrame frame = (JFrame)p;
		frame.dispose();
	}
	
	/*
	 *  Callback for when the "Next" button is pushed
	 */
	private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		boolean directed = directedCheckBox.isSelected();
		CyNetwork net = Cytoscape.getCurrentNetwork();

		LinkedList network = CytoscapeConversion.CyNetworkToDynamicGraph(net,!directed);
		String iterString = numIterTextField.getText();
		int numIter;
		try
		{
			numIter = Integer.parseInt(iterString);
			
		}catch(Exception e){numIterLabel.setForeground(java.awt.Color.RED); return;}

		numIterLabel.setForeground(java.awt.Color.BLACK);
		System.out.println("User Iterations: " + numIter);		
		DegreePreservingNetworkRandomizer dpnr = new DegreePreservingNetworkRandomizer(net,!directed,numIter);
		String ids[] = dpnr.getNodeIds();




		if(mode == 1)
		{
			AnalyzePanel analzyePanel = new AnalyzePanel(dpnr, !directed,0);
			
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
			JFrame frame = (JFrame)p;
			frame.pack();

			return;

		
		}

		try
		{	
			String value = numCreateTextField.getText().trim();
			value  = value.trim();
			visualRounds = Integer.parseInt(value);
		}catch(Exception e){numCreateLabel.setForeground(java.awt.Color.RED); return;}
		numCreateLabel.setForeground(java.awt.Color.BLACK);
		
		System.out.println("Visual Rounds:" + visualRounds);
		

		CyNetworkView netView = Cytoscape.getCurrentNetworkView();
		VisualStyle newStyle = netView.getVisualStyle();
		String visName = newStyle.getName();
		//returns CytoscapeWindow's VisualMappingManager object
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		//gets the global catalog of visual styles and calculators
		
		//Get the random network vistualStyle
		vmm.setVisualStyle(newStyle);
		//System.out.println(visName);
		
		
		CyAttributes attr = Cytoscape.getNetworkAttributes();
		CyLayoutAlgorithm alg = (CyLayoutAlgorithm)null;
		String layoutString = (String)attr.getAttribute(Cytoscape.getCurrentNetwork().getTitle(), "__layoutAlgorithm");
		//System.out.println(layoutString);
		if(alg == null)
		{
			alg = new GridNodeLayout();
		}
		for(int i = 0; i < visualRounds; i++)
		{

			DynamicGraph randGraph = dpnr.generate();
			
			CyNetwork randNetwork = CytoscapeConversion.DynamicGraphToCyNetwork(randGraph,ids);

			//Set this as the current visualStyle
			//Consider moving below code into CytoscapeConversion!
			vmm.setVisualStyle(newStyle);
			//CyNetworkView view = Cytoscape.getCurrentNetworkView();
			//view.applyLayout(alg); 
	
			CytoscapeConversion.useOriginalLayout( net, randNetwork);
		}
		
		
		//Set the network pane as active
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setSelectedIndex(0);
		
		

	

		
		//Go up through the parents to the main window
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JFrame frame = (JFrame)p;
		frame.dispose();
	}
	
	 /**
	  *
	  */
	  public void propertyChange(PropertyChangeEvent event) 
	  {
			if(event.getPropertyName() == Cytoscape.NETWORK_CREATED) 
			{
				runButton.setEnabled(true);
			}


			CyNetwork net = Cytoscape.getCurrentNetwork();
			if(net == Cytoscape.getNullNetwork())
			{
				runButton.setEnabled(false);

			}

			int E = net.getEdgeCount();
			numIterTextField.setText("" + E);
			
	  }


} 
	
	
