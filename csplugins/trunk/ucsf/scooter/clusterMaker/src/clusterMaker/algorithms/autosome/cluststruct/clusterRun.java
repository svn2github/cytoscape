package clusterMaker.algorithms.autosome.cluststruct;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */





import java.util.*;
import clusterMaker.algorithms.autosome.launch.Settings;
import java.io.Serializable;
/**
 * store all cluster properties for a given cluster run
 * @author Aaron
 */
public class clusterRun implements Serializable{
    
    public Point[] nodes; //nodes from mapping
    public double[][] edges; //edges of clusters
    public ArrayList[] ids; //data ids
    public String[] labelsSorted;
    public double[][] membership;
    public double[] memTotal; //sum of fractional membership for each cluster
    public float[][] DEC; //density equalized cartogram
    public double thresh; //p-value threshold for pruning tree
    public cluster[] c; //all clusters for current run
    public boolean[] usedEdges; //which edges were used for current clustering?
    private int size = 0; //number of data points
    private String inputFile = new String(); //directory of input file
    public String[][] fcn_nodes; //stores nodes pf fuzzy cluster network
    public String[][] fcn_edges; //stores edges of fuzzy cluster network
    public int[] columnClusters; //store column clusters

    
    //////////////benchmark metrics//////////
    public double Fmeasure = 0;
    public double Precision = 0;
    public double Recall = 0;
    public double NMI = 0;
    public double adjRand = 0;
    
    public clusterRun(Point[] nodes, double[][] edges,ArrayList[] ids, float[][] DEC, double thresh,  int size){

        this.nodes = nodes;
        this.edges = edges;
    
        this.ids = ids;
        this.thresh = thresh;
        this.DEC = DEC;
        usedEdges = new boolean[edges.length];
        for(int i = 0; i < usedEdges.length; i++) usedEdges[i] = false;
        this.size = size;
    }
    
    public clusterRun(cluster[] c) {this.c = c;  usedEdges = new boolean[edges.length];}
    
    public clusterRun(Point[] nodes, ArrayList[] ids, int size){
        this.nodes = nodes;
        this.ids = ids;
        this.size = size;
    }

    public clusterRun() {};
    
    public void setInputFile(String input) {inputFile = input;}
    public String getInputFile() {return inputFile;}
            
    public void makeMembership(Settings s){
        int dataCount = 0;
        for(int i = 0; i < c.length; i++) dataCount += c[i].ids.size();
        membership = new double[dataCount][c.length];
        labelsSorted = new String[dataCount];
        for(int i = 0; i < c.length; i++){
            for(int j = 0; j < c[i].ids.size(); j++){
                int id = Integer.valueOf(c[i].ids.get(j).toString());
                labelsSorted[id] = s.input[id].toString();
               // System.out.println(s.input[id].getIdentity()+" "+labelsSorted[id]+" "+i);
                membership[id][i] = 1;
            }
        }
    }
    
    public void updateFuzzy(double d){
        for(int i = 0; i < membership.length; i++){
            for(int j = 0; j < membership[i].length; j++){
                membership[i][j] *= d;
            }
        }
    }
    
    public void sumMembership(){
        memTotal = new double[membership[0].length];
        for(int i = 0; i < memTotal.length; i++){
            for(int j = 0; j < membership.length; j++){
                if(membership[j][i] > 0){
                    //System.out.println(Math.round(membership[j][i])+" "+membership[j][i]);
                    memTotal[i]+=(int)membership[j][i];
                }
            }
        }
    }
    
    public void cleanFuzzy(){
        
        for(int i = 0; i < membership.length; i++){
            double max = 0;
            int pos = 0;
            for(int j = 0; j < membership[i].length; j++){
                if(membership[i][j] >= max) {max = membership[i][j]; pos = j;}                
            }
            for(int k = 0; k < membership[i].length; k++) membership[i][k] = 0;
            membership[i][pos] = max;
        }
    }
    
    public void printFuzzy(){
        for(int i = 0; i < membership.length; i++){
            String[] tokens = labelsSorted[i].split(",");
            System.out.print(tokens[0]+"\t");
            for(int j = 0; j < membership[i].length; j++){
                System.out.print(membership[i][j]+"\t");
            }
            System.out.println();
        }
    }
    
    public void setMetrics(double F, double P, double R, double NMI, double adjRand){
        Fmeasure = F;
        Precision = P;
        Recall = R;
        this.NMI = NMI;
        this.adjRand = adjRand;
    }
    
    public void edgeSort(){
        sortEdges[] se = new sortEdges[edges.length];
        for(int i = 0; i < se.length; i++) se[i] = new sortEdges(edges[i]);
        Arrays.sort(se);
        for(int i = 0; i < se.length; i++) edges[i] = se[i].edge;
    }
    
    public int getSize() {return size;}
    
    private class sortEdges implements Comparable{
         double dist;
         double[] edge;
         
         public sortEdges(double[] edge){
             this.edge = edge;
             dist = edge[2];
         }
         
         public int compareTo(Object o){
           double dist2 = ((sortEdges)o).dist;
           return (dist < dist2 ? -1 : (dist == dist2 ? 0 : 1));
       }
    }
}
