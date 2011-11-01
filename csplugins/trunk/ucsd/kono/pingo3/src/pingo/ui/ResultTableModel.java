package pingo.ui;

import javax.swing.table.DefaultTableModel;

public class ResultTableModel extends DefaultTableModel {
	
	private static final long serialVersionUID = -2812874685703638609L;

	private static final Class<?>[] types = new Class[] { java.lang.Boolean.class,
		java.lang.String.class, java.lang.String.class };
	
	private static final String[] colNames = new String[] { "Select", "Term ID", "Name" };
	
	private static final boolean[] canEdit = new boolean[] { true, false, false };

	protected ResultTableModel(Object[][] data) {
		super(data, colNames);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return types[columnIndex];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return canEdit[columnIndex];
	}
}
