//VectorDataProvider.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package csplugins.common.vectormath;
//------------------------------------------------------------------------------
/**
 * Interface to sources of modifiable vector data.
 */
public interface VectorDataProvider extends ReadOnlyVectorDataProvider {
    
    int size();
    double getQuick(int index);
    /**
     * Sets the element at the specified index to the specified value without
     * range-checking.
     */
    void setQuick(int index, double value);
}

