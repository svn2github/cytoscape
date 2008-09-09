/* File: RandomNetworkAnalyzer.java
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
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;
import java.util.*;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;




/**
 * RandomNetworkAnalyzer is used for selecting which random 
 * network model to use.
 */
public class RandomNetworkAnalyzer implements Task {


	
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
		
		/**
		 *
		 *	Constructor
		 */
		public AnalyzeWorkerThread(int pID, int pIterations, RandomNetworkGenerator pMyNetworkModel, 
		LinkedList pMyMetrics, double pResults[][], int pCompleted[])
		{
			id = pID;
			numIterations = pIterations;
			myNetworkModel = pMyNetworkModel;
			myMetrics = pMyMetrics;
			results = pResults;
			completed = pCompleted;
		}
	
		/**
		 *
		 * This is the function that does all of our work.
		 */
		public void run()
		{
	
			//Go for the number of rounds unless we have been interrupted by the user
			for(int i = 0;((i < numIterations)&&(!interrupted)); i++)
			{
		
				//Generate the next random graph
				RandomNetwork net = myNetworkModel.generate();

				//Perform all metrics unless we have been interrupted
				for(int j = 0; ((j < myMetrics.size())&&(!interrupted)); j++)
				{
			
					//Get the next metric
					NetworkMetric metric = (NetworkMetric)myMetrics.get(j);
				
					//Compute the metric on this random network
					double t = metric.analyze(net,  directed);
					results[i][j] = t;
					
					totalCompleted++;

					int percentComplete = (int) (((double) totalCompleted / totalToAnalyze) * 100);
					if (taskMonitor != null) 
					{
						taskMonitor.setPercentCompleted(percentComplete);
					}
				}
		
				//Delete this random network
				net = null;
			}
		}//end run
	}//ends AnalyzeWorker class
	







	//The generator of our random networks
	private RandomNetworkGenerator networkModel;

	//Whether we are working with directed or undirected networks
	private boolean directed;
	
	//For the Progress Meter
    private TaskMonitor taskMonitor = null;

	private boolean interrupted = false;
	
	private double[][][] random_results;
	private double[] network_results;
	private String[] metric_names;
	private int numRounds;
	private int numThreads;
	private LinkedList<NetworkMetric> metrics;
	private int totalCompleted;
	private int totalToAnalyze;
	private Object data[][];
	private int elapsedTime;
	private RandomNetwork graph;

	/**
	 *  Default constructor
	 */
	public RandomNetworkAnalyzer(LinkedList<NetworkMetric> pMetrics, RandomNetwork pOriginal, 
								RandomNetworkGenerator pNetwork, boolean pDirected,
								int pNumThreads, int pNumRounds)
	{
		directed = pDirected;
		networkModel = pNetwork;
		numRounds = pNumRounds;
		numThreads = pNumThreads;
		metrics = pMetrics;
		totalCompleted = 0;
		totalToAnalyze = 0;
		graph = pOriginal;
	}
	
	
	/**-----------------------------------------------------------------------------
	 * Run our task
	 *-----------------------------------------------------------------------------*/
	public void run() {


		if(numRounds < numThreads)
		{
			numThreads = numRounds;
		}

		//Initialize these for passing information to the Display Panel
		network_results = new double[metrics.size()]; 
		metric_names = new String[metrics.size()];
		
		//Used for the progress meter to show progress
		totalToAnalyze = metrics.size() * (numRounds + 1);
		
					
		for(int j = 0; ((j < metrics.size())&&(!interrupted)); j++)
		{
				//Get the next metric
				NetworkMetric metric = (NetworkMetric)metrics.get(j);

				//Compute the metric on this random network
				double t = metric.analyze(graph,  directed);
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
		int roundsPerThread = numRounds / numThreads;
		int remainder = numRounds - roundsPerThread * numThreads;
		//Create an array to store the matrix results for each thread
		random_results = new double[numThreads][roundsPerThread][metrics.size()];
		
		//Let them tell us how much they have completed
		int completed[][] = new int[numThreads][1];
		
		//Keep pointers to all of our threads
		Thread threads[] = new Thread[numThreads];
		
		//For each thread
		for(int i = 0; ((i < numThreads) &&(!interrupted)); i++)
		{
			//Create a new copy of the metrics for each thread
			LinkedList threadMetrics = new LinkedList();
			ListIterator<NetworkMetric> iter = metrics.listIterator();
			while(iter.hasNext())
			{
				NetworkMetric nextMetric = iter.next();
				threadMetrics.addLast((NetworkMetric)nextMetric.copy());
			}
			
			//Create the new thread
			int threadRounds = roundsPerThread;
			
			if(i < remainder)
				threadRounds++;
			AnalyzeWorkerThread thread = new AnalyzeWorkerThread(i, threadRounds, networkModel.copy(), threadMetrics, random_results[i], completed[i]);
			
			//start the thread running
			threads[i] = new Thread(thread);
			threads[i].start();
		}
		
		
		try
		{
			for(int i = 0; ((i < numThreads) &&(!interrupted)); i++)
			{
				threads[i].join();
			}
		}catch(Exception e){e.printStackTrace();}
		
		data = new Object[metrics.size()][4];
		
		
		
		//For each metric
		for(int i = 0; ((i < metrics.size()) &&(!interrupted)); i++)
		{
			//Compute the avearge for this metric
			//accross all of the rounds
			double average = 0;
			for(int  t = 0; ((t < numThreads) &&(!interrupted)); t++)
			{
				for(int j = 0; ((j <roundsPerThread) &&(!interrupted)); j++)
				{
					average += random_results[t][j][i];
				}
			}
			
			average /= (double)(numThreads * roundsPerThread);
			
			//Compute the standard deviation
			double std = 0;

			
			for(int t = 0; ((t < numThreads) &&(!interrupted)); t++)
			{
				for(int j = 0; ((j < roundsPerThread) &&(!interrupted)); j++)
				{
					std += Math.pow(random_results[t][j][i] - average, 2.0d);
					
				}
			}
			
			std = Math.sqrt(std / (double)(roundsPerThread * numThreads));
			data[i][0] = metric_names[i];
			data[i][1] = new Double(network_results[i]);
			data[i][2] = new Double(average);
			data[i][3] = new Double(std);


			DecimalFormat df = new DecimalFormat("0.0000000");

			if((network_results[i] > Double.NEGATIVE_INFINITY) && (network_results[i] < Double.POSITIVE_INFINITY))
			{
				data[i][1] = new Double(df.format(network_results[i]));
			}
			
			if((average > Double.NEGATIVE_INFINITY) && (average < Double.POSITIVE_INFINITY))
			{
				data[i][2] = new Double(df.format(average));
			}

			if((std > Double.NEGATIVE_INFINITY) && (std < Double.POSITIVE_INFINITY))
			{
				data[i][3] = new Double(df.format(std));
			}

		}
		

		
		
				
	}


	/**
	 *
	 */
	public double[][][] getResults()
	{
	
		return random_results;
	}


	

	/**
	*
	*/
	public RandomNetworkGenerator getGenerator()
	{
		return networkModel;
	}

	/**
	*
	*/
	public boolean getDirected()
	{
		return directed;
	}

	
	//TODO
	public int getTime()
	{
		return 0;
	}

	/**
	*
	*/
	public RandomNetwork getNetwork()
	{
		return graph;
	}
	
	
	/**
	 *
	 *
	*/
	public Object[][] getData()
	{
		return data;
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
