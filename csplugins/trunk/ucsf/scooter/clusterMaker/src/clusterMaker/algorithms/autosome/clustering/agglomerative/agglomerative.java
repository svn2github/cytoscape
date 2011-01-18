/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusterMaker.algorithms.autosome.clustering.agglomerative;


import clusterMaker.algorithms.autosome.cluststruct.*;
import java.util.*;
import clusterMaker.algorithms.autosome.launch.Settings;
/**
 *
 * @author Aaron
 */
public class agglomerative {
    
    private dataItem[] data;//user data to cluster
    private ArrayList[] hierarchy;//data structure for clusters
    private int clustNum = 1;//number of clusters to stop at
    private int level = 0;//level in cluster hierarchy
    private int distanceFn = 1; //distance function:1=single,2=complete,3=average,4=ward's
    private Settings s;
    
    public agglomerative(dataItem[] data, int clustNum, int distanceFn, Settings s)
            {this.data = data; 
             this.clustNum = clustNum;
             hierarchy = new ArrayList[data.length-(clustNum-1)];
             for(int i = 0; i < hierarchy.length; i++) {
                 hierarchy[i] = new ArrayList();
             }
             for(int j = 0; j < data.length; j++) {
                 cluster c = new cluster();
                 c.addData(j);
                 hierarchy[0].add(c);
             }
             this.distanceFn = distanceFn;
             this.s=s;
            }
    
    public agglomerative() {};
    
    public cluster[] runAgg_Mapping(clusterRun cr, int clustNum, int distanceFn, boolean mds, Settings s){


            this.s=s;
            //convert dimensionally reduced clusterRun to dataItem object 

            ///////////////////////////////////////////////////

            ArrayList a = new ArrayList();
        
            int[] nodeKey = new int[cr.getSize()];
            double[][] origNodes = new double[cr.nodes.length][cr.nodes[0].getPoint().length+1];
            String[] labelKey = new String[cr.getSize()];
            
            for(int i = 0; i < cr.c.length; i++){

                for(int j = 0; j < cr.c[i].ids.size(); j++){
                    
                    String identifier = cr.c[i].ids.get(j).toString();

                    double[] data = (double[]) cr.c[i].indices.get(j);
                    
                    int nodeIndex = (int)data[0];
                    
                    origNodes[nodeIndex] = data;
                    //cluster original data (no dimensional reduction)
                    if(!mds){
                        String[] tokens = cr.c[i].labels.get(j).toString().split(",");
                        data = new double[tokens.length];
                        for(int w = 1; w < tokens.length; w++) data[w] = Double.valueOf(tokens[w]);
                    }
                    float[] values = new float[data.length-1];
                    for(int k = 1; k < data.length; k++){
                        values[k-1] = (float) data[k];
                    }
                    

                    //map dataItem id to node index
                    nodeKey[Integer.valueOf(cr.c[i].ids.get(j).toString())] = nodeIndex;
                
                    a.add(new dataItem(values,identifier));
      
                }
            }
            
            this.data = new dataItem[a.size()];
            for(int i = 0; i < a.size(); i++){
                data[i] = (dataItem) a.get(i);
            }
            
            //map dataItem id to dataItem label
            String[] idKey = new String[cr.getSize()];
            for(int i = 0; i < cr.ids.length; i++){
                for(int j = 0; j < cr.ids[i].size(); j++){
                    int id = Integer.valueOf(cr.ids[i].get(j).toString());
                    idKey[id] = String.valueOf(new StringTokenizer(s.input[id].toString(),",").nextToken());
                    labelKey[id] = s.input[id].toString();
                }
            }
            //////////////////////////////////////////////////////////////////
            
             this.clustNum = clustNum;
             hierarchy = new ArrayList[data.length-(clustNum-1)];
             for(int i = 0; i < hierarchy.length; i++) {
                 hierarchy[i] = new ArrayList();
             }
             for(int j = 0; j < data.length; j++) {
                 cluster c = new cluster();
                 c.addData(j);
                 hierarchy[0].add(c);
             }
             this.distanceFn = distanceFn;
             
             //run agglomerative clustering
             cluster[] clusters = run();
             
             //adjust clustering data for map projection
             for(int i = 0; i < clusters.length; i++){
                ArrayList ids = new ArrayList();
                ArrayList labels = new ArrayList();
                ArrayList indices = new ArrayList();
                ArrayList nodeIndices = new ArrayList();
            
                for(int j = 0; j < clusters[i].ids.size(); j++){                    
                    int id = Integer.valueOf(clusters[i].ids.get(j).toString());
                    ids.add(id);
                    labels.add(labelKey[id]);                   
                   // String[] tokens = clusters[i].labels.get(j).toString().split(",");
                    double[] ind = new double[origNodes[nodeKey[id]].length];
                    for(int p = 1; p < origNodes[nodeKey[id]].length; p++) ind[p] = origNodes[nodeKey[id]][p];
                    indices.add(ind);
                    nodeIndices.add(nodeKey[id]); 
                    //System.out.println(id+"\t"+nodeKey[id]+"\t"+idKey[id]);
                }
                clusters[i] = new cluster(indices, labels, ids);
                clusters[i].nodeIndices = nodeIndices;
            }


        for(int i = 0; i < cr.usedEdges.length; i++) cr.usedEdges[i] = false;
             
        return clusters;
    }
    
    

    
    public cluster[] run(){
        float[][] distMatrix = getDistMatrix();
       // printDistMatrix(distMatrix);
        for(int i = data.length; i > clustNum; i--)
            distMatrix = agglomerate(distMatrix);
        //printHierarchy();
        return getClusters();        
    }
    
