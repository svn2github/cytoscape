package org.cytoscape.property;

import static org.junit.Assert.assertTrue;
import java.util.Properties;
import org.junit.Test;

public class BasicCyPropertyTest { 

	@Test(expected=NullPointerException.class)
	public void testNullProp() throws Exception {
		BasicCyProperty p = new BasicCyProperty(null);
	}
	
	@Test
	public void testGetProp(){
		Properties props = new Properties();
		BasicCyProperty p = new BasicCyProperty(props);
		assertTrue(p.getProperties() != null);
	}	
}
