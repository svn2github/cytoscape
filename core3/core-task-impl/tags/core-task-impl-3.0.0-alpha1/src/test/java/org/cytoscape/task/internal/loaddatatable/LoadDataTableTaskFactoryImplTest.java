package org.cytoscape.task.internal.loaddatatable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.junit.Test;

public class LoadDataTableTaskFactoryImplTest {
	@Test
	public void testRun() throws Exception {
		
		CyTableReaderManager mgr = mock(CyTableReaderManager.class);;

		LoadDataTableTaskFactoryImpl factory = new LoadDataTableTaskFactoryImpl(mgr);
		
		TaskIterator ti = factory.getTaskIterator();
		assertNotNull(ti);
		
		assertTrue( ti.hasNext() );
		Task t = ti.next();
		assertNotNull( t );				
	}	
}
