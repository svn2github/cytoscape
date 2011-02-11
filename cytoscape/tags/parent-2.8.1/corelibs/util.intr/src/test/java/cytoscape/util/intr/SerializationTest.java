
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

import cytoscape.util.intr.IntArray;
import cytoscape.util.intr.IntBTree;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntIntHash;
import cytoscape.util.intr.IntObjHash;
import cytoscape.util.intr.IntQueue;
import cytoscape.util.intr.IntStack;
import cytoscape.util.intr.MinIntHeap;

import junit.framework.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public class SerializationTest extends TestCase {
	/**
	 *  DOCUMENT ME!
	 */
	public void testSerialize() {
		try {
			IntArray intArray = new IntArray();
			intArray.setIntAtIndex(-3, 5);

			IntBTree intBTree = new IntBTree();

			for (int i = 0; i < 200; i++)
				intBTree.insert((i * 77) % 1001);

			IntHash intHash = new IntHash();
			intHash.put(23);

			IntIntHash intIntHash = new IntIntHash();
			intIntHash.put(2, 3);

			IntObjHash intObjHash = new IntObjHash();
			intObjHash.put(1, "foo");

			IntQueue intQueue = new IntQueue();
			intQueue.enqueue(-43);

			IntStack intStack = new IntStack();
			intStack.push(72);

			MinIntHeap minIntHeap = new MinIntHeap();
			minIntHeap.insert(4);

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
			objOut.writeObject(intArray);
			objOut.writeObject(intBTree);
			objOut.writeObject(intHash);
			objOut.writeObject(intIntHash);
			objOut.writeObject(intObjHash);
			objOut.writeObject(intQueue);
			objOut.writeObject(intStack);
			objOut.writeObject(minIntHeap);
			objOut.flush();
			objOut.close();

			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			ObjectInputStream objIn = new ObjectInputStream(byteIn);
			intArray = (IntArray) objIn.readObject();
			intBTree = (IntBTree) objIn.readObject();
			intHash = (IntHash) objIn.readObject();
			intIntHash = (IntIntHash) objIn.readObject();
			intObjHash = (IntObjHash) objIn.readObject();
			intQueue = (IntQueue) objIn.readObject();
			intStack = (IntStack) objIn.readObject();
			minIntHeap = (MinIntHeap) objIn.readObject();
			objIn.close();

			assertEquals(-3, intArray.getIntAtIndex(5));
			assertEquals(0, intArray.getIntAtIndex(4));

			assertEquals(200, intBTree.size());
			assertTrue(intBTree.delete(154));
			assertTrue(intBTree.delete(77));
			assertEquals(198, intBTree.size());

			assertEquals(1, intHash.size());
			assertEquals(23, intHash.get(23));
			assertEquals(-1, intHash.get(7));

			assertEquals(1, intIntHash.size());
			assertEquals(3, intIntHash.get(2));
			assertEquals(-1, intIntHash.get(0));

			assertEquals(1, intObjHash.size());
			assertTrue("foo".equals(intObjHash.get(1)));
			assertNull(intObjHash.get(2));

			assertEquals(-43, intQueue.dequeue());
			assertEquals(0, intQueue.size());

			assertEquals(72, intStack.pop());
			assertEquals(0, intStack.size());

			assertEquals(4, minIntHeap.deleteMin());
			assertEquals(0, minIntHeap.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
