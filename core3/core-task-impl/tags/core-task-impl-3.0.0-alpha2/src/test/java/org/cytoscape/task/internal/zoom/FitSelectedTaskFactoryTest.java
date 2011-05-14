package org.cytoscape.task.internal.zoom;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FitSelectedTaskFactoryTest {
	@Test
	public void testGetTaskIterator() {
		
		CyNetworkView view = mock(CyNetworkView.class);
		
		FitSelectedTaskFactory factory = new FitSelectedTaskFactory();
		factory.setNetworkView(view);
		
		TaskIterator ti = factory.getTaskIterator();
		assertNotNull(ti);
		
		assertTrue( ti.hasNext() );
		Task t = ti.next();
		assertNotNull( t );		
	}
}
