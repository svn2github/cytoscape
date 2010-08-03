/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.dialogs;

import cytoscape.CyNetwork;

import cytoscape.data.readers.MetadataParser;

import java.net.URISyntaxException;

import java.util.Map;

import javax.swing.table.DefaultTableModel;


/**
 *
 * Table model for the Network Metadata Dialog.
 *
 * @version 1.0
 * @since 2.3
 * @see cytoscape.dialogs.NetworkMetaDataDialog
 * @author kono
 *
 */
public class NetworkMetaDataTableModel extends DefaultTableModel {
	private CyNetwork network;
	private Object[][] dataArray;
	private Map data;
	private String description;

	// Define Column names
	private static String[] columnHeader = new String[] { "Data Label", "Value" };

	// Define default entries. This determins the order in the table.
	private static String[] defaultEntries = {
	                                             "Title", "Identifier", "Source", "Type", "Format",
	                                             "Date"
	                                         };
	private MetadataParser mdp;

	/**
	 * Constructor for the network metadata table model.
	 *
	 * @param network
	 *                 Metadata for this network will be edited.
	 */
	public NetworkMetaDataTableModel(CyNetwork network) {
		super();
		this.network = network;
		description = null;
		mdp = new MetadataParser(this.network);
	}

	/**
	 * Set table data based on the Map object returned by the data
	 * parser.
	 *
	 * @throws URISyntaxException
	 */
	protected void setTable() throws URISyntaxException {
		// Always 2 columns --- Data label and value.
		Object[] column_names = new Object[2];
		column_names[0] = "Data Label";
		column_names[1] = "Value";

		data = mdp.getMetadataMap();
		description = (String) data.get("Description");
		dataArray = new Object[defaultEntries.length][2];

		// Order vector based on the labels
		for (int i = 0; i < defaultEntries.length; i++) {
			String key = defaultEntries[i];
			dataArray[i][0] = key;
			dataArray[i][1] = data.get(key);
		}

		setDataVector(dataArray, columnHeader);
	}

	/**
	 * Get Desctiption entry, which will not be included in the table.
	 *
	 * @return
	 *             Long string of description.
	 *
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnHeader.length;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getRowCount() {
		return defaultEntries.length;
	}

	/**
	 * Determine which cell is editible or not.
	 */
	public boolean isCellEditable(int row, int column) {
		if (column == 0) {
			// Do not allow to edit data names.
			return false;
		} else if (row == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param arg0 DOCUMENT ME!
	 * @param arg1 DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return dataArray[arg0][arg1];
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param obj DOCUMENT ME!
	 * @param row DOCUMENT ME!
	 * @param col DOCUMENT ME!
	 */
	public void setValueAt(Object obj, int row, int col) {
		dataArray[row][col] = obj;
		setDataVector(dataArray, columnHeader);
		fireTableCellUpdated(row, col);
	}
}
