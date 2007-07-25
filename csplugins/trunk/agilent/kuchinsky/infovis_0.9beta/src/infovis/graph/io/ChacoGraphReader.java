/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.io.WrongFormatException;

import java.awt.geom.Rectangle2D.Float;
import java.io.BufferedReader;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Reader for the Chaco format.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 * 
 * @infovis.factory GraphReaderFactory graph
 */
public class ChacoGraphReader extends AbstractGraphReader {
    private static final Logger logger = Logger.getLogger(ChacoGraphReader.class);
    
    public ChacoGraphReader(InputStream in, String name, Graph graph) {
        super(in, name, graph);
    }

    public Float getBbox() {
        return null;
    }
    
    public static boolean isComment(String line) {
        return (line.charAt(0)=='#' || line.charAt(0)=='%');
    }

    public boolean load() throws WrongFormatException {
        graph.setDirected(false);
        BufferedReader in = getBufferedReader();
        
        try {
            String[] fields;
            String line;
            
            while (true) {
                line = in.readLine();
                if (line == null) return false;
                if (! isComment(line)) 
                    break;
            }
            fields = line.split(" ");
            if (fields.length < 2 || fields.length > 3) {
                throw new WrongFormatException(
                        "Expected 2 or 3 values at first line");
            }
            int vertices = Integer.parseInt(fields[0]);
            int edges = Integer.parseInt(fields[1]);
            if (fields.length > 2) {
                throw new WrongFormatException(
                        "Unsupported Chaco file with attributes or vertex number");
            }
            //TODO: read attributes
            int[] vertexMap = new int[vertices];
            int v;
            int edgesRead = 0;
            for (v = 0; v < vertices; v++) {
                vertexMap[v] = graph.addVertex();
            }
            for (v = 0; v < vertices; ) {
                int vertex = vertexMap[v];
                line = in.readLine();
                if (line == null) {
                    throw new WrongFormatException(
                    "Unexpected EOL while reading vertex #"+v);
                }
                if (isComment(line)) continue;
                fields = line.split(" ");
                for (int i = 0; i < fields.length; i++) {
                    int v2 = Integer.parseInt(fields[i]);
                    graph.addEdge(vertex, vertexMap[v2]);
                }
                edgesRead += fields.length;
                v++;
            }
            if (edgesRead != edges) {
                logger.warn("Read "+edgesRead+" edges instead of "+edges);
            }
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }

}
