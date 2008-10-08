
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
* Wed Nov 29 05:38:08 2006 (Michael L. Creech) creech@w235krbza760
*  Changed addToCyNetwork()-->addToNetwork(),
*  getCyNetwork()-->getNetwork(), inCyNetwork()-->inNetwork(), and
*  removeFromCyNetwork()-->removeFromNetwork().
* Wed Nov 15 16:13:32 2006 (Michael L. Creech) creech@w235krbza760
*  Added getNode().
* Tue Nov 14 07:53:43 2006 (Michael L. Creech) creech@w235krbza760
*  Added isSharedEdge(), changed 
* Fri Nov 10 16:10:25 2006 (Michael L. Creech) creech@w235krbza760
*  Added hasSharedEdges() and getSharedEdges().
* Tue Nov 07 07:27:11 2006 (Michael L. Creech) creech@w235krbza760
*  Changed use of Edge-->CyEdge.
* Mon Nov 06 09:04:16 2006 (Michael L. Creech) creech@w235krbza760
*  Changed GraphPerspective-->CyNetwork, Node-->CyNode.
* Thu Nov 02 05:17:33 2006 (Michael L. Creech) creech@w235krbza760
* Changed overall API so that HyperEdges are shared
* across GraphPerspectives--change made to HyperEdge affects all
* GraphPerspectives.
* Tue Sep 12 14:53:10 2006 (Michael L. Creech) creech@w235krbza760
*  Added connectHyperEdges() and reverted to IllegalArgumentException
*  for ConnectorNode being given to constructors and addEdge().
* Mon Aug 21 06:57:32 2006 (Michael L. Creech) creech@w235krbza760
*  Updated to strongly type all collection arguments and return types
*  for Java 1.5.
* Wed Aug 16 09:29:41 2006 (Michael L. Creech) creech@w235krbza760
*  Added getGraphPerspectives().
* Thu Jul 27 15:14:57 2006 (Michael L. Creech) creech@w235krbza760
*  Fixed comments.
* Thu Aug 11 14:42:20 2005 (Michael L. Creech) creech@Dill
*    Changed HENode-->ConnectorNode.
********************************************************************************
*/
package cytoscape.hyperedge;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


/**
 * A CyEdge that can connect two or more CyNodes.  A HyperEdge consists
 * of a set of CyEdges and a special CyNode referred to as a
 * <EM>ConnectorNode</EM>. The ConnectorNode is a generated CyNode that
 * serves as one endpoint for all CyEdges contained by a HyperEdge.
 * A HyperEdge can connect to another HyperEdge by having a CyEdge that
 * connects to the ConnectorNode of this other HyperEdge. Such an edge
 * is called a <EM>shared edge</EM>, since the same CyEdge is contained
 * in both HyperEdges.
 *
 * <P>Assumptions:
 * <OL>
 * <LI>HyperEdges are mutable.
 * <P><LI>A HyperEdge may have more than one CyNode with the same
 *     CyEdge interaction type.
 * <P><LI>A HyperEdge may have more than one CyEdge to the same CyNode.
 * <P><LI>A HyperEdge has exactly one ConnectorNode.
 * <P><LI>A HyperEdge ConnectorNode may be used as a regular CyNode
 *        within another HyperEdge.
 * <P><LI>A HyperEdge is associated with one or more CyNetworks.
 * <P><LI>The only shared CyEdges in a HyperEdge are those connecting two
 *        ConnectorNodes (HyperEdges) that must exist in the same CyNetwork.
 * <P><LI>If HyperEdge A connects to HyperEdge B via a shared edge,
 *        both A and B can only exist in one (and the same) CyNetwork.
 * <P><LI>A regular Cytoscape CyEdge may directly connect to a HyperEdge
 *        ConnectorNode, but it will not be considered part of the HyperEdge.
 * <P><LI>HyperEdges are shared, in that any change to a HyperEdge
 *        existing in more than one CyNetwork is reflected in all CyNetworks
 *        to which the HyperEdge belongs.
 * </OL>
 *
 * <H4>CyNetworks</H4>
 *
 * When a HyperEdge belongs to more than one CyNetwork, it is
 * <EM>shared</EM>. This means that operations, such as adding or
 * removing edges, targeted for one CyNetwork affect the HyperEdge in
 * other CyNetworks. This behavior does <EM>not</EM> follow the
 * behavior of CyNodes and CyEdges in Cytoscape, where if a CyEdge exists
 * in two or more CyNetworks, deleting the CyEdge in one CyNetwork does
 * not remove the CyEdge in another CyNetwork.
 *
 * <H4>Persistence</H4>
 *
 * There are no specific API operations for saving and restoring
 * HyperEdges.  However, we wish to ensure that a reference
 * implementation can be constructed that allows transparent saving
 * and restoring of HyperEdges using the standard Cytoscape save and
 * restore operations.
 *
 * <H4>Attributes</H4>
 *
 * HyperEdges do not currently have their own attributes, however the
 * ConnectorNode can be used to house attributes for the entire
 * HyperEdge.
 *
 * @author Michael L. Creech
 * @version 2.4
 */
