
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



package nct.filter;

import junit.framework.*;
import java.util.*;
import java.util.logging.Level;
import nct.graph.BasicGraph;
import nct.graph.Graph;
import nct.networkblast.NetworkBlast;
import nct.networkblast.CompatibilityNode;
import nct.filter.Filter;

public class UniqueCompatNodeFilterTest extends TestCase {
    BasicGraph<CompatibilityNode<String,Double>,Double> a,b,c,d;
	Filter<CompatibilityNode<String,Double>,Double> f;
    protected void setUp() {
	NetworkBlast.setUpLogging(Level.WARNING);       

	// "compat" graphs that we'll be testing
	a = new BasicGraph<CompatibilityNode<String,Double>,Double>();
	b = new BasicGraph<CompatibilityNode<String,Double>,Double>();
	c = new BasicGraph<CompatibilityNode<String,Double>,Double>();
	d = new BasicGraph<CompatibilityNode<String,Double>,Double>();

	// dummy partions of the homology graph used to create compat nodes
	Graph<String,Double> lc = new BasicGraph<String,Double>();
	lc.addNode("a");
	lc.addNode("b");
	lc.addNode("c");
	Graph<String,Double> uc = new BasicGraph<String,Double>();
	uc.addNode("A");
	uc.addNode("B");

	// create compat nodes with dummy partitions
	CompatibilityNode<String,Double> node_aA = new CompatibilityNode<String,Double>();
	node_aA.add( lc, "a");
	node_aA.add( uc, "A");

	CompatibilityNode<String,Double> node_bB = new CompatibilityNode<String,Double>();
	node_bB.add( lc, "b");
	node_bB.add( uc, "B");

	CompatibilityNode<String,Double> node_cA = new CompatibilityNode<String,Double>();
	node_cA.add( lc, "c");
	node_cA.add( uc, "A");

	CompatibilityNode<String,Double> node_aB = new CompatibilityNode<String,Double>();
	node_aB.add( lc, "a");
	node_aB.add( uc, "B");

	// now create the "compat" graphs we'll use for the tests

   	// expect to get filtered out
	a.addNode( node_aA );
	a.addNode( node_bB );
	a.addNode( node_cA );

	// ok
	b.addNode( node_aA );
	b.addNode( node_bB );

	// ok
	c.addNode( node_aA );
	c.addNode( node_bB );

    // expect to get filtered out
	d.addNode( node_aA );
	d.addNode( node_aB );

	f = new UniqueCompatNodeFilter();

    }

    public void testRemoveDupes() {
	// tests whether we correctly filter out the two with 0 distance nodes 
	List<Graph<CompatibilityNode<String,Double>,Double>> s = new LinkedList<Graph<CompatibilityNode<String,Double>,Double>>();
	s.add(a);
	s.add(b);
	s.add(c);
	s.add(d);
	int size = f.filter(s).size();
	assertEquals(2, size);
   }
	
    public void testKeepAll() {
	// tests whether we correctly don't filter anything 
	List<Graph<CompatibilityNode<String,Double>,Double>> t = new LinkedList<Graph<CompatibilityNode<String,Double>,Double>>();
	t.add(b);
	t.add(c);
	int size = f.filter(t).size();
	assertEquals(2, size);
    }

    public static Test suite() { return new TestSuite( UniqueCompatNodeFilterTest.class ); }
}
