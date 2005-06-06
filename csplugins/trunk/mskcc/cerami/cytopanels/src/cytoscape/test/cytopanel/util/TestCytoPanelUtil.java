package cytoscape.test.cytopanel.util;

import cytoscape.cytopanel.util.CytoPanelUtil;
import junit.framework.TestCase;

import javax.swing.*;
import java.awt.*;

public class TestCytoPanelUtil extends TestCase {

    public void testLocationOfExternalFrame1() {
        Dimension screenDimension = new Dimension(1680, 1050);
        Rectangle containerBounds = new Rectangle(400, 300, 800, 500);
        Dimension preferredSize = new Dimension(200, 100);

        //  Test West Panel
        Point p1 = CytoPanelUtil.getLocationOfExternalFrame(screenDimension,
                containerBounds, preferredSize, SwingConstants.WEST, false);
        assertEquals(195, p1.x);
        assertEquals(300, p1.y);

        //  Test East Panel
        Point p2 = CytoPanelUtil.getLocationOfExternalFrame(screenDimension,
                containerBounds, preferredSize, SwingConstants.EAST, false);
        assertEquals(1205, p2.x);
        assertEquals(300, p2.y);

        //  Test South West Panel
        Point p3 = CytoPanelUtil.getLocationOfExternalFrame(screenDimension,
                containerBounds, preferredSize, SwingConstants.SOUTH_WEST,
                false);
        assertEquals(400, p3.x);
        assertEquals(805, p3.y);

        //  Test South East Panel
        Point p4 = CytoPanelUtil.getLocationOfExternalFrame(screenDimension,
                containerBounds, preferredSize, SwingConstants.SOUTH_EAST,
                false);
        assertEquals(1000, p4.x);
        assertEquals(805, p4.y);

        //  Now test with oversized dimensions
        screenDimension = new Dimension(1024, 640);
        containerBounds = new Rectangle(0, 0, 1000, 600);

        //  Test West Panel
        Point p5 = CytoPanelUtil.getLocationOfExternalFrame(screenDimension,
                containerBounds, preferredSize, SwingConstants.WEST, false);
        assertEquals(0, p5.x);
        assertEquals(0, p5.y);

        //  Test East Panel
        Point p6 = CytoPanelUtil.getLocationOfExternalFrame(screenDimension,
                containerBounds, preferredSize, SwingConstants.EAST, false);
        assertEquals(824, p6.x);
        assertEquals(0, p6.y);

        //  Test South West Panel
        Point p7 = CytoPanelUtil.getLocationOfExternalFrame(screenDimension,
                containerBounds, preferredSize, SwingConstants.SOUTH_WEST,
                false);
        assertEquals(0, p7.x);
        assertEquals(540, p7.y);

        //  Test South East Panel
        Point p8 = CytoPanelUtil.getLocationOfExternalFrame(screenDimension,
                containerBounds, preferredSize, SwingConstants.SOUTH_EAST,
                false);
        assertEquals(800, p8.x);
        assertEquals(540, p8.y);
    }
}