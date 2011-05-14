package org.cytoscape.task.internal.hide;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.junit.Test;

public class UnHideAllNodesTaskFactoryTest {
	@Test
	public void testRun() throws Exception {

		CyNetworkView view = mock(CyNetworkView.class);

		UnHideAllNodesTaskFactory factory = new UnHideAllNodesTaskFactory();
		factory.setNetworkView(view);
		
		TaskIterator ti = factory.getTaskIterator();
		assertNotNull(ti);
		
		assertTrue( ti.hasNext() );
		Task t = ti.next();
		assertNotNull( t );				
	}
}
