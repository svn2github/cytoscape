//FloatArrayDataProvider.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package csplugins.common.vectormath;
//------------------------------------------------------------------------------
/**
 * Data provider for arrays of floats.
 */
public class FloatArrayDataProvider implements VectorDataProvider {
    
    private float[] data;
    
    public FloatArrayDataProvider(float[] f) {data = f;}
    
    public int size() {return data.length;}
    public double getQuick(int index) {return data[index];}
    public void setQuick(int index, double value) {data[index] = (float)value;}
}

