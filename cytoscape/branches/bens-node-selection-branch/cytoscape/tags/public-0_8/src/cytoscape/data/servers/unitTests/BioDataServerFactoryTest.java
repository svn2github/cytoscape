// BioDataServerFactoryTest.java:  
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers.unitTests;
//-----------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import cytoscape.data.servers.*;
//------------------------------------------------------------------------------
public class BioDataServerFactoryTest extends TestCase {
  static String dataDirectory;
  static String rmiServerUri;
//------------------------------------------------------------------------------
public BioDataServerFactoryTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void testUseDirectory () throws Exception
{ 
  System.out.println ("testUseDirectory");
  BioDataServer server = BioDataServerFactory.create (dataDirectory);
  assertTrue (server.getServerType().equals ("in-process biodata server"));

} // testUseDirectory
//-------------------------------------------------------------------------
public void testUseRMI () throws Exception
{ 
  System.out.println ("testUseRmi");
  BioDataServer server = BioDataServerFactory.create (rmiServerUri);
  assertTrue (server.getServerType().equals ("rmi biodata server"));

} // testUsrRmi
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  if (args.length != 2) {
    System.out.println ("usage:  <biodata directory> <biodata rmi uri>");
    System.exit (0);
    }

  dataDirectory = args [0];
  rmiServerUri = args [1];
   
  junit.textui.TestRunner.run (new TestSuite (BioDataServerFactoryTest.class));

} // main
//------------------------------------------------------------------------------
} // BioDataServerFactoryTest
