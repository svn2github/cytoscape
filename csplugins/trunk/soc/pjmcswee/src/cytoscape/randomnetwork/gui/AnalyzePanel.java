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

package cytoscape.randomnetwork.gui;
import cytoscape.randomnetwork.*;

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
public class AnalyzePanel extends RandomNetworkPanel
{


	//The generator of our random networks
	private RandomNetworkGenerator networkModel;

	private static final int defaltRoundValue = 100;


	//Whether we are working with directed or undirected networks
	private boolean directed;




	private javax.swing.JPanel metricPanel;
	private javax.swing.JLabel metricLabel;
	private javax.swing.JCheckBox clusterCheckBox;
	private javax.swing.JCheckBox averageDegreeCheckBox;	
	private javax.swing.JCheckBox degreeDistCheckBox;		
	private javax.swing.JCheckBox averageShortPathCheckBox;		

	private javax.swing.JLabel roundsLabel;
	private javax.swing.JTextField roundsTextField;
	private javax.swing.JLabel threadLabel;
	private javax.swing.JTextField threadTextField;
	
	
	private javax.swing.JLabel threadExplain;
	private javax.swing.JLabel roundsExplain;
	
	//For the Progress Meter
    private TaskMonitor taskMonitor = null;
	private boolean interrupted = false;
	
	private double[][][] random_results;
	private double[] network_results;
	private String[] metric_names;
	private int rounds;


	/*
	 *  Default constructor
	 */
	public AnalyzePanel(RandomNetworkPanel pPrevious, RandomNetworkGenerator pNetwork, boolean pDirected){
	
		super(pPrevious ); 
		directed = pDirected;
		networkModel = pNetwork;
		initComponents();
	}
	
	/**
	 *
	 */
	public void setDirected(boolean pDirected)
	{
		directed = pDirected;
	}
	
	/**
	 *
	 */
	public void setGenerator(RandomNetworkGenerator pModel)
	{
		networkModel = pModel;
	}
	
	
	
	public String getTitle()
	{
		return new String("Analyze Network");
	}
	public String getDescription()
	{
		return new String("Select which metrics to run on the current network and compare against random networks. ");
	}
	
	public String getNextText()
	{
		return new String("Run");
	}	


	/**
	 * Initialize the components
	 */
	private void initComponents() 
	{

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
		
		
		metricLabel = new JLabel();
		metricLabel.setText("Metrics: ");
		
		metricPanel  = new JPanel();
		
		threadExplain = new javax.swing.JLabel();
		roundsExplain = new javax.swing.JLabel();
		
		threadExplain.setText("<html><font size=2 face=Verdana> Number of parallelizable threads." +
		" It is recommended to set this value to the number of available processors.</font></html>");	
		
		roundsExplain.setText("<html><font size=2 face=Verdana> Number of randomizations to perform.  As this number increases" +
				" the accuracy of the statistics should increase.</font></html>");	
						 
		threadExplain.setPreferredSize(new Dimension(200,70));
		threadExplain.setMinimumSize(new Dimension(200,70));
		roundsExplain.setPreferredSize(new Dimension(200,70));
		roundsExplain.setMinimumSize(new Dimension(200,70));


		roundsTextField = new javax.swing.JTextField();
		roundsLabel = new javax.swing.JLabel();
		threadTextField = new javax.swing.JTextField();
		threadLabel = new javax.swing.JLabel();

		roundsLabel.setText("Rounds to run:");		
		threadLabel.setText("Threads to run:");
		
		roundsTextField.setPreferredSize(new Dimension(50,25));
		threadTextField.setPreferredSize(new Dimension(50,25));
		roundsTextField.setMinimumSize(new Dimension(50,25));
		threadTextField.setMinimumSize(new Dimension(50,25));

		roundsTextField.setHorizontalAlignment(JTextField.RIGHT);
		threadTextField.setHorizontalAlignment(JTextField.RIGHT);		
		
		
		threadTextField.setText("" + Runtime.getRuntime().availableProcessors());
		roundsTextField.setText("" + defaltRoundValue);
		
		
		metricPanel.setLayout(new GridBagLayout());


		GridBagConstraints c = new GridBagConstraints();		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5,10,5,5);		
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		metricPanel.add(metricLabel, c);
		
		c = null;
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(15,5,5,5);		
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		metricPanel.add(clusterCheckBox, c);
		
		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 0;
		c.insets = new Insets(5,5,5,5);		
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 1;
//		c.weightx = 1;
//		c.weighty = 1;
		metricPanel.add(averageDegreeCheckBox, c);

		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets(5,5,5,5);			
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 2;
//		c.weightx = 1;
//		c.weighty = 1;
		metricPanel.add(degreeDistCheckBox,c);

		//
		c = null;
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 1;
		c.insets = new Insets(5,5,5,5);			
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 1;
//		c.weightx = 1;
//		c.weighty = 1;
		metricPanel.add(averageShortPathCheckBox,c);


		






		setLayout(new GridBagLayout());

		//Setup the titel
		c = null;
		c = new GridBagConstraints();		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0,10,0,0);		
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 5;
		c.weightx = 1;
		c.weighty = 1;
		add(metricPanel, c);
		
	
		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,0,5);		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		add(threadLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,0,5);		
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
//		c.weightx = 1;
//		c.weighty = 1;
		add(threadTextField,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,0,5);		
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(threadExplain,c);




		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,0,5);		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		add(roundsLabel,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,0,5);		
		c.gridx = 1;
		c.gridy = 2;	
		add(roundsTextField,c);

		c = null;
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,5,0,5);		
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 1;

		add(roundsExplain,c);

	}


	
	/**
	 *  Callback for when the "Next" button is pushed
	 */
	public RandomNetworkPanel next()
	{
		
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
			if(rounds < 0)
			{
				throw new Exception("Rounds Must be greater than 0!");
			}
		}catch(Exception e) {  roundsLabel.setForeground(java.awt.Color.RED);}
		
		roundsLabel.setForeground(java.awt.Color.BLACK);
		
		int numThreads = 1;
		String threadString = threadTextField.getText();
		try{
			numThreads = Integer.parseInt(threadString);
			if(numThreads < 1)
			{
				throw new Exception("Threads must be greater than 0!");
			}
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
		RandomNetworkAnalyzer rna = new RandomNetworkAnalyzer(netMetrics,new RandomNetwork(Cytoscape.getCurrentNetwork(), directed),
									networkModel,directed, numThreads ,rounds);
		
		//Run our task
		boolean success = TaskManager.executeTask(rna, config);
		if(success)
		{
			DisplayResultsPanel dpr = new DisplayResultsPanel(this,rna);
			
			return dpr;
		}
	
		return this;
	}
}