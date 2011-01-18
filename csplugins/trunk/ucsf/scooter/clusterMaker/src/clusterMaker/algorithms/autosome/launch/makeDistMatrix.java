/*
 * tranform input into distance matrix by comparing all columns with euclidean distance
 */

package clusterMaker.algorithms.autosome.launch;

import clusterMaker.algorithms.autosome.cluststruct.*;
/**
 *
 * @author Aaron
 */
public class makeDistMatrix {
    
    public dataItem[] getDistMatrix(Settings s){
        
        float[][] dist = new float[s.input[0].getValues().length][s.input[0].getValues().length];

        if(s.EWEIGHT!=null){
            for(int i = 0; i < s.input[0].getValues().length; i++){
                if(s.EWEIGHT[i]==1) continue;
                for(int j = 0; j < s.input.length; j++){
                    s.input[j].getValues()[i] *= s.EWEIGHT[i];
                }
            }
        }

        s.setCenter();
        
        for(int i = 0; i < dist.length; i++){
            for(int j = i; j < dist.length; j++){
                //if(i==j) {dist[i][j] = 0; continue;}
                dist[i][j] = dist[j][i] = (s.dmDist == 1) ? getEucDist(s.input,i,j)
                                        : (s.dmDist == 2) ? getPearsonDist(s.input, i, j, s)
                                        : getUncenteredDist(s.input,i,j);
            }
        }
        
        s.input = new dataItem[dist.length];
        
        for(int i = 0; i < s.input.length; i++){
            String label = String.valueOf(i+1);
            if(s.columnHeaders!=null) if(s.columnHeaders[i+s.startData] != null) label = s.columnHeaders[i+s.startData].replace(" ","_");
            s.input[i] = new dataItem(dist[i], label);
        }
        
        return s.input;
    }
    
    //retrieve euclidean distance between data points i and j
    private float getEucDist(dataItem[] d, int i, int j){
        float f = 0;
        
        //euclidean distance
        for(int k = 0; k < d.length; k++){
            if(d[k].getValues()[i] == -999999999f || d[k].getValues()[j] == -999999999f) continue;
            f += Math.pow(d[k].getValues()[i] - d[k].getValues()[j],2);
        }
        
        return (float)Math.sqrt(f);
    }
    
    //get pearson correlation between data points i and j
    private float getPearsonDist(dataItem[] d, int i, int j, Settings s){
        float dist = 0;
        float distSqr1 = 0;
        float distSqr2 = 0;
        
        for(int k = 0; k < d.length; k++){
            if(d[k].getValues()[i] == -999999999f || d[k].getValues()[j] == -999999999f) continue;
            dist += (d[k].getValues()[i] - s.center[i])*(d[k].getValues()[j] - s.center[j]);
            distSqr1 += Math.pow(d[k].getValues()[i] - s.center[i],2);
            distSqr2 += Math.pow(d[k].getValues()[j] - s.center[j],2);
        }
        
        
        return dist / (float)Math.sqrt(distSqr1 * distSqr2);
    }
    
    //get pearson correlation between data points i and j
    private float getUncenteredDist(dataItem[] d, int i, int j){
        float dist = 0;
        float distSqr1 = 0;
        float distSqr2 = 0;
        
        for(int k = 0; k < d.length; k++){
            if(d[k].getValues()[i] == -999999999f || d[k].getValues()[j] == -999999999f) continue;
            dist += (d[k].getValues()[i])*(d[k].getValues()[j]);
            distSqr1 += Math.pow(d[k].getValues()[i],2);
            distSqr2 += Math.pow(d[k].getValues()[j],2);
        }
        
        
        return dist / (float)Math.sqrt(distSqr1 * distSqr2);
    }
    
    //store mean of every row of input and return
    private float[] getMean(dataItem[] input){
        float[] mean = new float[input.length];
        for(int i = 0; i < input.length; i++){
            for(int j = 0; j < input[i].getValues().length; j++){
                mean[i] += input[i].getValues()[j];
            }
            mean[i] /= input[i].getValues().length;
        }
        return mean;
    }
}
