package org.cytoscape.work.internal.props;

import java.lang.reflect.Field;
import java.util.*;

import org.cytoscape.work.AbstractPropHandler;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.utils.ListSingleSelection;


public class ListSinglePropHandler<T> extends AbstractPropHandler {
	
	ListSingleSelection<T> LSS;

	
	@SuppressWarnings("unchecked")
	public ListSinglePropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		try{
			LSS = (ListSingleSelection<T>) f.get(o);
		}catch(Exception e){e.printStackTrace();}
		
	}

	
	public Properties getProps() {
		Properties p = new Properties();
		p.put(propKey,LSS.getSelectedValue());
		return p;
	}

	
	@SuppressWarnings("unchecked")
	public void add(Properties p) {
		LSS.setSelectedValue((T) "");
//		if(LSS.getSelectedValue()==null)
		p.put(propKey,LSS.getSelectedValue());
//		else{
//			try{
//				p.put(propKey,LSS.getSelectedValue());
//			}catch(Exception e){e.printStackTrace();}
//		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				String val = p.get(propKey).toString();
				if (val != null){
					LSS.setSelectedValue((T) val);
					f.set(o, LSS);
				}
			}
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}
	}
}
