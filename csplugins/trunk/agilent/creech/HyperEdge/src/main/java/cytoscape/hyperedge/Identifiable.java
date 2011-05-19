
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
* Fri Aug 11 15:53:38 2006 (Michael L. Creech) creech@w235krbza760
*  Gutted all but getIdentifier().
********************************************************************************
*/
package cytoscape.hyperedge;
/**
 * Used to specify uniquely identifiable objects.
 * @author Michael L. Creech
 *
 */
public interface Identifiable {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return the unique identifier of this object.
     */
    String getIdentifier ();

    //    /**
    //     * Adds a listener object to the list of listeners that wish to be
    //     * notified whenever this HyperObj's dirty state changes.
    //     * @param l  the listener object that seeks notification.
    //     * @return true if the listener was successfully added, false otherwise.
    //     */
    //    boolean addDirtyListener (DirtyListener l);
    //
    //    /**
    //     * Removes a listener object from the list of listeners to be
    //     * notified whenever this HyperObj's dirty state changes.
    //     * @param l the listener object that no longer seeks notification.
    //     * @return true if the listener was successfully removed, false otherwise.
    //     */
    //    boolean removeDirtyListener (DirtyListener l);
    //
    //    /**
    //     * Return whether this object needs to be saved.
    //     * @return true if this object has been modified or is a newly created
    //     * object. false otherwise.
    //
    //    */
    //    boolean isDirty ();
    //
    //    /**
    //     * Save out this object using a given Writer and optional arguments.
    //     * @return true iff the object was successfully saved.
    //    */
    //    boolean save (Writer w,
    //                  Object args,
    //                  Format format);
    //
    //    /**
    //     * Restore the references and attributes of this object from a
    //     * Map.
    //     * @return true iff the object was successfully restored.
    //     */
    //    boolean load (Map values);
}
