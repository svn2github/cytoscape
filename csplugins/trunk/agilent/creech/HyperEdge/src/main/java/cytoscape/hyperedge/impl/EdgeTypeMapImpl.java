
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
* Fri Aug 11 18:40:13 2006 (Michael L. Creech) creech@w235krbza760
*  Removed Identifiable ability.
* Sat Jul 29 13:57:27 2006 (Michael L. Creech) creech@w235krbza760
*   Added ACTIVATING_MEDIATOR & INHIBITING_MEDIATOR.
* Tue Oct 04 05:57:09 2005 (Michael L. Creech) creech@Dill
*    Added iterator() & toString().
********************************************************************************
*/
package cytoscape.hyperedge.impl;


import cytoscape.hyperedge.EdgeTypeMap;
import cytoscape.hyperedge.impl.utils.HEUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Implementation of the EdgeTypeMap interface.
 * <P>This map is used to define the role of Nodes used in
 * creating a HyperEdge and the role of Nodes added to a
 * HyperEdge. This role will be based on the edge interaction type
 * of the edge to create that contains that Node.
 *
 * @author Michael L. Creech
 * @version 1.0
 */
public final class EdgeTypeMapImpl implements EdgeTypeMap {
    //    /**
    //     * Potentially used by save() and load() for reading and writing EdgeTypeMap:
    //     */
    //    public static final String       PERSISTENT_MAP_ATTRIBUTE_NAME = "Map";
    private static final EdgeTypeMap INSTANCE = new EdgeTypeMapImpl();
    private Map<String, EdgeRole>    map = new HashMap<String, EdgeRole>();

    //    private String                 _uuid;
    //    private transient ListenerList _dirty_listener_store;
    //    private transient boolean      _dirty;
    private EdgeTypeMapImpl() {
        super();
        // this(null);
        setUpInitialMap();

        // primSetDirty(false, false);
    }
    /**
     * Gets the singleton edge type map.
     * @return the EdgeTypeMap.
     */

    protected static EdgeTypeMap getEdgeTypeMap() {
        return INSTANCE;
    }

    private void setUpInitialMap() {
        map.put(ACTIVATING_MEDIATOR, EdgeRole.SOURCE);
        map.put(INHIBITING_MEDIATOR, EdgeRole.SOURCE);
        map.put(SUBSTRATE, EdgeRole.SOURCE);
        map.put(PRODUCT, EdgeRole.TARGET);
    }

    // implements EdgeTypeMap interface:
    /**
     * {@inheritDoc}
     */
    public boolean addAll(final Map<String,EdgeRole> edgeTypeToEdgeRoleMap) {
        if (edgeTypeToEdgeRoleMap == null) {
            return false;
        }

        boolean   retVal = false;
        Map.Entry<String,EdgeRole> entry;
        EdgeRole  er;
        EdgeRole  oldEr;
        final Iterator<Map.Entry<String,EdgeRole>>  it = edgeTypeToEdgeRoleMap.entrySet().iterator();

        while (it.hasNext()) {
            entry  = it.next();
            er     = entry.getValue();
            oldEr = (EdgeRole) put(entry.getKey(),
                                    entry.getValue());

            if (er != oldEr) {
                retVal = true;
            }
        }

        return retVal;
    }

    // implements EdgeTypeMap interface:
    /**
     * {@inheritDoc}
     */
    public EdgeRole put(final String edgeIType, final EdgeRole sourceOrTarget) {
        if ((edgeIType == null) || (sourceOrTarget == null)) {
            HEUtils.throwIllegalArgumentException(
                "The 'edgeIType' parameter and 'sourceOrTarget' parameter must be non-null");
        }

        if (get(edgeIType) == sourceOrTarget) {
            return sourceOrTarget;
        }

        return primPut(edgeIType, sourceOrTarget, true);
    }

    private EdgeRole primPut(final String edgeIType, final EdgeRole sourceOrTarget,
                            final boolean fireEvents) {
        final EdgeRole er = (EdgeRole) map.put(edgeIType, sourceOrTarget);

        // primSetDirty (true, fireEvents);
        return er;
    }

