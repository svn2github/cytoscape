/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusterMaker.algorithms.autosome.fileio;


import java.io.*;
import java.util.*;
import clusterMaker.algorithms.autosome.launch.*;
import clusterMaker.algorithms.autosome.view.view2d.*;
import clusterMaker.algorithms.autosome.cluststruct.*;
import java.awt.*;
/**
 *
 * @author Aaron
 */
public class printClusters {
    
   
    
  public void printClusters(String add,  cluster[] clusters, Settings s){

        int singletons = 1;
        int clusterCount = 0;
        for(int i = 0; i < clusters.length; i++) if(clusters[i].ids.size() > 1) clusterCount++;
        
        try{
            if(s.printRowsCols<3) printSummary(add,clusters,s, clusterCount);
            
            DataOutputStream outHTML = (s.printRowsCols<3) ? new DataOutputStream(new BufferedOutputStream(new FileOutputStream(s.outputDirectory+s.getFolderDivider()+"AutoSOME_"+add+".html"))) : null;
            DataOutputStream outTable = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(s.outputDirectory+s.getFolderDivider()+"\\AutoSOME_"+add+".txt")));
            if(s.printRowsCols<3) outHTML.writeBytes("<HTML><HEAD><TITLE>AutoSOME Clusters</TITLE><body bgcolor = black><font face=\"Arial\" color = \"ffffff\"><br><h1><Center>"+clusterCount+" Clusters</Center></h1><br>");
            
