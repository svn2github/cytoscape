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
import infovis.io.AbstractReader;
import infovis.io.AbstractReaderFactory;

import java.io.InputStream;
import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

/**
 * Reader Factory for Graphs.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.18 $
 */
public class GraphReaderFactory extends AbstractReaderFactory {
    static GraphReaderFactory instance;
    static Logger logger = Logger.getLogger(GraphReaderFactory.class);

    public GraphReaderFactory() {
        super("graphreaderfactory");
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

    /*
    protected void addDefaultCreators() {
        add(new AbstractCreator("xml") {
            public AbstractReader create(BufferedReader in,
                    String name, Table table) {
                return new GraphMLReader(in, name, getGraph(table));
            }
        });
        add(new AbstractCreator("dot") {
            public AbstractReader create(BufferedReader in,
                    String name, Table table) {
                return new DOTGraphReader(in, name, getGraph(table));
            }
        });
        add(new AbstractCreator("vcg") {
            public AbstractReader create(BufferedReader in,
                    String name, Table table) {
                return new VCGGraphReader(in, name, getGraph(table));
            }
        });
        add(new Creator() {
            public String getName() {
                return "html";
            }

            public AbstractReader create(String name, Table table) {
                if (name.startsWith("http:") || name.startsWith("ftp:")
                        || name.endsWith(".html")
                        || name.endsWith(".htm"))
                    return new HTMLGraphReader(name, getGraph(table));
                return null;
            }

            public AbstractReader create(BufferedReader in,
                    String name, Table table) {
                return create(name, table);
            }
        });
    }
    */

    /**
     * Returns the shared instance of this <code>GraphReaderFactory</code>
     * 
     * @return the shared instance of this <code>GraphReaderFactory</code>
     */
    public static GraphReaderFactory getInstance() {
        if (instance == null) {
            instance = new GraphReaderFactory();
        }
        return instance;
    }

    public static void setInstance(GraphReaderFactory graph) {
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
    public static AbstractReader createGraphReader(String name,
            Graph graph) {
        return getInstance().create(name, graph.getEdgeTable());
    }

    public static AbstractReader createGraphReader(InputStream in,
            String name, Graph graph) {
        return getInstance().create(in, name, graph.getEdgeTable());
    }

    public static boolean readGraph(String name, Graph graph) {
        return getInstance().tryRead(name, graph.getEdgeTable());
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

        public AbstractReader create(InputStream in, String name,
                Table table) {
            if (readerClass == null) {
                try {
                    readerClass = Class.forName(readerClassName);
                } catch (ClassNotFoundException e) {
                    logger.error(
                            "Cannot find reader class named "+readerClassName, 
                            e);
                    return null;
                }
            }
            Class[] parameterTypes = { 
                    InputStream.class,
                    String.class, 
                    Graph.class };
            Constructor cons = null;
            try {
                cons = readerClass.getConstructor(parameterTypes);
            } catch (NoSuchMethodException ex) {
                logger.error(
                        "Cannot find constructor for "+readerClassName, 
                        ex);
            }
            if (cons == null) {
                return null;
            }
            Graph graph = getGraph(table);
            Object[] args = { in, name, graph };
            try {
                return (AbstractReader) cons.newInstance(args);
            } catch (Exception e) {
                logger.error(
                        "Cannot instantiate graph reader "+readerClassName, 
                        e);
            }
            return null;
        }
    }
}