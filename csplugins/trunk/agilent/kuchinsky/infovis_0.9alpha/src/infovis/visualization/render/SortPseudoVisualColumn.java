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

/**
 * Class SortPseudoVisualColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class SortPseudoVisualColumn extends AbstractVisualColumn {
    public static final String VISUAL = "sort";
    protected Column column;
    
    public SortPseudoVisualColumn(String name) {
        super(name);
        this.invalidate = true;
    }
    
    public SortPseudoVisualColumn() {
        this(VISUAL);
    }

    public Column getColumn() {
        return column;
    }
    
    public void setVisualization(Visualization vis) {
        this.visualization = vis;
    }
    
    public void setColumn(Column column) {
        if (this.column == column) return;
        super.setColumn(column);
        getVisualization().setPermutation(column);
    }

}
