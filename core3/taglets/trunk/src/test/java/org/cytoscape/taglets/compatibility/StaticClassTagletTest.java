
package org.cytoscape.taglets.compatibility;

import org.junit.Before;
import java.util.Map;

public class StaticClassTagletTest extends AbstractTagletTester {
	@Before
	public void setup() {
		taglet = new StaticClassTaglet();
	}

	void doRegister(Map map) {
		StaticClassTaglet.register(map);
	}
}
