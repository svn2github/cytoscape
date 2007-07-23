/* -*-Java-*-
********************************************************************************
*
* File:         EdgeTypeMap.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/EdgeTypeMap.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Thu Sep 29 05:50:54 2005
* Modified:     Fri Aug 18 07:14:15 2006 (Michael L. Creech) creech@w235krbza760
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
* Fri Aug 11 18:39:47 2006 (Michael L. Creech) creech@w235krbza760
*  Removed use of Identifiable. Added reset().
* Sat Jul 29 13:53:09 2006 (Michael L. Creech) creech@w235krbza760
*  Added ACTIVATING_MEDIATOR & INHIBITING_MEDIATOR, deprecated MEDIATOR.
* Tue Oct 04 05:45:58 2005 (Michael L. Creech) creech@Dill
*  Added iterator() & toString().
********************************************************************************
*/
package cytoscape.hyperedge;

import java.util.Iterator;
import java.util.Map;


/**
 * Maps Edge interaction types to EdgeRoles.
 * EdgeRoles represent the role all given Nodes play within an Edge
 * for a given Edge interaction type. Edges have roles of source and target.
 *
 * <P>This map is used to define the role of Nodes used in creating a
 * HyperEdge (via HyperEdgeFactory.createHyperEdge()) and the role of
 * Nodes added to a HyperEdge (via HyperEdge.addEdge()). The role of a
 * given node within an Edge is based on the edge interaction type of
 * the edge that contains that Node.
 *
 * Example: If we performed:
 * <PRE>
 *     he1.addEdge (Node1, EdgeTypeMap.PRODUCT);
 * </PRE>
 * This would add an edge that connects Node1 to the Connector node of he1 and
 * Node1 would be the target of this edge because EdgeTypeMap.PRODUCT maps to
 * EdgeRole.TARGET.
 *
 * @see HyperEdge#addEdge
 * @see HyperEdgeFactory#createHyperEdge
 *
 * @author Michael L. Creech
 * @version 1.0
 */

// public interface EdgeTypeMap extends Identifiable
public interface EdgeTypeMap {
    /**
     * A default edge interaction type that maps to EdgeRole.SOURCE.
     * Any Node added to a HyperEdge via HyperEdge.addEdge() or via
     * HyperEdgeFactory.createHyperEdge() using this interaction type
     * will end up as the source of any Edges created.
     */
    public static final String SUBSTRATE = "hyperedge.substrate";

    /**
     * A default edge interaction type that maps to EdgeRole.SOURCE.
     * Any Node added to a HyperEdge via HyperEdge.addEdge() or via
     * HyperEdgeFactory.createHyperEdge() using this interaction type
     * will end up as the source of any Edges created.
     */
    public static final String ACTIVATING_MEDIATOR = "hyperedge.mediator.activating";

    /**
     * @deprecated Replaced with ACTIVATING_MEDIATOR.
     * @see EdgeTypeMap#ACTIVATING_MEDIATOR
     */
    public static final String MEDIATOR = ACTIVATING_MEDIATOR;

    /**
     * A default edge interaction type that maps to EdgeRole.SOURCE.
     * Any Node added to a HyperEdge via HyperEdge.addEdge() or via
     * HyperEdgeFactory.createHyperEdge() using this interaction type
     * will end up as the source of any Edges created.
     */
    public static final String INHIBITING_MEDIATOR = "hyperedge.mediator.inhibiting";

    /**
     * A default edge interaction type that maps to EdgeRole.TARGET.
     * Any Node added to a HyperEdge via HyperEdge.addEdge() or via
     * HyperEdgeFactory.createHyperEdge() using this interaction type
     * will end up as the target of any Edges created.
     */
    public static final String PRODUCT = "hyperedge.product";

