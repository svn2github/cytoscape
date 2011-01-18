package clusterMaker.algorithms.autosome.clustering.kmeans;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Aaron
 */

//package org.c4s.algorithm.cluster;


import java.util.*;

/**

This class is the entry point for constructing Cluster Analysis objects.
Each instance of JCA object is associated with one or more clusters, 
and a Vector of DataPoint objects. The JCA and DataPoint classes are
the only classes available from other packages.
@see DataPoint

**/

public class kMeans {
    private Cluster[] clusters;
    private int miter = 1000;
    private Vector mDataPoints = new Vector();
    private double mSWCSS;

    public kMeans(){};
    
    public kMeans(int k, int iter, Vector dataPoints) {
        clusters = new Cluster[k];
        for (int i = 0; i < k; i++) {
            clusters[i] = new Cluster("Cluster" + i);
        }
        this.miter = iter;
        this.mDataPoints = dataPoints;
    }

    public static ArrayList[] run(double[][] data, int clusterNum, boolean rand){
        if(rand) data = randomizeInput(data);
                
        Vector dataPoints = new Vector();
        
       for(int i = 0; i < data.length; i++){
            dataPoints.add(new DataPoint(data[i],String.valueOf(data[i][0])));
        }
        

        kMeans jca = new kMeans(clusterNum,1000,dataPoints);
        jca.startAnalysis(data[0].length-1);

        Vector[] v = jca.getClusterOutput();
        ArrayList[] al = new ArrayList[v.length];
        for (int i=0; i<v.length; i++){
            al[i] = new ArrayList();
            Vector tempV = v[i];
           //System.out.println("-----------Cluster"+i+"---------");
            Iterator iter = tempV.iterator();
            while(iter.hasNext()){
                DataPoint dpTemp = (DataPoint)iter.next();
                double[] dat = new double[data[0].length];
                dat[0] = Double.valueOf(dpTemp.getObjName());
                for(int k = 1; k < data[0].length; k++){
                    dat[k] = dpTemp.getC(k-1);
                }
                al[i].add(dat);
                //System.out.println(dpTemp.getObjName());//+"["+dpTemp.getX()+","+dpTemp.getY()+","+dpTemp.getZ()+"]");
                //System.out.println(i+"\t"+dpTemp.getObjName()+"\t"+dpTemp.getC(0)+"\t"+dpTemp.getC(1));
                
            }
        }
        
        return al;
    }
    
    
    private static double[][] randomizeInput(double[][] data){
        
        Random r = new Random();
        
        for(int i = 0; i < data.length; i++){
            int p = r.nextInt(data.length);
            int q = r.nextInt(data.length);
            double[] temp = data[p];
            data[p] = data[q];
            data[q] = temp;
        }
        
        return data;
    }
    
    
    private void calcSWCSS() {
        double temp = 0;
        for (int i = 0; i < clusters.length; i++) {
            temp = temp + clusters[i].getSumSqr();
        }
        mSWCSS = temp;
    }

