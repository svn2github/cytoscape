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

package chemViz.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;

import chemViz.model.ChemInfoTableModel;
import chemViz.model.Compound.DescriptorType;
import chemViz.model.CompoundColumn;

public class TableAttributeHandler {

	private JTable table;
	private ChemInfoTableModel model;
	private CyNetwork network;
	public static int DEFAULT_IMAGE_SIZE=80;
	private static String tableAttribute = "_ChemVizTable";
	private static String tableWidthAttribute = "_ChemVizTableWidth";
	private static String tableHeightAttribute = "_ChemVizTableHeight";
	private	static CyLogger logger = CyLogger.getLogger(CompoundTable.class);

	public static List<CompoundColumn> getAttributes(CyNetwork network) {
		List<CompoundColumn> columns = new ArrayList();

		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		if (networkAttributes.hasAttribute(network.getIdentifier(), tableAttribute)) {
			// Get the attributes
			List<String> columnAttributes = networkAttributes.getListAttribute(network.getIdentifier(), tableAttribute);
			// Now iterate over the attribute list and create the columns
			for (String attr: columnAttributes) {
				try {
					columns.add(new CompoundColumn(attr));
				} catch (RuntimeException e) {
					logger.warning(e.getMessage(), e);
				}
			}
		} else {
			// No, create the default table map
			columns.add(new CompoundColumn("ID", "", CyAttributes.TYPE_STRING, -1));
			columns.add(new CompoundColumn(DescriptorType.ATTRIBUTE, -1));
			columns.add(new CompoundColumn(DescriptorType.IDENTIFIER, -1));
			columns.add(new CompoundColumn(DescriptorType.WEIGHT, -1));
			columns.add(new CompoundColumn(DescriptorType.IMAGE, DEFAULT_IMAGE_SIZE));
		}
		return columns;
	}

	public static void setTableAttributes(JTable table, ChemInfoTableModel model, CyNetwork network) {
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();

		TableColumnModel columnModel = table.getColumnModel();
		List<String> columnAttributes = new ArrayList();
		for (int column = 0; column < columnModel.getColumnCount(); column++) {
			TableColumn c = columnModel.getColumn(column);
			CompoundColumn cc = model.getColumnAt(column);
			// Update our width first
			cc.setWidth(c.getWidth());
			columnAttributes.add(cc.toString());
		}
		networkAttributes.setListAttribute(network.getIdentifier(), tableAttribute, columnAttributes);
		networkAttributes.setUserVisible(tableAttribute, false);
	}

	public static void setSizeAttributes(JDialog dialog, CyNetwork network) {
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();

		int height = dialog.getHeight();
		int width = dialog.getWidth();
		networkAttributes.setAttribute(network.getIdentifier(), tableWidthAttribute, new Integer(width));
		networkAttributes.setAttribute(network.getIdentifier(), tableHeightAttribute, new Integer(height));
		networkAttributes.setUserVisible(tableWidthAttribute, false);
		networkAttributes.setUserVisible(tableHeightAttribute, false);
	}

	public static int getWidthAttribute(CyNetwork network) {
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		if (networkAttributes.hasAttribute(network.getIdentifier(), tableWidthAttribute)) 
			return networkAttributes.getIntegerAttribute(network.getIdentifier(), tableWidthAttribute);
		return -1;
	}

	public static int getHeightAttribute(CyNetwork network) {
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		if (networkAttributes.hasAttribute(network.getIdentifier(), tableHeightAttribute)) 
			return networkAttributes.getIntegerAttribute(network.getIdentifier(), tableHeightAttribute);
		return -1;
	}
}