public interface HyperEdge extends Identifiable, Matchable {
    /**
     * Add a given HyperEdge to a given CyNetwork.
     * Only a HyperEdge that doesn't connect to another HyperEdge
     * can be added to another CyNetwork.
     * All CyNodes and CyEdges contained within this HyperEdge will
     * be added to the target CyNetwork.
     * @param net the CyNetwork that will be modified to
     *                  contain the CyNodes and CyEdges contained in
     *                  this HyperEdge.
     * @return true if this HyperEdge was newly added to net. false if
     * net is null, if this HyperEdge is already a member of net,
     * or if this HyperEdge shares any edges with another HyperEdge (via
     * connectHyperEdge()).
     *
     * <P>If this HyperEdge is successfully added to net,
     * all HyperEdgeManager ChangeListeners are notified by
     * invoking each listener's <CODE>objectChanged()</CODE> method with an
     * EventNote of type HYPEREDGE, sub-type=ADDED and a
     * supporting information object being net.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     * @see HyperEdge#connectHyperEdges
     */
    boolean addToNetwork(CyNetwork net);

    /**
     * Remove this HyperEdge from a given CyNetwork.  The CyEdges of
     * this HyperEdge will be removed from this CyNetwork.  Under
     * certain conditions, the ConnectorNode of this HyperEdge will
     * also be removed (see {@link
     * cytoscape.hyperedge.HyperEdge#destroy destroy()} for
     * details).  CyNodes are <em>never</em> removed, since it is
     * difficult to tell if the CyNodes existed prior to the creation
     * of the HyperEdge.
     * @param net the CyNetwork from which to remove this HyperEdge.
     *           If null, will remove this HyperEdge from all
     *           CyNetworks for which this HyperEdge belongs
     *           (equivalent to {@link HyperEdge#destroy destroy()}).
     * @return true if this HyperEdge was removed from net. false if
     * this HyperEdge was not a member of net.
     *
     * <P>Before this HyperEdge is successfully removed from net,
     * one of two event notifications are fired:
     * <OL>
     * <LI>If net is null, or is the last CyNetwork to which
     * this HyperEdge belongs, the HyperEdge will be deleted and all
     * the HyperEdgeManager's DeleteListeners are notified this
     * HyperEdge is deleted (see {@link
     * cytoscape.hyperedge.event.DeleteListener#objectDestroyed
     * DeleteListener.objectDestroyed()}).
     * <LI>If net is not null and is not the last CyNetwork to
     * which this HyperEdge belongs, all the HyperEdgeManager's
     * ChangeListeners are notified by invoking each listener's
     * <CODE>objectChanged()</CODE> method with an EventNote of type
     * HYPEREDGE, sub-type=REMOVED and a supporting information object
     * being net.
     * </OL>
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean removeFromNetwork(CyNetwork net);

    /**
     * Is this HyperEdge a member of a given CyNetwork?
     * @param net the CyNetwork in question.
     * @return true iff HyperEdge is a member of net.
     * @throws IllegalArgumentException if net == null.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean inNetwork(CyNetwork net);

    /**
     * @return a read-only Iterator (doesn't allow modification) of the
     * distinct String interaction types of the CyEdges contained by
     * this HyperEdge.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    Iterator<String> getAllEdgeTypes();


    /**
     * Return the CyNode associated with an given CyEdge of this HyperEdge (this is <EM>not</EM>
     * the ConnectorNode).
     * @param edge the CyEdge for which to return the associated Node.
     * @throws IllegalArgumentException if edge is null or doesn't belong to this HyperEdge.
     * @return the CyNode connected to edge.
     */
    CyNode getNode(CyEdge edge);

