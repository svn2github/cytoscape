/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Column;
import infovis.Graph;
import infovis.column.StringColumn;
import infovis.io.WrongFormatException;

import java.awt.geom.Rectangle2D.Float;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Reader for the GML graph file format.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 * 
 * @infovis.factory GraphReaderFactory gml
 */
public class GMLGraphReader extends AbstractGraphReader {
    static final Logger logger = Logger.getLogger(GMLGraphReader.class);
    protected StreamTokenizer tok;
    protected HashMap idNodeMap;
    
    public GMLGraphReader(
            InputStream in,
            String name,
            Graph graph) {
        super(in, name, graph);
    }
    
    public Float getBbox() {
        return null;
    }
    
    public ArrayList parseList() throws IOException {
        ArrayList list = new ArrayList();
        loop: while(true) {
            int t = tok.nextToken();
            if (t == ']' || t == StreamTokenizer.TT_EOF) {
                return list;
            }
            else if (t != StreamTokenizer.TT_WORD) {
                break;
            }
            String key = tok.sval;
            list.add(key);
            t = tok.nextToken();
            switch(t) {
            case '[':
                list.add(parseList());
                break;
            case StreamTokenizer.TT_NUMBER:
                list.add(new Double(tok.nval));
                break;
            case StreamTokenizer.TT_WORD:
            case '"':
                list.add(tok.sval);
                break;
            default:
                break loop;
            }
        }
        String errString = "Unexpected token type line "+tok.lineno();
        logger.error(errString);
        throw new IOException(errString);
    }
    
    public boolean load() throws WrongFormatException {
       idNodeMap = new HashMap();
        ArrayList list;
        tok = new StreamTokenizer(getBufferedReader());
        tok.ordinaryChar('[');
        tok.ordinaryChar(']');
        tok.wordChars('_', '_');
        
        try {
            list = parseList();
        }
        catch (Exception e) {
            logger.error("Cannot parse file "+getName(), e);
            return false;
        }
        finally {
            try {
                getBufferedReader().close();
            }
            catch (IOException e) {
                logger.error("Error closing file "+getName(), e);
            }
        }
        if (list.size() != 2
                || (!"graph".equals(list.get(0)))
                || ! (list.get(1) instanceof ArrayList)) {
            logger.error("Invalid GraphML file "+getName());
            return false;
        }
        return parseGraph((ArrayList)list.get(1));
    }
    
    public int getNode(String id) {
        Object node = idNodeMap.get(id);
        if (node == null) {
            return -1;
        }
        return ((Integer)node).intValue();
    }
    
    public int findNode(String id) {
        Object node = idNodeMap.get(id);
        if (node == null) {
            int n = graph.addVertex();
            node = new Integer(n);
            idNodeMap.put(id, node);
            return n;
        }
        return ((Integer)node).intValue();
    }
    
    public boolean parseGraph(ArrayList list) {
        if ((list.size()&1) != 0) {
            logger.error("Invalid GML list sizr="+list.size());
            return false;
        }
        boolean ret = true;
        for (int i = 0; i < list.size(); i+= 2) {
            Object key = list.get(i);
            Object value = list.get(i+1);
            if ("node".equals(key)) {
                ret = parseNode((ArrayList)value);
            }
            else if ("edge".equals(key)) {
                ret = parseEdge((ArrayList)value);
            }
            else {
                graph.getMetadata().addAttribute(key, value);
            }
            if (! ret) {
                break;
            }
        }
        return ret;
    }
    
    public boolean parseNode(ArrayList list) {
        int node = -1;
        for (int i = 0; i < list.size(); i += 2) {
            String key = (String)list.get(i);
            Object value = list.get(i+1);
            if ("id".equals(key)) {
                node = getNode(value.toString());
                if (node != -1) {
                    logger.error("Duplicate node id in "+getName());
                }
                else {
                    node = findNode(value.toString());
                }
                break;
            }
        }
        if (node == -1) {
            logger.error("No id defined for node in "+getName());
            return false;
        }
        return addNodeAttributes(node, "", list);
        //return true;
    }
    
    public boolean addNodeAttributes(int node, String prefix, ArrayList list) {
        boolean ret = true;
        for (int i = 0; i < list.size(); i += 2) {
            String key = (String)list.get(i);
            Object value = list.get(i+1);
            if ("id".equals(key)) {
                continue; // already parsed
            }
            if (value instanceof ArrayList) {
                // keep the  hierarchy
                ret = addNodeAttributes(node, prefix+"."+key, (ArrayList)value);
                if (! ret) {
                    break;
                }
            }
            else {
                Column c = graph.getVertexTable().getColumn(key);
                if (c == null) {
                    c = new StringColumn(key);
                    graph.getVertexTable().addColumn(c);
                }
                c.setValueOrNullAt(node, value.toString());
            }
        }
        return ret;
    }
    
    public boolean parseEdge(ArrayList list) {
        int source = -1;
        int target = -1;
        for (int i = 0; i < list.size(); i += 2) {
            String key = (String) list.get(i);
            Object value = list.get(i + 1);
            if ("source".equals(key)) {
                source = getNode(value.toString());
                if (source == -1) {
                    logger.error("Undefined node id in edge "
                            + getName());
                    source = findNode(value.toString());
                }
            } else if ("target".equals(key)) {
                target = getNode(value.toString());
                if (target == -1) {
                    logger.error("Undefined node id in edge "
                            + getName());
                    target = findNode(value.toString());
                }
            }
            if (source != -1 && target != -1) {
                break;
            }
        }
        if (source == -1 || target == -1) {
            logger.error("Missing source or target for edge in "
                    + getName());
            return false;
        }
        int edge = graph.findEdge(source, target);
        return addEdgeAttributes(edge, "", list);
    }
    
    public boolean addEdgeAttributes(int edge, String prefix, ArrayList list) {
        boolean ret = true;
        for (int i = 0; i < list.size(); i += 2) {
            String key = (String)list.get(i);
            Object value = list.get(i+1);
            if ("source".equals(key) || "target".equals(key)) {
                continue; // already parsed
            }
            if (value instanceof ArrayList) {
                // keep the hierarchy
                ret = addEdgeAttributes(edge, prefix+"."+key, (ArrayList)value);
                if (! ret) {
                    break;
                }
            }
            else if ("directed".equals(key)) {
                if (value instanceof Integer) {
                    graph.setDirected(((Integer)value).intValue() != 0);
                }
                else {
                    logger.error("Unexpected value for 'directed':"+value.toString());
                }
            }
            else {
                Column c = graph.getEdgeTable().getColumn(key);
                if (c == null) {
                    c = new StringColumn(key);
                    graph.getEdgeTable().addColumn(c);
                }
                c.setValueOrNullAt(edge, value.toString());
            }
        }
        return ret;        
    }
}
