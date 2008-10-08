
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
*
* Revisions:
*
* Tue Nov 21 14:04:36 2006 (Michael L. Creech) creech@w235krbza760
*  Added isHyperEdgeEdge ().
* Tue Nov 07 07:26:31 2006 (Michael L. Creech) creech@w235krbza760
*  Changed use of Edge-->CyEdge.
* Mon Nov 06 09:19:13 2006 (Michael L. Creech) creech@w235krbza760
*  Changed GraphPerspective-->CyNetwork, Node-->CyNode, Edge-->CyEdge.
* Thu Nov 02 05:18:59 2006 (Michael L. Creech) creech@w235krbza760
* Changed overall API so that HyperEdges are shared
* across GraphPerspectives--change made to HyperEdge affects all
* GraphPerspectives.
* Mon Aug 21 07:02:25 2006 (Michael L. Creech) creech@w235krbza760
*  Updated to strongly type all collection arguments and return types
*  for Java 1.5.
* Thu Aug 17 17:24:03 2006 (Michael L. Creech) creech@w235krbza760
*  Added add/removeNewObjectListener(), add/removeDeleteListener().
* Wed Aug 16 09:30:36 2006 (Michael L. Creech) creech@w235krbza760
*  Migrated getGraphPerspectives() to HyperEdge.
* Wed Aug 09 17:39:57 2006 (Michael L. Creech) creech@w235krbza760
*  Removed addToGraphPerspective () and removeFromGraphPerspective ()
* Fri Sep 23 10:23:51 2005 (Michael L. Creech) creech@Dill
*  Added getEdgesByNode().
********************************************************************************
*/
package cytoscape.hyperedge;


// import cytoscape.hyperedge.event.AnyDirtyListener;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.hyperedge.event.ChangeListener;
import cytoscape.hyperedge.event.DeleteListener;
import cytoscape.hyperedge.event.NewObjectListener;

import java.util.Collection;
import java.util.Iterator;


/**
 * Handles the management of HyperEdge objects.
 * @author Michael L. Creech
 * @version 1.0
 *
 */
public interface HyperEdgeManager {
    /**
     * Clear out the contents of this HyperEdgeManager.  All
     * HyperEdges, and CyEdges are removed.
     * @param fireEvents if true, HyperEdges will be
     * removed in such a way that DeleteListener.objectDestroyed()
     * events are fired at the expense of being slower to reset. If
     * false, no objectDestroyed() events are fired, but resetting
     * will be faster.
     */
    void reset(boolean fireEvents);

    /**
     * Return an Iterator over all HyperEdges containing CyEdges that
     * connect to a given CyNode, for a given CyNetwork.  Only
     * the non-ConnectorNode endpoints of all the CyEdges contained by
     * the HyperEdge are considered.
     * @param node the CyNode that must be connected to the
     *             non-ConnectorNode endpoint of a CyEdge that is a
     *             member of a HyperEdge.  If null, node is not
     *             considered in the matching process and all
     *             HyperEdges satisfying net are returned.
     * @param net the CyNetwork within which the HyperEdge must belong.
     * If null, all HyperEdges satisfying node are returned.
     * @return a non-null, read-only Iterator (doesn't allow
     * modification) over all matching HyperEdges.  The iterator will
     * contain all existing HyperEdges if both node and net are
     * null. The Iterator will be empty if no HyperEdge contains node,
     * or if no HyperEdge within the given non-null net contains the
     * node.
    */
    Iterator<HyperEdge> getHyperEdgesByNode(CyNode node, CyNetwork net);

    /**
     * Return an Iterator over all HyperEdges containing a collection
     * of CyNodes for a given CyNetwork.  Only the
     * non-ConnectorNode endpoints of all the CyEdges contained by the
     * HyperEdge are considered.
     * @param nodes the Collection of CyNodes that must all be connected to the
     *             non-ConnectorNode endpoint of a CyEdge that is a
     *             member of a HyperEdge. If null, nodes are not
     *             considered in the matching process and all
     *             HyperEdges satisfying net are returned.
     * @param net the CyNetwork within which the HyperEdge must belong.
     * If null, all HyperEdges satisfying nodes are returned.
     * @return a non-null, read-only Iterator (doesn't allow
     * modification) over all matching HyperEdges.  The iterator will
     * contain all existing HyperEdges if both nodes and net are null.
     * The Iterator will be empty if nodes is empty, no HyperEdge
     * contains the given set of nodes, or if no HyperEdge within the
     * given non-null net contains the set of nodes.
     */
    Iterator<HyperEdge> getHyperEdgesByNodes(Collection<CyNode> nodes,
                                             CyNetwork net);

