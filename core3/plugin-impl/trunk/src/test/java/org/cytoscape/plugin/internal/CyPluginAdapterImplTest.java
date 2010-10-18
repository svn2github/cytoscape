package org.cytoscape.plugin.internal;

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
import junit.framework.*;

import static org.mockito.Mockito.*;

public class CyPluginAdapterImplTest extends TestCase {
	
	CyPluginAdapterImpl adapter;
	
	private void defaultSetUp() {

		CyTableFactory cyTableFactory = mock(CyTableFactory.class);
		CyEventHelper cyEventHelper = mock(CyEventHelper.class);
		CyLayouts cyLayouts = mock(CyLayouts.class);
		CyNetworkFactory cyNetworkFactory = mock(CyNetworkFactory.class);
		CyNetworkManager cyNetworkManager = mock(CyNetworkManager.class);
		CyNetworkViewFactory cyNetworkViewFactory = mock(CyNetworkViewFactory.class);
		CyRootNetworkFactory cyRootNetworkFactory = mock(CyRootNetworkFactory.class);
		CySessionManager cySessionManager = mock(CySessionManager.class);
		RenderingEngineFactory presentationFactory = mock(RenderingEngineFactory.class);
		TaskManager taskManager = mock(TaskManager.class);
		VisualMappingManager visualMappingManager = mock(VisualMappingManager.class);

		adapter = new CyPluginAdapterImpl( 
                cyTableFactory,
                cyEventHelper,
                cyLayouts,
             	cyNetworkFactory,
             	cyNetworkManager,
             	cyNetworkViewFactory,
             	cyRootNetworkFactory,
             	cySessionManager,
             	presentationFactory,
             	taskManager,
             	visualMappingManager
			    );
	}
	
	@Test
	public void testGetCyDataTableFactory(){ 
		defaultSetUp();
		assertNotNull("dataTable exists",adapter.getCyDataTableFactory());
		assertTrue("CyTableFactory is expected", adapter.getCyDataTableFactory() instanceof CyTableFactory);
	}
	
	@Test
	public void testGetCyEventHelper() 
	{ 
		defaultSetUp();
		assertNotNull("dataTable exists",adapter.getCyEventHelper());
		assertTrue("CyEventHelper is expected", adapter.getCyEventHelper() instanceof CyEventHelper);
	} 

	@Test
	public void testGetCyLayouts() 
	{ 
		defaultSetUp();
		assertNotNull("CyLayouts exists",adapter.getCyLayouts());
		assertTrue("CyLayouts is expected", adapter.getCyLayouts()instanceof CyLayouts);
	} 

	@Test
	public void testGetCyNetworkFactory() 
	{ 
		defaultSetUp();
		assertNotNull("CyLayouts exists",adapter.getCyNetworkFactory());
		assertTrue("CyLayouts is expected", adapter.getCyNetworkFactory()instanceof CyNetworkFactory);
	}

	@Test
	public void testGetCyNetworkManager() 
	{ 
		defaultSetUp();
		assertNotNull("CyNetworkManager exists",adapter.getCyNetworkManager());
		assertTrue("CyNetworkManger is expected", adapter.getCyNetworkManager()instanceof CyNetworkManager);
	} 

	@Test
	public void testGetCyNetworkViewFactory() 
	{
		defaultSetUp();
		assertNotNull("CyLayouts exists",adapter.getCyNetworkViewFactory());
		assertTrue("CyNetworkViewFactory is expected", adapter.getCyNetworkViewFactory()instanceof CyNetworkViewFactory);
	}

	@Test
	public void testGetCyRootNetworkFactory() 
	{
		defaultSetUp();
		assertNotNull("CyLayouts exists",adapter.getCyRootNetworkFactory());
		assertTrue("CyRootNetworkFactory is expected", adapter.getCyRootNetworkFactory()instanceof CyRootNetworkFactory);
	} 

	@Test
	public void testGetCySessionManager() 
	{ 
		defaultSetUp();
		assertNotNull("CyLayouts exists",adapter.getCySessionManager());
		assertTrue("CySessionManager is expected", adapter.getCySessionManager()instanceof CySessionManager);
	} 

	@Test
	public void testGetPresentationFactory() 
	{ 
		defaultSetUp();
		assertNotNull("PresentationFactory exists",adapter.getPresentationFactory());
		assertTrue("PresentationFactory is expected", adapter.getPresentationFactory()instanceof RenderingEngineFactory);
	}

	@Test
	public void testGetTaskManager() 
	{ 
		defaultSetUp();
		assertNotNull("TaskManager exists",adapter.getTaskManager());
		assertTrue("TaskManager is expected", adapter.getTaskManager()instanceof TaskManager);
	}

	@Test
	public void testGetVisualMappingManager() 
	{ 
		defaultSetUp();
		assertNotNull("VisualMappingManager exists",adapter.getVisualMappingManager());
		assertTrue("VisualMappingManager is expected", adapter.getVisualMappingManager()instanceof VisualMappingManager);
	} 
}
