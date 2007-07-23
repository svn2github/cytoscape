/* -*-Java-*-
********************************************************************************
*
* File:         HEStructuralIntegrityException.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/HEStructuralIntegrityException.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:  
* Author:       Michael L. Creech
* Created:      Tue May 03 10:25:49 2005
* Modified:     Mon Sep 26 06:15:59 2005 (Michael L. Creech) creech@Dill
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
 * An Exception thrown whenever some internal problem with the structure of
 * HyperEdges is found.
 *
 * @author Michael L. Creech
 * @version 1.0
 */
public class HEStructuralIntegrityException extends RuntimeException
{
	private static final long serialVersionUID = -4564750527341156299L;
	public HEStructuralIntegrityException ()
    {
	super ();
    }
    public HEStructuralIntegrityException (String msg)
    {
	super (msg);
    }
}
