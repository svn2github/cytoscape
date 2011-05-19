
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
    String SUBSTRATE = "hyperedge.substrate";

    /**
     * A default edge interaction type that maps to EdgeRole.SOURCE.
     * Any Node added to a HyperEdge via HyperEdge.addEdge() or via
     * HyperEdgeFactory.createHyperEdge() using this interaction type
     * will end up as the source of any Edges created.
     */
    String ACTIVATING_MEDIATOR = "hyperedge.mediator.activating";

    /**
     * @deprecated Replaced with ACTIVATING_MEDIATOR.
     * @see EdgeTypeMap#ACTIVATING_MEDIATOR
     */
    String MEDIATOR = ACTIVATING_MEDIATOR;

    /**
     * A default edge interaction type that maps to EdgeRole.SOURCE.
     * Any Node added to a HyperEdge via HyperEdge.addEdge() or via
     * HyperEdgeFactory.createHyperEdge() using this interaction type
     * will end up as the source of any Edges created.
     */
    String INHIBITING_MEDIATOR = "hyperedge.mediator.inhibiting";

    /**
     * A default edge interaction type that maps to EdgeRole.TARGET.
     * Any Node added to a HyperEdge via HyperEdge.addEdge() or via
     * HyperEdgeFactory.createHyperEdge() using this interaction type
     * will end up as the target of any Edges created.
     */
    String PRODUCT = "hyperedge.product";

    /**
     * Add or replace a group of existing mappings based on a given
     * Map of edge interaction types and EdgeRoles.
     * FUTURE: Add change event notification.
     * @param edgeTypeToEdgeRoleMap a Map consisting of
     * String edge interaction types keys with EdgeRole values.
     * For each such Map entry, a put operation is performed to add or
     * replace the mapping within this EdgeTypeMap.
     * @return true if any mappings were changed based on the values
     * within edge_type_to_edge_role_map. Return false otherwise.
     */
    boolean addAll(Map<String,EdgeRole> edgeTypeToEdgeRoleMap);

    /**
     * Add or replace the mapping of a given edge interaction
     * type to a given EdgeRole. The EdgeRole determines
     * the role within an Edge of any Node added to a HyperEdge
     * with the given edge interaction type.
     * FUTURE: Add change event notification.
     *
     * @param edgeIType the edge interaction type to map to a,
     *                    possibly new, EdgeRole.
     * @param pos the EdgeRole the edge interaction type maps to 
     *        (e.g., source or target of the Edge).
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
     * @return true iff this map has no entries.
     */
    boolean isEmpty();

    /**
     * @return the number of entries in this map.
     */
    int size();

    /**
     * Return an iterator over all Edge interaction type to EdgeRole mappings.
     * @return a non-null, read-only Iterator (doesn't allow
     * modification) of all entries in this map. Each entry is a Map.Entry
     * where the key is the String Edge interaction type and the value is the
     * EdgeRole.
     */
    Iterator<Map.Entry<String,EdgeRole>> iterator();

    /**
     * @return a human readable string representing the content of this object.
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
    public static final class EdgeRole {
	    
        private static final String SOURCE_NAME = "Source";
	private static final String TARGET_NAME = "Target";  
	
	/**
	 * The EdgeRole representing the source of an edge.
	 */
        public static final EdgeRole SOURCE = new EdgeRole(SOURCE_NAME);
	/**
	 * The EdgeRole representing the target of an edge.
	 */
        public static final EdgeRole TARGET = new EdgeRole(TARGET_NAME);
        
	private final String erName;
	
        private EdgeRole(final String name) {
            erName = name;
        }

	/**
	 * @return the name of this EdgeRole.
	 */

        public String getName() {
            return erName;
        }

	/**
	 * Return an EdgeRole based on its name.
	 * @param name the name of the EdgeRole.
	 * @return the EdgeRole associated with the given name, or null if
	 *         no EdgeRole has the given name.
	 */
        public static EdgeRole getEdgeRoleByName(final String name) {
            if (SOURCE_NAME.equals(name)) {
                return SOURCE;
            }

            if (TARGET_NAME.equals(name)) {
                return TARGET;
            }

            return null;
        }

	/**
	 * @return a human readable string representing the content of this object.
	 *
	 */
        public String toString() {
            return ("EdgeRole " + erName);
        }
    }
}
