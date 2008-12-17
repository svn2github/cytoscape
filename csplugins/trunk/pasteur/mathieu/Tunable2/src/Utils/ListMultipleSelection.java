package Utils;

import java.util.ArrayList;
import java.util.List;



public class ListMultipleSelection<T> extends ListSelection<T> {
	private List<T> selected;

	public ListMultipleSelection(final List<T> values) {
		super(values);
	}


	public List<T> getSelectedValues() {
		return new ArrayList<T>(selected);
	}


	public void setSelectedValues(final List<T> vals) {
		if (vals == null)
			throw new NullPointerException("value list is null");

		for (T v : vals)
			if (!values.contains(v))
				throw new IllegalArgumentException("value not contained is list of possible values");

		selected = new ArrayList<T>(vals);
	}
}
