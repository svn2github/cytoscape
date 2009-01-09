// PluginInfoTest.java:  a junit test for the class which sets run-time configuration,

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
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

// usually from command line arguments
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.rmi.*;
import java.io.*;
import java.util.*;

import cytoscape.PluginInfo;
//------------------------------------------------------------------------------
public class PluginInfoTest extends TestCase {


//------------------------------------------------------------------------------
public PluginInfoTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void setUp () throws Exception
{
}
//------------------------------------------------------------------------------
public void tearDown () throws Exception
{
}
//------------------------------------------------------------------------------
public void testDefaultCtor () throws Exception
{ 
  System.out.println ("testDefaultCtor");
  PluginInfo pi = new PluginInfo ();

  assertTrue (pi.getAttributeName () == null);
  assertTrue (pi.getFileExtension () == null);
  assertTrue (pi.getClassName () == null);

} // testDefaultCtor
//-------------------------------------------------------------------------
public void testSettersAndGetters () throws Exception
{ 
  System.out.println ("testSettersAndGetters");
  PluginInfo pi = new PluginInfo ();

  String attributeName = "FOO";
  String fileExtension = "foo";
  String className = "cytoscape.plugins.demo.Foo";

  pi.setAttributeName (attributeName);
  pi.setFileExtension (fileExtension);
  pi.setClassName (className);

  assertTrue (pi.getAttributeName().equals (attributeName));
  assertTrue (pi.getFileExtension().equals (fileExtension));
  assertTrue (pi.getClassName().equals (className));

} // testSettersAndGetters
//-------------------------------------------------------------------------
public void testArgCtor () throws Exception
{ 
  System.out.println ("testArgCtor");

  String attributeName = "FOO";
  String fileExtension = "foo";
  String className = "cytoscape.plugins.demo.Foo";
  PluginInfo pi = new PluginInfo (className, fileExtension, attributeName);

  assertTrue (pi.getAttributeName().equals (attributeName));
  assertTrue (pi.getFileExtension().equals (fileExtension));
  assertTrue (pi.getClassName().equals (className));

} // testArgCtor
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (PluginInfoTest.class));
}
//------------------------------------------------------------------------------
} // PluginInfoTest


