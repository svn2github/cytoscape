package org.cytoscape.task.internal.session;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.work.TaskMonitor;
import org.junit.Test;

public class NewSessionTaskTest {

	@Test
	public void testRun() throws Exception {
		TaskMonitor tm = mock(TaskMonitor.class);
		CySessionManager mgr = mock(CySessionManager.class);;

		NewSessionTask t = new NewSessionTask(mgr);
		
		t.run(tm);

		verify(mgr, times(1)).setCurrentSession(null, null);
	}
}
