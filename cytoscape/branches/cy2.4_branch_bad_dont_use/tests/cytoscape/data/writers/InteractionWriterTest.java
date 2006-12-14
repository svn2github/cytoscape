
/*
  File: InteractionWriter.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.data.writers;

import junit.framework.TestCase;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.writers.InteractionWriter;

import giny.model.RootGraph;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Tests the InteractionWriter Class.
 *
 */
public class InteractionWriterTest extends TestCase {


    protected CyNetwork net;
    protected int a,b,c,d,e,ab,bc,ac,bd;

    public void setUp() {

	RootGraph root = Cytoscape.getRootGraph();

        a = root.createNode();
        b = root.createNode();
        c = root.createNode();
        d = root.createNode();
        e = root.createNode();

        int[] nodes = new int[] {a,b,c,d,e};

        ab = root.createEdge(a,b,true);
        bc = root.createEdge(b,c,true);
        ac = root.createEdge(a,c,true);
        bd = root.createEdge(b,d,true);

        int[] edges = new int[] {ab,bc,ac,bd};

        net = Cytoscape.createNetwork(nodes,edges,"graph");

    }

    /**
     * Verify that the writer is actually writing.
     */
    public void testWrite() { 
    	try {
        	StringWriter writer = new StringWriter();
	        InteractionWriter.writeInteractions(net, writer);
       		String output = writer.toString();
		checkSIF(output);
	} catch (IOException e) {
		fail(e.getMessage());
	}
    }

    /**
     * Verify that the string written is ok.
     */
    public void testInteractionString() {
	checkSIF(InteractionWriter.getInteractionString(net));
    }

    protected void checkSIF(String s) {
	String[] lines = s.split(System.getProperty("line.separator"));
	System.out.println("begin sif string");
	System.out.println(s);
	System.out.println("end sif string");
	assertEquals("number of lines",5,lines.length);

	int edgeLines = 0;
	int nodeLines = 0;
	// Since we don't necessarily know the node ids create
	// regular expressions of the lines.
	for ( int i = 0; i < lines.length; i++ ) {
		if ( checkLine(lines[i],a,b) )
			edgeLines++;
		else if ( checkLine(lines[i],b,c) )
			edgeLines++;
		else if ( checkLine(lines[i],a,c) )
			edgeLines++;
		else if ( checkLine(lines[i],b,d) )
			edgeLines++;
		else if ( lines[i].matches("^" + e + "$") )
			nodeLines++;
	}	
	assertEquals("number of edge lines",4,edgeLines);
	assertEquals("number of node only lines",1,nodeLines);
    }

    protected boolean checkLine(String s, int a, int b) {
	if ( s.matches("^" + a + "\\s+xx\\s+" + b + "$") ||
	     s.matches("^" + b + "\\s+xx\\s+" + a + "$") ) 
	     return true;
	else
		return false;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(InteractionWriterTest.class);
    }
}
