//CorrelationFinder.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.gui.actions;
//------------------------------------------------------------------------------
import java.util.*;
import java.io.*;

import csplugins.common.vectormath.*;
import csplugins.isb.pshannon.dataMatrix.*;
//------------------------------------------------------------------------------
/**
 * This class finds the correlation between gene expression vectors. Given an
 * ExpressionData object and a vector of expression values, it computes the
 * normalized dot product of the expression vector of every gene in the data set
 * with the provided vector.
 *
 * The target vector can be constructed in three ways; given a single gene, that
 * gene's expression vector forms the target vector; given a set of genes, the
 * average of their normalized expression vectors is the target vector; alternatively,
 * a user-defined vector of sufficient length can be provided as the target vector.
 *
 * The various build methods build and return the correlation table, which can also
 * be accessed later via the get method. The return value is a Map where the keys are
 * Strings, the gene names, and the values are Double objects containing the correlation
 * values. On any error, an empty Map is returned.
 */
//----------------------------------------------------------------------------------------------
public class CorrelationFinder {
    
    //private DataMatrix matrix;
  private DataMatrixLens lens;
  private Map myTable = new HashMap ();
  //private GraphObjAttributes nodeAttributes;
  
//----------------------------------------------------------------------------------------------
public CorrelationFinder() {}
//----------------------------------------------------------------------------------------------
public CorrelationFinder (DataMatrixLens lens)
{
  setDataMatrixLens (lens);
} 
//----------------------------------------------------------------------------------------------
public void setDataMatrixLens (DataMatrixLens lens)
{
  this.lens = lens;
}
//----------------------------------------------------------------------------------------------
//public void setNodeAttributes (GraphObjAttributes node_attributes)
//{
// this.nodeAttributes = node_attributes;
//
//} // setNodeAttributes
//----------------------------------------------------------------------------------------------
/**
  * Find the correlation of all gene expression vectors with the expression vector
  * of the supplied gene
  */
public Map buildCorrelationTable (String targetName) 
{
  myTable.clear ();
  if (lens == null) {return myTable;}
  double [] targetValues = lens.getFromAll (targetName);

  if (targetValues.length == 0) return myTable;
  ReadOnlyMathVector targetVector = MathVectorFactory.makeReadOnlyVector (targetValues);
  return buildCorrelationTable (targetVector);

}
//----------------------------------------------------------------------------------------------
/**
  * Find the correlation of all gene expression vectors with the average of the
  * normalized expression vectors for the supplied list of genes.
  */
public Map buildCorrelationTable (String [] targets) 
{
  myTable.clear ();
  if (lens == null || targets == null || targets.length == 0) {return myTable;}
  // assuming filtered column count is desired - DRT
  int conditionCount = lens.getEnabledColumnCount ();

  double [] initVals = new double [conditionCount];
  MathVector corrVector = MathVectorFactory.makeVector (initVals);
  int validTargetCount = 0;
  for (int i=0; i < targets.length; i++) {
    double [] values = lens.getFromAll (targets [i]);
    ReadOnlyMathVector mathVector = MathVectorFactory.makeReadOnlyVector (values);
    corrVector.add (mathVector.copy().normalize());
    validTargetCount++;
    }
  if (validTargetCount == 0)
    return myTable;
  else {
    corrVector.times (1.0/ (double) validTargetCount);
    return buildCorrelationTable (corrVector);
    }

} // buildCorrelationTable
//----------------------------------------------------------------------------------------------
public Map buildCorrelationTable (ReadOnlyMathVector targetVector)
{
  myTable.clear ();
  int columnCount = lens.getEnabledColumnCount ();
  if (targetVector.size() != columnCount) {return myTable;}

  String [] allNames = lens.getAllRowTitles ();

  for (int i=0; i < allNames.length; i++) {
    double [] values = lens.getFromAll (allNames [i]);
    ReadOnlyMathVector iVector = MathVectorFactory.makeReadOnlyVector (values);
    Double corr = new Double (targetVector.dotNorm (iVector));
    myTable.put (allNames[i], corr);
    } // for i

  return myTable;

}
//----------------------------------------------------------------------------------------------
/**
 * Find the correlation of all gene expression vectors with the supplied vector
 * which should be of the same length (i.e., the number of experimental conditions).
 */
public Map oldBuildCorrelationTable (ReadOnlyMathVector targetVector) 
{
  myTable.clear ();
  // assuming filtered column count is desired here -- DRT
  int columnCount = lens.getEnabledColumnCount ();
  if (targetVector.size() != columnCount) {return myTable;}
  String [] allNames = lens.getAllRowTitles ();
  for (int i=0; i < allNames.length; i++) {
    double [] values = lens.getFromAll (allNames [i]);
    ReadOnlyMathVector iVector = MathVectorFactory.makeReadOnlyVector (values);
    Double corr = new Double (targetVector.dotNorm (iVector));
    myTable.put (allNames[i], corr);
    } // for i

  return myTable;

} // buildCorrelationTable
//----------------------------------------------------------------------------------------------
/**
 * Return the most recently computed table of correlation values as a Map where
 * the gene name is the key and a Double object is the value.
 */
public Map getCorrelationTable () 
{
  return myTable;
}
//----------------------------------------------------------------------------------------------
} // CorrelationFinder
