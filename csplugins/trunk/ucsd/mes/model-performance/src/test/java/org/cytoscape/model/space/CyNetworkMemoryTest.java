
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

package org.cytoscape.model.space;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import org.cytoscape.model.*;
import org.cytoscape.*;

import java.util.Random;

import java.text.DecimalFormat;

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
		net = DupCyNetworkFactory.getInstance(); 
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

		long heapAlloc = 11000000; // in bytes
		long nonHeapAlloc = 15000; // in bytes
		String heapString = getPerAlloc(heapRes,heapAlloc);
		String nonHeapString = getPerAlloc(nonHeapRes,nonHeapAlloc);
		System.out.println("node heap consumption: " + heapString);
		System.out.println("node non-heap consumption: " + nonHeapString);
		
		assertTrue("node heap consumption: " + heapString, heapRes < heapAlloc); 
		assertTrue("node non-heap consumption: " + heapString, nonHeapRes < nonHeapAlloc); 
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

		long heapAlloc = 25000000; // in bytes
		long nonHeapAlloc = 40000; // in bytes
		String heapString = getPerAlloc(heapRes,heapAlloc);
		String nonHeapString = getPerAlloc(nonHeapRes,nonHeapAlloc);
		System.out.println("node and edge heap consumption: " + heapString);
		System.out.println("node and edge non-heap consumption: " + nonHeapString);
		
		assertTrue("node and edge heap consumption: " + heapString, heapRes < heapAlloc); 
		assertTrue("node and edge non-heap consumption: " + heapString, nonHeapRes < nonHeapAlloc); 
	}



	private static final DecimalFormat df = new DecimalFormat("#,###.00");
	static String getPerAlloc(long res, long alloc) {
	   double per = 100.0*(double)(res-alloc)/(double)alloc;
	   StringBuilder sb = new StringBuilder();
	   sb.append(df.format(per));
	   sb.append("% allocation or ");
	   sb.append(Long.toString(res-alloc));
	   sb.append(" difference between ");
	   sb.append(Long.toString(alloc));
	   sb.append(" (alloc) and ");
	   sb.append(Long.toString(res));
	   sb.append(" (res)");
	   return  sb.toString();
	}

	// TODO
	// Create a test case to see what the consequences of removing nodes + edges are 
}
