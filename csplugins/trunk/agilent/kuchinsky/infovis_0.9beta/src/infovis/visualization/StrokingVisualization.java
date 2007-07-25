/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import java.awt.geom.Rectangle2D;
import java.util.Set;

import infovis.Table;
import infovis.visualization.magicLens.StrokingExcentricItem;

/**
 * Visualization for shapes considered as strokes and not filled shapes.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 */
public class StrokingVisualization extends DefaultVisualization {
    public static final String PROPERTY_SHOW_EXCENTRIC = "showExcentric";
    
    private boolean showExcentric = true;
    
	public StrokingVisualization(
        Table table,
        ItemRenderer ir) {
        super(table, ir);
    }

    public StrokingVisualization(Table table) {
        super(table);
    }
    
    public LabeledItem createLabelItem(int row) {
        return new StrokingExcentricItem(this, row);
    }

    public boolean isShowExcentric() {
        return showExcentric;
    }

    public void setShowExcentric(boolean showExcentric) {
        if (this.showExcentric == showExcentric) return;
        this.showExcentric = showExcentric;
        repaint();
        firePropertyChange(PROPERTY_SHOW_EXCENTRIC, !showExcentric, showExcentric);
    }
    
    public Set pickAll(Rectangle2D hitBox, Rectangle2D bounds, Set pick) {
        if (showExcentric)
            pick = super.pickAll(hitBox, bounds, pick);
        return pick;
    }
}
