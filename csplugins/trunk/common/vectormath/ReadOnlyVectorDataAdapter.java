//ReadOnlyVectorDataAdapter.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package csplugins.common.vectormath;
//------------------------------------------------------------------------------
/**
 * This class provides a default implementation of the ReadOnlyVectorDataProvider
 * interface. The purpose of this class is for constructing anonymous inner classes
 * to implement a ReadOnlyVectorDataProvider; the anonymous class should override
 * all the methods. For example:
 *
 * final List theData = myData;
 * ReadOnlyVectorDataProvider p = new ReadOnlyVectorDataAdapter() {
 *   public int size() {return theData.size();}
 *   public double getQuick(int index) {return ((Double)theData.get(index)).doubleValue();}
 * };
 * ReadOnlyMathVector vector = MathVectorFactory.makeReadOnlyVector(p);
 */
public class ReadOnlyVectorDataAdapter implements ReadOnlyVectorDataProvider {
    
    public ReadOnlyVectorDataAdapter() {}
    
    public int size() {return 0;}
    public double getQuick(int index) {return 0.0;}
}

