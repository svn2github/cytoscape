/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.*;
import infovis.column.*;
import infovis.column.filter.NotTypedFilter;
import infovis.graph.*;
import infovis.utils.*;
import infovis.visualization.DefaultVisualColumn;
import infovis.visualization.ruler.DiscreteRulersBuilder;
import infovis.visualization.ruler.RulerTable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;

/**
 * Graph Visualization using Adjacency Matrix representation.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.66 $
 * 
 * @infovis.factory VisualizationFactory "Graph Matrix" infovis.Graph
 */
public class MatrixVisualization extends GraphVisualization {
    public static final String PROPERTY_ROW_PERMUTATION = "rowPermutation";
    public static final String PROPERTY_COLUMN_PERMUTATION = "columnPermutation";
    public static final String VISUAL_VERTEX_LABEL = "vertexLabel";
    public static final String VISUAL_ROW_SELECTION = "rowSelection";
    public static final String VISUAL_ROW_FILTER = "rowFilter";
    public static final String VISUAL_COLUMN_SELECTION = "columnSelection";
    public static final String VISUAL_COLUMN_FILTER = "columnFilter";

    /** Name of the column containing the rowSelection */
    public static final String ROWSELECTION_COLUMN = SELECTION_COLUMN; //"#rowSelection";
    /** Name of the column containing the columnSelection */
    public static final String COLUMNSELECTION_COLUMN = "#columnSelection";
    /** Name of the column containing the rowFilter */
    public static final String ROWFILTER_COLUMN = FILTER_COLUMN;
    /** Name of the column containing the columnFilter */
    public static final String COLUMNFILTER_COLUMN = "#columnFilter";
    /** Name of the IntColumn containing the rowPermutation */
    public static final String ROWPERMUTATION_COLUMN = PERMUTATION_COLUMN;
    /** Name of the optional IntColumn managing the inverse permutation. */
    public static final String INVERSEROWPERMUTATION_COLUMN = INVERSEPERMUTATION_COLUMN;
    /** Name of the IntColumn containing the columnPermutation */
    public static final String COLUMNPERMUTATION_COLUMN = "#columnPermutation";
    /** Name of the optional IntColumn managing the inverse permutation. */
    public static final String INVERSECOLUMNPERMUTATION_COLUMN = "#inverseColumnPermutation";

    protected Column vertexLabelColumn;
    protected BooleanColumn rowSelection;
    protected FilterColumn rowFilter;
    protected Permutation rowPermutation;
    protected boolean filteredRowVisible = true;
    protected BooleanColumn columnSelection;
    protected FilterColumn columnFilter;
    protected Permutation columnPermutation;
    protected boolean filteredColumnVisible = true;
    protected boolean squared;

    protected DoubleColumn rulersSize = new DoubleColumn("size");
    protected IntColumn rulersColor = new IntColumn("color");
    protected IntColumn rulersRow = new IntColumn("row");
    protected IntColumn rulersColumn = new IntColumn("column");

    /**
     * Constructor for MatrixVisualization.
     * 
     * @param graph
     *            the graph.
     */
    public MatrixVisualization(Graph graph) {
        super(graph);
        createRulers();
        //      TODO replace VERTEX_LABEL by a computed column on labels
        putVisualColumn(new DefaultVisualColumn(VISUAL_VERTEX_LABEL, false) {
            public void setColumn(Column column) {
                super.setColumn(column);
                vertexLabelColumn = column;
            }
        });
        putVisualColumn(new DefaultVisualColumn(VISUAL_ROW_SELECTION, false,
                new NotTypedFilter(BooleanColumn.class)) {
            public void setColumn(Column column) {
                super.setColumn(column);
                rowSelection = (BooleanColumn) column;
            }
        });
        putVisualColumn(new DefaultVisualColumn(VISUAL_ROW_FILTER, false,
                new NotTypedFilter(FilterColumn.class)) {
            public void setColumn(Column column) {
                super.setColumn(column);
                rowFilter = (FilterColumn) column;
            }
        });
        putVisualColumn(new DefaultVisualColumn(VISUAL_COLUMN_SELECTION, false,
                new NotTypedFilter(BooleanColumn.class)) {
            public void setColumn(Column column) {
                super.setColumn(column);
                columnSelection = (BooleanColumn) column;
            }
        });
        putVisualColumn(new DefaultVisualColumn(VISUAL_COLUMN_FILTER, false,
                new NotTypedFilter(FilterColumn.class)) {
            public void setColumn(Column column) {
                super.setColumn(column);
                columnFilter = (FilterColumn) column;
            }
        });

        setRowSelection(BooleanColumn.findColumn(graph.getVertexTable(),
                ROWSELECTION_COLUMN));
        setColumnSelection(BooleanColumn.findColumn(graph.getVertexTable(),
                COLUMNSELECTION_COLUMN));
        setRowFilter(FilterColumn.findColumn(graph.getVertexTable(),
                ROWFILTER_COLUMN));
        setColumnFilter(FilterColumn.findColumn(graph.getVertexTable(),
                COLUMNFILTER_COLUMN));
        OutDegree.getColumn(graph); // create a maintained outDegree column.
        InDegree.getColumn(graph);
    }

