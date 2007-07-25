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
import infovis.graph.*;
import infovis.utils.*;
import infovis.visualization.Layout;
import infovis.visualization.ruler.DiscreteRulersBuilder;
import infovis.visualization.ruler.RulerTable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Graph Visualization using Adjacency Matrix representation.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.70 $
 * 
 * @infovis.factory VisualizationFactory "Graph Matrix" infovis.Graph
 */
public class MatrixVisualization extends GraphVisualization 
    implements Layout, PropertyChangeListener {
    /** Name of the column containing the rowSelection */
    public static final String ROWSELECTION_COLUMN = SELECTION_COLUMN;
    /** Name of the column containing the columnSelection */
    public static final String COLUMNSELECTION_COLUMN = "#columnSelection";
    /** Name of the column containing the rowFilter */
    public static final String ROWFILTER_COLUMN = FILTER_COLUMN;
    /** Name of the column containing the columnFilter */
    public static final String COLUMNFILTER_COLUMN = "#columnFilter";

    protected MatrixAxisVisualization rowVisualization;
    protected MatrixAxisVisualization columnVisualization;
    
    protected FilterColumn rowFilter;
    protected FilterEdgeDynamicQuery rowDQ;
    protected FilterColumn columnFilter;
    protected FilterEdgeDynamicQuery columnDQ;
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
        rowVisualization = createAxis();
        rowVisualization.setVisualColumn(
                VISUAL_FILTER, 
                FilterColumn.findColumn(getVertexTable(), ROWFILTER_COLUMN));
        rowVisualization.setVisualColumn(
                VISUAL_SELECTION, 
                BooleanColumn.findColumn(getVertexTable(), ROWSELECTION_COLUMN));
        rowVisualization.addPropertyChangeListener(this);
        setRowFilter(rowVisualization.getFilter());
        
        columnVisualization = createAxis();
        columnVisualization.setVisualColumn(
                VISUAL_FILTER, 
                FilterColumn.findColumn(getVertexTable(), COLUMNFILTER_COLUMN));
        columnVisualization.setVisualColumn(
                VISUAL_SELECTION, 
                BooleanColumn.findColumn(getVertexTable(), COLUMNSELECTION_COLUMN));
        columnVisualization.addPropertyChangeListener(this);
        setColumnFilter(columnVisualization.getFilter());
        createRulers();
        OutDegree.getColumn(graph); // create a maintained outDegree column.
        InDegree.getColumn(graph);
    }

    public MatrixVisualization(Table table) {
        this(DefaultGraph.getGraph(table));
    }
    
    protected MatrixAxisVisualization createAxis() {
        return new MatrixAxisVisualization(graph, this);
    }
    
    public Layout getLayout() {
        return this;
    }
    
    public String getName() {
        return "Graph Matrix";
    }
    
    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {

    }
    
    protected void createRulers() {
        this.rulers = new RulerTable();
        this.rulers.add(rulersSize);
        this.rulers.add(rulersColor);
        this.rulers.add(rulersRow);
        this.rulers.add(rulersColumn);
    }

    public Dimension getPreferredSize(Visualization me) {
        assert(this==me);
        return new Dimension(getColumnPositionCount() * 10,
                getRowPositionCount() * 10);
    }

    /**
     * @see infovis.visualization.DefaultVisualization#computeShapes(Rectangle2D)
     */
    public void computeShapes(Rectangle2D bounds, Visualization me) {
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
            
            int v1 = graph.getFirstVertex(edge);
            int row = getRowPosition(v1);
            if (row < 0) {
                freeRectAt(edge);
                continue;
            }

            int v2 = graph.getSecondVertex(edge);
            int col = getColumnPosition(v2);
            if (col < 0) {
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
                if (row < 0 || col < 0) {
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
        Column column = getVertexLabelColumn();
        clearRulers();
        
        int i = 0;
        double h = (bounds.getHeight()) / getRowPositionCount();
        for (RowIterator rowIter = rowIterator(); rowIter.hasNext(); i++) {
            int v = rowIter.nextRow();
            String label = (column != null) ? column.getValueAt(v) : Integer.toString(v);
            double hpos = h / 2 +  i * h;
            
            assert(i==getRowPosition(v));
            
            DiscreteRulersBuilder.createHorizontalRuler(bounds, label, hpos, getRulerTable());
            int ruler = getRulerTable().getRowCount()-1;
            rulersSize.setExtend(ruler, h);
            rulersColor.setExtend(ruler, computeRulerColor(i));
            rulersRow.setExtend(ruler, v);
        }
        
        i = 0;
        double w = (bounds.getWidth()) / getColumnPositionCount();
        for (RowIterator colIter = columnIterator(); colIter.hasNext(); i++) {
            int v = colIter.nextRow();
            String label = (column != null) ? column.getValueAt(v) : Integer.toString(v);
            double vpos = w / 2 +  i * w;

            assert(i==getColumnPosition(v));

            DiscreteRulersBuilder.createVerticalRuler(bounds, label, vpos, getRulerTable());
            int ruler = getRulerTable().getRowCount()-1;
            rulersSize.setExtend(ruler, w);
            rulersColor.setExtend(ruler, computeRulerColor(i));
            rulersColumn.setExtend(ruler, v);
        }
    }

    // Methods maintained for compatibility
    
    public MatrixAxisVisualization getRowVisualization() {
        return rowVisualization;
    }
    
    public MatrixAxisVisualization getColumnVisualization() {
        return columnVisualization;
    }
    
    /**
     * Returns the vertexLabelColumn.
     * 
     * @return Column
     */
    public Column getVertexLabelColumn() {
        return getRowVisualization().getVisualColumn(VISUAL_LABEL);
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
        getRowVisualization().setVisualColumn(VISUAL_LABEL, vertexLabelColumn);
        return getColumnVisualization().setVisualColumn(VISUAL_LABEL, vertexLabelColumn);
    }

    // Row permutation management
    /**
     * Returns the rowSelection.
     * 
     * @return BooleanColumn
     */
    public BooleanColumn getRowSelection() {
        return getRowVisualization().getSelection();
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
        return getRowVisualization().setVisualColumn(
                VISUAL_SELECTION, 
                rowSelection);
    }

    /**
     * Returns the rowFilter.
     * 
     * @return FilterColumn
     */
    public FilterColumn getRowFilter() {
        return getRowVisualization().getFilter();
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
        if (this.rowFilter == rowFilter) return false;
        FilterColumn filter = getFilter();
        if (this.rowFilter != null) {
            this.rowFilter.removeChangeListener(this);
            rowDQ.setColumn(null);
        }
        this.rowFilter = rowFilter;
        if (this.rowFilter != null) {
            this.rowFilter.addChangeListener(this);
            if (rowDQ == null) {
                rowDQ = new FilterEdgeDynamicQuery(
                        getGraph(), this.rowFilter, true);
                rowDQ.setFilterColumn(filter);
            }
            else {
                rowDQ.setColumn(this.rowFilter);
            }
        }
        repaint();
        return true;
    }
    public RowIterator vertexIterator() {
        return graph.vertexIterator();
    }

    /**
     * Returns the row permutation.
     * 
     * @return the row permutation.
     */
    public Permutation getRowPermutation() {
        return getRowVisualization().getPermutation();
    }

    public void setRowPermutation(Permutation perm) {
        getRowVisualization().setPermutation(perm);
    }

    /**
     * Returns an iterator over the permuted rows.
     * 
     * @return an iterator over the permuted rows.
     */
    public RowIterator rowIterator() {
        return getRowVisualization().iterator();
    }

    public int getRowPosition(int row) {
        return getRowVisualization().getRowIndex(row);
    }

    public int getRowPositionCount() {
        return getRowVisualization().getRowCount();
    }
    
    // Column permutation management
    /**
     * Returns the columnSelection.
     * 
     * @return BooleanColumn
     */
    public BooleanColumn getColumnSelection() {
        return getColumnVisualization().getSelection();
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
        return getColumnVisualization().setVisualColumn(
                VISUAL_SELECTION, 
                columnSelection);
    }

    /**
     * Returns the columnFilter.
     * 
     * @return FilterColumn
     */
    public FilterColumn getColumnFilter() {
        return getColumnVisualization().getFilter();
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
        if (this.columnFilter == columnFilter) return false;
        FilterColumn filter = getFilter();
        if (this.columnFilter != null) {
            this.columnFilter.removeChangeListener(this);
            columnDQ.setColumn(null);
        }
        this.columnFilter = columnFilter;
        if (this.columnFilter != null) {
            this.columnFilter.addChangeListener(this);
            if (columnDQ == null) {
                columnDQ = new FilterEdgeDynamicQuery(
                        getGraph(), this.columnFilter, false);
                columnDQ.setFilterColumn(filter);
            }
            else {
                columnDQ.setColumn(this.columnFilter);
            }
            
        }
        repaint();
        return true;
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
        return getColumnVisualization().isFiltered(column);
    }

    /**
     * Returns the column permutation.
     * 
     * @return the column permutation.
     */
    public Permutation getColumnPermutation() {
        return getColumnVisualization().getPermutation();
    }

    /**
     * Sets the column permutation.
     * 
     * @param perm the permutation to set
     * 
     */
    public void setColumnPermutation(Permutation perm) {
        getColumnVisualization().setPermutation(perm);
    }

    /**
     * Returns an iterator over the permuted columns.
     * 
     * @return an iterator over the permuted columns.
     */
    public RowIterator columnIterator() {
        return getColumnVisualization().iterator();
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
        return getColumnVisualization().getRowIndex(col);
    }

    /**
     * Returns the count of visible column positions.
     * 
     * @return the count of visible column positions.
     */
    public int getColumnPositionCount() {
        return getColumnVisualization().getRowCount();
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
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == rowVisualization ||
                evt.getSource() == columnVisualization) {
            String prop = evt.getPropertyName();
            if (prop.equals(PROPERTY_PERMUTATION)) {
                invalidate();
            }
        }
    }
}