    /**
     * Return an Iterator over all HyperEdge CyEdges
     * containing a given CyNode for a given CyNetwork.
     * @param node the CyNode that must be connected to the
     * non-ConnectorNode endpoint of a CyEdge that is a member of a
     * HyperEdge.  If null, node is not considered in the matching
     * process and all CyEdges satisfying net are returned.
     * @param net the CyNetwork within which the CyEdge must belong.
     * If null, all HyperEdges satisfying node are returned.
     * @return a non-null, read-only Iterator (doesn't allow
     * modification) over all matching CyEdges.
     * The iterator will contain all existing CyEdges, within HyperEdges,
     * if both node and net are null. The Iterator will
     * be empty if no CyEdge contains node, or if no
     * CyEdge within the given non-null net contains the node.
     */
    Iterator<CyEdge> getEdgesByNode(CyNode node, CyNetwork net);

    /**
     * Return an Iterator over all HyperEdges containing CyNodes with
     * CyEdges that match a given Collection of edge interaction types
     * within a given CyNetwork.
     * @param edgeITypes the Collection of String edge interaction
     * types where each edge interaction type must match the interaction
     * type attribute of a CyEdge contained within the HyperEdge.
     * If null, edgeITypes are not considered in the
     * matching process and all HyperEdges satisfying net are returned.
     * @param net the CyNetwork within which the HyperEdges must belong.
     * If null, all HyperEdges satisfying edgeITypes are returned.
     * @return a non-null, read-only Iterator (doesn't allow
     * modification) over all matching HyperEdges. A HyperEdge matches
     * iff for every edge interaction type in edgeITypes, there is a
     * corresponding CyEdge whose interaction type attribute matches
     * edge type.  The iterator will contain all existing HyperEdges
     * if both edgeITypes and net are null.  Iterator will be empty
     * if no HyperEdge contains nodes that coorespond to the given
     * edgeITypes, or if no HyperEdge within the given non-null net
     * contains nodes that coorespond to the given edgeITypes.
    */
    Iterator<HyperEdge> getHyperEdgesByEdgeTypes(Collection<String> edgeITypes,
                                                 CyNetwork net);

    /**
     * Return an Iterator over all distinct CyNodes associated with
     * HyperEdge CyEdges that match any of a given Collection of edge types and
     * are found within a given CyNetwork. The CyNodes returned
     * are the CyNodes connected to the non-ConnectorNode endpoint of
     * each CyEdge.
     * @param edgeITypes the Collection of String edge interaction
     * types where at least one edge type must match the type of a CyEdge
     * associated with a given CyNode. If null, edgeITypes are not
     * considered in the matching process and all CyNodes satisfying net
     * are returned.
     * @param net the CyNetwork within which the HyperEdges must belong.
     * If null, the CyNetwork is not considered in the matching process
     * and all CyNodes satisfying edgeITypes are returned.
     * @return a non-null, read-only Iterator (doesn't allow
     * modification) over all distinct matching CyNodes. A CyNode matches
     * iff for at least one edge interaction type in edgeITypes, there is a
     * corresponding CyEdge associated with this CyNode whose interaction
     * type attribute matches edge type.  The iterator will contain
     * all CyNodes found within any HyperEdge if both edgeITypes and
     * net are null.  Iterator will be empty if edgeITypes is empty,
     * no CyNode cooresponds to the given edgeITypes, or if no CyNode
     * within the given non-null net contains CyNodes that coorespond to
     * the given edgeITypes.
    */
    Iterator<CyNode> getNodesByEdgeTypes(Collection<String> edgeITypes,
                                         CyNetwork net);

    /**
     * Return an Iterator over all HyperEdges for a given CyNetwork.
     * @param net  the CyNetwork within which a HyperEdge must belong.
     * If null, all HyperEdges are returned--whether or not they are members
     * of a CyNetwork.
     * @return a non-null, read-only Iterator (doesn't allow modification)
     *         over all HyperEdges within net, or if net is null, over all
     *         HyperEdges.
     */
    Iterator<HyperEdge> getHyperEdgesByNetwork(CyNetwork net);

    /**
     * Return an Iterator over all HyperEdge CyEdges for a given
     * CyNetwork.
     * @param net  the CyNetwork within which a CyEdge must belong.
     * If null, all CyEdges are returned--whether or not they are members
     * of a CyNetwork.
     * @return a non-null, read-only Iterator (doesn't allow modification)
     *         over all HyperEdges within net, or if net is null, over all
     *         HyperEdges.
     */
    Iterator<CyEdge> getEdgesByNetwork(CyNetwork net);

    /**
     * Return the number of HyperEdges in a given CyNetwork.
     * @param net  the CyNetwork of interest.
     * @return the number of HyperEdges in net.
     * If net is null the number of all HyperEdges is
     * returned.
     */
    int getNumHyperEdges(CyNetwork net);

