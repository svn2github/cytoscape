/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.Visualization;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JComponent;

import cern.colt.list.IntArrayList;

/**
 * Class VisualizationLayers
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class VisualizationLayers extends VisualizationProxy {
    protected ArrayList         layer;
    protected IntArrayList      layerRank;
    public final static int RULER_LAYER      = 0;
    public final static int MAIN_LAYER       = 100;
    public final static int MAGIC_LENS_LAYER = 200;
    public final static int DRAG_LAYER       = 300;

    public VisualizationLayers(Visualization visualization) {
        super(visualization);
    }

    protected ArrayList getLayer() {
        if (layer == null) {
            layer = new ArrayList();
            layerRank = new IntArrayList();
        }
        return layer;
    }
    
    protected IntArrayList getLayerRank() {
        if (layer == null) {
            layer = new ArrayList();
            layerRank = new IntArrayList();
        }
        return layerRank;
    }

    public int size() {
        return getLayer().size();
    }

    public void add(Visualization vis) {
        add(vis, MAIN_LAYER, -1);
    }

    public void add(Visualization vis, int layer) {
        add(vis, layer, -1);
    }

    public void add(Visualization vis, int l, int pos) {
        pos = insertIndexForLayer(l, pos);
        layer.add(pos, vis);
        layerRank.beforeInsert(pos, l);
        repaint();
    }

    public void remove(int index) {
        if (index < 0)
            return;
        Visualization vis = getVisualization(index);
        if (vis == visualization) {
            throw new RuntimeException("cannot remove main visualization");
        }
        layer.remove(index);
        layerRank.remove(index);
        repaint();
    }

    public int insertIndexForLayer(int layer, int pos) {
        int index = getLayerRank().binarySearch(layer);
        if (index < 0) {
            return -index - 1;
        }
        if (pos == -1) {
            int last = layerRank.size()-1;
            while(index != last 
                    && layerRank.get(index+1) == layer) {
                index++;
            }
        }
        else {
            while(index != 0 
                    && layerRank.get(index-1) == layer) {
                index--;
            }
        }
        return index;
    }

    public int indexOf(Visualization visualization) {
        if (layer == null)
            return -1;
        return layer.indexOf(visualization);
    }

    public int getLayer(Visualization vis) {
        int index = indexOf(vis);
        if (index == -1) return -1;
        return layerRank.get(index);
    }

    public void dispose() {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            vis.dispose();
        }
        layer.clear();
        layerRank.clear();
    }

    public Visualization findVisualization(Class cls) {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            Visualization v = vis.findVisualization(cls);
            if (v != null) {
                return v;
            }
        }
        return null;
    }

    public Visualization getVisualization(int index) {
        if (index >= size())
            return null;
        return (Visualization) layer.get(index);
    }

    public void invalidate() {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            vis.invalidate();
        }
    }

    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            vis.paint(graphics, bounds);
        }
    }

    public Set pickAll(Rectangle2D hitBox, Rectangle2D bounds, Set pick) {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            pick = vis.pickAll(hitBox, bounds, pick);
        }
        return pick;
    }

    public void print(Graphics2D graphics, Rectangle2D bounds) {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            vis.print(graphics, bounds);
        }
    }

    public void setParent(JComponent parent) {
        int i = 0;
        for (Visualization vis = getVisualization(i++); vis != null; vis = getVisualization(i++)) {
            vis.setParent(parent);
        }
    }

    public void setVisualization(Visualization vis) {
        if (visualization == vis)
            return;
        int index = indexOf(visualization);
        remove(index);
        visualization = vis;
        add(vis);
        invalidate();
    }
}
