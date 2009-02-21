package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.util.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListMultipleSelection;


public class ListMultiplePropHandler extends AbstractPropHandler {
	
	ListMultipleSelection LMS;
	List array;
	
	
	@SuppressWarnings("unchecked")
	public ListMultiplePropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			LMS = (ListMultipleSelection) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		p.put(propKey,(Object)((ListMultipleSelection) LMS).getSelectedValues());
		return p;
	}

	
	@SuppressWarnings("unchecked")
	public void add(Properties p) {
		array = new ArrayList();
		array.add(0, "");
		((ListMultipleSelection) LMS).setSelectedValues(array);
		p.put(propKey,((ListMultipleSelection) LMS).getSelectedValues());
	}
	
	
	@SuppressWarnings("unchecked")
	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				Object val = p.get(propKey);
				if (val != null){
					((ListMultipleSelection) LMS).setSelectedValues((List) val);
					f.set(o, LMS);
				}
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();}
	}
}
	
