package org.cytoscape.task.internal.setcurren;

import static org.mockito.Mockito.*;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.task.internal.setcurrent.SetCurrentNetworkTask;
import org.junit.Test;
import java.util.HashSet;

public class SetCurrentNetworkTaskTest {

	@Test
	public void testRun() throws Exception {

		CyNetworkManager netmgr = mock(CyNetworkManager.class);;
		TaskMonitor tm= mock(TaskMonitor.class);
		CyNetwork net= mock(CyNetwork.class);
		
		HashSet<CyNetwork> netSet = new HashSet<CyNetwork>();
		netSet.add(net);
		
		when(netmgr.getNetworkSet()).thenReturn(netSet);

		SetCurrentNetworkTask t = new SetCurrentNetworkTask(netmgr);
		t.run(tm);

		verify(netmgr, times(1)).setCurrentNetwork(net.getSUID());
	}
}
