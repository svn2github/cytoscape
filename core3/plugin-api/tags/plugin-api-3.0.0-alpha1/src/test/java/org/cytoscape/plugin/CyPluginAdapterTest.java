package org.cytoscape.plugin;


import org.junit.Test;
import static org.junit.Assert.*;


public abstract class CyPluginAdapterTest {
	protected CyPluginAdapter adapter;
	
	@Test
	public void testGetCyTableFactory() { 
		assertNotNull("dataTable exists", adapter.getCyTableFactory());
	}
	
	@Test
	public void testGetCyEventHelper() { 
		assertNotNull("dataTable exists", adapter.getCyEventHelper());
	} 

// TODO once layout api has stabilized
//	@Test
//	public void testGetCyLayouts() { 
//		assertNotNull("CyLayouts exists", adapter.getCyLayouts());
//	} 

	@Test
	public void testGetCyNetworkFactory() { 
		assertNotNull("CyNetworkFactory exists", adapter.getCyNetworkFactory());
	}

	@Test
	public void testGetCyNetworkManager() { 
		assertNotNull("CyNetworkManager exists", adapter.getCyNetworkManager());
	} 

	@Test
	public void testGetCyNetworkViewFactory() {
		assertNotNull("CyNetworkViewFactory exists", adapter.getCyNetworkViewFactory());
	}

	@Test
	public void testGetCyRootNetworkFactory() {
		assertNotNull("CyRootNetworkFactory exists", adapter.getCyRootNetworkFactory());
	} 

	@Test
	public void testGetCySessionManager() { 
		assertNotNull("CySessionManager exists", adapter.getCySessionManager());
	} 

	@Test
	public void testGetPresentationFactory() { 
		assertNotNull("PresentationFactory exists", adapter.getPresentationFactory());
	}

	@Test
	public void testGetTaskManager() { 
		assertNotNull("TaskManager exists", adapter.getTaskManager());
	}

	@Test
	public void testGetVisualMappingManager() { 
		assertNotNull("VisualMappingManager exists", adapter.getVisualMappingManager());
	} 

	@Test
	public void testGetCyNetworkViewManager() { 
		assertNotNull("NetworkViewManager exists", adapter.getCyNetworkViewManager());
	} 

	@Test
	public void testGetCyApplicationManager() { 
		assertNotNull("CyApplicationManager exists", adapter.getCyApplicationManager());
	} 
}
