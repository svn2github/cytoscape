package cytoscape.visual;

import junit.framework.TestCase;

public class NodeShapeTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParseNodeShapeText() {
		assertEquals(NodeShape.parseNodeShapeText("rect"), NodeShape.RECT);
		assertEquals(NodeShape.parseNodeShapeText("rect3d"), NodeShape.RECT_3D);
		assertEquals(NodeShape.parseNodeShapeText("trapezoid2"), NodeShape.TRAPEZOID_2);
		assertEquals(NodeShape.parseNodeShapeText("diamond"), NodeShape.DIAMOND);
	}

	public void testValuesAsString() {
		
	}

	public void testGetShapeName() {
		
	}

	public void testIsValidShape() {
		
	}

	public void testGetNodeShapeText() {
		
	}

	public void testGetNodeShape() {
		
	}

	public void testGetGinyShape() {
		
	}

}
