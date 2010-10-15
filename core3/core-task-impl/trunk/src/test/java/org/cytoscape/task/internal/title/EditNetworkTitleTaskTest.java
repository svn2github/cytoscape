package org.cytoscape.task.internal.title;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskMonitor;
import org.junit.Test;
import org.cytoscape.model.CyRow;
import static org.mockito.Mockito.*;

public class EditNetworkTitleTaskTest {

	@Test
	public void testRun() throws Exception {
		CyNetwork net = mock(CyNetwork.class);
		TaskMonitor tm = mock(TaskMonitor.class);

		CyRow r1 =  mock(CyRow.class);

		when(net.attrs()).thenReturn(r1);
		when(r1.get("name",String.class)).thenReturn("title");
					
		EditNetworkTitleTask t = new EditNetworkTitleTask(net);
		
		t.run(tm);
		
		verify(r1, times(1)).set("name", "title");

	}
}
