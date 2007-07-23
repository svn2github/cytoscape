/* -*-Java-*-
********************************************************************************
*
* File:         LifeState.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/LifeState.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Thu Mar 31 13:53:26 2005
* Modified:     Sat Sep 09 06:01:06 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
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
    /*
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
