/* -*-Java-*-
********************************************************************************
*
* File:         EdgeTypeMapImpl.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/impl/EdgeTypeMapImpl.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Thu Sep 29 13:09:09 2005
* Modified:     Fri Aug 18 07:14:49 2006 (Michael L. Creech) creech@w235krbza760
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
public class EdgeTypeMapImpl implements EdgeTypeMap {
    //    /**
    //     * Potentially used by save() and load() for reading and writing EdgeTypeMap:
    //     */
    //    public static final String       PERSISTENT_MAP_ATTRIBUTE_NAME = "Map";
    private static final EdgeTypeMap INSTANCE = new EdgeTypeMapImpl();
    private Map<String, EdgeRole>    _map = new HashMap<String, EdgeRole>();

    //    private String                 _uuid;
    //    private transient ListenerList _dirty_listener_store;
    //    private transient boolean      _dirty;
    protected EdgeTypeMapImpl() {
        super();
        // this(null);
        setUpInitialMap();

        // primSetDirty(false, false);
    }

    protected static EdgeTypeMap getEdgeTypeMap() {
        return INSTANCE;
    }

    private void setUpInitialMap() {
        _map.put(ACTIVATING_MEDIATOR, EdgeRole.SOURCE);
        _map.put(INHIBITING_MEDIATOR, EdgeRole.SOURCE);
        _map.put(SUBSTRATE, EdgeRole.SOURCE);
        _map.put(PRODUCT, EdgeRole.TARGET);
    }

    // implements EdgeTypeMap interface:
    public boolean addAll(Map edge_type_to_edge_role_map) {
        if (edge_type_to_edge_role_map == null) {
            return false;
        }

        boolean   ret_val = false;
        Map.Entry entry;
        EdgeRole  er;
        EdgeRole  old_er;
        Iterator  it = edge_type_to_edge_role_map.entrySet().iterator();

        while (it.hasNext()) {
            entry  = (Map.Entry) it.next();
            er     = (EdgeRole) entry.getValue();
            old_er = (EdgeRole) put((String) entry.getKey(),
                                    (EdgeRole) entry.getValue());

            if (er != old_er) {
                ret_val = true;
            }
        }

        return ret_val;
    }

    // implements EdgeTypeMap interface:
    public EdgeRole put(String edgeIType, EdgeRole source_or_target) {
        if ((edgeIType == null) || (source_or_target == null)) {
            HEUtils.throwIllegalArgumentException(
                "The 'edgeIType' parameter and 'source_or_target' parameter must be non-null");
        }

        if (get(edgeIType) == source_or_target) {
            return source_or_target;
        }

        return primPut(edgeIType, source_or_target, true);
    }

    public EdgeRole primPut(String edgeIType, EdgeRole source_or_target,
                            boolean fire_events) {
        EdgeRole er = (EdgeRole) _map.put(edgeIType, source_or_target);

        // primSetDirty (true, fire_events);
        return er;
    }

    // implements EdgeTypeMap interface:
    public EdgeRole get(String edgeIType) {
        return (EdgeRole) _map.get(edgeIType);
    }

    // implements EdgeTypeMap interface:
    public EdgeRole remove(String edgeIType) {
        EdgeRole er = (EdgeRole) _map.remove(edgeIType);

        //        if (er != null) {
        //            primSetDirty(true, true);
        //        }
        return er;
    }

    // implements EdgeTypeMap interface:
    public boolean isEmpty() {
        return _map.isEmpty();
    }

    // implements EdgeTypeMap interface:
    public void clear() {
        primClear(true);
    }

    // implements EdgeTypeMap interface:
    public void reset() {
        primClear(true);
        setUpInitialMap();
    }

    // implements EdgeTypeMap interface:
    public int size() {
        return _map.size();
    }

    // implements EdgeTypeMap interface:
    public Iterator iterator() {
        return Collections.unmodifiableSet(_map.entrySet()).iterator();
    }

    // implements EdgeTypeMap interface:
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("[" + HEUtils.getAbrevClassName(this) + '.' + hashCode());

        //        result.append (" UUID: '" + getIdentifier () + "'");
        Iterator it = iterator();

        if (it.hasNext()) {
            result.append('\n');
        } else {
            result.append("Is empty.");
        }

        Map.Entry entry;

        while (it.hasNext()) {
            entry = (Map.Entry) it.next();
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

    // public void primClear(boolean fire_events) {
    private void primClear(boolean fire_events) {
        _map.clear();

        // primSetDirty (true, fire_events);
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
    //            int ret_val = doXMLSaving (pw);
    //	    if (ret_val >= 0)
    //		{
    //		    primSetDirty (false, true);
    //		}
    //            return ret_val;
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
