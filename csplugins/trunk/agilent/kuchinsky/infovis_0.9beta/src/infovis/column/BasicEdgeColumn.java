/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.Graph;
import infovis.utils.RowIterator;

import java.text.ParseException;

/**
 * Column for graph edge table to access vertex attributes.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class BasicEdgeColumn extends ColumnProxy {
    private static final long serialVersionUID = -5420262118115839657L;
    protected Graph graph;
    protected Column vertexColumn;
    protected boolean usingFirstVertex = true;
    
    /**
     * Create an edge column that returns the value
     * of its related vertex column, either from
     * its firstVertex or secondVertex.
     *  
     * @param graph the graph
     * @param vertexColumn the associated vertex column
     * @param usingFirstVertex true if the firstVertex is used,
     * false if the secondVertex is used.
     * 
     * @see infovis.graph#getFirstVertex()
     * @see infovis.graph#getSecondVertex()
     */
    public BasicEdgeColumn(
            Graph graph,
            Column vertexColumn,
            boolean usingFirstVertex) {
        super(vertexColumn);
        this.graph = graph;
        this.usingFirstVertex = usingFirstVertex;
    }
    
    /**
     * Returns the considered vertex.
     * @param edge the edge
     * @return the considered vertex.
     */
    public int getVertex(int edge) {
        return usingFirstVertex ? 
                graph.getFirstVertex(edge)
                : graph.getSecondVertex(edge);
    }
    
    /**
     * {@inheritDoc}
     */
    public int size() {
        return graph.getEdgesCount();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isValueUndefined(int row) {
        return super.isValueUndefined(getVertex(row));
    }
    
    /**
     * {@inheritDoc}
     */
    public String getValueAt(int index) {
        return super.getValueAt(getVertex(index));
    }
    
    /**
     * {@inheritDoc}
     */
    public void setValueAt(int index, String element)
            throws ParseException {
        super.setValueAt(getVertex(index), element);
    }
    
    /**
     * Returns the first valid row (not undefined) of this column.
     * 
     * @return the first valid row of this column.
     */
    public int firstValidRow() {
        for (int edge = 0; edge < size(); edge++) {
            if (! column.isValueUndefined(getVertex(edge))) {
                return edge;
            }
        }
        return -1;
    }
    
    /**
     * Returns the last valid row (not undefined) of this column.
     * 
     * @return the last valid row of this column.
     */
    public int lastValidRow() {
        for (int edge = size()-1; edge >= 0; edge--) {
            if (! column.isValueUndefined(getVertex(edge))) {
                return edge;
            }
        }
        return -1;
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        return new ColumnIterator(
                this, 
                firstValidRow(), 
                lastValidRow()+1, 
                true);
    }
    
    /**
     * Returns true if the first vertex is considere, false
     * if the second vertex is considered.
     * 
     * @return true if the first vertex is considere, false
     * if the second vertex is considered.
     */
    public boolean isUsingFirstVertex() {
        return usingFirstVertex;
    }
    
    /**
     * Sets the vertex considered.
     * 
     * @param usingFirstVertex true is should use first vertex
     */
    public void setUsingFirstVertex(boolean usingFirstVertex) {
        if (this.usingFirstVertex == usingFirstVertex) {
            return;
        }
        this.usingFirstVertex = usingFirstVertex;
        modified();
    }
}
