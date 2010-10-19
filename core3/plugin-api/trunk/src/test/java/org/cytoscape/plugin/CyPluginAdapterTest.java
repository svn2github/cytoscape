package org.cytoscape.plugin;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskManager;
import org.junit.Test;

import junit.framework.TestCase;

public abstract class CyPluginAdapterTest extends TestCase {

	protected CyPluginAdapter adapter;
	
	@Test
	public void testGetCyDataTableFactory(){ 
		assertNotNull("dataTable exists",adapter.getCyDataTableFactory());
		assertTrue("CyTableFactory is expected", adapter.getCyDataTableFactory() instanceof CyTableFactory);
	}
	
	@Test
	public void testGetCyEventHelper() 
	{ 
		assertNotNull("dataTable exists",adapter.getCyEventHelper());
		assertTrue("CyEventHelper is expected", adapter.getCyEventHelper() instanceof CyEventHelper);
	} 

	@Test
	public void testGetCyLayouts() 
	{ 
		assertNotNull("CyLayouts exists",adapter.getCyLayouts());
		assertTrue("CyLayouts is expected", adapter.getCyLayouts()instanceof CyLayouts);
	} 

	@Test
	public void testGetCyNetworkFactory() 
	{ 
		assertNotNull("CyLayouts exists",adapter.getCyNetworkFactory());
		assertTrue("CyLayouts is expected", adapter.getCyNetworkFactory()instanceof CyNetworkFactory);
	}

	@Test
	public void testGetCyNetworkManager() 
	{ 
		assertNotNull("CyNetworkManager exists",adapter.getCyNetworkManager());
		assertTrue("CyNetworkManger is expected", adapter.getCyNetworkManager()instanceof CyNetworkManager);
	} 

	@Test
	public void testGetCyNetworkViewFactory() 
	{
		assertNotNull("CyLayouts exists",adapter.getCyNetworkViewFactory());
		assertTrue("CyNetworkViewFactory is expected", adapter.getCyNetworkViewFactory()instanceof CyNetworkViewFactory);
	}

	@Test
	public void testGetCyRootNetworkFactory() 
	{
		assertNotNull("CyLayouts exists",adapter.getCyRootNetworkFactory());
		assertTrue("CyRootNetworkFactory is expected", adapter.getCyRootNetworkFactory()instanceof CyRootNetworkFactory);
	} 

	@Test
	public void testGetCySessionManager() 
	{ 
		assertNotNull("CyLayouts exists",adapter.getCySessionManager());
		assertTrue("CySessionManager is expected", adapter.getCySessionManager()instanceof CySessionManager);
	} 

	@Test
	public void testGetPresentationFactory() 
	{ 
		assertNotNull("PresentationFactory exists",adapter.getPresentationFactory());
		assertTrue("PresentationFactory is expected", adapter.getPresentationFactory()instanceof RenderingEngineFactory);
	}

	@Test
	public void testGetTaskManager() 
	{ 
		assertNotNull("TaskManager exists",adapter.getTaskManager());
		assertTrue("TaskManager is expected", adapter.getTaskManager()instanceof TaskManager);
	}

	@Test
	public void testGetVisualMappingManager() 
	{ 
		assertNotNull("VisualMappingManager exists",adapter.getVisualMappingManager());
		assertTrue("VisualMappingManager is expected", adapter.getVisualMappingManager()instanceof VisualMappingManager);
	} 

}
