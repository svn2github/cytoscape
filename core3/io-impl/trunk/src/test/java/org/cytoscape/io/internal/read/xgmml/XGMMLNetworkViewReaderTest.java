package org.cytoscape.io.internal.read.xgmml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.cytoscape.io.internal.read.AbstractNetworkViewReaderTester;
import org.cytoscape.io.internal.read.xgmml.handler.AttributeValueUtil;
import org.cytoscape.io.internal.read.xgmml.handler.ReadDataManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.property.CyProperty;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.DiscreteMappingFactory;
import org.junit.Before;
import org.junit.Test;

public class XGMMLNetworkViewReaderTest extends AbstractNetworkViewReaderTester {

    RenderingEngineManager renderingEngineManager;
    ReadDataManager readDataManager;
    AttributeValueUtil attributeValueUtil;
    VisualStyleFactory styleFactory;
    VisualMappingManager visMappingManager;
    DiscreteMappingFactory discreteMappingFactory;
    XGMMLParser parser;
    CyProperty<Properties> properties;

    @Before
    public void setUp() throws Exception {
	super.setUp();
	properties = mock(CyProperty.class);
	when(properties.getProperties()).thenReturn(new Properties());
	renderingEngineManager = mock(RenderingEngineManager.class);
	when(renderingEngineManager.getDefaultVisualLexicon()).thenReturn(
		new MinimalVisualLexicon(new NullVisualProperty("MINIMAL_ROOT", "Minimal Root Visual Property")));
	visMappingManager = mock(VisualMappingManager.class);
	VisualStyle defVisualStyle = mock(VisualStyle.class);
	styleFactory = mock(VisualStyleFactory.class);
	when(styleFactory.getInstance(defVisualStyle)).thenReturn(defVisualStyle);
	when(visMappingManager.getDefaultVisualStyle()).thenReturn(defVisualStyle);
	readDataManager = new ReadDataManager();
	ObjectTypeMap objectTypeMap = new ObjectTypeMap();
	attributeValueUtil = new AttributeValueUtil(objectTypeMap, readDataManager);
	HandlerFactory handlerFactory = new HandlerFactory(readDataManager, attributeValueUtil);
	parser = new XGMMLParser(handlerFactory, readDataManager);
    }

    @Test
    public void testReadFromTypicalFile() throws Exception {

	CyNetworkView[] views = getViews("galFiltered.xgmml");
	CyNetwork net = checkSingleNetwork(views, 331, 362);

	findInteraction(net, "YGR136W", "YGR058W", "pp", 1);

	// Test low threshold
	this.viewThreshold = 5;
	CyNetworkView[] nullViews = getViews("galFiltered.xgmml");
	assertNotNull(nullViews);
	assertEquals(1, nullViews.length);
	assertTrue(nullViews[0].isEmptyView());
	this.viewThreshold = this.DEF_THRESHOLD;
    }

    private CyNetworkView[] getViews(String file) throws Exception {
	File f = new File("./src/test/resources/testData/xgmml/" + file);
	XGMMLNetworkViewReader snvp = new XGMMLNetworkViewReader(new FileInputStream(f), renderingEngineManager,
		viewFactory, netFactory, readDataManager, attributeValueUtil, styleFactory, visMappingManager, parser,
		properties);
	snvp.run(taskMonitor);

	return snvp.getNetworkViews();
    }
}
