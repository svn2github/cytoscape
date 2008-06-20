/*
 File: NetworkMerge.java

 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package csplugins.network.merge;

import java.util.Set;

import cytoscape.CyNetwork;

import giny.model.Node;

public interface NetworkMerge {
    public final String ID = "ID";
    
    public enum Operation {
        UNION("union"),INTERSECTION("intersection"),DIFFERENCE("difference");
        private String opName;
        
        private Operation(String opName) {
            this.opName = opName;            
        }

        public String toString() {
            return opName;
        }
    }
    
    /**
     * Check whether two nodes match
     *
     * @param n1 Node 1
     * @param n2 Node 2
     * 
     * return true if n1 and n2 matches
     */
    public boolean matchNode(Node n1, Node n2);
    
    /**
     * Merge (matched) nodes into one
     *
     * @param nodes Nodes to be merged
     * 
     * return merged Node
     */
    public Node mergeNode(Set<Node> nodes);
    
    /**
     * Merge networks into one
     *
     * @param networks Networks to be merged
     * 
     * return merged Node
     */
    public CyNetwork mergeNetwork(Set<CyNetwork> networks, Operation op);
    
    
}
