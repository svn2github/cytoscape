/* -*-Java-*-
********************************************************************************
*
* File:         EventTest.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/unittest/EventTest.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Wed Sep 21 09:15:53 2005
* Modified:     Tue Nov 07 06:58:28 2006 (Michael L. Creech) creech@w235krbza760
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
* Tue Nov 07 06:58:25 2006 (Michael L. Creech) creech@w235krbza760
*  Changed use of Edge-->CyEdge.
* Thu Nov 02 05:22:06 2006 (Michael L. Creech) creech@w235krbza760
* Changed to usage of new shared HyperEdges API.
********************************************************************************
*/
package cytoscape.hyperedge.unittest;


import cytoscape.CyEdge;
import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.event.DeleteListener;
import cytoscape.hyperedge.event.EventNote;
import cytoscape.hyperedge.event.NewObjectListener;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Test HyperEdge and Role events.
 * @author Michael L. Creech
 * @version 1.0
 */
public class EventTest extends TestBase {
    private int          _num_hes;
    private DeleteTester _dt = new DeleteTester();

    public static Test suite() {
        // Will dynamically add all methods as tests that begin with 'test'
        // and have no arguments:
        return new TestSuite(EventTest.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public void testHyperEdgeEvents() {
        NewObjTester not = new NewObjTester();
        manager.addNewObjectListener(not);
        setUp1(true);
        Assert.assertTrue(_num_hes == 2);
        manager.removeNewObjectListener(not);
        manager.reset(true);
        Assert.assertTrue(_num_hes == 0);
        setUp1(true);

        // TEST changing the name:
        ChangeTester ct = new ChangeTester(he1,
                                           EventNote.Type.NAME,
                                           EventNote.SubType.CHANGED,
                                           true,
                                           he1.getName());
        manager.addChangeListener(ct);
        he1.setName("jojo");
        manager.removeChangeListener(ct);

        // TEST changing directed:
        ct = new ChangeTester(he1,
                              EventNote.Type.DIRECTED,
                              EventNote.SubType.CHANGED,
                              true,
                              null);
        manager.addChangeListener(ct);
        he1.setDirected(true);
        manager.removeChangeListener(ct);

        // TEST adding an edge:
        ct = new ChangeTester(he1,
                              EventNote.Type.EDGE,
                              EventNote.SubType.ADDED,
                              true);
        manager.addChangeListener(ct);

        CyEdge edge = he1.addEdge(A, EXTRA);
        Assert.assertNotNull(edge);

        CyEdge supportInfo = (CyEdge) ct.getLastEventNote().getSupportingInfo();
        Assert.assertTrue(supportInfo == edge);
        manager.removeChangeListener(ct);

        // TEST removing an edge:
        ct = new ChangeTester(he1,
                              EventNote.Type.EDGE,
                              EventNote.SubType.REMOVED,
                              true);
        manager.addChangeListener(ct);
        he1.removeEdge(edge);
        supportInfo = (CyEdge) ct.getLastEventNote().getSupportingInfo();
        Assert.assertTrue(supportInfo == edge);
        manager.removeChangeListener(ct);

        // TODO: Add addToGraphPerspective() and
        // removeFromGraphPerspective().
    }

    //    public void testHyperEdgeDirtyEvents ()
    //    {
    //        manager.reset (false);
    //        setUp1 (true);
    //        DirtyTester dt = new DirtyTester(false);
    //        he1.addDirtyListener (dt);
    //        he1.setDirty (false);
    //        Assert.assertTrue (dt.getNumCalls () == 1);
    //        he1.removeDirtyListener (dt);
    //
    //        // TEST directed change:
    //        dt = new DirtyTester(true);
    //        he1.addDirtyListener (dt);
    //        he1.setDirected (true);
    //        he1.setDirected (true);
    //        Assert.assertTrue (dt.getNumCalls () == 1);
    //        he1.removeDirtyListener (dt);
    //
    //        // TEST name changes:
    //        he1.setDirty (false);
    //        dt = new DirtyTester(true);
    //        he1.addDirtyListener (dt);
    //        he1.setName ("jojo");
    //        he1.setName ("jojo");
    //        Assert.assertTrue (dt.getNumCalls () == 1);
    //        he1.removeDirtyListener (dt);
    //
    //        // TEST edge addition:
    //        he1.setDirty (false);
    //        dt = new DirtyTester(true);
    //        he1.addDirtyListener (dt);
    //        CyEdge edge1 = he1.addEdge (n4, EXTRA);
    //        CyEdge edge2 = he1.addEdge (n4, EXTRA);
    //        Assert.assertTrue (dt.getNumCalls () == 1);
    //        he1.removeDirtyListener (dt);
    //
    //        // TEST edge removal:
    //        he1.setDirty (false);
    //        dt = new DirtyTester(true);
    //        he1.addDirtyListener (dt);
    //        he1.removeEdge (edge1);
    //        he1.removeEdge (edge2);
    //        Assert.assertTrue (dt.getNumCalls () == 1);
    //        he1.removeDirtyListener (dt);
    //    }

    //    public void testAnyDirtyEvents ()
    //    {
    //        manager.reset (false);
    //        setUp1 ();
    //        Assert.assertTrue (manager.isAnyDirty ());
    //        AnyDirtyTester adt = new AnyDirtyTester(true);
    //        manager.addAnyDirtyListener (adt);
    //	// TODO: start testing here:
    //        manager.removeAnyDirtyListener (adt);
    //        // Assert.assertTrue (adt.getNumCalls () == 1);
    //    }
    private class NewObjTester implements NewObjectListener {
        public void objectCreated(HyperEdge obj) {
            manager.addDeleteListener(_dt);

            if (obj instanceof HyperEdge) {
                _num_hes++;
            } else {
                Assert.fail(
                    "found New object created that wasn't a HyperEdge or Role!");
            }
        }
    }

    private class DeleteTester implements DeleteListener {
        public void objectDestroyed(HyperEdge obj) {
            if (obj instanceof HyperEdge) {
                _num_hes--;
            } else {
                Assert.fail(
                    "found New object created that wasn't a HyperEdge or Role!");
            }
        }
    }

    //    private class AnyDirtyTester implements AnyDirtyListener
    //    {
    //        private boolean _test_val  = false;
    //        private int     _num_calls = 0;
    //
    //        public AnyDirtyTester (boolean test_val)
    //        {
    //            _test_val = test_val;
    //        }
    //
    //        public void dirtyHyperGraphStateChanged (boolean dirty_state)
    //        {
    //            Assert.assertTrue (dirty_state == _test_val);
    //            _num_calls++;
    //        }
    //
    //        public int getNumCalls ()
    //        {
    //            return _num_calls;
    //        }
    //    }
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
