package org.cytoscape.task.internal.zoom;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NETWORK_SCALE_FACTOR;

public class ZoomInTaskTest {
	
	@Test
	public void testRun() throws Exception {
		CyNetworkView view = mock(CyNetworkView.class);
		TaskMonitor tm = mock(TaskMonitor.class);

		double curScaleFactor = 2.0;
		
		when(view.getVisualProperty(NETWORK_SCALE_FACTOR)).thenReturn(curScaleFactor);
				
		ZoomInTask t = new ZoomInTask(view);
		
		t.run(tm);

		verify(view, times(1)).setVisualProperty(NETWORK_SCALE_FACTOR,curScaleFactor*1.1);
	}
}
