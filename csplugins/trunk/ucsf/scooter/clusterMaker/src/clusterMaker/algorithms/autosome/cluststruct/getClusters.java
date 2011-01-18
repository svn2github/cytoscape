package clusterMaker.algorithms.autosome.cluststruct;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.*;
import java.io.*;
import java.awt.*;
import clusterMaker.algorithms.autosome.launch.*;
/**
 *
 * @author Aaron
 */
public class getClusters {

     private ArrayList currClust = new ArrayList();
     private double[][] edges;
     private Point[] nodes;
     ArrayList[] ids;
     private double edgesThresh;
     private double maxDist = 0;
     private cluster[] clusters;
     private ArrayList[] origClusters;
     private boolean noThresh = false;
     private boolean[] validEdge;
     private boolean[] usedEdges;
     private clusterRun cr;
     private Settings s;
     private clusterMaker.algorithms.autosome.view.view2d.pic_Drawer pd;
     
     public getClusters(clusterRun cr, Settings s){
        this.cr = cr;
        
        this.edges = cr.edges; //all MST edges in node network
        this.nodes = cr.nodes; //all nodes
        this.ids = cr.ids;
        this.clusters = cr.c;
        edgesThresh = cr.thresh; //p-value cutoff
        this.usedEdges = cr.usedEdges;
        this.s = s;
     }
     
     public getClusters(clusterRun cr, boolean[] validEdge, Settings s){
        this.validEdge = validEdge;
        noThresh = true;
        this.edges = cr.edges; //all MST edges in node network
        this.nodes = cr.nodes; //all nodes
        this.ids = cr.ids;
        edgesThresh = 0;
        usedEdges = cr.usedEdges;
        this.s = s;
     }

     
     public void findClusters(boolean general){

        boolean[] used = new boolean[nodes.length]; //used edges for DFS
        for(int i = 0;i < used.length; i++) used[i] = false; 
        ArrayList clust = new ArrayList(); 
        ArrayList clustLabels = new ArrayList();
        ArrayList clustIDs = new ArrayList();
        ArrayList allNodes = new ArrayList();
        
        for(int j = 0; j < edges.length; j++){
            if(edges[j][2] > maxDist) maxDist = edges[j][2];
        }

        int max = 0;

            //iterate across all rescaled SOM nodes
            for(int i = 0; i < nodes.length; i++){
                
                //if node i has not been accessed, find all nodes connected to node i
                if(!used[i]) connected(i);
                //if node i has already been added to cluster, continue
                else continue;
                
            
                
                //populate cluster arrays
                ArrayList allIndices = new ArrayList();
                ArrayList allLabels = new ArrayList();
                ArrayList allIDs = new ArrayList();
                ArrayList indices = new ArrayList();
                
                for(int j = 0; j < currClust.size(); j++){
                    int index = Integer.valueOf(currClust.get(j).toString()); //node index
                    if(!general){ //if TR clusters
                          double[] info = new double[nodes[index].getPoint().length];
                          for(int k = 0; k < info.length; k++)
                            info[k] = nodes[index].getPoint()[k];
                            allIndices.add(info);
                    }else{ //If general numerical clusters
                        for(int h = 0; h < ids[index].size(); h++){
                                double[] info = new double[nodes[index].getPoint().length+1];  
                               /* String lab = new StringTokenizer(labels[index].get(h).toString(),",").nextToken();
                                try{
                                    int ID = Integer.parseInt(lab);
                                    info[0] = ID;
                                }catch(NumberFormatException err) {info[0]=1;}   */
                               // indices.add(index);
                                info[0] = index;
                                for(int k = 1; k < info.length; k++)
                                    info[k] = nodes[index].getPoint()[k-1];

                                int id = Integer.valueOf(ids[index].get(h).toString());
                                
                                String[] tokens = s.input[id].getDesc().split(",");
                                
                                if(s.benchmark){
                                
                                    int label = Integer.valueOf(tokens[0]);
                                
                                    if(label > max) max = (int)label; //if clusters are known (benchmarking), how many are there?
                      
                                }
                                
                                allIndices.add(info);
                                //allLabels.add(labels[index].get(h).toString());
                                allIDs.add(ids[index].get(h).toString());
                        }
                    }
                    used[index] = true; //node 'index' is now used                    
                }                    
                allNodes.add(indices);
                clust.add(allIndices);  //add node indices to current cluster  
                clustLabels.add(allLabels); //add labels to current cluster
                clustIDs.add(allIDs);
                
                currClust.clear();
            }

            clusters = new cluster[clust.size()];
            
             for(int i = 0; i < clusters.length; i++){
                clusters[i] = new cluster((ArrayList)clust.get(i), (ArrayList)clustLabels.get(i),(ArrayList)clustIDs.get(i), (ArrayList)allNodes.get(i));
            
                if(s.benchmark){
            
                origClusters = new ArrayList[max]; //if benchmarking, store original clusters

          
                if(general){
                    
                    for(int j = 0; j < clusters[i].labels.size(); j++){
                        String[] tokens = clusters[i].labels.get(j).toString().split(",");
                        int label = Integer.valueOf(tokens[0]);
                        if(origClusters[(int)label-1] == null) origClusters[(int)label-1] = new ArrayList();
                        origClusters[(int)label-1].add((int)label);
                    }
                }
            }
            
            }
            

            currClust.clear();

      
  }
     
