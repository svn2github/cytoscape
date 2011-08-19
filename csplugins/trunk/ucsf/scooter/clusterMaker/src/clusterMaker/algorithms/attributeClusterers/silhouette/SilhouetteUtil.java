/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clusterMaker.algorithms.attributeClusterers.silhouette;

import clusterMaker.algorithms.attributeClusterers.DistanceMetric;
import clusterMaker.algorithms.attributeClusterers.Matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;


/**
 *
 * @author lucasyao
 *
 * This is the utility class for Silhouette calculation and related functions
 */
public class SilhouetteUtil {
    
    /**
     * This method calculates the silhouette for a given data matrix using a metric as it's string.  The current
     * cluster is provided.
     *
     * @param matrix the data matrix
     * @param metric the distrance metric we're using
     * @param labels the labels for each of clusters
     * @return the resulting silhouette
     */
    public static SilhouetteResult SilhouetteCalculator(Matrix matrix, DistanceMetric metric, Integer[] labels)
    {
        double[][] distanceMatrix = matrix.getDistanceMatrix(metric);
        return SilhouetteCalculator(distanceMatrix, labels);
    }
    
    /**
     * This method calculates the silhouette for a given matrix and the current cluster labels.
     * @para distancematrix is 2-D double arrays for the pair-wise distances
     * @para labels the labels for each of clusters
     * @return the resulting silhouette
     */
    public static SilhouetteResult SilhouetteCalculator(double[][] distancematrix, Integer[] labels)
    {
 
        SilhouetteResult silresult = new SilhouetteResult();
        HashMap<Integer, Integer> classlabels = new HashMap<Integer, Integer>();
        int samplenum = labels.length;
    
        // Get the size of each cluster
        for(int i=0; i<samplenum; i++)
        {
            Integer currentlabel = labels[i];
            if(classlabels.containsKey(currentlabel))
            {
                int count = classlabels.get(currentlabel).intValue()+1;
                classlabels.put(currentlabel, Integer.valueOf(count));
            } else {
                classlabels.put(currentlabel, 1);
            }
        }

        // OK, now calculate the silhouete
        for(int i=0;i<labels.length;i++)
        {
            double silhouettevalue=0;
            double a=0;
            double b=0;
            int classnum = classlabels.size();
            Integer classlabel = labels[i];
        
            //initializing
            HashMap<Integer, Double> bvalues = new HashMap<Integer, Double>();
            for(int k=0; k<classnum; k++)
            {
                Set<Integer> labelset = classlabels.keySet();
                for (Integer label: labelset)
                {
                    bvalues.put(label, Double.valueOf(0));
                }
            }
        
            //calculate distance by differnt classes
            for(int j=0;j<samplenum;j++)
            {
                if (i == j) continue;
                Integer currentclasslabel = labels[j];
                double distancevalue = bvalues.get(currentclasslabel).doubleValue();
                distancevalue = distancevalue + distancematrix[i][j];
                bvalues.put(currentclasslabel, Double.valueOf(distancevalue));
            }
        
            //calculate a b and silhouette
            for(int k=0;k<classnum;k++)
            {
                double mindis = Double.MAX_VALUE;
                Integer minlabel = null;
                Set<Integer> labelset = classlabels.keySet();
                for (Integer label: labelset)
                {
                    int count = classlabels.get(label).intValue();
                    double value = bvalues.get(label).doubleValue();

                    if(label.equals(classlabel))
                        a = value/count;
                    else {
                     
                        double avedistance = value/count;
                        if(avedistance<mindis)
                        {
                            mindis = avedistance;
                            minlabel = label;
                        }
                    }
                }    
                b = mindis;

                if(a>b) {
                    silhouettevalue = (b-a)/a;
                } else  {
                    silhouettevalue = (b-a)/b;
                }
            
                silresult.addSilhouettevalue(silhouettevalue, minlabel);
            
            }
        }
    
        return silresult;
    }
    
}
