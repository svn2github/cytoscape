//ReadOnlyVectorDataProvider.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package csplugins.common.vectormath;
//------------------------------------------------------------------------------
/**
 * Interface to sources of read-only vector data.
 */
public interface ReadOnlyVectorDataProvider {
    
    /**
     * Returns the number of elements
     */
    int size();
    /**
     * Unchecked access to the double value at the specified index.
     */
    double getQuick(int index);
}

