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
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;
import java.util.*;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;




/*
 * AnalyzePanel is used for selecting which random 
 * network model to use.
 */
public class AnalyzePanel extends JPanel{


	//The generator of our random networks
	private RandomNetworkGenerator networkModel;

	private static final int defaltRoundValue = 100;


	//Whether we are working with directed or undirected networks
	private boolean directed;

	int mode;

	//Next Button
	private javax.swing.JButton runButton;
	//Cancel Button
	private javax.swing.JButton cancelButton;
	//Cancel Button
	private javax.swing.JButton backButton;

	
	private javax.swing.JLabel titleLabel;
	private javax.swing.JCheckBox clusterCheckBox;
	private javax.swing.JCheckBox averageDegreeCheckBox;	
	private javax.swing.JCheckBox degreeDistCheckBox;		
	private javax.swing.JCheckBox averageShortPathCheckBox;		

	private javax.swing.JLabel roundsLabel;
	private javax.swing.JTextField roundsTextField;
	private javax.swing.JLabel threadLabel;
	private javax.swing.JTextField threadTextField;
	
	
	//For the Progress Meter
    private TaskMonitor taskMonitor = null;
	private boolean interrupted = false;
	
	private double[][][] random_results;
	private double[] network_results;
	private String[] metric_names;
	private int rounds;
	/**
	 *  Default constructor
	 */
	public AnalyzePanel(RandomNetworkGenerator pNetwork, boolean pDirected, int mode){
	
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
		clusterCheckBox.setSelected(true);
		averageDegreeCheckBox.setSelected(true);
		degreeDistCheckBox.setSelected(true);
		averageShortPathCheckBox.setSelected(true);
		
		
		//Create the butons
		runButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		backButton = new javax.swing.JButton();

		//Set up the title
		titleLabel = new javax.swing.JLabel();
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Analyze Random Networks");


		roundsTextField = new javax.swing.JTextField();
		roundsLabel = new javax.swing.JLabel();
		threadTextField = new javax.swing.JTextField();
		threadLabel = new javax.swing.JLabel();

		roundsLabel.setText("How many rounds to run:");		
		threadLabel.setText("How many Threads to run:");
		
		roundsTextField.setPreferredSize(new Dimension(50,25));
		threadTextField.setPreferredSize(new Dimension(50,25));
		roundsTextField.setHorizontalAlignment(JTextField.RIGHT);
		threadTextField.setHorizontalAlignment(JTextField.RIGHT);		
		
		
		threadTextField.setText("" + Runtime.getRuntime().availableProcessors());
		roundsTextField.setText("" + defaltRoundValue);
		
		

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

		//Set up the cancel button
		backButton.setText("Back");
		backButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				backButtonActionPerformed(evt);
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


		/*
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5,10,10,0);		
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 6;
		c.weightx = 1;
		c.weighty = 1;
		add(explainLabel,c);
		*/

		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(5,5,5,5);		
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(clusterCheckBox, c);
		
		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(5,5,5,5);		
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(averageDegreeCheckBox, c);

		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(5,5,5,5);			
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(degreeDistCheckBox,c);

		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 5;
		c.insets = new Insets(5,5,5,5);			
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(averageShortPathCheckBox,c);


		

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5,5,5,5);		
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(threadLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5,5,5,5);		
		c.gridx = 1;
		c.gridy = 6;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(threadTextField,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5,5,5,5);		
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(roundsLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5,5,5,5);		
		c.gridx = 1;
		c.gridy = 7;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(roundsTextField,c);




		//Setup the 
		c = null;
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = 8;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,0,0,0);
		c.weightx = 1;
		c.weighty = 1;
		add(cancelButton,c);
		
		
		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 8;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0,0,0,0);
		add(backButton,c);

		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 8;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(0,0,0,0);
		c.weightx = 1;
		c.weighty = 1;
		add(runButton,c);
		
	}



	/**
	 *  Call back for the cancel button
	 */
	private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
	
		JPanel next = null;
		
		if(mode == 1)
		{
			next  = new GenerateRandomPanel(1);
		}
		else
		{
			next  = new RandomizeExistingPanel(1);			
		}


		//Get the TabbedPanel
		JTabbedPane parent = (JTabbedPane)getParent();
		int index = parent.getSelectedIndex();
			
		//Remove this Panel
		parent.remove(index);
		
		//Replace it with the panel
		parent.add(next, index);
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
		
		JTaskConfig config = new JTaskConfig();

		JFrame frame = Cytoscape.getDesktop();
		
		config.setOwner(frame);
		config.displayStatus(false);
		config.displayTimeElapsed(true);
		config.displayTimeRemaining(false);
		config.displayCancelButton(true);
		config.displayCloseButton(true);
		config.setAutoDispose(true);
		
		
			rounds = 0;
		
		String roundString = roundsTextField.getText();
		try{
			rounds = Integer.parseInt(roundString);
		}catch(Exception e)
		{
			roundsLabel.setForeground(java.awt.Color.RED);
		}
		
		roundsLabel.setForeground(java.awt.Color.BLACK);
		
		int numThreads = 1;
		String threadString = threadTextField.getText();
		try{
			numThreads = Integer.parseInt(threadString);
		}catch(Exception e)
		{
			threadLabel.setForeground(java.awt.Color.RED);
		}
		
		threadLabel.setForeground(java.awt.Color.BLACK);
	
	
	
		LinkedList netMetrics = new LinkedList();

		if(clusterCheckBox.isSelected())
		{
			ClusteringCoefficientMetric ccm = new ClusteringCoefficientMetric();
			netMetrics.add(ccm);
		}
		if(averageDegreeCheckBox.isSelected())
		{
			AverageDegreeMetric adm = new AverageDegreeMetric();
			netMetrics.add(adm);
		}
		if(degreeDistCheckBox.isSelected())
		{
			DegreeDistributionMetric ddm = new DegreeDistributionMetric();
			netMetrics.add(ddm);
		}
		if(averageShortPathCheckBox.isSelected())
		{
			MeanShortestPathMetric msm = new MeanShortestPathMetric();
			netMetrics.add(msm);
		}
			

		//Create the randomNetworkAnalyzer to do all of the statistical work
		RandomNetworkAnalyzer rna = new RandomNetworkAnalyzer(netMetrics,Cytoscape.getCurrentNetwork(),
									networkModel,directed, numThreads ,rounds);
		
		//Run our task
		boolean success = TaskManager.executeTask(rna, config);
		if(success)
		{
			DisplayResultsPanel dpr = new DisplayResultsPanel(mode,rna);
					//Get the TabbedPanel
			JTabbedPane parent = (JTabbedPane)getParent();
			int index = parent.getSelectedIndex();
			
			//Remove this Panel
			parent.remove(index);
		
			//Replace it with the panel
			parent.add(dpr, index);
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
			JFrame dframe = (JFrame)p;
			dframe.toFront();
			
			dframe.pack();
		}
	
		return;
	}
}