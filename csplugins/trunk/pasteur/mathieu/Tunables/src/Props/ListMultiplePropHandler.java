package Props;

import Tunable.Tunable;
import java.lang.reflect.Field;
import java.util.*;
import Utils.ListMultipleSelection;;

public class ListMultiplePropHandler<T> implements PropHandler {
	Field f;
	Object o;
	String propKey;
	ListMultipleSelection<T> LMS;
	List<T> array;
	
	
	@SuppressWarnings("unchecked")
	public ListMultiplePropHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		propKey = f.getName();
		try{
			LMS = (ListMultipleSelection<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		p.put(propKey,(Object)LMS.getSelectedValues());
		return p;
	}

	
	@SuppressWarnings("unchecked")
	public void add(Properties p) {
		array = new ArrayList<T>();
		array.add(0, (T) "");
		LMS.setSelectedValues(array);
		p.put(propKey,LMS.getSelectedValues());
	}
	
	
	@SuppressWarnings("unchecked")
	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				Object val = p.get(propKey);
				if (val != null){
					LMS.setSelectedValues((List<T>) val);
					f.set(o, LMS);
				}
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();}
	}
}
	
