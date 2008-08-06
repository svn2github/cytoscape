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

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;




/*
 * AnalyzePanel is used for selecting which random 
 * network model to use.
 */
public class AnalyzePanel extends JPanel implements Task {


	
	/**
	* Internal data structure for actually doing the analyzing
	*/

	class AnalyzeWorkerThread implements Runnable
	{
		//each thread should have a unique ID
		private int id;
		private int numIterations;	
		private RandomNetworkGenerator myNetworkModel;
		private LinkedList myMetrics;
		private double[][] results;
		public int completed[];
		
		/*
		*
		*	Constructor
		*/
		public AnalyzeWorkerThread(int pID, int pIterations, RandomNetworkGenerator pMyNetworkModel, LinkedList pMyMetrics, double pResults[][], int pCompleted[])
		{
			id = pID;
			numIterations = pIterations;
			myNetworkModel = pMyNetworkModel;
			myMetrics = pMyMetrics;
			results = pResults;
			completed = pCompleted;
		}
	
		//This is the function that does all of our work.
		public void run()
		{
	
			//Go for the number of rounds unless we have been interrupted by the user
			for(int i = 0;((i < numIterations)&&(!interrupted)); i++)
			{
		
				//Generate the next random graph
				DynamicGraph net = myNetworkModel.generate();

				//Perform all metrics unless we have been interrupted
				for(int j = 0; ((j < myMetrics.size())&&(!interrupted)); j++)
				{
			
					//Get the next metric
					NetworkMetric metric = (NetworkMetric)myMetrics.get(j);
				
					//Compute the metric on this random network
					double t = metric.analyze(net,  directed);
					results[i][j] = t;

					completed[0]++;
				}
				

				
				//System.out.println("Thread:" + id + " Completed:" + completed);
		
				//Delete this random network
				net = null;
			}
		}//end run
	}//ends AnalyzeWorker class
	







