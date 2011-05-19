
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
* Wed Oct 08 05:07:14 2008 (Michael L. Creech) creech@w235krbza760
* Massive update for Checkstyle. Removed some non-utf characters from comments.
* Thu Jun 21 05:07:14 2007 (Michael L. Creech) creech@w235krbza760
*  Added deleteAttribute() and its use.
* Tue Dec 12 08:20:52 2006 (Michael L. Creech) creech@w235krbza760
*  Added getEdgeInteractionType() and updated toString() method to
*  handle HyperEdges.
* Thu Nov 30 07:46:32 2006 (Michael L. Creech) creech@w235krbza760
*  Changed createConnectorNode to add ENTITY_TYPE attribute.
* Tue Nov 28 09:28:22 2006 (Michael L. Creech) creech@w235krbza760
*  Added createEdge() and createConnectorNode().
* Mon Nov 06 16:07:46 2006 (Michael L. Creech) creech@w235krbza760
*  Changed Node-->CyNode.
* Thu Nov 02 04:37:38 2006 (Michael L. Creech) creech@w235krbza760
*  Added buildUnmodifiableCollectionIterator() and createCollection().
* Sun Aug 27 15:19:34 2006 (Michael L. Creech) creech@w235krbza760
*  Added toString().
* Thu Aug 24 15:22:56 2006 (Michael L. Creech) creech@w235krbza760
*  Removed removeNode() and removeEdge().
* Tue Aug 15 12:51:40 2006 (Michael L. Creech) creech@w235krbza760
*  removed slowGetEdge() and slowGetNode().
* Tue Jun 14 09:38:01 2005 (Michael L. Creech) creech@Dill
*  Added isLogging(), setLogging(), generateUUID(), notNull(), copyList(),
*  stringEqual().
********************************************************************************
*/
package cytoscape.hyperedge.impl.utils;


import giny.model.GraphObject;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.AttributeFilter;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.LifeState;
import cytoscape.hyperedge.impl.HyperEdgeImpl;
import cytoscape.logger.CyLogger;


/**
 * Utility operations used throughout HyperEdge.
 * @author Michael L. Creech
 * @version 1.0
 */
public final class HEUtils {
    private static final String ATTRIBUTE_TRUE = "true";
    private static boolean doLogging = true;
    private static CyLogger logger = CyLogger.getLogger("HyperEdge");
    static {
	logger.setDebug (doLogging);
    }

    // used for making generateUUID() thread-safe and reducing the span of
    // what is locked:
    private static Boolean   lockObjForUUID = new Boolean(true);
    private static String localHostCache = cacheLocalHostInfo();

    // The number of calls to the UUID routine:
    private static long uuidCallNum;

    // Don't want people to manipulate the utility class constructor.
    private HEUtils() {}
    
    /**
     * Convenience method for throwing an IllegalArgumentException given
     * a message.
     * @param msg the String message associated with the exception.
     * @throws IllegalArgumentException
     */
    public static void throwIllegalArgumentException(final String msg){
        final IllegalArgumentException ex = new IllegalArgumentException(msg);
        ex.fillInStackTrace();

        // ex.printStackTrace();
	logger.error (msg,ex);
        throw ex;
    }

    /**
     * Convenience method for throwing an IllegalStateException given
     * a message.
     * @param msg the String message associated with the exception.
     * @throws IllegalStateException
     */
    public static void throwIllegalStateException(final String msg) {
        final IllegalStateException ex = new IllegalStateException(msg);
        ex.fillInStackTrace();

        // ex.printStackTrace();
	logger.error (msg,ex);
        throw ex;
    }

    //    static public Iterator createEmptyUnmodifiableListIterator() {
    //        return (Collections.unmodifiableList(new ArrayList(0))).iterator();
    //    }

    /**
     * Output a message to a CyLogger for HyperEdges.
     * Commonly used for debugging. Will not log when
     * isLogging returns false.
     * @param msg the String message to log.
     * @see HEUtils#isLogging
     * @see HEUtils#setLogging
     */
    public static void log(final String msg) {
	//        if (doLogging) {
	//            System.out.println(msg);
	//        }
	logger.info(msg);
    }

    /**
     * Output a message to a CyLogger for HyperEdges.
     * Used for logging non-fatal errors and warnings.
     * Will not log when isLogging returns false.
     * @param msg the String message to log.
     */
    public static void errorLog(final String msg) {
        // System.err.println(msg);
	logger.warn (msg);
    }

    /**
     * @return true iff we are displaying logged messages.
     */
    public static boolean isLogging() {
        return doLogging;
    }

