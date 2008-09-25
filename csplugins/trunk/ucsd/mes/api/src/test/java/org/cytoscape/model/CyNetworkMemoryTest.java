
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.model;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.cytoscape.model.internal.CyNetworkImpl;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import java.util.Random;

public class CyNetworkMemoryTest extends TestCase {
	private CyNetwork net;
	private int totalNodes = 100000;
	private int totalEdges = 200000;


    /**
     * Creates a new TimedAddNodeTest object.
     *
     * @param name  DOCUMENT ME!
     */
	public CyNetworkMemoryTest(String name) {
		super(name);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void setUp() {
		net = new CyNetworkImpl(new DummyCyEventHelper());
	}
	public void tearDown() {
		net = null;
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testAddNodes() {

        final MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = mbean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = mbean.getNonHeapMemoryUsage();

        long heapStart = heapUsage.getUsed();
        long nonHeapStart = nonHeapUsage.getUsed();

		for (int i = 0; i < totalNodes; i++)
			net.addNode();

		assertEquals(net.getNodeCount(), totalNodes);

        heapUsage = mbean.getHeapMemoryUsage();
        nonHeapUsage = mbean.getNonHeapMemoryUsage();

        long heapEnd = heapUsage.getUsed();
        long nonHeapEnd = nonHeapUsage.getUsed();

		long heapRes = (heapEnd - heapStart);
		long nonHeapRes = (nonHeapEnd - nonHeapStart); 

		assertTrue("node heap consumption: " + heapRes, heapRes < 11000000); // in bytes
		assertTrue("node non-heap consumption: " + nonHeapRes, nonHeapRes < 15000); // in bytes
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testAddNodesEdges() {

        final MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = mbean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = mbean.getNonHeapMemoryUsage();

        long heapStart = heapUsage.getUsed();
        long nonHeapStart = nonHeapUsage.getUsed();

		for (int i = 0; i < totalNodes; i++) 
			net.addNode();

		assertEquals(net.getNodeCount(), totalNodes);

		Random rand = new Random(totalNodes); // arbitrary constant seed

		for (int i = 0; i < totalEdges; i++) {
			int n1x = Math.abs(rand.nextInt() % (totalNodes-1));
			CyNode n1 = net.getNode( n1x );
			int n2x = Math.abs(rand.nextInt() % (totalNodes-1));
			CyNode n2 = net.getNode( n2x );
			net.addEdge( n1, n2, true ); 
		}

		assertEquals(net.getEdgeCount(), totalEdges);

        heapUsage = mbean.getHeapMemoryUsage();
        nonHeapUsage = mbean.getNonHeapMemoryUsage();

        long heapEnd = heapUsage.getUsed();
        long nonHeapEnd = nonHeapUsage.getUsed();

		long heapRes = (heapEnd - heapStart);
		long nonHeapRes = (nonHeapEnd - nonHeapStart); 

		assertTrue("node and edge heap consumption: " + heapRes, heapRes < 25000000); // in bytes
		assertTrue("node and edge non-heap consumption: " + nonHeapRes, nonHeapRes < 40000); // in bytes
	}

	// TODO
	// Create a test case to see what the consequences of removing nodes + edges are 
}
