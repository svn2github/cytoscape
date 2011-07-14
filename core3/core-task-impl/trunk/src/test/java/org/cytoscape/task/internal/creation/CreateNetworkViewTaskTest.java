package org.cytoscape.task.internal.creation;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.test.support.NetworkTestSupport;
import org.cytoscape.test.support.NetworkViewTestSupport;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskMonitor;
import org.junit.Test;


public class CreateNetworkViewTaskTest {
	
	private final NetworkTestSupport support = new NetworkTestSupport();
	private final NetworkViewTestSupport viewSupport = new NetworkViewTestSupport();
	
	private CyNetwork networkModel = support.getNetwork();
	private CyNetworkViewFactory viewFactory = viewSupport.getNetworkViewFactory();
	
	private CyNetworkViewManager networkViewManager = mock(CyNetworkViewManager.class);
	
	@Test
	public void testCreateNetworkViewTask() throws Exception {
		final TaskMonitor tm = mock(TaskMonitor.class);
		final CreateNetworkViewTask task = new CreateNetworkViewTask(networkModel, viewFactory, networkViewManager, null);
		
		task.run(tm);
		verify(networkViewManager, times(1)).addNetworkView(any(CyNetworkView.class));
	}

}
