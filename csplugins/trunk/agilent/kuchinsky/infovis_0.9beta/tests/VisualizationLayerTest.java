import infovis.Table;
import infovis.Visualization;
import infovis.table.DefaultTable;
import infovis.visualization.DefaultVisualization;
import infovis.visualization.VisualizationLayers;
import junit.framework.TestCase;

/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

/**
 * Class VisualizationLayerTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class VisualizationLayerTest extends TestCase {
    public VisualizationLayerTest(String name) {
        super(name);
    }
    
    public void testVisualizationLayer() {
        Table table = new DefaultTable();
        Visualization vis = new DefaultVisualization(table);
        VisualizationLayers layer = new VisualizationLayers(vis);
        assertTrue("Unexpected main vis", vis==layer.getVisualization());
        assertEquals(VisualizationLayers.MAIN_LAYER, layer.getLayer(vis));
        
    }

}
