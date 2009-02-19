package org.cytoscape.work.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ListMultipleSelection<T> extends ListSelection<T> {
	private List<T> selected;


	public ListMultipleSelection(final T ... values) {
		super(Arrays.asList(values));
		selected = new ArrayList<T>();
	}
	public ListMultipleSelection(final List<T> values) {
		super(values);
		selected = new ArrayList<T>();
	}
	
	public List<T> getSelectedValues() {
		return new ArrayList<T>(selected);
	}


	public void setSelectedValues(final List<T> vals) {
		String test= vals.get(0).toString();
		if(test.equals("")==false){
			if (vals == null) throw new NullPointerException("value list is null");
			
			for (T v : vals){
				if (!values.contains(v))
					throw new IllegalArgumentException("value not contained in list of possible values");
			}
		}
		selected = new ArrayList<T>(vals);
	}
}
