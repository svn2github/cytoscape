// BioDataServerMolFunctionsTest.java:  a junit test, focusing on the
//  retrieval of biological process information, obtained periodically from the 
//  gene ontology consortium, and digested into our RMI server.
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers.unitTests;
//-----------------------------------------------------------------------------------
import junit.framework.*;
import java.rmi.*;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import cytoscape.data.servers.*;
//------------------------------------------------------------------------------
public class BioDataServerMolFunctionsTest extends TestCase {

  static String serverName;
  RMIBioDataServer server;

//------------------------------------------------------------------------------
public BioDataServerMolFunctionsTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void setUp () throws Exception
{
  server = (RMIBioDataServer) Naming.lookup (serverName);
}
//------------------------------------------------------------------------------
public void tearDown () throws Exception
{
}
//------------------------------------------------------------------------------
/*
 * connection to the RMI server is established in setUp ();
 * make sure it works in separately invoked test methods
 *
**/
public void testConnectionToServer () throws Exception
{ 
  System.out.println ("testConnectionToServer");
  assertTrue (server != null);

} // testConnectionToServer
//-------------------------------------------------------------------------
/*
 * check a few well-known GO terms
 *
**/
public void testGetBioProcessName () throws Exception
{ 
  System.out.println ("testGetMolecularFunctionName");

  assertTrue (server.getMolecularFunctionName (34).equalsIgnoreCase (
                       "adenine deaminase"));

  assertTrue (server.getMolecularFunctionName (3994).equalsIgnoreCase (
                       "aconitate hydratase"));

  assertTrue (server.getMolecularFunctionName (5554).equalsIgnoreCase (
                       "molecular_function unknown"));

} // testGetMolecularFunctionName
//-------------------------------------------------------------------------
/*
 * check a few well-known GO terms
 *
**/
public void testGetMolecularFunctionIds () throws Exception
{ 
  System.out.println ("testGetMolecularFunctionIDs");

  //String geneName = "ACH1"; // YBL015W
  String geneName = "GAL1";   // YBR020W   (one go term, but two paths)
  //String geneName = "ACT1";   // YFL039C

  int [] ids = server.getMolecularFunctionIDs (geneName);

  System.out.println ("===================== " + geneName);
  for (int i=0; i < ids.length; i++) {
    Vector idPaths = server.getAllMolecularFunctionPaths (ids [i]);
    for (int v=0; v < idPaths.size (); v++) {
      Vector idPath = (Vector) idPaths.elementAt (v);
      System.out.println (idPath);
      for (int h=0; h < idPath.size (); h++) {
        int parentId = ((Integer) idPath.elementAt (h)).intValue ();
        String parentName = server.getMolecularFunctionName (parentId);
        System.out.print (parentId);
        System.out.print (": ");
        System.out.println (parentName);
        } // for h
      } // for v
    } // for i





} // testGetMolecularFunctionIDs
//-------------------------------------------------------------------------

public static void main (String[] args) 
{
  if (args.length != 1) {
    System.out.println ("usage:  BioDataServerMolFunctionsTest <rmiServer URI>");
    System.out.println ("  i.e., BioDataServerMolFunctionsTest rmi://localhost/bioData");
    System.exit (1);
    }

  serverName = args [0];
   
  junit.textui.TestRunner.run (new TestSuite (BioDataServerMolFunctionsTest.class));
}
//------------------------------------------------------------------------------
} // BioDataServerMolFunctionsTest
