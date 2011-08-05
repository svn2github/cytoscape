
package org.cytoscape.taglets.compatibility;

import org.junit.Before;
import java.util.Map;

public class FinalClassTagletTest extends AbstractTagletTester {
	@Before
	public void setup() {
		taglet = new FinalClassTaglet();
	}

	void doRegister(Map map) {
		FinalClassTaglet.register(map);
	}
}