    /**
     * @param doLog whether to perform logging for this CyLogger.
     * @return true iff we should display a logged messages?
     */
    public static boolean setLogging(final boolean doLog) {
        final boolean retVal = doLogging;
        doLogging = doLog;
	logger.setDebug (doLogging);
        return retVal;
    }

    /**
     * If you don't know why this method is called "AbbyNormal", you
     * probably haven't seen the movie YoungFrankenstein!
     * @param obj the HyperEdge whose state will be checked.
     */
    public static void checkAbbyNormal(final HyperEdge obj) {
        //        if ((!obj.isState (LifeState.NORMAL)) &&
        //            (!obj.isState (LifeState.CREATION_IN_PROGRESS)) &&
        //            (!obj.isState (LifeState.DELETION_IN_PROGRESS)))
        if (obj.isState(LifeState.DELETED)) {
            final String                msg = "Attempting operation on object " +
                                        obj.getIdentifier() + " in state " +
                                        obj.getState() +
                                        " when we expected the object to be in state " +
                                        LifeState.NORMAL + ", " +
                                        LifeState.CREATION_IN_PROGRESS +
                                        ", or " +
                                        LifeState.DELETION_IN_PROGRESS + ".";
            final IllegalStateException ex = new IllegalStateException(msg);
            ex.fillInStackTrace();

            // ex.printStackTrace();
	    logger.error (msg,ex);
            throw ex;
        }
    }

    /**
     * Attempts to generate a fairly universally unique identifier of
     * the form:
     * <PRE>
     *    &lt;uuid_call_num>:&lt;time>:&lt;host>.
     * </PRE>
     * Where:
     * &lt;uuid_call_num> is the number of times this UUID
     * routine has been called.
     * <P>&lt;time> is the current time in milliseconds.
     * <P>&lt;host> is the internet address of the host
     * machine. If not available, the user name and user home are used.
     * <P>Note that this UUID is not truely unique, since two processes on
     * the same machine could generate the same numbered UUID.
     *
     * <P>This method is thread-safe--several coexisting threads can call it
     *    and be guaranteed a unique UUID.
     *
     * <P>A sample UUID
     * is: <CODE>10:973202057660:64.92.27.47</CODE> if no host machine name is
     * available, the form is <CODE>7:973202057660:mikeC:\WINDOWS</CODE>.
     * @param prefix an optional prefix to be used in building the UUIDs.
     * @return the String representing the UUID.
     */
    public static String generateUUID(final String prefix) {
        final long          time = System.currentTimeMillis();
        final StringBuilder sb = new StringBuilder();

        if (prefix != null) {
            sb.append(prefix);
            sb.append('-');
        }

        // lockObjForUUID is only used to reduce the scope of what
        // is locked when we change uuidCallNum. This is instead of
        // doing a 'synchronized (this)' statement:
        synchronized (lockObjForUUID) {
            sb.append(++uuidCallNum);
        }

        sb.append(':');
        sb.append(time);
        sb.append(':');

        sb.append(localHostCache);

        return sb.toString();
    }

