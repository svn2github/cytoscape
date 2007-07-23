/* -*-Java-*-
********************************************************************************
*
* File:         DeleteListener.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/event/DeleteListener.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Mon Jul 11 11:46:42 2005
* Modified:     Mon Jul 11 11:48:11 2005 (Michael L. Creech) creech@Dill
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape.hyperedge.event;

import cytoscape.hyperedge.HyperEdge;

import java.util.EventListener;


/**
 * Interface for retrieving information on the destruction of a
 * HyperEdge.
 * <P><STRONG>CAUTION: Be careful when modifying the Object passed to
 *        the method defined in this interface!  Most of the
 *        implementations that fire the events that call this method
 *        do <EM>not</EM> copy the Object passed to this method for
 *        each separate listener. Therefore, you must be very careful
 *        when modifying this object. Since the order of listener
 *        invocation is arbitrary, if you modify the object
 *        passed as a parameter in one listener, another listener may
 *        see this object in its unmodified or modified form. Also,
 *        care must be taken to avoid changes to the object that might
 *        fire unexpected and undesired change events. </STRONG>
 *
 * @author Michael L. Creech
 * @version 1.1
 */
public interface DeleteListener extends EventListener
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Called when a HyperEdge is about to be destroyed.  This
     * callback is invoked just before any information is actually
     * deleted from the given object. The object will be in
     * LifeState.DELETION_IN_PROGRESS.  This operation is useful for
     * updating any internal state and datastructures you might have that
     * contained the deleted object, or state changes needed.
     *
     * <P>Note that there is no guarantee that objects referenced by
     * this object have not been deleted (this callback may be invoked
     * while in the middle of making major object changes).
     * Therefore, take care in accessing any other HyperEdge
     * attached to this object (actually, any other arbitrary
     * HyperEdge) while in this callback. You should always
     * check the state any objects (using isState()) you wish to
     * examine or modify before actually examining or modifying them.
     *
     * @param hedge the HyperEdge that is being deleted.
     * @throws IllegalArgumentException if any attempt is made to delete
     *  hobj or any other object in LifeState.DELETION_IN_PROGRESS.
     */
    public void objectDestroyed (HyperEdge hedge);
}
