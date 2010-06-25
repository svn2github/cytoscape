package cytoscape.layout.label;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cytoscape.data.CyAttributes;
import cytoscape.visual.LabelPosition;
import cytoscape.Cytoscape;

import cytoscape.view.*;
import giny.view.*;

public class LayoutLabelNodeTest extends LayoutAbstractNodeTest {

    LayoutLabelNode node1, node2,node3;
    LayoutNode parentNode1;
    CyNodeView nodeView1;

    @Before public void setUp() {
	node1 = new LayoutLabelNode();
	node2 = new LayoutLabelNode();
	//	nodeView1 = new CyNodeView();
	//parentNode1 = new LayoutNode(nodeView1, 1);
	//node3 = new LayoutLabelNode(parentNode1);
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

    @Test public void testMoveToLocation() {
// 	parentNode1.setLocation(1.0,3.5);
// 	parentNode1.moveToLocation();
// 	node3.setLocation(5.0,8.5);
// 	node3.moveToLocation();

// 	CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
// 	String labelPosition = (String) nodeAtts.getAttribute(parentNode1.getNode().
// 							      getIdentifier(), "node.labelPosition");

// 	assertNotNull(labelPosition);

// 	LabelPosition lp = LabelPosition.parse(labelPosition);
				
// 	assertEquals(lp.getOffsetX(),4.0,EPSILON);
// 	assertEquals(lp.getOffsetY(),5.0,EPSILON); 
    }

}