package cytoscape.visual.mappings;

import java.awt.Color;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.readers.InteractionsReader;
import junit.framework.TestCase;

public class PassthroughMappingTest extends TestCase {
	
	private CyAttributes nodeAttr;
	private CyAttributes edgeAttr;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		nodeAttr = Cytoscape.getNodeAttributes();
		edgeAttr = Cytoscape.getEdgeAttributes();
		InteractionsReader reader = new InteractionsReader("testData/galFiltered.sif");
		reader.read();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testPassThrough() throws Exception {
		assertNotNull(nodeAttr);
		final Color color1 = new Color(100, 100, 100);
		final Color color2 = new Color(200, 150, 0);
		
		// Test color values
		nodeAttr.setAttribute("?", "Color", color1.getRed() + "," + color1.getGreen() + "," + color1.getBlue());
		nodeAttr.setAttribute("YNL050C", "Color", color2.getRed() + "," + color2.getGreen() + "," + color2.getBlue());
		nodeAttr.setAttribute("YIL061C", "Color", "300,-1,10"); // Invalid
		
		// Test size values
		nodeAttr.setAttribute("?", "Node Size", "100");
		nodeAttr.setAttribute("YNL050C", "Node Size", "-10"); // Invalid, but should return value.
		
		// Edge
		edgeAttr.setAttribute("YNL216W (pd) YIL069C", "Width", "10");
		edgeAttr.setAttribute("YNL216W (pd) YAL038W", "Width", "abcd"); // Invalid
		
		
		ObjectMapping pm = new PassThroughMapping(Color.class, "Color");
		Map<String, Object> bundle = CyAttributesUtils.getAttributes("?", nodeAttr);
		Object rangeVal = pm.calculateRangeValue(bundle);
		
		assertNotNull(rangeVal);
		assertEquals(rangeVal.getClass(), Color.class);
		assertEquals(rangeVal, color1);
		
		bundle = CyAttributesUtils.getAttributes("YNL050C", nodeAttr);
		rangeVal = pm.calculateRangeValue(bundle);
		assertNotNull(rangeVal);
		assertEquals(rangeVal.getClass(), Color.class);
		assertEquals(rangeVal, color2);
		
		// Invalid string is given.  Should return null.
		bundle = CyAttributesUtils.getAttributes("YIL061C", nodeAttr);
		rangeVal = pm.calculateRangeValue(bundle);
		assertNull(rangeVal);
		
		pm = new PassThroughMapping(Double.class, "Node Size");
		bundle = CyAttributesUtils.getAttributes("?", nodeAttr);
		rangeVal = pm.calculateRangeValue(bundle);
		
		assertNotNull(rangeVal);
		assertEquals(rangeVal.getClass(), Double.class);
		assertEquals(rangeVal, 100.0);
		
		bundle = CyAttributesUtils.getAttributes("YNL050C", nodeAttr);
		rangeVal = pm.calculateRangeValue(bundle);
		
		assertNotNull(rangeVal);
		assertEquals(rangeVal.getClass(), Double.class);
		assertEquals(-10.0, rangeVal); // This value should be validated by the caller.
		
		pm = new PassThroughMapping(Float.class, "Width");
		bundle = CyAttributesUtils.getAttributes("YNL216W (pd) YIL069C", edgeAttr);
		rangeVal = pm.calculateRangeValue(bundle);
		
		assertNotNull(rangeVal);
		assertEquals(rangeVal.getClass(), Float.class);
		assertEquals(10.0f, rangeVal);
		
		bundle = CyAttributesUtils.getAttributes("YNL216W (pd) YAL038W", edgeAttr);
		rangeVal = pm.calculateRangeValue(bundle);
		
		assertNull(rangeVal);
	}

}
