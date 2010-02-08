
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

import cytoscape.util.intr.IntBTree;
import cytoscape.util.intr.IntEnumerator;

import junit.framework.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public class IntBTreeTest extends TestCase {
	IntBTree tree;

	/**
	 * DOCUMENT ME!
	 */
	public void setUp() {
		tree = new IntBTree(3);
		System.out.println("Instantiated new IntBTree.");

		final int[] arr = new int[] { 8, 1, 3, 8, 3, 0, 9, 1, 7, 3, 3, 0, 4, 3, 1, 3, 3 };

		System.out.print("My int[] is: { ");

		for (int i = 0; i < (arr.length - 1); i++) {
			tree.insert(arr[i]);
			System.out.print(arr[i] + ", ");
		}

		System.out.println(arr[arr.length - 1] + " }.");

		System.out.println("Inserted all elements of array into tree.");
	}

	// tests the order of the iterator
	/**
	 *  DOCUMENT ME!
	 */
	public void testOrder() {
		IntEnumerator iter = tree.searchRange(Integer.MIN_VALUE, Integer.MAX_VALUE, false);
		System.out.println("Here are the ordered elements:");
		System.out.print("  ");

		int curr = -1;
		int last = -1;

		while (iter.numRemaining() > 0) {
			curr = iter.nextInt();
			System.out.print(curr + " ");
			assertTrue(last <= curr);
			last = curr;
		}

		System.out.println(".");
	}

	// tests the counting of the number of occurrances in the btree
	/**
	 *  DOCUMENT ME!
	 */
	public void testCount() {
		final int[] countThese = new int[] {
		                             Integer.MIN_VALUE, -1, 0, 1, 3, 4, 6, 7, 8, 9, 10, 99,
		                             Integer.MAX_VALUE
		                         };

		for (int i = 0; i < countThese.length; i++)
			System.out.println("The count of integer " + countThese[i] + " is "
			                   + tree.count(countThese[i]) + ".");

		assertEquals(0, tree.count(Integer.MIN_VALUE));
		assertEquals(0, tree.count(-1));
		assertEquals(2, tree.count(0));
		assertEquals(3, tree.count(1));
		//assertEquals( 7, tree.count(3));// bug!
		assertEquals(1, tree.count(4));
		assertEquals(0, tree.count(6));
		assertEquals(1, tree.count(7));
		assertEquals(2, tree.count(8));
		assertEquals(1, tree.count(9));
		assertEquals(0, tree.count(10));
		assertEquals(0, tree.count(99));
		assertEquals(0, tree.count(Integer.MAX_VALUE));
	}

	/**
	 * Tests the values returned within specified min and max ranges.
	 */
	public void testMinMaxRange() {
		IntEnumerator iter = tree.searchRange(Integer.MIN_VALUE, Integer.MAX_VALUE, false);

		final int[] xMins = new int[] {
		                        Integer.MIN_VALUE, Integer.MIN_VALUE, -23, 1, 3, 2, 3, 8, 4, 4, 5, 6,
		                        -1, 11
		                    };
		final int[] xMaxs = new int[] {
		                        Integer.MAX_VALUE, 3, 99, 2, Integer.MAX_VALUE, 6, 4, 8, 4, 8, 6, 6,
		                        -1, 11
		                    };

		for (int i = 0; i < xMins.length; i++) {
			System.out.println("In range [" + xMins[i] + ", " + xMaxs[i] + "]:");
			System.out.print("  ascending: ");
			iter = tree.searchRange(xMins[i], xMaxs[i], false);

			while (iter.numRemaining() > 0) {
				int n = iter.nextInt();
				assertTrue(n <= xMaxs[i]);
				assertTrue(n >= xMins[i]);
				System.out.print(n + " ");
			}

			System.out.println(".");
			System.out.print("  descending: ");
			iter = tree.searchRange(xMins[i], xMaxs[i], true);

			while (iter.numRemaining() > 0) {
				int n = iter.nextInt();
				assertTrue(n <= xMaxs[i]);
				assertTrue(n >= xMins[i]);
				System.out.print(n + " ");
			}

			System.out.println(".");
		}
	}

	/**
	 * tests the deletion operation
	 */
	public void testDelete() {
		IntEnumerator iter = tree.searchRange(Integer.MIN_VALUE, Integer.MAX_VALUE, false);

		System.out.println("Now going to delete some random entries.");

		assertTrue(tree.delete(8));
		assertTrue(!tree.delete(-1));
		assertTrue(tree.delete(3));
		assertTrue(!tree.delete(2));
		assertTrue(tree.delete(9));
		assertTrue(tree.delete(3));
		assertTrue(tree.delete(0));
		assertTrue(tree.delete(3));

		System.out.println("Now going to delete remaining entries.");

		iter = tree.searchRange(Integer.MIN_VALUE, Integer.MAX_VALUE, false);

		final int[] intsRemaining = new int[iter.numRemaining()];

		for (int i = 0; i < intsRemaining.length; i++)
			intsRemaining[i] = iter.nextInt();

		for (int i = 0; i < intsRemaining.length; i++) {
			final int delInt = intsRemaining[(i + (intsRemaining.length / 2)) % intsRemaining.length];
			System.out.println("deleting " + delInt);
			assertTrue(tree.delete(delInt));
		}
	}
}
