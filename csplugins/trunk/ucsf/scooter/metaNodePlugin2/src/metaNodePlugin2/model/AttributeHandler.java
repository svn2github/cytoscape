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
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import java.util.ArrayList;
import java.util.Arrays;
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
	static private AttributeHandlingType[] defaultHandling = new AttributeHandlingType[15];
	static private boolean aggregating = false;
	static private CyNetwork network;
	static public String OVERRIDE_ATTRIBUTE = "__MetanodeAggregation";

	private String attribute;
	private AttributeHandlingType type;
	private byte attributeType;
	private Object aggregateValue;
	private int count;

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
		OR("Logical OR"),
		DEFAULT("(no override)");

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
		
		// Update our list of overrides in the network attributes
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		if (networkAttributes.hasAttribute(network.getIdentifier(), OVERRIDE_ATTRIBUTE)) {
			Map<String,String> attrMap = (Map<String,String>)networkAttributes.getMapAttribute(network.getIdentifier(), OVERRIDE_ATTRIBUTE);
			attrMap.put(attribute, handlerType.toString());
			networkAttributes.setMapAttribute(network.getIdentifier(), OVERRIDE_ATTRIBUTE, attrMap);
		}
	}

	/**
 	 * Remove a handler for the designated attribute from our internal map of handlers
 	 *
 	 * @param attribute the attribute this handler is for
 	 */
	static public void removeHandler(String attribute) {
		if (handlerMap != null && handlerMap.containsKey(attribute)) {
			handlerMap.remove(attribute);
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

	/**
 	 * Load the current mapping of attributes to attribute aggregation options from
 	 * the network attributes for this network.
 	 *
 	 * @param network the CyNetwork we're going to read our options from
 	 */
	static public void loadHandlerMappings(CyNetwork network) {
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		AttributeHandler.network = network;
		if (networkAttributes.hasAttribute(network.getIdentifier(), OVERRIDE_ATTRIBUTE)) {
			Map<String,String> attrMap = (Map<String,String>)networkAttributes.getMapAttribute(network.getIdentifier(), OVERRIDE_ATTRIBUTE);
			for (String attr: attrMap.keySet()) {
				handlerMap.put(attr, new AttributeHandler(attr, stringToType(attrMap.get(attr))));
			}
		}
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

	static public void setDefault(byte attributeType, AttributeHandlingType type) {
		if (attributeType < 0) attributeType += 10;
		defaultHandling[attributeType] = type;
	}

	static public AttributeHandler getDefaultHandler(byte attributeType, String attribute) {
		AttributeHandlingType t;
		if (attributeType < 0) attributeType += 10;
		t = defaultHandling[attributeType];
		if (t == null)
			return null;

		// OK, now add it in, but don't update our attributes
		if (handlerMap == null) handlerMap = new HashMap();
		AttributeHandler h = new AttributeHandler(attribute, t);
		handlerMap.put(attribute, h);
		return h;
	}


	static public AttributeHandlingType stringToType(String str) {
		for (AttributeHandlingType type: AttributeHandlingType.values()) {
			if (str.equals(type.toString()))
				return type;
		}
		return AttributeHandlingType.NONE;
	}

	/**************************************************************************
	 * Instance methods for AttributeHandler                                  *
	 *************************************************************************/

	protected AttributeHandler (String attribute, AttributeHandlingType type) {
		this.attribute = attribute.substring(5); // Skip over type
		this.type = type;
		this.count = 0;
		aggregateValue = null;
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

	public Object aggregateAttribute(CyAttributes attrMap, String source, int count) {
		byte attributeType = attrMap.getType(attribute);
		if (type == AttributeHandlingType.NONE)
			return null;

		if (!attrMap.hasAttribute(source,attribute))
			return aggregateValue;

		switch (attributeType) {
			case CyAttributes.TYPE_BOOLEAN:
				{
					boolean value = attrMap.getBooleanAttribute(source, attribute).booleanValue();
					if (aggregateValue == null)
						aggregateValue = Boolean.valueOf(value);
					else if (type == AttributeHandlingType.AND) {
						aggregateValue = Boolean.valueOf(((Boolean)aggregateValue).booleanValue() & value);
					} else if (type == AttributeHandlingType.OR) {
						aggregateValue = Boolean.valueOf(((Boolean)aggregateValue).booleanValue() | value);
					}
				}
				break;
			case CyAttributes.TYPE_INTEGER:
				{
					int value = attrMap.getIntegerAttribute(source, attribute).intValue();
					if (aggregateValue == null && type != AttributeHandlingType.MEDIAN) {
						aggregateValue = Integer.valueOf(value);
					} else if (type == AttributeHandlingType.MIN) {
						if (value < ((Integer)aggregateValue).intValue())
							aggregateValue = Integer.valueOf(value);
					} else if (type == AttributeHandlingType.MAX) {
						if (value > ((Integer)aggregateValue).intValue())
							aggregateValue = Integer.valueOf(value);
					} else if (type == AttributeHandlingType.AVG) {
						aggregateValue = Integer.valueOf(((Integer)aggregateValue).intValue() + value)*count;
					} else if (type == AttributeHandlingType.MEDIAN) {
						if (aggregateValue == null)
							aggregateValue = (List<Integer>)new ArrayList();
						((List<Integer>)aggregateValue).add(attrMap.getIntegerAttribute(source, attribute));
					} else if (type == AttributeHandlingType.SUM) {
						aggregateValue = Integer.valueOf(((Integer)aggregateValue).intValue() + value)*count;
					}
				}
				break;
			case CyAttributes.TYPE_FLOATING:
				{
					double value = attrMap.getDoubleAttribute(source, attribute).doubleValue();
					if (aggregateValue == null && type != AttributeHandlingType.MEDIAN) {
						aggregateValue = Double.valueOf(value);
					} else if (type == AttributeHandlingType.MIN) {
						if (value < ((Double)aggregateValue).doubleValue())
							aggregateValue = Double.valueOf(value);
					} else if (type == AttributeHandlingType.MAX) {
						if (value > ((Double)aggregateValue).doubleValue())
							aggregateValue = Double.valueOf(value);
					} else if (type == AttributeHandlingType.AVG) {
						aggregateValue = Double.valueOf(((Double)aggregateValue).doubleValue() + value)*count;
					} else if (type == AttributeHandlingType.MEDIAN) {
						if (aggregateValue == null)
							aggregateValue = (List<Double>)new ArrayList();
						((List<Double>)aggregateValue).add(attrMap.getDoubleAttribute(source, attribute));
					} else if (type == AttributeHandlingType.SUM) {
						aggregateValue = Double.valueOf(((Double)aggregateValue).doubleValue() + value)*count;
					}
				}
				break;
			case CyAttributes.TYPE_SIMPLE_LIST:
				{
					List value = attrMap.getListAttribute(source, attribute);
					if (aggregateValue == null) {
						aggregateValue = value;
					} else if (type == AttributeHandlingType.CONCAT) {
						((List)aggregateValue).addAll(value);
					}
				}
				break;
			case CyAttributes.TYPE_STRING:
				{
					String value = attrMap.getStringAttribute(source, attribute);
					if (aggregateValue == null && type != AttributeHandlingType.MCV) {
						aggregateValue = value;
					} else if (type == AttributeHandlingType.CSV) {
						aggregateValue = (String)aggregateValue + "," + value;
					} else if (type == AttributeHandlingType.TSV) {
						aggregateValue = (String)aggregateValue + "\t" + value;
					} else if (type == AttributeHandlingType.MCV) {
						if (aggregateValue == null)
							aggregateValue = (Map<String,Integer>)new HashMap();
						// TODO: What's the right way to handle blank values?
						Map<String,Integer>histo = (Map<String,Integer>)aggregateValue;
						if (histo.containsKey(value)) {
							histo.put(value, Integer.valueOf(histo.get(value).intValue()+1));
						} else {
							histo.put(value, Integer.valueOf(1));
						}
					}
				}
				break;
			default:
		}

		this.count += count;
		return aggregateValue;
	}

	public Object assignAttribute(CyAttributes attrMap, String destination) {
		byte attributeType = attrMap.getType(attribute);

		if (type == AttributeHandlingType.NONE)
			return null;

		if (aggregateValue == null) {
			attrMap.deleteAttribute(destination, attribute);
			return null;
		}

		switch (attributeType) {
			case CyAttributes.TYPE_BOOLEAN:
				attrMap.setAttribute(destination, attribute, (Boolean)aggregateValue);
				break;
			case CyAttributes.TYPE_INTEGER:
				if (aggregateValue != null && type == AttributeHandlingType.AVG)
					aggregateValue = Integer.valueOf(((Integer)aggregateValue).intValue() / this.count);
				else if (aggregateValue != null && type == AttributeHandlingType.MEDIAN) {
					List<Integer>vList = (List<Integer>)aggregateValue;
					Integer[] vArray = new Integer[vList.size()];
					vArray = vList.toArray(vArray);
					Arrays.sort(vArray);
					if (vArray.length % 2 == 1)
						aggregateValue = vArray[(vArray.length-1)/2];
					else {
						aggregateValue = (vArray[(vArray.length/2)-1] + vArray[(vArray.length/2)]) / 2;
					}
				}
				attrMap.setAttribute(destination, attribute, (Integer)aggregateValue);
				break;
			case CyAttributes.TYPE_FLOATING:
				if (aggregateValue != null && type == AttributeHandlingType.AVG)
					aggregateValue = Double.valueOf(((Double)aggregateValue).doubleValue() / (double)this.count);
				else if (aggregateValue != null && type == AttributeHandlingType.MEDIAN) {
					List<Double>vList = (List<Double>)aggregateValue;
					Double[] vArray = new Double[vList.size()];
					vArray = vList.toArray(vArray);
					Arrays.sort(vArray);
					if (vArray.length % 2 == 1)
						aggregateValue = vArray[(vArray.length-1)/2];
					else {
						aggregateValue = (vArray[(vArray.length/2)-1] + vArray[(vArray.length/2)]) / (double)2;
					}
				}
				attrMap.setAttribute(destination, attribute, (Double)aggregateValue);
				break;
			case CyAttributes.TYPE_SIMPLE_LIST:
				attrMap.setListAttribute(destination, attribute, (List)aggregateValue);
				break;
			case CyAttributes.TYPE_STRING:
				if (type == AttributeHandlingType.MCV) {
					int max = -1;
					String mcv = null;
					Map<String,Integer> histo = (Map<String,Integer>)aggregateValue;
					for (String str: histo.keySet()) {
						if (histo.get(str).intValue() > max) {
							mcv = str;
							max = histo.get(str).intValue();
						}
					}
					aggregateValue = mcv;
				}
				attrMap.setAttribute(destination, attribute, (String)aggregateValue);
				break;
			default:
		}

		aggregateValue = null;
		count = 0;
		return null;
	}
}
