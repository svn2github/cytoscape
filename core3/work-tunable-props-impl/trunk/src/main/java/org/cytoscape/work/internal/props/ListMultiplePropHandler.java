package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.util.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListMultipleSelection;


public class ListMultiplePropHandler<T> extends AbstractPropHandler {

	public ListMultiplePropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		try{
			p.setProperty(propKey,((ListMultipleSelection<T>)f.get(o)).getSelectedValues().toString());
		}catch (Exception e){e.printStackTrace();}		
		return p;
	}
	

	public void setProps(Properties p) {
		try{
			if(p.containsKey(propKey)){
				ListMultipleSelection<T> lms = (ListMultipleSelection<T>) f.get(o);
				T[] tab = (T[])p.getProperty(propKey).split(",");
				if(tab != null){
					lms.setSelectedValues(Arrays.asList(tab));
					f.set(o, lms);
				}
			}
		}catch(Exception e){e.printStackTrace();}
	}
}
	
