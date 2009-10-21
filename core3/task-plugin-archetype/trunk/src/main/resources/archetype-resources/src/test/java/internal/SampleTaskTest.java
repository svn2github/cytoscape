package ${package}.internal;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyDataTableUtil;
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.test.support.NetworkTestSupport;

/**
 * Unit test for SampleTask 
 */
public class SampleTaskTest {

	SampleTask sampleTask;
	TaskMonitor taskMonitor;
	CyNetwork network;
	NetworkTestSupport support;

	public SampleTaskTest() {
		support = new NetworkTestSupport();
	}
		

	@Before
	public void setUp() {
		network = support.getNetwork(); 

		// normal setup
		network.addNode();
		network.addNode();
		network.addNode();
		network.addNode();
		network.addNode();
		sampleTask = new SampleTask(network);
		sampleTask.sleepTime = 10; // to speed things along!
		taskMonitor = new DummyTaskMonitor();
	}

	@Test
    public void testLowerBound() {
		checkSelectedNodes(0); 
	}

	@Test
    public void testMiddle() {
		checkSelectedNodes(3); 
	}

	@Test
    public void testUpperBound() {
		checkSelectedNodes(5);
    }

	@Test(expected=IllegalArgumentException.class)
	public void testLessThanZero() {
		checkSelectedNodes(-1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGreaterThanNumNodes() {
		checkSelectedNodes(100);
	}

	private void checkSelectedNodes(final int x) {
		sampleTask.numNodesToSelect = x;
		sampleTask.run( taskMonitor );
        assertEquals("num selected nodes",x,
		             CyDataTableUtil.getNodesInState(network,"selected",true).size());
	}

	// Instead of writing your own dummy object, you could use a
	// mocking framework like JMock, EasyMock, or Mockito.
	private class DummyTaskMonitor implements TaskMonitor {
		public void setTitle(String title) {
			assertNotNull(title);
			assertTrue(title.length()>0);
		}
		public void setProgress(double progress) {
			assertTrue(progress >= 0.0d);
			assertTrue(progress <= 1.0d);
		}
		public void setStatusMessage(String statusMessage) {
			assertNotNull(statusMessage);
			assertTrue(statusMessage.length()>0);
		}
	}
}
