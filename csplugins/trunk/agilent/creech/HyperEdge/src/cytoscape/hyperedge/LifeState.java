
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


/**
 * Constants that represent the state of HyperEdge objects.
 * @author Michael L. Creech
 * @version 1.0
 */
public enum LifeState {
    /**
     * The state of an object being created. This is used by other objects
     * to determine when to trigger various events and used by users to
     * tell if it is safe to examine this object.
     */
    CREATION_IN_PROGRESS, 
    /**
     * The normal state of an object when it is fully formed and available
     * for use.
     */
    NORMAL, 
    /**
     * The state of an object that has been deleted.
     * This is used to throw IllegalStateExceptions when any attempt is
     * made to perform operations on deleted objects.
     */
    DELETED, 
    /**
     * The state of an object being deleted. This is used by other
     * objects to determine when to trigger various events and used by
     * users to tell if it is safe to examine this object.  An
     * IllegalArgumentException will be thrown on any attempt to
     * (re)delete an object that has LifeState.DELETION_IN_PROGRESS.
     */
    DELETION_IN_PROGRESS;
}
