// DataMatrixReaderFactory.java
//-----------------------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix;
//-------------------------------------------------------------------------------------------
public class DataMatrixReaderFactory {
  static String uri;
  static String protocol;
  static String path;
//-----------------------------------------------------------------------------------------------------
public static DataMatrixReader createReader (String uri) throws IllegalArgumentException
{
  if (uri == null || uri.length () == 0)
    throw new IllegalArgumentException ("DataMatrixReaderFactory.create called with empty uri");

   parseUri (uri);
   if (protocol.equals ("file://") || protocol.equals ("jar://"))
     return new DataMatrixFileReader (protocol, path);
   else
     throw new IllegalArgumentException ("no DataMatrixReader for protocol '" + protocol + "'");
   
} // create
//-----------------------------------------------------------------------------------------------------
static private void parseUri (String uri)
{
   String [] tokens = uri.split ("://");
   if (tokens.length == 2) {
     protocol = tokens [0] + "://";
     path = tokens [1];
     }
   else {
     protocol = "file://";
     path = uri;
     }
    
} // parseUri
//--------------------------------------------------------------------
} // class DataMatrixReaderFactory