     private void connected(int i){
       currClust.add(i);
       DFS(i, -1);
   }
    //depth first search (find all nodes for a given cluster)
   private boolean DFS(int node, int last){
       boolean valid = false;
       for(int i = 0; i < edges.length ; i++){

           if((int) edges[i][0] == node && (int)edges[i][1] == last
                   || (int) edges[i][1] == node && (int)edges[i][0] == last) continue;
           if((int) edges[i][0] == node || (int) edges[i][1] == node){
               if((int) edges[i][0] == node){
                   if(edges[i][2] <= edgesThresh * maxDist || (noThresh && validEdge[i])) {
                       currClust.add((int)edges[i][1]);
                       usedEdges[i] = true;
                       DFS((int)edges[i][1], node);
                   }
               }
               if((int) edges[i][1] == node){
                   if(edges[i][2] <= edgesThresh * maxDist || (noThresh && validEdge[i])) {
                       currClust.add((int)edges[i][0]);
                       usedEdges[i] = true;
                       DFS((int)edges[i][0], node);
                   }
               }
           }
       }
       
       return valid;
   }
   
     
     public void getClusterValidity(boolean prob){
         int max = 0;
         for(int i = 0; i < clusters.length; i++){
            for(int j = 0; j < clusters[i].ids.size(); j++){

                String[] tokens = s.input[Integer.valueOf(clusters[i].ids.get(j).toString())].getIdentity().split(",");
                //System.out.println(i+" "+tokens[0]);
                int benchmark = (!prob) ? Integer.valueOf(tokens[0]) : Integer.valueOf(tokens[1]);
                //System.out.println(benchmark+" "+s.input[Integer.valueOf(clusters[i].ids.get(j).toString())].getIdentity()+" "+i);
                if(benchmark > max) max = benchmark;
            }
        }
         origClusters = new ArrayList[max];
         //System.out.println(max);
         for(int i = 0; i < clusters.length; i++){
            for(int j = 0; j < clusters[i].ids.size(); j++){
                String[] tokens = s.input[Integer.valueOf(clusters[i].ids.get(j).toString())].getIdentity().split(",");
                int benchmark = (!prob) ? Integer.valueOf(tokens[0]) : Integer.valueOf(tokens[1]);
                if(origClusters[benchmark-1] == null) origClusters[benchmark-1] = new ArrayList();
                origClusters[benchmark-1].add(benchmark);
            }
        }
        clusterValidity cv = new clusterValidity(origClusters, clusters, s);
        double[] allF = cv.Fmeasure();
        cr.setMetrics(allF[0], allF[1], allF[2], cv.NMI(), cv.adjRand());
        //System.out.println(cv.Fmeasure()+"\t"+cv.NMI());

     }
     
     
      
  public cluster[] getClust() {return clusters;}
   
     
     
}