    /**
     * Return the number of HyperEdge CyNodes in a given CyNetwork.
     * These are the nodes that are connected to the non-ConnectorNode
     * endpoint of all the HyperEdge CyEdges of interest.
     * @param net  the CyNetwork of interest.
     * @return the number of CyNodes in net. If net is
     * null, the CyNetwork is not considered in the matching
     * process and the number of all CyNodes associated with HyperEdges
     * is returned.
     */
    int getNumNodes(CyNetwork net);

    /**
     * Return the number of HyperEdge CyEdges in a given CyNetwork.
     * @param net the CyNetwork from which to return the number of edges.
     * @return the number of CyEdges in net. If net is null, the number of
     * all CyEdges associated with HyperEdges is returned.  If net is
     * null, the CyNetwork is not considered in the matching
     * process and the number of all CyEdges associated with HyperEdges
     * is returned.
     */
    int getNumEdges(CyNetwork net);

    /**
     * Is a given CyNode part of any HyperEdge in a given CyNetwork?
     * @param node the CyNode to match in a HyperEdge
     * @param net the CyNetwork in which the matching HyperEdge must belong.
     * @return true if a given CyNode is part of a HyperEdge for a given
     * CyNetwork. If net is null, will return true if the CyNode
     * is part of any HyperEdge. false if node is null.
     */
    boolean inHyperEdge(CyNode node, CyNetwork net);

    /**
     * Return whether a given CyNode is the Connector node of
     * a HyperEdge that belongs to a given CyNetwork.
     * @param node the CyNode to check for being a ConnectorNode.
     * @param net the CyNetwork within which node must be a
     * ConnectorNode.
     * @return true iff node is the Connector node of a HyperEdge
     * within the given net.  If net is null, true is returned iff node
     * is the Connector node of any HyperEdge.
     */
    boolean isConnectorNode(CyNode node, CyNetwork net);

    /**
     * Return whether a given CyEdge is an edge that is contained
     * in a HyperEdge.
     * @param edge the CyEdge to check for being a HyperEdge edge.
     * @param net the CyNetwork within which edge must belong.
     * @return true iff edge is a member of a HyperEdge that belongs
     * to the given net.  If net is null, true is returned iff edge
     * is a member of any HyperEdge in any CyNetwork.
     */
    boolean isHyperEdgeEdge(CyEdge edge, CyNetwork net);



    /**
     * Return the HyperEdge associated with a given ConnectorNode.
     * @param connectorNode the ConnectorNode of the HyperEdge of interest.
     * @return The HyperEdge with the given ConnectorNode, or null if
     * connectorNode is not a ConnectorNode.
     */
    HyperEdge getHyperEdgeForConnectorNode(CyNode connectorNode);

    /**
     * @return the String representation of the version information about this
     * release of HyperEdge (e.g., "HyperEdge Version 2.0, 08/17/06").
     */
    String getHyperEdgeVersion();

    /**
     * @return the Double representation of the version number of this
     * release of HyperEdge (e.g., 2.5).
     */
    Double getHyperEdgeVersionNumber();

    /**
     * Adds a listener object to the list of listeners that wish to be
     * notified whenever a new HyperEdge is created.
     * @param l  the listener object that seeks notification.
     * @return true if the listener was successfully added, false otherwise.
     */
    boolean addNewObjectListener(NewObjectListener l);

    /**
     * Removes a listener object from the list of listeners to be
     * notified whenever  a new HyperEdge is created.
     * @param l the listener object that no longer seeks notification.
     * @return true if the listener was successfully removed, false otherwise.
     */
    boolean removeNewObjectListener(NewObjectListener l);

    /**
     * Adds a listener object to the list of listeners that wish to be
     * notified whenever a HyperEdge is changed.
     * @param l  the listener object that seeks notification.
     * @return true if the listener was successfully added, false otherwise.
     */
    boolean addChangeListener(ChangeListener l);

    /**
     * Removes a listener object from the list of listeners to be
     * notified whenever a HyperEdge is changed.
     * @param l the listener object that no longer seeks notification.
     * @return true if the listener was successfully removed, false otherwise.
     */
    boolean removeChangeListener(ChangeListener l);

