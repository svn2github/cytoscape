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
 * Abstract factory of Columns.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class ColumnFactory extends BasicFactory {
    /** Constant for requesting a dense column */
    public static final int COLUMN_TYPE_DENSE = 1;
    
    /** Constant for requesting a sparse column */
    public static final int COLUMN_TYPE_SPARSE = 2;
    
    /** Constant for requesting a column by default */
    public static final int COLUMN_TYPE_DEFAULT = COLUMN_TYPE_DENSE;
    
    protected TreeMap creators = new TreeMap();
    private static ColumnFactory instance;
    private static Logger logger = Logger.getLogger(ColumnFactory.class);
    
    public static ColumnFactory getInstance() {
        if (instance == null) {
            instance = new ColumnFactory();
        }
        return instance;
    }
    
    public static void setInstance(ColumnFactory factory) {
        instance = factory;
    }

    public static Column createColumn(String typeName, String name) {
        return getInstance().create(typeName, name);    
    }

    public static Column createColumn(String typeName, int type, String name) {
        return getInstance().create(typeName, type, name);    
    }
    
    protected ColumnFactory() {
        addDefaultCreators("columnfactory");
    }
    
    public Iterator iterator() {
        return creators.keySet().iterator();
    }
    
    public void add(String type, Creator c) {
        type = type.toLowerCase();
        ArrayList cl = (ArrayList)creators.get(type);
        if (cl == null) {
            cl = new ArrayList();
            creators.put(type, cl); 
        }
        cl.add(c);
    }
    
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
            throw new RuntimeException("Bad density name in ColumnFactory"+density);
        }
        add(new DefaultCreator(type, densityType, columnClass ));
    }
    
    public void add(Creator c) {
        add(c.getTypeName(), c);
    }
    
    public static void addColumn(Creator c) {
	getInstance().add(c);    
    }
    
    
    public boolean remove(Creator c) {
        ArrayList cl = (ArrayList)creators.get(c.getTypeName());
        if (cl == null) return false;
        return cl.remove(c);
    }
    
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
    
    public Creator getCreator(String typeName) {
        return getCreator(typeName, COLUMN_TYPE_DEFAULT);
    }
    
    public Column create(String typeName, int type, String name) {
        Creator c = getCreator(typeName, type);
        if (c == null)
            return null;
        return c.create(name);
    }
    
    public Column create(String typeName, String name) {
        return create(typeName, COLUMN_TYPE_DEFAULT, name);
    }

    public interface Creator {
        public String getTypeName();
        public int getColumnType();
        public Column create(String name);
    }
    
    public static class DefaultCreator implements Creator {
        protected String typeName;
        protected int type;
        protected String className;
        protected Class columnClass;
        
        public DefaultCreator(String typeName, int type, String className) {
            this.typeName = typeName;
            this.type = type;
            this.className = className;
        }
        
        public DefaultCreator(String typeName, String type, String className) {
            this(
                typeName,
                type.equals("SPARSE") ? COLUMN_TYPE_SPARSE: COLUMN_TYPE_DENSE,
                className);
        }
        
        public String getTypeName() {
            return typeName;
        }
        
        public int getColumnType() {
            return type;
        }
        
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