    /**
     * @return the minimum number of edges that a HyperEdge can have.
     */
    int getMinimumNumberEdges();

    /**
     * Obtain the number of CyNodes in this HyperEdge.
     * @return the number of distinct CyNodes contained in this HyperEdge.
     * For homodimer-like structures, the number of CyNodes may not
     * equal the number of CyEdges.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    int getNumNodes();

    /**
     * @return the number of CyEdges contained in this HyperEdge.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    int getNumEdges();

    /**
     * Return all CyNodes connected to a CyEdge of a given interaction
     * type (e.g., EdgeTypeMap.SUBSTRATE) or return all CyNodes within
     * this HyperEdge if the given interaction type is null.
     * @param edgeIType the interaction type of the CyEdges that connect
     * to the CyNodes of interest.
     * @return a non-null, read-only Iterator (doesn't allow
     * modification) over all distinct CyNodes associated with a CyEdge
     * whose interaction type is edgeIType.  If edgeIType is null, all
     * distinct CyNodes belonging to this HyperEdge are returned. The
     * Iterator will be empty if there are no CyNodes associated with an
     * CyEdge whose type is edgeIType.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    Iterator<CyNode> getNodes(String edgeIType);

    /**
     * Return all CyNodes connected to CyEdges whose interaction type is
     * one of a given Collection of types (e.g., ACTIVATING_MEDIATOR
     * or INHIBITING_MEDIATOR) or return all CyNodes within this
     * HyperEdge if the given Collection is null.
     * @param edgeITypes a Collection of the interaction types of the
     * CyEdges that connect to the CyNodes of interest.
     * @return a non-null, read-only Iterator (doesn't allow
     * modification) over all distinct CyNodes associated with any CyEdge
     * of this HyperEdge whose interaction type is a member of
     * edgeITypes.  If edgeITypes is null, all distinct CyNodes
     * belonging to this HyperEdge are returned. The Iterator will be
     * empty if there are no CyNodes associated with any CyEdge whose type
     * is a member of edgeITypes.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    Iterator<CyNode> getNodesByEdgeTypes(Collection<String> edgeITypes);

    /**
     * Returns all CyEdges associated with a given CyNode or returns all
     * CyEdges within this HyperEdge if the given CyNode is null.
     * @param node the CyNode for which to find associated CyEdges.
     * @return a non-null, read-only Iterator (doesn't allow modification)
     *         over all CyEdges associated with node. If node
     *         is null, all CyEdges belonging to this HyperEdge are
     *         returned.  The Iterator will be empty if
     *         there are are no CyEdges associated with node in this
     *         HyperEdge.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    Iterator<CyEdge> getEdges(CyNode node);

    /**
     * Returns an arbitrary CyEdge associated with a given CyNode.
     * This is a convenience method since the
     * majority of CyNodes will only have one CyEdge associated with them.
     * @param node the Node for which to return an Edge that is part of this HyperEdge.
     * @return an arbitrary CyEdge associated with node. Will return null if
     * node is null or is not associated with any CyEdge in this HyperEdge.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    CyEdge getAnEdge(CyNode node);

    /**
     * Does a given node have more than one CyEdge in this HyperEdge?
     * @param node the Node to check for multiple Edges.
     * @return true iff node has two or more CyEdges associated with it.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean hasMultipleEdges(CyNode node);

    /**
     * @return the HyperEdge CyNode associated with this HyperEdge.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    CyNode getConnectorNode();

    /**
     * Does this HyperEdge contain a given Node?
     * @param node the CyNode to check for containment within this HyperEdge.
     *             Note that this doesn't include this
     *             HyperEdge's ConnectorNode, which is treated specially.
     * @return true iff this HyperEdge contains node.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     * @see HyperEdge#hasConnectorNode
     */
    boolean hasNode(CyNode node);

