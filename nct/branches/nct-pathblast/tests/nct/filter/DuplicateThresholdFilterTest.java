
//============================================================================
// 
//  file: DuplicateThresholdFilterTest.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================



package nct.filter;

import junit.framework.*;
import java.util.*;
import java.util.logging.Level;
import nct.graph.basic.BasicGraph;
import nct.graph.Graph;
import nct.networkblast.NetworkBlast;

public class DuplicateThresholdFilterTest extends TestCase {
    BasicGraph<String,Double> a,b,c,d;
    Filter<String,Double> f;
    protected void setUp() {
	NetworkBlast.setUpLogging(Level.WARNING);       
	a = new BasicGraph<String,Double>();
	b = new BasicGraph<String,Double>();
	c = new BasicGraph<String,Double>();
	d = new BasicGraph<String,Double>();
    }
    public void testfilter() {
	assertTrue(a.addNode("a"));
	assertTrue(a.addNode("b"));
	assertTrue(a.addNode("c"));
	assertTrue(a.addNode("d"));
	assertTrue(a.addNode("e"));
	assertTrue(a.addNode("f"));
	assertTrue(a.addNode("g"));
	assertTrue(a.addNode("h"));
	assertTrue(a.addNode("i"));
	assertTrue(a.addNode("j"));

	assertTrue(b.addNode("a"));
	assertTrue(b.addNode("b"));
	assertTrue(b.addNode("c"));
	assertTrue(b.addNode("d"));
	assertTrue(b.addNode("e"));
	assertTrue(b.addNode("f"));
	assertTrue(b.addNode("g"));
	assertTrue(b.addNode("h"));
	assertTrue(b.addNode("i"));
	assertTrue(b.addNode("j"));

	assertTrue(c.addNode("b"));
	assertTrue(c.addNode("c"));
	assertTrue(c.addNode("d"));
	assertTrue(c.addNode("e"));
	assertTrue(c.addNode("f"));
	assertTrue(c.addNode("g"));
	assertTrue(c.addNode("h"));
	assertTrue(c.addNode("i"));
	assertTrue(c.addNode("j"));

	assertTrue(d.addNode("a"));
	assertTrue(d.addNode("b"));
	assertTrue(d.addNode("c"));
	assertTrue(d.addNode("d"));
	assertTrue(d.addNode("e"));
	assertTrue(d.addNode("f"));
	assertTrue(d.addNode("g"));
	assertTrue(d.addNode("h"));

	f = new DuplicateThresholdFilter<String,Double>(.9);
	List<Graph<String,Double>> s = new LinkedList<Graph<String,Double>>();
	s.add(a);
	s.add(b);
	s.add(c);
	s.add(d);
	assertEquals(2, f.filter(s).size());
    }
    public static Test suite() { return new TestSuite( DuplicateThresholdFilterTest.class ); }
}
