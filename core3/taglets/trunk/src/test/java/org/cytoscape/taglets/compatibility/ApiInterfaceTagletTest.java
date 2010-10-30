
package org.cytoscape.taglets.compatibility;

import org.junit.Before;
import java.util.Map;

public class ApiInterfaceTagletTest extends AbstractTagletTester {
	@Before
	public void setup() {
		taglet = new ApiInterfaceTaglet();
	}

	void doRegister(Map map) {
		ApiInterfaceTaglet.register(map);
	}
}
