package org.cytoscape.plugin.internal;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.plugin.CyPluginAdapterTest;

import static org.mockito.Mockito.*;
import org.junit.Before;

public class CyPluginAdapterImplTest extends CyPluginAdapterTest {

	@Before
	public void setUp() {

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
		CyNetworkViewManager networkViewManager = mock(CyNetworkViewManager.class);
		CyApplicationManager applicationManager = mock(CyApplicationManager.class);

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
             	visualMappingManager,
		networkViewManager,
		applicationManager
			    );
	}
	
}
