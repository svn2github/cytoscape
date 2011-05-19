
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

package cytoscape.hyperedge;

import cytoscape.CyEdge;


/**
 * Interface for defining CyEdge-based filters used for determining
 * which Edges should be used for a particular operation, such as
 * HyperEdge.copy().
 * @author Michael L. Creech
 */
public interface EdgeFilter {
    /**
     * A general filtering operation that separates certain Edges from others.
     
     * @param he the HyperEdge in question.
     * @param edge the edge within he in question.
     * @return true iff the Edge edge in he should be included in a specified
     * operation. For example, if we were performing a HyperEdge.copy(),
     * returning true would mean to copy this edge.
     * @see cytoscape.hyperedge.HyperEdge#copy
     */
    boolean includeEdge(HyperEdge he, CyEdge edge);
}
