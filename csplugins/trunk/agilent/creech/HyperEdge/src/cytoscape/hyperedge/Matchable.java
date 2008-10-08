
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

/*
*
* Revisions:
*
* Tue Aug 22 16:18:31 2006 (Michael L. Creech) creech@w235krbza760
*  
********************************************************************************
*/



package cytoscape.hyperedge;

/**
 * Interface used for defining similarity functions between HyperEdge objects.
 * @author Michael L. Creech
 * @version 1.0
 */

public interface Matchable {
    /**
     * Return if two HyperEdges are similar.
     * @param simMatcher the similarity matcher to use for determining
     * if two HyperEdges  are similar.
     * @param he the HyperEdge to compare against this object.
     * @param optArgs an object the may contain any optional information
     * needed for computing the similarity.
     * @return true if this object is similar to he. false if he is null,
     * simMatcher is null, or if this object is not similar to he.
     * 
     */
    boolean isSimilar(SimMatcher simMatcher, HyperEdge he, Object optArgs);

}