    public void startAnalysis(int d) {
        //set Starting centroid positions - Start of Step 1
        setInitialCentroids(d);
        int n = 0;
        //assign DataPoint to clusters
        loop1: while (true) {
            for (int l = 0; l < clusters.length; l++) 
            {
                clusters[l].addDataPoint((DataPoint)mDataPoints.elementAt(n));
                n++;
                if (n >= mDataPoints.size())
                    break loop1;
            }
        }
        
        //calculate E for all the clusters
        calcSWCSS();
        
        //recalculate Cluster centroids - Start of Step 2
        for (int i = 0; i < clusters.length; i++) {
            clusters[i].getCentroid().calcCentroid();
        }
        
        //recalculate E for all the clusters
        calcSWCSS();

        for (int i = 0; i < miter; i++) {
            //enter the loop for cluster 1
            for (int j = 0; j < clusters.length; j++) {
                for (int k = 0; k < clusters[j].getNumDataPoints(); k++) {
                
                    //pick the first element of the first cluster
                    //get the current Euclidean distance
                    double tempEuDt = clusters[j].getDataPoint(k).getCurrentEuDt();
                    Cluster tempCluster = null;
                    boolean matchFoundFlag = false;
                    
                    //call testEuclidean distance for all clusters
                    for (int l = 0; l < clusters.length; l++) {
                    
                    //if testEuclidean < currentEuclidean then
                        if (tempEuDt > clusters[j].getDataPoint(k).testEuclideanDistance(clusters[l].getCentroid())) {
                            tempEuDt = clusters[j].getDataPoint(k).testEuclideanDistance(clusters[l].getCentroid());
                            tempCluster = clusters[l];
                            matchFoundFlag = true;
                        }
                        //if statement - Check whether the Last EuDt is > Present EuDt 
                        
                        }
//for variable 'l' - Looping between different Clusters for matching a Data Point.
//add DataPoint to the cluster and calcSWCSS

       if (matchFoundFlag) {
		tempCluster.addDataPoint(clusters[j].getDataPoint(k));
		clusters[j].removeDataPoint(clusters[j].getDataPoint(k));
                        for (int m = 0; m < clusters.length; m++) {
                            clusters[m].getCentroid().calcCentroid();
                        }

//for variable 'm' - Recalculating centroids for all Clusters

                        calcSWCSS();
                    }
                    
//if statement - A Data Point is eligible for transfer between Clusters.
                }
                //for variable 'k' - Looping through all Data Points of the current Cluster.
            }//for variable 'j' - Looping through all the Clusters.
        }//for variable 'i' - Number of iterations.
    }

    public Vector[] getClusterOutput() {
        Vector v[] = new Vector[clusters.length];
        for (int i = 0; i < clusters.length; i++) {
            v[i] = clusters[i].getDataPoints();
        }
        return v;
    }


    private void setInitialCentroids(int d) {
        //kn = (round((max-min)/k)*n)+min where n is from 0 to (k-1).
        double[] coors = new double[d];
        for (int n = 1; n <= clusters.length; n++) {
            for(int j = 0; j < d; j++){
                coors[j] = (((getMaxCValue(j) - getMinCValue(j)) / (clusters.length + 1)) * n) + getMinCValue(j);
            }
            Centroid c1 = new Centroid(coors);
            clusters[n - 1].setCentroid(c1);
            c1.setCluster(clusters[n - 1]);
        }
    }

    private double getMaxCValue(int j){
        double temp;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getC(j);
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getC(j) > temp) ? dp.getC(j) : temp;
        }
        return temp;
    }
    
    private double getMinCValue(int j) {
        double temp = 0;
        temp = ((DataPoint) mDataPoints.elementAt(0)).getC(j);
        for (int i = 0; i < mDataPoints.size(); i++) {
            DataPoint dp = (DataPoint) mDataPoints.elementAt(i);
            temp = (dp.getC(j) < temp) ? dp.getC(j) : temp;
        }
        return temp;
    }

    public int getKValue() {
        return clusters.length;
    }

    public int getIterations() {
        return miter;
    }

    public int getTotalDataPoints() {
        return mDataPoints.size();
    }

    public double getSWCSS() {
        return mSWCSS;
    }

    public Cluster getCluster(int pos) {
        return clusters[pos];
    }
}

/*-----------------Cluster.java----------------*/

//package org.c4s.algorithm.cluster;


/**
 * This class represents a Cluster in a Cluster Analysis Instance. A Cluster is associated
 * with one and only one JCA Instance. A Cluster is related to more than one DataPoints and
 * one centroid.
 * @author Shyam Sivaraman
 * @version 1.1
 * @see DataPoint
 * @see Centroid
 */



class Cluster {
    private String mName;
    private Centroid mCentroid;
    private double mSumSqr;
    private Vector mDataPoints;

    public Cluster(String name) {
        this.mName = name;
        this.mCentroid = null; //will be set by calling setCentroid()
        mDataPoints = new Vector();
    }

    public void setCentroid(Centroid c) {
        mCentroid = c;
    }

    public Centroid getCentroid() {
        return mCentroid;
    }

    public void addDataPoint(DataPoint dp) { //called from CAInstance
        dp.setCluster(this); //initiates a inner call to
//calcEuclideanDistance() in DP.
        this.mDataPoints.addElement(dp);
        calcSumOfSquares();
    }

    public void removeDataPoint(DataPoint dp) {
        this.mDataPoints.removeElement(dp);
        calcSumOfSquares();
    }

    public int getNumDataPoints() {
        return this.mDataPoints.size();
    }

