package de.mpg.mpi_inf.bioinf.netanalyzer.ui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * A class that extends the ListCellRenderer class to visualize a separator
 * line at a certain position between the JComboBoxItems.
 */
class ComboBoxRenderer extends JLabel implements ListCellRenderer {

	/**
	 * Default constructor for creation of a ComboBoxRenderer.
	 */
	public ComboBoxRenderer() {
		setOpaque(true);
		setBorder(new EmptyBorder(1, 1, 1, 1));
		separator = new JSeparator(SwingConstants.HORIZONTAL);
		fixedHeight = new JLabel(
				"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ(){}[]'\",;/?!@#$%^&*()_+")
				.getPreferredSize().height * 12 / 10;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListCellRenderer#getListCellRenderer
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		String str = (value == null) ? null : value.toString();
		if (Utils.SEPARATOR.equals(str)) {
			return separator;
		}
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setFont(list.getFont());
		setText(str);
		updateHeight();
		return this;
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 1977983924324671280L;

	private void updateHeight() {
		setPreferredSize(null);
		final Dimension size = getPreferredSize();
		size.height = fixedHeight;
		setPreferredSize(size);
	}

	/**
	 * A separator line in a JComboBox. 
	 */
	private JSeparator separator;

	/**
	 * A fixed height of the separator line.
	 */
	private int fixedHeight;		
}