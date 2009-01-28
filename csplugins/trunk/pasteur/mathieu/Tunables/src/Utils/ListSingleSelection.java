package Utils;

import java.util.Arrays;
import java.util.List;


public class ListSingleSelection<T> extends ListSelection<T>{
	private T selected;

	public ListSingleSelection(final T ... values) {
		super(Arrays.asList(values));
	}

	public ListSingleSelection(final List<T> values) {
		super(values);
	}

	
	public T getSelectedValue() {
		return selected;
	}

	public void setSelectedValue(T val) {
		String test=val.toString();
		if(test.equals("")==false){
			if (!values.contains(val))
				throw new IllegalArgumentException("value not contained in list of possible values");
		}	
		selected = val;
	}
	
}
