package ListUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A bounded number object.
 *
 * @param <T>  DOCUMENT ME!
 */
class ListSelection<T> {
	/**
	 * DOCUMENT ME!
	 */
	protected final List<T> values;

	/**
	 * Creates a new ListSelection object.
	 *
	 * @param values  DOCUMENT ME!
	 */
	public ListSelection(final List<T> values) {
		if (values == null)
			throw new NullPointerException("values is null!");

		if (values.size() == 0)
			throw new IllegalArgumentException("list has size 0");

		this.values = values;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<T> getPossibleValues() {
		return new ArrayList<T>(values);
	}
}
