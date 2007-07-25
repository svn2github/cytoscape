/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.Column;
import infovis.Graph;
import infovis.column.FilterColumn;
import infovis.panel.dqinter.AbstractDynamicQuery;

import javax.swing.JComponent;

/**
 * 
 * Dynamic query to filter edges with a filtered vertex.
 *  
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class FilterEdgeDynamicQuery extends AbstractDynamicQuery {
    protected Graph graph;
    protected boolean isFirst;
    private FilterColumn columnFilter;
   
    /**
     * Constructor.
     * @param graph the graph
     * @param column the filter
     * @param isFirst true if the first vertex is the one filtered on
     */
    public FilterEdgeDynamicQuery(Graph graph, FilterColumn column, boolean isFirst) {
        super(column);
        this.graph = graph;
        this.isFirst = isFirst;
        this.columnFilter = column;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setColumn(Column column) {
        super.setColumn(column);
        columnFilter = (FilterColumn)column;
    }

    /**
     * {@inheritDoc}
     */
    public void update() {
        apply();
    }
    
    /**
     * {@inheritDoc}
     */
    public void apply() {
        if (filter != null)
            filter.applyDynamicQuery(this, graph.getEdgeTable().iterator());
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered(int edge) {
        if (isFirst) {
            return columnFilter.isFiltered(graph.getFirstVertex(edge));
        }
        else {
            return columnFilter.isFiltered(graph.getSecondVertex(edge));
        }
    }

    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return null;
    }

}
