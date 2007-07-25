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
 * Class BasicEdgeColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class BasicEdgeColumn extends ColumnProxy {
    private static final long serialVersionUID = -5420262118115839657L;
    protected Graph graph;
    protected Column vertexColumn;
    protected boolean usingInVertex = true;
    
    public BasicEdgeColumn(
            Graph graph,
            Column vertexColumn,
            boolean usingInVertex) {
        super(vertexColumn);
        this.graph = graph;
        this.usingInVertex = usingInVertex;
    }
    
    public int getVertex(int edge) {
        return usingInVertex ? 
                graph.getInVertex(edge)
                : graph.getOutVertex(edge);
    }
    
    public int size() {
        return graph.getEdgesCount();
    }
    
    public boolean isValueUndefined(int row) {
        return super.isValueUndefined(getVertex(row));
    }
    
    public String getValueAt(int index) {
        return super.getValueAt(getVertex(index));
    }
    
    public void setValueAt(int index, String element)
            throws ParseException {
        super.setValueAt(getVertex(index), element);
    }

    public int firstValidRow() {
        for (int edge = 0; edge < size(); edge++) {
            if (! column.isValueUndefined(getVertex(edge))) {
                return edge;
            }
        }
        return -1;
    }
    
    public int lastValidRow() {
        for (int edge = size()-1; edge >= 0; edge--) {
            if (! column.isValueUndefined(getVertex(edge))) {
                return edge;
            }
        }
        return -1;
    }
    
    public RowIterator iterator() {
        return new ColumnIterator(
                this, 
                firstValidRow(), 
                lastValidRow()+1, 
                true);
    }
    
    public boolean isUsingInVertex() {
        return usingInVertex;
    }
    
    public void setUsingInVertex(boolean usingInVertex) {
        if (this.usingInVertex == usingInVertex) {
            return;
        }
        this.usingInVertex = usingInVertex;
        modified();
    }
}
