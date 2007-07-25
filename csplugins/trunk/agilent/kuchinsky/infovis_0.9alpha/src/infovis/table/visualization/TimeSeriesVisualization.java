/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table.visualization;

import infovis.Column;
import infovis.Table;
import infovis.column.NumberColumn;
import infovis.utils.RowIterator;
import infovis.visualization.StrokingVisualization;
import infovis.visualization.ruler.*;
import infovis.visualization.ruler.LinearRulersBuilder;
import infovis.visualization.ruler.RulerTable;

import java.awt.geom.*;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;

import org.apache.log4j.Logger;

/**
 * BasicVisualization component for Time Series
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.42 $
 * @infovis.factory VisualizationFactory "Table Time Series" infovis.Table
 */
public class TimeSeriesVisualization extends StrokingVisualization {
    /** Name of the property for data columns change notification */
    public static final String       PROPERTY_DATA_COLUMNS = "dataColumns";
    private transient Point2D.Double dataBounds;
    protected DefaultListModel       columns               = new DefaultListModel();

    private static final Logger      logger                = Logger.getLogger(TimeSeriesVisualization.class);

    /**
     * Creates a new TimeSeriesVisualization object.
     * 
     * @param table
     *            the Table to visualize.
     */
    public TimeSeriesVisualization(Table table) {
        super(table);
        rulers = new RulerTable();
        addAllDataColumns();
    }

    /**
     * Adds a column.
     * 
     * @param c
     *            The column.
     */
    public void addDataColumn(NumberColumn c) {
        firePropertyChange(PROPERTY_DATA_COLUMNS, null, c);
        columns.addElement(c);
        addManagedColumn(c);
    }

    /**
     * Removes a column.
     * 
     * @param c
     *            The column.
     */
    public void removeDataColumn(NumberColumn c) {
        if (columns.removeElement(c)) {
            firePropertyChange(PROPERTY_DATA_COLUMNS, c, null);
            removeManagedColumn(c);
        }
    }

    /**
     * Adds all data columns.
     */
    public void addAllDataColumns() {
        columns.clear();
        for (int col = 0; col < table.getColumnCount(); col++) {
            Column c = table.getColumnAt(col);

            if ((c == null) || c.isInternal() || !(c instanceof NumberColumn)) {
                continue;
            }
            NumberColumn n = (NumberColumn) c;
            addDataColumn(n);
        }
    }

    public int getDataColumnCount() {
        return columns.size();
    }

    public ListModel getDataColumnList() {
        return columns;
    }
    
    public NumberColumn getNumberColumnAt(int index) {
        return (NumberColumn) columns.get(index);
    }

    protected Point2D.Double getDataBounds() {
        if (dataBounds == null) {
            double min = 0;
            double max = 0;
            boolean first = true;

            for (int i = 0; i < columns.size(); i++) {
                NumberColumn col = getNumberColumnAt(i);
                int minIndex = col.getMinIndex();
                if (minIndex == -1) {
                    logger.warn("Empty column #" + i + ": " + col.getName());
                    continue;
                }
                double m = col.getDoubleAt(minIndex);
                double M = col.getDoubleAt(col.getMaxIndex());
                if (first) {
                    min = m;
                    max = M;
                    first = false;
                }
                else {
                    min = Math.min(min, m);
                    max = Math.max(max, M);
                }
            }

            dataBounds = new Point2D.Double(min, max);
        }

        return dataBounds;
    }

    /**
     * @see infovis.visualization.DefaultVisualization#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        dataBounds = null;
        super.stateChanged(e);
    }

    /**
     * @see infovis.visualization.DefaultVisualization
     */
    public void computeShapes(Rectangle2D bounds) {
        Point2D.Double db = getDataBounds();

        double sx = bounds.getWidth() / (columns.size() - 1);
        double sy = bounds.getHeight() / (db.getY() - db.getX());
        double ty = -db.getX();

        clearRulers();

        double step = LinearRulersBuilder.computeStep(db.getX(), db.getY(), sy);
        LinearRulersBuilder.createHorizontalRulers(
                db.getX(),
                db.getY(),
                bounds,
                step,
                sy,
                getRulerTable());
        
        for (int col = 0; col < columns.size(); col++) {
            NumberColumn n = getNumberColumnAt(col);
            float x = (float) (sx * col + bounds.getX());

            if (getRulerTable().size() < columns.size()) {
                DiscreteRulersBuilder.createVerticalRuler(
                        bounds, 
                        n.getName(), 
                        x, 
                        getRulerTable());
            }
        }

        for (RowIterator iter = iterator(); iter.hasNext();) {
            int i = iter.nextRow();
            GeneralPath p = (GeneralPath) getShapeAt(i);
            if (p == null) {
                p = new GeneralPath();
            }
            else {
                p.reset();
            }
            boolean first = true;
            for (int col = 0; col < columns.size(); col++) {
                NumberColumn n = getNumberColumnAt(col);
                if (n.isValueUndefined(i)) {
                    first = true;
                    continue;
                }
                float x = (float) (sx * col + bounds.getX());
                float y = (float) (bounds.getHeight() - sy
                        * (n.getDoubleAt(i) + ty) + bounds.getY());

                if (first) {
                    p.moveTo(x, y);
                    first = false;
                }
                else {
                    p.lineTo(x, y);
                }
            }
            setShapeAt(i, p);
        }
    }
}