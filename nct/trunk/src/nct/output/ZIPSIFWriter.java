
//============================================================================
// 
//  file: ZIPSIFWriter.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.output;

import java.io.*;
import java.util.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

import nct.graph.Graph;
import nct.graph.Edge;

/**
 * This class writes the graphs contained in a given list out in
 * SIF format to individual files contained within a ZIP archive
 * file.
 */
public class ZIPSIFWriter<NodeType extends Comparable<NodeType>,
                          WeightType extends Comparable<WeightType>> { 
    /**
     * The base filename to write out
     */
    private String baseFileName;

    /**
     * Sets the output filename
     * @param fname The filename to write zip file to.
     */
    public ZIPSIFWriter(String fname) {
	assert(fname != null);
	baseFileName = fname;
    }

    /**
     * Writes the specified list of graphs as individual SIF files compressed
     * into a ZIP archive.
     * @param graphs The list of graphs to be written as SIF files. 
     */
    public void write(List<Graph<NodeType,WeightType>> graphs) throws IOException {
	assert(graphs != null);
	int totalGraphs = 0;
	byte[] bytes;
	StringBuffer content = new StringBuffer();

	ZipOutputStream out = new ZipOutputStream(new FileOutputStream(baseFileName + ".zip"));
	out.setLevel(9); // set compression level to maximum

	for (Graph<NodeType,WeightType> graph: graphs) { 
	    out.putNextEntry(new ZipEntry( baseFileName + "_" + (totalGraphs++) + ".sif" ));
	    content.append(getSIFString(graph));
	    bytes = content.toString().getBytes();
	    out.write(bytes,0,bytes.length);
	}

        out.close();
    }

    private String getSIFString(Graph<NodeType,WeightType> g) {
    	StringBuffer b = new StringBuffer();
	String newline = System.getProperty("line.separator");
	for ( Edge<NodeType,WeightType> e : g.getEdges() ) {
		b.append( e.getSourceNode().toString() );
		b.append(" ");
		b.append( e.getWeight().toString() );
		b.append(" ");
		b.append( e.getTargetNode().toString() );
		b.append(" ");
		b.append( e.getTargetNode().toString() );
		b.append( newline );
	}
	return b.toString();
    }
}
