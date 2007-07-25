/*******************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License * a
 * copy of which has been included with this distribution in the *
 * license-infovis.txt file. *
 ******************************************************************************/
package infovis.visualization;

import infovis.Column;
import infovis.Visualization;
import infovis.column.ShapeColumn;
import infovis.table.DefaultTable;
import infovis.utils.RowIterator;
import infovis.visualization.render.*;
import infovis.visualization.ruler.RulerTable;

import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D.Double;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.JComponent;

public class DefaultAxisVisualization extends StrokingVisualization implements
        PropertyChangeListener {
    public static final String PROPERTY_SIZE = "PROPERTY_SIZE";
    protected Visualization visualization;
    protected double ticksize = 0;
    protected double tickRatio = 0.25;
    protected int preferedsize = 100;
    
    private static Dimension MAXSIZE = new Dimension(5000, 50000); 

    /**
     * Constructor for DefaultAxisVisualization
     * 
     * @param vis the Visualization holding the Table of rulers
     * @param orientation the orientation of the component
     */
    public DefaultAxisVisualization(Visualization vis, short orientation) {
        this(vis, orientation, null);
    }

    /**
     * Constructor for DefaultAxisVisualization
     * 
     * @param vis the Visualization holding the Table of rulers
     * @param orientation the orientation of the component
     * @param ir the ItemRenderer to use for that visualization
     */
    public DefaultAxisVisualization(Visualization vis, short orientation,
            ItemRenderer ir) {
        super((vis.getRulerTable() == null) 
                ? new DefaultTable() 
                        : vis.getRulerTable(), ir);
        this.visualization = vis;
        this.setOrientation(orientation);
        vis.addPropertyChangeListener(PROPERTY_RULERS, this);
    }
    
    public void setPreferedsize(int preferedsize) {
        if (this.preferedsize == preferedsize) return;
        int old = this.preferedsize;
        this.preferedsize = preferedsize;
        invalidate();
        firePropertyChange(PROPERTY_SIZE, old, preferedsize);
    }
    
    public void setTickRatio(double tickRatio) {
        if (this.tickRatio == tickRatio) return;
        this.tickRatio = tickRatio;
        invalidate();
    }

    /**
     * @param rulers
     * @return
     */
    protected void computeRulers() {
        getShapes().clear();
        ShapeColumn shapeColumn = RulerTable.getShapes(getTable());
        switch (orientation) {
        case ORIENTATION_NORTH:
        case ORIENTATION_SOUTH:
            ticksize = getBounds().getHeight() * tickRatio;
            for (RowIterator iter = iterator(); iter.hasNext();) {
                int i = iter.nextRow();
                if (shapeColumn.isValueUndefined(i)) continue;
                Double line = (Double) shapeColumn.get(i);
                if (line.x1 == line.x2) {
                    setShapeAt(
                            i, 
                            new Line2D.Double(
                                    line.x1, 
                                    getBounds().getMaxY() - ticksize,
                                    line.x2,
                                    getBounds().getMaxY()));
                }
            }
            break;
        case ORIENTATION_EAST:
        case ORIENTATION_WEST:
        default:
            ticksize = getBounds().getWidth() * tickRatio;
            for (RowIterator iter = iterator(); iter.hasNext();) {
                int i = iter.nextRow();
                if (shapeColumn.isValueUndefined(i)) continue;
                Double line = (Double) shapeColumn.get(i);
                if (line.y1 == line.y2) {
                    setShapeAt(
                            i,
                            new Line2D.Double(
                                    getBounds().getMaxX() - ticksize,
                                    line.y1, 
                                    getBounds().getMaxX(), 
                                    line.y2));
                }
            }
            break;
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PROPERTY_RULERS)) {
            invalidate();
        }
    }
    
    public void validateShapes(Rectangle2D bounds) {
        JComponent panel = visualization.getParent();
        visualization.validateShapes(new Rectangle2D.Float(0, 0, panel.getWidth(), panel.getHeight()));
        super.validateShapes(bounds);
    }
    

    public void computeShapes(Rectangle2D bounds) {
        VisualLabel vl = VisualLabel.get(this);
        
        if (vl != null && vl instanceof VisualStrokingLabel) {
            VisualStrokingLabel vsl = (VisualStrokingLabel) vl;
            vsl.setPosition(1);
            vsl.setOrientation(getOrientation());
            vsl.setShowingLabel(true);
        }
        VisualSize vs = VisualSize.get(this);
        if (vs != null) {
            vs.setRescaling(false);
        }
        for (Iterator iter = getVisualColumnIterator(); iter.hasNext(); ) {
            String vname = (String)iter.next();
            if (vname.equals("shape") || vname.equals("#shape")) continue;
            Column c = getTable().getColumn(vname);
            if (c != null) {
                setVisualColumn(vname, c);
            }
        }
        computeRulers();
    }

    public Dimension getPreferredSize() {
        Dimension pref = visualization.getPreferredSize();
        if (pref == null) {
            //FIXME: when the visualization doesn't specify a preferredSize,
            // we must provide something meaningful.
            // Since the visualization parent size is not computed yet,
            // we have no way to return the final size of the main viewport
            // so we return something not too large.
            pref = MAXSIZE;
        }
        if (Orientation.isVertical(orientation)) {
            return new Dimension(pref.width, (int) preferedsize);
        } else {
            return new Dimension((int) preferedsize, pref.height);
        }
    }

}