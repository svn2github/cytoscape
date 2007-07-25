/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.ruler;

import infovis.Table;
import infovis.column.DoubleColumn;
import infovis.column.StringColumn;
import infovis.utils.RowIterator;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

/**
 * Helper methods to create rulers for discrete values.
 * 
 * @author Jean-Christophe Latsis
 */
public class DiscreteRulersBuilder {
    static final Logger logger = Logger.getLogger(DiscreteRulersBuilder.class);
    
    public static void createHorizontalRulers(
            Rectangle2D bounds,
            double min,
            double max,
            DoubleColumn positions,
            Table rulers) {
        double size = max - min;
        double Ysize = bounds.getMaxY() - bounds.getMinY();

        RowIterator positerator = positions.iterator();
        while (positerator.hasNext()) {
            int row = positerator.nextRow();
            double a = ((positions.get(row) - bounds.getMinY()) * size) / Ysize;

            a = Math.round(a*100)/100;
            String label = Double.toString(a);
            Line2D line = new Line2D.Double(
                    bounds.getMinX(),
                    positions.get(row),
                    bounds.getMaxX(),
                    positions.get(row));
            RulerTable.addRuler(rulers, line, label);
        }
    }
    
    public static void createHorizontalRulers(
            Rectangle2D bounds,
            StringColumn labels,
            DoubleColumn positions,
            Table rulers) {
        if (positions.size() > labels.size()) {
            logger.error("rulers positions size > rulers labels size");
            return;
        }

        RowIterator positerator = positions.iterator();
        while (positerator.hasNext()) {
            int row = positerator.nextRow();
            createHorizontalRuler(
                    bounds,
                    labels.get(row),
                    positions.get(row),
                    rulers);
        }
    }
    
    public static void createHorizontalRuler(
            Rectangle2D bounds,
            String label,
            double position,
            Table rulers) {
        Line2D line = new Line2D.Double(
                bounds.getMinX(),
                position,
                bounds.getMaxX(),
                position);
        RulerTable.addRuler(rulers, line, label);
    }
        
    public static void createVerticalRulers(
            Rectangle2D bounds,
            double min,
            double max,
            DoubleColumn positions,
            Table rulers) {
        double size = max - min;
        double Xsize = bounds.getMaxX() - bounds.getMinX();

        RowIterator positerator = positions.iterator();
        while (positerator.hasNext()) {
            int row = positerator.nextRow();
            double a = ((positions.get(row) - bounds.getMinX()) * size) / Xsize;

            a = Math.round(a*100)/100;
            String label = Double.toString(a);
            Line2D line = new Line2D.Double(
                    positions.get(row),
                    bounds.getMinY(),
                    positions.get(row),
                    bounds.getMaxY());
            RulerTable.addRuler(rulers, line, label);
        }
    }
    
    public static void createVerticalRulers(
            Rectangle2D bounds,
            StringColumn labels,
            DoubleColumn positions,
            Table rulers) {
        if (positions.size() > labels.size()) {
            logger.error("rulers positions size > rulers labels size");
            return;
        }

        RowIterator positerator = positions.iterator();
        while (positerator.hasNext()) {
            int row = positerator.nextRow();
            createVerticalRuler(
                    bounds, 
                    labels.get(row), 
                    positions.get(row), 
                    rulers);
        }
    }
    
    public static void createVerticalRuler(
            Rectangle2D bounds,
            String label,
            double position,
            Table rulers) {
        RulerTable.addRuler(
                rulers,
                new Line2D.Double(
                    position,
                    bounds.getMinY(),
                    position,
                    bounds.getMaxY()),
                label);
    }
}