    /**
     * Is the ConnectorNode of this HyperEdge the given CyNode?
     * @param node the CyNode to check for containment within this HyperEdge.
     * @return true iff the ConnectorNode of this HyperEdge is node.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean hasConnectorNode(CyNode node);

    /**
     * Does this HyperEdge contain a given CyEdge?
     * @param edge the CyEdge to check for containment within this HyperEdge.
     * @return true iff this HyperEdge contains edge.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean hasEdge(CyEdge edge);

    /**
     * Return whether this HyperEdge contains a CyEdge with a given
     * edge interaction type.
     * @param edgeIType the String interaction type of a matching CyEdge.
     * @return true iff this HyperEdge contains an edge whose
     *         interaction type equals edgeIType.
     *         If edgeIType is null, returns false.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean hasEdgeOfType(String edgeIType);

    /**
     * Remove all CyEdges associated with a given CyNode from this
     * HyperEdge.  This may cause this entire HyperEdge to be removed
     * if the number of CyEdges falls below {@link
     * HyperEdge#getMinimumNumberEdges getMinimumNumberEdges} (see {@link
     * cytoscape.hyperedge.HyperEdge#destroy destroy()} for
     * details).
     *
     * <P>Based on the above situations, one of two event
     * notifications may be fired:
     * <OL>
     * <LI>If removal of the CyNode leads to this HyperEdge being
     * deleted, then <EM>before</EM> this HyperEdge is deleted, all
     * HyperEdgeManger DeleteListeners are notified that this
     * HyperEdge is being deleted (see {@link
     * cytoscape.hyperedge.event.DeleteListener#objectDestroyed
     * DeleteListener.objectDestroyed()}).
     *
     * <LI>If removal of the CyNode does <EM>not</EM> lead to this HyperEdge
     * being deleted,  then <EM>before</EM> each
     * CyEdge is removed, all HyperEdgeManager
     * ChangeListeners are notified by invoking each listener's
     * <CODE>objectChanged()</CODE> method with an EventNote of type
     * EDGE, sub-type=REMOVED and a supporting information object=the
     * removed edge (see {@link
     * cytoscape.hyperedge.event.ChangeListener#objectChanged
     * ChangeListener.objectChanged()}).
     * Note that multiple objectChanged() notifications may take place--one
     * for each CyEdge associated with node.
     * </OL>
     * @param node the CyNode to remove.
     * @return true if the node was removed,
     * false if the node is not found, or is null.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean removeNode(CyNode node);

    /**
     * Remove the given CyEdge from this HyperEdge.  If the CyNode
     * associated with this CyEdge is not connected to any other
     * CyEdges, then this CyNode will be removed from this HyperEdge.
     * Removal of this CyEdge will cause this entire HyperEdge to be
     * removed if the number of CyEdges falls below {@link
     * HyperEdge#getMinimumNumberEdges getMinimumNumberEdges} (see
     * {@link cytoscape.hyperedge.HyperEdge#destroy destroy()}
     * for details).
     * <P>Based on the above situations, one of two event notifications
     * may be fired:
     * <OL>
     * <LI>If removal of the CyEdge leads to this HyperEdge being
     * deleted then <EM>before</EM> this HyperEdge is deleted, all
     * HyperEdgeManger DeleteListeners are notified that this
     * HyperEdge is being deleted (see {@link
     * cytoscape.hyperedge.event.DeleteListener#objectDestroyed
     * DeleteListener.objectDestroyed()}).
     *
     * <LI>If removal of the CyEdge does <EM>not</EM> lead to this
     * HyperEdge being deleted, then <EM>before</EM> this
     * this CyEdge is removed, all HyperEdgeManager ChangeListeners are
     * notified by invoking each listener's
     * <CODE>objectChanged()</CODE> method with an EventNote of type
     * EDGE, sub-type=REMOVED and a supporting information object=the
     * removed edge (see {@link
     * cytoscape.hyperedge.event.ChangeListener#objectChanged
     * ChangeListener.objectChanged()}).
     * </OL>
     * @param edge the CyEdge to remove.
     * @return true if the edge was removed,
     * false if edge is null or is not a member of this HyperEdge.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean removeEdge(CyEdge edge);

    /**
     * Add a new CyEdge connecting a given CyNode to this HyperEdge and
     * to all CyNetworks to which this HyperEdge belongs.
     *
     * <P>Under the appropriate conditions (see {@link
     * cytoscape.hyperedge.event.ChangeListener#objectChanged
     * ChangeListener.objectChanged()}) and <EM>after</EM> the CyEdge is added,
     * all of the HyperEdgeManager's ChangeListeners are notified by
     * invoking their <CODE>objectChanged()</CODE> method with an
     * EventNote of type EDGE, sub-type=ADDED and a
     * supporting information object=the new CyEdge.
     * @param node the CyNode for which to add a new CyEdge. If node is
     * not a member of this HyperEdge, it will be added.  If node is
     * already a member of this HyperEdge, and edgeIType is different
     * then the interaction type of all existing CyEdges to node,
     * another CyEdge will be created that is attached to node
     * with the given edgeIType. If a CyEdge is found that attaches
     * to node and has the same edgeIType, an exception will be thrown.
     * @param edgeIType the interaction type of the CyEdge to create.
     * edgeIType also determines the Role played by the node within
     * the newly created CyEdge. This is done by using edgeIType as a
     * key in the EdgeTypeMap to determine the EdgeRole associated
     * with that edgeIType. If the value associated with the
     * edgeIType key is EdgeRole.SOURCE, then node will be the
     * source of the newly created CyEdge. If the value associated with
     * the edgeIType key is EdgeRole.TARGET, then node will be the
     * target of the newly created CyEdge.  If edgeIType is not the
     * key for any EdgeRole in the EdgeTypeMap, then node is used as
     * the source of the newly created CyEdge.
     * @return the newly created CyEdge that was added to all CyNetworks
     *         to which this HyperEdge belongs.
     * @throws IllegalArgumentException if node or edgeIType are null,
     * if node is a ConnectorNode of another HyperEdge, or if a CyEdge
     * already exists that attaches to node with the given edgeIType.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    CyEdge addEdge(CyNode node, String edgeIType);

    /**
     * Connect this HyperEdge to another. Adds a CyEdge that is shared
     * in between this HyperEdge and a given target HyperEdge.  The
     * CyEdge connects the two HyperEdge's ConnectorNodes.  Note that
     * once two HyperEdges are connected, they can only exist in at
     * most one CyNetwork (addToCyNetwork() will throw an
     * exception). If an edge connection already exists in between
     * this HyperEdge and the given target HyperEdge, that contains
     * the given CyEdge interaction types, it will simply be returned.
     * @param targetHE the target HyperEdge to connect to. Must be
     *        a member of exactly the same one CyNetwork as this
     *        HyperEdge.
     * @param edgeIType the interaction type of the CyEdge within
     *                    this HyperEdge and targetHE.
     * @return the shared CyEdge.
     * @throws IllegalArgumentException if targetHE is
     *         this HyperEdge, if this HyperEdge or targetHE belong to
     *         more than one CyNetwork, if this HyperEdge and targetHE
     *         don't belong to the same CyNetwork, or if fromEdgeIType
     *         or toEdgeIType are null.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    CyEdge connectHyperEdges(HyperEdge targetHE, String edgeIType);
    //    CyEdge connectHyperEdges(HyperEdge targetHE, String fromEdgeIType,
    //                             String toEdgeIType);


    /**
     * Copy this HyperEdge into a new HyperEdge and place it in the
     * specified CyNetwork.  The copy will include copies of all 
     * CyEdges of this HyperEdge that pass the given EdgeFilter.
     * All attributes associated with
     * this HyperEdge's ConnectorNode and the copied edges are copied.
     * @param net the CyNetwork into which to place the copied HyperEdge.
     * @param filter the EdgeFilter to apply to each CyEdge of this
     * HyperEdge.  Only Edges that pass the EdgeFilter are included in
     * the copy. If an Edge is a shared edge, the connected HyperEdge
     * is also copied with the EdgeFilter being applied to each of the
     * connected HyperEdge's CyEdges.
     * Example:
     * <PRE>
     * he1.copy (net , new EdgeFilter () {
     *	    boolean includeEdge (HyperEdge he, CyEdge edge) { return true; }
     *	});
     * </PRE>
     * would copy all edges. For some common examples, see EdgeFilters.
     * @throws IllegalArgumentException if net or filter is null, or if
     * any of the HyperEdges copied would not have enough remaining
     * edges to make a new HyperEdge (see {@link
     * HyperEdge#getMinimumNumberEdges getMinimumNumberEdges}).
     * @return a non-null Map with keys being the HyperEdges to copy
     * and values being the corresponding copied HyperEdges. If
     * filter excludes shared edges, or if this HyperEdge has no
     * shared edges, then the Map will contain one element--this
     * HyperEdge as the key with a copy of this HyperEdge as the value.
     * @see HyperEdge#getMinimumNumberEdges
     * @see cytoscape.hyperedge.impl.utils.EdgeFilters
     */
    Map<HyperEdge, HyperEdge> copy(CyNetwork net, EdgeFilter filter);

