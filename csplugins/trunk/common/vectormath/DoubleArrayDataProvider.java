//DoubleArrayDataProvider.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package csplugins.common.vectormath;
//------------------------------------------------------------------------------
/**
 * Data provider for arrays of doubles.
 */
public class DoubleArrayDataProvider implements VectorDataProvider {
    
    private double[] data;
    
    public DoubleArrayDataProvider(double[] d) {data = d;}
    
    public int size() {return data.length;}
    public double getQuick(int index) {return data[index];}
    public void setQuick(int index, double value) {data[index] = value;}
}

