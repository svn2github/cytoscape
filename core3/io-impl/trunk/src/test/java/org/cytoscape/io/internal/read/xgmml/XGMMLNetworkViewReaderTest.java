package org.cytoscape.io.internal.read.xgmml;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import java.util.List;
import java.util.Properties;

import static org.mockito.Mockito.*;

import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.property.CyProperty;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.DiscreteMappingFactory;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.view.layout.CyLayoutAlgorithm;

import org.cytoscape.io.internal.read.xgmml.handler.ReadDataManager;
import org.cytoscape.io.internal.read.xgmml.handler.AttributeValueUtil;

import org.cytoscape.io.internal.read.AbstractNetworkViewReaderTester;

public class XGMMLNetworkViewReaderTest extends AbstractNetworkViewReaderTester {

    ReadDataManager        readDataManager;
    AttributeValueUtil     attributeValueUtil;
    VisualStyleFactory     styleFactory;
    VisualMappingManager   visMappingManager;
    DiscreteMappingFactory discreteMappingFactory;
    XGMMLParser            parser;
    CyProperty<Properties> properties;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        readDataManager = new ReadDataManager();
        ObjectTypeMap objectTypeMap = new ObjectTypeMap();
        attributeValueUtil = new AttributeValueUtil(objectTypeMap, readDataManager);
        HandlerFactory handlerFactory = new HandlerFactory(readDataManager, attributeValueUtil);
        parser = new XGMMLParser(handlerFactory, readDataManager);
        // TODO
        // properties = new Properties();
    }

    @Test
    public void testReadFromTypicalFile() throws Exception {
        // TODO
        // CyNetworkView[] views = getViews("galFiltered.xgmml");
        // CyNetwork net = checkSingleNetwork(views, 331, 362);

        // for ( CyNode n : net.getNodeList() ) {
        // System.out.print(n.attrs().get("name",String.class) + " - ");
        // for ( CyNode nn : net.getNeighborList(n,CyEdge.Type.OUTGOING)) {
        // System.out.print(" " + nn.attrs().get("name",String.class));
        // System.out.print("[");
        // for ( CyEdge e : net.getConnectingEdgeList(n,nn,CyEdge.Type.ANY))
        // System.out.print(e.attrs().get("interaction",String.class)+",");
        // System.out.print("]");
        // }
        // System.out.println();
        // }

        // findInteraction(net, "YGR136W", "YGR058W", "pp", 1);
    }

    private CyNetworkView[] getViews(String file) throws Exception {
        File f = new File("./src/test/resources/testData/xgmml/" + file);
        XGMMLNetworkViewReader snvp = new XGMMLNetworkViewReader(new FileInputStream(f), viewFactory, netFactory,
                                                                 readDataManager, attributeValueUtil, styleFactory,
                                                                 visMappingManager, discreteMappingFactory, parser,
                                                                 properties);
        snvp.run(taskMonitor);

        return snvp.getNetworkViews();
    }
}