            for(int i = 0; i < clusters.length; i++){
                
                if(i==0){
                    if(s.readColumns && s.columnHeaders != null ){
                        outTable.writeBytes("#\t");

                        if(!s.distMatrix){
                            if(s.logNorm) outTable.writeBytes("l\t");
                            if(s.unitVar) outTable.writeBytes("u\t");
                            if(s.scale>0) outTable.writeBytes("s"+s.scale+"\t");
                            if(s.medCenter) outTable.writeBytes("m\t");
                            if(s.medCenterCol) outTable.writeBytes("M\t");
                            if(s.sumSqrRows) outTable.writeBytes("q\t");
                            if(s.sumSqrCol) outTable.writeBytes("Q\t");
                        }
                        if(s.printRowsCols==3){
                            outTable.writeBytes("\n\t\tCLUST\t");
                            for(int k = 0; k < s.columnClusters.length-s.startData; k++) outTable.writeBytes((s.columnClusters[k]+1)+"\t");
                        }
                        outTable.writeBytes("\nCLUST\tCONF\t");
                        for(int k = s.startData-1; k < s.columnHeaders.length; k++){
                             if(s.PCLformat && k==s.startData-1) outTable.writeBytes(s.columnHeaders[1]+"\t");
                             else outTable.writeBytes(s.columnHeaders[k]+"\t");
                        }
                        outTable.writeBytes("\n");
                    }
                }
                
                Random r = new Random();
                int[] clusterCol = new int[3];
                clusterCol[0] = r.nextInt(255);
                clusterCol[1] = r.nextInt(255);
                clusterCol[2] = r.nextInt(255);
                if(clusterCol[0] > 175 && clusterCol[1] > 175 && clusterCol[2] > 175) clusterCol[0] = 75;
               // if(curr.size() == 1) clusterCol[0] = clusterCol[1] = clusterCol[2] = 75;
                Color c = new Color(clusterCol[0],clusterCol[1],clusterCol[2]);
                
                ArrayList curr = clusters[i].ids;
                if(curr.size() == 0) continue;
  
                if(s.printRowsCols<3) outHTML.writeBytes("<A NAME=\""+ i+"\"></A><h3><font face=\"Arial\" color = \"ffffff\">"+((curr.size()>1) ? "Cluster "+(i+1) : "Singleton "+(singletons++))+" "+((curr.size() > 1) ? "("+curr.size()+" Members)" : "")+"</font></h3><br>\n");
                

                
          
                
  
                   
                   for(int q = 0; q < clusters[i].ids.size();  q++){
                        int dataID = Integer.valueOf(clusters[i].ids.get(q).toString());
                         String identity = (s.ensemble_runs == 1) ? new StringTokenizer(s.input[dataID].getIdentity(),",").nextToken() : s.input[dataID].getIdentity();
                         //System.out.println(AA_Colors.getHexRGB(trStrings[index].get(h).toString().charAt(q)));
                            
                            StringBuffer sb = new StringBuffer();
                            
                            int[] rgbConf = new int[3];
                            String confHexString = new String();
                            if(s.confidence && s.ensemble_runs>1){
                                double conf = Double.valueOf(clusters[i].confidence.get(q).toString());
                                int blue = (int)(255*(conf/100));
                                int red = 255 - blue;
                                rgbConf[0] = red;
                                
                                rgbConf[2] = blue;

                                rgbConf[1] = 0;
                                Color cConf = new Color(rgbConf[0], rgbConf[1], rgbConf[2]);
                                String R = Integer.toHexString(cConf.getRed());
                                confHexString = confHexString.concat((R.length() == 1) ? "0"+R : R);
                                String G = Integer.toHexString(cConf.getGreen());
                                confHexString = confHexString.concat((G.length() == 1) ? "0"+G : G);
                                String B = Integer.toHexString(cConf.getBlue());
                                confHexString = confHexString.concat((B.length() == 1) ? "0"+B : B);
                            }
                            
                            
                            for(int k = 0; k < s.input[dataID].getOriginalValues().length; k++) sb.append(","+s.input[dataID].getOriginalValues()[k]);
                            if(s.printRowsCols<3)outHTML.writeBytes(((s.confidence && s.ensemble_runs>1) ? "<b><font face=\"Arial\" color = \""+confHexString+"\">"+Integer.valueOf(clusters[i].confidence.get(q).toString())+"</font><font face=\"Arial\" color = \"666666\">,</font></b>" : "")+
                                    "<b><font face=\"Arial\" color = \"ffffff\">"+identity+"</font>" +
                                    
                                    "<font face=\"Arial\" color = \"666666\">"+sb.toString()+"</font></b><br>");
                            //outTable.writeBytes((i+1)+","+Double.valueOf(clusters[i].confidence.get(q).toString())+","+clusters[i].ids.get(q).toString()+"\n");
                            outTable.writeBytes((i+1)+"\t"+((s.confidence && s.ensemble_runs>1) ? Integer.valueOf(clusters[i].confidence.get(q).toString())+"\t" : "")+s.input[dataID].toDescString()+"\n");
                        }
                        if(s.printRowsCols<3)outHTML.writeBytes("<br>\n");
                
            }
            if(s.printRowsCols<3)outHTML.writeBytes("</body></html>");

