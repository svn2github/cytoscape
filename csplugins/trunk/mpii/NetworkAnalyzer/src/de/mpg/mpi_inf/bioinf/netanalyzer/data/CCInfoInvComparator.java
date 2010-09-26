package de.mpg.mpi_inf.bioinf.netanalyzer.data;

import java.util.Comparator;

/**
 * Comparator of connected components ({@link de.mpg.mpi_inf.bioinf.netanalyzer.data.CCInfo} instances)
 * based on their sizes.
 * <p>
 * Note that this is an inverted comparator - a component of size <code>A</code> is reported as
 * larger than component of size <code>B</code> if and only if <code>A &lt; B</code>.
 * </p>
 * 
 * @author Yassen Assenov
 */
public class CCInfoInvComparator implements Comparator<CCInfo> {

	/**
	 * Initializes a new instance of <code>CCInfoComparator</code>.
	 */
	public CCInfoInvComparator() {
		// No specific initialization required
	}

	/**
	 * Compares the two given connected components.
	 * 
	 * @param o1 First connected component.
	 * @param o2 Second connected component.
	 * @return An integer which reflects the difference in the sizes of the two connected components -
	 *         positive integer if the first component is smaller than the second, negative if the
	 *         first component is larger, and <code>0</code> if both components are of the same
	 *         size.
	 */
	public int compare(CCInfo o1, CCInfo o2) {
		return o2.getSize() - o1.getSize();
	}
}