    public DataPoint getDataPoint(int pos) {
        return (DataPoint) this.mDataPoints.elementAt(pos);
    }

    public void calcSumOfSquares() { //called from Centroid
        int size = this.mDataPoints.size();
        double temp = 0;
        for (int i = 0; i < size; i++) {
            temp = temp + ((DataPoint)
this.mDataPoints.elementAt(i)).getCurrentEuDt();
        }
        this.mSumSqr = temp;
    }

    public double getSumSqr() {
        return this.mSumSqr;
    }

    public String getName() {
        return this.mName;
    }

    public Vector getDataPoints() {
        return this.mDataPoints;
    }

}

/*---------------Centroid.java-----------------*/

//package org.c4s.algorithm.cluster;

/**
 * This class represents the Centroid for a Cluster. The initial centroid is calculated
 * using a equation which divides the sample space for each dimension into equal parts
 * depending upon the value of k.
 * @author Shyam Sivaraman
 * @version 1.0
 * @see Cluster
 */

class Centroid {
    private double[] coors;
    private Cluster mCluster;

    public Centroid(double[] coors) {
        this.coors = new double[coors.length];
        for(int i = 0; i < coors.length; i++) this.coors[i] = coors[i];
    }

    public void calcCentroid() { //only called by CAInstance
        int numDP = mCluster.getNumDataPoints();
        double[] tempCoor = new double[coors.length];
        int i;
        //caluclating the new Centroid
        for (i = 0; i < numDP; i++) {
            for(int j = 0; j < coors.length; j++){
                tempCoor[j] = tempCoor[j] + mCluster.getDataPoint(i).getC(j); 
            }

        }
        for(int k = 0; k < coors.length; k++){
            coors[k] = tempCoor[k] / numDP;
        }

        //calculating the new Euclidean Distance for each Data Point
        for(int k = 0; k < coors.length; k++){
            tempCoor[k] = 0;
        }
        
        for (i = 0; i < numDP; i++) {
            mCluster.getDataPoint(i).calcEuclideanDistance();
        }
        //calculate the new Sum of Squares for the Cluster
        mCluster.calcSumOfSquares();
    }

    public void setCluster(Cluster c) {
        this.mCluster = c;
    }

    public double getC(int i) {
        return coors[i];
    }



    public Cluster getCluster() {
        return mCluster;
    }

}

/*----------------DataPoint.java----------------*/

//package org.c4s.algorithm.cluster;

/**
    This class represents a candidate for Cluster analysis. A candidate must have
    a name and two independent variables on the basis of which it is to be clustered.
    A Data Point must have two variables and a name. A Vector of  Data Point object
    is fed into the constructor of the JCA class. JCA and DataPoint are the only
    classes which may be available from other packages.
    @author Shyam Sivaraman
    @version 1.0
    @see JCA
    @see Cluster
*/

 class DataPoint {
    private double[] coors;
    private String mObjName;
    private Cluster mCluster;
    private double mEuDt;

    public DataPoint(double[] coors, String name) {
        this.coors = new double[coors.length-1];
        for(int i = 1; i < coors.length; i++) {
            this.coors[i-1] = coors[i];
        }
        this.mObjName = name;
        this.mCluster = null;
    }

    public void setCluster(Cluster cluster) {
        this.mCluster = cluster;
        calcEuclideanDistance();
    }

    public void calcEuclideanDistance() { 
    
        double sum = 0;
        for(int i = 0; i < coors.length; i++)
            sum += Math.pow((coors[i] - mCluster.getCentroid().getC(i)),2);
    //called when DP is added to a cluster or when a Centroid is recalculated.
        mEuDt = Math.sqrt(sum);
    }

    public double testEuclideanDistance(Centroid c) {
        double sum = 0;
        for(int i = 0; i < coors.length; i++)
            sum += Math.pow((coors[i] - c.getC(i)),2);   
        mEuDt = Math.sqrt(sum);
        return mEuDt;
    }

    public double getC(int i) {
        return coors[i];
    }


    public Cluster getCluster() {
        return mCluster;
    }

    public double getCurrentEuDt() {
        return mEuDt;
    }

    public String getObjName() {
        return mObjName;
    }

}

/*-----------------PrgMain.java---------------*/