    public MatrixVisualization(Table table) {
        this(DefaultGraph.getGraph(table));
    }
    
    protected void createRulers() {
        this.rulers = new RulerTable();
        this.rulers.add(rulersSize);
        this.rulers.add(rulersColor);
        this.rulers.add(rulersRow);
        this.rulers.add(rulersColumn);
    }

    public Dimension getPreferredSize() {
        return new Dimension(getColumnPositionCount() * 10,
                getRowPositionCount() * 10);
    }

    /**
     * @see infovis.visualization.DefaultVisualization#computeShapes(Rectangle2D)
     */
    public void computeShapes(Rectangle2D bounds) {
        if (getColumnPositionCount() == 0 || getRowPositionCount() == 0)
            return;
        double w = (bounds.getWidth()) / getColumnPositionCount();
        double h = (bounds.getHeight()) / getRowPositionCount();

        if (squared) {
            if (w < h) {
                h = w;
            } else {
                w = h;
            }
        }

        for (RowIterator iter = iterator(); iter.hasNext();) {
            int edge = iter.nextRow();
            
            int v1 = graph.getInVertex(edge);
            int row = getRowPosition(v1);
            if (row == -1) {
                freeRectAt(edge);
                continue;
            }

            int v2 = graph.getOutVertex(edge);
            int col = getColumnPosition(v2);
            if (col == -1) {
                freeRectAt(edge);
                continue;
            }

            if (isDirected()) {
                Rectangle2D.Float s = findRectAt(edge);
                s.setRect(w * col, h * row, w, h);

                setShapeAt(edge, s);
            } else {
                Rectangle2D.Float s1 = RectPool.allocateRect();
                s1.setRect(w * col, h * row, w, h);
                row = getRowPosition(v2);
                col = getColumnPosition(v1);
                if (row == -1 || col == -1) {
                    setShapeAt(edge, s1);
                } else {
                    Rectangle2D.Float s2 = RectPool.allocateRect();
                    s2.setRect(w * col, h * row, w, h);
                    
                    CompositeShape cs = new CompositeShape();
                    cs.addShape(s1);
                    cs.addShape(s2);
                    setShapeAt(edge, cs);
                }
            }
        }

        computeRulers(bounds);
    }
    
    protected int computeRulerColor(int row) {
        if ((row&1) == 0) {
            return Color.BLACK.getRGB();
        }
        else {
            return Color.WHITE.getRGB();
        }
    }

    protected void computeRulers(Rectangle2D bounds) {
        Column column = getVisualColumn(VISUAL_VERTEX_LABEL);
        clearRulers();
        
        for (RowIterator rowIter = rowIterator(); rowIter.hasNext(); ) {
            int v = rowIter.nextRow();
            int r = getRowPosition(v);
            double h = (bounds.getHeight()) / getRowPositionCount();
            String label = (column != null) ? column.getValueAt(v) : Integer.toString(v);
            double hpos = h / 2 +  r * h;
            
            DiscreteRulersBuilder.createHorizontalRuler(bounds, label, hpos, getRulerTable());
            int ruler = getRulerTable().getRowCount()-1;
            rulersSize.setExtend(ruler, h);
            rulersColor.setExtend(ruler, computeRulerColor(r));
            rulersRow.setExtend(ruler, v);
        }
        
        for (RowIterator colIter = columnIterator(); colIter.hasNext(); ) {
            int v = colIter.nextRow();
            int c = getColumnPosition(v);
            double w = (bounds.getWidth()) / getColumnPositionCount();
            String label = (column != null) ? column.getValueAt(v) : Integer.toString(v);
            double vpos = w / 2 +  c * w;
            
            DiscreteRulersBuilder.createVerticalRuler(bounds, label, vpos, getRulerTable());
            int ruler = getRulerTable().getRowCount()-1;
            rulersSize.setExtend(ruler, w);
            rulersColor.setExtend(ruler, computeRulerColor(c));
            rulersColumn.setExtend(ruler, v);
        }
    }

