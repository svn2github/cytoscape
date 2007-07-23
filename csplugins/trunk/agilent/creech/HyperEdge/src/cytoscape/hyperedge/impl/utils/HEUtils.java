/* -*-Java-*-
********************************************************************************
*
* File:         HEUtils.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/impl/utils/HEUtils.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Sun Apr 24 06:20:47 2005
* Modified:     Thu Jun 21 05:12:20 2007 (Michael L. Creech) creech@w235krbza760
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

import giny.model.GraphObject;

import java.io.File;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Utility operations used throughout HyperEdge.
 * @author Michael L. Creech
 * @version 1.0
 */
public class HEUtils {
    private static boolean _do_logging = true;

    // used for making generateUUID() thread-safe and reducing the span of
    // what is locked:
    private static Long   _lock_obj_for_uuid = new Long(0);
    private static String _local_host_cache = cacheLocalHostInfo();

    // The number of calls to the UUID routine:
    private static long _uuid_call_num = 0;

    /**
     * Convenience method for throwing an IllegalArgumentException given
     * a message.
     */
    static public void throwIllegalArgumentException(String msg)
                                              throws IllegalArgumentException {
        IllegalArgumentException ex = new IllegalArgumentException(msg);
        ex.fillInStackTrace();

        // ex.printStackTrace();
        throw ex;
    }

    /**
     * Convenience method for throwing an IllegalStateException given
     * a message.
     */
    static public void throwIllegalStateException(String msg)
                                           throws IllegalStateException {
        IllegalStateException ex = new IllegalStateException(msg);
        ex.fillInStackTrace();

        // ex.printStackTrace();
        throw ex;
    }

    //    static public Iterator createEmptyUnmodifiableListIterator() {
    //        return (Collections.unmodifiableList(new ArrayList(0))).iterator();
    //    }

    /**
     * Output a message to the system council or shell window.
     * Commonly used for debugging. Will not log when
     * isLogging returns false.
     * @see HEUtils#isLogging
     * @see HEUtils#setLogging
     */
    public static void log(String msg) {
        if (_do_logging) {
            System.out.println(msg);
        }
    }

    static public void errorLog(String msg) {
        System.err.println(msg);
    }

    /**
     * Are we displaying logged mesages?
     */
    public static boolean isLogging() {
        return _do_logging;
    }

    /**
     * Should we display logged mesages?
     */
    public static boolean setLogging(boolean do_log) {
        boolean ret_val = _do_logging;
        _do_logging = do_log;

        return ret_val;
    }