            if(s.htmlOut && s.printRowsCols<3) outHTML.close();
            if(s.textOut) outTable.close();
        }catch(IOException err){};
  }
  
  private void printSummary(String add,  cluster[] clusters, Settings s, int clusterCount){
      try{
          DataOutputStream outHTML = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(s.outputDirectory+s.getFolderDivider()+"AutoSOME_"+add+"_Summary.html")));
          
          outHTML.writeBytes("<HTML><HEAD><TITLE>AutoSOME Output Summary</TITLE><body bgcolor = black>" +
                  "<font face=\"Arial\" color = \"ffffff\"><br><h1><Center>AutoSOME Output Summary</Center></h1><br>");
          outHTML.writeBytes("<h2><Center>Input: "+s.getName()+"</Center></h2>" +
                  "<table CELLPADDING=0 CELLSPACING=0 border = 1 align=center>" +
                 
                  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Rows</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+s.inputSize+"</font></td></tr>" +    
                  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Attributes</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+s.input[0].getValues().length+"</font></td></tr>" +    
                   "</table>");
          outHTML.writeBytes("<h2><Center>Settings</Center></h2>" +
                  "<table CELLPADDING=0 CELLSPACING=0 border = 1 align=center>" +
                  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Ensemble Runs</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+s.ensemble_runs+"</font></td></tr>" +    
                  ((s.logNorm) ?  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Normalize Input</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Log2</font></td></tr>" : "")+
                  ((s.unitVar) ?  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Normalize Input</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Unit Variance</font></td></tr>" : "")+
                  ((s.scale>0) ?  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Normalize Input</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Range [0,"+s.scale+"]</font></td></tr>" : "")+
                  ((s.medCenter) ?  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Median Center</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Rows</font></td></tr>" : "")+
                  ((s.medCenterCol) ?  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Median Center</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Columns</font></td></tr>" : "")+
                  ((s.sumSqrRows) ?  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Sum of Squares=1</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Rows</font></td></tr>" : "")+
                  ((s.sumSqrCol) ?  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Sum of Squares=1</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Columns</font></td></tr>" : "")+
                  ((s.distMatrix && s.dmDist==1) ?  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Distance Matrix</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Euclidean</font></td></tr>" : "")+
                  ((s.distMatrix && s.dmDist==2) ?  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Distance Matrix</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Pearson's Corr.</font></td></tr>" : "")+
                  ((s.distMatrix && s.dmDist==3) ?  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Distance Matrix</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Uncentered Corr.</font></td></tr>" : "")+
                  ((s.distMatrix && s.unitVarAfterDM) ?  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Distance Matrix</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Unit Variance</font></td></tr>" : "")+
                  ((!s.doSM && s.noMapping) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Mapping Method</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">SOM</font></td></tr>": "") +
                  ((!s.doSM && !s.noMapping) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>SOM Iterations</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+s.som_iters+"</font></td></tr>" : "") +
                  ((!s.doSM && !s.noMapping) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>SOM Grid Size</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+((s.som_gridSize == 0) ? ((int)Math.min(s.som_maxGrid, Math.max(s.som_minGrid, Math.sqrt(s.inputSize*2)))) : s.som_gridSize)+"</font></td></tr>" : "")+
                  ((!s.doSM && !s.noMapping) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>SOM Topology</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+((s.som_circle) ? "circle" : "square")+"</font></td></tr>" : "")+
                  ((!s.doSM && !s.noMapping) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>SOM Error Exponent</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+s.som_DEScale+"</font></td></tr>": "") +
                  ((!s.doSM && !s.noMapping) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>SOM Distance Metric</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+((s.Pearson) ? "Pearson" : (s.unCentered) ? "Uncentered" : "Euclidean")+"</font></td></tr>": "") +
                  
                  ((s.doSM) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Mapping Method</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Sammon Mapping</font></td></tr>" : "")+
                  ((s.doCart) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Cartogram XY Size</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+s.de_resolution+"</font></td></tr>" : "")+
                  ((s.doKmeans) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Clustering Method</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">K-Means</font></td></tr>" : "")+
                  ((s.doHierarchical && s.hierarchical_choice == 1) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Clustering Method</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Single Aggl.</font></td></tr>": "")+
                  ((s.doHierarchical && s.hierarchical_choice == 2) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Clustering Method</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Complete Aggl.</font></td></tr>": "") +
                  ((s.doHierarchical && s.hierarchical_choice == 3) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Clustering Method</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Average Aggl.</font></td></tr>" : "")+
                  ((s.doHierarchical && s.hierarchical_choice == 4) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Clustering Method</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Ward's Aggl.</font></td></tr>" : "")+
                  ((!s.doKmeans && !s.doHierarchical) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Clustering Method</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">MST</font></td></tr>" : "") +
                  ((!s.doKmeans && !s.doHierarchical) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>MST P-value</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+s.mst_pval+"</font></td></tr>" : "") +
                  ((s.doKmeans || s.doHierarchical) ? "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>User Cluster Number</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+s.known_clusters+"</font></td></tr>" : "")+
                  
                      
                  "<tr align = center><td width = 200><font face=\"Arial\" color = \"ffffff\"><b>Maximum Threads</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+s.threads+"</font></td></tr>" +
                  "</table>");
      
          outHTML.writeBytes("<h2><Center>"+clusterCount+" Clusters</Center></h2>" +
                  "<table CELLPADDING=0 CELLSPACING=0 border = 1 align=center>"+
                  "<tr align = center><td width = 100><font face=\"Arial\" color = \"ffffff\"><b>ID</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Size</font></td>" +
                   ((s.confidence && s.ensemble_runs>1) ? "<td width = 100><font face=\"Arial\" color = \"ffffff\">Mean Confidence</font></td>": "")+
                  "</tr>"); 
 
                  for(int i = 0; i < clusters.length; i++){
                      if(clusters[i].ids.size() < 2) continue;
                      
                      double clusterConf = 0;
                      
                      if(s.confidence && s.ensemble_runs>1){
                        for(int k = 0; k < clusters[i].confidence.size(); k++)
                          clusterConf += Double.valueOf(clusters[i].confidence.get(k).toString());
                        clusterConf /= clusters[i].confidence.size();
                      }
                      
                      outHTML.writeBytes("<tr align = center><td width = 100><font face=\"Arial\" color = \"ffffff\"><b><A HREF=\"AutoSOME_"+add+".html#"+i+"\">cluster "+(i+1)+"</a></b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+clusters[i].ids.size()+"</font></td>" +
                             ((s.confidence && s.ensemble_runs>1) ? "<td width = 100><font face=\"Arial\" color = \"ffffff\">"+(int)clusterConf+"</font></td>" : "")+
                             "</tr>");   
                  }
      
          outHTML.writeBytes("</table>");
          int singleCount = 0;
          for(int i = 0; i < clusters.length; i++){
              if(clusters[i].ids.size() == 1) singleCount++;
          }
          if(singleCount>0){
          outHTML.writeBytes("<h2><Center>"+singleCount+" Singletons</Center></h2>" +
                  "<table CELLPADDING=0 CELLSPACING=0 border = 1 align=center>"+
                  "<tr align = center><td width = 100><font face=\"Arial\" color = \"ffffff\"><b>ID</b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">Size</font></td>" +
                   ((s.confidence && s.ensemble_runs>1) ? "<td width = 100><font face=\"Arial\" color = \"ffffff\">Confidence</font></td>": "")+
                  "</tr>"); 
 
                  for(int i = 0, j = 1; i < clusters.length; i++){
                      if(clusters[i].ids.size() != 1) continue;
                      
                      double clusterConf = 0;
                      
                      if(s.confidence && s.ensemble_runs>1){
                        for(int k = 0; k < clusters[i].confidence.size(); k++)
                          clusterConf += Double.valueOf(clusters[i].confidence.get(k).toString());
                        clusterConf /= clusters[i].confidence.size();
                      }
                      
                      outHTML.writeBytes("<tr align = center><td width = 100><font face=\"Arial\" color = \"ffffff\"><b><A HREF=\"AutoSOME_"+add+".html#"+i+"\">single "+(j++)+"</a></b></font></td><td width = 100><font face=\"Arial\" color = \"ffffff\">"+clusters[i].ids.size()+"</font></td>" +
                             ((s.confidence && s.ensemble_runs>1) ? "<td width = 100><font face=\"Arial\" color = \"ffffff\">"+(int)clusterConf+"</font></td>" : "")+
                             "</tr>");   
                  }
      
          outHTML.writeBytes("</table>");
          }
          outHTML.writeBytes("<br><br><h3><Center>Running Time: "+s.runTime+"</Center></h3>"); 
          outHTML.writeBytes("</HTML>");
          outHTML.close();
          
      }catch(IOException err){};
  }
  

  
  
 
  private class sortTR implements Comparable{
      public String TR;
      public String values;
      public int ID;
      public sortTR(String TR, String values, int ID){
          this.TR = TR;
          this.values = values;
          this.ID = ID;
      }
      public int compareTo(Object o){
           double dist2 = ((sortTR)o).TR.length();
           return (TR.length() < dist2 ? -1 : (TR.length() == dist2 ? 0 : 1));
      }
  }
  
  
}

