// BioDataServerSynonymsTest.java:  a junit test, focusing on the
//  retrieval of synonyms and canonicalName information for genes,
//  information which is periodically updated from ????
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
public class BioDataServerSynonymsTest extends TestCase {

  static String serverName;
  RMIBioDataServer server;

//------------------------------------------------------------------------------
public BioDataServerSynonymsTest (String name) 
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
 * YPD contains this mapping (among many others)
 *
 *   ACT1   YFL039C ACT1/END7/ABY1/(SLC1)/YFL039C
 *
 *  the canonical name is YJL005W; all others are synonyms.  test
 *  this mapping here.
 *
**/
public void testGetCanonicalName () throws Exception
{ 
  System.out.println ("testGetCanonicalName");
  String result = server.getCanonicalName ("ACT1");
  assertTrue (result.equals ("YFL039C"));
  assertTrue (server.getCanonicalName ("YFL039C").equals  ("YFL039C"));

} // testGetCanonicalName
//-------------------------------------------------------------------------
/*
 * YPD contains this mapping (among many others)
 *
 *   ACT1   YFL039C ACT1/END7/ABY1/(SLC1)/YFL039C
 *
 *  the canonical name is YJL005W; all others are synonyms.  test
 *  this mapping here.  the answer should be:
 *
 *          END7 ABY1 YFL039C
 *
**/
public void testGetSynonyms () throws Exception
{ 
  String geneName = "ACT1";
  System.out.println ("testGetSynonyms");
  String [] result = server.getSynonyms (geneName);

  assertTrue (result.length == 3);  // both (SLC1) and ACT1 are ignored

} // testGetSynonyms
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  if (args.length != 1) {
    System.out.println ("usage:  BioDataServerSynonymsTest <rmiServer URI>");
    System.out.println ("  i.e., BioDataServerSynonymsTest rmi://localhost/bioData");
    System.exit (1);
    }

  serverName = args [0];
   
  junit.textui.TestRunner.run (new TestSuite (BioDataServerSynonymsTest.class));
}
//------------------------------------------------------------------------------
} // BioDataServerSynonymsTest
