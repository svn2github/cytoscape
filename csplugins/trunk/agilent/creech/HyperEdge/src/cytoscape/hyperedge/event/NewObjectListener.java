/* -*-Java-*-
********************************************************************************
*
* File:         NewObjectListener.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/event/NewObjectListener.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Mon Jul 11 11:49:57 2005
* Modified:     Thu Aug 17 16:19:36 2006 (Michael L. Creech) creech@w235krbza760
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
 * Interface for retrieving information on the creation of a new HyperEdge
 * object.
 *
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
 * @version 1.05 */
public interface NewObjectListener extends EventListener
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Called when an new HyperEdge has just been created.
     * @param hedge the newly created HyperEdge.
     * hedge should be in a well-defined state with all required
     * fields and attributes set.
     *
     * <P>Note that since this callback may be invoked while in the
     * middle of making major object changes, care must be taken in
     * examining and modifying arbitrary HyperEdges while in this
     * callback. You should always check the state any objects (using
     * isState()) you wish to examine or modify before actually
     * examining or modifying them.
     */
    public void objectCreated (HyperEdge hedge);
}
