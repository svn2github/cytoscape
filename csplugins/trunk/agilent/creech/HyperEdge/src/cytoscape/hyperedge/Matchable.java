/* -*-Java-*-
********************************************************************************
*
* File:         Matchable.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/Matchable.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:  
* Author:       Michael L. Creech
* Created:      Thu Sep 15 05:38:37 2005
* Modified:     Tue Aug 22 16:20:33 2006 (Michael L. Creech) creech@w235krbza760
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

public interface Matchable
{
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
