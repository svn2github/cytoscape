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

import java.io.Writer;

/**
 * Class GraphWriterFactory
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class GraphWriterFactory extends AbstractWriterFactory {
    static GraphWriterFactory instance = new GraphWriterFactory();
    
    /**
     * Returns a Graph from a specified table.
     *
     * @param table the table.
     *
     * @return a Graph from a specified table.
     */
    public static Graph getGraph(Table table) {
        return DefaultGraph.getGraph(table);
    }
    

    protected void addDefaultCreators() {
        add(new AbstractCreator("xml") {
            public AbstractWriter create(
                Writer out,
                Table table) {
                return new GraphMLWriter(
                    out,
                    getGraph(table));
            }
        });
        add(new AbstractCreator("dot") {
            public AbstractWriter create(
                Writer out,
                Table table) {
                return new DOTGraphWriter(
                    out,
                    getGraph(table));
            }
        });
        add(new AbstractCreator("vcg") {
            public AbstractWriter create(
                Writer out,
                Table table) {
                return new VCGGraphWriter(
                    out,
                    getGraph(table));
            }
        });
    }

    /**
     * Returns the shared instance of this <code>GraphReaderFactory</code>
     *
     * @return the shared instance of this <code>GraphReaderFactory</code>
     */
    public static GraphWriterFactory sharedInstance() {
        return instance;
    }

	public static void setInstance(GraphWriterFactory graph){
			instance= graph;
		}
	
    /**
     * Creates a graph reader from a specified resource name and a graph
     *
     * @param name the resource name
     * @param graph the graph
     *
     * @return a graph reader or <code>null</code>.
     */
    public static AbstractWriter createGraphWriter(
        String name,
        Graph graph) {
        return sharedInstance().create(name, graph.getEdgeTable());
    }

    public static boolean writeGraph(String name, Graph graph) {
        return sharedInstance().tryWrite(name, graph.getEdgeTable());
    }
}
