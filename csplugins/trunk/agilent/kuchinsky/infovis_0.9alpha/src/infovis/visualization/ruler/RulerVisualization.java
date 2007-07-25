/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.ruler;

import infovis.Column;
import infovis.Visualization;
import infovis.column.ShapeColumn;
import infovis.visualization.DefaultVisualization;
import infovis.visualization.StrokingVisualization;
import infovis.visualization.render.VisualAlpha;
import infovis.visualization.render.VisualSize;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

/**
 * Display rulers under a Visualization
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public class RulerVisualization extends StrokingVisualization 
    implements PropertyChangeListener {
    public static final String PROPERTY_VISIBLE = "PROPERTY_VISIBLE";
    
    protected static Font   labelFont = new Font("Dialog", Font.BOLD, 15);
    protected static Font   tickFont  = new Font("Dialog", Font.PLAIN, 12);
    protected Visualization visualization;
    protected boolean visible = true;

    public RulerVisualization(Visualization vis) {
        super(vis.getRulerTable());
        this.visualization = vis;
        VisualSize vs = VisualSize.get(this);
        if (vs != null) {
            vs.setRescaling(false);
        }
        VisualAlpha va = VisualAlpha.get(this);
        if (va != null) {
            va.setDefaultAlpha(0.5);
        }

        vis.addPropertyChangeListener(PROPERTY_RULERS, this);
    }
    
    public void validateShapes(Rectangle2D bounds) {
        if (visualization instanceof DefaultVisualization) {
            DefaultVisualization vis = (DefaultVisualization) visualization;
            vis.validateShapes(bounds);
        }
        super.validateShapes(bounds);
    }
    
    public void computeShapes(Rectangle2D bounds) {
        this.shapes = ShapeColumn.getColumn(
                getTable(), 
                RulerTable.SHAPE_COLUMN);
        for (Iterator iter = getVisualColumnIterator(); iter.hasNext(); ) {
            String vname = (String)iter.next();
            
            Column c = getTable().getColumn(vname);
            if (c != null) {
                setVisualColumn(vname, c);
            }
        }        
    }
    
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        if (isVisible()) {
            super.paint(graphics, bounds);
        }
    }

    public Dimension getPreferredSize() {
        return visualization.getPreferredSize();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PROPERTY_RULERS)) {
            invalidate();
        }
    }

    public static RulerVisualization find(Visualization vis) {
        RulerVisualization ruler = null;
        if (vis instanceof RulerVisualization) {
            ruler = (RulerVisualization) vis;
        }
        else
            for (int i = 0; vis.getVisualization(i) != null; i++) {
                ruler = find(vis.getVisualization(i));
                if (ruler != null) {
                    break;
                }
            }
        return ruler;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            firePropertyChange(PROPERTY_VISIBLE, !visible, visible);
            repaint();
        }
    }


}