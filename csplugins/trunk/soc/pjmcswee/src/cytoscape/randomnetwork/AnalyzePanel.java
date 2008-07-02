/* File: AnalyzePanel.java
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

import cytoscape.graph.dynamic.*;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import cytoscape.giny.*;
import fing.model.*;
import giny.view.*;
import giny.model.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;
import java.util.*;

/*
 * AnalyzePanel is used for selecting which random 
 * network model to use.
 */
public class AnalyzePanel extends JPanel {

	private RandomNetworkGenerator networkModel;
	private boolean directed;


	//Next Button
	private javax.swing.JButton runButton;
	//Cancel Button
	private javax.swing.JButton cancelButton;

	
	private javax.swing.JLabel titleLabel;
	private javax.swing.JCheckBox clusterCheckBox;
	private javax.swing.JCheckBox averageDegreeCheckBox;	
	private javax.swing.JCheckBox degreeDistCheckBox;		
	private javax.swing.JCheckBox averageShortPathCheckBox;		

	private javax.swing.JLabel roundsLabel;
	private javax.swing.JTextField roundsTextField;
	
	/**
	 *  Default constructor
	 */
	public AnalyzePanel(RandomNetworkGenerator pNetwork, boolean pDirected ){
	
		super( ); 
		directed = pDirected;
		networkModel = pNetwork;
		initComponents();
	}

	/*
	 * Initialize the components
	 */
	private void initComponents() {

		clusterCheckBox = new javax.swing.JCheckBox();
		averageDegreeCheckBox = new javax.swing.JCheckBox();
		degreeDistCheckBox = new javax.swing.JCheckBox();
		averageShortPathCheckBox = new javax.swing.JCheckBox();	
		
		clusterCheckBox.setText("Clustering Coefficient");
		averageDegreeCheckBox.setText("Average Degree");
		degreeDistCheckBox.setText("Degree Distribution");
		averageShortPathCheckBox.setText("Mean Shortest Path");
		
		
		
		//Create the butons
		runButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		//Set up the title
		titleLabel = new javax.swing.JLabel();
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Analyze Random Networks");


		roundsTextField = new javax.swing.JTextField();
		roundsLabel = new javax.swing.JLabel();
		roundsLabel.setText("How many rounds to run:");
		

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

														.add(clusterCheckBox)
														.add(averageDegreeCheckBox)
														.add(degreeDistCheckBox)
														.add(averageShortPathCheckBox)
		
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				roundsLabel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				170)
																		.addPreferredGap(
																				1)
																		.add(
																				roundsTextField,
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
									
			.add(clusterCheckBox)
														.add(averageDegreeCheckBox)
														.add(degreeDistCheckBox)
														.add(averageShortPathCheckBox)
														.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(roundsLabel).add(
																roundsTextField))
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
	
		int rounds = 0;
		
		String roundString = roundsTextField.getText();
		try{
			rounds = Integer.parseInt(roundString);
		}catch(Exception e)
		{
			roundsLabel.setForeground(java.awt.Color.RED);
		}
		
		roundsLabel.setForeground(java.awt.Color.BLACK);
		
	
		LinkedList metrics = new LinkedList();

		if(clusterCheckBox.isSelected())
		{
			ClusteringCoefficientMetric ccm = new ClusteringCoefficientMetric();
			metrics.add(ccm);
		}
		if(averageDegreeCheckBox.isSelected())
		{
			AverageDegreeMetric adm = new AverageDegreeMetric();
			metrics.add(adm);
		}
		if(degreeDistCheckBox.isSelected())
		{
			DegreeDistributionMetric ddm = new DegreeDistributionMetric();
			metrics.add(ddm);
		}
		if(averageShortPathCheckBox.isSelected())
		{
			MeanShortestPathMetric msm = new MeanShortestPathMetric();
			metrics.add(msm);
		}
		
		double results[] = new double[metrics.size()];
		System.out.println("Free:" + Runtime.getRuntime().freeMemory());
		

		for(int i = 0; i < rounds; i++)
		{
			//Cytoscape.createNewSession();
			
			DynamicGraph net = networkModel.generate();
			System.out.println("generateed" );
			for(int j = 0; j < metrics.size(); j++)
			{
				long startTime = System.currentTimeMillis();
				NetworkMetric metric = (NetworkMetric)metrics.get(j);

				double t = metric.analyze(net,  directed);
				results[j] += t;
				long endTime = System.currentTimeMillis();
				
				
				System.out.println(i + ": \t" + t);
				System.out.println("After :" +Runtime.getRuntime().freeMemory());
				System.out.println((endTime - startTime)/1000.0d);
			}
		
		
			/*

			//Iterate over all of the nodes in the network
			Iterator netIter = net.nodesIterator();
			while(netIter.hasNext())
			{
				Node node = (Node)netIter.next();
				net.removeNode(node.getRootGraphIndex(),false);
				node = null;
			}
			
			Iterator edgeIter = net.edgesIterator();
			while(edgeIter.hasNext())
			{
				Edge edge = (Edge)edgeIter.next();
				net.removeEdge(edge.getRootGraphIndex(),false);
				edge = null;
			}
			*/
	
			//Cytoscape.destroyNetwork(net);	
			net = null;
		/*
			List edges =  Cytoscape.getCyEdgesList();
			List nodes =  Cytoscape.getCyNodesList();
			System.out.println("NumEdges : " + edges.size());
			System.out.println("NumNodes : " + nodes.size());

			edges = null;
			nodes = null;
			*/
			//Runtime.getRuntime().gc();
			

		}
	
		for(int j = 0; j < metrics.size(); j++)
		{

			System.out.println(results[j]/rounds);
		}
		
		//Runtime.getRuntime().gc();
		System.out.println("After :" +Runtime.getRuntime().freeMemory());
	
	
	
	
	}
}
