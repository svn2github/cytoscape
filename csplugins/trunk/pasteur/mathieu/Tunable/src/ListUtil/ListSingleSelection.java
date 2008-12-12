package ListUtil;

import java.util.List;


/**
 * Allows a single value from a list to be selected.
 *
 * @param <T>  DOCUMENT ME!
 */
public class ListSingleSelection<T> extends ListSelection<T> {
	private T selected;

	/**
	 * Creates a new ListSingleSelection object.
	 *
	 * @param values  DOCUMENT ME!
	 */
	public ListSingleSelection(final List<T> values) {
		super(values);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public T getSelectedValue() {
		return selected;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param val DOCUMENT ME!
	 */
	public void setSelectedValue(T val) {
		if (!values.contains(val))
			throw new IllegalArgumentException("value not contained is list of possible values");

		selected = val;
	}
}
