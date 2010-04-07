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
	
	protected void setUp() throws Exception {
		super.setUp();
		
		nodeAttr = Cytoscape.getNodeAttributes();
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
		
		nodeAttr.setAttribute("?", "Color", color1.getRed() + "," + color1.getGreen() + "," + color1.getBlue());
		nodeAttr.setAttribute("YNL050C", "Color", color2.getRed() + "," + color2.getGreen() + "," + color2.getBlue());
		nodeAttr.setAttribute("YIL061C", "Color", "300,-1,10");
		
		final ObjectMapping pm = new PassThroughMapping(Color.class, "Color");
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

	}

}
