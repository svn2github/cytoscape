/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.Table;
import infovis.visualization.magicLens.StrokingExcentricItem;

/**
 * Visualization for shapes considered as strokes and not filled shapes.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.11 $
 */
public class StrokingVisualization extends DefaultVisualization {
    
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
}
