// BioDataServerCellularComponentsTest.java:  a junit test, focusing on the

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

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
public class BioDataServerCellularComponentsTest extends TestCase {

  static String serverName;
  RMIBioDataServer server;

//------------------------------------------------------------------------------
public BioDataServerCellularComponentsTest (String name) 
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
  System.out.println ("testGetCellularComponentName");

  assertTrue (server.getCellularComponentName (5628).equalsIgnoreCase (
                       "prospore membrane"));

  assertTrue (server.getCellularComponentName (5619).equalsIgnoreCase (
                       "spore wall (sensu Fungi)"));

  assertTrue (server.getCellularComponentName (5857).equalsIgnoreCase (
                       "actin cortical patch (sensu Saccharomyces)"));

} // testGetCellularComponentName
//-------------------------------------------------------------------------
/*
 * check a few well-known GO terms
 *
**/
public void testGetCellularComponentIds () throws Exception
{ 
  System.out.println ("testGetCellularComponentIDs");

  //String geneName = "ACH1"; // YBL015W
  String geneName = "GAL1";   // YBR020W   (one go term, but two paths)
  //String geneName = "ACT1";   // YFL039C

  int [] ids = server.getCellularComponentIDs (geneName);

  System.out.println ("===================== " + geneName);
  for (int i=0; i < ids.length; i++) {
    Vector idPaths = server.getAllCellularComponentPaths (ids [i]);
    for (int v=0; v < idPaths.size (); v++) {
      Vector idPath = (Vector) idPaths.elementAt (v);
      System.out.println (idPath);
      for (int h=0; h < idPath.size (); h++) {
        int parentId = ((Integer) idPath.elementAt (h)).intValue ();
        String parentName = server.getCellularComponentName (parentId);
        System.out.print (parentId);
        System.out.print (": ");
        System.out.println (parentName);
        } // for h
      } // for v
    } // for i


} // testGetCellularComponentIDs
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  if (args.length != 1) {
    System.out.println (
         "usage:  BioDataServerCellularComponentsTest <rmiServer URI>");
    System.out.println (
         "  i.e., BioDataServerCellularComponentsTest rmi://localhost/biodata");
    System.exit (1);
    }

  serverName = args [0];
   
  junit.textui.TestRunner.run (
         new TestSuite (BioDataServerCellularComponentsTest.class));

} // main
//------------------------------------------------------------------------------
} // BioDataServerCellularComponentsTest


