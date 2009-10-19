/* vim: set ts=2: */
/**
 * Copyright (c) 2009 The Regents of the University of California.
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
package coreCommands.commands;

import cytoscape.command.CyCommandResult;

import cytoscape.data.CyAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XXX FIXME XXX Description 
 */
public class AttributeUtils {

	public static String attributeToString(Object attribute, byte attributeType) {
		if (attributeType == CyAttributes.TYPE_SIMPLE_LIST) {
			List attrList = (List)attribute;
			String value = "[";
			for (Object v: attrList) {
				value+=v.toString()+",";
			}
			return value.substring(0, value.length()-1)+"]";
		} else if (attributeType == CyAttributes.TYPE_SIMPLE_MAP) {
			Map attrMap = (Map)attribute;
			String value = "{";
			for (Object key: attrMap.keySet()) {
				Object v = attrMap.get(key);
				value+=key.toString()+":"+v.toString()+",";
			}
			return value.substring(0, value.length()-1)+"}";
		} else if (attributeType == CyAttributes.TYPE_COMPLEX)
			return "{complex type}";
		else if (attributeType == CyAttributes.TYPE_UNDEFINED)
			return "{undefined type}";
		else
			return attribute.toString();
	}

	public static byte attributeStringToByte (String attributeType) {
		if (attributeType.equalsIgnoreCase("float")) return CyAttributes.TYPE_FLOATING;
		if (attributeType.equalsIgnoreCase("double")) return CyAttributes.TYPE_FLOATING;
		if (attributeType.equalsIgnoreCase("int")) return CyAttributes.TYPE_INTEGER;
		if (attributeType.equalsIgnoreCase("integer")) return CyAttributes.TYPE_INTEGER;
		if (attributeType.equalsIgnoreCase("string")) return CyAttributes.TYPE_STRING;
		if (attributeType.equalsIgnoreCase("boolean")) return CyAttributes.TYPE_BOOLEAN;
		if (attributeType.equalsIgnoreCase("list")) return CyAttributes.TYPE_SIMPLE_LIST;
		if (attributeType.equalsIgnoreCase("map")) return CyAttributes.TYPE_SIMPLE_MAP;
		return CyAttributes.TYPE_UNDEFINED;
	}

	public static String attributeByteToString (byte type) {
		if (type == CyAttributes.TYPE_FLOATING) return "float";
		if (type == CyAttributes.TYPE_INTEGER) return "int";
		if (type == CyAttributes.TYPE_STRING) return "string";
		if (type == CyAttributes.TYPE_BOOLEAN) return "boolean";
		if (type == CyAttributes.TYPE_SIMPLE_LIST) return "list";
		if (type == CyAttributes.TYPE_SIMPLE_MAP) return "map";
		return "undefined";
	}

	public static boolean setAttribute(CyCommandResult result, String command, 
	                                CyAttributes attributes, byte type, 
	                                String id, String attrName, String value) {
		try {
			if (type == CyAttributes.TYPE_STRING)
				attributes.setAttribute(id, attrName, value);
			else if (type == CyAttributes.TYPE_BOOLEAN)
				attributes.setAttribute(id, attrName, new Boolean(value));
			else if (type == CyAttributes.TYPE_FLOATING)
				attributes.setAttribute(id, attrName, new Double(value));
			else if (type == CyAttributes.TYPE_INTEGER)
				attributes.setAttribute(id, attrName, new Integer(value));
			else if (type == CyAttributes.TYPE_SIMPLE_LIST) {
				List attrList = stringToList(value);
				attributes.setListAttribute(id, attrName, attrList);
			} else if (type == CyAttributes.TYPE_SIMPLE_MAP) {
				Map attrMap = stringToMap(value);
				attributes.setMapAttribute(id, attrName, attrMap);
			}
		} catch (Exception e) {
			result.addError(command+": unable to convert '"+value+"' to type '"+attributeByteToString(type)+"'");
			return false;
		}
		return true;
	}

	public static List stringToList(String value) throws Exception {
		if (value == null || value.length() == 0)
			throw new Exception();

		if (value.startsWith("["))
			value = value.substring(1);

		if (value.endsWith("]"))
			value = value.substring(0, value.length()-1);

		String [] elements = value.split(",");
		List<String> list = new ArrayList();
		for (int index=0; index < elements.length; index++) {
			list.add(elements[index]);
		}
		return list;
	}

	public static Map stringToMap(String value) throws Exception {
		if (value == null || value.length() == 0)
			throw new Exception();

		if (value.startsWith("{") || value.startsWith("["))
			value = value.substring(1);

		if (value.endsWith("}") || value.endsWith("]"))
			value = value.substring(0, value.length()-1);

		String [] elements = value.split(",");
		Map<String, String> map = new HashMap();
		for (int index=0; index < elements.length; index++) {
			String[] pair = elements[index].split(":");
			map.put(pair[0], pair[1]);
		}
		return map;
	}
}