    /**
     * Getting the host address information can be *very* slow, so only
     * do it once.
     */
    private static String cacheLocalHostInfo() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return (System.getProperty("user.name") +
                   System.getProperty("user.home"));
        }
    }

    /**
     * Ensures that <EM>obj</EM> is non-null. If
     * found to be null, this method and raises an
     * <CODE>IllegalArgumentException</CODE>.
     * @param obj the Object to ensure is non-null.
     * @param arg the name of the argument being checked--used
     * for issuing an error message and throwing an exception with a
     * message of the form:
     * <PRE>
     *   "Found the object '" + arg + "' to be null."
     * </PRE>
     * @throws IllegalArgumentException if <EM>obj</EM> is null.
     */
    public static void notNull(final Object obj, final String arg) {
        if (obj == null) {
            final String error = "Found the object '" + arg + "' to be null.";
            throw new IllegalArgumentException(error);
        }
    }

    /**
     * Returns the class name of an object without all the package information
     * This is very useful for
     * debugging (e.g., <CODE>toString()</CODE>) routines.
     *
     * @param obj the Object from which to derive the class name.
     * Example:
     *<CODE>
     *<PRE>
     *getClassName ("package.CachingList")-->"CachingList"
     *</PRE>
     *</CODE>
     * @return the String representing the class name, or null if <EM>obj</EM>
     *         is null.
     */
    public static String getAbrevClassName(final Object obj) {
        if (obj == null) {
            return null;
        }

        String fullName = obj.getClass().getName();
        final int    index = fullName.lastIndexOf('.');

        if (index >= 0) {
            fullName = fullName.substring(index + 1);
        }

        return fullName;
    }

    /**
     * Return a copy of a given List.
     * Currently, an ArrayList-based implementation is used for the copy.
     * @param toCopy the List to copy.
     * @param <Element> the type of the List elements.
     * @return a shallow copy of <EM>to_copy</EM>.
     * If <EM>to_copy</EM> is null, return null.
     */
    public static <Element> List<Element> copyList(final List<Element> toCopy) {
        if (toCopy == null) {
            return null;
        }

        final List<Element> copy = new ArrayList<Element>(toCopy.size());
        copy.addAll(toCopy);

        return (copy);
    }

    /**
     * Check if two string fields are equal to each other.
     * @param field1 first string to compare. May be null.
     * @param field2 second string to compare. May be null.
     * @param caseSensitive true if we are to match in
     *        a case sensitive manner, false otherwise.
     * @return true if the two fields are null
     * or have the same characters. Return false otherwise.
     */
    public static boolean stringEqual(final String field1, final String field2,
                                      final boolean caseSensitive) {
        if (field1 != field2) {
            if (field1 != null) {
                if (caseSensitive) {
                    return field1.equals(field2);
                } else {
                    return field1.equalsIgnoreCase(field2);
                }
            } else {
                // field1 is null and field2 must not be null, so not equal:
                return false;
            }
        }

        return true;
    }

    /**
     * Return the part of a string to the right of a match character
     * (i.e., suffix).
     * @param str the String within which to search for a suffix.
     *                     May be null.
     * @param matchChar the character to match for finding a suffix.
     *                            all characters to the right of the rightmost
     *                            <EM>match_char</EM> make up the suffix.
     * @return null if no match character is found or if the
     *                           match character is at the end of the string,
     *                           or if <EM>str</EM> is null.
     *                           Otherwise the String suffix is returned.
     */
    public static String getSuffix(final String str, final char matchChar) {
        String suffix = null;

        if (str != null) {
            final int i = str.lastIndexOf(matchChar);

            // It must be less than length() - 1 to have any characters to the right
            // of match_char:
            if ((i >= 0) && (i < (str.length() - 1))) {
                suffix = str.substring(i + 1);
            }
        }

        return suffix;
    }

    /**
     * Ensures that a specified directory exists.
     * @param dirPath the path to a given directory.
     * If <EM>dir_path</EM> doesn't exist, it is created.
     * @return true if the directory already exists, or is
     * successfully created.
     * Returns false otherwise.
     *
     */
    public static boolean ensurePathCreated(final String dirPath) {
        final File    f      = new File(dirPath);
        final boolean exists = f.isDirectory();

        if (!exists) {
            return f.mkdirs();
        } else {
            return true;
        }
    }

    //    public static CyEdge findEdge (String uuid)
    //    {
    //	CyEdge edge = _root_graph.getEdge (uuid);
    //	if (edge != null)
    //	    {
    //		//		log ("findEdge: " + uuid + " exists = true" +
    //		//		     " RGidx = " + _root_graph.getIndex (edge));
    //	    }
    //	else
    //	    {
    //		//		log ("findEdge: " + uuid + " exists = false");
    //	    }
    //	return edge;
    //    }
    /**
     * @param uuid the identifier of the CyNode desired.
     * @return a CyNode for a given Node name (uuid). 
     * If no such CyNode exists, a new CyNode is created.
     */
    public static CyNode getNode(final String uuid) {
        // return Cytoscape.getCyNode (uuid, true);
        //        log ("getNode: " + uuid + " exists = " +
        //             (_root_graph.getNode (uuid) != null));
        // CyNode node = (CyNode) slowGetNode (uuid);
        return Cytoscape.getCyNode(uuid, true);
    }

    //    public static void removeNode(CyNode node) {
    //        //        log ("removeNode: " + node.getIdentifier ());
    //        _root_graph.removeNode(node);
    //    }

    //    public static void removeEdge(CyEdge edge) {
    //        //        log ("removeEdge: " + edge.getIdentifier ());
    //        _root_graph.removeEdge(edge);
    //    }

    //    // TODO To be replaced with call to CytoscapeRootGraph.getEdge() when
    //    //       bug is fixed.
    //    public static CyEdge slowGetEdge (String uuid)
    //    {
    //        Iterator it   = _root_graph.edgesIterator ();
    //        CyEdge     edge;
    //        while (it.hasNext ())
    //        {
    //            edge = (CyEdge) it.next ();
    //            if (uuid.equals (edge.getIdentifier ()))
    //            {
    //                return edge;
    //            }
    //        }
    //        return null;
    //    }
    //
    //    // TODO To be replaced with call to CytoscapeRootGraph.getNode() when
    //    //       bug is fixed.
    //    public static CyNode slowGetNode (String uuid)
    //    {
    //	if (uuid == null) { return null; }
    //	Iterator it   = _root_graph.nodesIterator ();
    //	CyNode     node;
    //	while (it.hasNext ())
    //	    {
    //		node = (CyNode) it.next ();
    //		if (uuid.equals (node.getIdentifier ()))
    //	            {
    //	                return node;
    //	            }
    //	    }
    //	return null;
    //    }
    
    /**
     * @param edge the CyEdge for which to find the associated interaction type.
     * @return the interaction type of a given edge.
     */
    
    public static String getEdgeInteractionType(final CyEdge edge) {
        return Cytoscape.getEdgeAttributes().getStringAttribute(edge.getIdentifier(),
                                              Semantics.INTERACTION);
    }

    /**
     * Create a CyEdge that connects two given CyNodes and will be part of a HyperEdge.
     * @param source the source of the CyEdge.
     * @param target the target of the CyEdge.
     * @param edgeIType the interaction type of the CyEdge.
     * @return the CyEdge created.
     */
    public static CyEdge createHEEdge(final CyNode source, final CyNode target,
                                      final String edgeIType) {
        final CyEdge edge = createEdge(source, target, edgeIType);
        Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),
                                                   HyperEdgeImpl.HYPEREDGE_EDGE_TAG_NAME,
                                                   ATTRIBUTE_TRUE);

        return edge;
    }
    /**
     * Create a CyEdge that connects two given CyNodes.
     * @param source the source of the CyEdge.
     * @param target the target of the CyEdge.
     * @param edgeIType the interaction type of the CyEdge.
     * @return the CyEdge created.
     */
    public static CyEdge createEdge(final CyNode source, final CyNode target,
                                    final String edgeIType) {
        return Cytoscape.getCyEdge(source, target, Semantics.INTERACTION,
                                   edgeIType, true, true);
    }

    /**
     * Create a HyperEdge ConnectorNode.
     * @param uuid the identifier of this connector node.
     * @return the CyNode ConnectorNode.
     */
    public static CyNode createConnectorNode(final String uuid) {
        final CyNode retNode = getNode(uuid);

        // TODO Fix hack for removing canonical name on connectors:
        // &&& HACK getNode() sets the CANONICAL_NAME to the node id,
        //     which we don't  want for connector nodes:
        final CyAttributes nodeAddrs = Cytoscape.getNodeAttributes();
	// MLC 06/21/07:
        // nodeAddrs.deleteAttribute(uuid, HyperEdgeImpl.LABEL_ATTRIBUTE_NAME);
	// MLC 06/21/07:
        deleteAttribute (nodeAddrs, uuid, HyperEdgeImpl.LABEL_ATTRIBUTE_NAME);
        nodeAddrs.setAttribute(uuid,
                               HyperEdgeImpl.IS_CONNECTOR_NODE_ATTRIBUTE_NAME,
                               ATTRIBUTE_TRUE);
        nodeAddrs.setAttribute(uuid, HyperEdgeImpl.ENTITY_TYPE_ATTRIBUTE_NAME,
                               HyperEdgeImpl.ENTITY_TYPE_CONNECTOR_NODE_VALUE);

        return retNode;
    }


    // delete's the given attribute from the given identifier from the
    // given CyAttributes. This is temporary method until
    // CyAttributes.deleteAttribute() if fixed to not throw an
    // IllegalStateException when the attribute to delete doesn't
    // exist (to be fixed in Cytoscape 2.6).
    /**
     * @param attrs the CyAttributes to delete an attribute from.
     * @param uuid the identifier of the object in attrs to remove the attribute from.
     * @param attrName the name of the attribute value to remove.
     * @return true iff the attribute value was removed.
     */
    public static boolean deleteAttribute (final CyAttributes attrs,
				    final String uuid , final String attrName) {
	if (!attrs.hasAttribute (uuid, attrName)) {
	    return false;
	}
	// it really has the attribute:
	return attrs.deleteAttribute (uuid, attrName);
    }

    /**
     * Return an unmodifiable Iterator over a given Collection.
     * The way this is currently implemented, is to return an Iterator
     * using Collections.unmodifiableCollection() of the internal
     * value of interest. For example, the HyperEdge internal Set
     * _edges could be returned as:
     * <PRE>
     *    Collections.unmodifiableCollection(_edges).iterator();
     * </PRE>
     *
     *<P>This Collections.unmodifiableCollection() method provide a read-only
     * "view" onto the internal data structure--acting as a wrapper.
     * For example, here's the Java API description of unmodifiableSet():
     *<BLOCKQUOTE>
     *   "Returns an unmodifiable view of the specified collection. This method
     *    allows modules to provide users with "read-only" access to internal
     *    collections. Query operations on the returned collection "read
     *    through" to the specified collection, and attempts to modify the
     *    returned collection, whether direct or via its iterator, result in an
     *    UnsupportedOperationException."
     *</BLOCKQUOTE>
     *
     * <P>What this all implies is that if the underlying data structure is
     * modified after obtaining an Iterator, then attempting to use the
     * Iterator will lead to a ConcurrentModificationException.  For example,
     * if we obtain an HyperEdge CyEdge iterator and then add an underlying
     * HyperEdge, we would get a ConcurrentModificationException.  For
     * example, given a HyperEdge he:
     *
     * <PRE>
     *    Iterator&lt;CyEdge&gt; edgeIt = he.getEdges (null);
     *    he.addEdge ("A", EdgeTypeMap.PRODUCT);
     *    while (edgeIt.hasNext()) {
     *       CyEdge edge = edgeIt.next(); // throws ConcurrentModificationException
     *    }
     * </PRE>
     * @param colVal the Collection for which to build an Iterator.
     * @param <E> the type of the elements of the Collection and resulting Iterator.\
     * @return the unmodifiable Iterator over the Collection.
     */
    public static <E> Iterator<E> buildUnmodifiableCollectionIterator(final Collection<E> colVal) {
        return Collections.unmodifiableCollection(colVal).iterator();
    }

    /**
     * Create a new, safely modifiable Collection from a given Iterator.
     * This is also useful in rare cases where we must use a collection
     * of items independent of an unmodifiable collection iterator returned
     * because the underlying collection is modified and the iterator
     * would get a ConcurrentModificationException.
     * @param colIt the Iterator over elements to add to the Collection.
     * @param <E> the type of the elements in the Iterator and Collection.
     * @return the new safely modifiable Collection.
     */
    public static <E> Collection<E> createCollection(final Iterator<E> colIt) {
        final Collection<E> retCol = new ArrayList<E>();

        while (colIt.hasNext()) {
            retCol.add(colIt.next());
        }

        return retCol;
    }

    /**
     * Return a short, meaningful representation of a given object if it
     * is a CyEdge, CyNode, CyNetwork, or HyperEdge. Otherwise, just use
     * the object's toString() method.
     * @param obj the object to describe.
     * @return the representation of the object.
     */
    public static String toString(final Object obj) {
        final StringBuilder sb = new StringBuilder();

        if (obj instanceof GraphObject) {
            String id = ((GraphObject) obj).getIdentifier();

            if (id == null) {
                id = obj.toString();
            }

            primToString(id, obj, sb);
        } else if (obj instanceof CyNetwork) {
            String id = ((CyNetwork) obj).getIdentifier();

            if (id == null) {
                id = obj.toString();
            }

            primToString(id, obj, sb);
        } else if (obj instanceof HyperEdge) {
            final HyperEdge he    = (HyperEdge) obj;
            String    label = he.getName();

            if (label == null) {
                label = he.getIdentifier();
            }

            primToString(label, he, sb);
        } else {
            sb.append(obj);
        }

        return sb.toString();
    }

    private static void primToString(final String label, final Object obj, final StringBuilder sb) {
        sb.append('[');
        sb.append(obj.getClass().getSimpleName());
        sb.append(" ");
        sb.append(label);
        sb.append(']');
    }

    /**
     * Ignore attributes found in a given List. For example:
     * <PRE>
     * List&lt;String&gt; attrsToIgnore = new ArrayList&lt;String&gt;();
     * attrsToIgnore.add ("canonicalName");
     * attrsToIgnore.add ("label");
     * AttributeIgnoreFilter filter = new CyAttributesUtils.AttributeIgnoreFilter (attrsToIgnore);
     * CyAttributesUtils.copyAttributes (edge1, edge1Copy, filter, attrs, false);
     * </PRE>
     * would copy all attributes except for "canonicalName" and
     * "label", from edge1 to edge1Copy..
     */
    public static class AttributeIgnoreFilter implements AttributeFilter {
        private List<String> attrNames;
        /**
         * @param attrNames the list of attributes to ignore.
         */
        public AttributeIgnoreFilter(final List<String> attrNames) {
            this.attrNames = attrNames;
        }
        /**
         * {@inheritDoc}
         */
        public boolean includeAttribute(final CyAttributes attr, final String objID,
                                        final String attrName) {
            return (!attrNames.contains(attrName));
        }
    }
}