    //read in datItem struct and convert to distance matrix
    private float[][] getDistMatrix(){
        float[][] distMatrix = new float[data.length][data.length];
        for(int i = 0; i < data.length; i++){
            for(int j = i+1; j < data.length; j++){
                distMatrix[i][j] = distMatrix[j][i] = getEuclideanDist(data[i].getValues(), data[j].getValues());
            }
        }
        return distMatrix;
    }
    
    //calculate euclidean distance between points p and q
    private float getEuclideanDist(float[] p, float[] q){
        float f = 0;
        for(int i = 0; i < p.length; i++)
            f += Math.pow(p[i] - q[i],2);
        return (float)Math.sqrt(f);
    }
    
    //find closest pair of clusters and update distance matrix
    private float[][] agglomerate(float[][] distMatrix){
        float min = Float.MAX_VALUE;
        int a = 0, b = 0;
        for(int i = 0; i < distMatrix.length; i++){
            for(int j = i+1; j < distMatrix.length; j++){
                if(i==j) continue;
                if(distMatrix[i][j] < min) {
                    min = distMatrix[i][j];
                    a = i;
                    b = j;
                }
            }
        }
        return merge(distMatrix, a, b);
    }
    
    //merge clusters and update distance matrix
    private float[][] merge(float[][] distMatrix, int a, int b){
        
        updateHierarchy(a,b);
        //System.out.println("LEVEL "+level);
        //printHierarchy();
        float[][] reduced = new float[distMatrix.length-1][distMatrix.length-1];
        outer:
        for(int i = 0, p = 0; i < distMatrix.length; i++, p++){
            for(int j = i+1, q = p+1; j < distMatrix.length; j++, q++){
                if(i==j) continue;
                if(i==b) {p--; continue outer;}
                if(j==b) {q--; continue;}
                if(i!=a){
                    //System.out.println(a+" "+b+" "+i+" "+j+" "+p+" "+q+" "+reduced.length+" "+distMatrix.length);
                    reduced[p][q] = reduced[q][p] = distMatrix[i][j];

                }else if(i==a){
                    reduced[p][q] = getLink(p,q);

                }
            }
        }
        
        return reduced;
    }
    
    //update hierarchical clustering result (add cluster b to cluster a)
    private void updateHierarchy(int a, int b){

        for(int i = 0; i < hierarchy[level].size(); i++) 
            hierarchy[level+1].add(hierarchy[level].get(i));
        level++;
        cluster add = (cluster) hierarchy[level].get(b);
        cluster c = (cluster)hierarchy[level].get(a);
        for(int j = 0; j < add.getIndices().size(); j++) c.addData(add.getIndices().get(j));
        hierarchy[level].remove(a); 
        hierarchy[level].add(a,c); 
        hierarchy[level].remove(b); 
        
  
    }
    
