/* vim: set ts=2: */
/**
 * Copyright (c) 2008 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package metaNodePlugin2.model;

import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The AttributeHandler class has two major functions.  First, through the use
 * of static methods, it maintains and manages the map of AttributeHandler objects
 * for each attribute being managed.  Second, the AttributeHandler objects
 * themselves handle the aggregation of attributes.
 */
public class AttributeHandler {
	static private Map<String, AttributeHandler>handlerMap = null;
	static private Map<String, AttributeHandler>saveHandlerMap = null;
	static private boolean aggregating = false;

	private String attribute;
	private AttributeHandlingType type;
	private byte attributeType;
	private Object aggregateValue;

	/**
 	 * The AttributeHandlingType enum contains the list of all of the
 	 * different ways that attributes can be aggregated. Note that there
 	 * is no explicit mapping from attribute type to allowable attribute
 	 * aggregation type.  That mapping is contained within the various
 	 * AttributeHandlingType arrays defined below.
 	 */
	public enum AttributeHandlingType {
		NONE("None"),
		CSV("Comma-separated Values"),
		TSV("Tab-separated Values"),
		MCV("Most Common Value"),
		SUM("Sum"),
		AVG("Average"),
		MIN("Minimum value"),
		MAX("Maximum value"),
		MEDIAN("Median value"),
		CONCAT("Concatenate"),
		AND("Logical AND"),
		OR("Logical OR");

		private String name;
		private AttributeHandlingType(String s) { name = s; }
		public String toString() { return name; }
	}

	static AttributeHandlingType[] stringArray = {AttributeHandlingType.NONE, AttributeHandlingType.CSV, 
	                                              AttributeHandlingType.TSV, AttributeHandlingType.MCV};

	static AttributeHandlingType[] intArray = {AttributeHandlingType.NONE, AttributeHandlingType.AVG, 
	                                           AttributeHandlingType.SUM, AttributeHandlingType.MIN, 
	                                           AttributeHandlingType.MAX, AttributeHandlingType.MEDIAN}; 

	static AttributeHandlingType[] doubleArray = {AttributeHandlingType.NONE, AttributeHandlingType.AVG, 
	                                              AttributeHandlingType.SUM, AttributeHandlingType.MIN, 
	                                              AttributeHandlingType.MAX, AttributeHandlingType.MEDIAN}; 

	static AttributeHandlingType[] listArray = {AttributeHandlingType.NONE, AttributeHandlingType.CONCAT};

	static AttributeHandlingType[] booleanArray = {AttributeHandlingType.NONE, AttributeHandlingType.AND, 
	                                               AttributeHandlingType.OR};

	static AttributeHandlingType[] emptyArray = {AttributeHandlingType.NONE};

	/**************************************************************************
	 * Static (Class) methods for AttributeHandler                            *
	 *************************************************************************/

	/**
 	 * Return the allowable AttributeHandlingTypes for TYPE_STRING attributes
 	 *
 	 * @return array of AttributeHandlingType values
 	 */
	static public AttributeHandlingType[] getStringOptions() { return stringArray; }

	/**
 	 * Return the allowable AttributeHandlingTypes for TYPE_INTEGER attributes
 	 *
 	 * @return array of AttributeHandlingType values
 	 */
	static public AttributeHandlingType[] getIntOptions() { return intArray; }

	/**
 	 * Return the allowable AttributeHandlingTypes for TYPE_FLOATING attributes
 	 *
 	 * @return array of AttributeHandlingType values
 	 */
	static public AttributeHandlingType[] getDoubleOptions() { return doubleArray; }

	/**
 	 * Return the allowable AttributeHandlingTypes for TYPE_LIST attributes
 	 *
 	 * @return array of AttributeHandlingType values
 	 */
	static public AttributeHandlingType[] getListOptions() { return listArray; }

	/**
 	 * Return the allowable AttributeHandlingTypes for TYPE_BOOLEAN attributes
 	 *
 	 * @return array of AttributeHandlingType values
 	 */
	static public AttributeHandlingType[] getBooleanOptions() { return booleanArray; }

