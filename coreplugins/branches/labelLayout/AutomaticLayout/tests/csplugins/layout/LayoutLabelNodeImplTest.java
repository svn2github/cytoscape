package csplugins.layout;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cytoscape.data.CyAttributes;
import cytoscape.Cytoscape;

import cytoscape.view.*;
import giny.view.*;

public class LayoutLabelNodeImplTest extends LayoutNodeTest {

    LayoutLabelNodeImpl node1, node2,node3;
    LayoutNodeImpl parentNode1;
    NodeView nodeView1;

    @Before public void setUp() {
	node1 = new LayoutLabelNodeImpl();
	node2 = new LayoutLabelNodeImpl();
    }

    @Test public void testSetX() {
	super.testSetX(node1,1.5);
	super.testSetX(node2,4.0002);
    }

    @Test public void testSetY() {
	super.testSetY(node1,1.5);
	super.testSetY(node2,4.0002);
    }

    @Test public void testSetLocation() {
	super.testSetLocation(node1,3.0,7.789);
	super.testSetLocation(node2,0.0,2.67);
    }

    @Test public void testDisplacement() {
	super.testDisplacement(node1, 1.4, 3.6, 5.0, -0.3, 9.3, 12.45);
	super.testDisplacement(node2, 11.34, 33.0, -5.3, 0.0, 78.12, -123.3);
    }

    @Test public void testDistance() {
	super.testDistance(node1, node2, 0.4, -3.5, 2345.2, 234.3);
    }

    // TODO: Use nodes properly created for this test
//     @Test public void testMoveToLocation1() {
// 	node1.lock();
// 	super.testMoveToLocation1(node1,4.6,7.1);
// 	node2.unLock();
// 	super.testMoveToLocation1(node2,6.564,12.143);
//     }

}