    /**
     * If you don't know why this method is called "AbbyNormal", you
     * probably haven't seen the movie YoungFrankenstein!
     */
    public static void checkAbbyNormal(HyperEdge obj) {
        //        if ((!obj.isState (LifeState.NORMAL)) &&
        //            (!obj.isState (LifeState.CREATION_IN_PROGRESS)) &&
        //            (!obj.isState (LifeState.DELETION_IN_PROGRESS)))
        if (obj.isState(LifeState.DELETED)) {
            String                msg = "Attempting operation on object " +
                                        obj.getIdentifier() + " in state " +
                                        obj.getState() +
                                        " when we expected the object to be in state " +
                                        LifeState.NORMAL + ", " +
                                        LifeState.CREATION_IN_PROGRESS +
                                        ", or " +
                                        LifeState.DELETION_IN_PROGRESS + ".";
            IllegalStateException ex = new IllegalStateException(msg);
            ex.fillInStackTrace();

            // ex.printStackTrace();
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
     */
    public static String generateUUID(String prefix) {
        long          time = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();

        if (prefix != null) {
            sb.append(prefix);
            sb.append('-');
        }

        // _lock_obj_for_uuid is only used to reduce the scope of what
        // is locked when we change _uuid_call_num. This is instead of
        // doing a 'synchronized (this)' statement:
        synchronized (_lock_obj_for_uuid) {
            sb.append(++_uuid_call_num);
        }

        sb.append(':');
        sb.append(time);
        sb.append(':');

        sb.append(_local_host_cache);

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
     * @throws <CODE>IllegalArgumentException</CODE> if <EM>obj</EM> is null.
     */
    static public void notNull(Object obj, String arg)
                        throws IllegalArgumentException {
        if (obj == null) {
            String error = "Found the object '" + arg + "' to be null.";
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
    public static String getAbrevClassName(Object obj) {
        if (obj == null) {
            return null;
        }

        String full_name = obj.getClass().getName();
        int    index = full_name.lastIndexOf('.');

        if (index >= 0) {
            full_name = full_name.substring(index + 1);
        }

        return full_name;
    }

    /**
     * Return a copy of a given List.
     * Currently, an ArrayList-based implementation is used for the copy.
     * @param to_copy the List to copy.
     * @return a shallow copy of <EM>to_copy</EM>.
     * If <EM>to_copy</EM> is null, return null.
     */
    static public List copyList(List<Object> to_copy) {
        if (to_copy == null) {
            return null;
        }

        List<Object> copy = new ArrayList<Object>(to_copy.size());
        copy.addAll(to_copy);

        return (copy);
    }

    /**
     * Check if two string fields are equal to each other.
     * @param field1 first string to compare. May be null.
     * @param field2 second string to compare. May be null.
     * @param case_sensitive true if we are to match in
     *        a case sensitive manner, false otherwise.
     * @return true if the two fields are null
     * or have the same characters. Return false otherwise.
     */
    static public boolean stringEqual(String field1, String field2,
                                      boolean case_sensitive) {
        if (field1 != field2) {
            if (field1 != null) {
                if (case_sensitive) {
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
     * @param match_char the character to match for finding a suffix.
     *                            all characters to the right of the rightmost
     *                            <EM>match_char</EM> make up the suffix.
     * @return null if no match character is found or if the
     *                           match character is at the end of the string,
     *                           or if <EM>str</EM> is null.
     *                           Otherwise the String suffix is returned.
     */
    static public String getSuffix(String str, char match_char) {
        String suffix = null;

        if (str != null) {
            int i = str.lastIndexOf(match_char);

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
     * @param dir_path the path to a given directory.
     * If <EM>dir_path</EM> doesn't exist, it is created.
     * @return true if the directory already exists, or is
     * successfully created.
     * Returns false otherwise.
     *
     */
    public static boolean ensurePathCreated(String dir_path) {
        File    f      = new File(dir_path);
        boolean exists = f.isDirectory();

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
    public static CyNode getNode(String uuid) {
        // return Cytoscape.getCyNode (uuid, true);
        //        log ("getNode: " + uuid + " exists = " +
        //             (_root_graph.getNode (uuid) != null));
        // CyNode node = (CyNode) slowGetNode (uuid);
        // MLC 08/17/06 BEGIN:
        return Cytoscape.getCyNode(uuid, true);

        //        CyNode node = Cytoscape.getRootGraph().getNode(uuid);
        //        if (node == null) {
        //            // BEGIN PATCH:
        //            // Magic from Cytoscape.java:
        //            node = (CyNode) Cytoscape.getRootGraph()
        //                                     .getNode(Cytoscape.getRootGraph()
        //                                                       .createNode());
        //            node.setIdentifier(uuid);
        //            //            Cytoscape.getNodeNetworkData ().addNameMapping (uuid, node);
        //            Semantics.assignNodeAliases(node, null, null);
        //
        //            // END PATH.
        //        }
        //        return node;
    }

    //    public static void removeNode(CyNode node) {
    //        //        log ("removeNode: " + node.getIdentifier ());
    //        _root_graph.removeNode(node);
    //    }

    //    public static void removeEdge(CyEdge edge) {
    //        //        log ("removeEdge: " + edge.getIdentifier ());
    //        _root_graph.removeEdge(edge);
    //    }

    //    // TODO: To be replaced with call to CytoscapeRootGraph.getEdge() when
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
    //    // TODO: To be replaced with call to CytoscapeRootGraph.getNode() when
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

    public static String getEdgeInteractionType(CyEdge edge) {
        return Cytoscape.getEdgeAttributes().getStringAttribute(edge.getIdentifier(),
                                              Semantics.INTERACTION);
    }

    public static CyEdge createHEEdge(CyNode source, CyNode target,
                                      String edgeIType) {
        CyEdge edge = createEdge(source, target, edgeIType);
        Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),
                                                   HyperEdgeImpl.HYPEREDGE_EDGE_TAG_NAME,
                                                   "true");

        return edge;
    }

    public static CyEdge createEdge(CyNode source, CyNode target,
                                    String edgeIType) {
        return Cytoscape.getCyEdge(source, target, Semantics.INTERACTION,
                                   edgeIType, true, true);
    }

    public static CyNode createConnectorNode(String uuid) {
        CyNode retNode = getNode(uuid);

        // TODO: Fix hack for removing canonical name on connectors:
        // &&& HACK getNode() sets the CANONICAL_NAME to the node id,
        //     which we don't  want for connector nodes:
        CyAttributes nodeAddrs = Cytoscape.getNodeAttributes();
	// MLC 06/21/07:
        // nodeAddrs.deleteAttribute(uuid, HyperEdgeImpl.LABEL_ATTRIBUTE_NAME);
	// MLC 06/21/07:
        deleteAttribute (nodeAddrs, uuid, HyperEdgeImpl.LABEL_ATTRIBUTE_NAME);
        nodeAddrs.setAttribute(uuid,
                               HyperEdgeImpl.IS_CONNECTOR_NODE_ATTRIBUTE_NAME,
                               "true");
        nodeAddrs.setAttribute(uuid, HyperEdgeImpl.ENTITY_TYPE_ATTRIBUTE_NAME,
                               HyperEdgeImpl.ENTITY_TYPE_CONNECTOR_NODE_VALUE);

        return retNode;
    }


    // delete's the given attribute from the given identifier from the
    // given CyAttributes. This is temporary method until
    // CyAttributes.deleteAttribute() if fixed to not throw an
    // IllegalStateException when the attribute to delete doesn't
    // exist (to be fixed in Cytoscape 2.6).

    public static boolean deleteAttribute (CyAttributes attrs,
				    String uuid , String attrName) {
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
     * For example, here’s the Java API description of unmodifiableSet():
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
     *    Iterator<CyEdge> edgeIt = he.getEdges (null);
     *    he.addEdge (“A”, EdgeTypeMap.PRODUCT);
     *    while (edgeIt.hasNext()) {
     *       CyEdge edge = edgeIt.next(); // throws ConcurrentModificationException
     *    }
     * </PRE>
     */
    static public <E> Iterator<E> buildUnmodifiableCollectionIterator(Collection<E> colVal) {
        return Collections.unmodifiableCollection(colVal).iterator();
    }

    /**
     * Create a new, safely modifiable Collection from a given Iterator.
     * This is also useful in rare cases where we must use a collection
     * of items independent of an unmodifiable collection iterator returned
     * because the underlying collection is modified and the iterator
     * would get a ConcurrentModificationException.
     */
    static public <E> Collection<E> createCollection(Iterator<E> colIt) {
        Collection<E> retCol = new ArrayList<E>();

        while (colIt.hasNext()) {
            retCol.add(colIt.next());
        }

        return retCol;
    }

    /**
     * Return a short, meaningful representation of a given object if it
     * is a CyEdge, CyNode, CyNetwork, or HyperEdge. Otherwise, just use
     * the object's toString() method.
     */
    static public String toString(Object obj) {
        StringBuilder sb = new StringBuilder();

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
            HyperEdge he    = (HyperEdge) obj;
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

    private static void primToString(String label, Object obj, StringBuilder sb) {
        sb.append('[');
        sb.append(obj.getClass().getSimpleName());
        sb.append(" ");
        sb.append(label);
        sb.append(']');
    }

    /**
     * Ignore attributes found in a given List. For example:
     * <PRE>
     * List<String> attrsToIgnore = new ArrayList<String>();
     * attrsToIgnore.add ("canonicalName");
     * attrsToIgnore.add ("label");
     * AttributeIgnoreFilter filter = new CyAttributesUtils.AttributeIgnoreFilter (attrsToIgnore);
     * CyAttributesUtils.copyAttributes (edge1, edge1Copy, filter, attrs, false);
     * </PRE>
     * would copy all attributes except for "canonicalName" and
     * "label", from edge1 to edge1Copy..
     */
    public static class AttributeIgnoreFilter implements AttributeFilter {
        private List<String> _attrNames;

        public AttributeIgnoreFilter(List<String> attrNames) {
            _attrNames = attrNames;
        }

        public boolean includeAttribute(CyAttributes attr, String objID,
                                        String attrName) {
            return (!_attrNames.contains(attrName));
        }
    }
}
