/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.graph.io;

import infovis.*;
import infovis.io.AbstractXMLWriter;
import infovis.utils.RowIterator;

import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Writer for the GraphML format.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.22 $
 * @infovis.factory GraphWriterFactory gml GraphML
 */
public class GraphMLWriter extends AbstractXMLWriter {
    private Logger           logger = Logger.getLogger(GraphMLWriter.class);
    protected Graph          graph;
    /** DefaultTable of edge labels */
    protected ArrayList      edgeLabels;
    protected AttributesImpl attrs  = new AttributesImpl();
    protected boolean        interlaced;
    protected Column         vertexIdColumn;
    protected Column         edgeIdColumn;

    public GraphMLWriter(OutputStream out, Graph graph) {
        super(out, graph.getEdgeTable());
        this.graph = graph;
    }

    public void addEdgeLabel(String name) {
        if (edgeLabels == null) {
            edgeLabels = new ArrayList();
        }

        edgeLabels.add(name);
    }

    public String getVertexId(int node) {
        if (vertexIdColumn == null || vertexIdColumn.isValueUndefined(node)) {
            return "n" + node;
        }
        else {
            return vertexIdColumn.getValueAt(node);
        }
    }

    public String getEdgeId(int edge) {
        if (edgeIdColumn == null || edgeIdColumn.isValueUndefined(edge)) {
            return "e" + edge;
        }
        else {
            return edgeIdColumn.getValueAt(edge);
        }
    }

    public void addAllEdgeLabels() {
        int col;
        Table edges = graph.getEdgeTable();

        for (col = 0; col < edges.getColumnCount(); col++) {
            Column c = edges.getColumnAt(col);

            if (c.isInternal() || namedType(c) == null) {
                continue;
            }

            addEdgeLabel(c.getName());
        }
    }

    public void addAllColumnLabels() {
        int col;
        Table vertices = graph.getVertexTable();

        for (col = 0; col < vertices.getColumnCount(); col++) {
            Column c = vertices.getColumnAt(col);

            if (c.isInternal() || namedType(c) == null) {
                continue;
            }

            addColumnLabel(c.getName());
        }
    }

    public String getEdgeLabelAt(int col) {
        return (String) edgeLabels.get(col);
    }

    public ArrayList getEdgeLabels() {
        return edgeLabels;
    }

    /**
     * Writes the data associated with a specified edge.
     * 
     * @param edge
     *            the edge.
     * 
     * @exception SAXException
     *                passed from the underlying XMLWriter.
     */
    protected void writeEdge(int edge) throws SAXException {
        Table edgeTable = graph.getEdgeTable();

        indent(writer);
        attrs.clear();
        attrs.addAttribute("", "id", "id", "ID", getEdgeId(edge));
        attrs.addAttribute("", "source", "source", "IDREF", getVertexId(graph
                .getFirstVertex(edge)));
        attrs.addAttribute("", "target", "tager", "IDREF", getVertexId(graph
                .getSecondVertex(edge)));
        writer.startElement("", "edge", "", attrs);
        depth++;
        for (int col = 0; edgeLabels != null && col < edgeLabels.size(); col++) {
            String label = getEdgeLabelAt(col);
            Column c = edgeTable.getColumn(label);
            if (c == edgeIdColumn)
                continue;

            if (!c.isValueUndefined(edge)) {
                indent(writer);
                attrs.clear();
                attrs.addAttribute("", "key", "key", "IDREF", c.getName());
                writer.startElement("", "data", "", attrs);
                writer.characters(c.getValueAt(edge));
                writer.endElement("data");
            }
        }

        depth--;
        indent(writer);
        writer.endElement("edge");
    }

    /**
     * Writes the data associated with a specified vertex.
     * 
     * @param vertex
     *            the vertex.
     * 
     * @exception SAXException
     *                passed from the underlying XMLWriter.
     */
    protected void writeVertex(int vertex) throws SAXException {
        ArrayList columnLabels = getColumnLabels();
        indent(writer);
        attrs.clear();
        attrs.addAttribute("", "id", "id", "ID", getVertexId(vertex));
        writer.startElement("", "node", "", attrs);
        depth++;
        for (int col = 0; columnLabels != null && col < columnLabels.size(); col++) {
            String label = getColumnLabelAt(col);
            Column c = graph.getVertexTable().getColumn(label);
            if (c == vertexIdColumn)
                continue;

            if (!c.isValueUndefined(vertex)) {
                indent(writer);
                attrs.clear();
                attrs.addAttribute("", "key", "key", "IDREF", c.getName());
                writer.startElement("", "data", "", attrs);
                writer.characters(c.getValueAt(vertex));
                writer.endElement("data");
            }
        }
        depth--;
        indent(writer);
        writer.endElement("node");
        if (interlaced) {
            for (RowIterator iter = graph.outEdgeIterator(vertex); iter
                    .hasNext();) {
                int edge = iter.nextRow();
                writeEdge(edge);
            }
        }
    }

