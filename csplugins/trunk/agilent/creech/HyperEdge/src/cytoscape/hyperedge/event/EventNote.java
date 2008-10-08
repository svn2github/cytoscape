
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

package cytoscape.hyperedge.event;

import cytoscape.hyperedge.impl.utils.HEUtils;

/**
 * Houses information about an event notification. Event notifications are
 * triggered when an affected object is changed in some way and listeners exist
 * that are interested in monitoring changes to this affected object.
 *
 * @author Michael L. Creech
 * @version 1.0
 */
// TODO Should change the names of 'type' and 'sub-type' to be more
//       meaningful, such as 'what' was affected and 'how' it was affected.

public class EventNote {

    private static final String SINGLE_QUOTE = "'";

    // ~ Instance fields
    // ////////////////////////////////////////////////////////
    
    private boolean hasSupportingInfo;
    private Object supportingInfo;
    private EventNote.Type eventType;
    private SubType eventSubType;
    private Object affectedObj;

    // ~ Constructors
    // ///////////////////////////////////////////////////////////

    /**
     * EventNode constructor.
     * @param affected the Object this notification is about (usually a HyperEdge).
     * @param eventType what has changed in affected (e.g., Node)
     * @param eventSubType more details about what has changed (e.g., a Node was removed).
     * @param supportingInfo an object that gives more information about this event
     *        (e.g., such as what Network contained a removed HyperEdge).
     */

    public EventNote(final Object affected, final EventNote.Type eventType,
	    final SubType eventSubType, final Object supportingInfo) {
	affectedObj = affected;
	this.eventType = eventType;
	this.eventSubType = eventSubType;
	setSupportingInfo(supportingInfo);
    }

    /**
     * EventNode constructor.
     * @param affected the Object this notification is about (usually a HyperEdge).
     * @param eventType what has changed in affected (e.g., Node)
     * @param eventSubType more details about what has changed (e.g., a Node was removed).
     */
    public EventNote(final Object affected, final EventNote.Type eventType,
	    final SubType eventSubType) {
	affectedObj = affected;
	this.eventType = eventType;
	this.eventSubType = eventSubType;
    }

    /**
     * EventNode constructor.
     * @param affected the Object this notification is about (usually a HyperEdge).
     * @param eventType what has changed in affected (e.g., Node)
     */
    public EventNote(final Object affected, final EventNote.Type eventType) {
	affectedObj = affected;
	this.eventType = eventType;
	eventSubType = EventNote.SubType.NONE;
    }

    // ~ Methods
    // ////////////////////////////////////////////////////////////////

    /**
     * @return the object for which this change notification applies.
     */
    public Object getAffected() {
	return affectedObj;
    }

    /**
     * @return the primary event type--what thing was affected, such
     * as a Node, Edge, or Attribute.
     */

    public EventNote.Type getEventType() {
	return eventType;
    }

    /**
     * @param matchEventType the primary event type to match.
     * @return true iff matchEventType matches the primary event type of
     *              this EventNote.
     */
    public boolean isEventType(final EventNote.Type matchEventType) {
	return (eventType == matchEventType);
    }

    /**
     * @return the secondary event type--what happened, such
     * as a removal, addition, or setting.
     */

    public EventNote.SubType getEventSubType() {
	return eventSubType;
    }

    /**
     * @param matchSubType the secondary event type to match.
     * @return true iff matchSubType matches the secondary event type of
     *              this EventNote.
     */
    public boolean isEventSubType(final EventNote.SubType matchSubType) {
	return (eventSubType == matchSubType);
    }

    /**
     * @return true iff this notification has a supporting information object.
     */
    public boolean hasSupportingInfo() {
	return hasSupportingInfo;
    }

    /**
     * @return the supporting information object, or null if not defined.
     */
    public Object getSupportingInfo() {
	return supportingInfo;
    }

    /**
     * Sets the supporting information object of this notification.
     * @param newSupportingInfo the new supporting information object.
     * @return the previous supporting information object.
     */
    public Object setSupportingInfo(final Object newSupportingInfo) {
	final Object retVal = supportingInfo;
	supportingInfo = newSupportingInfo;
	hasSupportingInfo = true;
	return retVal;
    }

    /**
     * @return a human-readable representation of this EventNote.
     */
    public String toString() {
	final StringBuffer result = new StringBuffer();
	result.append("[" + HEUtils.getAbrevClassName(this) + '.' + hashCode());
	result.append(" type: '" + eventType + SINGLE_QUOTE);
	result.append(" sub-type: '" + eventSubType + SINGLE_QUOTE);
	result.append(" affected: '" + affectedObj + SINGLE_QUOTE);
	if (hasSupportingInfo) {
	    result.append(" supporting-info: '" + supportingInfo + SINGLE_QUOTE);
	} else {
	    result.append(" supporting-info: NONE");
	}
	result.append(']');
	return result.toString();
    }

    // ~ Inner Classes
    // //////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    // ///
    // class EventNote.Type
    ////////////////////////////////////////////////////////////////////////////
    // ///

    /**
     * Specifies what was affected.
     */
    public static final class Type {
	/**
	 * The name of the object (e.g., HyperEdge) was affected.
	 */
	public static final Type NAME = new Type("NAME");
	/**
	 * The name of the object (e.g., HyperEdge) was affected.
	 */
	public static final Type TYPE = new Type("TYPE");
	/**
	 * An attribute of the object (e.g., HyperEdge) was affected.
	 */
	public static final Type ATTRIBUTE = new Type("ATTRIBUTE");
	/**
	 * The directedness of the object (e.g., HyperEdge) was affected.
	 */
	public static final Type DIRECTED = new Type("DIRECTED");
	/**
	 * A Node associated with the object (e.g., HyperEdge) was affected.
	 */
	public static final Type NODE = new Type("NODE");
	/**
	 * An Edge associated with the object (e.g., HyperEdge) was affected.
	 */
	public static final Type EDGE = new Type("EDGE");
	/**
	 * A HyperEdge was affected (e.g., added).
	 */
	public static final Type HYPEREDGE = new Type("HYPEREDGE");
	private String type;

	/**
	 * Only this class can construct instances.
	 */
	private Type(final String newType) {
	    type = newType;
	}

	/**
	 * @return a human-readable representation of this Type.
	 */
	public String toString() {
	    return type;
	}
    }

    /**
     * Specifies details about this type of event notification. For example,
     * if the Type is NODe, the SubType might be DELETED, to specify a
     * node was deleted.
     */
    public static final class SubType {
	/**
	 * No subtype information.
	 */
	public static final SubType NONE = new SubType("NONE");
	/**
	 * The primary type was added (e.g., added an Edge).
	 */
	public static final SubType ADDED = new SubType("ADDED");
	/**
	 * The primary type was removed (e.g., removed a HyperEdge).
	 */
	public static final SubType REMOVED = new SubType("REMOVED");
	/**
	 * The primary type was set (e.g., set an attribute).
	 */
	public static final SubType SET = new SubType("SET");

	/**
	 * The associated supporting info is usually the old value of the type
	 * specified. If the old value and new value are the same, then this
	 * usually implies the changed occurred inside the value (e.g., a
	 * property list HashMap value that changed).
	 */
	public static final SubType CHANGED = new SubType("CHANGED");
	private String type;

	/**
	 * Only this class can construct instances.
	 */
	private SubType(final String newType) {
	    type = newType;
	}

	/**
	 * @return a human-readable representation of this SubType.
	 */
	public String toString() {
	    return type;
	}
    }
}
