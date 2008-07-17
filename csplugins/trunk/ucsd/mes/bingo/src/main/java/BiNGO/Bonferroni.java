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
 * * Description: Class implementing the Bonferroni multiple testing correction.         
 **/


import cytoscape.task.TaskMonitor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Arrays;

/**
 * ****************************************************************
 * Bonferroni.java:         Steven Maere & Karel Heymans (c) March 2005
 * ----------------
 * <p/>
 * Class implementing the Bonferroni multiple testing correction.
 * *****************************************************************
 */


public class Bonferroni implements CalculateCorrectionTask {

    /*--------------------------------------------------------------
    FIELDS.
    --------------------------------------------------------------*/
    
    private HashEntry[] hash ;
    /**
     * the GO labels that have been tested (constructor input).
     */
    private static String [] goLabels;
    /**
     * the raw p-values that were given as input for the constructor, order corresponds to String [] goLabels.
     */
    private static String [] pvalues;
    /**
     * the goLabels ordened according to the ordened pvalues.
     */
    private static String [] ordenedGOLabels;
    /**
     * the raw p-values ordened in ascending order.
     */
    private static String [] ordenedPvalues;
    /**
     * the adjusted p-values ordened in ascending order.
     */
    private static String [] adjustedPvalues;

    /**
     * hashmap with the results (adjusted p-values) as values and the GO labels as keys.
     */
    private static HashMap correctionMap;

    /**
     * the significance level.
     */
    private static BigDecimal alpha;
    /**
     * the number of tests.
     */
    private static int m;
    /**
     * scale for the division in de method 'runBonferroni'.
     */
    private static final int RESULT_SCALE = 100;
    // Keep track of progress for monitoring:

    private int maxValue;
    private TaskMonitor taskMonitor = null;
    private boolean interrupted = false;

    /*--------------------------------------------------------------
    CONSTRUCTOR.
    --------------------------------------------------------------*/

    /**
     * Constructor.
     *
     * @param golabelstopvalues Hashmap of Strings with the goLabels and their pvalues.
     * @param alpha             String with the alpha-level.
     */


    public Bonferroni(HashMap golabelstopvalues, String alpha) {

        //Get all the go labels and their corresponding pvalues from the map
        Iterator iteratorGoLabelsSet = golabelstopvalues.keySet().iterator();
        HashEntry [] hash = new HashEntry [golabelstopvalues.size()];
        String [] pvalues = new String [golabelstopvalues.size()];
        String [] goLabels = new String [golabelstopvalues.size()];
        for (int i = 0; iteratorGoLabelsSet.hasNext(); i++) {
            goLabels[i] = iteratorGoLabelsSet.next().toString() ;
            pvalues[i] = golabelstopvalues.get(new Integer(goLabels[i])).toString();
            hash[i] = new HashEntry(goLabels[i], pvalues[i]) ;
        }
        this.hash = hash ;
        this.pvalues = pvalues;
        this.goLabels = goLabels;
        this.alpha = new BigDecimal(alpha);
        this.m = pvalues.length;
        this.adjustedPvalues = new String[m];
        this.correctionMap = null;
        this.maxValue = pvalues.length;

    }

    public Bonferroni(HashMap golabelstopvalues, String alpha, TaskMonitor taskMonitor) {
        this(golabelstopvalues,alpha);
        this.taskMonitor = taskMonitor;
    }
    
    class HashEntry{
        public String key;
        public String value;
        
        public HashEntry(String k, String v){
            this.key = k ;
            this.value = v ;
        }
    } 
    
    class HashComparator implements java.util.Comparator{
       /* public HashComparator(){        
        }*/
        public int compare(Object o1, Object o2){
            return (new BigDecimal(((HashEntry) o1).value)).compareTo(new BigDecimal(((HashEntry) o2).value)) ;
        }
        /*public boolean equals(Object o){
         return ((Object)this).equals(o) ; 
        }*/
    }

    /*--------------------------------------------------------------
    METHODS.
    --------------------------------------------------------------*/

    /**
     * method that calculates the bonferroni procedure
     * p< alpha/m
     * i* (istar) first i such that the inequality is correct.
     * reject hypotheses for i=1...i*
     * adjusted p-value = m*p
     */
    public void calculate() {

        // ordening the pvalues.
        java.util.Arrays.sort(hash, new HashComparator()) ; 
        this.ordenedPvalues = parse(hash);
        
        // calculating adjusted p-values.
        BigDecimal min = new BigDecimal("" + 1);
        BigDecimal mp;

        for (int i = 0; i < m; i++) {
            mp = new BigDecimal("" + m).multiply(new BigDecimal(ordenedPvalues[i]));
            if (mp.compareTo(min) < 0)
                adjustedPvalues[i] = mp.toString();
            else
                adjustedPvalues[i] = min.toString();
        }

        String [] sortedGOLabels = getOrdenedGOLabels();
        correctionMap = new HashMap();
        for (int i = 0; i < adjustedPvalues.length && i < sortedGOLabels.length; i++) {
            correctionMap.put(sortedGOLabels[i], adjustedPvalues[i]);
        }
    }

 
    
    public String [] parse(HashEntry [] data) {
        String[] keys = new String[data.length];
        String[] values = new String[data.length];
        for(int i = 0; i < data.length; i++){
            keys[i] = data[i].key;
            values[i] = data[i].value;
        }
        ordenedGOLabels = keys;
        return values;
    }

    /*--------------------------------------------------------------
      GETTERS.
    --------------------------------------------------------------*/

    /**
     * getter for the map of corrected p-values.
     *
     * @return HashMap correctionMap.
     */
    public HashMap getCorrectionMap() {
        return correctionMap;
    }

    /**
     * getter for the ordened p-values.
     *
     * @return String[] with the ordened p-values.
     */
    public String[] getOrdenedPvalues() {
        return ordenedPvalues;
    }

    /**
     * getter for the adjusted p-values.
     *
     * @return String[] with the adjusted p-values.
     */
    public String[] getAdjustedPvalues() {
        return adjustedPvalues;
    }

    /**
     * getter for the ordened GOLabels.
     *
     * @return String[] with the ordened GOLabels.
     */
    public String[] getOrdenedGOLabels() {
        return ordenedGOLabels;
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
        return new String("Calculating Bonferroni corrections");
    }


}
