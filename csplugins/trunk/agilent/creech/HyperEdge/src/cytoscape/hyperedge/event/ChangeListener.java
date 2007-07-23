/* -*-Java-*-
********************************************************************************
*
* File:         ChangeListener.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/event/ChangeListener.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:  
* Author:       Michael L. Creech
* Created:      Mon Jul 11 11:46:25 2005
* Modified:     Mon Jul 11 11:46:26 2005 (Michael L. Creech) creech@Dill
* Language:     Java
* Package:      
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/



package cytoscape.hyperedge.event;

import java.util.EventListener;


/**
 * Interface for determining how a HyperEdge object has changed.
 *
 * @author Michael L. Creech
 * @version 1.0
 */
public interface ChangeListener extends EventListener
  {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Called when an HyperEdge is modified (changed).
     * Changes include most types of modifications, such as changes to
     * the name, type, attributes, or objects associated with this
     * object (e.g., adding Edges to a HyperEdge).  Change events are
     * only triggered when an HyperEdge is in a NORMAL state,
     * not when it is being created or deleted.
     *
     * <P>Since this callback may be invoked while the system is in the
     * middle of making major object changes, care must be taken in
     * examining and modifying arbitrary HyperEdges while in this
     * callback. You should always check the state of any objects (using
     * isState()) you wish to examine or modify before actually
     * examining or modifying them.
     *
     * @param notification an object containing specific
     * information on the type of change that occurred. This object will
     * include the HyperEdge that was changed.
     *   
     * <P><STRONG>CAUTION: Be careful when modifying the HyperEdge
     *        passed within notification!  Most of the implementations
     *        that fire the events that call this method do
     *        <EM>not</EM> copy this notification, or HyperEdge
     *        passed within it, for each separate listener. Therefore,
     *        you must be very careful when modifying this
     *        object. Since the order of listener invocation is
     *        arbitrary, if you modify this HyperEdge in one
     *        listener, another listener may see this object in its
     *        unmodified or modified form. Also, care must be taken to
     *        avoid changes to the object that might fire unexpected
     *        and undesired change events. </STRONG>
     *
     * @see EventNote
     */
    public void objectChanged(EventNote notification);
  }
