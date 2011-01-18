/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusterMaker.algorithms.autosome.cluststruct;

import java.io.Serializable;
/**
 *
 * @author Aaron
 */
public class Point implements Serializable{
    float[] point;
    
    public Point(int size) {point = new float[size];}
    
    public Point(int[] pt){
        point = new float[pt.length];
        for(int i = 0; i < point.length; i++) point[i] = (float) pt[i];
    }
    
    public Point(double[] pt){
        point = new float[pt.length];
        for(int i = 0; i < point.length; i++) point[i] = (float) pt[i];
    }
    
    public Point(float[] pt){point = pt;}
    
    public float[] getPoint() {return point;}
    
    public int[] getIntegerPoint() {
        int[] pt = new int[point.length];
        for(int i = 0; i < pt.length; i++) pt[i] = (int)point[i];
        return pt;
    }

}
