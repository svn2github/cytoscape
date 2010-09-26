package de.mpg.mpi_inf.bioinf.netanalyzer.ui;

import javax.swing.JTable;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.NetworkInterpretation;

/**
 * Cell renderer for instance of the {@link NetworkInterpretation} type.
 * <p>
 * This class is to be used for visualization applied network interpretations in a {@link JTable} Swing
 * control.
 * </p>
 * 
 * @author Yassen Assenov
 */
public class InterpretationCellRenderer extends DefaultCellRenderer {

	/**
	 * Initializes a new instance of <code>InterpretationCellRenderer</code>.
	 */
	public InterpretationCellRenderer() {
		// No specific initialization is required.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		final StringBuilder text = new StringBuilder();
		text.append("<html>");
		if (value != null) {
			final NetworkInterpretation interpr = (NetworkInterpretation) value;
			if (interpr.isDirected()) {
				text.append(Messages.NI_R_DIR);
				if (interpr.isIgnoreUSL()) {
					text.append(Messages.NI_R_DIRL);
				}
			} else {
				text.append(Messages.NI_R_UNDIR);
				if (interpr.isPaired()) {
					text.append(Messages.NI_R_UNDIRC);
				}
			}
		}
		setText(text.toString());
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = -4847632212033029150L;
}
