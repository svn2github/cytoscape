package org.cytoscape.session.internal;

import org.cytoscape.session.AbstractCySessionTest;
import org.junit.Before;

public class CySessionTest extends AbstractCySessionTest {

	@Before
	public void setUp() {
		session = new CySessionImpl();
	}
}
