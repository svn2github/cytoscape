/* -*-Java-*-
********************************************************************************
*
* File:         SimMatcher.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/SimMatcher.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Fri Apr 01 08:26:31 2005
* Modified:     Tue Aug 22 16:18:54 2006 (Michael L. Creech) creech@w235krbza760
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
* Tue Aug 22 16:17:09 2006 (Michael L. Creech) creech@w235krbza760
*  Updated comments.
********************************************************************************
*/
package cytoscape.hyperedge;


/**
 * Interface used for matching similar HyperEdge objects.
 * This is used in conjunction with the Matchable.isSimilar() operation.
 * @see Matchable#isSimilar
 * @author Michael L. Creech
 * @version 1.0
 */
public interface SimMatcher
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns if two HyperEdge objects are similar.
     * @param he1 the first HyperEdge to compare for similarity.
     * @param he2 the second HyperEdge to compare for similarity.
     * @param optArgs an object the may contain any optional information
     * needed for computing the similarity.
     * @return true if he1 is similar to he2. false otherwise.
     */
    boolean similarTo (HyperEdge he1,
                       HyperEdge he2,
                       Object    optArgs);
}
