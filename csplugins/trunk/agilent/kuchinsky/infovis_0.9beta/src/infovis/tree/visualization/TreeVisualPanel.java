/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.panel.DefaultVisualPanel;

/**
 * Visual panel for class derived from TreeVisualization.
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class TreeVisualPanel extends DefaultVisualPanel {
    /**
     * Constructor for TreeVisualPanel.
     * @param visualization the visualization
     * @param filter the filter
     */
    public TreeVisualPanel(Visualization visualization, ColumnFilter filter) {
        super(visualization, filter);
    }
    
    /**
     * Returns the TreeVisualization.
     * @return the TreeVisualization
     */
    public TreeVisualization getTreeVisualization() {
        return (TreeVisualization)getVisualization().findVisualization(TreeVisualization.class);
    }
    
    protected void createAll() {
        super.createAll();
        addOrientationButtons();
    }
//    
//    public RowComparator densifyColumn(Column col) {
//        return getTreeVisualization().densifyColumn(col);
//    }
}
