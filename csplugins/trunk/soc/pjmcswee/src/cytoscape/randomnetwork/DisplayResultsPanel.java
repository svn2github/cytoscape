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
import java.awt.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import giny.view.*;
import cytoscape.graph.dynamic.*;
import cytoscape.graph.dynamic.util.*;
import javax.swing.table.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;

import java.awt.Dimension;

/*
 * RandomizeExistingPanel is used for selecting which randomizing 
 * network model to use.
 */
public class DisplayResultsPanel extends JPanel {



	//Next Button
	private javax.swing.JButton runButton;
	//Cancel Button
	private javax.swing.JButton cancelButton;
	//Back Button
	private javax.swing.JButton backButton;
	//Title Label
	private javax.swing.JLabel titleLabel;
	


	private javax.swing.JLabel nodeLabel;
	private javax.swing.JLabel numNodeLabel;

	private javax.swing.JLabel edgeLabel;
	private javax.swing.JLabel numEdgeLabel;
	
	private javax.swing.JLabel timeLabel;
	private javax.swing.JLabel timeInfoLabel;
	
	private javax.swing.JLabel networkLabel;
	private javax.swing.JLabel roundLabel;
	
	//Treat this network as directed
	private javax.swing.JCheckBox directedCheckBox;
	
	//
	private javax.swing.JScrollPane scrollPane;
	
	//1 == randomized existing network
	//0 == generated new random network
	private int mode;
	//The Generator that created the networks
	//private RandomNetworkGenerator gen;
	
	//true means the network is directed
	//false means the network is undirected
//	private boolean directed;
	
//	private Object data[][];

	private RandomNetworkAnalyzer rna;

	/**
	 *  Default constructor
	 */
	public DisplayResultsPanel(int pMode, RandomNetworkAnalyzer pRNA)
	{
		
		super( ); 
		//gen = pGen;
		//directed = pDirected;
	
		//data = pData;
		rna = pRNA;
		mode = pMode;
		initComponents();
	}

	/**
	 * Initialize the components
	 */
	private void initComponents() {

		CyNetwork net = rna.getNetwork();

		//Create the butons
		runButton = new javax.swing.JButton();
		backButton = new javax.swing.JButton(); 
		cancelButton = new javax.swing.JButton();

		//Set up the title
		titleLabel = new javax.swing.JLabel();
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Results for " + net.getTitle());



		nodeLabel = new javax.swing.JLabel();
		numNodeLabel = new javax.swing.JLabel();

		edgeLabel = new javax.swing.JLabel();
		numEdgeLabel = new javax.swing.JLabel();
	
		timeLabel = new javax.swing.JLabel();
		timeInfoLabel = new javax.swing.JLabel();


		nodeLabel.setText("Number of nodes: ");
		edgeLabel.setText("Number of edges: ");
		timeLabel.setText("Time elapsed: ");
		numNodeLabel.setText("" + net.getNodeCount());
		numEdgeLabel.setText("" + net.getEdgeCount());


		runButton.setVisible(false);

	   
		//Create the column headings
		String[] columnNames = 
		 {"Metric","Existing Network","Average","Standard Deviation"};

		//Set up the Table for displaying 
		DefaultTableModel model = new DefaultTableModel(rna.getData(),columnNames);
		JTable table = new JTable(model) {


		public boolean isCellEditable(int rowIndex, int colIndex) {
			return false;   //Disallow the editing of any cell
		}

        // Override this method so that it returns the preferred
        // size of the JTable instead of the default fixed size
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }};



		table.getTableHeader().setReorderingAllowed( false );
		table.setGridColor(java.awt.Color.black);
		scrollPane = new JScrollPane(table);
		

		//int height  = table.getRowHeight() * data[0].length/2 +1;

		table.setPreferredScrollableViewportSize(new Dimension(500, 100)) ;
		scrollPane.setPreferredSize(new Dimension(500,100));
		scrollPane.setMinimumSize(new Dimension(500,100));
		//scrollPane.
	
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

		

		//Set up the cancel button
		cancelButton.setText("Close");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
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

	
		//Setup the 
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,0,0,0);
		//c.weightx = 1;
		//c.weighty = 1;
		add(nodeLabel,c);
	
		c = null;
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,0,0,0);
		//c.weightx =	1;
		//c.weighty = 1;
		add(numNodeLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,0,0,0);
		//c.weightx = 1;
		//c.weighty = 1;
		add(edgeLabel,c);
	
		c = null;
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,0,0,0);
		//c.weightx =	1;
		//c.weighty = 1;
		add(numEdgeLabel,c);



		
		//Setup the 
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 10;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0,0,0,0);
		c.weightx = 1;
		c.weighty = 1;
		add(scrollPane,c);
		




		

		//Setup the 
		c = null;
		c = new GridBagConstraints();
		c.gridx = 9;
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(0,0,0,0);
		c.weightx = 1;
		c.weighty = 1;
		add(cancelButton,c);
		
		
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,0,0,0);
		add(backButton,c);

		/*
		c = null;
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 8;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(0,0,0,0);
		c.weightx = 1;
		c.weighty = 1;
		add(runButton,c);
		*/
		
}




	
	/**
	 *  Call back for the cancel button
	 */
	private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
	
		AnalyzePanel analyzePanel = new AnalyzePanel(rna.getGenerator(),rna.getDirected(),mode);

		//Get the TabbedPanel
		JTabbedPane parent = (JTabbedPane)getParent();
		int index = parent.getSelectedIndex();
			
		//Remove this Panel
		parent.remove(index);
		
		//Replace it with the panel
		parent.add(analyzePanel, index);
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

	/**
	 *
	 */
	private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
	
		JTabbedPane parent = (JTabbedPane)getParent();
		
		
		//Re-pack the window based on this new panel
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JFrame frame = (JFrame)p;
		frame.pack();

	
	
	}

	/**
	 * cancelButtonActionPerformed call back when the cancel button is pushed
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		System.out.println();
		
		//Go up through the parents to the main window
		JTabbedPane parent = (JTabbedPane)getParent();
		java.awt.Container p = parent.getParent();
		p = p.getParent();
		p = p.getParent();
		p = p.getParent();
		JFrame frame = (JFrame)p;
		frame.dispose();
	}
	
	
}