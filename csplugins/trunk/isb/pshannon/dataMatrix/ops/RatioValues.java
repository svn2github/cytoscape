//ExpressionRatioValues.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package csplugins.trial.pshannon.dataMatrix.ops;
//------------------------------------------------------------------------------
import java.util.Vector;
import cytoscape.GraphObjAttributes;
import cytoscape.data.mRNAMeasurement;
import csplugins.vectormath.ReadOnlyVectorDataProvider;
import csplugins.graphAlgo.MetaNode;

import csplugins.trial.pshannon.newDataCube.*;
//------------------------------------------------------------------------------
/**
 * This class provides access to the expression ratios for all the conditions
 * in an ExpressionData object to create a vector view of the data.
 */
public class RatioValues implements ReadOnlyVectorDataProvider {
    
  private DataMatrix rawData;
  private String name;
  private GraphObjAttributes nodeAttributes;
  
//------------------------------------------------------------------------------
/**
 * Constructs an object accessing data for the named gene in the supplied
 * ExpressionData object.
 */
public RatioValues (MatrixData data, String name) {
    rawData = data;
    this.name = name;
}
//------------------------------------------------------------------------------
/**
* Constructs an object accessing data for the named gene in the supplied
* ExpressionData Object.&nbsp;This constructor is intended to support the existence
* of <code>MetaNode</code> objects.
*/
public RatioValues (MatrixData data, String name, GraphObjAttributes node_attributes){
    this(data, name);
    this.nodeAttributes = node_attributes;
}
//------------------------------------------------------------------------------
public int size () 
{
  if (rawData == null) {return 0;} 
    
 if (this.isMetaNode()) {
   Double[] metaNodeXprProfile = getMetaNodeXpr();
   return (metaNodeXprProfile == null) ? 0 : metaNodeXprProfile.length;
   } 
 else {//is an ordinary gene
   Vector measurements = rawData.getMeasurements(name);
   return (measurements == null) ? 0 : measurements.size();
   }

} / /size
//------------------------------------------------------------------------------
public double getQuick (int index) 
{
  if (this.isMetaNode()) {
    Double[] metaNodeXprProfile = getMetaNodeXpr();
    return metaNodeXprProfile[index].doubleValue();
    } 
  else { //is an ordinary gene
   Vector measurements = rawData.getMeasurements(name);
   return ((mRNAMeasurement)measurements.get(index)).getRatio();
   }

} // getQuick
//------------------------------------------------------------------------------
/**
 * Returns true if the name is known as a metanode, false otherwise.
 */
public boolean isMetaNode() 
{
 if (nodeAttributes == null) {return false;}
 return MetaNode.isMetaNode(this.name, this.nodeAttributes);

} // isMetaNode
//------------------------------------------------------------------------------
/**
 * Assuming that this is a metanode and returns the expression profile
 * for that metanode, calculating it if necessary.
 */
private Double [] getMetaNodeXpr() 
{
  Double [] metaNodeXprProfile = 
     (Double[])this.nodeAttributes.getValue(MetaNode.MNODE_XPR_PROFILE_ATT,
                                              this.name);
  if (metaNodeXprProfile == null){
    MetaNode metaNode = MetaNode.getMetaNode(this.name, this.nodeAttributes);
    metaNodeXprProfile = metaNode.createAverageXprArray();
    }

  return metaNodeXprProfile;

} // getMetaNodeXpr
//------------------------------------------------------------------------------
} // RatioValues
