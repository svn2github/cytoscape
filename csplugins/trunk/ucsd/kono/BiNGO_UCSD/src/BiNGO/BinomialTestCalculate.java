package BiNGO;

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

import cytoscape.task.TaskMonitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


/**
 * ************************************************************
 * BinomialTestCalculate.java
 * --------------------------
 * <p/>
 * Steven Maere & Karel Heymans (c) March 2005
 * <p/>
 * Class that calculates the binomial test results for a given cluster.
 * *************************************************************
 */


public class BinomialTestCalculate implements CalculateTestTask {

    /*--------------------------------------------------------------
    FIELDS.
    --------------------------------------------------------------*/

    /**
     * hashmap with as values the values of small n and with as key the GO label.
     */
    private static HashMap mapSmallN;
    /**
     * hashmap with as values the values of small x and with as key the GO label.
     */
    private static HashMap mapSmallX;
    /**
     * int containing value for big N.
     */
    private static HashMap mapBigN;
    /**
     * int containing value for big X.
     */
    private static HashMap mapBigX;
    /**
     * hashmap with the Binomial Test results as values and as key the GO label.
     */
    private static HashMap binomialTestMap;

    // Keep track of progress for monitoring:
    private int maxValue;
    private TaskMonitor taskMonitor = null;
    private boolean interrupted = false;

    /*--------------------------------------------------------------
    CONSTRUCTOR.
    --------------------------------------------------------------*/

    /**
     * constructor with as arguments the selected cluster, the reference set (from graph or annotation file),
     * the annotation, the ontology and the significance threshold.
     */

    public BinomialTestCalculate(DistributionCount dc) {
        // calculates x (#genes in selection assigned to each GO class), X (total #genes in selection),
        // n (# genes in reference set assigned to each GO class) and N (total # genes in reference set) from input
        dc.calculate();
        this.mapSmallN = dc.getMapSmallN();
        this.mapSmallX = dc.getMapSmallX();
        this.mapBigN = dc.getMapBigN();
        this.mapBigX = dc.getMapBigX();
        this.maxValue = mapSmallX.size();


    }

    public BinomialTestCalculate(DistributionCount dc,TaskMonitor taskMonitor) {
        this(dc);
        this.taskMonitor = taskMonitor;
    }

    /*--------------------------------------------------------------
      METHODS.
    --------------------------------------------------------------*/

    /**
     * method that redirects the calculation of the Binomial Tests to the BinomialDistribution class.
     */
    public void calculate() {

        BinomialDistribution bt;
        binomialTestMap = new HashMap();

        HashSet set = new HashSet(mapSmallX.keySet());
        Iterator iterator = set.iterator();
        Integer id;
        Integer smallXvalue;
        Integer smallNvalue;
        Integer bigXvalue;
        Integer bigNvalue;
        int currentProgress = 0;
        try {
            while (iterator.hasNext()) {
                id = new Integer(iterator.next().toString());
                smallXvalue = new Integer(mapSmallX.get(id).toString());
                smallNvalue = new Integer(mapSmallN.get(id).toString());
                bigXvalue = new Integer(mapBigX.get(id).toString());
                bigNvalue = new Integer(mapBigN.get(id).toString());
                bt = new BinomialDistribution(smallXvalue.intValue(),
                        bigXvalue.intValue(),
                        smallNvalue.intValue(),
                        bigNvalue.intValue());
                binomialTestMap.put(id, bt.calculateBinomialDistribution());

                // Calculate Percentage.  This must be a value between 0..100.
                int percentComplete = (int) (((double) currentProgress / maxValue) * 100);

                //  Estimate Time Remaining
                long timeRemaining = maxValue - currentProgress;

                //  Update the Task Monitor.
                //  This automatically updates the UI Component w/ progress bar.
                if (taskMonitor != null) {
                    taskMonitor.setPercentCompleted(percentComplete);
                    taskMonitor.setStatus("Calculating Binomial P-value: " + currentProgress + " of " + maxValue);
                    taskMonitor.setEstimatedTimeRemaining(timeRemaining);
                }

                currentProgress++;

                if (interrupted) {
                    throw new InterruptedException();
                }

            }
        } catch (InterruptedException e) {
            taskMonitor.setException(e, "Binomial P-value calculation cancelled");
        }
    }

    /*--------------------------------------------------------------
      GETTERS.
    --------------------------------------------------------------*/

    /**
     * getter for the binomial test map.
     *
     * @return HashMap binomialTestMap
     */
    public HashMap getTestMap() {
        return binomialTestMap;
    }

    /**
     * getter for mapSmallX.
     *
     * @return HashMap mapSmallX
     */
    public HashMap getMapSmallX() {
        return mapSmallX;
    }

    /**
     * getter for mapSmallN.
     *
     * @return HashMap mapSmallN
     */
    public HashMap getMapSmallN() {
        return mapSmallN;
    }

    public HashMap getMapBigX() {
        return mapBigX;
    }

    public HashMap getMapBigN() {
        return mapBigN;
    }


    /**
     * Run the Task.
     */
    public void run() {
        calculate();
    }

    /**
     * Non-blocking call to interrupt the task.
     */
    public void halt() {
        this.interrupted = true;
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor TaskMonitor Object.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor) {
        if (this.taskMonitor != null) {
            throw new IllegalStateException("Task Monitor is already set.");
        }
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets the Task Title.
     *
     * @return human readable task title.
     */
    public String getTitle() {
        return new String("Calculating Binomial Distribution");
       }

	
}

