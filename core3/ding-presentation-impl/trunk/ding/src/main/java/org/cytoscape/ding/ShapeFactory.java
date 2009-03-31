
package org.cytoscape.ding;

import cytoscape.render.immed.GraphGraphics;
import org.cytoscape.ding.impl.GinyUtil;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ShapeFactory {

    public static Map<Integer, Shape> getNodeShapes() {
        final Map<Byte, Shape> nodeShapes = GraphGraphics.getNodeShapes();
        final Map<Integer, Shape> ginyKeyShapes = new HashMap<Integer, Shape>();

        Shape shape;

        for (Byte key : nodeShapes.keySet()) {
            shape = nodeShapes.get(key);
            ginyKeyShapes.put(GinyUtil.getGinyNodeType(key), shape);
        }

        return ginyKeyShapes;
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Map<Integer, Shape> getArrowShapes() {
        final Map<Byte, Shape> arrowShapes = GraphGraphics.getArrowShapes();
        final Map<Integer, Shape> ginyKeyShapes = new HashMap<Integer, Shape>();

        Shape shape;

        for (Byte key : arrowShapes.keySet()) {
            shape = arrowShapes.get(key);
            ginyKeyShapes.put(GinyUtil.getGinyArrowType(key), shape);
        }

        return ginyKeyShapes;
    }
}
