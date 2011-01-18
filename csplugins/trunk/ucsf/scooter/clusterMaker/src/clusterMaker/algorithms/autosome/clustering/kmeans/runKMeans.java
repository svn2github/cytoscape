package clusterMaker.algorithms.autosome.clustering.kmeans;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */




import clusterMaker.algorithms.autosome.clustering.kmeans.kMeans;
import clusterMaker.algorithms.autosome.cluststruct.*;
import java.util.*;
import java.io.*;
import clusterMaker.algorithms.autosome.launch.Settings;

/**
 *
 * @author Aaron
 */
public class runKMeans {

    private static boolean rand = true;
    private static double[] aveBM = new double[4];
    private Settings s;

    public static void main(String[] args){
        int trials = 1;
        System.out.println(args[0]+"\n\tF\tP\tR\tNMI");
        for(int i = 0; i < trials; i++) {
            new runKMeans().run(args[0], args, true, 0, new String[1], rand);
        }
        System.out.println("\nave\t"+aveBM[0]/trials+"\t"+aveBM[1]/trials+"\t"+aveBM[2]/trials+"\t"+aveBM[3]/trials);
    }
    
    public cluster[] kdesom(clusterRun cr, int num, boolean mds, Settings s){
   
        ArrayList a = new ArrayList();

        this.s=s;

        int[] nodeKey = new int[cr.getSize()];
        double[][] origNodes = new double[cr.nodes.length][cr.nodes[0].getPoint().length+1];
        String[] labelsKey = new String[cr.getSize()];

        for(int i = 0; i < cr.c.length; i++){
            StringBuffer sb= new StringBuffer();
            for(int j = 0; j < cr.c[i].ids.size(); j++){
                sb.append(Integer.valueOf(cr.c[i].ids.get(j).toString()));
                //if(Integer.valueOf(cr.c[i].ids.get(j).toString())>maxID) maxID = Integer.valueOf(cr.ids[i].get(j).toString());
                double[] data = (double[]) cr.c[i].indices.get(j);
                
                int nodeIndex = (int)data[0];
                
                origNodes[nodeIndex] = data;
                //if no multidimensional scaling
                if(!mds){
                    String[] tokens = cr.c[i].labels.get(j).toString().split(",");
                    data = new double[tokens.length];
                    for(int w = 1; w < tokens.length; w++) data[w] = Double.valueOf(tokens[w]);
                }
                for(int k = 1; k < data.length; k++){
                    sb.append("\t"+data[k]);
                }
                //System.out.println(Integer.valueOf(cr.c[i].ids.get(j).toString())+" "+data[0]);
                
               
                nodeKey[Integer.valueOf(cr.c[i].ids.get(j).toString())] = nodeIndex;
                
                a.add(sb.toString());
                sb = new StringBuffer();
            }
        }
        
        String[] idKey = new String[cr.getSize()];
        for(int i = 0; i < cr.ids.length; i++){
            for(int j = 0; j < cr.ids[i].size(); j++){
                int id = Integer.valueOf(cr.ids[i].get(j).toString());
               /* int classID = Integer.valueOf(new StringTokenizer(cr.labels[i].get(j).toString(),",").nextToken());
                if(classID > maxNum) maxNum = classID;*/
                idKey[id] = String.valueOf(new StringTokenizer(s.input[id].toString(),",").nextToken());
                labelsKey[id] = s.input[id].toString();
            }
        }
        
        String[] input = new String[a.size()];
        for(int i = 0; i < input.length; i++) {
            input[i] = a.get(i).toString();
            //System.out.println(input[i]);
        }

        ArrayList[] clusters = new runKMeans().run(input[0], input, false, num, idKey, rand);
        cluster[] clust = new cluster[clusters.length];
   
        
        for(int i = 0; i < clusters.length; i++){
            ArrayList ids = new ArrayList();
            ArrayList labels = new ArrayList();
            ArrayList indices = new ArrayList();
            ArrayList nodeIndices = new ArrayList();
            
            for(int j = 0; j < clusters[i].size(); j++){
                double[] data = ((double[]) clusters[i].get(j));
                int id = (int)data[0];
                ids.add(id);
                labels.add(labelsKey[id]);
                //System.out.println(i+" "+id+" "+labelsKey[id]);
                double[] ind = new double[origNodes[nodeKey[id]].length];
                for(int p = 1; p < origNodes[nodeKey[id]].length; p++) ind[p] = origNodes[nodeKey[id]][p];//data[p];
                indices.add(ind);      
                nodeIndices.add(nodeKey[id]);
                
            }
            clust[i] = new cluster(indices, labels, ids);
            clust[i].nodeIndices = nodeIndices;
        }

        for(int i = 0; i < cr.usedEdges.length; i++) cr.usedEdges[i] = false;
        return clust;
    }
    
