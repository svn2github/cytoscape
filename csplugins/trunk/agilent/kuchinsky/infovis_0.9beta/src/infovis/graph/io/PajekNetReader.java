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
import infovis.column.DoubleColumn;
import infovis.column.StringColumn;
import infovis.io.WrongFormatException;

import java.awt.geom.Rectangle2D.Float;
import java.io.*;

import org.apache.log4j.Logger;

/**
 * Class PajekNetReader
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 * 
 * @infovis.factory GraphReaderFactory net
 */
public class PajekNetReader extends AbstractGraphReader {
    private static Logger logger = Logger.getLogger(PajekNetReader.class);
    protected StreamTokenizer tok;
    protected Column labelColumn;
    protected DoubleColumn weightColumn;
    
    public PajekNetReader(InputStream in, String name, Graph graph) {
        super(in, name, graph);
    }

    public Float getBbox() {
        return null;
    }
    public String readString() throws IOException {
        if (tok.nextToken() != '"') {
            throw new IOException("Unexpected token, expecting a String");
        }
        return tok.sval;
        
    }
    public String readWord() throws IOException {
        if (tok.nextToken() != StreamTokenizer.TT_WORD) {
            throw new IOException("Unexpected token, expecting a word");
        }
        return tok.sval;
    }
    
    public int readInt() throws IOException {
        if (tok.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Unexpected token, expecting a word");
        }
        int val = (int)tok.nval;
        if (val != tok.nval) {
            throw new IOException("Expected an int, got a real");
        }
        return val;
    }
    
    public double readValue() throws IOException {
        if (tok.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Unexpected token, expecting a word");
        }
        return tok.nval;
    }
    
    public boolean readEol() throws IOException {
        return tok.nextToken() == StreamTokenizer.TT_EOL;
    }
    
    public void skipToEol() throws IOException {
        while(! readEol()) ;
    }
    
    public void readVertex(int n, int v) throws IOException {
        int i = readInt()-1;
        if (n != i) {
            throw new IOException("Expected vertex#"+n+" received#"+i);
        }
        String label = readString();
        labelColumn.setValueOrNullAt(v, label);
        //skip other information for nom
        //TODO read the specified info
        skipToEol();
    }
    
    public boolean load() throws WrongFormatException {
        try {
            tok = new StreamTokenizer(getBufferedReader());
            tok.wordChars('*', '*');
            tok.eolIsSignificant(true);
            
            if (! readWord().equalsIgnoreCase("*vertices")) {
                logger.error("Invalid Pajek .NET file "+getName());
                return false;
            }
            int vertexCount = readInt();
            skipToEol();
            int[] vertices = new int[vertexCount];
            labelColumn = StringColumn.findColumn(graph.getVertexTable(), "label");
            for (int i = 0; i < vertexCount; i++) {
                int v = graph.addVertex();
                vertices[i] = v;
                readVertex(i, v);
            }
            while(true) {
                String edges = readWord();
                if (edges.equalsIgnoreCase("*edges")) {
                    graph.setDirected(false);
                }
                else if (edges.equalsIgnoreCase("*arcs")) {
                    graph.setDirected(true);
                }
                else {
                    logger.error("Unknown edge type: "+edges);
                    return false;
                }
                skipToEol();
                weightColumn = DoubleColumn.findColumn(graph.getEdgeTable(), "weight");
                while(true){
                    int next = tok.nextToken();
                    if (next == StreamTokenizer.TT_EOF) {
                        return true;
                    }
                    tok.pushBack();
                    if (next == StreamTokenizer.TT_WORD) {
                        break; // expect *edges or *arcs
                    }
                    int from = readInt()-1;
                    int to = readInt()-1;
                    double v = readValue();
                    int edge = graph.addEdge(vertices[from], vertices[to]);
                    weightColumn.setExtend(edge, v);
                    skipToEol();
                }
            }
        }
        catch(Exception e) {
            logger.error("while reading pajek net file ", e);
            return false;
        }
        //return true;
    }

}