    /**
     * @see infovis.Visualization#isFiltered(int)
     */
    public boolean isFiltered(int edge) {
        if (super.isFiltered(edge))
            return true;
        int row = graph.getInVertex(edge);
        if (isRowFiltered(row))
            return true;
        int col = graph.getOutVertex(edge);
        return isColumnFiltered(col);
    }

    /**
     * Returns the vertexLabelColumn.
     * 
     * @return Column
     */
    public Column getVertexLabelColumn() {
        return vertexLabelColumn;
    }

    /**
     * Sets the vertexLabelColumn.
     * 
     * @param vertexLabelColumn
     *            The vertexLabelColumn to set.
     * 
     * @return <code>true</code> if the column has been set.
     */
    public boolean setVertexLabelColumn(Column vertexLabelColumn) {
        boolean test = setVisualColumn(VISUAL_VERTEX_LABEL, vertexLabelColumn);
        if (test) {
            computeRulers(getBounds());
        }
        return test;
         
    }

    // Row permutation management
    /**
     * Returns the rowSelection.
     * 
     * @return BooleanColumn
     */
    public BooleanColumn getRowSelection() {
        return rowSelection;
    }

    /**
     * Sets the rowSelection.
     * 
     * @param rowSelection
     *            The rowSelection to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public boolean setRowSelection(BooleanColumn rowSelection) {
        return setVisualColumn(VISUAL_ROW_SELECTION, rowSelection);
    }

    /**
     * Returns the rowFilter.
     * 
     * @return FilterColumn
     */
    public FilterColumn getRowFilter() {
        return rowFilter;
    }

    /**
     * Sets the rowFilter.
     * 
     * @param rowFilter
     *            The rowFilter to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public boolean setRowFilter(FilterColumn rowFilter) {
        return setVisualColumn(VISUAL_ROW_FILTER, rowFilter);
    }

    /**
     * Returns <code>true</code> if the specified row is filtered.
     * 
     * @param row
     *            the row.
     * 
     * @return <code>true</code> if the row is filtered.
     */
    public boolean isRowFiltered(int row) {
        return rowFilter != null && rowFilter.isFiltered(row);
    }

    public RowIterator vertexIterator() {
        return graph.vertexIterator();
    }

    /**
     * Compute the rowPermutation according to the current comparator.
     */
    protected void permuteRowRows(RowComparator comp) {
        if (comp == null) {
            rowPermutation = null;
        } else {
            if (rowPermutation == null) {
                rowPermutation = new Permutation(IntColumn.findColumn(graph
                        .getVertexTable(), ROWPERMUTATION_COLUMN), IntColumn
                        .findColumn(graph.getVertexTable(),
                                INVERSEROWPERMUTATION_COLUMN));
            }
            rowPermutation.sort(graph.getVertexTable().getRowCount(), comp);
            updateRowPositions();
        }
        invalidate();
        firePropertyChange(PROPERTY_ROW_PERMUTATION, null, rowPermutation);
    }

    /**
     * Returns the row permutation.
     * 
     * @return the row permutation.
     */
    public Permutation getRowPermutation() {
        return rowPermutation;
    }

    /**
     * Change the row permutation
     * 
     * @param rowComparator
     *            The row comparator used to set the permutation
     */
    public void setRowComparator(RowComparator rowComparator) {
        permuteRowRows(rowComparator);
    }

    /**
     * Returns an iterator over the permuted rows.
     * 
     * @return an iterator over the permuted rows.
     */
    public RowIterator rowIterator() {
        if (rowPermutation != null)
            return new PermutedIterator(0, rowPermutation);
        else
            return new TableIterator(0, graph.getVertexTable().getRowCount(),
                    true);
    }