	/**
 	 * Return the allowable AttributeHandlingTypes for the attribute type
 	 *
 	 * @param type CyAttribute type
 	 * @return array of AttributeHandlingType values for that type
 	 */
	static public AttributeHandlingType[] getHandlingOptions(byte type) {
		switch(type) {
			case CyAttributes.TYPE_BOOLEAN:
				return getBooleanOptions();
			case CyAttributes.TYPE_INTEGER:
				return getIntOptions();
			case CyAttributes.TYPE_FLOATING:
				return getDoubleOptions();
			case CyAttributes.TYPE_STRING:
				return getStringOptions();
			case CyAttributes.TYPE_SIMPLE_LIST:
				return getListOptions();
			default:
				return emptyArray;
		}
	}

	/**
 	 * Add a new handler to our internal map for aggregating the designated
 	 * attribute and handlerType
 	 *
 	 * @param attribute the attribute this handler is for
 	 * @param handlerType the aggregation method for use by the handler
 	 */
	static public void addHandler(String attribute, AttributeHandlingType handlerType) {
						if (handlerMap == null) handlerMap = new HashMap();

		if (handlerMap.containsKey(attribute)) {
			handlerMap.get(attribute).setHandlerType(handlerType);
		} else {
			handlerMap.put(attribute, new AttributeHandler(attribute, handlerType));
		}
	}

	/**
 	 * Save the current attribute map
 	 */
	static public void saveSettings() {
		if (handlerMap == null) return;
		saveHandlerMap = new HashMap();
		for (String attribute: handlerMap.keySet()) {
			AttributeHandler handler = handlerMap.get(attribute);
			saveHandlerMap.put(attribute, new AttributeHandler(handler.getAttribute(),handler.getHandlerType()));
		}
	}

	/**
 	 * Revert the attribute map back to the saved settings.
 	 */
	static public void revertSettings() {
		handlerMap = saveHandlerMap;
		saveHandlerMap = null;
	}

	/**
 	 * Clear the attribute handler map
 	 */
	static public void clearSettings() {
		handlerMap = null;
		saveHandlerMap = null;
	}

	/**
 	 * Return the attribute handler for a specific attribute
 	 *
 	 * @param attribute the attribute to get the handler for
 	 * @return the AttributeHandler or null if there is no attribute handler
 	 *         for this attribute.
 	 */
	static public AttributeHandler getHandler(String attribute) {
		if (handlerMap == null) return null;
		if (handlerMap.containsKey(attribute))
			return handlerMap.get(attribute);
		return null;
	}

	static public Object aggregateAttribute(String source, String attribute, int count) {
		// Are we aggregating?
		if (!aggregating) return null;
		// Get the AttributeHandler
		// If there isn't one, create a default handler for this attribute
		// Aggregate
		return null;
	}

	static public Object assignAttribute(String destination, String attribute, int count) {
		// Are we aggregating?
		if (!aggregating) return null;
		// Get the AttributeHandler
		// Assign
		return null;
	}

	/**
 	 * Save the current mapping of attributes to attribute aggregation options into
 	 * the network attributes for this network.
 	 *
 	 * @param network the CyNetwork we're going to save our options into
 	 */
	static public void saveHandlerMappings(CyNetwork network) {
		//
	}

	/**
 	 * Load the current mapping of attributes to attribute aggregation options from
 	 * the network attributes for this network.
 	 *
 	 * @param network the CyNetwork we're going to read our options from
 	 */
	static public void loadHandlerMappings(CyNetwork network) {
		//
	}

	/**
 	 * Enable or disable attribute aggregation.
 	 *
 	 * @param enable if 'true' enable aggregation otherwise, disable aggregation
 	 */
	static public void setEnable(boolean enable) {
		aggregating = enable;
	}

	/**
 	 * Check to see if attribute aggregation is enabled
 	 *
 	 * @return 'true' if attribute aggregation is enabled otherwise, 'false'
 	 */
	static public boolean getEnable() {
		return aggregating;
	}

	/**************************************************************************
	 * Instance methods for AttributeHandler                                  *
	 *************************************************************************/

	protected AttributeHandler (String attribute, AttributeHandlingType type) {
		this.attribute = attribute;
		this.type = type;
	}

	public void setHandlerType(AttributeHandlingType type) {
		this.type = type;
	}

	public String getAttribute() {
		return attribute;
	}

	public AttributeHandlingType getHandlerType() {
		return type;
	}

	public Object aggregateAttribute(String source, int count) {
		return null;
	}

	public Object assignAttribute(String destination, int count) {
		return null;
	}
}
