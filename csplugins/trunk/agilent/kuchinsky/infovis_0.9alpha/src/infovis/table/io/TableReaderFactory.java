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

import java.io.BufferedReader;
import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

/**
 * Factory of table readers.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.16 $
 */
public class TableReaderFactory extends AbstractReaderFactory {
    private static TableReaderFactory instance;
    private static Logger logger = Logger.getLogger(TableReaderFactory.class);

    public TableReaderFactory() {
        super("tablereaderfactory");
    }
//    
//    protected void addDefaultCreators(String factoryName) {
//        super.addDefaultCreators(factoryName);
//        add(new AbstractCreator("csv") {
//            public AbstractReader create(BufferedReader in,
//                    String name, Table table) {
//                return new CSVTableReader(in, name, table);
//            }
//        });
//        add(new AbstractCreator("tqd") {
//            public AbstractReader create(BufferedReader in,
//                    String name, Table table) {
//                return new TQDTableReader(in, name, table);
//            }
//        });
//        add(new Creator() {
//            public String getName() {
//                return "directory";
//            }
//
//            public AbstractReader create(String name, Table table) {
//                File f = new File(name);
//                if (!f.isDirectory())
//                    return null;
//                return new DirectoryTreeReader(name, DefaultTree
//                        .findTree(table));
//            }
//
//            public AbstractReader create(BufferedReader in,
//                    String name, Table table) {
//                return create(name, table);
//            }
//        });
//        
//    }    

    public static TableReaderFactory getInstance() {
        if (instance == null) {
            instance = new TableReaderFactory();
        }
        return instance;
    }

    public static void setInstance(TableReaderFactory table) {
        instance = table;
    }

    public static AbstractReader createTableReader(String name,
            Table table) {
        return getInstance().create(name, table);
    }

    public static boolean readTable(String name, Table table) {
        return getInstance().tryRead(name, table);
    }

    protected void add(String name, String className, String data) {
        add(new DefaultCreator(name, className));
    }
    
    public static class DefaultCreator extends AbstractCreator {
        protected String readerClassName;

        protected Class readerClass;

        public DefaultCreator(String name, String readerClassName) {
            super(name);
            this.readerClassName = readerClassName;
        }

        public AbstractReader create(BufferedReader in, String name,
                Table table) {
            if (readerClass == null) {
                try {
                    readerClass = Class.forName(readerClassName);
                } catch (ClassNotFoundException e) {
                    logger.error("Cannot find class named "+readerClassName, e);
                    return null;
                }
            }
            Class[] parameterTypes = { BufferedReader.class,
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