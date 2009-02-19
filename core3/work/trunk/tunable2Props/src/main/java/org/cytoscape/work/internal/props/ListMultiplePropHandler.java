package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.util.*;

import org.cytoscape.work.AbstractPropHandler;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.utils.ListMultipleSelection;
import org.cytoscape.work.utils.ListSelection;


public class ListMultiplePropHandler<T> extends AbstractPropHandler {
	
	ListSelection<T> LMS;
	List<T> array;
	
	
	@SuppressWarnings("unchecked")
	public ListMultiplePropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			LMS = (ListMultipleSelection<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		p.put(propKey,(Object)((ListMultipleSelection<T>) LMS).getSelectedValues());
		return p;
	}

	
	@SuppressWarnings("unchecked")
	public void add(Properties p) {
		array = new ArrayList<T>();
		array.add(0, (T) "");
		((ListMultipleSelection<T>) LMS).setSelectedValues(array);
		p.put(propKey,((ListMultipleSelection<T>) LMS).getSelectedValues());
	}
	
	
	@SuppressWarnings("unchecked")
	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				Object val = p.get(propKey);
				if (val != null){
					((ListMultipleSelection<T>) LMS).setSelectedValues((List<T>) val);
					f.set(o, LMS);
				}
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();}
	}
}
	
