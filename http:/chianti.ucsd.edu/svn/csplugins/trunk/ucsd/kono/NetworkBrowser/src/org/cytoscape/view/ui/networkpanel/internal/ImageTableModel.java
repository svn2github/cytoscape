package org.cytoscape.view.ui.networkpanel.internal;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ImageTableModel extends AbstractTableModel {

	private final List<Image> dataModel;
	private final List<String> columnNames;

	public ImageTableModel() {
		dataModel = new ArrayList<Image>();
		columnNames  = new ArrayList<String>();
	}

	public void addImage(Image image, String name) {
		this.dataModel.add(image);
		this.columnNames.add(name);
	}

	public int getColumnCount() {
		return dataModel.size();
	}

	public int getRowCount() {
		// This is always 1. Actually, this is a horizontal list.
		return 1;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex > 1) {
			throw new IllegalArgumentException(
					"This data structure only accepts data for the 1st row.");
		}
		return dataModel.get(columnIndex);
	}

	@Override
	public void setValueAt(Object image, int rowIndex, int columnIndex) {
		if ((image instanceof Image) == false) {
			throw new IllegalArgumentException("Wrong data type.");
		}

		dataModel.set(columnIndex, (Image) image);
	}

	@Override
	public Class<?> getColumnClass(int idx) {
		return Image.class;
	}
	
	@Override
	public String getColumnName(int idx) {
		return columnNames.get(idx);
		
	}

}
