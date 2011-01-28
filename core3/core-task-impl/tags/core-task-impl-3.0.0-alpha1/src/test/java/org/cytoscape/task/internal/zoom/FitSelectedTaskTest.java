package org.cytoscape.task.internal.zoom;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class FitSelectedTaskTest {
	
	@Test
	public void testRun() throws Exception {
		CyNetworkView view = mock(CyNetworkView.class);
				
		TaskMonitor tm = mock(TaskMonitor.class);
		
		FitSelectedTask t = new FitSelectedTask(view);
		
		t.run(tm);
		
		verify(view, times(1)).fitSelected();
	}
	
	@Test(expected=Exception.class)
	public void testNullView() throws Exception {
		CyNetworkView view = null;
				
		TaskMonitor tm = mock(TaskMonitor.class);
		
		FitSelectedTask t = new FitSelectedTask(view);
		
		t.run(tm);
	}

}
