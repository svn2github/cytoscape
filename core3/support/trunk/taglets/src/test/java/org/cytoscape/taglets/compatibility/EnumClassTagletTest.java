
package org.cytoscape.taglets.compatibility;

import org.junit.Before;
import java.util.Map;

public class EnumClassTagletTest extends AbstractTagletTester {
	@Before
	public void setup() {
		taglet = new EnumClassTaglet();
	}

	void doRegister(Map map) {
		EnumClassTaglet.register(map);
	}
}
