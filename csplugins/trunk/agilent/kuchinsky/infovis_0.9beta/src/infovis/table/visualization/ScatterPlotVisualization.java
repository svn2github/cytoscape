/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table.visualization;

import infovis.*;
import infovis.column.*;
import infovis.column.filter.NotStringOrNumberFilter;
import infovis.metadata.DependencyMetadata;
import infovis.panel.DoubleBoundedRangeModel;
import infovis.panel.dqinter.NumberColumnBoundedRangeModel;
import infovis.utils.*;
import infovis.visualization.*;
import infovis.visualization.render.VisualSize;
import infovis.visualization.ruler.LinearRulersBuilder;
import infovis.visualization.ruler.RulerTable;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;

/**
 * Scatter plot visualization.
 * 
 * <p>
 * Visualize a table with a scatter plot representation.
 * </p>
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.64 $
 * @infovis.factory VisualizationFactory "Table Scatter Plot" infovis.Table
 */
public class ScatterPlotVisualization extends DefaultVisualization implements Layout {
    /** Name of the property for x axis model change notification */
    public static final String              PROPERTY_X_AXIS_MODEL = "xAxisModel";
    /** Name of the property for y axis model change notification */
    public static final String              PROPERTY_Y_AXIS_MODEL = "yAxisModel";
    public static final String              VISUAL_X_AXIS         = "xAxis";
    public static final String              VISUAL_Y_AXIS         = "yAxis";
    protected NumberColumnBoundedRangeModel xAxisModel;
    protected NumberColumnBoundedRangeModel yAxisModel;
    protected Column                        xAxisColumn;
    protected Column                        yAxisColumn;
    protected int                           margin                = 0;
    protected IdColumn                      idColumn;
//    private static final Logger             logger                = Logger.getLogger(ScatterPlotVisualization.class);

    /**
     * Creates a new ScatterPlotVisualization object.
     * 
     * @param table
     *            the table.
     * @param xAxis
     *            the <code>Column</code> for the X Axis
     * @param yAxis
     *            the <code>Column</code> for the Y Axis
     */
    public ScatterPlotVisualization(Table table, Column xAxis, Column yAxis) {
        super(table);
        idColumn = new IdColumn();
        putVisualColumn(new DefaultVisualColumn(
                VISUAL_X_AXIS,
                true,
                NotStringOrNumberFilter.sharedInstance()) {
            public void setColumn(Column column) {
                super.setColumn(column);
                xAxisColumn = column;
                setXAxisModel(null);
            }
        });
        putVisualColumn(new DefaultVisualColumn(
                VISUAL_Y_AXIS,
                true,
                NotStringOrNumberFilter.sharedInstance()) {
            public void setColumn(Column column) {
                super.setColumn(column);
                yAxisColumn = column;
                setYAxisModel(null);
            }
        });
        setXAxisColumn(xAxis);
        setYAxisColumn(yAxis);
        this.rulers = new RulerTable();
    }

    /**
     * Creates a new ScatterPlotVisualization object.
     * 
     * @param table
     *            the table.
     * @param xAxis
     *            the name of the <code>Column</code> for the X Axis
     * @param yAxis
     *            the name of the <code>Column</code> for the Y Axis
     */
    public ScatterPlotVisualization(Table table, String xAxis, String yAxis) {
        this(table, table.getColumn(xAxis), table.getColumn(yAxis));
    }

    /**
     * Creates a new ScatterPlotVisualization object. Required columns are
     * searched in the table, taking the first two <code>NumberColumn</code> s
     * as axes.
     * 
     * @param table
     *            the table.
     */
    public ScatterPlotVisualization(Table table) {
        this(table, getNumberColumn(table, 0), getNumberColumn(table, 1));
        //FIXME
    }
    
    public Layout getLayout() {
        return this;
    }
    