    // implements EdgeTypeMap interface:
    /**
     * {@inheritDoc}
     */
    public EdgeRole get(final String edgeIType) {
        return (EdgeRole) map.get(edgeIType);
    }

    // implements EdgeTypeMap interface:
    /**
     * {@inheritDoc}
     */
    public EdgeRole remove(final String edgeIType) {
        final EdgeRole er = (EdgeRole) map.remove(edgeIType);

        //        if (er != null) {
        //            primSetDirty(true, true);
        //        }
        return er;
    }

    // implements EdgeTypeMap interface:
    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    // implements EdgeTypeMap interface:
    /**
     * {@inheritDoc}
     */
    public void clear() {
        primClear(true);
    }

    // implements EdgeTypeMap interface:
    /**
     * {@inheritDoc}
     */
    public void reset() {
        primClear(true);
        setUpInitialMap();
    }

    // implements EdgeTypeMap interface:
    /**
     * {@inheritDoc}
     */
    public int size() {
        return map.size();
    }

    // implements EdgeTypeMap interface:
    /**
     * {@inheritDoc}
     */
    public Iterator<Map.Entry<String,EdgeRole>> iterator() {
        return Collections.unmodifiableSet(map.entrySet()).iterator();
    }

    // implements EdgeTypeMap interface:
    /**
     * {@inheritDoc}
     */
    public String toString() {
        final StringBuffer result = new StringBuffer();
        result.append("[" + HEUtils.getAbrevClassName(this) + '.' + hashCode());

        //        result.append (" UUID: '" + getIdentifier () + "'");
        final Iterator<Map.Entry<String,EdgeRole>> it = iterator();

        if (it.hasNext()) {
            result.append('\n');
        } else {
            result.append("Is empty.");
        }

        Map.Entry<String,EdgeRole> entry;

        while (it.hasNext()) {
            entry = it.next();
            result.append("key: '");
            result.append(entry.getKey());
            result.append("' value: ");
            result.append(entry.getValue());

            if (it.hasNext()) {
                result.append('\n');
            }
        }

        result.append(']');

        return result.toString();
    }

    /**
     * Not a user operation. Used by persistence.
     */

    // public void primClear(boolean fireEvents) {
    private void primClear(final boolean fireEvents) {
        map.clear();

        // primSetDirty (true, fireEvents);
    }