    /**
     * Are the given CyNodes members of this HyperEdge?
     * @param node1 the first CyNode to consider as a member.
     * @param node2 the second CyNode to consider as a member.
     * @return true iff both node1 and node2 belong to this HyperEdge.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean isConnected(CyNode node1, CyNode node2);

    /**
     * Does this HyperEdge contain connections to other HyperEdges via
     * a shared edge?
     * @return true iff this HyperEdge contains one or more CyEdges
     * that connect to the ConnectorNode of another HyperEdge.
     */
    boolean hasSharedEdges();

    /**
     * Return all edges that connect to other HyperEdges.
     * @return  a non-null, read-only Iterator (doesn't allow
     * modification) over all edges that connect to other HyperEdges.
     * If this HyperEdge doesn't contain any shared edges, an
     * empty iterator is returned.
     */
    Iterator<CyEdge> getSharedEdges();

    /**
     * Is a given CyEdge a shared edge (connects to another HyperEdge)
     * of this HyperEdge?
     * @param edge the CyEdge for which to determine if shared.
     * @return true iff edge is a shared edge of this HyperEdge.
     */
    boolean isSharedEdge (CyEdge edge);

    /**
     * Return all the CyNetworks this HyperEdge belongs to.
     * @return a non-null, read-only Iterator (doesn't allow
     * modification) over all CyNetworks to which this HyperEdge
     * belongs.
     */
    Iterator<CyNetwork> getNetworks();

