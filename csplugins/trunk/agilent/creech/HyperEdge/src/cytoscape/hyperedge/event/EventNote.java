/* -*-Java-*-
********************************************************************************
*
* File:         EventNote.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/event/EventNote.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Sat Apr 23 06:31:25 2005
* Modified:     Mon Jul 11 11:51:59 2005 (Michael L. Creech) creech@Dill
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape.hyperedge.event;

import cytoscape.hyperedge.impl.utils.HEUtils;


/**
 * Houses information about an event notification.
 * Event notifications are triggered when an affected object is changed in
 * some way and listeners exist that are interested in monitoring changes to
 * this affected object.
 * @author Michael L. Creech
 * @version 1.0
 */
public class EventNote
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean        _has_supporting_info;
    private Object         _supporting_info;
    private EventNote.Type _event_type;
    private SubType        _event_sub_type;
    private Object         _affected_obj;

    //~ Constructors ///////////////////////////////////////////////////////////

    public EventNote (Object               affected,
                      final EventNote.Type event_type,
                      final SubType        event_sub_type,
                      Object               supporting_info)
    {
        _affected_obj   = affected;
        _event_type     = event_type;
        _event_sub_type = event_sub_type;
        setSupportingInfo (supporting_info);
    }

    public EventNote (Object               affected,
                      final EventNote.Type event_type,
                      final SubType        event_sub_type)
    {
        _affected_obj   = affected;
        _event_type     = event_type;
        _event_sub_type = event_sub_type;
    }

    public EventNote (Object               affected,
                      final EventNote.Type event_type)
    {
        _affected_obj   = affected;
        _event_type     = event_type;
        _event_sub_type = EventNote.SubType.NONE;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Return the object for which this change notification applies.
     */
    public Object getAffected ()
    {
        return _affected_obj;
    }

    public EventNote.Type getEventType ()
    {
        return _event_type;
    }

    public boolean isEventType (EventNote.Type event_type)
    {
        return (_event_type == event_type);
    }

    public EventNote.SubType getEventSubType ()
    {
        return _event_sub_type;
    }

    public boolean isEventSubType (EventNote.SubType sub_type)
    {
        return (_event_sub_type == sub_type);
    }

    public boolean hasSupportingInfo ()
    {
        return _has_supporting_info;
    }

    public Object getSupportingInfo ()
    {
        return _supporting_info;
    }

    public Object setSupportingInfo (Object sup_info)
    {
        Object ret_val = _supporting_info;
        _supporting_info     = sup_info;
        _has_supporting_info = true;
        return ret_val;
    }

    public String toString ()
    {
        StringBuffer result = new StringBuffer();
        result.append ("[" + HEUtils.getAbrevClassName (this) + '.' +
                       hashCode ());
        result.append (" type: '" + _event_type + "'");
        result.append (" sub-type: '" + _event_sub_type + "'");
        result.append (" affected: '" + _affected_obj + "'");
        if (_has_supporting_info)
        {
            result.append (" supporting-info: '" + _supporting_info + "'");
        }
        else
        {
            result.append (" supporting-info: NONE");
        }
        result.append (']');
        return result.toString ();
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////
    // class EventNote.Type
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * Specifies the types of Event Notification objects.
     */
    public static class Type
    {
        public static final Type NAME      = new Type("NAME");
        public static final Type TYPE      = new Type("TYPE");
        public static final Type ATTRIBUTE = new Type("ATTRIBUTE");
        public static final Type DIRECTED  = new Type("DIRECTED");
        public static final Type NODE      = new Type("NODE");
        public static final Type EDGE      = new Type("EDGE");
        public static final Type HYPEREDGE = new Type("HYPEREDGE");
        private String           _type;

        /**
         * Only this class can construct instances.
         */
        private Type (String type)
        {
            _type = type;
        }

        public String toString ()
        {
            return _type;
        }
    }

    /**
     * Specifies the sub-type of this type of event notification. For
     * example, if the Type is PROPERTY, the SubType might be DELETED,
     * to specify a deleted property.
     */
    public static class SubType
    {
        public static final SubType NONE    = new SubType("NONE");
        public static final SubType ADDED   = new SubType("ADDED");
        public static final SubType REMOVED = new SubType("REMOVED");
        public static final SubType SET     = new SubType("SET");

        /**
         * The associated supporting info is usually the old value
         * of the type specified. If the old value and new value are the
         * same, then this usually implies the changed occurred inside
         * the value (e.g., a property list HashMap value that changed).
         */
        public static final SubType CHANGED = new SubType("CHANGED");
        private String              _type;

        /**
         * Only this class can construct instances.
         */
        private SubType (String type)
        {
            _type = type;
        }

        public String toString ()
        {
            return _type;
        }
    }
}
