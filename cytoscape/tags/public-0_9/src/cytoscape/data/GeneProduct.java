// GeneProduct.java
//-----------------------------------------------------------------------------
// $Revision$  
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.data;
//-----------------------------------------------------------------------------
import java.io.*;
//-----------------------------------------------------------------------------
public class GeneProduct implements Serializable {
  String symbol;
  String molecularFunction;
  String cellularComponent;
  String biologicalProcess;
//-----------------------------------------------------------------------------
public GeneProduct (String symbol, String molecularFunction, 
                    String cellularComponent, String biologicalProcess)
{

  this.symbol = symbol;
  this.molecularFunction = molecularFunction;
  this.cellularComponent = cellularComponent;
  this.biologicalProcess = biologicalProcess;

}
//-----------------------------------------------------------------------------
public String getSymbol ()
{
  return symbol;
}
//-----------------------------------------------------------------------------
public String getMolecularFunction ()
{
  return molecularFunction;
}
//-----------------------------------------------------------------------------
public String getCellularComponent ()
{
  return cellularComponent;
}
//-----------------------------------------------------------------------------
public String getBiologicalProcess ()
{
  return biologicalProcess;
}
//-----------------------------------------------------------------------------
} // GeneProduct