    /**
     * Remove this HyperEdge. All CyEdges belonging to this HyperEdge
     * will be removed from Cytoscape. The ConnectorNode of this
     * HyperEdge is also removed unless "regular" (non-HyperEdge)
     * edges are connected to the ConnectorNode. In this case, the
     * ConnectorNode is removed from all CyNetworks that do not have
     * such edges. All automatically generated attributes of the
     * ConnectorNode and all CyEdges are also removed. The LifeState
     * of this HyperEdge is set to DELETED and any future attempts to
     * perform operations on this object will throw
     * IllegalStateExceptions.
     *
     * <P><EM>Before</EM> this HyperEdge is deleted, {@link
     * cytoscape.hyperedge.event.DeleteListener#objectDestroyed  DeleteListener.objectDestroyed()}
     * methods are invoked to notify interested listeners that this
     * object is being destroyed.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    void destroy();

    /**
     * @return the state of this object as defined by LifeState (e.g., LifeState.NORMAL).
     */
    LifeState getState();

    /**
     * Is this object in a given LifeState?
     * @param ls the LifeState that must match the state of this HyperEdge.
     * @return true iff this object is in state ls.
    */
    boolean isState(LifeState ls);

    /**
     * @return a human readable string representing the content of this object.
     *
     */
    String toString();

    /**
     * A convenience method for obtaining the HyperEdgeManager.
     * Equivalent to HyperEdgeFactory.INSTANCE.getHyperEdgeManager().
     * @return the HyperEdgeManager
     */
    HyperEdgeManager getHyperEdgeManager();

    /**
     * Return the name given to this HyperEdge.
     * @return the String name given to this HyperEdge, or null if none
     *         has been set.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    String getName();

    /**
     * Set the name of this object.
     * <P>Under the appropriate conditions (see {@link
     * cytoscape.hyperedge.event.ChangeListener#objectChanged
     * ChangeListener.objectChanged()}) and <EM>after</EM> the name
     * has been changed,
     * all of this object's ChangeListeners are notified by
     * invoking their <CODE>objectChanged()</CODE> method with an
     * EventNote of type NAME, sub-type=CHANGED and a
     * supporting information object the previous name.
     * @param newName the new String name of this object.
     * @return the previous name of the object.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    String setName(String newName);

    /**
     * Is this HyperEdge considered to be a directed edge?
     * @return true if this HyperEdge is directed. Returns false
     * if directed was never set.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean isDirected();

    /**
     * Set the directedness of this HyperEdge.
     * <P>Under the appropriate conditions (see {@link
     * cytoscape.hyperedge.event.ChangeListener#objectChanged
     * ChangeListener.objectChanged()}) and <EM>after</EM> the directedness
     * has been changed,
     * all of this object's ChangeListeners are notified by
     * invoking their <CODE>objectChanged()</CODE> method with an
     * EventNote of type DIRECTED, sub-type=CHANGED and a
     * supporting information object=null.
     * @return the last value of directedness.
     * @param newState the directedness this HyperEdge is to have.
     * @throws IllegalStateException if this HyperEdge has been deleted.
     */
    boolean setDirected(boolean newState);

}
