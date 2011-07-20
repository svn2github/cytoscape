package org.cytoscape.task.internal.hide;

import org.cytoscape.task.AbstractNetworkViewTaskTest;
import org.cytoscape.test.support.NetworkViewTestSupport;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class HideSelectedNodesTaskTest extends AbstractNetworkViewTaskTest {
	
	private final NetworkViewTestSupport viewSupport = new NetworkViewTestSupport();
	private CyNetworkView view = viewSupport.getNetworkView();
	@Mock TaskMonitor tm;
	
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testHideSelectedEdgesTask() throws Exception {
		
		final HideSelectedNodesTask task = new HideSelectedNodesTask(view);
		task.run(tm);
	}

}