	//The generator of our random networks
	private RandomNetworkGenerator networkModel;

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
	public AnalyzePanel(RandomNetworkGenerator pNetwork, boolean pDirected,int mode ){
	
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
		backButton = new javax.swing.JButton();

		//Set up the title
		titleLabel = new javax.swing.JLabel();
		titleLabel.setFont(new java.awt.Font("Sans-Serif", Font.BOLD, 14));
		titleLabel.setText("Analyze Random Networks");


		roundsTextField = new javax.swing.JTextField();
		roundsLabel = new javax.swing.JLabel();
		roundsLabel.setText("How many rounds to run:");
		
		threadTextField = new javax.swing.JTextField();
		threadLabel = new javax.swing.JLabel();
		threadLabel.setText("How many Threads to run:");
		
		

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
																layout
																		.createSequentialGroup()
																		.add(
																				threadLabel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				170)
																		.addPreferredGap(
																				1)
																		.add(
																				threadTextField,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				10,
																				Short.MAX_VALUE))
																											
		
																
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																layout
																		.createSequentialGroup()
																			.add(
																				backButton)
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
														.add(threadLabel).add(
																threadTextField))
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
																runButton).add(backButton))
										.addContainerGap()));
	}



	/**
	 *  Call back for the cancel button
	 */
	private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
	
		JPanel next = null;
		
		if(mode == 0)
		{
			next  = new GenerateRandomPanel(1);
		}
		else
		{
			next  = new RandomizeExistingPanel(1);			
		}
		//GenerateRandomPanel generateRandomPanel = new GenerateRandomPanel(mode);

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
		
		//Run our task
		boolean success = TaskManager.executeTask(this, config);
		if(success)
		{
			DisplayResultsPanel dpr = new DisplayResultsPanel(networkModel,directed,metric_names, random_results, network_results,mode);
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
		dframe.pack();
		
		
			return;
		}
	
		return;
	}
	
	/**
	* Run our task
	*/
	public void run() {

		
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
			
		//Initialize these for passing information to the Display Panel
		//random_results = new double[rounds][netMetrics.size()];
		network_results = new double[netMetrics.size()]; 
		metric_names = new String[netMetrics.size()];
		
		//Used for the progress meter to show progress
		int totalToAnalyze = netMetrics.size() * (rounds + 1);
		//int totalCompleted = 0;
		
		
		
		//Compute the metrics on the current network
		DynamicGraph original = (DynamicGraph)(CytoscapeConversion.CyNetworkToDynamicGraph( Cytoscape.getCurrentNetwork() , directed )).get(0);
		for(int j = 0; ((j < netMetrics.size())&(!interrupted)); j++)
		{
				//Get the next metric
				NetworkMetric metric = (NetworkMetric)netMetrics.get(j);

				//Compute the metric on this random network
				double t = metric.analyze(original,  directed);
				network_results[j] = t;
				metric_names[j] = metric.getDisplayName();
		
				//Compute how much we have completed
				
				int percentComplete = (int) (((double) j / totalToAnalyze) * 100);
			
				//Update the taskMonitor to show our progress
				if (taskMonitor != null) {
                    taskMonitor.setPercentCompleted(percentComplete);
                }	
		}
		
		
		//Calculate how many rounds are needed per thread
		int roundsPerThread = rounds / numThreads;
		
		//Create an array to store the matrix results for each thread
		random_results = new double[numThreads][roundsPerThread][netMetrics.size()];
		
		//Let them tell us how much they have completed
		int completed[][] = new int[numThreads][1];
		
		//Keep pointers to all of our threads
		Thread threads[] = new Thread[numThreads];
		
		//For each thread
		for(int i = 0; i < numThreads; i++)
		{
			//Create a new copy of the metrics for each thread
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
			
			//Create the new thread
			AnalyzeWorkerThread thread = new AnalyzeWorkerThread(i, roundsPerThread, networkModel.copy(), metrics, random_results[i], completed[i]);
			
			//start the thread running
			threads[i] = new Thread(thread);
			threads[i].start();
		}
		
		
		boolean finished = false;
		while(!finished)
		{
			try
			{
				Thread.currentThread().sleep(990);
			}catch(Exception e){e.printStackTrace();}
		
			double totalCompleted = netMetrics.size();
			finished = true;
			for(int i = 0; i < numThreads; i++)
			{
				finished = finished && (!threads[i].isAlive());
				totalCompleted += completed[i][0];
				//System.out.println(i +"\t" + completed[i]);
			}
			
			//System.out.println(totalCompleted);
			int percentComplete = (int) (((double) totalCompleted / totalToAnalyze) * 100);
		//	System.out.println(percentComplete);		
			//Update the taskMonitor to show our progress
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(percentComplete);
			}
			

		}
		
		
		/*

		//Go for the number of rounds unless we have been interrupted by the user
		for(int i = 0;((i < rounds)&&(!interrupted)); i++)
		{
		
			//Generate the next random graph
			DynamicGraph net = networkModel.generate();


			//Perform all metrics unless we have been interrupted
			for(int j = 0; ((j < metrics.size())&&(!interrupted)); j++)
			{
			
				//Get the next metric
				NetworkMetric metric = (NetworkMetric)metrics.get(j);
				
				//Compute the metric on this random network
				double t = metric.analyze(net,  directed);
				random_results[i][j] = t;
				//System.out.println(t);
				
				//Compute how much we have completed
				totalCompleted++;
				int percentComplete = (int) (((double) totalCompleted / totalToAnalyze) * 100);
			
				//Update the taskMonitor to show our progress
				if (taskMonitor != null) {
                    taskMonitor.setPercentCompleted(percentComplete);

                }

			}
		
			//Delete this random network
			net = null;
		}
		*/
		
	}
	
	
	/**
     * Gets the Task Title.
     *
     * @return human readable task title.
     */
    public String getTitle() {
        return new String("Analyzing Network");
    }
	
	  /**
     * Non-blocking call to interrupt the task.
     */
    public void halt() {
		interrupted = true;
    }

	/**
     * Sets the Task Monitor.
     *
     * @param taskMonitor TaskMonitor Object.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
    }


	
	
	
}
