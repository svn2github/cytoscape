/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package chemViz.model;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import giny.model.GraphObject;
import giny.view.EdgeView;
import giny.view.NodeView;

import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.CyAttributes;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import chemViz.model.Compound;
import chemViz.model.Compound.DescriptorType;

public class CompoundColumn {

	public enum ColumnType { ATTRIBUTE, DESCRIPTOR };

	private ColumnType columnType;
	private String attributeName;
	private String objectType;
	private byte attributeType;
	private DescriptorType descriptor;
	private int columnWidth;
	private boolean hasNodes;
	private boolean hasEdges;

	public CompoundColumn(DescriptorType descriptor, int width) {
		this.columnType = ColumnType.DESCRIPTOR;
		this.descriptor = descriptor;
		this.columnWidth = width;
	}

	public CompoundColumn(String attributeName, String objectType, byte type, int width) {
		this.columnType = ColumnType.ATTRIBUTE;
		this.attributeName = attributeName;
		this.attributeType = type;
		this.objectType = objectType;
		this.columnWidth = width;
	}

	public CompoundColumn(String attributeString) throws RuntimeException {
		String [] words = attributeString.split("[:,]");
		if (words[0].equals("DESCRIPTOR")) {
			this.columnType = ColumnType.DESCRIPTOR;
			this.descriptor = null;
			if (words.length != 3)
				throw new RuntimeException("Illegal column specification: "+attributeString);
			List<DescriptorType> descriptorList = Compound.getDescriptorList();
			for (DescriptorType type: descriptorList) {
				if (words[1].equals(type.toString())) {
					this.descriptor = type;
					break;
				}
			}
			if (this.descriptor == null) 
				throw new RuntimeException("Unknown descriptor: "+words[1]);
			columnWidth = Integer.parseInt(words[2]);
		} else if (words[0].equals("ATTRIBUTE")) {
			if (words.length != 5)
				throw new RuntimeException("Illegal column specification: "+attributeString);
			columnType = ColumnType.ATTRIBUTE;
			attributeName = words[1];
			objectType = words[2];
			attributeType = CyAttributesUtils.toByte(words[3]);
			columnWidth = Integer.parseInt(words[4]);
		} else {
			throw new RuntimeException("Illegal column specification: "+attributeString);
		}
	}

	public int getWidth() { 
		if (columnWidth == -1)
			return 100;
		return columnWidth; 
	}

	public void setWidth(int width) {
		columnWidth = width;
	}

	public boolean hasNodes() {
		return this.hasNodes;
	}

	public boolean hasEdges() {
		return this.hasEdges;
	}

	public String toString() {
		if (columnType == ColumnType.DESCRIPTOR) {
			return "DESCRIPTOR:"+descriptor.toString()+","+columnWidth;
		} else {
			return "ATTRIBUTE:"+attributeName+","+objectType+","+CyAttributesUtils.toString(attributeType)+","+columnWidth;
		}
	}

	public Object getValue(Compound cmpd) {
		// Get the GraphObject so we can note whether we have nodes
		GraphObject obj = cmpd.getSource();
		if (obj instanceof CyNode) {
			this.hasNodes = true;
		} else {
			this.hasEdges = true;
		}

		if (columnType == ColumnType.ATTRIBUTE) {
			CyAttributes attributes;

			// Special case for "ID"
			if (attributeName.equals("ID"))
				return obj.getIdentifier();

			// Get the appropriate attribute
			if (obj instanceof CyNode) {
				if (objectType.equals("edge."))
					return null;
				attributes = Cytoscape.getNodeAttributes();
			} else {
				if (objectType.equals("node."))
					return null;
				attributes = Cytoscape.getEdgeAttributes();
			}
			// Return the value
			switch (attributeType) {
				case CyAttributes.TYPE_BOOLEAN:
					return attributes.getBooleanAttribute(obj.getIdentifier(), attributeName);
				case CyAttributes.TYPE_FLOATING:
					return attributes.getDoubleAttribute(obj.getIdentifier(), attributeName);
				case CyAttributes.TYPE_INTEGER:
					return attributes.getIntegerAttribute(obj.getIdentifier(), attributeName);
				case CyAttributes.TYPE_SIMPLE_LIST:
					List result = attributes.getListAttribute(obj.getIdentifier(), attributeName);
					String retValue = "[";
					for (int index = 0; index < result.size(); index++) {
						if (index > 0) retValue += ", ";
						retValue += result.get(index).toString();
					}
					retValue += "]";
					return retValue;
				case CyAttributes.TYPE_STRING:
					return attributes.getStringAttribute(obj.getIdentifier(), attributeName);
				default:
					return null;
			}
		} else if (columnType == ColumnType.DESCRIPTOR) {
			// Hand it off
			return cmpd.getDescriptor(descriptor);
		}
		return null;
	}

	public Class getColumnClass() {
		if (columnType == ColumnType.DESCRIPTOR)
			return descriptor.getClassType();

		switch (attributeType) {
			case CyAttributes.TYPE_BOOLEAN:
				return Boolean.class;
			case CyAttributes.TYPE_FLOATING:
				return Double.class;
			case CyAttributes.TYPE_INTEGER:
				return Integer.class;
			case CyAttributes.TYPE_SIMPLE_LIST:
			case CyAttributes.TYPE_STRING:
			default:
				return String.class;
		}
	}

	public String getColumnName() {
		if (columnType == ColumnType.DESCRIPTOR)
			return descriptor.toString();
		return attributeName;
	}

	public ColumnType getColumnType() { return columnType; }
	public DescriptorType getDescriptor() { return descriptor; }

	public void output(FileWriter writer, Compound compound) throws IOException {
		Object obj = getValue(compound);
		if (obj != null) {
			// We don't handle the images, yet
			if (obj instanceof Compound) 
				writer.write("[2D Image]");
			else
				writer.write(obj.toString());
		}
		return;
	}
}
