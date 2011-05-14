package org.cytoscape.task.internal.zoom;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.task.internal.zoom.FitContentTaskFactory;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class FitContentTaskTest {

	@Test
	public void testRun() throws Exception {
		CyNetworkView view = mock(CyNetworkView.class);
				
		TaskMonitor tm = mock(TaskMonitor.class);
		
		FitContentTask t = new FitContentTask(view);
		
		t.run(tm);
		
		verify(view, times(1)).fitContent();
	}
	
	@Test(expected=Exception.class)
	public void testNullView() throws Exception {
		CyNetworkView view = null;
				
		TaskMonitor tm = mock(TaskMonitor.class);
		
		FitContentTask t = new FitContentTask(view);
		
		t.run(tm);
	}
}
