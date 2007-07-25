/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.algorithm;

import infovis.Graph;
import infovis.Table;
import infovis.column.ColumnOne;
import infovis.column.NumberColumn;
import infovis.utils.RowIterator;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * Computes the Laplacian of a graph
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class Laplacian extends Algorithm {

    public Laplacian(Graph graph) {
        super(graph);
    }
    
    public DoubleMatrix2D compute(NumberColumn edgeWeights, DoubleMatrix2D matrix) {
        return computeLaplacian(graph, edgeWeights, matrix);
    }
    
    public DoubleMatrix2D compute(NumberColumn edgeWeights) {
        return computeLaplacian(graph, edgeWeights, null);
    }
    
    public DoubleMatrix2D compute() {
        return computeLaplacian(graph, null, null);
    }
    
    public DoubleMatrix2D compute(DoubleMatrix2D matrix) {
        return computeLaplacian(graph, null, matrix);
    }
    
    public static DoubleMatrix2D computeLaplacian(Graph graph, NumberColumn edgeWeights, DoubleMatrix2D matrix) {
        assert(!graph.isDirected());
        Table vertices = graph.getVertexTable();
        int size = vertices.getLastRow()+1;
        if (matrix == null
                || matrix.columns() < size 
                || matrix.rows() < size) {
            matrix = DoubleFactory2D.dense.make(size, size, 0);
        }
        else {
            matrix.assign(0);
        }
        if (edgeWeights == null) {
            edgeWeights = ColumnOne.instance;
        }
        for (int v = 0; v < size; v++) {
            if (!vertices.isRowValid(v)) continue;
            double acc = 0;
            for (RowIterator edges = graph.edgeIterator(v);
                edges.hasNext(); ) {
                int e = edges.nextRow();
                int v2 = graph.getOtherVertex(e, v);
                if (v2 != v) {
                    double w = edgeWeights.getDoubleAt(e);
                    assert(edgeWeights.isValueUndefined(e) && w!=0);
                    acc += w;
                    matrix.setQuick(v, v2, -w);
                    matrix.setQuick(v2, v, -w);
                }
            }
            matrix.setQuick(v, v, acc);
        }
        return matrix;
    }
}
