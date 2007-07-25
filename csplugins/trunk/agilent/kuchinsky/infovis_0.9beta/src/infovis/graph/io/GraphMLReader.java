/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.*;
import infovis.column.StringColumn;
import infovis.io.AbstractXMLReader;
import infovis.io.WrongFormatException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * GraphML format reader.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.18 $
 * 
 * @infovis.factory GraphReaderFactory xml
 * @infovis.factory GraphReaderFactory gml
 */
public class GraphMLReader extends AbstractXMLReader {
    protected Graph        graph;
    protected int          inNode;
    protected int          inEdge;
    protected String       keyFor;
    protected String       ID;
    protected StringBuffer characters;
    protected StringColumn nodeIdColumn;
    protected StringColumn edgeIdColumn;
    protected Map          nodeMap;

    /**
     * Constructor for GraphMLReader.
     * 
     * @param in the BufferedReader
     * @param name the graph name
     * @param graph the graph.
     */
    public GraphMLReader(InputStream in, String name, Graph graph) {
        super(in, name);
        this.graph = graph;
    }

    /**
     * Declare a key.
     *
     * @param keyFor category the key is for, can be graph, edge or node.
     * @param ID identificator for this key.
     * @param type data type of this key.
     */
    public void declareKey(String keyFor, String ID, String type) {
        if (keyFor.equals("node")) {
            Column c = graph.getVertexTable().getColumn(ID);
            if (c == null) {
                c = createColumn(type, ID);
                graph.getVertexTable().addColumn(c);
//            } else if (c.getClass() != cl) {
//                throw new RuntimeException("bad column type " + c.getClass() +
//                                           " instead of " + cl);
            }
        } 
//        else if (keyFor.equals("graph")) {
//        }
        else if (keyFor.equals("edge")) {
            Table  edgeTable = graph.getEdgeTable();
            Column c = edgeTable.getColumn(ID);
            if (c == null) {
                c = createColumn(type, ID);
                edgeTable.addColumn(c);
//            } else if (c.getClass() != cl) {
//                throw new RuntimeException("bad column type " + c.getClass() +
//                                           " instead of " + cl);
            }
        }
    }

    /**
     * Returns a n ode given its unique id.
     *
     * @param id the unique id.
     *
     * @return a n ode given its unique id.
     */
    public int findNode(String id) {
        Integer i = (Integer)nodeMap.get(id);
        if (i == null) {
            int node = graph.addVertex();
            nodeIdColumn.setExtend(node, id);
            nodeMap.put(id, new Integer(node));
            return node;
        } else
            return i.intValue();
    }

    /**
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        inNode = inEdge = Graph.NIL;
        nodeMap = new HashMap();
        nodeIdColumn = StringColumn.findColumn(graph.getVertexTable(), "id");
        edgeIdColumn = null;
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
     */
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts)
                      throws SAXException {
        if (firstTag) {
       if (!qName.equals("graphml"))
            throw new WrongFormatException("Expected a graph toplevel element");
          firstTag = false;
        }
        if (qName.equals("graph")) {
            String edgedefault = atts.getValue("edgedefault");
            if ("directed".equals(edgedefault)) {
                graph.setDirected(true);
            }
            else if ("undirected".equals(edgedefault)) {
                graph.setDirected(false);
            }
            // otherwise, leave the graph as it is
        }
        if (qName.equals("key")) {
            keyFor = atts.getValue("for");
            ID = atts.getValue("id");
            characters = new StringBuffer();
        } else if (qName.equals("node")) {
            ID = atts.getValue("id");
            inNode = findNode(ID);
        } else if (qName.equals("edge")) {
            String source = atts.getValue("source");
            String target = atts.getValue("target");

            ID = atts.getValue("id");
            int from = findNode(source);
            int to = findNode(target);
            inEdge = graph.addEdge(from, to);
            if (ID != null) {
                if (edgeIdColumn == null) {
                    edgeIdColumn = 
                        StringColumn.findColumn(graph.getEdgeTable(), "id");
                }
                edgeIdColumn.setExtend(inEdge, ID);
            }
        } else if (qName.equals("data")) {
            if (inNode != Graph.NIL || inEdge != Graph.NIL) {
                keyFor = atts.getValue("key");
                characters = new StringBuffer();
            }
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void endElement(String namespaceURI, String localName, String qName)
                    throws SAXException {
        if (qName.equals("key")) {
            declareKey(keyFor, ID, characters.toString());
            characters = null;
            ID = null;
        } else if (qName.equals("node")) {
            inNode = Graph.NIL;
            ID = null;
        } else if (qName.equals("edge")) {
            inEdge = Graph.NIL;
            ID = null;
        } else if (qName.equals("data")) {
            Column c = null;
            int row = Graph.NIL;
            if (inNode != Graph.NIL) {
                c = graph.getVertexTable().getColumn(keyFor);
                row = inNode;
            }
            else if (inEdge != Graph.NIL) {
                c = graph.getEdgeTable().getColumn(keyFor);
                row = inEdge;
            }
            
            if (c != null) {
                String value = characters.toString();
                c.setValueOrNullAt(row, value);
            }
            characters = null;
            keyFor = null;
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
                    throws SAXException {
        if (characters != null) {
            characters.append(ch, start, length);
        }
    }
}
