
//============================================================================
// 
//  file: SIFWriter.java
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
 */
public class SIFWriter<NodeType extends Comparable<? super NodeType>,
                       WeightType extends Comparable<? super WeightType>> { 

	public SIFWriter(Graph<NodeType,WeightType> g, String name) throws IOException {
		String sif = getSIFString(g);
		FileWriter fw = new FileWriter(name);
		fw.write(sif,0,sif.length());
		fw.close();
	}

	public static <NodeType extends Comparable<? super NodeType>, WeightType extends Comparable<? super WeightType>> String getSIFString(Graph<NodeType,WeightType> g) {
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
