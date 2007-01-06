
/*
  File: InteractionTest.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

// InteractionTest.java


//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.rmi.*;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import cytoscape.data.Interaction;
import cytoscape.AllTests;
//------------------------------------------------------------------------------
public class InteractionTest extends TestCase {

//------------------------------------------------------------------------------
public InteractionTest (String name) 
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
public void test3ArgCtor () throws Exception
{ 
  AllTests.standardOut ("test3ArgCtor");

  String source = "YNL312W";
  String type = "pd";
  String target = "YPL111W";

  Interaction inter0 = new Interaction (source, target, type);
  assertTrue (inter0.getSource().equals (source));
  assertTrue (inter0.getType().equals (type));
  assertTrue (inter0.numberOfTargets () == 1);
  assertTrue (inter0.getTargets()[0].equals (target));


} // test3ArgCtor
//-------------------------------------------------------------------------
public void test1ArgCtor () throws Exception
{ 
  AllTests.standardOut ("test1ArgCtor");

  String rawText0 = "YNL312W pp YPL111W";
  Interaction inter0 = new Interaction (rawText0);
  assertTrue (inter0.getSource().equals ("YNL312W"));
  assertTrue (inter0.getType().equals ("pp"));
  assertTrue (inter0.numberOfTargets () == 1);
  assertTrue (inter0.getTargets()[0].equals ("YPL111W"));

  String rawText1 = "YPL075W pd YDR050C YGR254W YHR174W";
  Interaction inter1 = new Interaction (rawText1);
  assertTrue (inter1.getSource().equals ("YPL075W"));
  assertTrue (inter1.getType().equals ("pd"));
  assertTrue (inter1.numberOfTargets () == 3);
  assertTrue (inter1.getTargets()[0].equals ("YDR050C"));
  assertTrue (inter1.getTargets()[1].equals ("YGR254W"));
  assertTrue (inter1.getTargets()[2].equals ("YHR174W"));

} // test1ArgCtor
//-------------------------------------------------------------------------
public void test1ArgCtorOnDegenerateFrom () throws Exception
// a degenerate form has -only- a source node:  no interaction type
// and no target node
{ 
  AllTests.standardOut ("test1ArgCtorOnDegenerateForm");

  String rawText0 = "YNL312W";
  Interaction inter0 = new Interaction (rawText0);
  assertTrue (inter0.getSource().equals ("YNL312W"));
  assertTrue (inter0.getType() == null);
  assertTrue (inter0.numberOfTargets () == 0);

} // test1ArgCtorOnDegenerateForm
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (InteractionTest.class));
}
//------------------------------------------------------------------------------
} // InteractionTest
