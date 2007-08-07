// BioDataServerFactoryTest.java:  

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


