
package org.cytoscape.view.model.internal;

import org.cytoscape.view.model.AbstractViewTest;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.internal.CyNetworkFactoryImpl;
import org.cytoscape.event.CyEventHelper;
import org.junit.Before;

import static org.easymock.EasyMock.*;

public class ViewTest extends AbstractViewTest {

	private Object source;
	private CyNetwork net;
	private CyEventHelper eh;
	private ColumnOrientedNetworkViewImpl netview;
	private CyNetworkFactoryImpl netfact;

	@Before
	public void setup() {
		source = new Object();
		eh = createMock(CyEventHelper.class);
		netfact = new CyNetworkFactoryImpl(eh);
		net = netfact.getInstance(); 
		netview = new ColumnOrientedNetworkViewImpl(eh,net);

		// actually used in the unit test
		view = new ColumnOrientedViewImpl(source,netview);
	}
}
