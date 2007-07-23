/* -*-Java-*-
********************************************************************************
*
* File:         Identifiable.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/Identifiable.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Fri May 06 08:52:11 2005
* Modified:     Fri Aug 11 15:53:56 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Fri Aug 11 15:53:38 2006 (Michael L. Creech) creech@w235krbza760
*  Gutted all but getIdentifier().
********************************************************************************
*/
package cytoscape.hyperedge;

public interface Identifiable
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Get the unique identifer of this object.
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
