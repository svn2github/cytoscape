package de.mpg.mpi_inf.bioinf.netanalyzer.ui.filter;

import java.awt.Dialog;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import de.mpg.mpi_inf.bioinf.netanalyzer.data.IntHistogram;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.filter.ComplexParamFilter;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.filter.IntHistogramFilter;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.settings.IntHistogramGroup;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.SpringUtilities;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.Utils;

/**
 * Dialog for creating {@link de.mpg.mpi_inf.bioinf.netanalyzer.data.filter.IntHistogramFilter} based on
 * user's input.
 * 
 * @author Yassen Assenov
 */
public class IntHistogramFilterDialog extends ComplexParamFilterDialog {

	/**
	 * Initializes a new instance of <code>IntHistogramFilterDialog</code> based on the given IntHistgoram
	 * instance.
	 * 
	 * @param aOwner The <code>Dialog</code> from which this dialog is displayed.
	 * @param aTitle Title of the dialog.
	 * @param aHistogram Histogram instance, based on which the ranges for the minimum and maximum degrees are
	 *            to be chosen.
	 * @param aSettings Visual settings for <code>aHistogram</code>.
	 */
	public IntHistogramFilterDialog(Dialog aOwner, String aTitle, IntHistogram aHistogram,
			IntHistogramGroup aSettings) {
		super(aOwner, aTitle);

		populate(aHistogram, aSettings);
		pack();
		setResizable(false);
		setLocationRelativeTo(aOwner);
	}

	/**
	 * Creates and initializes a filter instance based on user's choice for minimum and maximum degree.
	 * 
	 * @return Instance of <code>IntHistogramFilter</code> reflecting the user's filtering criteria.
	 */
	@Override
	protected ComplexParamFilter createFilter() {
		return new IntHistogramFilter(Utils.getSpinnerInt(spnMin), Utils.getSpinnerInt(spnMax));
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 1556691517305385646L;

	/**
	 * Creates and lays out the two spinner controls for choosing minimum and maximum degree.
	 * 
	 * @param aHistogram Histogram instance, based on which the ranges for the minimum and maximum degrees are
	 *            to be chosen.
	 * @param aSettings Visual settings for <code>aHistogram</code>.
	 */
	private void populate(IntHistogram aHistogram, IntHistogramGroup aSettings) {
		centralPane.setLayout(new SpringLayout());
		int[] range = aHistogram.getObservedRange();

		// Add a spinner for minimum observation
		centralPane.add(new JLabel(aSettings.filter.getMinObservationLabel() + ":", SwingConstants.RIGHT));
		SpinnerModel minSettings = new SpinnerNumberModel(range[0], range[0], range[1], 1);
		centralPane.add(spnMin = new JSpinner(minSettings));

		// Add a spinner for maximum observation
		centralPane.add(new JLabel(aSettings.filter.getMaxObservationLabel() + ":", SwingConstants.RIGHT));
		SpinnerModel maxSettings = new SpinnerNumberModel(range[1], range[0], range[1], 1);
		centralPane.add(spnMax = new JSpinner(maxSettings));

		final int gap = Utils.BORDER_SIZE / 2;
		SpringUtilities.makeCompactGrid(centralPane, 2, 2, 0, 0, gap, gap);
	}

	/**
	 * Spinner to choose the maximum observation value to display.
	 */
	private JSpinner spnMin;

	/**
	 * Spinner to choose the maximum observation value to display.
	 */
	private JSpinner spnMax;
}
