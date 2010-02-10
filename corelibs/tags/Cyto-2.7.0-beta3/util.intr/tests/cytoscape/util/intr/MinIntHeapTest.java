
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.util.intr;

import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.MinIntHeap;

import junit.framework.*;


/**
 *
 */
public class MinIntHeapTest extends TestCase {
	MinIntHeap heap;
	int[] arr;

	/**
	 *  DOCUMENT ME!
	 */
	public void setUp() {
		heap = new MinIntHeap();
		System.out.println("Instantiated new MinIntHeap.");

		arr = new int[] { 5, 8, 1, 3, 8, 3, 0, 9, 1, 2, 7, 3 };
		System.out.println("Defined my int[] to be: { 5, 8, 1, 3, 8, 3, 0, 9, 1, 2, 7, 3 }.");
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testToss() {
		int last = -1;
		int size = 0;

		for (int i = 0; i < arr.length; i++) {
			heap.toss(arr[i]);
			size = heap.size();
			assertTrue(size > last);
			last = size;
		}

		System.out.println("Tossed all elements of array onto heap.");
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testIterator() {
		// doh!
		testToss();

		IntEnumerator iter = heap.orderedElements(true);
		assertNotNull(iter);

		System.out.println("Got an IntEnumerator by calling orderedElements(true) on heap.");
		System.out.println("The enumerator's numRemaining() method returns " + iter.numRemaining()
		                   + ".");

		assertEquals(8, iter.numRemaining());

		System.out.print("The enumerator looks like this: { ");

		while (iter.numRemaining() > 1)
			System.out.print(iter.nextInt() + ", ");

		System.out.println(iter.nextInt() + " }.");
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testOrder() {
		// doh!
		testToss();

		IntEnumerator iter = heap.orderedElements(false);
		assertNotNull(iter);
		System.out.println("Got an IntEnumerator by calling orderedElements(false) on heap.");
		System.out.println("The enumerator's numRemaining() method returns " + iter.numRemaining()
		                   + ".");

		assertEquals(12, iter.numRemaining());
		System.out.print("The enumerator looks like this: { ");

		while (iter.numRemaining() > 1)
			System.out.print(iter.nextInt() + ", ");

		System.out.println(iter.nextInt() + " }.");
	}
}
