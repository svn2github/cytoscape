// DataMatrixReader.java
//-----------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix;
//-----------------------------------------------------------------------------------
abstract public class DataMatrixReader {
  protected String protocol = "unassigned";
  protected String path;
  abstract public DataMatrix [] get () throws Exception;
  abstract public void read () throws Exception;
//-----------------------------------------------------------------------------------
public DataMatrixReader (String protocol, String path)
{
  this.protocol = protocol;
  this.path = path;
}
//-----------------------------------------------------------------------------------
public String getProtocol () 
{
  return protocol;
}
//-----------------------------------------------------------------------------------
public String getPath () 
{
  return path;
}
//-----------------------------------------------------------------------------------
} // class DataMatrixReader
