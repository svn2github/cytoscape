package org.cytoscape.task.internal.zoom;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.junit.Test;

public class ZoomOutTaskFactoryTest {

	@Test
	public void testGetTaskIterator() {
		
		CyNetworkView view = mock(CyNetworkView.class);
		
		ZoomOutTaskFactory factory = new ZoomOutTaskFactory();
		factory.setNetworkView(view);
		
		TaskIterator ti = factory.getTaskIterator();
		assertNotNull(ti);
		
		assertTrue( ti.hasNext() );
		Task t = ti.next();
		assertNotNull( t );		
	}
	
	
}
