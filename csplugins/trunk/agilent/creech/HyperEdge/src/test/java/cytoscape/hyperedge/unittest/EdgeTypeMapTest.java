
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

/* 
*
* Revisions:
*
* Sat Jul 29 14:10:28 2006 (Michael L. Creech) creech@w235krbza760
*  Changed MEDIATOR-->ACTIVATING_MEDIATOR & INHIBITING_MEDIATOR.
********************************************************************************
*/
package cytoscape.hyperedge.unittest;

import cytoscape.hyperedge.EdgeTypeMap;
import cytoscape.hyperedge.EdgeTypeMap.EdgeRole;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Test the various EdgeTypeMap operations.
 * @author Michael L. Creech
 * @version 1.0
 */
public class EdgeTypeMapTest extends TestBase {
    private static final String BIOPAX_IN = "biopax.in";
    private static final String BIOPAX_OUT = "biopax.out";
    //    private final String TEST_LOC = "hyperedge-etm-test1.xml";
    //    private final String TEST_LOC2 = "hyperedge-etm-test2.xml";
    private EdgeTypeMap etm = factory.getEdgeTypeMap();

    /**
     * JUnit method for running tests for this class.
     * @return the Test to peform.
     */
    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(EdgeTypeMapTest.class);
    }

    /**
     * Main for test.
     * @param args standard args to main program
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /**
     * Overall Edge Type Map tester.
     */
    public void testEdgeTypeMap() {
        //        saveMapTestHelper(TEST_LOC);
        performETMTests();

        //        loadMapTestHelper(TEST_LOC);
        //        performETMTests();
    }

    private void performETMTests() {
        performPutTests();
        performRemoveTests();
        performGetTests();
        performAddAllTest();
        // performDirtyTests();
        performIteratorTests();
    }

    private void performPutTests() {
        // test put ():
        try {
            etm.put(null, EdgeRole.SOURCE);
            fail("Didn't get expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }

        try {
            etm.put(BIOPAX_IN, null);
            fail("Didn't get expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }

        Assert.assertNull(etm.put(BIOPAX_IN, EdgeRole.SOURCE));
        Assert.assertTrue(etm.get(BIOPAX_IN) == EdgeRole.SOURCE);
        Assert.assertTrue(etm.size() == 5);
        Assert.assertNull(etm.put(BIOPAX_OUT, EdgeRole.TARGET));
        Assert.assertTrue(etm.get(BIOPAX_OUT) == EdgeRole.TARGET);
        final int sizeSix = 6;
	Assert.assertTrue(etm.size() == sizeSix);
        // test replacing existing entries:
        Assert.assertTrue(EdgeRole.SOURCE == etm.put(EdgeTypeMap.SUBSTRATE,
                                                     EdgeRole.TARGET));
        Assert.assertTrue(etm.get(EdgeTypeMap.SUBSTRATE) == EdgeRole.TARGET);
        Assert.assertTrue(etm.size() == sizeSix);
    }

    private void performRemoveTests() {
        // reset the map:
        resetMap();
        Assert.assertNull(etm.put(BIOPAX_IN, EdgeRole.SOURCE));
        Assert.assertNull(etm.put(BIOPAX_OUT, EdgeRole.TARGET));
        Assert.assertTrue(etm.size() == 6);
        Assert.assertTrue(etm.remove(null) == null);
        Assert.assertTrue(etm.remove("no mapping") == null);
        Assert.assertTrue(etm.remove(BIOPAX_IN) == EdgeRole.SOURCE);
        Assert.assertTrue(etm.size() == 5);
        Assert.assertTrue(etm.remove(BIOPAX_OUT) == EdgeRole.TARGET);
        Assert.assertTrue(etm.size() == 4);
        // remove everything and check empty:
        Assert.assertTrue(etm.remove(EdgeTypeMap.SUBSTRATE) == EdgeRole.SOURCE);
        Assert.assertTrue(
            etm.remove(EdgeTypeMap.ACTIVATING_MEDIATOR) == EdgeRole.SOURCE);
        Assert.assertTrue(
            etm.remove(EdgeTypeMap.INHIBITING_MEDIATOR) == EdgeRole.SOURCE);
        Assert.assertTrue(etm.remove(EdgeTypeMap.PRODUCT) == EdgeRole.TARGET);
        Assert.assertTrue(etm.isEmpty());
    }

    private void performGetTests() {
        resetMap();
        Assert.assertTrue(etm.size() == 4);
        Assert.assertTrue(!etm.isEmpty());
        Assert.assertTrue(etm.get(EdgeTypeMap.SUBSTRATE) == EdgeRole.SOURCE);
        Assert.assertTrue(
            etm.get(EdgeTypeMap.ACTIVATING_MEDIATOR) == EdgeRole.SOURCE);
        Assert.assertTrue(
            etm.get(EdgeTypeMap.INHIBITING_MEDIATOR) == EdgeRole.SOURCE);
        Assert.assertTrue(etm.get(EdgeTypeMap.PRODUCT) == EdgeRole.TARGET);
        // try getting nonexistent:
        Assert.assertTrue(etm.get("jojo") == null);
        Assert.assertTrue(etm.get(null) == null);
    }

    private void performAddAllTest() {
        resetMap(); // should now have 3 elements

        final Map<String, EdgeRole> newMap = new HashMap<String, EdgeRole>();

        // add nothing:
        Assert.assertFalse(etm.addAll(null));
        // add nothing:
        Assert.assertFalse(etm.addAll(newMap));
        Assert.assertTrue(etm.size() == 4);

        newMap.put(EdgeTypeMap.SUBSTRATE, EdgeRole.TARGET);
        newMap.put(EdgeTypeMap.ACTIVATING_MEDIATOR, EdgeRole.TARGET);
        newMap.put(EdgeTypeMap.INHIBITING_MEDIATOR, EdgeRole.TARGET);
        newMap.put(EdgeTypeMap.PRODUCT, EdgeRole.SOURCE);
        newMap.put(BIOPAX_IN, EdgeRole.SOURCE);
        newMap.put(BIOPAX_OUT, EdgeRole.TARGET);
        // now add 2 new entries and change 4 existing entries:
        Assert.assertTrue(etm.addAll(newMap));
        Assert.assertTrue(etm.get(EdgeTypeMap.SUBSTRATE) == EdgeRole.TARGET);
        Assert.assertTrue(
            etm.get(EdgeTypeMap.ACTIVATING_MEDIATOR) == EdgeRole.TARGET);
        Assert.assertTrue(
            etm.get(EdgeTypeMap.INHIBITING_MEDIATOR) == EdgeRole.TARGET);
        Assert.assertTrue(etm.get(EdgeTypeMap.PRODUCT) == EdgeRole.SOURCE);
        Assert.assertTrue(etm.get(BIOPAX_IN) == EdgeRole.SOURCE);
        Assert.assertTrue(etm.get(BIOPAX_OUT) == EdgeRole.TARGET);
        Assert.assertTrue(etm.size() == 6);

        //        saveMapTestHelper(TEST_LOC2);
        //        // will erase existing map contents before loading:
        //        loadMapTestHelper(TEST_LOC2);
        //        // recheck values:
        //        Assert.assertTrue(etm.get(EdgeTypeMap.SUBSTRATE) == EdgeRole.TARGET);
        //        Assert.assertTrue(etm.get(EdgeTypeMap.ACTIVATING_MEDIATOR) == EdgeRole.TARGET);
        //        Assert.assertTrue(etm.get(EdgeTypeMap.INHIBITING_MEDIATOR) == EdgeRole.TARGET);
        //        Assert.assertTrue(etm.get(EdgeTypeMap.PRODUCT) == EdgeRole.SOURCE);
        //        Assert.assertTrue(etm.get("biopax.in") == EdgeRole.SOURCE);
        //        Assert.assertTrue(etm.get("biopax.out") == EdgeRole.TARGET);
        //        Assert.assertTrue(etm.size() == 6);
    }

    //    private void performDirtyTests() {
    //        resetMap(); // should now have 3 elements
    //        saveMapTestHelper(TEST_LOC2); // should clean the dirty flag
    //        Assert.assertFalse(etm.isDirty());
    //
    //        DirtyTester dt = new DirtyTester(true);
    //        etm.addDirtyListener(dt);
    //        // test adding an entry:
    //        Assert.assertNull(etm.put("biopax.out", EdgeRole.TARGET));
    //        // 2nd change should do nothing:
    //        Assert.assertNull(etm.put("biopax.in", EdgeRole.SOURCE));
    //        Assert.assertTrue(dt.getNumCalls() == 1);
    //        etm.removeDirtyListener(dt);
    //        // test removing an entry:
    //        resetMap();
    //        saveMapTestHelper(TEST_LOC2); // should clean the dirty flag
    //        dt = new DirtyTester(true);
    //        etm.addDirtyListener(dt);
    //        etm.remove(EdgeTypeMap.SUBSTRATE);
    //        Assert.assertTrue(dt.getNumCalls() == 1);
    //        etm.removeDirtyListener(dt);
    //
    //        // test save to reset to clean:
    //        resetMap();
    //        Assert.assertNull(etm.put("biopax.in", EdgeRole.SOURCE));
    //        dt = new DirtyTester(false);
    //        etm.addDirtyListener(dt);
    //        saveMapTestHelper(TEST_LOC2);
    //        Assert.assertTrue(dt.getNumCalls() == 1);
    //        etm.removeDirtyListener(dt);
    //    }
    private void performIteratorTests() {
        // reset the map:
        etm.clear();

        Iterator<Map.Entry<String, EdgeRole>> it = etm.iterator();
        Assert.assertFalse(it.hasNext());
        // now add entries:
        resetMap();
        // loadMapTestHelper(TEST_LOC);
        Assert.assertNull(etm.put(BIOPAX_IN, EdgeRole.SOURCE));
        Assert.assertNull(etm.put(BIOPAX_OUT, EdgeRole.TARGET));
        it = etm.iterator();

        int count = 0;

        while (it.hasNext()) {
            it.next();
            count++;
        }

        Assert.assertTrue(count == 6);
    }

    private void resetMap() {
        etm.reset();

        //        loadMapTestHelper(TEST_LOC);
    }

    //    protected int saveMapTestHelper(String file_name) {
    //        String full_loc = load_save_loc + file_name;
    //        File full_loc_as_file = new File(full_loc);
    //
    //        return etm.save(full_loc_as_file.toURI().toString(),
    //            HyperEdgeManager.Format.XML);
    //    }
    //
    //    // Tests are loaded under user's home directory in '.hyperedge/test-results/'.
    //    protected int loadMapTestHelper(String file_name) {
    //        String full_loc = load_save_loc + file_name;
    //        File full_loc_as_file = new File(full_loc);
    //
    //        return etm.load(full_loc_as_file.toURI().toString(),
    //            HyperEdgeManager.Format.XML);
    //    }
    //
    //    private class DirtyTester implements DirtyListener {
    //        private boolean _test_val = false;
    //        private int _num_calls = 0;
    //
    //        public DirtyTester(boolean test_val) {
    //            _test_val = test_val;
    //        }
    //
    //        public void dirtyStateChanged(Identifiable obj) {
    //            Assert.assertTrue(obj.isDirty() == _test_val);
    //            _num_calls++;
    //        }
    //
    //        public int getNumCalls() {
    //            return _num_calls;
    //        }
    //    }
}
