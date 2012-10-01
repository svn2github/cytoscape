
package org.cytoscape.taglets.compatibility;

import org.junit.Before;
import java.util.Map;

public class SpiInterfaceTagletTest extends AbstractTagletTester {
	@Before
	public void setup() {
		taglet = new SpiInterfaceTaglet();
	}

	void doRegister(Map map) {
		SpiInterfaceTaglet.register(map);
	}
}
