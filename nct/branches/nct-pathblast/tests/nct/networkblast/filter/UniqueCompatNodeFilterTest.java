
//============================================================================
// 
//  file: UniqueCompatNodeFilterTest.java
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



package nct.networkblast.filter;

import junit.framework.*;
import java.util.*;
import java.util.logging.Level;
import nct.graph.basic.BasicGraph;
import nct.graph.Graph;
import nct.networkblast.NetworkBlast;
import nct.filter.Filter;

public class UniqueCompatNodeFilterTest extends TestCase {
    BasicGraph<String,Double> a,b,c,d;
    Filter<String,Double> f;
    protected void setUp() {
	NetworkBlast.setUpLogging(Level.WARNING);       
	a = new BasicGraph<String,Double>();
	b = new BasicGraph<String,Double>();
	c = new BasicGraph<String,Double>();
	d = new BasicGraph<String,Double>();

    	// expect to get filtered out
	a.addNode("a|A");
	a.addNode("b|B");
	a.addNode("c|A");

	// ok
	b.addNode("a|A");
	b.addNode("b|B");

	// ok
	c.addNode("a|A");
	c.addNode("b|B");

    	// expect to get filtered out
	d.addNode("a|A");
	d.addNode("a|B");

	f = new UniqueCompatNodeFilter();

    }

    public void testRemoveDupes() {
	// tests whether we correctly filter out the two with 0 distance nodes 
	List<Graph<String,Double>> s = new LinkedList<Graph<String,Double>>();
	s.add(a);
	s.add(b);
	s.add(c);
	s.add(d);
	int size = f.filter(s).size();
	assertTrue("expect 2, got " + size, size == 2);
   }
	
    public void testKeepAll() {
	// tests whether we correctly don't filter anything 
	List<Graph<String,Double>> t = new LinkedList<Graph<String,Double>>();
	t.add(b);
	t.add(c);
	int size = f.filter(t).size();
	assertTrue("expect 2, got " + size, size == 2);
    }

    public static Test suite() { return new TestSuite( UniqueCompatNodeFilterTest.class ); }
}
