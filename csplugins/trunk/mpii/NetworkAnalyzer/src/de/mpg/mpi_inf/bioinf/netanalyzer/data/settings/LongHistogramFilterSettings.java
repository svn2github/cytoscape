package de.mpg.mpi_inf.bioinf.netanalyzer.data.settings;

import org.jdom.Element;
import org.w3c.dom.DOMException;

/**
 * Settings for filters on {@link de.mpg.mpi_inf.bioinf.netanalyzer.data.LongHistogram} instances.
 * 
 * @see de.mpg.mpi_inf.bioinf.netanalyzer.data.filter.LongHistogramFilter
 * @author Yassen Assenov
 * @author Sven-Eric Schelhorn
 */
public class LongHistogramFilterSettings extends Settings {

	/**
	 * Initializes a new instance of <code>LongHistogramFilterSettings</code> based on the given
	 * XML node.
	 * 
	 * @param aElement Node in the XML settings file that identifies long histogram filter
	 *        settings.
	 * @throws DOMException When the given element is not an element node with the expected name ({@link #tag})
	 *         or when the subtree rooted at <code>aElement</code> does not have the expected
	 *         structure.
	 */
	public LongHistogramFilterSettings(Element aElement) {
		super(aElement);
	}

	/**
	 * Gets the label to be displayed for entering a value for minimal observation.
	 * 
	 * @return Human-readable message explaining the semantics of the minimal observation.
	 */
	public String getMinObservationLabel() {
		return minObservationLabel;
	}

	/**
	 * Gets the label to be displayed for entering a value for maximal observation.
	 * 
	 * @return Human-readable message explaining the semantics of the maximal observation.
	 */
	public String getMaxObservationLabel() {
		return maxObservationLabel;
	}

	/**
	 * Tag name used in XML settings file to identify <code>LongHistogram</code> filter settings.
	 */
	public static final String tag = "filter";

	/**
	 * Name of the tag identifying the minimal observation label.
	 */
	static final String minObservationLabelTag = "minobslabel";

	/**
	 * Name of the tag identifying the maximal observation label.
	 */
	static final String maxObservationLabelTag = "maxobslabel";

	/**
	 * Initializes a new instance of <code>LongHistogramFilterSettings</code>.
	 * <p>
	 * The initialized instance contains no data and therefore this constructor is used only by the
	 * {@link Settings#clone()} method.
	 * </p>
	 */
	LongHistogramFilterSettings() {
		super();
	}

	/**
	 * Label for entering value for the minimal observation.
	 */
	String minObservationLabel;

	/**
	 * Label for entering value for the maximal observation.
	 */
	String maxObservationLabel;
}
