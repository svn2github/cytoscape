/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusterMaker.algorithms.autosome.cluststruct;

import java.io.Serializable;
/**
 * structure for data item : store all data values, data identifier string
 * 
 * @author Aaron
 */
public class dataItem implements Serializable{
    private float[] values; //store current data values (normalized for clustering or for display)
    private float[] originalValues; //store original data values
    private float[] normValues; //temporarily store values originally normalized for clustering
    private byte[] identifier; //data item label
    private Point pnt; //point in euclidean space after mapping is done
    private int pointIndex; //maps to point array
    private int extraID; //another id for data item (in addition to identifier)
    private byte[] desc; //set description for data item
    private short confidence = -1; //set confidence of cluster membership
    private short clusterID = 0;

    
    public dataItem(float[] v, String i){
        values = v;
        identifier = i.getBytes();
        originalValues = new float[v.length];
        for(int j = 0; j < v.length; j++) originalValues[j] = v[j];
    }
    
    public float[] getValues() {return values;}
    public void setValue(int i, float f) {values[i] = f;}
    public void setIdentity(String identity) {identifier = identity.getBytes();}
    public String getIdentity() {return new String(identifier);}
    public void setPoint(Point pnt) {this.pnt = pnt;}
    public Point getPoint() {return pnt;}
    public void setPointIndex(int index) {this.pointIndex = index;}
    public int getPointIndex() {return pointIndex;}
    public void setOriginalValue(int i, float f) {originalValues[i] = f;}
    public float[] getOriginalValues() {return originalValues;}
    public float[] getNormValues() {return normValues;}
    public void setnormValue(int i, float f) {normValues[i] = f;}
    public void initNormValue(int size) {normValues = new float[size];}
    public void setExtraID(int id) {extraID = id;}
    public int getExtraID() {return extraID;}
    public void setDesc(String desc) {this.desc = desc.getBytes();}
    public String getDesc(){return (desc==null) ? "" : new String(desc);}
    public void setConf(int conf) {this.confidence = (short)conf;}
    public int getConf() {return confidence;}
    public void setClustID(int id) {this.clusterID = (short)id;}
    public int getClustID() {return clusterID;}
    
    //prints out entire data point (with normalized input) excluding description as comma delimited string
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append(new String(identifier)+",");
        for(int i = 0; i < values.length; i++) sb.append(values[i]+",");
        return sb.substring(0,sb.length()-1);
    }
    
    //prints out entire data point (with original input) including description as comma delimited string
    public String toDescString(){
        StringBuffer sb = new StringBuffer();
        sb.append(/*((desc==null) ? "" : desc+"\t")+*/new String(identifier)+"\t");
        for(int i = 0; i < originalValues.length; i++) sb.append(originalValues[i]+"\t");
        return sb.substring(0,sb.length()-1);
    }

    //prints out entire data point (with normalized input) including description as comma delimited string
    public String toDescNormString(){
        StringBuffer sb = new StringBuffer();
        sb.append(new String(identifier)+"\t");
        for(int i = 0; i < originalValues.length; i++) sb.append(values[i]+"\t");
        return sb.substring(0,sb.length()-1);
    }
}