    /**
     * Adds a listener object to the list of listeners that wish to be
     * notified whenever this HyperEdge is deleted.
     * @param l  the listener object that seeks notification.
     * @return true if the listener was successfully added, false otherwise.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean addDeleteListener(DeleteListener l);

    /**
     * Removes a listener object from the list of listeners to be
     * notified whenever this HyperEdge is deleted.
     * @param l the listener object that no longer seeks notification.
     * @return true if the listener was successfully removed, false otherwise.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean removeDeleteListener(DeleteListener l);

    //    /**
    //     * Save all HyperEdges within a given CyNetwork to a given
    //     * file in a specified format. Critical CyNode and CyEdge attributes
    //     * required for the definition of the contents of HyperEdges may
    //     * also be saved.
    //     * @param uri the URI representing the location of where to save
    //     *        the HyperEdges. For local files, this would be something like
    //     *        <EM>file:/c:/cytoscape/my-data/my-hyperedges.xml</EM>.
    //     * @param net an optional CyNetwork from which to extract
    //     * HyperEdges to save. If net is null, all HyperEdges are saved.
    //     * @param format the format in which to save the HyperEdge data
    //     * (e.g., Format.XML).
    //     * @return the number of HyperEdges saved. If there are no HyperEdges to
    //     *         save, no information is written and zero is returned.
    //     */
    //    int save (String           uri,
    //              CyNetwork net,
    //              Format           format);
    //
    //    /**
    //     * Load a set of HyperEdges from a given file.  A replacement
    //     * policy is used when an existing, in memory, HyperEdge matches a
    //     * HyperEdges being loaded. This policy works as follows:
    //     * <OL>
    //     * <P><LI>All CyNode and CyEdge references are reused--if the CyNode or CyEdge
    //     *        existed prior to restoring, it is directly used as a replacement
    //     *        for the CyNode or CyEdge being loaded. The only caveat to this
    //     *        is that any critical CyEdge and CyNode attributes loaded may be
    //     *        replaced, if different from the old values.
    //     * <P><LI>HyperEdges are replaced. Their contents will only contain
    //     *        the newly read content of the HyperEdge.
    //     * </OL>
    //     * @param uri the URI representing the location of the HyperEdges
    //     *        to load. For local files, this would be something like
    //     *        <EM>file:/c:/cytoscape/my-data/my-hyperedges.xml</EM>.
    //     * @param net an optional CyNetwork in which to add all
    //     * HyperEdge objects read. When the CyNodes and CyEdges contained by
    //     * the HyperEdges are not already a member of net, they will be
    //     * added to net. If net is null, the HyperEdges are simply read
    //     * in--no association with any CyNetworks is made.
    //     * @param format the format in which to load the HyperEdge data
    //     * (e.g., Format.XML).
    //     * @return the number of HyperEdges loaded. Will return 0 if uri can't
    //     *         be read or under other certain errors.
    //     */
    //    int load (String           uri,
    //              CyNetwork net,
    //              Format           format);
    //
    //    //~ Inner Classes //////////////////////////////////////////////////////////
    //
    //    /**
    //     * Defines a set of formats which may be used to load and save HyperEdges.
    //     */
    //    public static class Format
    //    {
    //        public static final Format XML = new Format("XML");
    //        public static final Format SIF = new Format("SIF");
    //        public static final Format SBML = new Format("SBML");
    //        private final String _name;
    //
    //        private Format (String name)
    //        {
    //            _name = name;
    //        }
    //
    //        public String toString ()
    //        {
    //            return ("Format " + _name);
    //        }
    //    }

    //    /**
    //     * Return the CytoscapeData attribute table used for all HyperEdge objects.
    //     */
    //    CytoscapeData getHyperEdgeData ();
    //    /**
    //     * Has any persistent HyperGraph objects been changed, added or deleted?
    //     * @return true when at least one persistent HyperGraph in-memory object
    //     * has been changed or deleted, or when a new in-memory HyperGraph
    //     * object has been added that has not yet been persistently saved.
    //     * false otherwise.
    //     */
    //    boolean isAnyDirty ();
    //
    //    /**
    //     * Adds a listener object to the list of listeners that wish to be
    //     * notified whenever HyperGraph's dirty state changes. This occurs when
    //     * HyperGraph moves from having no objects dirty to having at least one
    //     * object dirty, and when HyperGraph changes state from having objects
    //     * dirty to having none dirty.
    //     * @param l  the listener object that seeks notification.
    //     * @return true if the listener was successfully added, false otherwise.
    //     * @see HyperEdgeManager#isAnyDirty
    //     * @see HyperEdgeManager#removeAnyDirtyListener
    //     */
    //    boolean addAnyDirtyListener (AnyDirtyListener l);
    //
    //    /**
    //     * Removes a listener object from the list of listeners to be
    //     * notified whenever the HyperGraph environment changes its dirty state.
    //     * @param l the listener object that no longer seeks notification.
    //     * @return true if the listener was successfully removed, false otherwise.
    //     */
    //    boolean removeAnyDirtyListener (AnyDirtyListener l);
    // Object setAttributeValue (HyperObj hobj, String attribute, Object value);
}