    /**
     * @see infovis.io.AbstractWriter#write()
     */
    public boolean write() {
        ArrayList columnLabels = getColumnLabels();
        ArrayList edgeLabels = getEdgeLabels();

        int col;

        if (columnLabels == null) {
            addAllColumnLabels();
            columnLabels = getColumnLabels();
        }

        if (edgeLabels == null) {
            addAllEdgeLabels();
            edgeLabels = getEdgeLabels();
        }
        depth = 0;
        try {
            writer.startDocument();
            writer.flush();
            write("<!DOCTYPE graphml SYSTEM 'http://graphml.graphdrawing.org/dtds/1.0rc/graphml.dtd'>\n");
            writer.startElement("graphml");
            depth++;
            indent(writer);
            if (graph.isDirected()) {
                attrs.addAttribute(
                        "",
                        "edgedefault",
                        "edgedefault",
                        "CDATA",
                        "directed");
            }
            else {
                attrs.addAttribute(
                        "",
                        "edgedefault",
                        "edgedefault",
                        "CDATA",
                        "undirected");
            }
            writer.startElement("", "graph", "", attrs);
            attrs.clear();
            depth++;
            attrs.addAttribute("", "id", "id", "ID", "0");
            attrs.addAttribute("", "for", "for", "CDATA", "node");

            for (col = 0; columnLabels != null && col < columnLabels.size(); col++) {
                String label = getColumnLabelAt(col);
                Column c = graph.getVertexTable().getColumn(label);

                indent(writer);
                attrs.setAttribute(0, "", "id", "id", "ID", c.getName());
                writer.startElement("", "key", "", attrs);
                writer.characters(namedType(c));
                writer.endElement("key");
            }

            attrs.setAttribute(1, "", "for", "for", "CDATA", "edge");
            Table edgeTable = graph.getEdgeTable();
            for (col = 0; edgeLabels != null && col < edgeLabels.size(); col++) {
                String label = getEdgeLabelAt(col);
                Column c = edgeTable.getColumn(label);

                indent(writer);
                // attNameFor[0] = "id";
                // attName[1] = c.getName();
                attrs.setAttribute(0, "", "id", "id", "ID", c.getName());
                writer.startElement("", "key", "", attrs);
                writer.characters(namedType(c));
                writer.endElement("key");
            }

            for (RowIterator iter = graph.vertexIterator(); iter.hasNext();) {
                int vertex = iter.nextRow();
                writeVertex(vertex);
            }

            if (!interlaced) {
                for (RowIterator iter = graph.edgeIterator(); iter.hasNext();) {
                    int edge = iter.nextRow();
                    writeEdge(edge);
                }
            }

            depth--;
            indent(writer);
            writer.endElement("graph");
            depth--;
            indent(writer);
            writer.endElement("graphml");
            writer.endDocument();
        } catch (Exception e) {
            logger.error("Error wrinting GraphML file ", e);
            return false;
        }
        return true;
    }

    /**
     * Returns the interlaced.
     * 
     * @return boolean
     */
    public boolean isInterlaced() {
        return interlaced;
    }

    /**
     * Sets the interlaced.
     * 
     * @param interlaced
     *            The interlaced to set
     */
    public void setInterlaced(boolean interlaced) {
        this.interlaced = interlaced;
    }

    public Column getEdgeIdColumn() {
        return edgeIdColumn;
    }

    public void setEdgeIdColumn(Column edgeIdColumn) {
        this.edgeIdColumn = edgeIdColumn;
    }

    public Column getVertexIdColumn() {
        return vertexIdColumn;
    }

    public void setVertexIdColumn(Column vertexIdColumn) {
        this.vertexIdColumn = vertexIdColumn;
    }
}