    //1 = single-, 2 = complete-, 3 = average-linkage
    private float getLink(int a, int j){
        float minDist = Float.MAX_VALUE;
        float maxDist = Float.MIN_VALUE;
        float aveDist = 0;
        cluster c = (cluster) hierarchy[level].get(a);
        cluster d = (cluster) hierarchy[level].get(j);
        
        if(distanceFn < 4){ //if not ward's function
            
            for(int q = 0; q < c.getIndices().size(); q++)
                for(int w = 0; w < d.getIndices().size(); w++){
                    int index1 = Integer.valueOf(c.getIndices().get(q).toString());
                    int index2 = Integer.valueOf(d.getIndices().get(w).toString());
                    float dist = getEuclideanDist(data[index1].getValues(),data[index2].getValues());
                    if(minDist > dist) minDist = dist;
                    if(maxDist < dist) maxDist = dist;
                    aveDist += dist;
                }
        
            aveDist /= (c.getIndices().size()*d.getIndices().size());
        }
        
        return (distanceFn == 1) ? minDist : (distanceFn == 2) ? maxDist : (distanceFn == 3) ? aveDist : getWards(c, d);
    }
    
    
    private float getWards(cluster c, cluster d){
        float cdESS = 0, cESS = 0, dESS = 0;
        float[] cSum = new float[data[0].getValues().length];
        float[] dSum = new float[data[0].getValues().length];
        float[] cdSum =  new float[data[0].getValues().length];
        
        //get sum of all vectors in cluster c
        for(int i = 0; i < c.getIndices().size(); i++){
            int index = Integer.valueOf(c.getIndices().get(i).toString());
            for(int j = 0; j < data[index].getValues().length; j++)
                cSum[j] += data[index].getValues()[j];
        }
        //get sum of all vectors in cluster d
        for(int i = 0; i < d.getIndices().size(); i++){
            int index = Integer.valueOf(d.getIndices().get(i).toString());
            for(int j = 0; j < data[index].getValues().length; j++)
                dSum[j] += data[index].getValues()[j];
        }
        
        //get average vectors
        for(int i = 0; i < cdSum.length; i++) {
            cdSum[i] = cSum[i]+dSum[i];
            cdSum[i]/=(c.getIndices().size()+d.getIndices().size());
            cSum[i]/=c.getIndices().size();
            dSum[i]/=d.getIndices().size();    
            //System.out.println(cdSum[i]+" "+cSum[i]+" "+dSum[i]);
        }
        
         //get ess of all vectors in cluster c
        for(int i = 0; i < c.getIndices().size(); i++){
            int index = Integer.valueOf(c.getIndices().get(i).toString());
            double diff = 0, cdDiff = 0;
            for(int j = 0; j < data[index].getValues().length; j++){
                diff += Math.pow(data[index].getValues()[j]-cSum[j],2);
                cdDiff += Math.pow(data[index].getValues()[j]-cdSum[j],2);
            }
            cESS += diff;
            cdESS += cdDiff;
        }
        
         //get ess of all vectors in cluster d
        for(int i = 0; i < d.getIndices().size(); i++){
            int index = Integer.valueOf(d.getIndices().get(i).toString());
            double diff = 0, cdDiff = 0;
            for(int j = 0; j < data[index].getValues().length; j++){
                diff += Math.pow(data[index].getValues()[j]-dSum[j],2);
                cdDiff += Math.pow(data[index].getValues()[j]-cdSum[j],2);
            }
            dESS += diff;
            cdESS += cdDiff;
        }
        
      //  System.out.println((cdESS - (cESS + dESS))+" "+cdESS+" "+cESS+" "+dESS);
        
        return cdESS - (cESS + dESS);
    }
    
    
    private void printDistMatrix(float[][] distMatrix){
        for(int i = 0; i < distMatrix.length; i++){
            for(int j = 0; j < distMatrix.length; j++){
                System.out.print(distMatrix[i][j]+"\t");
            }
            System.out.println();
        }
    }
    
    private void printHierarchy(){
        for(int i = 0; i < hierarchy[level].size(); i++){
            cluster c = (cluster) hierarchy[level].get(i);
            System.out.println("\nCluster "+(i+1));
            for(int j = 0; j < c.getIndices().size(); j++){
                System.out.println(data[Integer.valueOf(c.getIndices().get(j).toString())].toString());
            }
        }
        System.out.println();
    }
    
    private cluster[] getClusters(){
         cluster[] clusters = new cluster[hierarchy[level].size()];

         for(int i = 0; i < hierarchy[level].size(); i++){
            cluster c = (cluster) hierarchy[level].get(i);
            for(int j = 0; j < c.getIndices().size(); j++){
                c.labels.add(data[Integer.valueOf(c.getIndices().get(j).toString())].toString());
               // c.ids.add(Integer.valueOf(c.getIndices().get(j).toString()));
                c.ids.add(data[Integer.valueOf(c.getIndices().get(j).toString())].getIdentity());
            }
            clusters[i] = c;
        }
        return clusters;
    }
    
}
