/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.io;

import infovis.Table;
import infovis.Tree;
import infovis.io.AbstractReader;
import infovis.io.AbstractReaderFactory;

import java.io.InputStream;
import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

/**
 * Factory of tree readers.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.19 $
 */
public class TreeReaderFactory extends AbstractReaderFactory {
    static TreeReaderFactory instance;
    static Logger logger = Logger.getLogger(TreeReaderFactory.class);

    public TreeReaderFactory() {
        super("treereaderfactory");
    }

    /**
     * Returns a tree from a table.
     * 
     * @param table
     *            the table.
     * 
     * @return a tree from a table.
     */
    public static Tree getTree(Table table) {
        return (Tree)table.getTable();
    }

    /**
     * Returns the shared instance of this <code>TreeReaderFactory</code>
     * 
     * @return the shared instance of this <code>TreeReaderFactory</code>
     */
    public static TreeReaderFactory getInstance() {
        if (instance == null) {
            instance = new TreeReaderFactory();
        }
        return instance;
    }

    public static void setInstance(TreeReaderFactory tree) {
        instance = tree;
    }

    /**
     * Creates a tree reader from a specified resource name and a tree
     * 
     * @param name
     *            the resource name
     * @param tree
     *            the tree
     * 
     * @return a tree reader or <code>null</code>.
     */
    public static AbstractReader createTreeReader(String name, Tree tree) {
        return getInstance().create(name, tree);
    }

    public static boolean readTree(String name, Tree tree) {
        return getInstance().tryRead(name, tree);
    }

    protected void add(String name, String className, String data) {
        if (data == null) {
            add(new DefaultCreator(name, className));
        }
        else {
            try {
                Class c = Class.forName(className);
                Creator creator = (Creator)c.newInstance();
                add(creator);
            }
            catch(Exception e) {
                logger.error("Cannot instantiate TreeReader creator "+className, e);
            }
        }
    }

    public static class DefaultCreator extends AbstractCreator {
        protected String readerClassName;

        protected Class readerClass;

        public DefaultCreator(
                String name,
                String readerClassName, 
                boolean needingOpen) {
            super(name, needingOpen);
            this.readerClassName = readerClassName;
        }
        public DefaultCreator(
                String name,
                String readerClassName) {
            this(name, readerClassName, true);
        }

        public AbstractReader create(
                InputStream in,
                String name,
                Table table) {
            if (readerClass == null) {
                try {
                    readerClass = Class.forName(readerClassName);
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }
            Class[] parameterTypes = { 
                    InputStream.class,
                    String.class, Tree.class };
            Constructor cons = null;
            try {
                cons = readerClass.getConstructor(parameterTypes);
            } catch (NoSuchMethodException ex) {
                logger.error("Cannot get expected constructor", ex);
            }
            if (cons == null)
                return null;
            Tree tree = getTree(table);
            Object[] args = { in, name, tree };
            try {
                return (AbstractReader) cons.newInstance(args);
            } catch (Exception e) {
                logger.error("Cannot instantiate TreeReader", e);
            }
            return null;
        }
    }
}