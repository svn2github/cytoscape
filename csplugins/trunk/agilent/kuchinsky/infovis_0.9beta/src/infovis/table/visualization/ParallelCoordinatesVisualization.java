/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table.visualization;

import infovis.Table;
import infovis.Visualization;
import infovis.column.NumberColumn;
import infovis.utils.IntPair;
import infovis.utils.RowIterator;
import infovis.visualization.ruler.DiscreteRulersBuilder;
import infovis.visualization.ruler.RulerTable;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

/**
 * Implements the Parallel Coordinates Visualization as described by A.
 * Inselberg (1985), "The Plane with Parallel Coordinates", Special Issue on
 * Computational Geometry of The Visual Computer 1: 69--97.
 * 
 * @author Pooven Calinghee
 * @version $Revision: 1.17 $
 * @infovis.factory VisualizationFactory "Table Parallel Coordinates"
 *                  infovis.Table
 */
public class ParallelCoordinatesVisualization extends TimeSeriesVisualization {
    
    /**
     * Creates a new Parallel Coordinates object.
     * 
     * @param table
     *            the Table to visualize.
     */
    public ParallelCoordinatesVisualization(Table table) {
        super(table);
        rulers = new RulerTable();
    }
    
    public String getName() {
        return "Table Parallel Coortinales";
    }

    public void computeShapes(Rectangle2D bounds, Visualization vis) {
        clearRulers();
        shapes.clear();
        double sx = bounds.getWidth() / (columns.size()-1);
        double min =0;
        double max = 0;
        double[] sy = new double[columns.size()];
        double[] oy = new double[columns.size()];
        for (int col = 0; col < columns.size(); col++) {
            NumberColumn n = getNumberColumnAt(col);

            IntPair p = computeMinMax(n);
            if (p.first == -1) {
                oy[col] = 0;
                sy[col] = 1;
            }
            else {
                min = n.getDoubleAt(p.first);
                oy[col] = min;
                max = n.getDoubleAt(p.second);
                double diff = (max - min);
                sy[col] = diff == 0 ? 1 : bounds.getHeight() / diff;
            }
            float x = (float) (sx * col + bounds.getX());
            DiscreteRulersBuilder.createVerticalRuler(
                    bounds,
                    n.getName(),
                    x,
                    getRulerTable());
        }
        
        for (RowIterator iter = iterator(); iter.hasNext();) {
            int i = iter.nextRow();
            GeneralPath p = (GeneralPath) getShapeAt(i);
            if (p == null) {
                p = new GeneralPath();
            } else {
                p.reset();
            }
            boolean first = true;
            for (int col = 0; col < columns.size(); col++) {
                NumberColumn n = getNumberColumnAt(col);
                if (n.isValueUndefined(i)) {
                    first = true;
                    continue;
                }
                float x = (float) (sx * col+ bounds.getX());
                float h = (float) (sy[col] * (n.getDoubleAt(i) - oy[col]));
                float y = (float) (bounds.getY() + bounds.getHeight() - h);
                if (first) {
                    p.moveTo(x, y);
                    first = false;
                } else {
                    p.lineTo(x, y);
                }
            }
            setShapeAt(i, p);
        }
    }

}