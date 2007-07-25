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
import java.io.StreamTokenizer;

import cern.colt.map.OpenIntIntHashMap;

/**
 * Class AdjGraphReader
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * 
 * @infovis.factory GraphReaderFactory adj
 */
public class AdjGraphReader extends AbstractGraphReader {
    
    public AdjGraphReader(BufferedReader in, String name, Graph graph) {
        super(in, name, graph);
    }

    public Float getBbox() {
        return null;
    }

    public boolean load() throws WrongFormatException {
        OpenIntIntHashMap vertexMap = new OpenIntIntHashMap();
        graph.setDirected(false);

        try {
            StreamTokenizer tok = new StreamTokenizer(in);
            while (true) {
                int t = tok.nextToken();
                if (t == StreamTokenizer.TT_EOF) break;
                if (t != StreamTokenizer.TT_NUMBER) return false;
                int t1 = (int)tok.nval;
                int v1;
                if (vertexMap.containsKey(t1)) {
                    v1 = vertexMap.get(t1);
                }
                else {
                    v1 = graph.addVertex();
                    vertexMap.put(t1, v1);
                }
                t = tok.nextToken();
                if (t == StreamTokenizer.TT_EOF) break;
                if (t != StreamTokenizer.TT_NUMBER) return false;
                int t2 = (int)tok.nval;
                int v2;
                if (vertexMap.containsKey(t2)) {
                    v2 = vertexMap.get(t2);
                }
                else {
                    v2 = graph.addVertex();
                    vertexMap.put(t2, v2);
                }
                graph.findEdge(v1, v2);
            }
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }

}
