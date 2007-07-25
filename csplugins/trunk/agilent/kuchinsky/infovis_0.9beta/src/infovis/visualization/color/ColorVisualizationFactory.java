/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.color;

import infovis.Column;
import infovis.column.IntColumn;
import infovis.column.NumberColumn;
import infovis.metadata.ValueCategory;
import infovis.visualization.ColorVisualization;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Factory for Color Visualizations.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public abstract class ColorVisualizationFactory extends ValueCategory {
    static LinkedList          creators                         = new LinkedList();
//    public static final String COLOR_VISUALIZATION_DEFAULT      = "default";
//    public static final String COLOR_VISUALIZATION_ORDERED      = VALUE_CATEGORY_TYPE_ORDERED;
//    public static final String COLOR_VISUALIZATION_NOMINAL      = VALUE_CATEGORY_TYPE_NOMINAL;
//    public static final String COLOR_VISUALIZATION_CATEGORICAL  = VALUE_CATEGORY_TYPE_CATEGORICAL;
//    public static final String COLOR_VISUALIZATION_DIFFERENTIAL = VALUE_CATEGORY_TYPE_DIFFERENTIAL;
//    public static final String COLOR_VISUALIZATION_EXPLICIT     = "explicit";

    static {
        add(new DefaultCreator());
    }

    /**
     * Creates a dynamic query from a column.
     * 
     * @param c
     *            The column
     * @param type
     *            the default type of ColorVisualization.
     * 
     * @return A Dynamic query or null.
     */
    public static ColorVisualization create(Column c, String type) {
        ColorVisualization ret = null;
        for (Iterator iter = creators.iterator(); iter.hasNext();) {
            Creator creator = (Creator) iter.next();
            ret = creator.create(c, type);
            if (ret != null)
                break;
        }
        return ret;
    }
    
    /**
     * Returns the class created for a column and a type.
     * @param c the column
     * @param type the type
     * @return the class created for a column and a type.
     */
    public static Class createdClass(Column c, String type) {
        Class ret = null;
        for (Iterator iter = creators.iterator(); iter.hasNext();) {
            Creator creator = (Creator) iter.next();
            ret = creator.createdClass(c, type);
            if (ret != null)
                break;
        }
        return ret;
    }

    /**
     * Creates a dynamic query of default type from a column.
     * 
     * @param c
     *            The column
     * 
     * @return A Dynamic query or null.
     */
    public static ColorVisualization createColorVisualization(Column c) {
        return create(c, null);
    }
    
    /**
     * Returns the created class given a column.
     * @param c the column
     * @return the created class given a column.
     */    
    public static Class createdColorVisualization(Column c) {
        return createdClass(c, null);
    }


    /**
     * Adds a default creator for a specific kind of column.
     * 
     * @param c
     *            The creator
     */
    public static void add(Creator c) {
        creators.addFirst(c);
    }

    /**
     * Constructor for ColorVisualizationFactory.
     */
    public ColorVisualizationFactory() {
        super();
    }
    
    /**
     * Interface for creator of ColorVisualization.
     * 
     * @author Jean-Daniel Fekete
     * @version $Revision: 1.13 $
     */
    public interface Creator {
        /** 
         * Creates a ColorVisualization given a column and a color type.
         * @param col the column
         * @param type the color type
         * @return a ColorVisualization given a column and a color type.
         */
        ColorVisualization create(Column col, String type);
        /**
         * Returns the created class given a column and a color type.
         * @param col the column
         * @param type the type
         * @return the created class given a column and a color type.
         */
        Class createdClass(Column col, String type);
    }

    /**
     * Default creator class.
     * 
     * @author Jean-Daniel Fekete
     * @version $Revision: 1.13 $
     */
    public static class DefaultCreator implements Creator {
        /**
         * {@inheritDoc}
         */
        public ColorVisualization create(Column col, String type) {
            int cat;
//            if (type == null || type.equals(COLOR_VISUALIZATION_DEFAULT)) {
            if (type == null) {
                cat = findValueCategory(col);
            }
            else {
                cat = categoryValue(type);
            }

            switch (cat) {
            case TYPE_ORDERED: {
                if (col instanceof NumberColumn) {
                    NumberColumn column = (NumberColumn) col;
                    return new EqualizedOrderedColor(column);
                    // return new OrderedColor(column);
                }
                break;
            }
            case TYPE_NOMINAL:
                return new NominalColor(col);
            case TYPE_CATEGORIAL:
                if (col instanceof IntColumn) {
                    IntColumn column = (IntColumn) col;
                    return new CategoricalColor(column);
                }
                break;
            case TYPE_DIFFERENTIAL:

                break;
            case TYPE_EXPLICIT:
                if (col instanceof IntColumn) {
                    return new ExplicitColor((IntColumn) col);
                }
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public Class createdClass(Column col, String type) {
            int cat;
//            if (type == null || type.equals(COLOR_VISUALIZATION_DEFAULT)) {
            if (type == null) {
                cat = findValueCategory(col);
            }
            else {
                cat = categoryValue(type);
            }

            switch (cat) {
            case TYPE_ORDERED: {
                if (col instanceof NumberColumn) {
                    return EqualizedOrderedColor.class;
                }
                break;
            }
            case TYPE_NOMINAL:
                return NominalColor.class;
            case TYPE_CATEGORIAL:
                if (col instanceof IntColumn) {
                    return CategoricalColor.class;
                }
                break;
            case TYPE_DIFFERENTIAL:

                break;
            case TYPE_EXPLICIT:
                if (col instanceof IntColumn) {
                    return ExplicitColor.class;
                }
            }
            return null;
        }

    }
}
