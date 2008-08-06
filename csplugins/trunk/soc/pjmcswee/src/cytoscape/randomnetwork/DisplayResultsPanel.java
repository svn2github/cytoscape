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
	

	//Treat this network as directed
	private javax.swing.JCheckBox directedCheckBox;
	
	//
	private javax.swing.JScrollPane scrollPane;
	
	
	//The names of the metrics
	private String[] metricNames;
	
	//The results from the random networks
	private double[][][] randomResults;
	
	//The results from the actual network
	private double[] networkResults;
	//1 == randomized existing network
	//0 == generated new random network
	private int mode;
	//The Generator that created the networks
	private RandomNetworkGenerator gen;
	//true means the network is directed
	//false means the network is undirected
	private boolean directed;


	/*
	 *  Default constructor
	 */
	public DisplayResultsPanel(RandomNetworkGenerator pGen, boolean pDirected,
					String pMetricNames[], double pRandomResults[][][], double pNetworkResults[],int pMode){
		
		
		super( ); 
		metricNames = pMetricNames;
		randomResults = pRandomResults;
		networkResults = pNetworkResults;
		gen = pGen;
		directed = pDirected;
		mode = pMode;
		initComponents();
	}

	/*
	 * Initialize the components
	 */
	private void initComponents() {

		//Create the butons
		runButton = new javax.swing.JButton();
		backButton = new javax.swing.JButton(); 
		cancelButton = new javax.swing.JButton();

		//Set up the title
		titleLabel = new javax.swing.JLabel();
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Results");

	

		runButton.setVisible(false);

	   
		//Create the column headings
		String[] columnNames = 
		 {"Metric","Existing Network","Average","Standard Deviation"};

		//Information to display in the table
		Object[][] data = new Object[metricNames.length][5];

		//Used to make information readable
		DecimalFormat df = new DecimalFormat("0.0000000");

		//Check how many threads were run
		int threads = randomResults.length;
		
		//Check how many runs where executed
		int rounds =  randomResults[0].length;

		//For each metric
		for(int i = 0; i < metricNames.length; i++)
		{
			//Compute the avearge for this metric
			//accross all of the rounds
			double average = 0;
			for(int  t = 0; t < threads; t++)
			{
				for(int j = 0; j <rounds; j++)
				{
					average += randomResults[t][j][i];
				}
			}
			
			average /= (double)(threads * rounds);
			
			//Compute the standard deviation
			double std = 0;
			//System.out.println("Rounds:" + rounds);
			
			for(int t = 0; t< threads; t++)
			{
				for(int j = 0; j < rounds; j++)
				{
					std += Math.pow(randomResults[t][j][i] - average, 2.0d);
					//System.out.println(randomResults[j][i] + "\t" + average );
				}
			}
			
			std = Math.sqrt(std / (double)(rounds * threads));
			
			//System.out.println(std);
			//Update the table data
			data[i][0] = metricNames[i];
			data[i][1] = new Double(df.format(networkResults[i]));
			data[i][2] = new Double(df.format(average));
			data[i][3] = new Double(df.format(std));
			data[i][4] = new Double(df.format());	
		}

		//Set up the Table for displaying 
		DefaultTableModel model = new DefaultTableModel(data,columnNames);
		JTable table = new JTable(model) {
        // Override this method so that it returns the preferred
        // size of the JTable instead of the default fixed size
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }
    };

		table.setGridColor(java.awt.Color.black);
		scrollPane = new JScrollPane(table);

		int height  = table.getRowHeight() * rounds/2 +1;

		table.setPreferredScrollableViewportSize(new Dimension(300, 16)) ;
		
	
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

													
														.add(scrollPane)
																											
													
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																layout
																		.createSequentialGroup()
																		.add(
																				backButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(runButton)
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
										
										.add(scrollPane)
									
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
	
		AnalyzePanel analyzePanel = new AnalyzePanel(gen,directed,mode);

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

	/*
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