    /**
     * Add or replace a group of existing mappings based on a given
     * Map of edge interaction types and EdgeRoles.
     * FUTURE: Add change event notification.
     * @param edge_type_to_edge_role_map a Map consisting of
     * String edge interaction types keys with EdgeRole values.
     * For each such Map entry, a put operation is performed to add or
     * replace the mapping within this EdgeTypeMap.
     * @return true if any mappings were changed based on the values
     * within edge_type_to_edge_role_map. Return false otherwise.
     */
    boolean addAll(Map edge_type_to_edge_role_map);

    /**
     * Add or replace the mapping of a given edge interaction
     * type to a given EdgeRole. The EdgeRole determines
     * the role within an Edge of any Node added to a HyperEdge
     * with the given edge interaction type.
     * FUTURE: Add change event notification.
     *
     * @param edgeIType the edge interaction type to map to a,
     *                    possibly new, EdgeRole.
     * @return the EdgeRole of the last value of any mapping for
     *         edgeIType, or null if there was no previous mapping.
     */
    EdgeRole put(String edgeIType, EdgeRole pos);

    /**
     * Returns the EdgeRole of a given edge interaction type.
     * @param edgeIType the edge interaction type for which to find
     *                    an edge role mapping.
     * @return the EdgeRole of edgeIType if found. null otherwise
     * (includes edgeIType equal to null).
     */
    EdgeRole get(String edgeIType);

    /**
     * Remove the mapping that exists for a given edge interaction type.
     * FUTURE: Add change event notification.
     * @param edgeIType the Edge interaction type to remove.
    
     * @return the removed EdgeRole or null if there was no mapping
     *         for edgeIType as key (including edgeIType equal to null).
     */
    EdgeRole remove(String edgeIType);

    /**
     * Empty the contents of this map.
     * FUTURE: Add change event notification.
     */
    void clear();

    /**
     * Reset the EdgeTypeMap back to the state when first constructed.
     * FUTURE: Add change event notification.     
     */
    void reset();

    /**
     * Return true iff this map has no entries.
     */
    boolean isEmpty();

    /**
     * Return the number of entries in this map.
     */
    int size();

    /**
     * Return an iterator over all Edge interaction type to EdgeRole mappings.
     * @return a non-null, read-only Iterator (doesn't allow
     * modification) of all entries in this map. Each entry is a Map.Entry
     * where the key is the String Edge interaction type and the value is the
     * EdgeRole.
     */
    Iterator iterator();

    /**
     * Return a human readable string representing the content of this object.
     *
     */
    String toString();

    //    /**
    //     * Replace the EdgeTypeMap with the contents loaded from a given file.
    //     * The previous contents of the map is erased before the new
    //     * map information is loaded.
    //     * @param uri the URI representing the location of where to load
    //     *        this EdgeTypeMap. For local files, this would be something like
    //     *        <EM>file:/c:/cytoscape/my-data/my-map.xml</EM>.
    //     * @param format the format from which to load the EdgeTypeMap data.
    //     * (e.g., Format.XML).
    //     */
    //    int load (String uri,
    //              Format format);
    //
    //    /**
    //     * Save the EdgeTypeMap to a given file.
    //     * @param uri the URI representing the location of where to save
    //     *        this EdgeTypeMap. For local files, this would be something like
    //     *        <EM>file:/c:/cytoscape/my-data/my-map.xml</EM>.
    //     * @param format the format with which to save the EdgeTypeMap data.
    //     */
    //    int save (String uri,
    //              Format format);

    /**
     * Defines the role within an Edge--either the Source or Target of an Edge.
     */
    public static class EdgeRole {
        public static final EdgeRole SOURCE = new EdgeRole("Source");
        public static final EdgeRole TARGET = new EdgeRole("Target");
        private final String _name;

        private EdgeRole(String name) {
            _name = name;
        }

        public String getName() {
            return _name;
        }

        static public EdgeRole getEdgeRoleByName(String name) {
            if ("Source".equals(name)) {
                return SOURCE;
            }

            if ("Target".equals(name)) {
                return TARGET;
            }

            return null;
        }

        public String toString() {
            return ("EdgeRole " + _name);
        }
    }
}
