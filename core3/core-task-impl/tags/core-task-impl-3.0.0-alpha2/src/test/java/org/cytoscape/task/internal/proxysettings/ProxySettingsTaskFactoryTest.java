package org.cytoscape.task.internal.proxysettings;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.junit.Test;
import org.cytoscape.work.TaskManager;
import org.cytoscape.io.util.StreamUtil;

public class ProxySettingsTaskFactoryTest {
	@Test
	public void testRun() throws Exception {

		TaskManager taskManager = mock(TaskManager.class);;
		StreamUtil streamUtil = mock(StreamUtil.class);

		ProxySettingsTaskFactory factory = new ProxySettingsTaskFactory(taskManager, streamUtil);
		
		TaskIterator ti = factory.getTaskIterator();
		assertNotNull(ti);
		
		assertTrue( ti.hasNext() );
		Task t = ti.next();
		assertNotNull( t );				
	}	
}
