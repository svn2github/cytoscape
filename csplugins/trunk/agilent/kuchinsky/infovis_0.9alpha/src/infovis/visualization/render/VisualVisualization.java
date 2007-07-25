/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Column;
import infovis.Visualization;
import infovis.column.ObjectColumn;
import infovis.column.filter.NotTypedFilter;
import infovis.visualization.ItemRenderer;
import infovis.visualization.magicLens.Fisheye;

import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * Class VisualVisualization
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class VisualVisualization extends AbstractVisualColumn {
    public static final String VISUAL = "visualization";
    protected ObjectColumn visualizationColumn;
    protected transient Fisheye fisheye;

    public static VisualVisualization get(Visualization vis) {
        return (VisualVisualization) findNamed(VISUAL, vis);
    }

    public static VisualVisualization get(ItemRenderer ir) {
        return (VisualVisualization) findNamed(VISUAL, ir);
    }
    
    public VisualVisualization(String name) {
        super(name);
    }
    

    public VisualVisualization() {
        super(VISUAL);
    }

    public VisualVisualization(ItemRenderer child) {
        this();
        addRenderer(child);
        this.invalidate = true;
        this.filter = new NotTypedFilter(Visualization.class);
    }

    public Column getColumn() {
        return visualizationColumn;
    }
    
    public ObjectColumn getVisualizationColumn() {
        return visualizationColumn;
    }

    public void setColumn(Column column) {
        if (visualizationColumn == column)
            return;
        super.setColumn(column);
        visualizationColumn = (ObjectColumn) column;
        invalidate();
    }
    
    public Visualization getVisualizationAt(int row) {
        if (visualizationColumn == null
                || visualizationColumn.isValueUndefined(row))
            return null;
        Object vis = visualizationColumn.get(row);
        if (vis instanceof Visualization) {
            return (Visualization)vis;
        }
        return null;
    }
    
    public void install(Graphics2D graphics) {
        super.install(graphics);
        VisualFisheye vf = VisualFisheye.get(getVisualization());
        if (vf != null) {
            fisheye = vf.getFisheye();
        }
    }
    
    public void paint(Graphics2D graphics, int row, Shape shape) {
        super.paint(graphics, row, shape);
        Visualization vis = getVisualizationAt(row);
        if (vis == null) return;
        VisualFisheye vf = VisualFisheye.get(vis);
        if (vf != null && vf.getFisheye() != fisheye) {
            vf.setFisheye(fisheye);
        }
        // Set the parent last to avoid repaints if possible
        if (vis.getParent() != getVisualization().getParent()) {
            vis.setParent(visualization.getParent());
        }

        vis.paint(graphics, shape.getBounds2D());
    }
}
