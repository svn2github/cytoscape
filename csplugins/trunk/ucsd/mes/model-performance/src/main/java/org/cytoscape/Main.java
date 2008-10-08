
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape;

import org.cytoscape.model.*;
import java.util.*; 


/**
 * DOCUMENT ME!
  */
public class Main {
	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		int totalNodes = 100000;
		int totalEdges = 200000;
		CyNetwork net = DupCyNetworkFactory.getInstance(); 

		for (int i = 0; i < totalNodes; i++) {
			net.addNode();
		}

		Random rand = new Random(totalNodes);
        for (int i = 0; i < totalEdges; i++) {
            int n1x = Math.abs(rand.nextInt() % (totalNodes-1));
            CyNode n1 = net.getNode( n1x );
            int n2x = Math.abs(rand.nextInt() % (totalNodes-1));
            CyNode n2 = net.getNode( n2x );
            net.addEdge( n1, n2, true );
        }

		System.out.println("num nodes: " + net.getNodeCount());
		System.out.println("num edges: " + net.getEdgeCount());
	}
}
