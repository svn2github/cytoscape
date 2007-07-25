/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table.io;

import infovis.Table;
import infovis.io.AbstractReader;
import infovis.io.AbstractReaderFactory;

import java.io.InputStream;
import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

/**
 * Factory of table readers.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.19 $
 */
public class TableReaderFactory extends AbstractReaderFactory {
    private static TableReaderFactory instance;
    private static Logger logger = Logger.getLogger(TableReaderFactory.class);

    /**
     * Constructor.
     */
    public TableReaderFactory() {
        super("tablereaderfactory");
    }

    /**
     * Returns the instance of the factory.
     * @return the instance of the factory
     */
    public static TableReaderFactory getInstance() {
        if (instance == null) {
            instance = new TableReaderFactory();
        }
        return instance;
    }

    /**
     * Sets the instance of the factory.
     * @param factory the factory
     */
    public static void setInstance(TableReaderFactory factory) {
        instance = factory;
    }

    /**
     * Returns a reader or null from a format name and a table.
     * @param name the name
     * @param table the table
     * @return a reader or null from a format name and a table.
     */
    public static AbstractReader createTableReader(
            String name,
            Table table) {
        return getInstance().create(name, table);
    }

    /**
     * Reads a table from a resource name.
     * @param name the name
     * @param table the table
     * @return true of the resource has been successfuly read
     */
    public static boolean readTable(String name, Table table) {
        return getInstance().tryRead(name, table);
    }

    protected void add(String name, String className, String data) {
        add(new DefaultCreator(name, className));
    }

    /**
     * Default creator class for table readers.
     * 
     * @author Jean-Daniel Fekete
     * @version $Revision: 1.19 $
     */
    public static class DefaultCreator extends AbstractCreator {
        protected String readerClassName;

        protected Class readerClass;

        /**
         * Constructor.
         * @param name format name
         * @param readerClassName reader class
         */
        public DefaultCreator(String name, String readerClassName) {
            super(name);
            this.readerClassName = readerClassName;
        }

        /**
         * {@inheritDoc}
         */
        public AbstractReader create(InputStream in, String name,
                Table table) {
            if (readerClass == null) {
                try {
                    readerClass = Class.forName(readerClassName);
                } catch (ClassNotFoundException e) {
                    logger.error("Cannot find class named "+readerClassName, e);
                    return null;
                }
            }
            Class[] parameterTypes = { InputStream.class,
                    String.class, Table.class };
            Constructor cons = null;
            try {
                cons = readerClass.getConstructor(parameterTypes);
            } catch (NoSuchMethodException ex) {
                logger.error("Cannot find constructor for "+readerClassName, ex);
            }
            if (cons == null)
                return null;
            Object[] args = { in, name, table};
            try {
                return (AbstractReader) cons.newInstance(args);
            } catch (Exception e) {
                logger.error("Cannot instantiate a "+readerClassName, e);
            }
            return null;
        }
    }
}