    public String getName() {
        return "Table Scatter Plot";
    }
    
    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
    }
    
    /**
     * Returns the nth <code>Column</code> skipping internal columns.
     * 
     * @param t
     *            the Table.
     * @param index
     *            the index of the column, skipping over internal column,
     * 
     * @return the nth <code>NumberColumn</code> skipping internal columns.
     */
    public static NumberColumn getNumberColumn(Table t, int index) {
        NumberColumn ret = null;
        for (int i = 0; i < t.getColumnCount(); i++) {
            ret = LiteralColumn.getNumberColumn(t, i);
            if (ret != null && !ret.isInternal() && index-- == 0)
                return ret;
        }
        return null;
    }

    public NumberColumn getNumberColumnFor(Column column) {
        if (column == null)
            return idColumn;
        if (column instanceof NumberColumn) {
            return (NumberColumn) column;
        }

        for (int i = 0; i < table.getColumnCount(); i++) {
            Column col = table.getColumnAt(i);
            if (col instanceof NumberColumn
                    && DependencyMetadata.isDependentColumn(column, col)) {
                return (NumberColumn) col;
            }
        }
        return ColumnId.findIdColumn(column);
    }

    public void computeShapes(Rectangle2D bounds, Visualization vis) {
        VisualSize vs = VisualSize.get(this);
        vs.install(null);
        double maxSize = vs.getMaxSize();
        double height = bounds.getHeight();
        double off = maxSize / 2 + margin;
        double insideMargin = maxSize - 2 * margin;
        Rectangle2D.Float insideBounds = RectPool.allocateRect();
        insideBounds.setRect(bounds);
        insideBounds.x += off;
        insideBounds.y += off;
        insideBounds.width -= insideMargin;
        insideBounds.height -= insideMargin;

        if (insideBounds.isEmpty()) {
            return;
        }

        NumberColumn xCol = getNumberColumnFor(xAxisColumn);
        NumberColumn yCol = getNumberColumnFor(yAxisColumn);
        IntPair xlimits = computeMinMax(xCol);
        if (xlimits.first == -1) {
            return;
        }
        IntPair ylimits = computeMinMax(yCol);
        if (ylimits.first == -1) {
            return;
        }
        idColumn.setSize(getRowCount());
        double xmin;
        double xmax;
        double ymin;
        double ymax;
        if (xAxisModel == null) {
            xmin = xCol.getDoubleAt(xlimits.first);
            xmax = xCol.getDoubleAt(xlimits.second);
        }
        else {
            xmin = xAxisModel.getValue();
            xmax = xmin + xAxisModel.getExtent();
        }
        double xscale = insideBounds.width / Math.max(xmax - xmin, 1);

        if (yAxisModel == null) {
            ymin = yCol.getDoubleAt(xlimits.first);
            ymax = yCol.getDoubleAt(ylimits.second);
        }
        else {
            ymin = yAxisModel.getValue();
            ymax = ymin + yAxisModel.getExtent();
        }
        double yscale = insideBounds.height / Math.max(ymax - ymin, 1);
        
        shapes.clear();
        for (RowIterator iter = iterator(); iter.hasNext();) {
            int row = iter.nextRow();
            if (xCol.isValueUndefined(row) || yCol.isValueUndefined(row)) {
                freeRectAt(row);
                continue;
            }

            double xpos = 
                (xCol.getDoubleAt(row) - xmin) * xscale 
                + off
                + bounds.getX();
            double ypos = 
                height - ((yCol.getDoubleAt(row) - ymin) * yscale
                + off
                + bounds.getY());
            Rectangle2D.Float rect = findRectAt(row);
            vs.setRectSizeAt(row, rect);
            rect.x = (float) xpos - rect.width / 2;
            rect.y = (float) ypos - rect.height / 2;
            setShapeAt(row, rect);
        }
        clearRulers();
        double step = LinearRulersBuilder.computeStep(
                xmin,
                xmax,
                xscale);
        LinearRulersBuilder.createVerticalRulers(
                xmin,
                xmax,
                bounds,
                step,
                xscale,
                getRulerTable());
        RulerTable.setAxisLabel(getRulerTable(), xAxisColumn.getName(), false);
        step = LinearRulersBuilder.computeStep(
                ymin,
                ymax,
                yscale);
        LinearRulersBuilder.createHorizontalRulers(
                ymin,
                ymax,
                bounds,
                step,
                yscale,
                getRulerTable(), true);
        RulerTable.setAxisLabel(getRulerTable(), yAxisColumn.getName(), true);
        vs.uninstall(null);
    }
    
    public Dimension getPreferredSize(Visualization vis) {
        return null;
    }

    /**
     * Returns the xAxisColumn.
     * 
     * @return Column
     */
    public Column getXAxisColumn() {
        return xAxisColumn;
    }

    /**
     * Returns the yAxisColumn.
     * 
     * @return Column
     */
    public Column getYAxisColumn() {
        return yAxisColumn;
    }

    /*
     * Sets the xAxisColumn. @param xAxisColumn The xAxisColumn to set.
     * 
     * @return <code> true </code> if the column has been set.
     */
    public boolean setXAxisColumn(Column xAxis) {
        return setVisualColumn(VISUAL_X_AXIS, xAxis);
    }

    /**
     * Sets the yAxisColumn.
     * 
     * @param yAxis
     *            The yAxisColumn to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public boolean setYAxisColumn(Column yAxis) {
        return setVisualColumn(VISUAL_Y_AXIS, yAxis);
    }

    /**
     * Returns the margin.
     * 
     * @return int
     */
    public int getMargin() {
        return margin;
    }

    /**
     * Sets the margin.
     * 
     * @param margin
     *            The margin to set
     */
    public void setMargin(int margin) {
        this.margin = margin;
        invalidate();
    }

    /**
     * Returns the xAxisModel.
     * 
     * @return DoubleBoundedRangeModel
     */
    public DoubleBoundedRangeModel getXAxisModel() {
        return xAxisModel;
    }

    /**
     * Returns the yAxisModel.
     * 
     * @return DoubleBoundedRangeModel
     */
    public DoubleBoundedRangeModel getYAxisModel() {
        return yAxisModel;
    }

    /**
     * Sets the xAxisModel.
     * 
     * @param xAxisModel
     *            The xAxisModel to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public boolean setXAxisModel(NumberColumnBoundedRangeModel xAxisModel) {
        if (xAxisModel != null && xAxisModel.getColumn() != xAxisColumn)
            return false;
        firePropertyChange(PROPERTY_X_AXIS_MODEL, this.xAxisModel, xAxisModel);
        if (this.xAxisModel != xAxisModel) {
            if (this.xAxisModel != null) {
                this.xAxisModel.removeChangeListener(this);
            }
            this.xAxisModel = xAxisModel;
            if (this.xAxisModel != null) {
                this.xAxisModel.addChangeListener(this);
            }
            invalidate();
            return true;
        }
        return false;
    }

    /**
     * Sets the yAxisModel.
     * 
     * @param yAxisModel
     *            The yAxisModel to set
     * 
     * @return <code>true</code> if the column has been set.
     */
    public boolean setYAxisModel(NumberColumnBoundedRangeModel yAxisModel) {
        if (yAxisModel != null && yAxisModel.getColumn() != yAxisColumn)
            return false;
        if (this.yAxisModel != yAxisModel) {
            firePropertyChange(
                    PROPERTY_Y_AXIS_MODEL,
                    this.yAxisModel,
                    yAxisModel);
            if (this.yAxisModel != null) {
                this.yAxisModel.removeChangeListener(this);
            }
            this.yAxisModel = yAxisModel;
            if (this.yAxisModel != null) {
                this.yAxisModel.addChangeListener(this);
            }
            invalidate();
            return true;
        }
        return false;
    }

    /**
     * @see infovis.visualization.DefaultVisualization#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == xAxisModel || e.getSource() == yAxisModel) {
            invalidate();
        }
        else {
            super.stateChanged(e);
        }
    }
}