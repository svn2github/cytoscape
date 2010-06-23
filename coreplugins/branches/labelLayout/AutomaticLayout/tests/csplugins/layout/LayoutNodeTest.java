package csplugins.layout;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.Math;

public class LayoutNodeTest {

    static final double EPSILON = 0.0000001D;

    // This doesn't do anything, but if it doesn't exist an error happens 
    @Test public void doNothing() {
    }


    /**
     * These tests should work with any subclass of LayoutNode
     */

    public void testSetX(LayoutNode node, double x) {
	node.setX(x);
	assertEquals(x,node.getX(),0.0);
    }

    public void testSetY(LayoutNode node, double y) {
	node.setY(y);
	assertEquals(y,node.getY(),0.0);
    }
    
    public void testSetLocation(LayoutNode node, double x, double y) {
	node.setLocation(x,y);
	assertEquals(x,node.getX(),0.0);	
	assertEquals(y,node.getY(),0.0);
    }

    public void testDisplacement(LayoutNode node, 
				 double x, 
				 double y, 
				 double dx1, 
				 double dx2,
				 double dy1,
				 double dy2) {
	node.setDisp(x,y);
	node.incrementDisp(dx1,dy1);
	node.decrementDisp(dx2,dy2);
	assertEquals(x+dx1-dx2,node.getXDisp(), EPSILON);
	assertEquals(y+dy1-dy2,node.getYDisp(), EPSILON);
    }

    public void testDistance(LayoutNode node1, 
			     LayoutNode node2,
			     double x1,
			     double x2,
			     double y1,
			     double y2) {
	node1.setLocation(x1,y1);
	node2.setLocation(x2,y2);
	double d = Math.sqrt((x2-x1) * (x2-x1) + (y2-y1) * (y2-y1));
	assertEquals(d, node1.distance(node2), EPSILON);
    }

}