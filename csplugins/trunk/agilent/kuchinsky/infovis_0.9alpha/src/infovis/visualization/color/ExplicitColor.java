/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.color;

import infovis.column.IntColumn;
import infovis.visualization.ColorVisualization;

import javax.swing.event.ChangeEvent;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class ExplicitColor extends ColorVisualization {
    IntColumn colorColumn;
    
    /**
     * Constructor for ExplicitColor.
     * @param column
     */
    public ExplicitColor(IntColumn column) {
        super(column);
        colorColumn = column;
    }

    /**
     * @see infovis.visualization.ColorVisualization#getColorValue(int)
     */
    public int getColorValue(int row) {
        if (colorColumn.isValueUndefined(row))
            return 0;
        return colorColumn.get(row);
    }
    
    /**
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
    }

}
