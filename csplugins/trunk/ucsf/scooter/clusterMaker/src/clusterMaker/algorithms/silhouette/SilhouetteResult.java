/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clusterMaker.algorithms.silhouette;
import java.util.ArrayList;

/**
 *
 * @author lucasyao
 *
 * TODO: Package documentation
 */
public class SilhouetteResult {
    
    int samplenumber;
    ArrayList<Double> silhouetteValues;
    ArrayList<Integer> neighborLabels;
    
    /**
     * !DOCUMENT ME!
     */
    public SilhouetteResult()
    {
        samplenumber = 0;
        silhouetteValues = new ArrayList<Double>();
        neighborLabels = new ArrayList<Integer>();
    }

    /**
     * !DOCUMENT ME!
     */
    public void addSilhouettevalue(double value, Integer label)
    {
        samplenumber++;
        silhouetteValues.add(value);
        neighborLabels.add(label);
    }

    /**
     * !DOCUMENT ME!
     */
    public void deleteSilhouettevalue(int index)
    {
        samplenumber--;
        silhouetteValues.remove(index);
        neighborLabels.remove(index);
    }

    /**
     * !DOCUMENT ME!
     */
    public double getSilhouettevalue(int index)
    {
        
        return silhouetteValues.get(index).doubleValue();
    }

    /**
     * !DOCUMENT ME!
     */
    public Integer getSilhouetteneighborlabel(int index)
    {
        return neighborLabels.get(index);
    }

    /**
     * Return the average silhouette for this clustering.
     *
     * @return the average silhouette
     */
    public double getAverageSilhouette()
    {
        double avgS = 0;
        for (Double v: silhouetteValues) {
            avgS = avgS+v.doubleValue();
        }
        return avgS/(double)silhouetteValues.size();
    }
    
}
