package GOlorize.BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class that calculates the binomial test results for a given cluster.         
 **/

import java.util.*; 

import cytoscape.data.annotation.*;

/***************************************************************
 * BinomialTestCalculate.java   
 * --------------------------
 *
 * Steven Maere & Karel Heymans (c) March 2005
 *
 * Class that calculates the binomial test results for a given cluster.  
 ***************************************************************/


public class BinomialTestCalculate implements CalculateTestTask {
    
    
    
    
	/*--------------------------------------------------------------
      FIELDS.
      --------------------------------------------------------------*/
	/** Vector of selected nodes.*/
	private static Vector selectedNodes;
	/** Vector of reference nodes.*/
	private static Vector graphNodes;
	/** hashmap with as values the values of small n and with as key the GO label.*/
	private static HashMap mapSmallN;
	/** hashmap with as values the values of small x and with as key the GO label.*/
	private static HashMap mapSmallX;
	/** int containing value for big N.*/
	private static int bigN;	
	/** int containing value for big X.*/
	private static int bigX;
	/** hashmap with the Binomial Test results as values and as key the GO label.*/
	private static HashMap binomialTestMap;

	// Keep track of progress for monitoring:
	protected int currentProgress;
	protected int lengthOfTask;
	protected String statusMessage;
	protected boolean done;
	protected boolean canceled;
    
    
    
    
	/*--------------------------------------------------------------
      CONSTRUCTOR.
      --------------------------------------------------------------*/

	/**
	 * constructor with as arguments the selected cluster, the reference set (from graph or annotation file), 
	 * the annotation, the ontology and the significance threshold. 
	 */
	
	 public BinomialTestCalculate (Vector selectedNodes,
									Vector graphNodes,
									Annotation annotation,
									Ontology ontology){
		this.selectedNodes = selectedNodes;
		this.graphNodes = graphNodes;		
		// calculates x (#genes in selection assigned to each GO class), X (total #genes in selection), 
		// n (# genes in reference set assigned to each GO class) and N (total # genes in reference set) from input
		DistributionCount dc = new DistributionCount(annotation,ontology, selectedNodes, graphNodes);
		dc.countSmallN();
		dc.countSmallX();
		dc.countBigN();
		dc.countBigX();
		this.mapSmallN = dc.getHashMapSmallN();
		this.mapSmallX = dc.getHashMapSmallX();
		this.bigN = dc.getBigN();
		this.bigX = dc.getBigX();
		this.currentProgress = 0;
		this.lengthOfTask = mapSmallX.size();
		this.done = false;
		this.canceled = false;
	}
    
    
    
    
    
	/*--------------------------------------------------------------
		METHODS.
      --------------------------------------------------------------*/	
		
	/**
	 * method that redirects the calculation of the Binomial Tests to the BinomialDistribution class.
	 */
	public void calculate(){
		
		BinomialDistribution bt;
		binomialTestMap = new HashMap();

		HashSet set = new HashSet(mapSmallX.keySet());
		Iterator iterator = set.iterator();
		Integer id;
		Integer smallXvalue;
		Integer smallNvalue;
		this.currentProgress = 0;
		this.lengthOfTask = set.size();
		this.done = false;
		this.canceled = false;
		
		
		while(iterator.hasNext()){
			id = new Integer(iterator.next().toString());
			smallXvalue = new Integer(mapSmallX.get(id).toString());
			smallNvalue = new Integer(mapSmallN.get(id).toString());
			bt = new BinomialDistribution(smallXvalue.intValue() , 
								          bigX, 
								          smallNvalue.intValue() , 
								          bigN);
			binomialTestMap.put(id, bt.calculateBinomialDistribution());
			this.currentProgress++;
			double percentDone = (this.currentProgress * 100) / this.lengthOfTask;
			this.statusMessage = "Completed " + percentDone + "%.";	
		}
		this.done = true;
		this.currentProgress = this.lengthOfTask;
		
	}
    
    
    
    
    
	/*--------------------------------------------------------------
		GETTERS.
      --------------------------------------------------------------*/	
	
	/**
	 * getter for the binomial test map.
	 *
	 * @return HashMap binomialTestMap
	 */
	public HashMap getTestMap(){
		return binomialTestMap;
	}

	/**
	 * getter for mapSmallX.
	 *
	 * @return HashMap mapSmallX
	 */
	public HashMap getMapSmallX(){
		return mapSmallX;
	}

	/**
	 * getter for mapSmallN.
	 *
	 * @return HashMap mapSmallN
	 */
	public HashMap getMapSmallN(){
		return mapSmallN;
	}

	/**
	 * getter for bigX.
	 *
	 * @return int bigX
	 */
	public int getBigX(){
		return bigX;
	}

	/**
	 * getter for bigN.
	 *
	 * @return int bigN
	 */
	public int getBigN(){
		return bigN;
	}

  	/**
     * @return the current progress
     */
    public int getCurrentProgress() {
        return this.currentProgress;
    }//getCurrentProgress

    /**
     * @return the total length of the task
     */
    public int getLengthOfTask() {
        return this.lengthOfTask;
    }//getLengthOfTask

    /**
     * @return a <code>String</code> describing the task being performed
     */
    public String getTaskDescription() {
        return "Calculating Binomial Tests";
    }//getTaskDescription

    /**
     * @return a <code>String</code> status message describing what the task
     *         is currently doing (example: "Completed 23% of total.", "Initializing...", etc).
     */
    public String getCurrentStatusMessage() {
        return this.statusMessage;
    }//getCurrentStatusMessage

    /**
     * @return <code>true</code> if the task is done, false otherwise
     */
    public boolean isDone() {
        return this.done;
    }//isDone

    /**
     * Stops the task if it is currently running.
     */
    public void stop() {
        this.canceled = true;
        this.statusMessage = null;
    }//stop

    /**
     * @return <code>true</code> if the task was canceled before it was done
     *         (for example, by calling <code>MonitorableSwingWorker.stop()</code>,
     *         <code>false</code> otherwise
     */
    // TODO: Not sure if needed
    public boolean wasCanceled() {
        return this.canceled;
    }//wasCanceled
	
    public void start(boolean return_when_done) {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new DoTask();
            }//construct
        };
        worker.start();
        if (return_when_done) {
            worker.get(); // maybe use finished() instead
        }
    }//starts

    class DoTask {
        DoTask() {
            calculate();
        }
    }

	
}

