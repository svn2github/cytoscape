/* -*-Java-*-
********************************************************************************
*
* File:         EdgeTypeMapTest.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/unittest/EdgeTypeMapTest.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Tue Oct 04 06:03:24 2005
* Modified:     Fri Aug 11 20:55:43 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
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
    //    private final String TEST_LOC = "hyperedge-etm-test1.xml";
    //    private final String TEST_LOC2 = "hyperedge-etm-test2.xml";
    protected EdgeTypeMap etm = factory.getEdgeTypeMap();

    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(EdgeTypeMapTest.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

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
            etm.put("biopax.in", null);
            fail("Didn't get expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }

        Assert.assertNull(etm.put("biopax.in", EdgeRole.SOURCE));
        Assert.assertTrue(etm.get("biopax.in") == EdgeRole.SOURCE);
        Assert.assertTrue(etm.size() == 5);
        Assert.assertNull(etm.put("biopax.out", EdgeRole.TARGET));
        Assert.assertTrue(etm.get("biopax.out") == EdgeRole.TARGET);
        Assert.assertTrue(etm.size() == 6);
        // test replacing existing entries:
        Assert.assertTrue(EdgeRole.SOURCE == etm.put(EdgeTypeMap.SUBSTRATE,
                                                     EdgeRole.TARGET));
        Assert.assertTrue(etm.get(EdgeTypeMap.SUBSTRATE) == EdgeRole.TARGET);
        Assert.assertTrue(etm.size() == 6);
    }

    private void performRemoveTests() {
        // reset the map:
        resetMap();
        Assert.assertNull(etm.put("biopax.in", EdgeRole.SOURCE));
        Assert.assertNull(etm.put("biopax.out", EdgeRole.TARGET));
        Assert.assertTrue(etm.size() == 6);
        Assert.assertTrue(etm.remove(null) == null);
        Assert.assertTrue(etm.remove("no mapping") == null);
        Assert.assertTrue(etm.remove("biopax.in") == EdgeRole.SOURCE);
        Assert.assertTrue(etm.size() == 5);
        Assert.assertTrue(etm.remove("biopax.out") == EdgeRole.TARGET);
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

        Map<String, EdgeRole> new_map = new HashMap<String, EdgeRole>();

        // add nothing:
        Assert.assertFalse(etm.addAll(null));
        // add nothing:
        Assert.assertFalse(etm.addAll(new_map));
        Assert.assertTrue(etm.size() == 4);

        new_map.put(EdgeTypeMap.SUBSTRATE, EdgeRole.TARGET);
        new_map.put(EdgeTypeMap.ACTIVATING_MEDIATOR, EdgeRole.TARGET);
        new_map.put(EdgeTypeMap.INHIBITING_MEDIATOR, EdgeRole.TARGET);
        new_map.put(EdgeTypeMap.PRODUCT, EdgeRole.SOURCE);
        new_map.put("biopax.in", EdgeRole.SOURCE);
        new_map.put("biopax.out", EdgeRole.TARGET);
        // now add 2 new entries and change 4 existing entries:
        Assert.assertTrue(etm.addAll(new_map));
        Assert.assertTrue(etm.get(EdgeTypeMap.SUBSTRATE) == EdgeRole.TARGET);
        Assert.assertTrue(
            etm.get(EdgeTypeMap.ACTIVATING_MEDIATOR) == EdgeRole.TARGET);
        Assert.assertTrue(
            etm.get(EdgeTypeMap.INHIBITING_MEDIATOR) == EdgeRole.TARGET);
        Assert.assertTrue(etm.get(EdgeTypeMap.PRODUCT) == EdgeRole.SOURCE);
        Assert.assertTrue(etm.get("biopax.in") == EdgeRole.SOURCE);
        Assert.assertTrue(etm.get("biopax.out") == EdgeRole.TARGET);
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

        Iterator it = etm.iterator();
        Assert.assertFalse(it.hasNext());
        // now add entries:
        resetMap();
        // loadMapTestHelper(TEST_LOC);
        Assert.assertNull(etm.put("biopax.in", EdgeRole.SOURCE));
        Assert.assertNull(etm.put("biopax.out", EdgeRole.TARGET));
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
