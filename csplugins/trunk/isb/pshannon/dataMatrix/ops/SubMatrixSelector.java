//SubMatrixSelector.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package csplugins.trial.pshannon.newDataCube.ops;
//------------------------------------------------------------------------------
import java.util.*;
import java.io.*;

import y.base.*;
import y.view.*;

import cytoscape.GraphObjAttributes;
import csplugins.trial.pshannon.newDataCube.*;
//------------------------------------------------------------------------------
/**
 */
//----------------------------------------------------------------------------------------------
public class SubMatrixSelector {
    
  private DataMatrixLens lens;
  
//----------------------------------------------------------------------------------------------
public SubMatrixSelector() {}
//----------------------------------------------------------------------------------------------
public SubMatrixSelector (DataMatrixLens lens)
{
  setDataMatrixLens (lens);
} 
//----------------------------------------------------------------------------------------------
public void setDataMatrixLens (DataMatrixLens lens)
{
  this.lens = lens;
}
//----------------------------------------------------------------------------------------------
} // SubMatrixSelector