    //    // implements Identifiable interface:
    //    public String getIdentifier ()
    //    {
    //        return _uuid;
    //    }
    //
    //    /**
    //     * This constructor is used for restoring persistent objects.
    //     * @param uuid the String uuid to assign this object. If null,
    //     * a UUID is generated for this object.
    //     */
    //    protected EdgeTypeMapImpl (String uuid)
    //    {
    //        // REMEMBER: Don't invoke overridable methods of this object
    //        //           in this constructor!
    //        super();
    //        if (uuid == null)
    //        {
    //            _uuid = HEUtils.generateUUID ();
    //            primSetDirty (true, false);
    //        }
    //        else
    //        {
    //            _uuid = uuid;
    //        }
    //    }
    //
    //    // implements EdgeTypeMap interface:
    //    public int load (String uri,
    //                     Format format)
    //    {
    //        if (format == Format.XML)
    //        {
    //            Document doc = HEXMLUtils.readDocument (uri, false);
    //            if (doc == null)
    //            {
    //                return 0;
    //            }
    //            return XMLPersist.parseEdgeTypeMap (doc);
    //        }
    //        else
    //        {
    //            String msg = "We currently can only load using Format.XML format.";
    //            HEUtils.errorLog (msg);
    //            return -1;
    //        }
    //    }
    //
    //    // implements EdgeTypeMap interface:
    //    // returns the number of EdgeTypeMap Entries saved.
    //    public int save (String uri_str,
    //                     Format format)
    //    {
    //        if (format == Format.XML)
    //        {
    //            PrintWriter pw = XMLPersist.openPrintWriter (uri_str);
    //            if (pw == null)
    //            {
    //                return -1;
    //            }
    //            int retVal = doXMLSaving (pw);
    //	    if (retVal >= 0)
    //		{
    //		    primSetDirty (false, true);
    //		}
    //            return retVal;
    //        }
    //        else
    //        {
    //            String msg = "We currently can only save using Format.XML format.";
    //            HEUtils.errorLog (msg);
    //            return -1;
    //        }
    //    }
    //
    //
    //    // implements Identifiable interface:
    //    public boolean addDirtyListener (DirtyListener l)
    //    {
    //        _dirty_listener_store = ListenerList.setupListenerListWhenNecessary (_dirty_listener_store);
    //        return _dirty_listener_store.addListener (l);
    //    }
    //
    //    // implements Identifiable interface:
    //    public boolean removeDirtyListener (DirtyListener l)
    //    {
    //        if (_dirty_listener_store != null)
    //        {
    //            return _dirty_listener_store.removeListener (l);
    //        }
    //        return false;
    //    }
    //
    //    // implements Identifiable interface:
    //    public boolean isDirty ()
    //    {
    //        return _dirty;
    //    }
    //
    //    // implements Identifiable interface:
    //    public boolean save (Writer w,
    //                         Object args,
    //                         Format format)
    //    {
    //        if (format == Format.XML)
    //        {
    //            return (doXMLSaving (w) >= 0);
    //        }
    //        return false;
    //    }
    //
    //    // implements Identifiable interface:
    //    public boolean load (Map values)
    //    {
    //        if (values == null)
    //        {
    //            HEUtils.throwIllegalArgumentException ("EdgeTypeMapImpl.load() called with a null values map!");
    //        }
    //
    //        Map edge_type_to_edge_role_map = (Map) values.get (PERSISTENT_MAP_ATTRIBUTE_NAME);
    //        addAll (edge_type_to_edge_role_map);
    //        return true;
    //    }
    //
    //    private int doXMLSaving (Writer pw)
    //    {
    //        String indent         = HEXMLUtils.INDENT_INCREMENT;
    //        String interface_name = HEXMLUtils.mapClassToInterfaceName (this);
    //        String bigger_indent  = indent + HEXMLUtils.INDENT_INCREMENT;
    //        HEXMLUtils.println (pw, "<" + interface_name + ">");
    //        HEXMLUtils.saveElement (pw, getIdentifier (), "UUID", "value", indent);
    //        Iterator  it            = _map.entrySet ().iterator ();
    //        String    key;
    //        EdgeRole  er;
    //        Map.Entry entry;
    //        int       num_entries   = 0;
    //        Map       attribute_map = new HashMap(5);
    //        while (it.hasNext ())
    //        {
    //            entry = (Map.Entry) it.next ();
    //            key   = (String) entry.getKey ();
    //            er    = (EdgeRole) entry.getValue ();
    //            // key may have funny XML characters in it:
    //            attribute_map.put ("key", HEXMLUtils.addAmpStrings (key));
    //            attribute_map.put ("value", er.getName ());
    //            HEXMLUtils.saveElement (pw, "EdgeEntry", attribute_map,
    //                                    bigger_indent, true);
    //            num_entries++;
    //        }
    //        HEXMLUtils.println (pw, "</" + interface_name + ">");
    //        if (HEXMLUtils.closeWriter (pw))
    //        {
    //            return num_entries;
    //        }
    //        return -1;
    //    }
    //
    //    /**
    //     * Keep package private to avoid allowing overriding:
    //     */
    //    boolean primSetDirty (boolean new_dirty,
    //                          boolean trigger_event)
    //    {
    //        if (_dirty == new_dirty)
    //        {
    //            return false;
    //        }
    //        _dirty = new_dirty;
    //        //        if (new_dirty)
    //        //        {
    //        //            _manager.setAnyDirty (true);
    //        //        }
    //        if (trigger_event)
    //        {
    //            fireDirtyEvent ();
    //        }
    //        return true;
    //    }
    //
    //    private void fireDirtyEvent ()
    //    {
    //        if ((_dirty_listener_store != null) &&
    //            (_dirty_listener_store.hasListeners ()))
    //        {
    //            // Now call all the listeners:
    //            Iterator it = _dirty_listener_store.iterator ();
    //            synchronized (_dirty_listener_store)
    //            {
    //                while (it.hasNext ())
    //                {
    //                    ((DirtyListener) it.next ()).dirtyStateChanged (this);
    //                }
    //            }
    //        }
    //    }
}
