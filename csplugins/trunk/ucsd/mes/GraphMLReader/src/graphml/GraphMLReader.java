
/*
 File: XGMMLReader.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute of Systems Biology
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
package graphml; 

import java.io.*;
import java.util.*;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamConstants;

import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.AbstractGraphReader;

import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;

/**
 * Rudimentary GraphML file reader.
 */
public class GraphMLReader extends AbstractGraphReader {

	private List<CyEdge> edges;
	private List<CyNode> nodes;

	private int[] edgeInds;
	private int[] nodeInds;

	public GraphMLReader(String fileName) {
		super(fileName);
		edges = new LinkedList<CyEdge>();
		nodes = new LinkedList<CyNode>();
		edgeInds = null;
		nodeInds = null;
	}

	public int[] getNodeIndicesArray() {
		return nodeInds;
	}

	public int[] getEdgeIndicesArray() {
		return edgeInds;
	}
	
	public void read() throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(fileInputStream);

            while (xmlStreamReader.hasNext()) {
                printEventInfo(xmlStreamReader);
            }
            xmlStreamReader.close();
        } catch (Throwable e) {
			throw new IOException(e.getMessage());
			//throw new IOException(e); // java 1.6 only!
        }
    }

    private void printEventInfo(XMLStreamReader reader) throws XMLStreamException {
        int eventCode = reader.next();
        switch (eventCode) {

            case XMLStreamConstants.START_ELEMENT :

				// create edge
				if ( reader.getLocalName().equals("edge") ) {
					String src = reader.getAttributeValue(0);
					String trg = reader.getAttributeValue(1);
					String id = CyEdge.createIdentifier(src, "xx", trg); 
					CyEdge edge = Cytoscape.getCyEdge(src,id,trg,"xx");
					edges.add( edge );

				// create node
				} else if ( reader.getLocalName().equals("node") ) {
					String nodeName = reader.getAttributeValue(0);
					CyNode node = Cytoscape.getCyNode(nodeName, true);
					nodes.add( node );
				}
                break;

            case XMLStreamConstants.END_DOCUMENT :
				// copy edge indices
				edgeInds = new int[edges.size()];
				int i = 0;
				for ( CyEdge edge : edges )
					edgeInds[i++] = edge.getRootGraphIndex();
					
				// copy node indices
				nodeInds = new int[nodes.size()];
				i = 0;
				for ( CyNode node : nodes )
					nodeInds[i++] = node.getRootGraphIndex();

                break;

            case XMLStreamConstants.END_ELEMENT :
            case XMLStreamConstants.PROCESSING_INSTRUCTION :
            case XMLStreamConstants.CHARACTERS :
            case XMLStreamConstants.COMMENT :
            case XMLStreamConstants.SPACE :
            case XMLStreamConstants.START_DOCUMENT :
            case XMLStreamConstants.ENTITY_REFERENCE :
            case XMLStreamConstants.DTD :
            case XMLStreamConstants.CDATA :
			default :
                break;
        }
    }
}