    public ArrayList[] run(String fileInput, String[] input, boolean file, int number, String[] idKey, boolean rand){
      
      ArrayList tmp = new ArrayList();
      
      int Num = 0;
      int columns = 0;
      
      if(file){
      try{
          BufferedReader bf = new BufferedReader(new FileReader(fileInput));
          String line = new String();
          while((line = bf.readLine())!=null){
              if(line.length() != 0){
                  String[] tokens = line.split("\t");
                  columns = tokens.length;
                  double[] pts = new double[tokens.length];
                  for(int i = 0; i < pts.length; i++)
                      if(i==0) {
                          pts[i] = Integer.valueOf(tokens[i]);
                          if(pts[i] > Num) Num = (int)pts[i];
                      }
                      else pts[i] = (tokens[i].equals("?")) ? 0 : Double.valueOf(tokens[i]);
                  tmp.add(pts);
              }
          }
       
      }catch(IOException err){};
      }else{
          for(int j = 0; j < input.length; j++){
              String[] tokens = input[j].split("\t");
                  columns = tokens.length;
                  double[] pts = new double[tokens.length];
                  for(int i = 0; i < pts.length; i++)
                      if(i==0) {
                          pts[i] = Integer.valueOf(tokens[i]);
                      }
                      else pts[i] = (tokens[i].equals("?")) ? 0 : Double.valueOf(tokens[i]);
                  tmp.add(pts);
          }
          Num = number;
      }
      
       double[][] points = new double[tmp.size()][columns];
      
     for(int i = 0; i < tmp.size(); i++) {
          points[i] = (double[]) tmp.get(i);   

      }

      kMeans km = new kMeans();
      
      ArrayList[] clusters = km.run(points, Num, rand);
      
     /* ArrayList[] newClusters = new ArrayList[clusters.length];
      ArrayList[] origClusters = new ArrayList[Num];
      for(int q = 0; q < clusters.length; q++){
          newClusters[q] = new ArrayList();
          for(int j = 0; j < clusters[q].size(); j++){
              double[] dat = (double[]) clusters[q].get(j);
              //System.out.println(idKey.length+" "+dat[0]+" "+idKey[(int)dat[0]]);
              int index = (int)dat[0];//Integer.valueOf(idKey[(int)dat[0]]);
              dat[0] = index;
              newClusters[q].add(dat);
              if(origClusters[index-1]==null) origClusters[index-1] = new ArrayList();
              origClusters[index-1].add(index);
          }
      }
  
      clusterValidity cv = new clusterValidity(origClusters, newClusters, new ArrayList[1]);      
      double[] F = cv.Fmeasure();
      double NMI = cv.NMI();

      aveBM[0] += F[0];
      aveBM[1] += F[1];
      aveBM[2] += F[2];
      aveBM[3] += NMI;
      
      System.out.println("\t"+F[0]+"\t"+F[1]+"\t"+F[2]+"\t"+NMI);*/
      return clusters;
      
      
    }
    
   
}
