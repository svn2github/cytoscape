
package org.cytoscape.taglets.compatibility;

import org.junit.Before;
import java.util.Map;

public class AbstractClassTagletTest extends AbstractTagletTester {
	@Before
	public void setup() {
		taglet = new AbstractClassTaglet();
	}

	void doRegister(Map map) {
		AbstractClassTaglet.register(map);
	}
}
