package clusterMaker.algorithms.autosome.cluststruct;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.Serializable;
import java.util.*;
/**
 *
 * @author Aaron
 */
  public class cluster implements Comparable, Serializable{
      int size;
      public ArrayList indices = new ArrayList(); //node coordinates
      public ArrayList labels = new ArrayList(); //data labels
      public ArrayList ids = new ArrayList(); //label ids
      public ArrayList nodeIndices = new ArrayList(); //original node indices
      public ArrayList confidence = new ArrayList(); //cluster membership confidence
      public ArrayList order = new ArrayList(); //store data item order
      public int parentID = -1; //primary cluster identifier
      public int childID = -1; //secondary cluster identifier
      public String finalID = new String(); //string version of final cluster identifier
      
      public cluster(){};
      public cluster(ArrayList indices, ArrayList labels, ArrayList ids, ArrayList nodeIndices){
          this.indices = indices;
          this.labels = labels;
          this.ids = ids;
          this.nodeIndices = nodeIndices;
          size = ids.size();
      }
      
      public cluster(ArrayList indices, ArrayList labels, ArrayList ids){
          this.indices = indices;
          this.labels = labels;
          this.ids = ids;
          size = ids.size();
      }
      
      public void setConf(ArrayList confidence) {this.confidence = confidence;}
      
      public void setIndices(ArrayList a) {indices = a;}
      public ArrayList getIndices() {return indices;}
      public void addData(Object o) {indices.add(o);}
      public void setSize() {size = ids.size();}
            
      public int compareTo(Object o){
           double size2 = ((cluster)o).size;
           return (size > size2 ? -1 : (size == size2 ? 0 : 1));
       }

  }
