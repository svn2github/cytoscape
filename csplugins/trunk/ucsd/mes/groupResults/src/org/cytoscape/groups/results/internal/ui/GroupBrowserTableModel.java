package org.cytoscape.groups.results.internal.ui;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import cytoscape.groups.CyGroup;

public class GroupBrowserTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 5179387428151655809L;

	// Create column headings
	private static final String[] COLUMN_NAMES = { "Network", "Details" };

	private int groupCount = 0;

	public GroupBrowserTableModel() {
		this(new ArrayList<CyGroup>(), new ArrayList<Image>());
	}

	public GroupBrowserTableModel(List<CyGroup> groups, List<Image> imageList) {
		super(null, COLUMN_NAMES);
		groupCount = groups.size();

		for (int i = 0; i < groupCount; i++) {
			Object[] data = new Object[COLUMN_NAMES.length];
			data[1] = groups.get(i);
			data[0] = new ImageIcon(imageList.get(i));
			dataVector.add(data);
		}
	}

	public Class<?> getColumnClass(int col) {
		return getValueAt(0, col).getClass();
	}
}