    /**
     * Hide the filtered row positions.
     */
    public void updateRowPositions() {
        invalidate();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param row
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public int getRowPosition(int row) {
        if (rowPermutation != null)
            return rowPermutation.getInverse(row);
        return row;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public int getRowPositionCount() {
        if (rowPermutation != null)
            return rowPermutation.getInverseCount();
        return graph.getVertexTable().getRowCount();
    }

    /**
     * Returns the filteredRowVisible.
     * 
     * @return boolean
     */
    public boolean isFilteredRowVisible() {
        return filteredRowVisible;
    }

    /**
     * Sets the filteredRowVisible.
     * 
     * @param filteredRowVisible
     *            The filteredRowVisible to set.
     * 
     * @return <code>true</code> if the variable has been set.
     */
    public boolean setFilteredRowVisible(boolean filteredRowVisible) {
        if (this.filteredRowVisible != filteredRowVisible) {
            this.filteredRowVisible = filteredRowVisible;
            updateRowPositions();
            return true;
        }
        return false;
    }

    // Column permutation management
    /**
     * Returns the columnSelection.
     * 
     * @return BooleanColumn
     */
    public BooleanColumn getColumnSelection() {
        return columnSelection;
    }

    /**
     * Sets the columnSelection.
     * 
     * @param columnSelection
     *            The columnSelection to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public boolean setColumnSelection(BooleanColumn columnSelection) {
        return setVisualColumn(VISUAL_COLUMN_SELECTION, columnSelection);
    }

    /**
     * Returns the columnFilter.
     * 
     * @return FilterColumn
     */
    public FilterColumn getColumnFilter() {
        return columnFilter;
    }

    /**
     * Sets the columnFilter.
     * 
     * @param columnFilter
     *            The columnFilter to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public boolean setColumnFilter(FilterColumn columnFilter) {
        return setVisualColumn(VISUAL_COLUMN_FILTER, columnFilter);
    }

    /**
     * Returns <code>true</code> if the specified column is filtered.
     * 
     * @param column
     *            the column.
     * 
     * @return <code>true</code> if the column is filtered.
     */
    public boolean isColumnFiltered(int column) {
        return columnFilter != null && columnFilter.isFiltered(column);
    }

    /**
     * Compute the columnPermutation according to the current comparator.
     */
    protected void permuteColumnColumns(RowComparator comp) {
        if (comp == null) {
            columnPermutation = null;
        } else {
            if (columnPermutation == null) {
                columnPermutation = new Permutation(IntColumn.findColumn(graph
                        .getVertexTable(), COLUMNPERMUTATION_COLUMN), IntColumn
                        .findColumn(graph.getVertexTable(),
                                INVERSECOLUMNPERMUTATION_COLUMN));
            }
            columnPermutation.sort(graph.getVertexTable().getRowCount(), comp);
            updateColumnPositions();
        }
        firePropertyChange(PROPERTY_COLUMN_PERMUTATION, null, columnPermutation);
        invalidate();
    }

    /**
     * Returns the column permutation.
     * 
     * @return the column permutation.
     */
    public Permutation getColumnPermutation() {
        return columnPermutation;
    }

    /**
     * Sets the column comparator.
     * 
     * @param columnComparator
     *            The column comparator to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public void setColumnComparator(RowComparator columnComparator) {
        permuteColumnColumns(columnComparator);
    }

    /**
     * Returns an iterator over the permuted columns.
     * 
     * @return an iterator over the permuted columns.
     */
    public RowIterator columnIterator() {
        if (columnPermutation != null)
            return new PermutedIterator(0, columnPermutation);
        else
            return new TableIterator(0, graph.getVertexTable().getRowCount(),
                    true);
    }

    /**
     * Hide the filtered column positions.
     */
    public void updateColumnPositions() {
        invalidate();
    }

    /**
     * Returns the position of the specified column or -1 if it is hidden.
     * 
     * @param col
     *            the column.
     * 
     * @return the position of the specified column or -1 if it is hidden.
     */
    public int getColumnPosition(int col) {
        if (columnPermutation != null)
            return columnPermutation.getInverse(col);
        return col;
    }

    /**
     * Returns the count of visible column positions.
     * 
     * @return the count of visible column positions.
     */
    public int getColumnPositionCount() {
        if (columnPermutation != null)
            return columnPermutation.getInverseCount();
        return graph.getVertexTable().getRowCount();
    }

    /**
     * Returns the filteredColumnVisible.
     * 
     * @return boolean
     */
    public boolean isFilteredColumnVisible() {
        return filteredColumnVisible;
    }

    /**
     * Sets the filteredColumnVisible.
     * 
     * @param filteredColumnVisible
     *            The filteredColumnVisible to set
     * 
     * @return <code>true</code> if the variable has been set.
     */
    public boolean setFilteredColumnVisible(boolean filteredColumnVisible) {
        if (this.filteredColumnVisible != filteredColumnVisible) {
            this.filteredColumnVisible = filteredColumnVisible;
            updateColumnPositions();
            return true;
        }
        return false;
    }

    /**
     * Returns the squared.
     * 
     * @return boolean
     */
    public boolean isSquared() {
        return squared;
    }

    /**
     * Sets the squared.
     * 
     * @param squared
     *            The squared to set
     */
    public void setSquared(boolean squared) {
        if (this.squared != squared) {
            this.squared = squared;
            invalidate();
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == columnFilter) {
            updateColumnPositions();
        } else if (e.getSource() == rowFilter) {
            updateRowPositions();
        }
        super.stateChanged(e);
    }
}