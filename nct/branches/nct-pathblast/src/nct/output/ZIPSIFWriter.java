
//============================================================================
// 
//  file: ZIPSIFWriter.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
public class ZIPSIFWriter<NodeType extends Comparable<? super NodeType>,
                          WeightType extends Comparable<? super WeightType>> { 

    /**
     * The output stream.
     */
     protected ZipOutputStream out; 

    /**
     * The content to be written. 
     */
     protected StringBuffer content; 

    /**
     * The bytes of the content to be written. 
     */
     protected byte[] bytes; 

    /**
     * Sets the output filename
     * @param fname The filename to write zip file to.
     */
    public ZIPSIFWriter(String fname) throws IOException {
	assert(fname != null);
	content = new StringBuffer();
	out = new ZipOutputStream(new FileOutputStream(fname + ".zip"));
	out.setLevel(9); // set compression level to maximum
    }

    /**
     * Adds graph to zip archive. 
     * @param graph The graph to be written as a SIF file and included in the archive. 
     * @param name The name of the SIF file to be written to the archive. 
     */
    public void add(Graph<NodeType,WeightType> graph, String name ) throws IOException {
	    out.putNextEntry(new ZipEntry( name +  ".sif" ));
	    content.delete(0,content.length());
	    content.append(getSIFString(graph));
	    bytes = content.toString().getBytes();
	    out.write(bytes,0,bytes.length);
    }

    /**
     * Finishes writing the ZIP file.  Call this after you've added the graphs you want
     * to the archive.
     */
    public void write() throws IOException {
        out.close();
    }

    private String getSIFString(Graph<NodeType,WeightType> g) {
    	StringBuffer b = new StringBuffer();
	String newline = System.getProperty("line.separator");
	for ( Edge<NodeType,WeightType> e : g.getEdges() ) {
		b.append( e.getSourceNode().toString() );
		b.append(" ");
		b.append( e.getDescription() );
		b.append(" ");
		b.append( e.getTargetNode().toString() );
		b.append( newline );
	}
	return b.toString();
    }
}
