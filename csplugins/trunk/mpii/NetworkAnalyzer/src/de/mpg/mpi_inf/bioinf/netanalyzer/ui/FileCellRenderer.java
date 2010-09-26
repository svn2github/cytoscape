package de.mpg.mpi_inf.bioinf.netanalyzer.ui;

import java.io.File;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Cell renderer for instance of the {@link File} type.
 * <p>
 * This class is to be used for visualization of files (file names) in the {@link JTable} Swing control.
 * </p>
 * 
 * @author Yassen Assenov
 */
class FileCellRenderer extends DefaultTableCellRenderer {

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 6855862789588816843L;

	/**
	 * Initializes a new instance of <code>FileCellRenderer</code>.
	 */
	public FileCellRenderer() {
		// No fields to initialize
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		String text = "";
		if (value != null) {
			final File f = (File) value;
			final String name = f.getName();
			text = "<html><a href=\"" + name + "\">" + name + "</a></html>";
			setToolTipText(f.getAbsolutePath());
		}
		setText(text);
	}
}
