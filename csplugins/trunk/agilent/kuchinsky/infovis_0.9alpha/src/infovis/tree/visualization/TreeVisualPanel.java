/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.Column;
import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.panel.DefaultVisualPanel;
import infovis.utils.RowComparator;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class TreeVisualPanel extends DefaultVisualPanel {
    /**
     * Constructor for TreeVisualPanel.
     * @param visualization
     * @param filter
     */
    public TreeVisualPanel(Visualization visualization, ColumnFilter filter) {
        super(visualization, filter);
    }
    
    public TreeVisualization getTreeVisualization() {
        return (TreeVisualization)getVisualization().findVisualization(TreeVisualization.class);
    }
    
    protected void createAll() {
        super.createAll();
        addOrientationButtons();
    }
    
    public RowComparator densifyColumn(Column col) {
        return getTreeVisualization().densifyColumn(col);
    }
}
