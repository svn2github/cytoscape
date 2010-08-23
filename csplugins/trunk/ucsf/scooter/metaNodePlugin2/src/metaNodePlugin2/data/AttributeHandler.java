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
package metaNodePlugin2.data;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;

import metaNodePlugin2.model.MetaNode;
import metaNodePlugin2.model.MetaNodeManager;

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
	private String attribute;
	private AttributeHandlingType type;
	private byte attributeType;
	private Object aggregateValue;
	private int count;

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
