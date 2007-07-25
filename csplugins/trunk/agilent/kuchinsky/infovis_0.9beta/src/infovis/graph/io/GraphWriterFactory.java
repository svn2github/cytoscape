/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.Table;
import infovis.graph.DefaultGraph;
import infovis.io.AbstractWriter;
import infovis.io.AbstractWriterFactory;

import java.io.OutputStream;
import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

/**
 * Class GraphWriterFactory
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class GraphWriterFactory extends AbstractWriterFactory {
    static GraphWriterFactory instance = new GraphWriterFactory();
    static Logger             logger   = Logger
                                               .getLogger(GraphWriterFactory.class);

    public GraphWriterFactory() {
        super("graphwriterfactory");
    }

    /**
     * Returns a Graph from a specified table.
     * 
     * @param table
     *            the table.
     * 
     * @return a Graph from a specified table.
     */
    public static Graph getGraph(Table table) {
        return DefaultGraph.getGraph(table);
    }

    /**
     * Returns the shared instance of this <code>GraphReaderFactory</code>
     * 
     * @return the shared instance of this <code>GraphReaderFactory</code>
     */
    public static GraphWriterFactory getInstance() {
        return instance;
    }

    public static void setInstance(GraphWriterFactory graph) {
        instance = graph;
    }

    /**
     * Creates a graph reader from a specified resource name and a graph
     * 
     * @param name
     *            the resource name
     * @param graph
     *            the graph
     * 
     * @return a graph reader or <code>null</code>.
     */
    public static AbstractWriter createGraphWriter(String name, Graph graph) {
        return getInstance().create(name, graph.getEdgeTable());
    }

    public static boolean writeGraph(String name, Graph graph) {
        return getInstance().tryWrite(name, graph.getEdgeTable());
    }

    protected void add(String name, String className, String data) {
        add(new DefaultCreator(name, className, data));
    }

    public static class DefaultCreator extends AbstractCreator {
        protected String writerClassName;
        protected String suffix;
        protected Class  writerClass;

        public DefaultCreator(String name, String writerClassName, String suffix) {
            super(name, suffix);
            this.writerClassName = writerClassName;
        }

        public AbstractWriter create(OutputStream out, String name, Table table) {
            if (writerClass == null) {
                try {
                    writerClass = Class.forName(writerClassName);
                } catch (ClassNotFoundException e) {
                    logger.error("Cannot find reader class named "
                            + writerClassName, e);
                    return null;
                }
            }
            Class[] parameterTypes = {
                    OutputStream.class,
                    String.class,
                    Graph.class };
            Constructor cons = null;
            try {
                cons = writerClass.getConstructor(parameterTypes);
            } catch (NoSuchMethodException ex) {
                logger.error(
                        "Cannot find constructor for " + writerClassName,
                        ex);
            }
            if (cons == null) {
                return null;
            }
            Graph graph = getGraph(table);
            Object[] args = { out, name, graph };
            try {
                return (AbstractWriter) cons.newInstance(args);
            } catch (Exception e) {
                logger.error("Cannot instantiate graph writer "
                        + writerClassName, e);
            }
            return null;
        }
    }
}
