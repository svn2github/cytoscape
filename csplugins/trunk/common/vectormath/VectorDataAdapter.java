//VectorDataAdapter.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package csplugins.common.vectormath;
//------------------------------------------------------------------------------
/**
 * This class provides a default implementation of the VectorDataProvider interface.
 * The purpose of this class is for constructing anonymous inner classes to implement
 * a VectorDataProvider; the anonymous class should override all three methods.
 * See ReadOnlyVectorDataAdapter for an example.
 */
public class VectorDataAdapter extends ReadOnlyVectorDataAdapter implements VectorDataProvider {
    
    public VectorDataAdapter() {super();}
    
    public void setQuick(int index, double d) {}
}

