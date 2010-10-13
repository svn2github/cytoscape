package org.cytoscape.task.internal.zoom;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ZoomInTaskFactoryTest {

	@Test
	public void testGetTaskIterator() {
		
		CyNetworkView view = mock(CyNetworkView.class);
		
		ZoomInTaskFactory factory = new ZoomInTaskFactory();
		factory.setNetworkView(view);
		
		TaskIterator ti = factory.getTaskIterator();
		assertNotNull(ti);
		
		assertTrue( ti.hasNext() );
		Task t = ti.next();
		assertNotNull( t );		
	}
	
}
