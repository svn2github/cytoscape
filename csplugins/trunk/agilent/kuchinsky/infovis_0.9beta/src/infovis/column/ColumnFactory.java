/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.utils.BasicFactory;

import java.lang.reflect.Constructor;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Factory of Columns to create a new column
 * based on its type name and density/sparsity.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public class ColumnFactory extends BasicFactory {
    /** Constant for requesting a dense column. */
    public static final int      COLUMN_TYPE_DENSE   = 1;

    /** Constant for requesting a sparse column. */
    public static final int      COLUMN_TYPE_SPARSE  = 2;

    /** Constant for requesting a column by default. */
    public static final int      COLUMN_TYPE_DEFAULT = COLUMN_TYPE_DENSE;

    protected TreeMap            creators            = new TreeMap();
    private static ColumnFactory instance;
    private static Logger        logger              = Logger
                                                             .getLogger(ColumnFactory.class);
    protected ColumnFactory() {
        addDefaultCreators("columnfactory");
    }
    
    /**
     * Returns the current instance of this factory.
     * 
     * @return the current instance of this factory.
     */
    public static ColumnFactory getInstance() {
        if (instance == null) {
            instance = new ColumnFactory();
        }
        return instance;
    }
    
    /**
     * Sets the current instance of this factory.
     * 
     * @param factory the new instance.
     */
    public static void setInstance(ColumnFactory factory) {
        instance = factory;
    }

    /**
     * Utility method to create a column from a type name using a specified name.
     * 
     * <p>Delegates the creation to the current instance.
     * 
     * @param typeName the type name
     * @param name the column name
     * @return a column of the specified type with the given name
     * of null if the type is not known.
     */
    public static Column createColumn(String typeName, String name) {
        return getInstance().create(typeName, name);    
    }

    /**
     * Utility method to create a column from a type name using a specified name
     * and sparsity/density specification.
     * 
     * <p>Delegates the creation to the current instance.
     * 
     * @param typeName the type name
     * @param type density, either <code>COLUMN_TYPE_DENSE</code>
     * or <code>COLUMN_TYPE_SPARSE</code>.
     * @param name the column name
     * @return a column of the specified type with the given name
     * of null if the type is not known.
     */
    public static Column createColumn(String typeName, int type, String name) {
        return getInstance().create(typeName, type, name);    
    }
    
    /**
     * Returns an iterator over the type names maintained
     * by the factory.
     * @return an iterator over the type names maintained
     * by the factory.
     */
    public Iterator iterator() {
        return creators.keySet().iterator();
    }
    
    /**
     * Adds a new creator associated with a specified type name.
     * @param type the type name
     * @param c the Creator
     */
    public void add(String type, Creator c) {
        type = type.toLowerCase();
        ArrayList cl = (ArrayList)creators.get(type);
        if (cl == null) {
            cl = new ArrayList();
            creators.put(type, cl); 
        }
        cl.add(c);
    }
    
    /**
     * Adds a new creator associated with a specified type name and
     * density name.
     * @param type the type name
     * @param columnClass the class of column to create for this type name
     * @param density a string "DENSE" or "SPARSE"
     */
    public void add(String type, String columnClass, String density) {
        int densityType;
        if (density == null || columnClass == null) {
            return;
        }
        if (density.equals("DENSE")) {
            densityType = COLUMN_TYPE_DENSE;
        }
        else if (density.equals("SPARSE")) {
            densityType = COLUMN_TYPE_SPARSE;
        }
        else {
            logger.error("Bad density name in ColumnFactory"+density);
            throw new RuntimeException("Bad density name in ColumnFactory"+density);
        }
        add(new DefaultCreator(type, densityType, columnClass ));
    }
    
    /**
     * Adds a creator using its name.
     * @param c the creator
     */
    public void add(Creator c) {
        add(c.getTypeName(), c);
    }

    /**
     * Removes a creator from this factory.
     * @param c the creator
     * @return true if the creator existed and has been removed.
     */
    public boolean remove(Creator c) {
        ArrayList cl = (ArrayList)creators.get(c.getTypeName());
        if (cl == null) return false;
        return cl.remove(c);
    }
    
    /**
     * Returns the creator for a specified type name and density type.
     * @param typeName the type name
     * @param type the density
     * @return the creator for a specified type name and density type
     * or null.
     */
    public Creator getCreator(String typeName, int type) {
        typeName = typeName.toLowerCase();
        ArrayList cl = (ArrayList)creators.get(typeName);
        if (cl == null) {
            return null;
        }
        
        for (int i = 0; i < cl.size(); i++) {
            Creator c = (Creator)cl.get(i);
            if (c.getColumnType() == type) {
                return c;
            }
        }
        return null;
    }
    
    /**
     * Returns a creator for the specified type name.
     * @param typeName the type name
     * @return a creator for the specified type name
     */
    public Creator getCreator(String typeName) {
        return getCreator(typeName, COLUMN_TYPE_DEFAULT);
    }
    
    /**
     * Create a Column given its type name and density type.
     * 
     * @param typeName the type name of the column
     * @param type the density
     * @param name the column name
     * @return a Column or null
     */
    public Column create(String typeName, int type, String name) {
        Creator c = getCreator(typeName, type);
        if (c == null)
            return null;
        return c.create(name);
    }
    
    /**
     * Create a Column given its type name.
     * 
     * @param typeName the type name of the column
     * @param name the column name
     * @return a Column or null
     */
    public Column create(String typeName, String name) {
        return create(typeName, COLUMN_TYPE_DEFAULT, name);
    }
    
    /**
     * Returns a registered type name for a specified column.
     * @param col the column
     * @return a registered type name for the specified column.
     */
    public String getTypeName(Column col) {
        for (Class cl = col.getClass(); cl != Object.class; cl = cl
                .getSuperclass()) {
            for (Iterator iter = iterator(); iter.hasNext();) {
                String typeName = (String) iter.next();
                Creator c = getCreator(typeName);
                if (c.getClassName().equals(cl.getName())) {
                    return typeName;
                }
            }
        }
        return null;
    }

    /**
     * 
     * Column Creator for the Factory.
     * 
     * @author Jean-Daniel Fekete
     */
    public interface Creator {
        /**
         * @return the type name
         */
        String getTypeName();
        /** @return the class name of the column */
        String getClassName();
        /** @return the density type */
        int getColumnType();
        /**
         * Create a column with a specified name. 
         * @param name the column name. 
         * @return a column with a specified name. 
         * */
        Column create(String name);
    }
    
    /**
     * 
     * Default implementation of Creator.
     * 
     * @author Jean-Daniel Fekete
     */
    public static class DefaultCreator implements Creator {
        protected String typeName;
        protected int type;
        protected String className;
        protected Class columnClass;
        
        /**
         * Constructor. 
         * @param typeName the type name
         * @param type the density type
         * @param className the column class name
         */
        public DefaultCreator(String typeName, int type, String className) {
            this.typeName = typeName;
            this.type = type;
            this.className = className;
        }
        
        /**
         * Constructor.
         * @param typeName the type name
         * @param type the density type as a string
         * @param className the column class name
         */
        public DefaultCreator(String typeName, String type, String className) {
            this(
                typeName,
                type.equals("SPARSE") ? COLUMN_TYPE_SPARSE: COLUMN_TYPE_DENSE,
                className);
        }
        
        /**
         * {@inheritDoc} 
         */
        public String getTypeName() {
            return typeName;
        }
        
        /**
         * {@inheritDoc} 
         */
        public int getColumnType() {
            return type;
        }
        
        /**
         * {@inheritDoc} 
         */
        public String getClassName() {
            return className;
        }
        
        /**
         * {@inheritDoc} 
         */
        public Column create(String name) {
            if (columnClass == null) {
                try {
                    columnClass = Class.forName(className);
                }
                catch (ClassNotFoundException e) {
                    logger.error("Cannot load class named "+className);
                    return null;
                }
            }
            try {
                Class[] parameterTypes = { String.class };
                Constructor cons = columnClass.getConstructor(parameterTypes);
                Object[] args = { name };
                return (Column)cons.newInstance(args);
            }
            catch(Exception e) {
                logger.error("Cannot instantiate Column class "+className, e);
            }
            return null;
        }
    }
}
